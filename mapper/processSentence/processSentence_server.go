package main

import (
	"flag"
	"fmt"
	"io"
	"log"
	"net"
	"unicode/utf8"

	pb "../proto"
	"../../mpbase"
	"google.golang.org/grpc"
	"github.com/plutoshe/taskgraph/filesystem"

)

type mpserver struct{}


func (t *mpserver) GetFileSystem() filesystem.Client {
	return filesystem.NewLocalFSClient()
}

func (t *mpserver) MapperFunc(key, value string) {
	for len(key) > 0 {
		cc, size := utf8.DecodeRuneInString(key)
		if cc == '，' || cc == '。' || cc == '？' || cc == '！' || cc == '；' {
			if len(chop) >= 3 && len(chop) <= 30 {
				mpbase.Emit(Key: chop, Value: "1"})
				// fmt.Println(chop[i])

			}
			chop = ""
		} else {
			chop += fmt.Sprintf("%c", cc)
		}
		key = key[size:]
	}	
}

func (t *mpserver) ReducerFunc(key string, value []string) (rKey, rValue string) {
	mpbase.Collect(key, "1")
}

func (*server) GetStreamCollectResult(stream pb.ReducerStream_GetStreamCollectResultServer) error {
	for {
		in, err := stream.Recv()
		if err == io.EOF {
			return nil
		}
		if err != nil {
			return err
		}
		mapperProcess(in, my)
	}
	return nil
}

func (*server) GetStreamEmitResult(stream pb.MapperStream_GetStreamEmitResultServer) error {
	for {
		in, err := stream.Recv()
		if err == io.EOF {
			return nil
		}
		if er != nil {
			return err
		}
		reducerProcess(in, )
	}
	return nil
}

func ServerStart(tp string, port string) {
	// flag.Parse()
	// if *tp == "" {
	// 	log.Fatalln("Need a server type m/r")
	// }

	lis, err := net.Listen("tcp", fmt.Sprintf(":%d", port))
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}
	fmt.Println("Listening...")

	if tp == "m" {
		s = grpc.NewServer()
		pb.RegisterMapperStreamServer(s, &server{})
		s.Serve(lis)
	} else {
		s = grpc.NewServer()
		pb.RegisterReducerStreamServer(s, &server{})
		s.Serve(lis)
	}
}


