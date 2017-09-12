package main

import (
	"./pfmsg"
	"./util"
	"fmt"
	proto "github.com/golang/protobuf/proto"
	"log"
	"net"
	"time"

	"encoding/json"
	"os"

	"io"

	"sync"

	"encoding/base64"
	"github.com/urfave/cli"
	"runtime"
	"strings"
)

//client to connect server
//running in edge device
//
type Client struct {
	ServerIp     string
	ServerPort   string
	DeviceId     string
	Connect      net.Conn
	ConnectMutex sync.Mutex
	Logger       *log.Logger
}

func (client *Client) Conn() error {
	conn, err := net.Dial("tcp", client.ServerIp+":"+client.ServerPort)
	client.Logger.Println("client connect to server...")
	if err != nil {
		if err == io.EOF {
			client.RetryConn()
		}
		client.Logger.Println(err)
		panic("conn error")
	}

	client.Connect = conn
	return nil
}
func (client *Client) SendMsg(data []byte) (int, error) {
	totalLen := len(data)
	sentLen := 0
	n := 0
	var err error = nil
	for sentLen < totalLen {
		n, err = client.Connect.Write(data[sentLen:])
		if err != nil {
			if err == io.EOF {
				client.RetryConn()
				continue
			}
			panic("Error send")
		}
		sentLen += n
	}

	return sentLen, err
}
func (client *Client) ReceiveMsg() ([]byte, error) {
	buf := make([]byte, 1024)
	len, err := client.Connect.Read(buf)
	if err != nil {
		fmt.Println("Error receiveMsg ", err)
		return buf, err
	}
	return buf[:len-1], nil
}
func (client *Client) receiveMsg() ([]byte, int, error) {
	buf := make([]byte, 1024)
	len, err := client.Connect.Read(buf)
	if err != nil {

		fmt.Println("Error receiveMsg ", err)
		return buf, 0, err
	}
	return buf[:len-1], len, nil
}
func (client *Client) receive_n(n int) []byte {
	result := make([]byte, n)
	getLen := n
	for n > 0 {
		//client.Connect.SetReadDeadline(time.Now().Add(time.Second*15))
		var buf = make([]byte, n)
		l, err := client.Connect.Read(buf)
		if err != nil {

			if err == io.EOF {
				client.RetryConn()
				continue
			}
			continue
		}
		copy(result[getLen-n:], buf[0:l])
		n -= l

	}
	return result
}
func (client *Client) ReceiveMsg_loop() []byte {
	headData := client.receive_n(4)
	length, pos := proto.DecodeVarint(headData)
	message := client.receive_n(int(length) - 4 + pos)
	result := make([]byte, length)
	copy(result[0:], headData[pos:])
	copy(result[4-pos:], message[:])
	return result
}
func (client *Client) sendResultMsg(callBackId string, content string) error {
	client.Logger.Println("get result from " + callBackId + ":" + content)
	msg := pfmsg.MsgFactory{}.GetesultMsg(client.DeviceId, util.Utl{}.RandomMsgId(), content, callBackId)
	data, err := pfmsg.EnDecode{}.EncodeClientMsg(msg)
	if err != nil {
		client.Logger.Println("error in encode msg ")

	}
	data = pfmsg.EnDecode{}.Encode(data)
	client.ConnectMutex.Lock()
	_, errSend := client.SendMsg(data)
	client.ConnectMutex.Unlock()
	if errSend != nil {
		client.Logger.Println("error send result msg")
		return err
	}
	client.Logger.Println("send result successful", callBackId)
	return nil
}
func (client *Client) sendPingMsg() {
	for {
		time.Sleep(time.Second * 180)

		go func(client *Client) {

			client.Logger.Println("sending HeartBeating ")
			pingMsg := pfmsg.MsgFactory{}.GetPingMsg(client.DeviceId, util.Utl{}.RandomMsgId(), "", "")
			data, errEncode := pfmsg.EnDecode{}.EncodeClientMsg(pingMsg)
			if errEncode != nil {
				client.Logger.Println("error in encode ping msg")
			}

			data = pfmsg.EnDecode{}.Encode(data)

			client.ConnectMutex.Lock()
			_, errWrite := client.SendMsg(data)
			client.ConnectMutex.Unlock()

			if errWrite != nil {
				client.Logger.Println("error send ping msg", errWrite)
				if errWrite == io.EOF {
					client.RetryConn()
				}

			}
		}(client)
	}
}

func (client *Client) MonitorAndHeart() {
	go func(client *Client) {
		client.sendPingMsg()

	}(client)

	lambda_map = make(map[string]*LambdaSpec)
	for {

		data := client.ReceiveMsg_loop()

		go func(client *Client, data []byte) {
			serverMsg := &pfmsg.ServiceMessage{}
			err := pfmsg.EnDecode{}.DecodeServerMsg(data, serverMsg)
			if err != nil {
				client.Logger.Println("received msg can`t decode")
			}
			client.Logger.Println("receive msg", *serverMsg.Content)
			switch {
			case *serverMsg.Type == int32(0):
				client.Logger.Println("receive lambda from server")
				var jsonMap pfmsg.LambdaJson
				errJson := json.Unmarshal([]byte(*serverMsg.Content), &jsonMap)
				if errJson != nil {
					client.Logger.Println("received msg can`t json parse")
				}
				name := jsonMap.FuncName
				version := jsonMap.FuncVersion
				code := jsonMap.FuncBody
				codeLanguage := jsonMap.FuncRuntime
				suffix := ""
				switch {
				case strings.Contains(strings.ToLower(codeLanguage), "python"):
					suffix = ".py"
				case strings.Contains(strings.ToLower(codeLanguage), "javascript"):
					suffix = ".js"

				}

				client.Logger.Println(name, version, code)
				codeBase, errEncode := base64.StdEncoding.DecodeString(code)
				if errEncode != nil {

				}
				code = string(codeBase)
				dir, err := os.Getwd()
				if err != nil {
					client.Logger.Println("Error getWd")
				}
				lambda_id := name + "-" + version
				dir = dir + string(os.PathSeparator) + "lambda" +
					string(os.PathSeparator) + lambda_id
				os.MkdirAll(dir, os.ModePerm)
				file, err := os.Create(dir + string(os.PathSeparator) + name + suffix)
				if err != nil {
					client.Logger.Println("Error create lambda file:", err)
				}
				file.WriteString(code)
				file.Close()

				lambda_spec := &LambdaSpec{
					Name:        name,
					Version:     version,
					Handler:     jsonMap.FuncHandler,
					Description: "test lambda",
					Runtime:     "python2.7",
					MemorySize:  "64000",
					Timeout:     "3",
					Location:    dir,
				}
				CreateLambda(lambda_spec)
			case *serverMsg.Type == int32(2):
				//var jsonMap pfmsg.LambdaJson
				//json.Unmarshal([]byte(*serverMsg.Content), &jsonMap)
				//name := jsonMap.FuncName
				//version := jsonMap.FuncVersion
				//lambda_id := name + "-" + version

				var jsonMap pfmsg.EventFunction
				json.Unmarshal([]byte(*serverMsg.Content), &jsonMap)
				event := jsonMap.Event
				funcList := jsonMap.FuncList

				//dir, err := os.Getwd()
				if err != nil {
					client.Logger.Println("Error getWd")
				}
				//dir = dir + string(os.PathSeparator) + "lambda" +
				//	string(os.PathSeparator) + lambda_id
				client.Logger.Println("get event")

				for _, currFunc := range funcList {
					name := currFunc.FuncName
					version := currFunc.Version
					client.Logger.Println("createEvent ", name, version, event)
					r := CreateEvent(*serverMsg.MessageId, name, version, event)

					jsonData, errEncode := json.Marshal(&pfmsg.ResultJson{name, version, r})
					if errEncode != nil {
						client.Logger.Println("error json result ", errEncode)
					}
					client.Logger.Println("get result from event", string(jsonData))
					client.sendResultMsg(*serverMsg.MessageId, string(jsonData))
					//client.sendResultMsg(*serverMsg.MessageId, r)
				}
			case *serverMsg.Type == int32(1):
				client.Logger.Println("get result")
				client.sendResultMsg("hello world sent from edge device", "12312")

			}
		}(client, data)
		client.Logger.Println("dealing msg....")

	}
}

func (client *Client) RetryConn() {
	retryTimes := 5
	for i := 1; i < retryTimes; i++ {
		err := client.Conn()
		if err != nil {
			client.Logger.Println("client retry connect ", i)

		} else {
			return
		}
		time.Sleep(time.Second * 30)
	}
	os.Exit(1)

}

var cloud_client *Client

func connectCloudLoop(context *cli.Context) {
	var logger = log.New(os.Stdout, "CloudClient ", log.Lshortfile|log.Ldate|log.Ltime)
	runtime.GOMAXPROCS(2)
	cloud_client = &Client{
		context.String("host"),
		context.String("port"),
		context.String("deviceid"),
		nil,
		sync.Mutex{},
		logger}

	cloud_client.Conn()
	msg := pfmsg.MsgFactory{}.GetConnMsg(cloud_client.DeviceId, util.Utl{}.RandomMsgId(), "HuaweiEdgeCloud device", "")

	data, err := pfmsg.EnDecode{}.EncodeClientMsg(msg)
	if err != nil {
		cloud_client.Logger.Println("error encode connect msg", msg)
		panic(err)
	}
	data = pfmsg.EnDecode{}.Encode(data)
	cloud_client.Connect.Write(data)

	go cloud_client.MonitorAndHeart()

	var local_logger = log.New(os.Stdout, "LocalClient ", log.Lshortfile|log.Ldate|log.Ltime)

	local_client := &Client{
		context.String("localHost"),
		context.String("localPort"),
		context.String("deviceId"),
		nil,
		sync.Mutex{},
		local_logger}
	local_client.Conn()

	local_msg := pfmsg.MsgFactory{}.GetConnMsg(local_client.DeviceId, util.Utl{}.RandomMsgId(), "HuaweiEdgeCloud device", "")

	local_data, local_err := pfmsg.EnDecode{}.EncodeClientMsg(local_msg)
	if local_err != nil {
		local_client.Logger.Println("error encode connect msg", local_msg)
		panic(local_err)
	}

	local_data = pfmsg.EnDecode{}.Encode(local_data)
	local_client.Connect.Write(local_data)

	go local_client.MonitorAndHeart()

}

func SendResult(lambda string, result string) {
	cloud_client.sendResultMsg(result, lambda)
}
