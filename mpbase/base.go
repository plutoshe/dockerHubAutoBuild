package mpbase

import (
	"flag"
	"fmt"
	"log"
	"net"
	"strconv"

	pb "../proto"
	"github.com/plutoshe/taskgraph/filesystem"
	"google.golang.org/grpc"
)

var (
	port = flag.Int("port", 10000, "The server port")
	tp   = flag.String("type", "", "The server type, m(mapper)/r(reducer)")
	s    *grpc.Server
)

type mapperEmitKV struct {
	Key   string `json:"key"`
	Value string `json:"value"`
}


type server struct{}

type mapreduceIO struct {
	mapperInput []buf.io

}

type MpConfig interface {
	GetFileSystem() filesystem.Client
	MapperFunc(key, value string)
	ReducerFunc(key string, value []string)
}

func Emit(key, value string) {

}

var mapperWriteCloser []bufio.
// mapper process procedure
func mapperProcess(input *pb.MapperRequest, userConfig MpConfig) {
	config := make(map[string][string])
	for i, v := input.Arr {
		config[i] = v
	}
	reducerNum := strconv.ParseUint(config["ReducerNum"], 10, 64)
	workID := strconv.ParseUint(config["WorkID"], 10, 64)
	fs := userConfig.GetFileSystem()

	for i = uint64(0); i < reducerNum; i++ {
		path := config["OutputFilePath"] + "/" + strconv.FormatUint(i, 10) + "from" + workID
		Clean(path)
		tmpWrite, err := fs.OpenWriteCloser(path)
		if err != nil {
			t.logger.Fatalf("MapReduce : get mapreduce filesystem client writer failed, ", err)
		}
		mapperWriteCloser = append(t.mapperWriteCloser, *bufio.NewWriterSize(tmpWrite, BufferSize))
	}

		
	mapperReaderCloser, err := fs.OpenReadCloser(config["InputFilePath"])
	if err != nil {
		return fmt.Errorf("MapReduce : get mapreduce filesystem client reader failed, ", err)
	}
	var str string
	bufioReader := bufio.NewReaderSize(mapperReaderCloser, t.config.ReaderBufferSize)

	for err != io.EOF {
		str, err = bufioReader.ReadString('\n')
		if err != io.EOF && err != nil {
			return fmt.Errorf("MapReduce : mapper read Error, ", err)
		}
		if err != io.EOF {
			str = str[:len(str)-1]
		}
		buf = append(buf, &pb.KvPair{Key: str, Value: ""})
		if len(buf) >= bufferSize {
			stream.Send(&pb.MapperRequest{buf})
			buf = nil
		}
		mapperReaderCloser.Close()
	}
}

// reducer process procedure

func reducerProcess
		reducerOutputPath := workConfig.OutputFilePath[0]
		t.Clean(reducerOutputPath)

		reducerWriteCloser, err := t.config.FilesystemClient.OpenWriteCloser(reducerOutputPath)
		t.reducerWriteCloser = *bufio.NewWriterSize(reducerWriteCloser, t.config.WriterBufferSize)

		t.shuffleContainer = make(map[string][]string)

		arg := strings.Split(workConfig.SupplyContent[ProcessID], " ")

		mapperWorkSum, err := strconv.ParseUint(arg[0], 10, 64)
		if err != nil {
			t.logger.Fatalf("Failed to get argv mapperWorkSum : %v", err)
		}
		t.logger.Println(workConfig.InputFilePath)
		t.logger.Println(workConfig.OutputFilePath)
		for i := uint64(0); i < mapperWorkSum; i++ {
			shufflePath := workConfig.InputFilePath[0] + "/" + arg[1] + "from" + strconv.FormatUint(i, 10)
			shuffleReadCloser, err := t.config.FilesystemClient.OpenReadCloser(shufflePath)
			t.logger.Println("get shuffle data from ", shufflePath)
			if err != nil {
				if strings.Contains(err.Error(), "The specified blob does not exist") {
					continue
				}
				t.logger.Fatalf("MapReduce : get azure storage client failed, ", err)
			}
			bufioReader := bufio.NewReaderSize(shuffleReadCloser, t.config.ReaderBufferSize)
			var str []byte
			err = nil
			for err != io.EOF {
				str, err = bufioReader.ReadBytes('\n')
				if err != io.EOF && err != nil {
					t.logger.Fatalf("MapReduce : Shuffle read Error, ", err)
				}
				if err != io.EOF {
					str = str[:len(str)-1]
				}
				t.processShuffleKV(str)
			}
		}
		if err != nil {
			t.logger.Fatalf("MapReduce : get reducer writer error, %v", err)
		}

		for k := range t.shuffleContainer {
			// t.collectKvPairs(userClient, k, t.shuffleContainer[k], false)
			buf = append(buf, &pb.KvsPair{Key: k, Value: t.shuffleContainer[k]})
			if len(buf) >= bufferSize {
				stream.Send(&pb.ReducerRequest{buf})
				buf = nil
			}
		}




func (t *workerTask) Emit(key, val string) {
	if t.config.ReducerNum == 0 {
		return
	}
	h := fnv.New32a()
	h.Write([]byte(key))
	var KV mapperEmitKV
	KV.Key = key
	KV.Value = val
	toShuffle := h.Sum32() % uint32(t.config.ReducerNum)
	data, err := json.Marshal(KV)7
	data = append(data, '\n')
	if err != nil {
		t.logger.Fatalf("json marshal error : ", err)
	}
	t.mapperWriteCloser[toShuffle].Write(data)
}


func (t *workerTask) Collect(key string, val string) {
	t.reducerWriteCloser.Write([]byte(key + " " + val + "\n"))
}

func (t *workerTask) Clean(path string) {
	err := t.config.FilesystemClient.Remove(path)
	if err != nil {
		t.logger.Println(err)
	}
}

func (t *workerTask) processShuffleKV(str []byte) {
	var tp mapperEmitKV
	if err := json.Unmarshal([]byte(str), &tp); err == nil {
		t.shuffleContainer[tp.Key] = append(t.shuffleContainer[tp.Key], tp.Value)
	}
}



