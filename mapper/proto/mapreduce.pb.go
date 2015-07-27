// Code generated by protoc-gen-go.
// source: mapreduce.proto
// DO NOT EDIT!

/*
Package mapreduce is a generated protocol buffer package.

It is generated from these files:
	mapreduce.proto

It has these top-level messages:
	KvPair
	KvsPair
	MapperRequest
	MapperResponse
	ReducerRequest
	ReducerResponse
	WorkRequest
	WorkConfigResponse
*/
package mapreduce

import proto "github.com/golang/protobuf/proto"

import (
	context "golang.org/x/net/context"
	grpc "google.golang.org/grpc"
)

// Reference imports to suppress errors if they are not otherwise used.
var _ context.Context
var _ grpc.ClientConn

// Reference imports to suppress errors if they are not otherwise used.
var _ = proto.Marshal

type KvPair struct {
	Key   string `protobuf:"bytes,1,opt,name=key" json:"key,omitempty"`
	Value string `protobuf:"bytes,2,opt,name=value" json:"value,omitempty"`
}

func (m *KvPair) Reset()         { *m = KvPair{} }
func (m *KvPair) String() string { return proto.CompactTextString(m) }
func (*KvPair) ProtoMessage()    {}

type KvsPair struct {
	Key   string   `protobuf:"bytes,1,opt,name=key" json:"key,omitempty"`
	Value []string `protobuf:"bytes,2,rep,name=value" json:"value,omitempty"`
}

func (m *KvsPair) Reset()         { *m = KvsPair{} }
func (m *KvsPair) String() string { return proto.CompactTextString(m) }
func (*KvsPair) ProtoMessage()    {}

type MapperRequest struct {
	Arr []*KvPair `protobuf:"bytes,1,rep,name=arr" json:"arr,omitempty"`
}

func (m *MapperRequest) Reset()         { *m = MapperRequest{} }
func (m *MapperRequest) String() string { return proto.CompactTextString(m) }
func (*MapperRequest) ProtoMessage()    {}

func (m *MapperRequest) GetArr() []*KvPair {
	if m != nil {
		return m.Arr
	}
	return nil
}

type MapperResponse struct {
	Arr []*KvPair `protobuf:"bytes,1,rep,name=arr" json:"arr,omitempty"`
}

func (m *MapperResponse) Reset()         { *m = MapperResponse{} }
func (m *MapperResponse) String() string { return proto.CompactTextString(m) }
func (*MapperResponse) ProtoMessage()    {}

func (m *MapperResponse) GetArr() []*KvPair {
	if m != nil {
		return m.Arr
	}
	return nil
}

type ReducerRequest struct {
	Arr []*KvsPair `protobuf:"bytes,1,rep,name=arr" json:"arr,omitempty"`
}

func (m *ReducerRequest) Reset()         { *m = ReducerRequest{} }
func (m *ReducerRequest) String() string { return proto.CompactTextString(m) }
func (*ReducerRequest) ProtoMessage()    {}

func (m *ReducerRequest) GetArr() []*KvsPair {
	if m != nil {
		return m.Arr
	}
	return nil
}

type ReducerResponse struct {
	Arr []*KvPair `protobuf:"bytes,1,rep,name=arr" json:"arr,omitempty"`
}

func (m *ReducerResponse) Reset()         { *m = ReducerResponse{} }
func (m *ReducerResponse) String() string { return proto.CompactTextString(m) }
func (*ReducerResponse) ProtoMessage()    {}

func (m *ReducerResponse) GetArr() []*KvPair {
	if m != nil {
		return m.Arr
	}
	return nil
}

type WorkRequest struct {
	TaskID uint64 `protobuf:"varint,1,opt,name=taskID" json:"taskID,omitempty"`
}

func (m *WorkRequest) Reset()         { *m = WorkRequest{} }
func (m *WorkRequest) String() string { return proto.CompactTextString(m) }
func (*WorkRequest) ProtoMessage()    {}

type WorkConfigResponse struct {
	Key   []string `protobuf:"bytes,1,rep,name=key" json:"key,omitempty"`
	Value []string `protobuf:"bytes,2,rep,name=value" json:"value,omitempty"`
}

func (m *WorkConfigResponse) Reset()         { *m = WorkConfigResponse{} }
func (m *WorkConfigResponse) String() string { return proto.CompactTextString(m) }
func (*WorkConfigResponse) ProtoMessage()    {}

func init() {
}

// Client API for MapperStream service

type MapperStreamClient interface {
	GetStreamEmitResult(ctx context.Context, opts ...grpc.CallOption) (MapperStream_GetStreamEmitResultClient, error)
}

type mapperStreamClient struct {
	cc *grpc.ClientConn
}

func NewMapperStreamClient(cc *grpc.ClientConn) MapperStreamClient {
	return &mapperStreamClient{cc}
}

func (c *mapperStreamClient) GetStreamEmitResult(ctx context.Context, opts ...grpc.CallOption) (MapperStream_GetStreamEmitResultClient, error) {
	stream, err := grpc.NewClientStream(ctx, &_MapperStream_serviceDesc.Streams[0], c.cc, "/mapreduce.MapperStream/GetStreamEmitResult", opts...)
	if err != nil {
		return nil, err
	}
	x := &mapperStreamGetStreamEmitResultClient{stream}
	return x, nil
}

type MapperStream_GetStreamEmitResultClient interface {
	Send(*MapperRequest) error
	Recv() (*MapperResponse, error)
	grpc.ClientStream
}

type mapperStreamGetStreamEmitResultClient struct {
	grpc.ClientStream
}

func (x *mapperStreamGetStreamEmitResultClient) Send(m *MapperRequest) error {
	return x.ClientStream.SendMsg(m)
}

func (x *mapperStreamGetStreamEmitResultClient) Recv() (*MapperResponse, error) {
	m := new(MapperResponse)
	if err := x.ClientStream.RecvMsg(m); err != nil {
		return nil, err
	}
	return m, nil
}

// Server API for MapperStream service

type MapperStreamServer interface {
	GetStreamEmitResult(MapperStream_GetStreamEmitResultServer) error
}

func RegisterMapperStreamServer(s *grpc.Server, srv MapperStreamServer) {
	s.RegisterService(&_MapperStream_serviceDesc, srv)
}

func _MapperStream_GetStreamEmitResult_Handler(srv interface{}, stream grpc.ServerStream) error {
	return srv.(MapperStreamServer).GetStreamEmitResult(&mapperStreamGetStreamEmitResultServer{stream})
}

type MapperStream_GetStreamEmitResultServer interface {
	Send(*MapperResponse) error
	Recv() (*MapperRequest, error)
	grpc.ServerStream
}

type mapperStreamGetStreamEmitResultServer struct {
	grpc.ServerStream
}

func (x *mapperStreamGetStreamEmitResultServer) Send(m *MapperResponse) error {
	return x.ServerStream.SendMsg(m)
}

func (x *mapperStreamGetStreamEmitResultServer) Recv() (*MapperRequest, error) {
	m := new(MapperRequest)
	if err := x.ServerStream.RecvMsg(m); err != nil {
		return nil, err
	}
	return m, nil
}

var _MapperStream_serviceDesc = grpc.ServiceDesc{
	ServiceName: "mapreduce.MapperStream",
	HandlerType: (*MapperStreamServer)(nil),
	Methods:     []grpc.MethodDesc{},
	Streams: []grpc.StreamDesc{
		{
			StreamName:    "GetStreamEmitResult",
			Handler:       _MapperStream_GetStreamEmitResult_Handler,
			ServerStreams: true,
			ClientStreams: true,
		},
	},
}

// Client API for ReducerStream service

type ReducerStreamClient interface {
	GetStreamCollectResult(ctx context.Context, opts ...grpc.CallOption) (ReducerStream_GetStreamCollectResultClient, error)
}

type reducerStreamClient struct {
	cc *grpc.ClientConn
}

func NewReducerStreamClient(cc *grpc.ClientConn) ReducerStreamClient {
	return &reducerStreamClient{cc}
}

func (c *reducerStreamClient) GetStreamCollectResult(ctx context.Context, opts ...grpc.CallOption) (ReducerStream_GetStreamCollectResultClient, error) {
	stream, err := grpc.NewClientStream(ctx, &_ReducerStream_serviceDesc.Streams[0], c.cc, "/mapreduce.ReducerStream/GetStreamCollectResult", opts...)
	if err != nil {
		return nil, err
	}
	x := &reducerStreamGetStreamCollectResultClient{stream}
	return x, nil
}

type ReducerStream_GetStreamCollectResultClient interface {
	Send(*ReducerRequest) error
	Recv() (*ReducerResponse, error)
	grpc.ClientStream
}

type reducerStreamGetStreamCollectResultClient struct {
	grpc.ClientStream
}

func (x *reducerStreamGetStreamCollectResultClient) Send(m *ReducerRequest) error {
	return x.ClientStream.SendMsg(m)
}

func (x *reducerStreamGetStreamCollectResultClient) Recv() (*ReducerResponse, error) {
	m := new(ReducerResponse)
	if err := x.ClientStream.RecvMsg(m); err != nil {
		return nil, err
	}
	return m, nil
}

// Server API for ReducerStream service

type ReducerStreamServer interface {
	GetStreamCollectResult(ReducerStream_GetStreamCollectResultServer) error
}

func RegisterReducerStreamServer(s *grpc.Server, srv ReducerStreamServer) {
	s.RegisterService(&_ReducerStream_serviceDesc, srv)
}

func _ReducerStream_GetStreamCollectResult_Handler(srv interface{}, stream grpc.ServerStream) error {
	return srv.(ReducerStreamServer).GetStreamCollectResult(&reducerStreamGetStreamCollectResultServer{stream})
}

type ReducerStream_GetStreamCollectResultServer interface {
	Send(*ReducerResponse) error
	Recv() (*ReducerRequest, error)
	grpc.ServerStream
}

type reducerStreamGetStreamCollectResultServer struct {
	grpc.ServerStream
}

func (x *reducerStreamGetStreamCollectResultServer) Send(m *ReducerResponse) error {
	return x.ServerStream.SendMsg(m)
}

func (x *reducerStreamGetStreamCollectResultServer) Recv() (*ReducerRequest, error) {
	m := new(ReducerRequest)
	if err := x.ServerStream.RecvMsg(m); err != nil {
		return nil, err
	}
	return m, nil
}

var _ReducerStream_serviceDesc = grpc.ServiceDesc{
	ServiceName: "mapreduce.ReducerStream",
	HandlerType: (*ReducerStreamServer)(nil),
	Methods:     []grpc.MethodDesc{},
	Streams: []grpc.StreamDesc{
		{
			StreamName:    "GetStreamCollectResult",
			Handler:       _ReducerStream_GetStreamCollectResult_Handler,
			ServerStreams: true,
			ClientStreams: true,
		},
	},
}

// Client API for Master service

type MasterClient interface {
	GetWork(ctx context.Context, in *WorkRequest, opts ...grpc.CallOption) (Master_GetWorkClient, error)
}

type masterClient struct {
	cc *grpc.ClientConn
}

func NewMasterClient(cc *grpc.ClientConn) MasterClient {
	return &masterClient{cc}
}

func (c *masterClient) GetWork(ctx context.Context, in *WorkRequest, opts ...grpc.CallOption) (Master_GetWorkClient, error) {
	stream, err := grpc.NewClientStream(ctx, &_Master_serviceDesc.Streams[0], c.cc, "/mapreduce.Master/GetWork", opts...)
	if err != nil {
		return nil, err
	}
	x := &masterGetWorkClient{stream}
	if err := x.ClientStream.SendMsg(in); err != nil {
		return nil, err
	}
	if err := x.ClientStream.CloseSend(); err != nil {
		return nil, err
	}
	return x, nil
}

type Master_GetWorkClient interface {
	Recv() (*WorkConfigResponse, error)
	grpc.ClientStream
}

type masterGetWorkClient struct {
	grpc.ClientStream
}

func (x *masterGetWorkClient) Recv() (*WorkConfigResponse, error) {
	m := new(WorkConfigResponse)
	if err := x.ClientStream.RecvMsg(m); err != nil {
		return nil, err
	}
	return m, nil
}

// Server API for Master service

type MasterServer interface {
	GetWork(*WorkRequest, Master_GetWorkServer) error
}

func RegisterMasterServer(s *grpc.Server, srv MasterServer) {
	s.RegisterService(&_Master_serviceDesc, srv)
}

func _Master_GetWork_Handler(srv interface{}, stream grpc.ServerStream) error {
	m := new(WorkRequest)
	if err := stream.RecvMsg(m); err != nil {
		return err
	}
	return srv.(MasterServer).GetWork(m, &masterGetWorkServer{stream})
}

type Master_GetWorkServer interface {
	Send(*WorkConfigResponse) error
	grpc.ServerStream
}

type masterGetWorkServer struct {
	grpc.ServerStream
}

func (x *masterGetWorkServer) Send(m *WorkConfigResponse) error {
	return x.ServerStream.SendMsg(m)
}

var _Master_serviceDesc = grpc.ServiceDesc{
	ServiceName: "mapreduce.Master",
	HandlerType: (*MasterServer)(nil),
	Methods:     []grpc.MethodDesc{},
	Streams: []grpc.StreamDesc{
		{
			StreamName:    "GetWork",
			Handler:       _Master_GetWork_Handler,
			ServerStreams: true,
		},
	},
}

// Client API for Worker service

type WorkerClient interface {
}

type workerClient struct {
	cc *grpc.ClientConn
}

func NewWorkerClient(cc *grpc.ClientConn) WorkerClient {
	return &workerClient{cc}
}

// Server API for Worker service

type WorkerServer interface {
}

func RegisterWorkerServer(s *grpc.Server, srv WorkerServer) {
	s.RegisterService(&_Worker_serviceDesc, srv)
}

var _Worker_serviceDesc = grpc.ServiceDesc{
	ServiceName: "mapreduce.Worker",
	HandlerType: (*WorkerServer)(nil),
	Methods:     []grpc.MethodDesc{},
	Streams:     []grpc.StreamDesc{},
}