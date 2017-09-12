package main

import (
	"encoding/json"
	"fmt"
	"io"
	"io/ioutil"
	"log"
	"net/http"
	"time"

	"github.com/gorilla/mux"
)

const (
	version = "v1.0"
)

// Route-related
type Route struct {
	Name        string
	Method      string
	Pattern     string
	HandlerFunc http.HandlerFunc
}

type Routes []Route

func NewRouter() *mux.Router {
	router := mux.NewRouter().StrictSlash(true)
	for _, route := range routes {
		var handler http.Handler
		handler = route.HandlerFunc
		handler = Logger(handler, route.Name)
		router.
			Methods(route.Method).
			Path(route.Pattern).
			Name(route.Name).
			Handler(handler)
	}
	return router
}

var routes = Routes{
	Route{
		"Versions",
		"GET",
		"/",
		GetVersions,
	},
	Route{
		"Versions",
		"GET",
		"/versions",
		GetVersions,
	},
	Route{
		"PostLambda",
		"POST",
		"/" + version + "/lambda/{lambdaId}",
		PostLambda,
	},
	Route{
		"GetLambda",
		"Get",
		"/" + version + "/lambda/{lambdaId}",
		GetLambda,
	},
	Route{
		"PostLambdaResult",
		"POST",
		"/" + version + "/lambda/{lambdaId}/result",
		PostLambdaResult,
	},
	Route{
		"GetLambdaResult",
		"GET",
		"/" + version + "/lambda/{lambdaId}/result",
		GetLambdaResult,
	},
}

// Route Handlers definition
func GetVersions(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, `{"versions": ["%s"]}`, version)
	// var versions map[string]int
	// versions :=
	// json.NewEncoder(w).Encode(versions)
}

func GetLambdaResult(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	lambdaId := vars["lambdaId"]
	w.Header().Set("content-type", "application/json")
	w.WriteHeader(http.StatusNotFound)
	if err := json.NewEncoder(w).Encode(jsonErr{Code: http.StatusNotFound, Text: lambdaId + " Not Found"}); err != nil {
	}
}

func PostLambda(w http.ResponseWriter, r *http.Request) {
	//lambdaId := mux.Vars(r)["lambdaId"]

}
func GetLambda(w http.ResponseWriter, r *http.Request) {
	lambdaId := mux.Vars(r)["lambdaId"]
	work, ok := FetchWork(lambdaId)
	log.Print("Get Lambda")
	if ok {
		w.WriteHeader(http.StatusOK)
		w.Header().Set("content-type", "application/json")
		w.Header().Set("X-Invocation-Id", work.InvocationId)
		w.Write([]byte(work.Event))
		// if err := json.NewEncoder(w).Encode(work.Payload); err != nil {
		// }
	} else {
		w.WriteHeader(http.StatusNotFound)
	}

}

func PostLambdaResult(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	lambdaId := vars["lambdaId"]
	var err error
	body, err := ioutil.ReadAll(io.LimitReader(r.Body, 1048576))
	log.Printf("=== write lambda<%s>:%s", lambdaId, body)
	go WriteWorkerResult(lambdaId, body)
	if err != nil {
		// todo
	}
	if err := r.Body.Close(); err != nil {
		// todo
	}
	w.WriteHeader(http.StatusCreated)

}

// http-handler Logger wrapper
func Logger(inner http.Handler, name string) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		start := time.Now()
		inner.ServeHTTP(w, r)
		log.Printf(
			"%s\t%s\t%s\t%s",
			r.Method,
			r.RequestURI,
			name,
			time.Since(start),
		)
	})
}

// error
type jsonErr struct {
	Code int    `json:"code"`
	Text string `json:"text"`
}

func startLocalServer() {
	log.Fatal(http.ListenAndServe("127.0.0.1:6666", NewRouter()))
}
