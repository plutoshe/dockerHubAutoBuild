package mpbase

import (
	"flag"
	"fmt"
	"log"
	"net"

	pb "../proto"
	"github.com/plutoshe/taskgraph/filesystem"
	"google.golang.org/grpc"
)

var (
	port = flag.Int("port", 10000, "The server port")
	tp   = flag.String("type", "", "The server type, m(mapper)/r(reducer)")
	s    *grpc.Server
)

type server struct{}

type MpConfig interface {
	GetFileSystem() filesystem.Client
	MapperFunc(key, value string)
	ReducerFunc(key string, value []string)
}

func Emit(key, value string) {

}

func Collect(key, value string) {

}

func (*server) GetStreamCollectResult(stream pb.ReducerStream_GetStreamCollectResultServer) error {
}

func (*server) GetStreamEmitResult(stream pb.MapperStream_GetStreamEmitResultServer) error {
}

func ServerStart() {
	flag.Parse()
	if *tp == "" {
		log.Fatalln("Need a server type m/r")
	}

	lis, err := net.Listen("tcp", fmt.Sprintf(":%d", *port))
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}
	fmt.Println("Listening...")

	if *tp == "m" {
		s = grpc.NewServer()
		pb.RegisterMapperStreamServer(s, &server{})
		s.Serve(lis)
	} else {
		s = grpc.NewServer()
		pb.RegisterReducerStreamServer(s, &server{})
		s.Serve(lis)
	}
}
