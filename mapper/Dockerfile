FROM golang

ADD . /go/src/app
WORKDIR /go/src/app
RUN export GOPATH=$GOPATH:/go/src/app
RUN go get -v -d ./processSentence/
RUN go get -v github.com/golang/protobuf/proto
RUN go get -v golang.org/x/net/context
RUN go get -v google.golang.org/grpc
RUN go build -o process /go/src/app/processSentence/processSentence_server.go
CMD /go/src/app/process -type m
EXPOSE 10000
