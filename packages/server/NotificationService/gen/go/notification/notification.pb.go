// Code generated by protoc-gen-go. DO NOT EDIT.
// versions:
// 	protoc-gen-go v1.34.2
// 	protoc        v5.28.0
// source: notification/notification.proto

package notification

import (
	protoreflect "google.golang.org/protobuf/reflect/protoreflect"
	protoimpl "google.golang.org/protobuf/runtime/protoimpl"
	reflect "reflect"
	sync "sync"
)

const (
	// Verify that this generated code is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(20 - protoimpl.MinVersion)
	// Verify that runtime/protoimpl is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(protoimpl.MaxVersion - 20)
)

type CreateNotificationRequest struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	Sender   string `protobuf:"bytes,1,opt,name=sender,proto3" json:"sender,omitempty"`
	Receiver string `protobuf:"bytes,2,opt,name=receiver,proto3" json:"receiver,omitempty"`
	Content  string `protobuf:"bytes,3,opt,name=content,proto3" json:"content,omitempty"`
}

func (x *CreateNotificationRequest) Reset() {
	*x = CreateNotificationRequest{}
	if protoimpl.UnsafeEnabled {
		mi := &file_notification_notification_proto_msgTypes[0]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *CreateNotificationRequest) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*CreateNotificationRequest) ProtoMessage() {}

func (x *CreateNotificationRequest) ProtoReflect() protoreflect.Message {
	mi := &file_notification_notification_proto_msgTypes[0]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use CreateNotificationRequest.ProtoReflect.Descriptor instead.
func (*CreateNotificationRequest) Descriptor() ([]byte, []int) {
	return file_notification_notification_proto_rawDescGZIP(), []int{0}
}

func (x *CreateNotificationRequest) GetSender() string {
	if x != nil {
		return x.Sender
	}
	return ""
}

func (x *CreateNotificationRequest) GetReceiver() string {
	if x != nil {
		return x.Receiver
	}
	return ""
}

func (x *CreateNotificationRequest) GetContent() string {
	if x != nil {
		return x.Content
	}
	return ""
}

type Response struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	Success bool   `protobuf:"varint,1,opt,name=success,proto3" json:"success,omitempty"`
	Message string `protobuf:"bytes,2,opt,name=message,proto3" json:"message,omitempty"`
}

func (x *Response) Reset() {
	*x = Response{}
	if protoimpl.UnsafeEnabled {
		mi := &file_notification_notification_proto_msgTypes[1]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *Response) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*Response) ProtoMessage() {}

func (x *Response) ProtoReflect() protoreflect.Message {
	mi := &file_notification_notification_proto_msgTypes[1]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use Response.ProtoReflect.Descriptor instead.
func (*Response) Descriptor() ([]byte, []int) {
	return file_notification_notification_proto_rawDescGZIP(), []int{1}
}

func (x *Response) GetSuccess() bool {
	if x != nil {
		return x.Success
	}
	return false
}

func (x *Response) GetMessage() string {
	if x != nil {
		return x.Message
	}
	return ""
}

var File_notification_notification_proto protoreflect.FileDescriptor

var file_notification_notification_proto_rawDesc = []byte{
	0x0a, 0x1f, 0x6e, 0x6f, 0x74, 0x69, 0x66, 0x69, 0x63, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x2f, 0x6e,
	0x6f, 0x74, 0x69, 0x66, 0x69, 0x63, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x2e, 0x70, 0x72, 0x6f, 0x74,
	0x6f, 0x12, 0x04, 0x70, 0x6f, 0x73, 0x74, 0x22, 0x69, 0x0a, 0x19, 0x43, 0x72, 0x65, 0x61, 0x74,
	0x65, 0x4e, 0x6f, 0x74, 0x69, 0x66, 0x69, 0x63, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x52, 0x65, 0x71,
	0x75, 0x65, 0x73, 0x74, 0x12, 0x16, 0x0a, 0x06, 0x73, 0x65, 0x6e, 0x64, 0x65, 0x72, 0x18, 0x01,
	0x20, 0x01, 0x28, 0x09, 0x52, 0x06, 0x73, 0x65, 0x6e, 0x64, 0x65, 0x72, 0x12, 0x1a, 0x0a, 0x08,
	0x72, 0x65, 0x63, 0x65, 0x69, 0x76, 0x65, 0x72, 0x18, 0x02, 0x20, 0x01, 0x28, 0x09, 0x52, 0x08,
	0x72, 0x65, 0x63, 0x65, 0x69, 0x76, 0x65, 0x72, 0x12, 0x18, 0x0a, 0x07, 0x63, 0x6f, 0x6e, 0x74,
	0x65, 0x6e, 0x74, 0x18, 0x03, 0x20, 0x01, 0x28, 0x09, 0x52, 0x07, 0x63, 0x6f, 0x6e, 0x74, 0x65,
	0x6e, 0x74, 0x22, 0x3e, 0x0a, 0x08, 0x52, 0x65, 0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65, 0x12, 0x18,
	0x0a, 0x07, 0x73, 0x75, 0x63, 0x63, 0x65, 0x73, 0x73, 0x18, 0x01, 0x20, 0x01, 0x28, 0x08, 0x52,
	0x07, 0x73, 0x75, 0x63, 0x63, 0x65, 0x73, 0x73, 0x12, 0x18, 0x0a, 0x07, 0x6d, 0x65, 0x73, 0x73,
	0x61, 0x67, 0x65, 0x18, 0x02, 0x20, 0x01, 0x28, 0x09, 0x52, 0x07, 0x6d, 0x65, 0x73, 0x73, 0x61,
	0x67, 0x65, 0x32, 0x5c, 0x0a, 0x13, 0x4e, 0x6f, 0x74, 0x69, 0x66, 0x69, 0x63, 0x61, 0x74, 0x69,
	0x6f, 0x6e, 0x53, 0x65, 0x72, 0x76, 0x69, 0x63, 0x65, 0x12, 0x45, 0x0a, 0x12, 0x43, 0x72, 0x65,
	0x61, 0x74, 0x65, 0x4e, 0x6f, 0x74, 0x69, 0x66, 0x69, 0x63, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x12,
	0x1f, 0x2e, 0x70, 0x6f, 0x73, 0x74, 0x2e, 0x43, 0x72, 0x65, 0x61, 0x74, 0x65, 0x4e, 0x6f, 0x74,
	0x69, 0x66, 0x69, 0x63, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x52, 0x65, 0x71, 0x75, 0x65, 0x73, 0x74,
	0x1a, 0x0e, 0x2e, 0x70, 0x6f, 0x73, 0x74, 0x2e, 0x52, 0x65, 0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65,
	0x42, 0x4e, 0x5a, 0x4c, 0x67, 0x69, 0x74, 0x68, 0x75, 0x62, 0x2e, 0x63, 0x6f, 0x6d, 0x2f, 0x53,
	0x68, 0x75, 0x74, 0x53, 0x61, 0x73, 0x68, 0x61, 0x2f, 0x64, 0x65, 0x76, 0x68, 0x75, 0x62, 0x2f,
	0x70, 0x61, 0x63, 0x6b, 0x61, 0x67, 0x65, 0x73, 0x2f, 0x73, 0x65, 0x72, 0x76, 0x65, 0x72, 0x2f,
	0x4e, 0x6f, 0x74, 0x69, 0x66, 0x69, 0x63, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x53, 0x65, 0x72, 0x76,
	0x69, 0x63, 0x65, 0x3b, 0x6e, 0x6f, 0x74, 0x69, 0x66, 0x69, 0x63, 0x61, 0x74, 0x69, 0x6f, 0x6e,
	0x62, 0x06, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x33,
}

var (
	file_notification_notification_proto_rawDescOnce sync.Once
	file_notification_notification_proto_rawDescData = file_notification_notification_proto_rawDesc
)

func file_notification_notification_proto_rawDescGZIP() []byte {
	file_notification_notification_proto_rawDescOnce.Do(func() {
		file_notification_notification_proto_rawDescData = protoimpl.X.CompressGZIP(file_notification_notification_proto_rawDescData)
	})
	return file_notification_notification_proto_rawDescData
}

var file_notification_notification_proto_msgTypes = make([]protoimpl.MessageInfo, 2)
var file_notification_notification_proto_goTypes = []any{
	(*CreateNotificationRequest)(nil), // 0: post.CreateNotificationRequest
	(*Response)(nil),                  // 1: post.Response
}
var file_notification_notification_proto_depIdxs = []int32{
	0, // 0: post.NotificationService.CreateNotification:input_type -> post.CreateNotificationRequest
	1, // 1: post.NotificationService.CreateNotification:output_type -> post.Response
	1, // [1:2] is the sub-list for method output_type
	0, // [0:1] is the sub-list for method input_type
	0, // [0:0] is the sub-list for extension type_name
	0, // [0:0] is the sub-list for extension extendee
	0, // [0:0] is the sub-list for field type_name
}

func init() { file_notification_notification_proto_init() }
func file_notification_notification_proto_init() {
	if File_notification_notification_proto != nil {
		return
	}
	if !protoimpl.UnsafeEnabled {
		file_notification_notification_proto_msgTypes[0].Exporter = func(v any, i int) any {
			switch v := v.(*CreateNotificationRequest); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_notification_notification_proto_msgTypes[1].Exporter = func(v any, i int) any {
			switch v := v.(*Response); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
	}
	type x struct{}
	out := protoimpl.TypeBuilder{
		File: protoimpl.DescBuilder{
			GoPackagePath: reflect.TypeOf(x{}).PkgPath(),
			RawDescriptor: file_notification_notification_proto_rawDesc,
			NumEnums:      0,
			NumMessages:   2,
			NumExtensions: 0,
			NumServices:   1,
		},
		GoTypes:           file_notification_notification_proto_goTypes,
		DependencyIndexes: file_notification_notification_proto_depIdxs,
		MessageInfos:      file_notification_notification_proto_msgTypes,
	}.Build()
	File_notification_notification_proto = out.File
	file_notification_notification_proto_rawDesc = nil
	file_notification_notification_proto_goTypes = nil
	file_notification_notification_proto_depIdxs = nil
}
