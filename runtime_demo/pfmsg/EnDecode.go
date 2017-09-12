package pfmsg

import (
	proto "github.com/golang/protobuf/proto"
	"bytes"
)
type EnDecode struct {
	name string
}
func (en EnDecode) Encode(data []byte) []byte{
	lenByte := proto.EncodeVarint(uint64((len(data))))
	byteArray := make([][]byte ,2)
	byteArray[0] = lenByte
	byteArray[1] = data
	data = bytes.Join(byteArray,[]byte(""))
	return  data
}
func (en EnDecode)EncodeClientMsg(message *ClientMessage) ([]byte, error){
	return proto.Marshal(message)
}

func (en EnDecode)DecodeClientMsg(data []byte,message *ClientMessage) error{
	return proto.Unmarshal(data,message)
}

func (en EnDecode)EncodeServerMsg(message *ServiceMessage) ([]byte, error){
	return proto.Marshal(message)
}

func (en EnDecode)DecodeServerMsg(data []byte,message *ServiceMessage) error{
	return proto.Unmarshal(data,message)
}
