//
//  KKError.m
//  Better
//
//  Created by apple on 10-3-25.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "KKError.h"

// ================================================================================================
//  Define
// ================================================================================================
// Note: sequence of following must be consistent with protocol error code
static const NSString* k_KKProtocolErrString[] = {
	@"error_request_not_found",
	@"error_parse_root_not_response",
	@"error.isbn.illegal",									// 输入的isbn非法 
	
	@"error.protocol.unknown"								// 未知错误
};

// ================================================================================================
//  KKError
// ================================================================================================
#pragma mark -
@implementation KKError

@synthesize description = _description;
@synthesize code = _code;
@synthesize subCode = _subCode;
@synthesize detail = _detail;

+ (KKError*) KKErrorWithCode:(NSInteger)aTypecode withSubcode:(NSInteger)aSubcode withDesc:(NSString*)aDesc withDetailErr:(NSError*)aDetail
{
	KKError *err = [[KKError alloc]initWithCode:aTypecode withSubcode:aSubcode withDesc:aDesc withDetailErr:aDetail];
	return [err autorelease];
}

- (KKError*) initWithCode:(NSInteger)aTypecode withSubcode:(NSInteger)aSubcode withDesc:(NSString*)aDesc withDetailErr:(NSError*)aDetail
{
	self = [super init];
	
	self.code = aTypecode;
	self.subCode = aSubcode;
	self.description = aDesc;
	self.detail = aDetail;
	
	return self;
}

- (void) dealloc
{
	self.description = nil;
	self.detail = nil;
	
	[super dealloc];
}

+ (NSString*) protocolErrCode2String:(NSInteger)errcode
{
	if (errcode > error_protocol_max || errcode < error_protocol_none)
		return (NSString*)k_KKProtocolErrString[error_protocol_max-error_reserved_base];

	NSInteger offset = errcode - error_reserved_base;
	return (NSString*)k_KKProtocolErrString[offset];
}

+ (NSInteger) protocolErrString2Code:(NSString*)errString
{
	NSInteger i,n = sizeof(k_KKProtocolErrString)/sizeof(NSString*);

	for (i=0; i<n; i++) {
		if (NSOrderedSame == [errString caseInsensitiveCompare:(NSString*)k_KKProtocolErrString[i]]) 
			break;
	}
	
	if (i < n) 
		return error_reserved_base + i;
	return error_protocol_unknown;
}


@end
