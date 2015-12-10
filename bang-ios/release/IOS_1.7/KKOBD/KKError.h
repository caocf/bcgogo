//
//  KKError.h
//  Better
//
//  Created by apple on 10-3-25.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

// ================================================================================================
//  Defines
// ================================================================================================

// Error type code
typedef enum {
	eErrorNetwork = 0,				// 网络错误, 详细请看subcode,subcode会含KKNetworkErrCode, 如 eKKConnectionFailureErrorType
	eErrorProtocol,					// 协议参数错误,服务器返回错误码 详细请看subcode,subcode会含error_protocol_base以上的错误码,如error_request_post_only
	eErrorJsonParser,				// 解析错误
	eErrorHTTPError,				// http响应为非 200 ok 错误, 详细请看subcode,subcode会含http status错误代码，如404等
	
	eKKErrorMax
	
} KKErrorTypeCode;

// Network error code (sub type), Just convert ASINetworkErrorType to KKErrCode, detail pls refer to ASIHTTPRequest.h
typedef enum {
    eKKConnectionFailureErrorType = 1, 
    eKKRequestTimedOutErrorType = 2,
    eKKAuthenticationErrorType = 3,
    eKKRequestCancelledErrorType = 4,
    eKKUnableToCreateRequestErrorType = 5,
    eKKInternalErrorWhileBuildingRequestType  = 6,
    eKKInternalErrorWhileApplyingCredentialsType  = 7,
	eKKFileManagementError = 8,
	eKKTooMuchRedirectionErrorType = 9,
	eKKUnhandledExceptionError = 10,
	
	eKKNetworkErrCodeMax = eKKUnhandledExceptionError
} KKNetworkErrCode;

// Protocol error code (sub type)
typedef enum {
	
	// 10000以下的保留为HTTP错误代码使用，如501，404等
	
	// 10001 ~ 10100 内的为Reserved
	// error_reserved_base = 10100,
	error_reserved_base = 10099,
	
	// 10100以上为KaiK showbook 服务器返回错误代码
	error_protocol_none = 10000,						// 操作成功
	error_request_not_found = error_reserved_base,
	error_parse_not_json,

	
	
	error_protocol_unknown,								// 未知错误
	error_protocol_max = error_protocol_unknown
	
} _KKProtocolErrCode;
typedef NSInteger KKProtocolErrCode;

#pragma mark －
#pragma mark KKError

@interface KKError : NSObject
{
	NSString *_description;
	NSInteger _code;
	NSInteger _subCode;
	NSError *_detail;
}

@property (nonatomic, copy) NSString *description;
@property (nonatomic, assign) NSInteger code;
@property (nonatomic, assign) NSInteger subCode;
@property (nonatomic, retain) NSError *detail;

+ (KKError*) KKErrorWithCode:(NSInteger)aTypecode withSubcode:(NSInteger)aSubcode withDesc:(NSString*)aDesc withDetailErr:(NSError*)aDetail;
- (KKError*) initWithCode:(NSInteger)aTypecode withSubcode:(NSInteger)aSubcode withDesc:(NSString*)aDesc withDetailErr:(NSError*)aDetail;

+ (NSString*) protocolErrCode2String:(NSInteger)errcode;
+ (NSInteger) protocolErrString2Code:(NSString*)errString;

@end

