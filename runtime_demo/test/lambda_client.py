import message_pb2

import sys
import socket
import time
from google.protobuf.internal import encoder

from google.protobuf.internal import decoder


class SocketService():
    def __init__(self, host, port):
        self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.s.connect((host, port))

    def send(self, msg):
        delimiter = encoder._VarintBytes(len(msg))
        msg = delimiter + msg
        msg_l = len(msg)
        total_l = 0
        while total_l < msg_l:
            send = self.s.send(msg[total_l:])
            total_l = total_l + send

rpc = SocketService(host='162.3.200.193', port=5030)
#rpc = SocketService(host='114.115.145.32', port=8082)
req = message_pb2.ClientMessage()
req.deviceId = '12322'
req.type = 1
req.content = 'i am a new device'
req.messageId = 'xxxxxxxxx'
req.callbackId = 'yyyyyy'
sa = req.SerializeToString()
print sa
rpc.send(sa)

def socket_read_n(sock, n):
    buf = ''
    while n> 0:
        data = sock.recv(n)
        buf += data
        n -= len(data)
    return buf


def get_message(sock):
    len_buf = socket_read_n(sock, 4)
    (size, pos) = decoder._DecodeVarint(len_buf, 0)
    msg_buf =  socket_read_n(sock, size -4 + pos)
    msg_buf = len_buf + msg_buf
    msg = message_pb2.ClientMessage()
    msg.ParseFromString(msg_buf[pos:])
    return msg

while True:
    msg = get_message(rpc.s)
    print msg 
    req1 = message_pb2.ClientMessage()
    req1.deviceId = '12322'
    req1.type = 4
    req1.content = msg.content
    req1.callbackId = msg.messageId
    req1.messageId = 'xxxxxyyy'
    sa1 = req1.SerializeToString()
    rpc.send(sa1)
    print sa1
