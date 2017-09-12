package main

import (
	"github.com/urfave/cli"
	_ "time"
)

var daemonCommand = cli.Command{
	Name:  "daemon",
	Usage: `run the service..`,
	Flags: []cli.Flag{
		cli.StringFlag{
			Name:  "host, H",
			Usage: "the server ip",
		},
		cli.StringFlag{
			Name:  "port,P",
			Value: "8082",
			Usage: "the server port, default 8082",
		},
		cli.StringFlag{
			Name:  "deviceid,d",
			Usage: "the specific device id",
		},

		cli.StringFlag{
			Name:  "localHost, LH",
			Usage: "the server ip",
		},
		cli.StringFlag{
			Name:  "localPort,LP",
			Value: "8082",
			Usage: "the server port, default 8082",
		},
		cli.StringFlag{
			Name: "localdeviceid,ld",

			Usage: "the specific device id",
		},
	},
	Action: func(context *cli.Context) error {
		// start the localhost service
		go func() {
			// time.Sleep(1000 * time.Millisecond)
			// RunContainerTest()
			connectCloudLoop(context)
		}()
		startLocalServer()
		return nil
	},
}
