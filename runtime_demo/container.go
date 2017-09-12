package main

import (
	"fmt"
	"log"
	"os"
	"path/filepath"

	"github.com/opencontainers/runc/libcontainer"
	"github.com/opencontainers/runc/libcontainer/cgroups/systemd"
	"github.com/opencontainers/runc/libcontainer/configs"
	"github.com/opencontainers/runc/libcontainer/specconv"
	"github.com/opencontainers/runtime-spec/specs-go"
	// "github.com/opencontainers/runc/libcontainer/utils"
)

type LambdaSpec struct {
	Name        string
	Version     string
	Handler     string // filename.handler-method
	Location    string // local lambda store directory
	Description string
	Runtime     string // e.g. python2.7
	MemorySize  string // KB
	Timeout     string // seconds
}

func GetSpecTemplate(ls *LambdaSpec, id string) *specconv.CreateOpts {
	location := ls.Location
	rcwd, _ := os.Getwd()
	cwd, _ := filepath.Abs(rcwd)
	if !filepath.IsAbs(location) {
		location = filepath.Join(cwd, location)
	}
	runtimedir := filepath.Join(cwd, "runtime")
	return &specconv.CreateOpts{
		CgroupName: id,
		// UseSystemdCgroup: context.GlobalBool("systemd-cgroup"),
		UseSystemdCgroup: true,
		NoPivotRoot:      false,
		NoNewKeyring:     false,
		Rootless:         false,
		Spec: &specs.Spec{
			Root: &specs.Root{
				Path:     "rootfs",
				Readonly: true,
			},
			Process: &specs.Process{
				Terminal: false,
				User:     specs.User{},
				Args: []string{
					"python", "/runtime/lambda_runtime.py", "--handler=" + ls.Handler,
				},
				Env: []string{
					"PATH=/usr/local/bin:/usr/bin:/bin",
					"MY_LAMBDA_NAME=" + id,
					"PYTHONPATH=/lambda",
				},
				Cwd:             "/",
				NoNewPrivileges: true,
				Capabilities: &specs.LinuxCapabilities{
					Bounding: []string{
						"CAP_AUDIT_WRITE",
						"CAP_KILL",
						"CAP_NET_BIND_SERVICE",
					},
					Permitted: []string{
						"CAP_AUDIT_WRITE",
						"CAP_KILL",
						"CAP_NET_BIND_SERVICE",
					},
					Inheritable: []string{
						"CAP_AUDIT_WRITE",
						"CAP_KILL",
						"CAP_NET_BIND_SERVICE",
					},
					Ambient: []string{
						"CAP_AUDIT_WRITE",
						"CAP_KILL",
						"CAP_NET_BIND_SERVICE",
					},
					Effective: []string{
						"CAP_AUDIT_WRITE",
						"CAP_KILL",
						"CAP_NET_BIND_SERVICE",
					},
				},
				Rlimits: []specs.POSIXRlimit{
					{
						Type: "RLIMIT_NOFILE",
						Hard: uint64(1024),
						Soft: uint64(1024),
					},
				},
			},
			Hostname: "lambda-" + ls.Name,
			Mounts: []specs.Mount{
				{
					Destination: "/proc",
					Type:        "proc",
					Source:      "proc",
					Options:     nil,
				},
				{
					Destination: "/dev",
					Type:        "tmpfs",
					Source:      "tmpfs",
					Options:     []string{"nosuid", "strictatime", "mode=755", "size=65536k"},
				},
				{
					Destination: "/dev/pts",
					Type:        "devpts",
					Source:      "devpts",
					Options:     []string{"nosuid", "noexec", "newinstance", "ptmxmode=0666", "mode=0620", "gid=5"},
				},
				{
					Destination: "/dev/shm",
					Type:        "tmpfs",
					Source:      "shm",
					Options:     []string{"nosuid", "noexec", "nodev", "mode=1777", "size=65536k"},
				},
				{
					Destination: "/dev/mqueue",
					Type:        "mqueue",
					Source:      "mqueue",
					Options:     []string{"nosuid", "noexec", "nodev"},
				},
				{
					Destination: "/sys",
					Type:        "sysfs",
					Source:      "sysfs",
					Options:     []string{"nosuid", "noexec", "nodev", "ro"},
				},
				{
					Destination: "/sys/fs/cgroup",
					Type:        "cgroup",
					Source:      "cgroup",
					Options:     []string{"nosuid", "noexec", "nodev", "relatime", "ro"},
				},
				{
					Destination: "/runtime",
					Source:      runtimedir,
					Options:     []string{"bind", "relatime", "ro", "errors=remount-ro", "data=ordered"},
				},
				{
					Destination: "/lambda",
					Source:      location,
					Options:     []string{"bind", "relatime", "ro", "errors=remount-ro", "data=ordered"},
				},
				{
					Destination: "/tmp",
					Type:        "tmpfs",
					Source:      "tmpfs",
					Options:     []string{"nosuid", "strictatime", "mode=755"},
				},
			},
			Linux: &specs.Linux{
				MaskedPaths: []string{
					"/proc/kcore",
					"/proc/latency_stats",
					"/proc/timer_list",
					"/proc/timer_stats",
					"/proc/sched_debug",
					"/sys/firmware",
				},
				ReadonlyPaths: []string{
					"/proc/asound",
					"/proc/bus",
					"/proc/fs",
					"/proc/irq",
					"/proc/sys",
					"/proc/sysrq-trigger",
				},
				Resources: &specs.LinuxResources{
					Devices: []specs.LinuxDeviceCgroup{
						{
							Allow:  false,
							Access: "rwm",
						},
					},
				},
				Namespaces: []specs.LinuxNamespace{
					{
						Type: "pid",
					},
					{
						Type: "network",
						Path: "/proc/1/ns/net", // share init net ns
					},
					{
						Type: "ipc",
					},
					{
						Type: "uts",
					},
					{
						Type: "mount",
					},
				},
			},
		},
	}
}

func RunLambda(lambda *LambdaSpec) error {

	// container :=
	cgroupManager := libcontainer.Cgroupfs
	if systemd.UseSystemd() {
		cgroupManager = libcontainer.SystemdCgroups
	} else {
		// todo
	}
	// todo if invalid character in Name
	id := GetID(lambda.Name, lambda.Version)
	createOpts := GetSpecTemplate(lambda, id)
	config, err := specconv.CreateLibcontainerConfig(createOpts)
	if err != nil {
		log.Panic("create container config failed: ", err)
		return err
	}
	factory, err := libcontainer.New("/run/runc", cgroupManager)
	if err != nil {
		// todo
		log.Panic("new container failed: ", err)
		return err
	}
	container, err := factory.Load(id)
	if err != nil {
		// todo
		log.Print("container not exists, so create one ")
		container, err = factory.Create(id, config)
		if err != nil {
			log.Print("create container failed: ", err)
			return err
		}
	}
	p := createOpts.Spec.Process

	lp := &libcontainer.Process{
		Args:            p.Args,
		Env:             p.Env,
		User:            fmt.Sprintf("%d:%d", p.User.UID, p.User.GID),
		Cwd:             p.Cwd,
		Label:           p.SelinuxLabel,
		NoNewPrivileges: &p.NoNewPrivileges,
	}
	if p.Capabilities != nil {
		lp.Capabilities = &configs.Capabilities{}
		lp.Capabilities.Bounding = p.Capabilities.Bounding
		lp.Capabilities.Effective = p.Capabilities.Effective
		lp.Capabilities.Inheritable = p.Capabilities.Inheritable
		lp.Capabilities.Permitted = p.Capabilities.Permitted
		lp.Capabilities.Ambient = p.Capabilities.Ambient

	}
	log.Print("container run process")
	err = container.Run(lp)
	if err != nil {
		log.Printf("destroy.. %s : %s", container, err)
		_destroy(container)
		panic(err)
	}
	return nil
}

func _destroy(container libcontainer.Container) {
	if err := container.Destroy(); err != nil {
		log.Printf("destroy %s failed", container)
	}
}

type WorkItem struct {
	InvocationId string
	Event        string
	Result       chan string
}

var lambda_map map[string]*LambdaSpec = make(map[string]*LambdaSpec)
var work_map map[string]WorkItem = make(map[string]WorkItem)

func GetID(name string, version string) string {
	return name + "-" + version
}

func CreateLambda(ls *LambdaSpec) {
	lambda_map[GetID(ls.Name, ls.Version)] = ls
}

func CreateEvent(invoke_id string, name string, version string, event string) string {
	id := GetID(name, version)
	ch := make(chan string)
	work_map[id] = WorkItem{
		InvocationId: invoke_id,
		Event:        event,
		Result:       ch,
	}
	ls, ok := lambda_map[id]
	if ok {
		log.Printf("run lambda ")
		RunLambda(ls)
		result := <-ch
		return result
	} else {
		log.Printf("no lambda function found")
		return "no lambda function found"

	}
}

func FetchWork(id string) (WorkItem, bool) {
	// todo, implement block FIFO queue
	w, ok := work_map[id]
	return w, ok

}

func WriteWorkerResult(id string, body []byte) {
	w, ok := work_map[id]
	w.Result <- string(body)
	if ok {
		delete(work_map, id)
	}
}

func RunContainerTest() {
	var ls *LambdaSpec = &LambdaSpec{
		Name:        "testLambda",
		Handler:     "testLambda.hello_lambda",
		Description: "test lambda",
		Runtime:     "python2.7",
		MemorySize:  "64000",
		Timeout:     "3",
		Location:    "lambda/testLambda",
	}
	CreateLambda(ls)
	r := CreateEvent("123", ls.Name, ls.Version, `{"event" : "23", "key1": "1"}`)
	log.Printf(" get result %s", r)
}
