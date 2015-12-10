//
//  KKProtocolEngine.m
//  KKShowBooks
//
//  Created by zhuyc on 12-10-11.
//  Copyright (c) 2012年 zhuyc. All rights reserved.
//

#import "ASIHTTPRequest.h"
#import "ASIFormDataRequest.h"
#import "TBXML.h"
#import "KKError.h"
#import "KKUtils.h"
#import "JSONKit.h"
#import "KKProtocolParser.h"
#import "KKProtocolEngineDelegate.h"
#import "KKProtocolEngine.h"
#import "KKGlobal.h"
#import "KKModelComplex.h"
#import "KKAppDelegate.h"
#import "KKApplicationDefine.h"
#import "KKBLEEngine.h"
#import "BMKMapView.h"
#import "KKPreference.h"
#import "NSObject+AutoMagicCoding.h"

// ================================================================================================
//  Defines
// ================================================================================================
#define KK_HTTP_DEBUG
//#define KK_HTTP_DEBUG_SAVE_LOG
#define KK_HTTP_ERROR_DEBUG

#define KK_HTTP_RETRY_COUNT 3

// ================================================================================================
#define KKSetParamString(params, key, value, filledNull)  \
do \
{    if (value && [value length] > 0) \
        [params setValue:value forKey:key]; \
    else if (filledNull) \
        [params setValue:@"NULL" forKey:key]; \
} while(0)

#define KKSetParamDate(params, key, value, filledNull) \
do \
{   NSString *dateString = nil; \
    if (value) { \
        dateString = [NSString stringWithFormat:@"%lld",(long long int)([value timeIntervalSince1970]*1000)]; \
    } \
    KKSetParamString(params, key, dateString, filledNull); \
} while(0)

#define KKSetParamFloat(params, key, value, filledNull) \
do \
{ \
    NSString *valueString = [NSString stringWithFormat:@"%f", value]; \
    KKSetParamString(params, key, valueString, filledNull); \
} while(0)

#define KKSetParamInt(params, key, value, filledNull) \
do \
{ \
    NSString *valueString = [NSString stringWithFormat:@"%d", value]; \
    KKSetParamString(params, key, valueString, filledNull); \
} while(0)

#define KKSetSequenceParamString(keySeq, params, key, value) \
do { \
    KKSetParamString(params, key, value, YES); \
    [keySeq addObject:key]; \
} while(0)


#define KKSetSequenceParamInt(keySeq, params, key, value) \
do { \
    NSString *valueString_ = [NSString stringWithFormat:@"%d", value]; \
    KKSetParamString(params, key, valueString_, YES); \
    [keySeq addObject:key]; \
} while(0)

#define KKSetSequenceParamFloat(keySeq, params, key, value) \
do { \
    KKSetParamFloat(params, key, value, YES); \
    [keySeq addObject:key]; \
} while(0)


static const NSTimeInterval KKDefaultProtocolTimeoutSeconds = 15;

typedef enum {
	eHTTPRequest = 0,
	eHTTPFormRequest
} KKHTTPRequestType;


// singletone
static KKProtocolEngine *s_protocolEngine = nil;

static NSInteger s_KKProtocolReqID = 0;     // identify the request, increatment
static const NSString *s_KKProtocolReqKey = @"req_id"; 
static const NSString *s_KKProtocolReqDelegateKey = @"req_delegate";
static const NSString *s_KKProtocolReqApiId = @"req_apiId";

//注册用户访问URL
static const KKPtlApiDictionary k_KKPtlApiDict[] = {
    { @"%@/api/user/registration",                  ePtlApi_user_register },                 // 用户注册
    { @"%@/api/login",                              ePtlApi_user_login },                    // 用户登录
    { @"%@/api/obd/binding",                        ePtlApi_obd_bind },                      // OBD绑定
    { @"%@/api/user/password",                      ePtlApi_user_password },                 // 找回密码
    { @"%@/api/newVersion",                         ePtlApi_new_version },                   // 升级检测
    
    { @"%@/api/vehicle/list",                       ePtlApi_vehicle_list},                   // 获取车辆列表
    { @"%@/api/vehicle/vehicleInfo",                ePtlApi_vehicle_saveInfo },              // 保存车辆信息
    { @"%@/api/vehicle/list/guest",                 ePtlApi_vehicle_saveList},                 // 保存车辆列表
    { @"%@/api/vehicle/singleVehicle",              ePtlApi_vehicle_getInfo },               // 获取一辆车信息
    { @"%@/api/vehicle/singleVehicle",              ePtlApi_vehicle_delete },                // 删除车辆
    { @"%@/api/vehicle/brandModel",                 ePtlApi_vehicle_getModelByKey },         // 根据关键字获取车辆品牌和车型
    { @"%@/api/vehicle/fault",                      ePtlApi_vehicle_fault },                 // 发送车辆故障信息
    { @"%@/api/vehicle/faultCodeList",              ePtlApi_vehicle_faultCodeList},               // 获取故障码列表
    { @"%@/api/vehicle/faultCode",                  ePtlApi_vehicle_faultCodeOperate},
    { @"%@/api/vehicle/faultDic",                   ePtlApi_vehicle_faultDic },              // 故障字典信息更新
    { @"%@/api/vehicle/condition",                  ePtlApi_vehicle_condition },             // 发送车况信息
    
    { @"%@/api/area/list",                          ePtlApi_area_list },                     // 获取地区列表
    { @"%@/api/shop/list",                          ePtlApi_shop_searchList },               // 查询推荐店铺
    { @"%@/api/shop/detail",                        ePtlApi_shop_detail },                   // 根据店铺ID获取店铺详情
    { @"%@/api/shop/suggestions",                   ePtlApi_shop_suggestionsByKey },         // 根据关键字获取店铺建议列表
    
    { @"%@/api/message/polling",                    ePtlApi_message_polling },               // 消息轮询
    { @"%@/api/service/appointment",                ePtlApi_service_appointment },           // 预约服务
    { @"%@/api/service/AllHistoryList",                ePtlApi_service_historyList },           // 服务历史查询
    { @"%@/api/service/historyDetail",              ePtlApi_service_historyDetail },         // 服务历史详情
    { @"%@/api/service/singleService",              ePtlApi_service_delete },                // 取消服务
    
    { @"%@/api/shop/score",                         ePtlApi_shop_score },                    // 评价店铺
    { @"%@/api/user/information",                   ePtlApi_user_information },              // 查看个人资料
    { @"%@/api/user/password",                      ePtlApi_user_passwordModify },           // 修改密码
    { @"%@/api/user/information",                   ePtlApi_user_informationModify },        // 修改个人资料
    { @"%@/api/service/maintain",                   ePtlApi_vehicle_maintainModify },        // 修改保养信息
    { @"%@/api/logout",                             ePtlApi_user_logout },                   // 注销
    { @"%@/api/user/feedback",                      ePtlApi_user_feedback },                 // 用户反馈
    { @"%@/api/serviceCategory/list",               ePtlApi_serviceCategory_list },          // 获取服务范围
    { @"%@/api/vehicle/updateDefault",              ePtlApi_vehicle_updateDefault },         // 修改默认车辆
    { @"%@/api/vehicle/singleVehicle",              ePtlApi_vehicle_singleVehicle_vehicleVin },   // 根据vehicleVin获取车辆信息
    {@"%@/api/vehicle/info/suggestion",             ePtlApi_Register_SuggestVehicle},
    
    {@"http://apis.juhe.cn/oil/local",              ePtlApi_oil_stationList},    //加油站列表
    {@"%@/api/shop/binding",              ePtlApi_Register_shopBinding},               //绑定店铺，仅仅适用于注册只填店铺
    
    {@"%@/api/violateRegulations/juhe/area/list",   ePtlApi_violate_juheAreaList},        //违章-聚合支持的地区列表
    {@"%@/api/driveLog/newDriveLog",            ePtlApi_driveRecord_upload},    //上传行车日志
    
    {@"http://v.juhe.cn/wz/query?",                 ePtlApi_violate_query},
    
    {@"%@/api/user/updateAppUserConfig",            ePtlApi_appUserConfig_update},

};
//游客访问URL
static const KKPtlApiDictionary k_KKPtlApiDict_Visitor[] = {
    { @"%@/api/user/registration",                  ePtlApi_user_register },                 // 用户注册
    { @"%@/api/login",                              ePtlApi_user_login },                    // 用户登录
    { @"%@/api/user/password",                      ePtlApi_user_password },                 // 找回密码
    { @"%@/api/newVersion",                         ePtlApi_new_version },                   // 升级检测
    
    { @"%@/api/vehicle/brandModel",                 ePtlApi_vehicle_getModelByKey },         // 根据关键字获取车辆品牌和车型
    
    { @"%@/api/area/list",                          ePtlApi_area_list },                     // 获取地区列表
    { @"%@/api/shop/list/guest",                          ePtlApi_shop_searchList },               // 查询推荐店铺
    { @"%@/api/shop/detail/guest",                        ePtlApi_shop_detail },                   // 根据店铺ID获取店铺详情
    { @"%@/api/shop/suggestions/guest",                   ePtlApi_shop_suggestionsByKey },         // 根据关键字获取店铺建议列表
    { @"%@/api/serviceCategory/list",               ePtlApi_serviceCategory_list },          // 获取服务范围
    
    { @"%@/api/violateRegulations/juhe/area/list",   ePtlApi_violate_juheAreaList},        //违章-聚合支持的地区列表
    {@"http://v.juhe.cn/wz/query?",                 ePtlApi_violate_query},
    {@"http://apis.juhe.cn/oil/local",              ePtlApi_oil_stationList},    //加油站列表
    { @"%@/api/guest/feedback",                      ePtlApi_user_feedback },                 // 用户反馈
};
@interface KKProtocolEngine(_private)
//-------------------------------------------------------------------------------------------------
+ (NSString *) makeParams:(NSMutableDictionary*)paramDict;
+ (NSString *) makeURIParams:(NSMutableDictionary*)paramDict;
+ (NSString *) makeURIParams:(NSMutableDictionary*)paramDict sequenceKeys:(NSMutableArray *)sequenceKeys;
+ (BOOL) makePostData:(NSMutableDictionary*)dataDict request:(KKFormDataRequest*)request;
+ (BOOL) makePutData:(NSMutableDictionary*)dataDict request:(KKFormDataRequest*)request;

- (KKHTTPRequest *) basicRequestWithUser:(KKHTTPRequestType)requestType user:(NSString*)user password:(NSString*)password url:(NSString*)url delegate:(id)aDelegate;
- (KKHTTPRequest *) basicRequest:(KKHTTPRequestType)requestType url:(NSString*)url apiId:(NSInteger)apiId delegate:(id)aDelegate;

- (void) issueRequest:(KKHTTPRequest*)request;
- (NSInteger) requestID:(KKHTTPRequest*)request;    // if not specify the request id, return <0;
- (NSInteger) requestApiID:(KKHTTPRequest*)request;

- (id) requestDelegate:(KKHTTPRequest *)request;

- (void) DispatchResponse:(KKHTTPRequest*)request withObject:(id)anObject action:(SEL)aSelctor;

// Get index in api dictionay, if not found, return -1
+ (NSInteger)indexInApiDictByUrl:(NSString*)url;
+ (NSInteger)indexInApiDictById:(KKProtocolApiId)apiId;
- (NSString *) getAccessUrl:(KKProtocolApiId)apiId;

@end

// ================================================================================================
//  KKProtocolEngine
// ================================================================================================
@implementation KKProtocolEngine
@synthesize delegates = _delegates;
@synthesize userName = _userName;
@synthesize password = _password;
@synthesize language = _language;
@synthesize sessionCookie = _sessionCookie;

#pragma mark -
#pragma mark life cycle
- (id)initWithLanguage:(NSString*)aLanguage;
{
	if (nil == (self=[super init]))
		return nil;
	
	_requestQueue = [[NSMutableArray alloc] init];
	_delegates = [[NSMutableArray alloc] init];
	if ([aLanguage length] > 0)
		self.language = aLanguage;
	else 
		self.language = @"zh-cn";						// default chinese
	
	// for test
//	self.userName = @"5";
//	self.password = @"e7e9a05d0aa8677fc54e990c812d87f9";
	
	return self;
}

+ (id)KKPtlEngineWithLanguage:(NSString*)aLanguage
{
	KKProtocolEngine *engine = [[KKProtocolEngine alloc] initWithLanguage:aLanguage];
	return [engine autorelease];
}

- (void) removeDelegate:(id)aDelegate
{
	[_delegates removeObject:aDelegate];
}

- (void) cancelRequest:(NSInteger)aRequestID
{
	if (aRequestID <= KKInvalidRequestID)
		return;
	
	for (NSInteger i = 0; i < [_requestQueue count]; i++) {
		KKHTTPRequest *request = (KKHTTPRequest*)[_requestQueue objectAtIndex:i];
		NSInteger rid = [self requestID:request];
		if (aRequestID == rid) {
			[request cancel];
			return;
		}
	}
}

- (void) dealloc
{
	[_requestQueue release];
	[_delegates release];
	_requestQueue = nil;
	_delegates = nil;
	self.userName = nil;
	self.password = nil;
	self.language = nil;
    self.sessionCookie = nil;
	
	[super dealloc];
}


#pragma mark -
#pragma mark custom method
+ (NSString *) getKKServerDomain
{
	NSString *domain = nil;
	if (NO == [KKProtocolEngine sharedPtlEngine].serviceEnvironment) {
//        domain = [[NSString alloc] initWithString:@"http://192.168.1.33:8080"];
		domain = [[NSString alloc] initWithString:@"https://phone.bcgogo.cn:1443"];
    }
	else {
//        domain = [[NSString alloc] initWithString:@"phone.bcgogo.cn"];
		domain = [[NSString alloc] initWithString:@"https://shop.bcgogo.com"];
//        domain = [[NSString alloc] initWithString:@"192.168.1.165:8080"];
	}
	return [domain autorelease];
}

//+ (NSString*) getUUIDSecret;
//{
//	NSString *udid = [[UIDevice currentDevice] uniqueIdentifier];
//	return udid;
//}

+ (NSInteger)indexInApiDictByUrl:(NSString*)url
{
	NSInteger i;
	NSString *urlWithoutQueryAndDomain = [NSString stringWithString:url];
	NSRange range = [url rangeOfString:@"?"];
	if (range.location != NSNotFound) 
		urlWithoutQueryAndDomain = [url substringToIndex:range.location];
	range = [urlWithoutQueryAndDomain rangeOfString:@"http://"];
	if (range.location != NSNotFound)
		urlWithoutQueryAndDomain = [urlWithoutQueryAndDomain substringFromIndex:range.location+range.length];
	range = [urlWithoutQueryAndDomain rangeOfString:@"/"];
	if (range.location != NSNotFound)
		urlWithoutQueryAndDomain = [urlWithoutQueryAndDomain substringFromIndex:range.location+range.length];
	
	for (i=0; i<sizeof(k_KKPtlApiDict)/sizeof(KKPtlApiDictionary); i++) {
		KKPtlApiDictionary item = k_KKPtlApiDict[i];
		NSString *strApi = item.strUrl;
		range = [item.strUrl rangeOfString:@"http://"];
		if (range.location != NSNotFound)
			strApi = [item.strUrl substringFromIndex:range.location+range.length];
		range = [strApi rangeOfString:@"/"];
		if (range.location != NSNotFound)
			strApi = [strApi substringFromIndex:range.location+range.length];
		//if (YES == [strApi isEqualToString:urlWithoutQueryAndDomain])
        if (NSOrderedSame == [strApi caseInsensitiveCompare:urlWithoutQueryAndDomain])
			break;
	}
	if (i<sizeof(k_KKPtlApiDict)/sizeof(KKPtlApiDictionary))
		return i;
	return -1;
}

+ (NSInteger)indexInApiDictById:(KKProtocolApiId)apiId
{
	NSInteger i;
	
    switch ([KKAuthorization sharedInstance].authorizationType) {
        case Authorization_Visitor:
        {
            for (i=0; i<sizeof(k_KKPtlApiDict_Visitor)/sizeof(KKPtlApiDictionary); i++) {
                KKPtlApiDictionary item = k_KKPtlApiDict_Visitor[i];
                if (item.apiId == apiId)
                    break;
            }
            if (i<sizeof(k_KKPtlApiDict_Visitor)/sizeof(KKPtlApiDictionary))
                return i;
        }
            break;
        case Authorization_Register:
        {
            for (i=0; i<sizeof(k_KKPtlApiDict)/sizeof(KKPtlApiDictionary); i++) {
                KKPtlApiDictionary item = k_KKPtlApiDict[i];
                if (item.apiId == apiId) 
                    break;
            }
            if (i<sizeof(k_KKPtlApiDict)/sizeof(KKPtlApiDictionary))
                return i;
        }
            break;
    }
	return -1;
}

+ (KKProtocolApiId) url2Api:(NSURL*)url
{
	NSInteger i = [self indexInApiDictByUrl:[url absoluteString]];
	
	if (i<0)
		return ePtlApiNotSupport;
	return k_KKPtlApiDict[i].apiId;
}

- (NSString *) getAccessUrl:(KKProtocolApiId)apiId
{
	NSString *domain = [KKProtocolEngine getKKServerDomain];
	NSInteger index = [KKProtocolEngine indexInApiDictById:apiId];
    NSString *url = nil;
    if(index!=-1)
    {
        switch ([KKAuthorization sharedInstance].authorizationType) {
            case Authorization_Visitor:
                url = [[[NSString alloc] initWithFormat:k_KKPtlApiDict_Visitor[index].strUrl, domain] autorelease];
                break;
                
            case Authorization_Register:
                url = [[[NSString alloc] initWithFormat:k_KKPtlApiDict[index].strUrl, domain] autorelease];
                break;
        }
    }
	return url;
}

- (NSString *) getAccessUrlInMulti:(KKProtocolApiId)apiId;
{
	NSString *origin = [self getAccessUrl:apiId];
	NSRange range = [origin rangeOfString:@"index.php"];
	NSString *url = [origin substringFromIndex:range.location+range.length];
	return url;
}

- (NSString *) basicAuthHeaderValue
{
    if ([self.userName length] == 0 || [self.password length] == 0)
        return nil;
    
    NSString *base64 = [KKUtils encodeBase64:[NSString stringWithFormat:@"%@:%@", self.userName, self.password]];
	NSString *value = [NSString stringWithFormat:@"Basic %@", base64];
    return value;
}

+ (KKProtocolEngine*)sharedPtlEngine 
{
	if (s_protocolEngine)
		return  s_protocolEngine;
	s_protocolEngine = [[KKProtocolEngine alloc] initWithLanguage:nil];
	return s_protocolEngine;
}

#pragma mark -
#pragma mark request relative
+ (NSString*) makeParams:(NSMutableDictionary*)paramDict
{
	NSInteger count = [paramDict count];
	if (count == 0) 
		return nil;
	
	NSMutableString *query = [[NSMutableString alloc] initWithCapacity:100];
	NSInteger i = 0;
	for (id key in paramDict) {
		NSString *pair = [NSString stringWithFormat:@"%@=%@", key, [paramDict objectForKey:key]];
		[query appendString:pair];
		if (i < count - 1) 
			[query appendString:@"&"];
		i++;
	}
	return [query autorelease];
}

+ (NSString *) makeURIParams:(NSMutableDictionary*)paramDict
{
	NSInteger count = [paramDict count];
	if (count == 0)
		return nil;
	
	NSMutableString *query = [[NSMutableString alloc] initWithCapacity:100];
	for (id key in paramDict) {
		NSString *pair = [NSString stringWithFormat:@"/%@/%@", key, [paramDict objectForKey:key]];
		[query appendString:pair];
	}
	return [query autorelease];
}

+ (NSString *) makeURIParams:(NSMutableDictionary*)paramDict sequenceKeys:(NSMutableArray *)sequenceKeys
{
    return [self makeURIParams:paramDict sequenceKeys:sequenceKeys containKeys:YES];
}

+ (NSString *) makeURIParams:(NSMutableDictionary*)paramDict sequenceKeys:(NSMutableArray *)sequenceKeys containKeys:(BOOL) containKeys
{
	NSInteger count = [paramDict count];
	if (count == 0 || count != [sequenceKeys count])
		return nil;
	
	NSMutableString *query = [[NSMutableString alloc] initWithCapacity:100];
	for (id key in sequenceKeys) {
        NSString *pair = nil;
        if (containKeys)
            pair = [NSString stringWithFormat:@"/%@/%@", key, [paramDict objectForKey:key]];
		else
            pair = [NSString stringWithFormat:@"/%@", [paramDict objectForKey:key]];
        [query appendString:pair];
	}
	return [query autorelease];
}

+(NSString *)makeURIParamsWithAnd:(NSMutableDictionary*)paramDict sequenceKeys:(NSMutableArray *)sequenceKeys
{
    NSInteger count = [paramDict count];
	if (count == 0 || count != [sequenceKeys count])
		return nil;
	
	NSMutableString *query = [[NSMutableString alloc] initWithCapacity:100];
	for (id key in sequenceKeys) {
        NSString *pair = nil;
        pair = [NSString stringWithFormat:@"%@＝%@&", key, [paramDict objectForKey:key]];
        [query appendString:pair];
	}
	return [query autorelease];
}


+ (BOOL) makePostData:(NSMutableDictionary*)dataDict request:(KKFormDataRequest*)request
{
	if (nil == request)
		return NO;
	
	for (id key in dataDict)
		[request setPostValue:[dataDict objectForKey:key] forKey:(NSString*)key];
	
	return YES;
}

+ (BOOL) makePutData:(NSMutableDictionary*)dataDict request:(KKFormDataRequest*)request
{
    if (nil == request)
        return NO;
    
    NSData *postData = [dataDict JSONData];
    if ([postData length] == 0)
        return NO;
    
    
    NSMutableData *npd = [[NSMutableData alloc] initWithCapacity:1024];
    [npd appendData:postData];
    [request setPostBody:npd];
    [request setRequestMethod:@"PUT"];
    [request addRequestHeader:@"Content-Type" value:@"application/json; charset=UTF-8"];
    [npd release];
    
    NSLog(@"putData is %@", [dataDict JSONString]);
    
    return YES;
}

- (KKHTTPRequest *) basicRequestWithUser:(KKHTTPRequestType)requestType user:(NSString*)user password:(NSString*)password url:(NSString*)url delegate:(id)aDelegate
{
	NSString *base64 = [KKUtils encodeBase64:[NSString stringWithFormat:@"%@:%@", user, password]];
	NSString *value = [NSString stringWithFormat:@"Basic %@", base64];
	
	KKHTTPRequest *request;
	NSURL *URL = [NSURL URLWithString:[url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
	if (eHTTPRequest == requestType)
		request = [[KKHTTPRequest alloc] initWithURL:URL];
	else {
		request = [[ASIFormDataRequest alloc] initWithURL:URL];
	}
	if (request.userInfo)
		[request.userInfo release];
	s_KKProtocolReqID++;
	NSString *strReqId = [NSString stringWithFormat:@"%d", s_KKProtocolReqID];
	if (aDelegate)
		request.userInfo = [NSDictionary dictionaryWithObjectsAndKeys:strReqId, s_KKProtocolReqKey, aDelegate, s_KKProtocolReqDelegateKey, nil];
	else
		request.userInfo = [NSDictionary dictionaryWithObjectsAndKeys:strReqId, s_KKProtocolReqKey, nil];
	
	[request addRequestHeader:@"User-Agent" value:@"bcgogoRequest"];
	[request addRequestHeader:@"Accept-Language" value:self.language];
	if ([user length] && [password length])
		[request addRequestHeader:@"Authorization" value:value];

    [request addRequestHeader:@"Connection" value:@"keep-alive"];
    
	return [request autorelease];
}

- (KKHTTPRequest *) basicRequest:(KKHTTPRequestType)requestType url:(NSString*)url apiId:(NSInteger)apiId delegate:(id)aDelegate
{
    if(url == nil)
    {
        return nil;
    }
	return [self basicRequest:requestType url:url URLEncode:YES apiId:apiId delegate:aDelegate];
}
- (KKHTTPRequest *) basicRequest:(KKHTTPRequestType)requestType url:(NSString*)url URLEncode:(BOOL)isEncode apiId:(NSInteger)apiId delegate:(id)aDelegate
{
    KKHTTPRequest *request;
	NSURL *URL;
    if(isEncode)
        URL = [NSURL URLWithString:[url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    else
        URL = [NSURL URLWithString:[url stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
	if (eHTTPRequest == requestType) {
		request = [[KKHTTPRequest alloc] initWithURL:URL];
        [request addRequestHeader:@"Content-Type" value:@"charset=UTF-8"];
    }
	else {
		request = [[ASIFormDataRequest alloc] initWithURL:URL];
	}
	if (request.userInfo)
		[request.userInfo release];
	s_KKProtocolReqID++;
	NSString *strReqId = [NSString stringWithFormat:@"%d", s_KKProtocolReqID];
    if (aDelegate)
		request.userInfo = [NSMutableDictionary dictionaryWithObjectsAndKeys:strReqId, s_KKProtocolReqKey, aDelegate, s_KKProtocolReqDelegateKey, nil];
	else
		request.userInfo = [NSMutableDictionary dictionaryWithObjectsAndKeys:strReqId, s_KKProtocolReqKey, nil];
	if (apiId >= 0 && apiId < ePtlApiNotSupport) {
        NSMutableDictionary *userInfo = (NSMutableDictionary *)request.userInfo;
        [userInfo setValue:[NSString stringWithFormat:@"%d", apiId] forKey:(NSString*)s_KKProtocolReqApiId];
    }
	[request addRequestHeader:@"User-Agent" value:@"bcgogoRequest"];
	[request addRequestHeader:@"Accept-Language" value:self.language];
    
    request.useCookiePersistence = NO;
    if ([self.sessionCookie.value length] > 0)
        [request.requestCookies addObject:self.sessionCookie];
    [request addRequestHeader:@"Connection" value:@"keep-alive"];
    
	return [request autorelease];
}

- (void) issueRequest:(KKHTTPRequest*)request
{
	NSLog(@"request url=%@", request.url);
    
    if(request == nil)
        return;
	
	[request setDelegate:self];
    [request setValidatesSecureCertificate:NO];
	[_requestQueue addObject:request];
	
	NSTimeInterval timeoutSeconds = KKDefaultProtocolTimeoutSeconds;
	[request setTimeOutSeconds:timeoutSeconds];
	
	[request startAsynchronous];
	[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
}

- (NSInteger) requestID:(KKHTTPRequest*)request    // if not specify the request id, return <0;
{
	if (request.userInfo == nil)
		return -1;
	
	NSString *strReqId = (NSString*)[request.userInfo objectForKey:s_KKProtocolReqKey];
	if (strReqId == nil)
		return -1;
	
	return [strReqId integerValue];
}

- (NSInteger) requestApiID:(KKHTTPRequest*)request
{
    if (request.userInfo == nil)
        return ePtlApiNotSupport;
    
	NSString *strApiId = (NSString*)[request.userInfo objectForKey:s_KKProtocolReqApiId];
	if (strApiId == nil)
		return ePtlApiNotSupport;
	
	return [strApiId integerValue];
}

- (id) requestDelegate:(KKHTTPRequest *)request
{
	if (request.userInfo == nil)
		return nil;
	
	return [request.userInfo objectForKey:s_KKProtocolReqDelegateKey];
}

- (void) DispatchResponse:(KKHTTPRequest*)request withObject:(id)anObject action:(SEL)aSelctor
{
	NSInteger reqID = [self requestID:request];
	NSNumber *reqIdNumber = [NSNumber numberWithInteger:reqID];
	NSObject *delegateObject = (NSObject*)[self requestDelegate:request];
	
	// If specified a individual delegate, only handled by the delegate
	if (delegateObject) {
		if ([delegateObject conformsToProtocol:@protocol(KKProtocolEngineDelegate)] && [delegateObject respondsToSelector:aSelctor])
			[delegateObject performSelector:aSelctor withObject:reqIdNumber withObject:anObject];
		return;
	}
	
	// If not specified a individual delegate, then traverse delegates(queue) to handle 
	NSEnumerator *enumerator = [_delegates objectEnumerator];	
	while (delegateObject = (NSObject*)[enumerator nextObject]) {
		if ([delegateObject conformsToProtocol:@protocol(KKProtocolEngineDelegate)] && [delegateObject respondsToSelector:aSelctor]) {
			NSNumber *number = (NSNumber*)[delegateObject performSelector:aSelctor withObject:reqIdNumber withObject:anObject];
			NSInteger r = [number integerValue];
			if (r == eKKResultEnd) 
				break;
		}
	}
}

#pragma mark -
#pragma mark KKProtocolEngine-APIS
#pragma mark -
#pragma mark tonggou API-register&login
// Function:    用户注册
// Note:        PUT
// Params:      aUserNo(*)：             用户账号
//              aPassword(*)：           用户密码
//              aMobile(*)：             用户手机号
//              aName：                  用户名字
//              aVehicleNo：             用户车牌号
//              aVehicleModel：          用户车型
//              aVehicleModelId：        用户车型ID
//              aVehicleBrand：          用户车辆品牌信息
//              aVehicleBrandId：        用户车辆品牌ID
//              aNextMaintainMileage：   下次保养里程数
//              aNextInsuranceTime：     下次保险时间
//              aNextExamineTime：       下次验车时间
//              aCurrentMileage:         当前里程数
//              aShopId：                为用户安装注册APP的店面ID
//              aShopEmployee：          为用户安装注册APP的店面员工
//              loginInfo:  Object
//                          *platform：用户手机系统平台类型(ANDROID,IOS)
//                           platformVersion：用户手机系统平台版本
//                           mobileModel：用户手机型号
//                          *appVersion：APP版本号
//                          *imageVersion：例如格式：320X480

- (NSInteger)registerWithUserNo:(NSString *)aUserNo
                       password:(NSString *)aPassword
                         mobile:(NSString *)aMobile
                           name:(NSString *)aName
                      vehicleNo:(NSString *)aVehicleNo
                   vehicleModel:(NSString *)aVehicleModel
                 vehicleModelId:(NSString *)aVehicleModelId
                   vehicleBrand:(NSString *)aVehicleBrand
                 vehicleBrandId:(NSString *)aVehicleBrandId
            nextMaintainMileage:(NSInteger)aNextMaintainMileage
              nextInsuranceTime:(NSDate *)aNextInsuranceTime
                nextExamineTime:(NSDate *)aNextExamineTime
                 currentMileage:(NSString *)aCurrentMileage
                         shopId:(NSString *)aShopId
                   shopEmployee:(NSString *)aShopEmployee
                      loginInfo:(KKModelPlatform *)aLoginInfo
                       delegate:(id)aDelegate;
{
    NSString *url = [self getAccessUrl:ePtlApi_user_register];
    NSAssert(url, @"register url can't get");
    
    if ([aUserNo length] == 0 || [aPassword length] == 0|| [aMobile length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    [params setValue:aUserNo forKey:@"userNo"];
    [params setValue:aPassword forKey:@"password"];
    [params setValue:aMobile forKey:@"mobile"];
    [params setValue:aName forKey:@"name"];
    
    if ([aVehicleNo length] > 0)
        [params setValue:aVehicleNo forKey:@"vehicleNo"];
    if ([aVehicleModel length] > 0)
        [params setValue:aVehicleModel forKey:@"vehicleModel"];
    if ([aVehicleModelId length] > 0)
        [params setValue:aVehicleModelId forKey:@"vehicleModelId"];
    if ([aVehicleBrand length] > 0)
        [params setValue:aVehicleBrand forKey:@"vehicleBrand"];
    if ([aVehicleBrandId length] > 0)
        [params setValue:aVehicleBrandId forKey:@"vehicleBrandId"];
    
    if (aNextMaintainMileage > 0)
        [params setValue:[NSString stringWithFormat:@"%d", aNextMaintainMileage] forKey:@"nextMaintainMileage"];
    
    KKSetParamDate(params, @"nextInsuranceTime", aNextInsuranceTime, NO);
    KKSetParamDate(params, @"nextExamineTime", aNextExamineTime, NO);
    
    if ([aShopId length] > 0)
        [params setValue:aShopId forKey:@"shopId"];
    
    if ([aCurrentMileage length] > 0)
        [params setValue:aCurrentMileage forKey:@"currentMileage"];
    
    if ([aShopEmployee length] > 0)
        [params setValue:aShopEmployee forKey:@"shopEmployee"];
    if (aLoginInfo) {
        NSMutableDictionary *logDict = [NSMutableDictionary dictionaryWithCapacity:10];
        if ([aLoginInfo.platform length] > 0)
            [logDict setObject:aLoginInfo.platform forKey:@"platform"];
        if ([aLoginInfo.platformVersion length] > 0)
            [logDict setObject:aLoginInfo.platformVersion forKey:@"platformVersion"];
        if ([aLoginInfo.mobileModel length] > 0)
            [logDict setObject:aLoginInfo.mobileModel forKey:@"mobileModel"];
        if ([aLoginInfo.appVersion length] > 0)
            [logDict setObject:aLoginInfo.appVersion forKey:@"appVersion"];
        if ([aLoginInfo.imageVersion length] > 0)
            [logDict setObject:aLoginInfo.imageVersion forKey:@"imageVersion"];
        [params setValue:logDict forKey:@"loginInfo"];
    }
    
    self.userName = aUserNo;
    self.password = aPassword;
    
	KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_user_register delegate:aDelegate];
    [KKProtocolEngine makePutData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}

-(NSInteger) registerShopBind:(NSString *)aShopId vehicleId:(NSString *)aVehicleId delegate:(id)aDelegate
{
    
    NSString *url = [self getAccessUrl:ePtlApi_Register_shopBinding];
    NSAssert(url, @"get singleVehicle info url can't get");
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    KKSetSequenceParamString(seqKeys, params, @"shopId", aShopId);
    if(!(aVehicleId == nil || [aVehicleId isEqualToString:@""]))
        KKSetSequenceParamString(seqKeys, params, @"vehicleId", aVehicleId);
    
    KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_Register_shopBinding delegate:aDelegate];
    [KKProtocolEngine makePostData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}


// Function:    登陆
// Note:        POST
// Params:      *userNo：用户账号
//              *password：用户密码
//              *platform：用户手机系统平台类型
//              platformVersion：用户手机系统平台版本
//              mobileModel：用户手机型号
//              *appVersion：APP版本号
//              imageVersion：图片版本，APP需要根据手机硬件分辨率等信息告知后台需要的图片版本
//              手机分辨率与图片版本枚举值的映射关系如下：
//              320 X 480：[109X109][63X63]
//              480 X 800：[164X164][94X94]
//              720 X 1280：[245X245][141X141]
//              640 X 960：[218X218][125X125]
//              640 X 1136：[218X218][125X125]
//
- (NSInteger)userLoginWithUser:(NSString *)aUserNo password:(NSString *)aPassword platform:(NSString *)aPlatform platformVersion:(NSString *)aPlatformVer mobileModel:(NSString *)aMobileModel appVersion:(NSString *)aVersion imageVersion:(NSString *)aImgVer delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_user_login];
    //NSAssert(url, @"login url can't get");
    
    if ([aUserNo length] == 0 || [aPassword length] == 0 || [aPlatform length] == 0 || [aVersion length] == 0)
        return KKInvalidRequestID;
    
    self.userName = aUserNo;
    self.password = aPassword;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    [params setValue:aUserNo forKey:@"userNo"];
    [params setValue:aPassword forKey:@"password"];
    [params setValue:aPlatform forKey:@"platform"];
    if ([aPlatformVer length] > 0)
        [params setValue:aPlatformVer forKey:@"platformVersion"];
    if ([aMobileModel length] > 0)
        [params setValue:aMobileModel forKey:@"mobileModel"];
    [params setValue:aVersion forKey:@"appVersion"];
    if ([aImgVer length] > 0)
        [params setValue:aImgVer forKey:@"imageVersion"];
    
    KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_user_login delegate:aDelegate];
    //[KKProtocolEngine makePutData:params request:request];
    //[request setRequestMethod:@"POST"];
    [KKProtocolEngine makePostData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    绑定OBD
// Note:        POST
// Params:      (vehicleId与vehicleVin不能同时为空)
//              *userNo：用户账号 String
//              *obdSN：obd硬件唯一标识号 String
//              *vehicleVin：车辆唯一标识号 String （根据此字段判断是新增还是更新车辆信息）
//              vehicleId:后台数据主键（vehicleId为空表示新增）
//              *vehicleNo：车牌号 String
//              *vehicleModel：车型 String
//              vehicleModelId：车型ID Long
//              *vehicleBrand：车辆品牌 String
//              vehicleBrandId：车辆品牌ID Long
//              sellShopId:销售OBD的店铺
//              nextMaintainMileage：下次保养里程数
//              nextInsuranceTime：下次保险时间
//              nextExamineTime：下次验车时间
//              currentMileage：当前里程数

- (NSInteger)obdBinding:(NSString *)aUserNo
                  obdSN:(NSString *)aObdSN
             vehicleVin:(NSString *)aVehicleVin
              vehicleId:(NSString *)aVehicleId
              vehicleNo:(NSString *)aVehicleNo
           vehicleModel:(NSString *)aVehicleModel
         vehicleModelId:(NSString *)aVehicleModelId
           vehicleBrand:(NSString *)aVehicleBrand
         vehicleBrandId:(NSString *)aVehicleBrandId
             sellShopId:(NSString *)aSellShopId
               engineNo:(NSString *)aEngineNo
               registNo:(NSString *)aRegistNo
    nextMaintainMileage:(NSString *)aNextMaintainMileage
      nextInsuranceTime:(NSDate *)aNextInsuranceTime
        nextExamineTime:(NSDate *)aNextExamineTime
         currentMileage:(NSString *)aCurrentMileage
               delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_obd_bind];
    NSAssert(url, @"obd bind url can't get");
    
    if ([aUserNo length] == 0)
        aUserNo = self.userName;
    if ([aUserNo length] == 0 || [aObdSN length] == 0 || [aVehicleModel length] == 0 || [aVehicleBrand length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    [params setValue:aUserNo forKey:@"userNo"];
    [params setValue:aObdSN  forKey:@"obdSN"];
    KKSetParamString(params, @"vehicleVin", aVehicleVin, NO);
    KKSetParamString(params, @"vehicleId", aVehicleId, NO);
    KKSetParamString(params, @"vehicleNo", aVehicleNo, NO);
    KKSetParamString(params, @"vehicleModel", aVehicleModel, NO);
    KKSetParamString(params, @"vehicleModelId", aVehicleModelId, NO);
    KKSetParamString(params, @"vehicleBrand", aVehicleBrand, NO);
    KKSetParamString(params, @"vehicleBrandId", aVehicleBrandId, NO);
    KKSetParamString(params, @"sellShopId", aSellShopId, NO);
    KKSetParamString(params, @"engineNo", aEngineNo, NO);
    KKSetParamString(params, @"registNo", aRegistNo, NO);
    KKSetParamString(params, @"nextMaintainMileage", aNextMaintainMileage, NO);
    KKSetParamDate(params, @"nextInsuranceTime", aNextInsuranceTime, NO);
    KKSetParamDate(params, @"nextExamineTime", aNextExamineTime, NO);
    KKSetParamString(params, @"currentMileage", aCurrentMileage, NO);
    
    KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_obd_bind delegate:aDelegate];
    [KKProtocolEngine makePostData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    找回密码
// Note:        GET
// Params:
//              *userNo：用户账号 String
- (NSInteger)userPassword:(NSString *)aUserNo delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_user_password];
    NSAssert(url, @"user password url can't get");
    
    if ([aUserNo length] == 0)
        aUserNo = self.userName;
    if ([aUserNo length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    [params setValue:aUserNo forKey:@"userNo"];
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params]];
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_user_password delegate:aDelegate];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    升级检测
// Note:        GET
// Params:
//              *platform：用户手机系统平台类型(ANDROID,IOS)
//              platformversion：用户手机系统平台版本
//              mobileModel：用户手机型号
//              *appVersion：APP版本号
- (NSInteger)newVersion:(NSString *)aPlatform appVersion:(NSString *)aAppVersion platformVersion:(NSString *)aPlatformVersion mobileModel:(NSString *)aMobileModel delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_new_version];
    NSAssert(url, @"new version url can't get");
    
    if ([aPlatform length] == 0 || [aAppVersion length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    KKSetSequenceParamString(seqKeys, params, @"platform", aPlatform);
    KKSetSequenceParamString(seqKeys, params, @"appVersion", aAppVersion);
    KKSetSequenceParamString(seqKeys, params, @"platformVersion", aPlatformVersion);
    KKSetSequenceParamString(seqKeys, params, @"mobileModel", aMobileModel);
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys]];
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_new_version delegate:aDelegate];
	[self issueRequest:request];
    
	return [self requestID:request];
    
}

// Function:    获取车辆列表
// Note:        GET
// Params:
//              *userNo：用户账号 String

- (NSInteger)vehicleListInfo:(NSString *)aUserNo delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_vehicle_list];
    NSAssert(url, @"vehicle list url can't get");
    
    if ([aUserNo length] == 0)
        aUserNo = self.userName;
    if ([aUserNo length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    KKSetSequenceParamString(seqKeys, params, @"userNo", aUserNo);
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys]];
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_vehicle_list delegate:aDelegate];
	[self issueRequest:request];
    
	return [self requestID:request];
}


// Function:    保存车辆信息
// Note:        PUT
// Params:
//              vehicleId：后台数据主键(vehicleId为空新增、不为空更新)
//              vehicleVin：车辆唯一标识号
//              *vehicleNo：车牌号
//              *vehicleModel：车型
//              vehicleModelId：车型ID
//              *vehicleBrand：车辆品牌
//              vehicleBrandId：车辆品牌ID
//              obdSN：当前车辆所安装的obd的唯一标识号
//              *userNo：用户账号
//              nextMaintainMileage：下次保养里程数
//              nextInsuranceTime：下次保险时间
//              nextExamineTime：下次验车时间
//              currentMileage：当前里程数

- (NSInteger)vehicleSaveInfo:(NSString *)aVehicleId vehicleVin:(NSString *)aVehicleVin vehicleNo:(NSString *)aVehicleNo vehicleModel:(NSString *)aVehicleModel vehicleModelId:(NSString *)aVehicleModelId vehicleBrand:(NSString *)aVehicleBrand vehicleBrandId:(NSString *)aVehicleBrandId obdSN:(NSString *)aObdSN bindingShopId:(NSString *)aShopId userNo:(NSString *)aUserNo engineNo:(NSString *)aEngineNo registNo:(NSString *)aRegistNo nextMaintainMileage:(NSString *)aNextMaintainMileage nextInsuranceTime:(NSDate *)aNextInsuranceTime nextExamineTime:(NSDate *)aNextExamineTime currentMileage:(NSString *)aCurrentMileage delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_vehicle_saveInfo];
    NSAssert(url, @"vehicle save info url can't get");
    
    if ([aUserNo length] == 0)
        aUserNo = self.userName;
    if ([aUserNo length] == 0 || [aVehicleNo length] == 0 || [aVehicleModel length] == 0 || [aVehicleBrand length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    KKSetParamString(params, @"vehicleId", aVehicleId, NO);
    KKSetParamString(params, @"vehicleVin", aVehicleVin, NO);
    KKSetParamString(params, @"vehicleNo", aVehicleNo, NO);
    KKSetParamString(params, @"vehicleModel", aVehicleModel, NO);
    KKSetParamString(params, @"vehicleModelId", aVehicleModelId, NO);
    KKSetParamString(params, @"vehicleBrand", aVehicleBrand, NO);
    KKSetParamString(params, @"vehicleBrandId", aVehicleBrandId, NO);
    KKSetParamString(params, @"obdSN", aObdSN, NO);
    KKSetParamString(params, @"bindingShopId", aShopId, NO);
    KKSetParamString(params, @"userNo", aUserNo, NO);
    KKSetParamString(params, @"engineNo", aEngineNo, NO);
    KKSetParamString(params, @"registNo", aRegistNo, NO);
    KKSetParamString(params, @"nextMaintainMileage", aNextMaintainMileage, NO);
    KKSetParamDate(params, @"nextInsuranceTime", aNextInsuranceTime, NO);
    KKSetParamDate(params, @"nextExamineTime", aNextExamineTime, NO);
    KKSetParamString(params, @"currentMileage", aCurrentMileage, NO);
    
	KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_vehicle_saveInfo delegate:aDelegate];
    [KKProtocolEngine makePutData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}

-(NSInteger) vehicleSaveList:(NSArray *)aVehicleList delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_vehicle_saveList];
    
    NSMutableArray *dicArray = [[NSMutableArray alloc] init];
    for(KKModelVehicleDetailInfo *info in aVehicleList)
    {
        NSMutableDictionary *dic =(NSMutableDictionary *)[info dictionaryRepresentation];
        [dic removeObjectForKey:@"localId"];
        [dic removeObjectForKey:@"class"];
        [dic removeObjectForKey:@"isDefault"];
        [dicArray addObject:dic];
    }
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithObject:dicArray forKey:@"vehicles"];
    [dicArray release];
	KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_vehicle_saveList delegate:aDelegate];
    [KKProtocolEngine makePutData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    获取车辆信息
// Note:        GET
// Params:
//              *vehicleId：后台数据主键
- (NSInteger)vehicleGetInfo:(NSString *)aVehicleId delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_vehicle_getInfo];
    NSAssert(url, @"vehicle get info url can't get");
    
    if ([aVehicleId length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    KKSetParamString(params, @"vehicleId", aVehicleId, YES);
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params]];
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_vehicle_getInfo delegate:aDelegate];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    删除车辆
// URL:         https://shop.bcgogo.com/api/vehicle/singlevehicle/vehicleId/{vehicleId}
// Note:        DELETE
// Params:      vehicleId：后台数据主键
- (NSInteger)vehicleDeleteWithId:(NSString *)aVehicleId delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_vehicle_delete];
    NSAssert(url, @"vehicle delete url can't get");
    
    if ([aVehicleId length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    KKSetParamString(params, @"vehicleId", aVehicleId, YES);
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params]];
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_vehicle_delete delegate:aDelegate];
    [request setRequestMethod:@"DELETE"];
    [self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    根据关键字获取车辆品牌和车型
// URL:         https://shop.bcgogo.com/api/vehicle/brandModel/keywords/{keywords}/type/{type}/brandId/{brandId}
// Note:        GET
// Params:
//              keywords：车型或者车辆品牌关键字
//              *type：车辆品牌或者车型  （brand|model）
//              brandid：车辆品牌ID，当type值为"model"时生效
- (NSInteger)vehicleBrandModel:(NSString *)aKeywords type:(NSString *)aType brandId:(NSString *)aBrandId delegate:(id)aDelegate;
{
    NSString *url = [self getAccessUrl:ePtlApi_vehicle_getModelByKey];
    NSAssert(url, @"vehicle get model url can't get");
    
    if ([aType length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    
    KKSetSequenceParamString(seqKeys, params, @"keywords", aKeywords);
    KKSetSequenceParamString(seqKeys, params, @"type", aType);
    KKSetSequenceParamString(seqKeys, params, @"brandId", aBrandId);
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys]];
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_vehicle_getModelByKey delegate:aDelegate];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    发送车辆故障信息
// URL:         https://shop.bcgogo.com/api/vehicle/fault
//              data:faultCode={faultCode}&userNo={userNo}&vehicleVin={vehicleVin}&obdSN={obdSN}&reportTime={reportTime}
// Note:        POST
// Params:      *vehicleId:后台数据主键
//              *faultCode：故障码 如果有多个故障码请以逗号 ,分开；  一起可以接收多个故障码
//              *userNo：用户账号
//              vehicleVin：车辆唯一标识号
//              *obdSN：obd唯一标识号
//              *reportTime：故障时间（Long型unixtime）
- (NSInteger)vehicleFault:(NSString *)aFaultCode userNo:(NSString *)aUserNo vehicleVin:(NSString *)aVehicleVin obdSN:(NSString *)aObdSN reportTime:(NSDate *)aReportTime vehicleId:(NSString *)aVehicleId delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_vehicle_fault];
    NSAssert(url, @"vehicle fault url can't get");
    
    if ([aUserNo length] == 0)
        aUserNo = self.userName;
    if ([aUserNo length] == 0 || [aObdSN length] == 0 || nil == aReportTime)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    KKSetParamString(params, @"faultCode", aFaultCode, NO);
    KKSetParamString(params, @"userNo", aUserNo, NO);
    KKSetParamString(params, @"vehicleVin", aVehicleVin, NO);
    KKSetParamString(params, @"obdSN", aObdSN, NO);
    KKSetParamDate(params, @"reportTime", aReportTime, NO);
    KKSetParamString(params, @"vehicleId", aVehicleId, NO);
    KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_vehicle_fault delegate:aDelegate];
    [KKProtocolEngine makePostData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}

-(NSInteger)vehicleFaultCodeList:(NSString *)aStatus pageNo:(NSInteger)aPageNo pageSize:(NSInteger)aPageSize delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_vehicle_faultCodeList];
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    
    KKSetSequenceParamInt(seqKeys, params, @"pageNo", aPageNo);
    KKSetSequenceParamInt(seqKeys, params, @"pageSize", aPageSize);
    KKSetSequenceParamString(seqKeys, params, @"status", aStatus);
    
    KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_vehicle_faultCodeList delegate:aDelegate];
    [KKProtocolEngine makePostData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}

-(NSInteger) vehicleFaultCodeOperate:(long long)aID errorCode:(NSString *)aFaultCode oldStatus:(NSString *)aOldStatus newStatus:(NSString *)aNewStatus vehicleId:(NSString *)aAppVehicleId delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_vehicle_faultCodeOperate];
    NSAssert(url, @"vehicle fault url can't get");
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    if (aID != 0)
    {
        NSString *idStr = [NSString stringWithFormat:@"%lld",aID];
        KKSetParamString(params, @"appVehicleFaultInfoDTOs[0].id", idStr, NO);
    }
    KKSetParamString(params, @"appVehicleFaultInfoDTOs[0].errorCode", aFaultCode, NO);
    KKSetParamString(params, @"appVehicleFaultInfoDTOs[0].lastStatus", aOldStatus, NO);
    KKSetParamString(params, @"appVehicleFaultInfoDTOs[0].status", aNewStatus, NO);
    KKSetParamString(params, @"appVehicleFaultInfoDTOs[0].appVehicleId", aAppVehicleId, NO);

    KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_vehicle_faultCodeOperate delegate:aDelegate];
    [KKProtocolEngine makePostData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];

}

// Function:    故障字典信息更新
// URL:         https://shop.bcgogo.com/api/vehicle/faultDic/dicVersion/{dicVersion}/vehicleModelId/{vehicleModelId}
// Note:        GET
// Params:      *vehicleModelId：车型ID ( 如果车型Id为空则取通用字典)
//              dicVersion：字典版本 (如果版本号为空则取最新版）
- (NSInteger)vehicleFaultDict:(NSString *)aVehicleModelId dictVersion:(NSString *)aDictVersion delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_vehicle_faultDic];
    NSAssert(url, @"vehicle faultDict url can't get");
        
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    
    KKSetSequenceParamString(seqKeys, params, @"dicVersion", aDictVersion);
    KKSetSequenceParamString(seqKeys, params, @"vehicleModelId", aVehicleModelId);
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys]];
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_vehicle_faultDic delegate:aDelegate];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    发送车况信息
// URL:         https://shop.bcgogo.com/api/vehicle/condition
// Note:        PUT
// Params:        *vehicleId
//                vehicleVin：车辆唯一标识号 String
//                *userNo：用户账号String
//                *obdSN：obd唯一标识号 String
//                oilWear：油耗 Double
//                currentMileage：当前里程 int
//                instantOilWear：瞬时油耗 单位 ml/s Double 单位不需要传
//                oilWearPerHundred：Double 百公里油耗 L/100km 单位不需要传
//                oilMass：Double 油量 百分比%  %需要传
//                engineCoolantTemperature： Double 发动机冷却液（发动机水温）   单位 ℃ 单位不需要传
//                batteryVoltage：Double 电瓶电压 单位 V 单位不需要传
//                reportTime：报告时间 Long
- (NSInteger)vehicleCondition:(NSString *)aVehicleVin
                        obdSN:(NSString *)aObdSN
                      oilWear:(CGFloat)aOilWear
               currentMileage:(NSInteger)aCurrentMileage
               instantOilWear:(CGFloat)aInstantOilWear
            oilWearPerHundred:(CGFloat)aOilWearPerHundred
                      oilMass:(CGFloat)aOilMass
     engineCoolantTemperature:(CGFloat)aTemperature
               batteryVoltage:(CGFloat)aVoltage
                   reportTime:(NSDate*)aReportTime
                    vehicleId:(NSString *)aVehicleId
                     delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_vehicle_condition];
    NSAssert(url, @"vehicle condition url can't get");
    
    if ([self.userName length] == 0 || [aObdSN length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    KKSetParamString(params, @"vehicleId", aVehicleId, NO);
    KKSetParamString(params, @"vehicleVin", aVehicleVin, NO);
    KKSetParamString(params, @"userNo", self.userName, NO);
    KKSetParamString(params, @"obdSN", aObdSN, NO);
    if (aOilWear != KKOBDDataNA)
        KKSetParamFloat(params, @"oilWear", aOilWear, NO);
    if (aCurrentMileage != KKOBDDataNA)
        KKSetParamInt(params, @"currentMileage", aCurrentMileage, NO);
    if (aInstantOilWear != KKOBDDataNA)
        KKSetParamFloat(params, @"instantOilWear", aInstantOilWear, NO);
    if (aOilWearPerHundred != KKOBDDataNA)
        KKSetParamFloat(params, @"oilWearPerHundred", aOilWearPerHundred, NO);
    if (aOilMass != KKOBDDataNA)
        KKSetParamFloat(params, @"oilMass", aOilMass, NO);
    if (aTemperature != KKOBDDataNA)
        KKSetParamFloat(params, @"engineCoolantTemperature", aTemperature, NO);
    if (aVoltage != KKOBDDataNA)
        KKSetParamFloat(params, @"batteryVoltage", aVoltage, NO);
    KKSetParamDate(params, @"reportTime", aReportTime, NO);
    
	KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_vehicle_condition delegate:aDelegate];
    [KKProtocolEngine makePutData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}

//- (NSInteger)vehicleCondition:(NSString *)aVehicleVin obdSN:(NSString *)aObdSN oilWear:(NSString *)aOilWear currentMileage:(NSString *)aCurrentMileage instantOilWear:(NSString *)aInstantOilWear oilWearPerHundred:(NSString *)aOilWearPerHundred oilMass:(NSString *)aOilMass engineCoolantTemperature:(NSString *)aTemperature batteryVoltage:(NSString *)aVoltage reportTime:(NSDate*)aReportTime delegate:(id)aDelegate;
//{
//    NSString *url = [self getAccessUrl:ePtlApi_vehicle_condition];
//    NSAssert(url, @"vehicle condition url can't get");
//    
//    if ([self.userName length] == 0 || [aVehicleVin length] == 0 || [aObdSN length] == 0)
//        return KKInvalidRequestID;
//    
//    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
//    KKSetParamString(params, @"vehicleVin", aVehicleVin, NO);
//    KKSetParamString(params, @"userNo", self.userName, NO);
//    KKSetParamString(params, @"obdSN", aObdSN, NO);
//    KKSetParamString(params, @"oilWear", aOilWear, NO);
//    KKSetParamString(params, @"currentMileage", aCurrentMileage, NO);
//    KKSetParamString(params, @"instantOilWear", aInstantOilWear, NO);
//    KKSetParamString(params, @"oilWearPerHundred", aOilWearPerHundred, NO);
//    KKSetParamString(params, @"oilMass", aOilMass, NO);
//    KKSetParamString(params, @"engineCoolantTemperature", aTemperature, NO);
//    KKSetParamString(params, @"batteryVoltage", aVoltage, NO);
//    KKSetParamDate(params, @"reportTime", aReportTime, NO);
//    
//	KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_vehicle_condition delegate:aDelegate];
//    [KKProtocolEngine makePutData:params request:request];
//	[self issueRequest:request];
//    
//	return [self requestID:request];
//}
//


// Function:    查询推荐店铺
// Note:        GET
// URL:         https://shop.bcgogo.com/api/shop/searchList/coordinate/{coordinate}/serviceScopeIds/{serviceScopeIds}/sortType/{sortType}/areaId/{areaId}/cityCode/{cityCode}/shopType/{shopType}/keywords/{keywords}/pageNo/{pageNo}/pageSize/{pageSize}/userNo/{userNo}
// Params:
//                *coordinate：地理坐标（经纬度）格式：lon,lat
//                *serviceScopeIds：服务id（逗号分隔）
//                *coordinateType :CURRENT,LAST （当前、上次）
//                *areaId: 城市Id
//                *cityCode：百度地图城市编码
//                shopType：店铺类型，是否是4s店(ALL,SHOP_4S)
//                sortType：排序规则 (DISTANCE,EVALUATION)   按距离、按评价
//                keywords：用户填写的店铺名称关键字
//                pageNo：当前分页
//                pageSize：分页大小
//                userNo：用户账号

- (NSInteger)shopSearchList:(NSString *)aCoordinate
            serviceScopeIds:(NSString *)aServiceScopeIds
             coordinateType:(NSString *)aCoordinateType
                   sortType:(NSString *)aSortType
                     areaId:(NSString *)aAreaId
                   cityCode:(NSString *)aCityCode
                   shopType:(NSString *)aShopType
                   keywords:(NSString *)aKeywords
                     isMore:(BOOL)aIsMore
                     pageNo:(NSInteger)aPageNo
                   pageSize:(NSInteger)aPageSize delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_shop_searchList];
    NSAssert(url, @"shop searchList url can't get");
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    
    switch ([KKAuthorization sharedInstance].authorizationType) {
        case Authorization_Visitor:
        {
            KKSetSequenceParamString(seqKeys, params, @"dataKind", KK_DataKind);
            KKSetSequenceParamString(seqKeys, params, @"imageVersion", self.imageVersion);
            KKSetSequenceParamString(seqKeys, params, @"coordinateType", aCoordinateType);
            KKSetSequenceParamString(seqKeys, params, @"coordinate", aCoordinate);
            KKSetSequenceParamString(seqKeys, params, @"areaId", aAreaId);
            KKSetSequenceParamString(seqKeys, params, @"serviceScopeIds", aServiceScopeIds);
            KKSetSequenceParamString(seqKeys, params, @"sortType", aSortType);
//            KKSetSequenceParamString(seqKeys, params, @"cityCode", aCityCode);
            KKSetSequenceParamString(seqKeys, params, @"shopType", aShopType);
            KKSetSequenceParamString(seqKeys, params, @"keywords", aKeywords);
            KKSetSequenceParamInt(seqKeys, params, @"pageNo", aPageNo);
            KKSetSequenceParamInt(seqKeys, params, @"pageSize", aPageSize);
        }
            break;
        case Authorization_Register:
        {
            KKSetSequenceParamString(seqKeys, params, @"coordinateType", aCoordinateType);
            KKSetSequenceParamString(seqKeys, params, @"coordinate", aCoordinate);
            KKSetSequenceParamString(seqKeys, params, @"serviceScopeIds", aServiceScopeIds);
            KKSetSequenceParamString(seqKeys, params, @"sortType", aSortType);
            KKSetSequenceParamString(seqKeys, params, @"areaId", aAreaId);
            KKSetSequenceParamString(seqKeys, params, @"cityCode", aCityCode);
            KKSetSequenceParamString(seqKeys, params, @"shopType", aShopType);
            KKSetSequenceParamString(seqKeys, params, @"keywords", aKeywords);
            KKSetSequenceParamString(seqKeys, params, @"isMore", (aIsMore ? @"true" : nil));
            KKSetSequenceParamInt(seqKeys, params, @"pageNo", aPageNo);
            KKSetSequenceParamInt(seqKeys, params, @"pageSize", aPageSize);
        }
            break;
    }
    

    //NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys]];
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys containKeys:NO]];
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_shop_searchList delegate:aDelegate];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    获取地区列表
// Note:        GET
// URL:         https://shop.bcgogo.com/api/area/list/{type}/{provinceId}
// Params:
//                provinceId：省份(或城市)ID
//                type：类型 "PROVINCE/CITY"
- (NSInteger)areaList:(NSString *)aType provinceId:(NSString *)aProvinceId delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_area_list];
    NSAssert(url, @"area list url can't get");
    
    if ([aType length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    
    KKSetSequenceParamString(seqKeys, params, aType, aProvinceId);
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys]];
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_area_list delegate:aDelegate];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    根据关键字获取店铺建议列表
// URL:         https://shop.bcgogo.com/api/shop/suggestions/keywords/keywords/{keywords}/cityCode/{cityCode}/areaId/{areaId}
// Note:        GET
// Params:
//              keywords：店铺名称关键字
//              *cityCode：地图数据中的城市编号
- (NSInteger)shopSuggestionsByKey:(NSString *)aKeyword cityCode:(NSString *)aCityCode areaId:(NSString *)aAreaId serviceScopeIds:(NSString *)aServiceScopeIds delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_shop_suggestionsByKey];
    NSAssert(url, @"shop suggestion by keyword url can't get");
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    
    KKSetSequenceParamString(seqKeys, params, @"keywords", aKeyword);
    KKSetSequenceParamString(seqKeys, params, @"cityCode", aCityCode);
    KKSetSequenceParamString(seqKeys, params, @"areaId", aAreaId);
    KKSetSequenceParamString(seqKeys, params, @"serviceScopeIds", aServiceScopeIds);
    
    switch ([KKAuthorization sharedInstance].authorizationType) {
        case Authorization_Visitor:
        {
            KKSetSequenceParamString(seqKeys, params, @"dataKind", KK_DataKind);
        }
            break;
        default:
            break;
    }
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys containKeys:NO]];
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_shop_suggestionsByKey delegate:aDelegate];
	[self issueRequest:request];
    
	return [self requestID:request];
}


// Function:    根据店铺ID获取店铺详情
// URL:         https://shop.bcgogo.com/api/shop/detail/{shopId}/userNo/{userNo}
// Note:        GET
// Params:
//              *shopId：店铺ID
//              *userNo：用户账号
- (NSInteger)shopDetailWithId:(NSString *)aShopId delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_shop_detail];
    NSAssert(url, @"shop detail url can't get");
    
    if ([aShopId length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    
    NSString *reqUrl = nil;
    switch ([KKAuthorization sharedInstance].authorizationType) {
        case Authorization_Visitor:
        {
            KKSetSequenceParamString(seqKeys, params, @"imageVersion", self.imageVersion);
            KKSetSequenceParamString(seqKeys, params, @"shopId", aShopId);
            reqUrl = [NSString stringWithFormat:@"%@%@",url,[KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys containKeys:NO]];
        }
            break;
        case Authorization_Register:
        {
            KKSetSequenceParamString(seqKeys, params, @"", aShopId);
            KKSetSequenceParamString(seqKeys, params, @"userNo", self.userName);
            reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys]];
        }
            break;
    }
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_shop_detail delegate:aDelegate];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    消息轮询
// URL:         https://shop.bcgogo.com/api/message/polling/type/{type1,type2...}/userNo/{userNo}
// Note:        GET
// Params:      type：消息类型（多个），用逗号隔开
//              店铺预约修改消息      SHOP_CHANGE_APPOINT,
//              店铺预约结束消息      SHOP_FINISH_APPOINT,
//              店铺接受预约单        SHOP_ACCEPT_APPOINT,
//              店铺预约拒绝消息      SHOP_REJECT_APPOINT,
//              店铺预约取消消息      SHOP_CANCEL_APPOINT,
//              APP过期预约单        OVERDUE_APPOINT_TO_APP,
//              保养里程             APP_VEHICLE_MAINTAIN_MILEAGE,
//              保养时间             APP_VEHICLE_MAINTAIN_TIME,
//              保险时间             APP_VEHICLE_INSURANCE_TIME,
//              验车时间             APP_VEHICLE_EXAMINE_TIME
//              userNo：用户账号
- (NSInteger)messagePollingWithType:(NSString *)aTypes delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_message_polling];
    NSAssert(url, @"message polling url can't get");
        
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    
    KKSetSequenceParamString(seqKeys, params, @"types", aTypes);
    KKSetSequenceParamString(seqKeys, params, @"userNo", self.userName);
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys]];
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_message_polling delegate:aDelegate];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    预约服务
// URL:         https://shop.bcgogo.com/api/service/appointment
// Note:        Request Method:PUT
// Params:
//                *shopId:要预约的店铺id
//                *serviceCategoryId：服务类型id Long：接口30获取的服务范围id
//                *appointTime：预约时间 Long
//                *mobile：手机号 String
//                *vehicleNo：车牌号 String
//                vehicleBrand：车辆品牌 String
//                vehicleBrandId：车辆品牌ID Long
//                vehicleModel：车型 String
//                vehicleModelId：车型ID Long
//                *userNo：用户账号String
//                vehicleVin：车辆唯一 标识号 String
//                remark：备注 String
//                *contact：联系人 String

- (NSInteger)serviceAppointmentWithShopId:(NSString *)aShopId
                        serviceCategoryId:(NSString *)aServiceCategoryId
                              appointTime:(NSDate *)aAppointTime
                                   mobile:(NSString *)aMobile
                                vehicleNo:(NSString *)aVehicleNo
                             vehicleBrand:(NSString *)aVehicleBrand
                           vehicleBrandId:(NSString *)aVehicleBrandId
                             vehicleModel:(NSString *)aVehicleModel
                           vehicleModelId:(NSString *)aVehicleModelId
                               vehicleVin:(NSString *)aVehicleVin
                                   remark:(NSString *)aRemark
                                  contact:(NSString *)aContact
                           faultInfoItems:(NSArray *)aDTCMsgArray
                                 delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_service_appointment];
    NSAssert(url, @"service appoint url can't get");
    
    if ([aShopId length] == 0 || [aServiceCategoryId length] ==0 || aAppointTime == nil || [aMobile length] == 0 ||  [aVehicleNo length] == 0 || [aContact length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
   
    KKSetParamString(params, @"shopId", aShopId, NO);
    KKSetParamString(params, @"serviceCategoryId", aServiceCategoryId, NO);
    KKSetParamDate(params, @"appointTime", aAppointTime, NO);
    KKSetParamString(params, @"mobile", aMobile, NO);
    KKSetParamString(params, @"vehicleNo", aVehicleNo, NO);
    KKSetParamString(params, @"vehicleBrand", aVehicleBrand, NO);
    KKSetParamString(params, @"vehicleBrandId", aVehicleBrandId, NO);
    KKSetParamString(params, @"vehicleModel", aVehicleModel, NO);
    KKSetParamString(params, @"vehicleModelId", aVehicleModelId, NO);
    KKSetParamString(params, @"userNo", self.userName, NO);
    KKSetParamString(params, @"vehicleVin", aVehicleVin, NO);
    KKSetParamString(params, @"remark", aRemark, NO);
    KKSetParamString(params, @"contact", aContact, NO);
    
//    KKSetParamString(params, @"faultInfoItems", aDTCMsgArray, NO);
    if(aDTCMsgArray != nil)
        [params setObject:aDTCMsgArray forKey:@"faultInfoItems"];
    
	KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_service_appointment delegate:aDelegate];
    [KKProtocolEngine makePutData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    服务历史查询
// URL:         https://shop.bcgogo.com/api/service/historyList/type/{type1,type2...}/status/{status1,status2...}/userNo/{userNo}/pageNo/{pageNo}/pageSize/{pageSize}
// Note:        GET
// Params:      serviceScope：服务范围（多个，用逗号分隔） ServiceScope: OVERHAUL_AND_MAINTENANCE、DECORATION_BEAUTY、PAINTING、INSURANCE、WASH
//              status：状态（多个）（已完成：finished|未完成：unfinished） String
//              *userNo：用户账号 String
//              *pageNo：当前页 String
//              *pageSize：分页大小 String
//- (NSInteger)serviceHistoryListWithServiceScope:(NSString *)aServiceScope status:(NSString *)aStatus pageNo:(NSInteger)aPageNo pageSize:(NSInteger)aPageSize delegate:(id)aDelegate
//{
//    NSString *url = [self getAccessUrl:ePtlApi_service_historyList];
//    NSAssert(url, @"service historyList url can't get");
//    
//    if ([self.userName length] == 0)
//        return KKInvalidRequestID;
//    
//    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
//    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
//    
//    KKSetSequenceParamString(seqKeys, params, @"serviceScope", aServiceScope);
//    KKSetSequenceParamString(seqKeys, params, @"status", aStatus);
//    KKSetSequenceParamString(seqKeys, params, @"userNo", self.userName);
//    KKSetSequenceParamInt(seqKeys, params, @"pageNo", aPageNo);
//    KKSetSequenceParamInt(seqKeys, params, @"pageSize", aPageSize);
//    
//    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys]];
//    
//    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_service_historyList delegate:aDelegate];
//	[self issueRequest:request];
//    
//	return [self requestID:request];
//}
-(NSInteger)serviceAllHistoryList:(NSInteger)aPageNo pageSize:(NSInteger)aPageSize delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_service_historyList];
    NSAssert(url, @"service historyList url can't get");
    
    if ([self.userName length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    
    KKSetSequenceParamInt(seqKeys, params, @"pageNo", aPageNo);
    KKSetSequenceParamInt(seqKeys, params, @"pageSize", aPageSize);
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys]];
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_service_historyList delegate:aDelegate];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    服务历史详情
// URL:         https://shop.bcgogo.com/api/service/historyDetail/orderId/{orderId}/type/{type}
// Note:        GET
// Params:      serviceScope：服务范围 ServiceScope: OVERHAUL_AND_MAINTENANCE、DECORATION_BEAUTY、PAINTING、INSURANCE、WASH
//              *orderId：单据ID Long
- (NSInteger)serviceHistoryDetail:(NSString *)aOrderId serviceScope:(NSString *)aServiceScope delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_service_historyDetail];
    NSAssert(url, @"service historyDetail url can't get");
    
    if ([aOrderId length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    
    KKSetSequenceParamString(seqKeys, params, @"orderId", aOrderId);
    KKSetSequenceParamString(seqKeys, params, @"serviceScope", aServiceScope);
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys]];
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_service_historyDetail delegate:aDelegate];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    取消服务
// URL:         https://shop.bcgogo.com/api/service/singleService/orderId/{orderId}/userNo/{userNo}
// Note:        DELETE
// Params:      *orderId：单据ID Long
//              *userNo：用户账号 String
- (NSInteger)serviceCancel:(NSString *)aOrderId delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_service_delete];
    NSAssert(url, @"service delete url can't get");
    
    if ([aOrderId length] == 0 || [self.userName length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
 
    KKSetSequenceParamString(seqKeys, params, @"orderId", aOrderId);
    KKSetSequenceParamString(seqKeys, params, @"userNo", self.userName);
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params]];
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_service_delete delegate:aDelegate];
    [request setRequestMethod:@"DELETE"];
    [self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    评价店铺
// URL:         https://shop.bcgogo.com/api/shop/score
// Note:        PUT
// Params:
//              *userNo：用户账号 String
//              *orderId：单据ID Long
//              *commentScore：评分 Integer
//              commentContent：评论 String
- (NSInteger)shopScore:(NSString *)aOrderId commentScore:(NSInteger)aCommentScore commentContent:(NSString *)aCommentContent delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_shop_score];
    NSAssert(url, @"shop score url can't get");
    
    if ([aOrderId length] ==0 || [self.userName length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    
    KKSetParamString(params, @"userNo", self.userName, NO);
    KKSetParamString(params, @"orderId", aOrderId, NO);
    KKSetParamInt(params, @"commentScore", aCommentScore, NO);
    KKSetParamString(params, @"commentContent", aCommentContent, NO);
    
	KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_shop_score delegate:aDelegate];
    [KKProtocolEngine makePutData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}


// Function:    查看个人资料
// URL:         https://shop.bcgogo.com/api/user/information/userNo/{userNo}
// Note:        GET
// Params:      *userNo：用户账号 String
- (NSInteger)userInformation:(NSString *)aUserNo delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_user_information];
    NSAssert(url, @"user infomation url can't get");
    
    if ([aUserNo length] == 0)
        aUserNo = self.userName;
    if ([aUserNo length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    
    KKSetSequenceParamString(seqKeys, params, @"userNo", aUserNo);
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys]];
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_user_information delegate:aDelegate];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    修改密码
// URL:         https://shop.bcgogo.com/api/user/password
// Note:        PUT
// Params:      *userNo：用户账号 String
//              *oldPassword：旧密码 String
//              *newPassword：新密码 String
- (NSInteger)userPasswordModify:(NSString *)aUserNo oldPassword:(NSString *)aOldPassword newPassword:(NSString *)aNewPassword delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_user_passwordModify];
    NSAssert(url, @"user password modify url can't get");
    
    if ([aUserNo length] == 0)
        aUserNo = self.userName;
    if ([aUserNo length] == 0|| [aNewPassword length] ==0 || [aOldPassword length] == 0 )
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    
    KKSetParamString(params, @"userNo", aUserNo, NO);
    KKSetParamString(params, @"oldPassword", aOldPassword, NO);
    KKSetParamString(params, @"newPassword", aNewPassword, NO);
    
	KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_user_passwordModify delegate:aDelegate];
    [KKProtocolEngine makePutData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    修改个人资料
// URL:         https://shop.bcgogo.com/api/user/information
// Note:        PUT
// Params:      *userNo：用户账号 String
//              *mobile：手机号 String
//              *name：客户名 String
- (NSInteger)userInformationModify:(NSString *)aUserNo mobile:(NSString *)aMobile name:(NSString *)aName delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_user_informationModify];
    NSAssert(url, @"user password modify url can't get");
    
    if ([aUserNo length] == 0)
        aUserNo = self.userName;

    if ([aUserNo length] == 0 || [aMobile length] ==0 || [aName length] == 0 )
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    
    KKSetParamString(params, @"userNo", aUserNo, NO);
    KKSetParamString(params, @"mobile", aMobile, NO);
    KKSetParamString(params, @"name", aName, NO);
    
	KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_user_informationModify delegate:aDelegate];
    [KKProtocolEngine makePutData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}


// Function:    修改保养信息
// URL:         https://shop.bcgogo.com/api/vehicle/maintain
// Note:        PUT
// Params:      *userNo：用户账号 String
//              *vehicleId：用户车辆ID 数据库id Long型
//              nextMaintainMileage：下次保养里程   int
//              nextInsuranceTime：下次保险时间     Long
//              nextExamineTime：下次验车时间  Long
//              currentMileage:当前里程     int
- (NSInteger)vehicleMaintainInfoModify:(NSString *)aUserNo vehicleId:(NSString *)aVehicleId nextMaintainMileage:(NSInteger)aNextMaintainMileage
                     nextInsuranceTime:(NSDate *)aNextInsuranceTime nextExamineTime:(NSDate *)aNextExamineTime currentMileage:(NSInteger)aCurrentMileage
                              delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_vehicle_maintainModify];
    NSAssert(url, @"maintain modify url can't get");
    
    if ([aUserNo length] == 0)
        aUserNo = self.userName;
    
    if ([aUserNo length] == 0 || [aVehicleId length] ==0 )
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    
    KKSetParamString(params, @"userNo", aUserNo, NO);
    KKSetParamString(params, @"vehicleId", aVehicleId, NO);
    KKSetParamInt(params, @"nextMaintainMileage", aNextMaintainMileage, NO);
    KKSetParamDate(params, @"nextInsuranceTime", aNextInsuranceTime, NO);
    KKSetParamDate(params, @"nextExamineTime", aNextExamineTime, NO);
    KKSetParamInt(params, @"currentMileage", aCurrentMileage, NO);
    
	KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_vehicle_maintainModify delegate:aDelegate];
    [KKProtocolEngine makePutData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    注销
// URL:         https://shop.bcgogo.com/api/logout
// Note:        PUT
// Params:      *userNo：用户账号     String
- (NSInteger)userLogout:(NSString *)aUserNo delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_user_logout];
    NSAssert(url, @"user logout url can't get");
    
    if ([aUserNo length] == 0)
        aUserNo = self.userName;
    
    if ([aUserNo length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    
    KKSetParamString(params, @"userNo", aUserNo, NO);
    
	KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_user_logout delegate:aDelegate];
    [KKProtocolEngine makePutData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    用户反馈
// URL:         https://shop.bcgogo.com/api/user/feedback
// Note:        POST
// Params:      *userNo：用户账号     StrinG
//              *content：反馈内容 String
//              *mobile：联系方式   String
- (NSInteger)userFeedback:(NSString *)aUserNo content:(NSString *)aContent mobile:(NSString *)aMobile delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_user_feedback];
    NSAssert(url, @"vehicle fault url can't get");
    
    
    if ([aUserNo length] == 0)
        aUserNo = self.userName;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    switch ([KKAuthorization sharedInstance].authorizationType) {
        case Authorization_Visitor:
        {
            KKSetParamString(params, @"content", aContent, NO);
            KKSetParamString(params, @"mobile", aMobile, NO);
            KKSetParamString(params, @"mobileInfo.platform", CurrentSystemPlatform, NO);
            KKSetParamString(params, @"mobileInfo.platformVersion", CurrentSystemVersion, NO);
            KKSetParamString(params, @"mobileInfo.appVersion", KKAppDelegateSingleton.versionStr, NO);
        }
            break;
        case Authorization_Register:
        {
            if ([aUserNo length] == 0 || [aContent length] == 0 || [aMobile length] == 0)
                return KKInvalidRequestID;
            KKSetParamString(params, @"userNo", aUserNo, NO);
            KKSetParamString(params, @"content", aContent, NO);
            KKSetParamString(params, @"mobile", aMobile, NO);
        }
            break;
    }
    
    KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_user_feedback delegate:aDelegate];
    [KKProtocolEngine makePostData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    获取服务范围
// URL:         https://shop.bcgogo.com/api/serviceCategory/list
// Note:        GET
// Params:      serviceScope ：可以为空（为空的时候返回的是所有的二级分类）

//              OVERHAUL_AND_MAINTENANCE    //机修保养
//              DECORATION_BEAUTY           //美容装潢
//              PAINTING                    //钣金喷漆
//              INSURANCE                   //保险验车
//              WASH                        //洗车服务
- (NSInteger)getServiceCategoryList:(NSString *)aServiceScope delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_serviceCategory_list];
    NSAssert(url, @"serviceCategory List url can't get");
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];

    KKSetSequenceParamString(seqKeys, params, @"serviceScope", aServiceScope);
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys]];
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_serviceCategory_list delegate:aDelegate];
	[self issueRequest:request];
    
	return [self requestID:request];
}


// Function:    修改默认车辆
// URL:         https://shop.bcgogo.com/api/vehicle/updateDefault
// Note:        POST
// Params:      *vehicleId ：车辆Id
- (NSInteger)updateDefaultVehicle:(NSString *)aVehicleId delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_vehicle_updateDefault];
    NSAssert(url, @"update default vehicle url can't get");

    if ([aVehicleId length] == 0)
        return KKInvalidRequestID;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    KKSetParamString(params, @"vehicleId", aVehicleId, NO);
    
    KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_vehicle_updateDefault delegate:aDelegate];
    [KKProtocolEngine makePostData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    根据一辆车vin获取车辆信息
// URL:         https://shop.bcgogo.com/api/vehicle/singleVehicle/vehicleVin/{vehicleVin}/userNo/{userNo}
// Note:        GET
// Params:      *vehicleVin :
//              *userNo:
- (NSInteger)getVehicleInfoWithVehicleVin:(NSString *)aVehicleVin withUserNo:(NSString *)aUserNo delegate:(id)aDelegate
{
    if ([aVehicleVin length] == 0 || [aUserNo length] == 0)
        return KKInvalidRequestID;
    
    NSString *url = [self getAccessUrl:ePtlApi_vehicle_singleVehicle_vehicleVin];
    NSAssert(url, @"get singleVehicle info url can't get");
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    
    KKSetSequenceParamString(seqKeys, params, @"vehicleVin", aVehicleVin);
    KKSetSequenceParamString(seqKeys, params, @"userNo", aUserNo);
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys]];
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_vehicle_singleVehicle_vehicleVin delegate:aDelegate];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    获取后台车辆信息建议
// URL:         {URL}/api/vehicle/info/suggestion/{mobile}/{vehicleNo}
// Note:        POST
// Params:
//
-(NSInteger) getSuggestVehicleWithMobile:(NSString *)aMobile VehicleNo:(NSString *)aVehicleNo delegate:(id)aDelegate
{
    
    NSString *url = [self getAccessUrl:ePtlApi_Register_SuggestVehicle];
    NSAssert(url, @"get singleVehicle info url can't get");
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    KKSetSequenceParamString(seqKeys, params, @"mobile", aMobile);
    KKSetSequenceParamString(seqKeys, params, @"vehicleNo", aVehicleNo);
    
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParams:params sequenceKeys:seqKeys containKeys:NO]];
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_Register_SuggestVehicle delegate:aDelegate];
	[self issueRequest:request];
    
//    KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_oil_stationList delegate:aDelegate];
//    [KKProtocolEngine makePostData:params request:request];
//	[self issueRequest:request];
    
	return [self requestID:request];
}


// Function:    获取加油站列表
// URL:         http://apis.juhe.cn/oil/local?key=c8adc805a4a1fdeb7a79798d03d06a46&dtype=json&lon=120.799379&lat=31.271484&r=10000&page=1
// Note:        GET
// Params:      lon,lat 经纬度
//              radius  搜索半径
//              page    搜索分页
-(NSInteger) getOilStationList:(CLLocationCoordinate2D)coordinate Radius:(NSInteger)radius Page:(NSInteger)page delegate:(id)aDelegate
{
    
    NSString *url = [self getAccessUrl:ePtlApi_oil_stationList];
    NSAssert(url, @"get singleVehicle info url can't get");

    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    KKSetSequenceParamString(seqKeys, params, @"key", @"c8adc805a4a1fdeb7a79798d03d06a46");
    KKSetSequenceParamString(seqKeys, params, @"dtype", @"json");
//    KKSetSequenceParamString(seqKeys, params, @"lon", @"120.74443");
//    KKSetSequenceParamString(seqKeys, params, @"lat", @"31.260389");
    KKSetSequenceParamFloat(seqKeys, params, @"lon", coordinate.longitude);
    KKSetSequenceParamFloat(seqKeys, params, @"lat", coordinate.latitude);
    KKSetSequenceParamInt(seqKeys, params, @"page", page);
    KKSetSequenceParamInt(seqKeys, params, @"r", radius);
    
    
    //NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeURIParamsWithAnd:params sequenceKeys:seqKeys]];
    KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_oil_stationList delegate:aDelegate];
    [KKProtocolEngine makePostData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}

// Function:    获取聚合支持的违章查询地区列表
// URL:         {URL}/api/area/juhe/list
// Note:        GET
// Params:
-(NSInteger) getViolateJuheAreaList:(id) aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_violate_juheAreaList];
    NSAssert(url, @"get singleVehicle info url can't get");
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:url apiId:ePtlApi_violate_juheAreaList delegate:aDelegate];
	[self issueRequest:request];
    
    return [self requestApiID:request];
}

// Function:    获取聚合支持的违章查询地区列表
// URL:         http://v.juhe.cn/wz/query
// Note:        GET
// Params:
-(NSInteger) getViolateJuheQuery:(NSString *)aVehicleNo veNoType:(NSString *)aVeNoType city:(NSString *)aCity engineNo:(NSString *)aEngineNo classNo:(NSString *)aClassNo registNo:(NSString *)aRegistNo delegate:(id) aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_violate_query];
    NSAssert(url, @"get singleVehicle info url can't get");
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:20];
    NSMutableArray *seqKeys = [NSMutableArray arrayWithCapacity:10];
    KKSetSequenceParamString(seqKeys, params, @"dtype", @"json");
    KKSetSequenceParamString(seqKeys, params, @"key", @"60ad2a9b3c7bcda13b781dabe01fe843");
    KKSetSequenceParamString(seqKeys, params, @"city", aCity);
    KKSetSequenceParamString(seqKeys, params, @"hphm", aVehicleNo);
    KKSetSequenceParamString(seqKeys, params, @"hpzl", aVeNoType);
    
    if(aEngineNo && ![aEngineNo isEqualToString:@""])
    {
        KKSetSequenceParamString(seqKeys, params, @"engineno", aEngineNo);
    }
    if(aClassNo && ![aClassNo isEqualToString:@""])
    {
        KKSetSequenceParamString(seqKeys, params, @"classno", aClassNo);
    }
    if(aRegistNo && ![aRegistNo isEqualToString:@""])
    {
        KKSetSequenceParamString(seqKeys, params, @"registno", aRegistNo);
    }
    
    NSString *reqUrl = [NSString stringWithFormat:@"%@%@", url, [KKProtocolEngine makeParams:params]];
    
    KKHTTPRequest *request = (KKHTTPRequest*)[self basicRequest:eHTTPRequest url:reqUrl apiId:ePtlApi_violate_query delegate:aDelegate];
	[self issueRequest:request];
    
    return [self requestApiID:request];

}

-(NSInteger) driveRecord_Upload:(BGDriveRecordDetail *)aDriveRecord delegate:(id)aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_driveRecord_upload];

    NSMutableDictionary *dic =(NSMutableDictionary *)[aDriveRecord dictionaryRepresentation];
    [dic removeObjectForKey:@"class"];
    [dic removeObjectForKey:@"pointArray"];
    [dic removeObjectForKey:@"state"];
    if(aDriveRecord.id == 0)
    {
        [dic removeObjectForKey:@"id"];
    }
    
	KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_driveRecord_upload delegate:aDelegate];
    [KKProtocolEngine makePutData:dic request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}


//              用户配置数据修改
-(NSInteger) updateAppUserConfig:(NSString *)aOilPrice delegate:(id) aDelegate
{
    NSString *url = [self getAccessUrl:ePtlApi_appUserConfig_update];
    NSAssert(url, @"obd bind url can't get");
    
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithCapacity:2];
    KKSetParamString(params, @"appUserConfigDTOs[0].name", @"oil_price", NO);
    KKSetParamString(params, @"appUserConfigDTOs[0].value", aOilPrice, NO);
    
    KKFormDataRequest *request = (KKFormDataRequest*)[self basicRequest:eHTTPFormRequest url:url apiId:ePtlApi_appUserConfig_update delegate:aDelegate];
    [KKProtocolEngine makePostData:params request:request];
	[self issueRequest:request];
    
	return [self requestID:request];
}

#pragma mark -
#pragma mark delegate(ASIHTTPRequest)

// Called when a request completes successfully, lets the delegate know via didFinishSelector
- (void)requestFinished:(ASIHTTPRequest *)request
{
	NSAssert(request!=nil, @"");
	
	NSString *responseString = [request responseString];
    
#ifdef KK_HTTP_DEBUG	
	NSLog(@"request url=%@", [request.url absoluteString]);
	NSLog(@"response is %@", responseString);
    
#ifdef KK_HTTP_DEBUG_SAVE_LOG 
	static int i = 0;
    NSString* path = [KKHelper getPathWithinDocumentDir:[NSString stringWithFormat:@"rsp/%d.xml", ++i]];
    [KKHelper createDirectory:path lastComponentIsDirectory:NO];
    if (![responseString writeToFile:path atomically:YES encoding:NSUTF8StringEncoding error:nil])  
    {
        NSLog(@"%@", @"save responseString failed");
    }
#endif
    
#endif
	
	NSEnumerator *enumer = [[request responseHeaders] keyEnumerator];
	id key;
	while (key = [enumer nextObject]) {
#ifdef KK_HTTP_DEBUG	
		id object = [[request responseHeaders] objectForKey:key];
		NSLog(@"http header key=%@, value=%@", key, object);
#endif
	}
    
    NSArray *cookies = [request responseCookies];
//    NSLog(@"cookies are %@", cookies);
    for (NSHTTPCookie *cookie in cookies) {
        if ([cookie.name isEqualToString:@"JSESSIONID"])
            self.sessionCookie = cookie;
    }
	
	id retObj = nil;
    KKProtocolApiId apiID = [self requestApiID:request];
    
	if ([request responseStatusCode] != 200) {
		NSInteger _subcode = [request responseStatusCode];
		KKError *err = [KKError KKErrorWithCode:eErrorHTTPError withSubcode:_subcode  withDesc:[request responseStatusMessage] withDetailErr:request.error];
        err.description = @"服务器错误";
		retObj = err;
    
	}
    else {
        id jsonObj = [responseString mutableObjectFromJSONString];
        if (jsonObj == nil)
            jsonObj = [responseString mutableObjectFromJSONStringWithParseOptions:JKParseOptionLooseUnicode];
        if (nil == jsonObj || NO == [[jsonObj class] isSubclassOfClass:[NSMutableDictionary class]]) {
            NSInteger _subcode = error_request_not_found;
            KKError *err = [KKError KKErrorWithCode:eErrorProtocol withSubcode:_subcode  withDesc:@"no response" withDetailErr:nil];
            retObj = err;
            
        }
        else {
            KKProtocolAbstractParser *parser = [KKProtocolParserFactory createParser:apiID];
            if (parser)
                retObj = [parser parse:jsonObj];
            
            if ([retObj isKindOfClass:[KKError class]])
            {
                KKError *error = (KKError *)retObj;
                if ([error.description isEqualToString:@"登录过期"])
                {
                    [_requestQueue removeAllObjects];
                    [KKAppDelegateSingleton loginOverdue];
                    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
                    return;
                }
            }
        }
    }
    
	SEL sel = [KKProtocolEngineSelector getResponseSEL:apiID];
	if (sel)
		[self DispatchResponse:request withObject:retObj action:sel];
	
	[_requestQueue removeObject:request];
	
	if ([_requestQueue count] == 0)
		[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
}

// Called when a request fails, and lets the delegate know via didFailSelector
- (void)requestFailed:(ASIHTTPRequest *)request
{
	NSAssert(request!=nil, @"");
	
#ifdef KK_HTTP_ERROR_DEBUG	
    NSString *responseString = [request responseString];
	NSLog(@"%@", responseString);
	NSLog(@"request error = %@", request.error);
#endif
	
	NSInteger _subcode = [request.error code];
	if (_subcode > eKKNetworkErrCodeMax)
		_subcode = eKKUnhandledExceptionError;
	KKNetworkErrCode subcode = (KKNetworkErrCode)_subcode;
	
	if (subcode == eKKRequestCancelledErrorType) {
		[_requestQueue removeObject:request];
		if ([_requestQueue count] == 0)
			[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
		return;
	}

    KKProtocolApiId apiID = [self requestApiID:request];
	NSString *desc = [NSString stringWithFormat:@"请求失败, 错误代码(%d-%d)", apiID, subcode];
	if (subcode == eKKRequestTimedOutErrorType)
		desc = NSLocalizedString(@"请求超时，请重试一次！", nil);
    if (subcode == eKKConnectionFailureErrorType)
        desc = NSLocalizedString(@"当前网络不可用，请检查您的网络设置!", nil);
    
	KKError *err = [KKError KKErrorWithCode:eErrorNetwork withSubcode:subcode withDesc:desc withDetailErr:request.error];
	
	SEL sel = [KKProtocolEngineSelector getResponseSEL:apiID];
	if (sel)
		[self DispatchResponse:request withObject:err action:sel];
	[_requestQueue removeObject:request];
	if ([_requestQueue count] == 0)
		[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
}

- (KKHTTPRequest*) httpRequestById:(NSInteger)aRequestID
{
	NSEnumerator *enumerator = [_requestQueue objectEnumerator];
	NSObject *object = nil;
	while (object = (NSObject*)[enumerator nextObject]) {
		if ([[object class] isSubclassOfClass:[KKHTTPRequest class]]) {
			KKHTTPRequest *request = (KKHTTPRequest*)object;
			NSInteger rid = [self requestID:request];
			if (rid == aRequestID)
				return request;
		}
	}
	return nil;
}

@end




