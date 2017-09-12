package pfmsg

type LambdaJson struct {
	Id             string
	FuncName       string
	FuncVersion    string
	FuncBody       string
	FuncPath       string
	FuncHandler    string
	FuncDesc       string
	FuncRuntime    string
	FuncMemorySize string
}
type EventJson struct {
	FuncName    string
	FuncVersion string
	FuncHandler string
}
type Func struct {
	FuncName string
	Version  string
}
type EventFunction struct {
	Event    string
	FuncList []Func
}
type ResultJson struct {
	FuncName    string
	FuncVersion string
	Result      string
}
