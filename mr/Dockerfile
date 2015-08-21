FROM ubuntu:14.04

# Let's start with some basic stuff.
RUN apt-get update -qq && apt-get install -qqy \
    apt-transport-https \
    ca-certificates \
    curl \
    lxc \
    iptables 
# software-properties-common
# python-software-properties
    
# Install Docker from Docker Inc. repositories.
RUN curl -sSL https://get.docker.com/ubuntu/ | sh

# Install the magic wrapper.
ADD ./dind/wrapdocker /usr/local/bin/wrapdocker
RUN chmod +x /usr/local/bin/wrapdocker

# Define additional metadata for our image.
VOLUME /var/lib/docker
# CMD ["wrapdocker"]

# RUN apt-get install -qqy  # 12.04
# RUN add-apt-repository ppa:duh/golang
# RUN apt-get update -qq
# RUN apt-get install -qqy golang 
RUN wget https://storage.googleapis.com/golang/go1.5.linux-amd64.tar.gz
RUN tar -C /usr/local -xzf go1.5.linux-amd64.tar.gz
ENV PATH $PATH:/usr/local/go/bin
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
CMD ["/usr/local/bin/wrapdocker"]