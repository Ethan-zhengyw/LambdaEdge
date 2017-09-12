package util

import (
	"strconv"
	"math/rand"
)

type Utl struct {

}
func (Utl) RandomMsgId()string{
	result := ""
	for i:=0;i<=7;i++{
		result+=strconv.Itoa(rand.Intn(10))
	}
	return result
}
