FROM jpetazzo/dind

RUN sudo apt-get install golang -y
ENV GOPATH /go/lib:/go/src/app
ADD . /go/src/app
WORKDIR /go/src/app
RUN go get -v -d
RUN go get -v github.com/golang/protobuf/proto
RUN go get -v golang.org/x/net/context
RUN go get -v google.golang.org/grpc
RUN go get -v github.com/plutoshe/mr
RUN go get -v github.com/plutoshe/taskgraph
RUN go get -v github.com/coreos/go-etcd/etcd
RUN go get -v github.com/Azure/azure-sdk-for-go/storage
RUN go get -v github.com/colinmarc/hdfs
RUN go build -o mapreduce /go/src/app/example.go
