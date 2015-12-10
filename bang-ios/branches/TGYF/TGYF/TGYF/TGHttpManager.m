//
//  TGHttpManager.m
//  TGYIFA
//
//  Created by James Yu on 14-5-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGHttpManager.h"
#import "TGNetworkOperation.h"
#import "TGJsonRspParser.h"
#import "TGUtils.h"

#define GET     @"GET"
#define POST    @"POST"
#define PUT     @"PUT"
#define COOKIE  @"COOKIE"

#define TGSetParamString(params, key, value, fillNull) \
    do{ \
        if (value && [value length] > 0) \
            [params setValue:value forKey:key]; \
        else if (fillNull) \
            [params setValue:@"NULL" forKey:key]; \
        }while(0)

#define TGSetSequenceParamString(seqKeys, params, key, value) \
    do{ \
        TGSetParamString(params, key, value, YES); \
            [seqKeys addObject:key]; \
        }while(0)


#define TGSetParamFloat(params, key, value, fillNull) \
    do { \
        NSString *valueString = [NSString stringWithFormat:@"%f", value]; \
        TGSetParamString(params, key, valueString, fillNull); \
        } while(0)

#define TGSetSequenceParamFloat(seqKeys, params, key, value) \
    do{ \
        TGSetParamFloat(params, key, value, YES); \
        [seqKeys addObject:key]; \
        }while(0)

#define TGSetParamInt(params, key, value, fillNull) \
    do { \
        NSString *valueString = [NSString stringWithFormat:@"%d", value]; \
        TGSetParamString(params, key, valueString, fillNull); \
        } while(0)

#define TGSetSequenceParamInt(seqKeys, params, key, value) \
    do{ \
        TGSetParamInt(params, key, value, YES); \
        [seqKeys addObject:key]; \
        }while(0)

#define TGSetParamLongLong(params, key, value, fillNull) \
    do { \
        NSString *valueString = [NSString stringWithFormat:@"%lld", value]; \
        TGSetParamString(params, key, valueString, fillNull); \
        } while(0)

#define TGSetSequenceParamLongLong(seqKeys, params, key, value) \
    do{ \
        TGSetParamLongLong(params, key, value, YES); \
        [seqKeys addObject:key]; \
    }while(0)

static TGHttpManager *_httpManager= nil;

static const TGApiIDDictItem apiIDDictionary[] = {
    {apiID_user_login,                      @"bcgogoApp/login"},
    {apiID_get_SMS_content,                 @"bcgogoApp"},
    {apiID_send_SMS,                        @"bcgogoApp/sendMsg"},
    {apiID_remindHandle,                    @"bcgogoApp/remindHandle"},
    {apiID_accept_appoint,                  @"bcgogoApp/acceptAppoint"},
    {apiID_change_appoint_time,             @"bcgogoApp/changeAppointTime"},
    {apiID_get_faultInfo_list,              @"bcgogoApp/vehicleFaultInfoList"},
    {apiID_get_remind_list,                 @"bcgogoApp/customerRemindList"},
};

@implementation TGHttpManager

#pragma mark - 请求前处理

+ (TGHttpManager *)sharedInstance
{
    if (_httpManager == nil) {
        static dispatch_once_t oncePredicate;
        dispatch_once(&oncePredicate,^{
            _httpManager = [[TGHttpManager alloc] init];
        });
    }
    return _httpManager;
}

- (id)init
{
    if (self = [super init]) {
        _jsonNetWorkEngine = [[TGJsonNetworkEngine alloc] initWithHostName:[self getHostName]];
        [_jsonNetWorkEngine registerOperationSubclass:[TGNetworkOperation class]];
        _operationQueues = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void)cancelAllRequest
{
    for (TGNetworkOperation *operation in _operationQueues) {
        [operation cancel];
        [_operationQueues removeObject:operation];
    }
}

- (void)cancelRquestWithViewControllerClass:(NSString *)aViewControllerClass
{
    for (TGNetworkOperation *operation in _operationQueues) {
        if ([operation.viewControllerClass isEqualToString:aViewControllerClass]) {
            [operation cancel];
            [_operationQueues removeObject:operation];
        }
    }
}

+ (NSString *)getUrlPathByApiId:(TGHTTPRequest_ApiID)apiId
{
    NSInteger count = sizeof(apiIDDictionary) / sizeof(TGApiIDDictItem);
    
    NSString *path = nil;
    for (int i = 0; i < count ; i ++) {
        TGApiIDDictItem item = apiIDDictionary[i];
        if (item.apiId == apiId) {
            path = item.urlStr;
        }
    }
    
    return path;
}

#define HTTP_SSL YES
//#define HTTP_SSL NO
- (NSString *)getHostName
{
    //return @"phone.bcgogo.com:443/api";
    return @"192.168.1.186/api";
}
//拼接Get Url
+ (NSString *)markURIParams:(NSMutableDictionary*)paramDict sequenceKeys:(NSMutableArray *)sequenceKeys containKeys:(BOOL)containKeys
{
    NSMutableString *query = [[NSMutableString alloc] init];
    
    for(NSString *key in sequenceKeys)
    {
        NSString *pair = nil;
        
        if(containKeys)
        {
            if([key isEqualToString:@""])
            {
                pair = [NSString stringWithFormat:@"/%@", [paramDict objectForKey:key]];
            }
            else
            {
                pair = [NSString stringWithFormat:@"/%@/%@", key, [paramDict objectForKey:key]];
            }
        }
		else
            pair = [NSString stringWithFormat:@"/%@", [paramDict objectForKey:key]];
        [query appendString:pair];
    }
    
    return query;
}

#pragma mark - POST
- (void)postWithParams:(NSMutableDictionary *)aParams apiId:(TGHTTPRequest_ApiID)apiId viewControllerClass:aViewControllerClass success:(success)aSuccess error:(error)aError
{
    NSString *path = [TGHttpManager getUrlPathByApiId:apiId];
    
    [self performRequestWithParams:aParams path:path httpMethod:POST apiId:apiId viewControllerClass:aViewControllerClass success:aSuccess error:aError];
}

#pragma mark - GET
- (void)getWithParams:(NSMutableDictionary *)aParams sequenceKeys:(NSMutableArray *)aSequenceKeys containKeys:(BOOL)aContainKeys apiId:(TGHTTPRequest_ApiID)apiId viewControllerClass:aViewControllerClass success:(success)aSuccess error:(error)aError
{
    NSString *path = [TGHttpManager getUrlPathByApiId:apiId];
    
    path = [NSString stringWithFormat:@"%@%@", path, [TGHttpManager markURIParams:aParams sequenceKeys:aSequenceKeys containKeys:aContainKeys]];
    
    aParams = nil;
    
    [self performRequestWithParams:aParams path:path httpMethod:GET apiId:apiId viewControllerClass:aViewControllerClass success:aSuccess error:aError];
}

#pragma mark - PUT

- (void)putWithParams:(NSMutableDictionary *)aParams apiId:(TGHTTPRequest_ApiID)apiId viewControllerClass:aViewControllerClass success:(success)aSuccess error:(error)aError
{
    NSString *path = [TGHttpManager getUrlPathByApiId:apiId];
    
    [self performRequestWithParams:aParams path:path httpMethod:PUT apiId:apiId viewControllerClass:aViewControllerClass success:aSuccess error:aError];
}

#pragma mark - 网络请求解析

- (void)performRequestWithParams:(NSMutableDictionary *)aParams path:(NSString *)aPath httpMethod:(NSString *)aHttpMethod apiId:(TGHTTPRequest_ApiID)apiId viewControllerClass:(Class)aViewControllerClass success:(success)aSuccess error:(error)aError
{
    TGNetworkOperation *operation = (TGNetworkOperation *)[_jsonNetWorkEngine operationWithPath:aPath params:aParams httpMethod:aHttpMethod ssl:HTTP_SSL];
    operation.viewControllerClass = aViewControllerClass;
    operation.shouldContinueWithInvalidCertificate = YES;
    operation.apiId = apiId;
    [operation addHeaders:[NSDictionary dictionaryWithObjectsAndKeys:[[NSUserDefaults standardUserDefaults] objectForKey:COOKIE],@"Cookie", nil]];
    NSLog(@"========handl url=====%@", [operation url]);
    
    TGHttpManager *weakSelf = self;
    
    [operation addCompletionHandler:^(MKNetworkOperation *completedOperation) {
        TGNetworkOperation *operation = (TGNetworkOperation *)completedOperation;
        
        [weakSelf.operationQueues removeObject:operation];
        
        NSHTTPURLResponse *httpUrlRsp = [operation readonlyResponse];
        if ([httpUrlRsp statusCode] == 200) {
            //提取cookies
            NSDictionary *dict = [httpUrlRsp allHeaderFields];
            NSArray *cookieArray = [NSHTTPCookie cookiesWithResponseHeaderFields:dict forURL:[[operation readonlyRequest] URL]];
    
            for (NSHTTPCookie *cookie in cookieArray) {
                if ([cookie.name isEqualToString:@"JSESSIONID"]) {
                    [[NSUserDefaults standardUserDefaults] setValue:[NSString stringWithFormat:@"JSESSIONID=%@",cookie.value] forKey:COOKIE];
                }
            }
            
            NSString *jsonString = [operation responseString];
             NSLog(@"=======request response=====%@", jsonString);
            
            if (jsonString) {
                TGComplexModel *rsp = (TGComplexModel *)[TGJsonRspParser parserWithJsonString:jsonString apiId:operation.apiId];
                
                //TODO
                aSuccess(rsp);
            }
            
        }
        
    } errorHandler:^(MKNetworkOperation *completedOperation, NSError *error) {
        TGNetworkOperation *operation = (TGNetworkOperation *)completedOperation;
        [weakSelf.operationQueues removeObject:operation];
        
        aError(error);
    }];
    
    [_jsonNetWorkEngine enqueueOperation:operation];
}

#pragma mark - 业务请求

#pragma mark - 登陆
- (void)login:(NSString *)userName password:(NSString *)password success:(success)aSuccess error:(error)aEror viewControllerClass:(Class)aViewControllerClass
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    
    TGSetParamString(params, @"userNo", userName, NO);
    TGSetParamString(params, @"password", password, NO);
    TGSetParamString(params, @"appVersion", [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"], NO);
    TGSetParamString(params, @"imageVersion", [TGUtils getImageVersion], NO);
    
    [self postWithParams:params apiId:apiID_user_login viewControllerClass:aViewControllerClass success:aSuccess error:aEror];
}


@end
