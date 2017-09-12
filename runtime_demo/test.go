package main

import (
	"github.com/urfave/cli"
	"time"
)

var testCommand = cli.Command{
	Name:  "test",
	Usage: `test the service..`,
	Action: func(context *cli.Context) error {
		// start the localhost service
		go func() {
			time.Sleep(1000 * time.Millisecond)
			RunContainerTest()
			// connectCloudLoop(context)
		}()
		startLocalServer()
		return nil
	},
}
