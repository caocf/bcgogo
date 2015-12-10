//
//  XSHTTPRequest.m
//  
//
//  Created by Jiahai on 14-2-26.
//
//

#import "TGHTTPRequestEngine.h"
#import "TGHTTPRequestOperation.h"
#import "TGHTTPRequestOperationManager.h"

#define VIEWCONTROLLERIDENTIFIER            @"ViewControllerIdentifier"

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


static TGHTTPRequestEngine *_requestEngine = nil;

static const TGApiIDDictItem apiIDDictionary[] = {
    {apiID_oilStation_getList,      @"http://apis.juhe.cn/oil/local"},
    {apiID_Shop_serviceCategoty,    @"%@/api/serviceCategory/list"},
};

@implementation TGHTTPRequestEngine


+ (TGHTTPRequestEngine *)sharedInstance
{
    @synchronized(self)
    {
        if(_requestEngine == nil)
            _requestEngine = [[TGHTTPRequestEngine alloc] init];
    }
    return _requestEngine;
}

- (id)init
{
    if(self = [super init])
    {
        _requestManager = [TGHTTPRequestOperationManager manager];
    }
    return self;
}

#pragma mark - static Method
+ (NSString *)getServerDomain
{
    return @"https://phone.bcgogo.cn:1443";
}

+ (NSInteger)indexInApiDictById:(TGHTTPRequest_ApiID)apiID
{
	NSInteger i;
	
    NSInteger count = sizeof(apiIDDictionary)/sizeof(TGApiIDDictItem);
    
    for (i=0; i<count; i++) {
        TGApiIDDictItem item = apiIDDictionary[i];
        if (item.apiID == apiID)
            break;
    }
    if (i<count)
        return i;
    
    return -1;
}

+ (NSString *)getAPIURLWithApiID:(TGHTTPRequest_ApiID)aApiID
{
    NSString *url = nil;
    NSString *domain = [TGHTTPRequestEngine getServerDomain];
    
    NSInteger index = [TGHTTPRequestEngine indexInApiDictById:aApiID];
    
    if(index != -1)
    {
        url = [NSString stringWithFormat:apiIDDictionary[index].urlStr,domain];
    }
    
    return url;
}


+ (NSString *)markURIParams:(NSMutableDictionary*)paramDict sequenceKeys:(NSMutableArray *)sequenceKeys
{
    return [TGHTTPRequestEngine markURIParams:paramDict sequenceKeys:sequenceKeys containKeys:YES];
}

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

- (void)cancleRequestWithViewControllerIdentifier:(id)aIdentifier
{
    NSArray *array = [_requestManager.operationQueue operations];
    
    for(TGHTTPRequestOperation *operation in array)
    {
        if([[operation.userInfo objectForKey:VIEWCONTROLLERIDENTIFIER] isEqualToString:aIdentifier])
        {
            [operation cancel];
        }
    }
}

/**
 *  发送网络请求
 *
 *  @param aType       网络请求类型：GET/POST/PUT等
 *  @param apiID       apiID
 *  @param aParams     发送的参数
 *  @param aSeqKeys    参数的KEY序列
 *  @param aIdentifier 用来标识是哪个ViewController的请求
 *  @param aSuccess    成功执行的Block
 *  @param aFailure    失败执行的Block
 */
- (void)performRequest:(TGRequestType)aType
                 apiID:(TGHTTPRequest_ApiID)aApiID
                params:(NSMutableDictionary *)aParams
          sequenceKeys:(NSMutableArray *)aSeqKeys
viewControllerIdentifier:(id)aIdentifier
               success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess
               failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure
{
    NSString *url = [TGHTTPRequestEngine getAPIURLWithApiID:aApiID];
    
    NSLog(@"url is %@;",url);
    if(url == nil)
    {
        return;
    }
    
    TGHTTPRequestOperation *operation = nil;
    switch (aType) {
        case TGRequestType_Get:
        {
            NSString *reqUrl = [NSString stringWithFormat:@"%@%@",url,[TGHTTPRequestEngine markURIParams:aParams sequenceKeys:aSeqKeys]];
            operation = (TGHTTPRequestOperation *)[_requestManager GET:reqUrl parameters:nil success:aSuccess failure:aFailure];
        }
            break;
        case TGRequestType_Post:
        {
            operation = (TGHTTPRequestOperation *)[_requestManager POST:url parameters:aParams success:aSuccess failure:aFailure];
        }
            break;
        case TGRequestType_Put:
        {
            operation = (TGHTTPRequestOperation *)[_requestManager PUT:url parameters:aParams success:aSuccess failure:aFailure];
        }
            break;
        default:
            break;
    }
    
    if(operation)
    {
        operation.apiID = aApiID;
        if(aIdentifier)
            [operation.userInfo setValue:aIdentifier forKey:VIEWCONTROLLERIDENTIFIER];
    }
}




#pragma mark - request method

#pragma mark - 加油站相关
- (void)oilStationGetList:(CLLocationCoordinate2D)coordinate radius:(NSInteger)radius pageNo:(NSInteger)pageNo viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    
    TGSetParamString(params, @"key", @"c8adc805a4a1fdeb7a79798d03d06a46", NO);
    TGSetParamString(params, @"dtype", @"json", NO);
    TGSetParamFloat(params, @"lon", coordinate.longitude, NO);
    TGSetParamFloat(params, @"lat", coordinate.latitude, NO);
    TGSetParamInt(params, @"page", pageNo, NO);
    TGSetParamInt(params, @"r", radius, NO);
    
    [self performRequest:TGRequestType_Post apiID:apiID_oilStation_getList params:params sequenceKeys:nil viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

- (void)shopGetServiceCategoty:(NSString *)aServiceScope viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure
{
    NSMutableArray *seqKeys = [[NSMutableArray alloc] init];
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    
    [seqKeys addObject:@"serviceScope"];
    [params setObject:aServiceScope forKey:@"serviceScope"];
    
    [self performRequest:TGRequestType_Get apiID:apiID_Shop_serviceCategoty params:params sequenceKeys:seqKeys viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

- (void)dealloc
{
    NSLog(@"HTTPRequestEngine  dealloc");
}
@end
