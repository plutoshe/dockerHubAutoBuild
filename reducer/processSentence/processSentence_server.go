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

type mpserver mpbase.MpConfig 


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


