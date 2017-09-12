package pfmsg

import proto "github.com/golang/protobuf/proto"
type MsgFactory struct {

}
func (MsgFactory )GetPingMsg(deviceId string,messageId string,content string,callbackId string) *ClientMessage{
	msg :=&ClientMessage{
		proto.String(deviceId),
		proto.Int32(3),
		proto.String(content),
		proto.String(messageId),
		proto.String(callbackId),
		[]byte{}}
	return msg
}
func (MsgFactory )GetConnMsg(deviceId string,messageId string,content string,callbackId string) *ClientMessage{
	msg :=&ClientMessage{
		proto.String(deviceId),
		proto.Int32(1),
		proto.String(content),
		proto.String(messageId),
		proto.String(callbackId),
		[]byte{}}
	return msg
}
func (MsgFactory )GetDisConnMsg(deviceId string,messageId string,content string,callbackId string) *ClientMessage{
	msg :=&ClientMessage{
		proto.String(deviceId),
		proto.Int32(2),
		proto.String(content),
		proto.String(messageId),
		proto.String(callbackId),
		[]byte{}}
	return msg
}
func (MsgFactory )GetesultMsg(deviceId string,messageId string,content string,callbackId string) *ClientMessage{
	msg :=&ClientMessage{
		proto.String(deviceId),
		proto.Int32(4),
		proto.String(content),
		proto.String(messageId),
		proto.String(callbackId),
		[]byte{}}
	return msg
}
