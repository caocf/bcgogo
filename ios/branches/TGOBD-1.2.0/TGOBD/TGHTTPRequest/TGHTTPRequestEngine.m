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

static TGHTTPRequestEngine *_requestEngine = nil;

static const TGApiIDDictItem apiIDDictionary[] = {
    {apiID_user_login,                  @"%@/gsm/login"},
    {apiID_user_register,               @"%@/register/gsm/register"},
    {apiID_user_validateIMEI,           @"%@/register/gsm/validateRegister"},
    {apiID_change_password,             @"%@/user/password"},
    {apiID_user_logout,                 @"%@/logout"},
    {apiID_user_forgetPassword,         @"%@/user/gsm/password"},
    
    {apiID_driveRecord_getVehicle,      @"%@/vehicle/gsmUserGetAppVehicle"},
    {apiID_driveRecord_downloadList,    @"%@/driveLog/driveLogContents"},
    {apiID_driveRecord_downloadDetail,  @"%@/driveLog/detail/NULL"},
    
    //{apiID_oilStation_getList,          @"http://apis.juhe.cn/oil/local"},
    {apiID_violate_getCityList,         @"%@/area/juhe/list"},
    {apiID_violate_query,              @"%@/violateRegulations/queryVehicleViolateRegulation"},
    //{apiID_violate_juheQuery,           @"http://v.juhe.cn/wz/query?"},
    
    {apiID_dtc_getList,                 @"%@/vehicle/faultCodeList"},
    {apiID_dtc_operate,                 @"%@/vehicle/faultCode"},
    
    {apiID_get_newMessage,              @"%@/message/polling"},
    
    {apiID_order_online,                @"%@/service/appointment"},
    {apiID_get_orderList,               @"%@/service/historyList"},
    {apiID_get_orderDetail,             @"%@/service/historyDetail"},
    
    {apiID_update_vehicle,              @"%@/vehicle/saveGsmVehicle"},
    
    {apiID_check_newVersion,            @"%@/newVersion"},
    
    {apiID_Shop_serviceCategoty,        @"%@/serviceCategory/list"},
    
    {apiID_get_advertList,              @"%@/advert/advertList"},
    {apiID_get_advert_detail,           @"%@/advert/advertDetail"},
    {apiID_get_driveStat,               @"%@/driveStat/yearList"},
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
    //return @"http://192.168.1.186:8080/api";
   // return @"https://phone.bcgogo.com:443/api";
    //return @"https://ios.bcgogo.com:1443/api";
    //return @"http://192.168.1.23:8080/api";
    return @"http://61.177.55.242:8141/api";
    //正式发布地址
    //return @"https://shop.bcgogo.com/api";
    
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

- (void)cancleAllRequests
{
    [_requestManager.operationQueue cancelAllOperations];
}

/**
 *  取消某个界面的所有网络请求
 *
 *  @param aIdentifier 该界面的ViewControllerIdentifier
 */
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
 *  GET请求，默认URL包含KEY
 *
 *  @param apiID      apiID
 *  @param params     发送的参数
 *  @param seqKeys    生成URL参数的KEY序列
 *  @param identifier 用来标识是哪个ViewController的请求
 *  @param success    成功执行的Block
 *  @param failure    失败执行的Block
 */
- (void)GET:(TGHTTPRequest_ApiID)apiID parameters:(NSMutableDictionary *)params sequenceKeys:(NSMutableArray *)seqKeys viewControllerIdentifier:(id)identifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))success failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))failure
{
    [self GET:apiID parameters:params sequenceKeys:seqKeys containKeys:YES viewControllerIdentifier:identifier success:success failure:failure];
}

- (void)GET:(TGHTTPRequest_ApiID)apiID parameters:(NSMutableDictionary *)params sequenceKeys:(NSMutableArray *)seqKeys containKeys:(BOOL)containKeys viewControllerIdentifier:(id)identifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))success failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))failure
{
    [self performRequest:TGRequestType_Get apiID:apiID params:params sequenceKeys:seqKeys containKeys:containKeys viewControllerIdentifier:identifier success:success failure:failure];
}

/**
 *  POST请求
 *
 *  @param apiID      apiID description
 *  @param params     POST参数
 *  @param identifier 用来标识是哪个ViewController的请求
 *  @param success    成功执行的Block
 *  @param failure    失败执行的Block
 */
- (void)POST:(TGHTTPRequest_ApiID)apiID parameters:(NSMutableDictionary *)params viewControllerIdentifier:(id)identifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))success failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))failure
{
    [self performRequest:TGRequestType_Post apiID:apiID params:params sequenceKeys:nil containKeys:YES viewControllerIdentifier:identifier success:success failure:failure];
}


/**
 *  PUT请求
 *
 *  @param apiID      apiID description
 *  @param params     PUT参数
 *  @param identifier 用来标识是哪个ViewController的请求
 *  @param success    成功执行的Block
 *  @param failure    失败执行的Block
 */
- (void)PUT:(TGHTTPRequest_ApiID)apiID parameters:(NSMutableDictionary *)params viewControllerIdentifier:(id)identifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))success failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))failure
{
    [self performRequest:TGRequestType_Put apiID:apiID params:params sequenceKeys:nil containKeys:YES viewControllerIdentifier:identifier success:success failure:failure];
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
           containKeys:(BOOL)aContainKeys
viewControllerIdentifier:(id)aIdentifier
               success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess
               failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure
{   
    NSString *url = [TGHTTPRequestEngine getAPIURLWithApiID:aApiID];
    
    //TGLog(@"url is %@;",url);
    if(url == nil)
    {
        NSError *error = [NSError errorWithDomain:[TGHTTPRequestEngine getServerDomain] code:NSURLErrorBadURL userInfo:nil];
        if(aFailure)
        {
            aFailure(nil,error);
        }
        return;
    }
    
    TGHTTPRequestOperation *operation = nil;
    switch (aType) {
        case TGRequestType_Get:
        {
            NSString *reqUrl = [NSString stringWithFormat:@"%@%@",url,[TGHTTPRequestEngine markURIParams:aParams sequenceKeys:aSeqKeys containKeys:aContainKeys]];
            reqUrl = [reqUrl stringByReplacingOccurrencesOfString:@" " withString:@""];
            reqUrl = [reqUrl stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
            TGLog(@"url is %@;",reqUrl);
            operation = (TGHTTPRequestOperation *)[_requestManager GET:reqUrl parameters:nil success:aSuccess failure:aFailure];
        }
            break;
        case TGRequestType_Post:
        {
            TGLog(@"url------%@",url);
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

#pragma mark - 登录/注册
- (void)userLogin:(NSString *)userNo password:(NSString *)password viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    TGModelLoginInfo *loginInfo = [[TGModelLoginInfo alloc] init];
    
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    
    TGSetParamString(params, @"userNo", userNo, NO);
    TGSetParamString(params, @"password", password, NO);
    TGSetParamString(params, @"platform", loginInfo.platform, NO);
    TGSetParamString(params, @"appVersion", loginInfo.appVersion, NO);
    TGSetParamString(params, @"imageVersion", loginInfo.imageVersion, NO);
    TGSetParamString(params, @"platformVersion", loginInfo.platformVersion, NO);
    TGSetParamString(params, @"mobileModel", loginInfo.mobileModel, NO);
    TGSetParamString(params, @"deviceToken", loginInfo.deviceToken, NO);
    
    [self POST:apiID_user_login parameters:params viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

- (void)userValidateIMEI:(NSString *)IMEI mobile:(NSString *)mobile password:(NSString *)password viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    
    TGSetParamString(params, @"mobile", mobile, NO);
    TGSetParamString(params, @"password", password, NO);
    TGSetParamString(params, @"imei", IMEI, NO);
    
    [self PUT:apiID_user_validateIMEI parameters:params viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

- (void)userRegister:(NSString *)mobile password:(NSString *)password imei:(NSString *)imei vehicleNo:(NSString *)vehicleNo oilPrice:(NSString *)oilPrice currentMileage:(NSString *)currentMileage maintainPeriod:(NSString *)maintainPeriod juheCityCode:(NSString *)juheCityCode juheCityName:(NSString *)juheCityName registerNo:(NSString *)registerNo engineNo:(NSString *)engineNo vehicleVin:(NSString *)vehicleVin lastMaintainMileage:(NSString *)lastMaintainMileage nextMaintainTime:(long long)nextMaintainTime nextExamineTime:(long long)nextExamineTime loginInfo:(TGModelLoginInfo *)loginInfo viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    
    TGSetParamString(params, @"mobile", mobile, NO);
    TGSetParamString(params, @"password", password, NO);
    TGSetParamString(params, @"imei", imei, NO);
    TGSetParamString(params, @"vehicleNo", vehicleNo, NO);
    TGSetParamString(params, @"oilPrice", oilPrice, NO);
    TGSetParamString(params, @"currentMileage", currentMileage, NO);
    TGSetParamString(params, @"maintainPeriod", maintainPeriod, NO);
    TGSetParamString(params, @"lastMaintainMileage", lastMaintainMileage, NO);
    
    if (juheCityCode) {
        TGSetParamString(params, @"juheCityCode", juheCityCode, NO);
    }
    if (registerNo) {
        TGSetParamString(params, @"registerNo", registerNo, NO);
    }
    if (engineNo) {
        TGSetParamString(params, @"engineNo", engineNo, NO);
    }
    if (vehicleVin) {
        TGSetParamString(params, @"vehicleVin", vehicleVin, NO);
    }
    if (nextExamineTime) {
         TGSetParamLongLong(params, @"nextExamineTime", nextExamineTime, NO);
    }
    if (nextMaintainTime) {
        TGSetParamLongLong(params, @"nextMaintainTime", nextMaintainTime, NO);
    }
    
    NSMutableDictionary *loginInfoDict = [[NSMutableDictionary alloc] init];
    
    TGSetParamString(loginInfoDict, @"platform", loginInfo.platform, NO);
    TGSetParamString(loginInfoDict, @"platformVersion", loginInfo.platformVersion, NO);
    TGSetParamString(loginInfoDict, @"mobileModel", loginInfo.mobileModel, NO);
    TGSetParamString(loginInfoDict, @"appVersion", loginInfo.appVersion, NO);
    TGSetParamString(loginInfoDict, @"imageVersion", loginInfo.imageVersion, NO);
    TGSetParamString(loginInfoDict, @"deviceToken", loginInfo.deviceToken, NO);

    [params setValue:loginInfoDict forKey:@"loginInfo"];
    
    [self PUT:apiID_user_register parameters:params viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

- (void)userForgetPassword:(NSString *)mobile viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    NSMutableArray *seqKeys = [[NSMutableArray alloc] init];
    
    TGSetSequenceParamString(seqKeys, params, @"mobile", mobile);

    [self GET:apiID_user_forgetPassword parameters:params sequenceKeys:seqKeys viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

#pragma mark - 行车日志相关

- (void)driveRecordGetVehicle:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    [self GET:apiID_driveRecord_getVehicle parameters:nil sequenceKeys:nil viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

- (void)driveRecordDownLoadList:(long long)startTime
                        endTime:(long long)endTime
       viewControllerIdentifier:(id)aIdentifier
                        success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess
                        failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    NSMutableArray *seqKeys = [[NSMutableArray alloc] init];
    
    TGSetSequenceParamLongLong(seqKeys, params, @"startTime", startTime);
    TGSetSequenceParamLongLong(seqKeys, params, @"endTime", endTime);
    
    [self GET:apiID_driveRecord_downloadList parameters:params sequenceKeys:seqKeys containKeys:NO viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

- (void)driveRecordDownLoadDetail:(long long)aID viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    NSMutableArray *seqKeys = [[NSMutableArray alloc] init];
    
    TGSetSequenceParamLongLong(seqKeys, params, @"detailIds", aID);
    
    [self GET:apiID_driveRecord_downloadDetail parameters:params sequenceKeys:seqKeys containKeys:NO viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

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
    
    //[self POST:apiID_oilStation_getList parameters:params viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

#pragma mark - 违章相关
- (void)violateGetCityList:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    [self GET:apiID_violate_getCityList parameters:nil sequenceKeys:nil viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

- (void)violateJuheQuery:(NSString *)juheCitycode hphm:(NSString *)hphm hpzl:(NSString *)hpzl engineno:(NSString *)engineno classno:(NSString *)classno registerno:(NSString *)registerno viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    NSMutableArray *seqKeys = [[NSMutableArray alloc] init];
    
    TGSetSequenceParamString(seqKeys, params, @"city", juheCitycode);
    TGSetSequenceParamString(seqKeys, params, @"hphm", hphm);
    TGSetSequenceParamString(seqKeys, params, @"hpzl", hpzl);
    TGSetSequenceParamString(seqKeys, params, @"engineno", engineno);
    TGSetSequenceParamString(seqKeys, params, @"classno", classno);
    TGSetSequenceParamString(seqKeys, params, @"registno", registerno);
    
    [self GET:apiID_violate_query parameters:params sequenceKeys:seqKeys containKeys:YES viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

#pragma mark - 故障码相关

- (void)dtcGetList:(NSString *)aStatus pageNo:(NSInteger)aPageNo pageSize:(NSInteger)aPageSize viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    
    TGSetParamString(params, @"status", aStatus, NO);
    TGSetParamInt(params, @"pageNo", aPageNo, NO);
    TGSetParamInt(params, @"pageSize", aPageSize, NO);
    
    [self POST:apiID_dtc_getList parameters:params viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

- (void)dtcOperate:(long long)aID errorCode:(NSString *)errorCode oldStatus:(NSString *)oldStatus newStatus:(NSString *)newStatus vehicleId:(long long)vehicleId viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    if (aID != 0)
    {
        TGSetParamLongLong(params, @"appVehicleFaultInfoDTOs[0].id", aID, NO);
    }
    
    TGSetParamString(params, @"appVehicleFaultInfoDTOs[0].errorCode", errorCode, NO);
    TGSetParamString(params, @"appVehicleFaultInfoDTOs[0].lastStatus", oldStatus, NO);
    TGSetParamString(params, @"appVehicleFaultInfoDTOs[0].status", newStatus, NO);
    TGSetParamLongLong(params, @"appVehicleFaultInfoDTOs[0].appVehicleId", vehicleId, NO);
    
    [self POST:apiID_dtc_operate parameters:params viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

#pragma mark - 个人资料

- (void)changePassword:(NSString *)userNo oldPwd:(NSString *)oldPwd newPwd:(NSString *)newPwd viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    
    TGSetParamString(params, @"userNo", userNo, NO);
    TGSetParamString(params, @"oldPassword", oldPwd, NO);
    TGSetParamString(params, @"newPassword", newPwd, NO);

    [self PUT:apiID_change_password parameters:params viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

- (void)logout:(NSString *)userNo viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    
    TGSetParamString(params, @"userNo", userNo, NO);
    
    [self PUT:apiID_user_logout parameters:params viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

#pragma mark - 消息

- (void)getNewMessage:(NSString *)userNo types:(NSString *)types viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    NSMutableArray *seqKeys = [[NSMutableArray alloc] init];
    
    TGSetSequenceParamString(seqKeys, params, @"types", types);
    TGSetSequenceParamString(seqKeys, params, @"userNo", userNo);
    
    [self GET:apiID_get_newMessage parameters:params sequenceKeys:seqKeys containKeys:YES viewControllerIdentifier:nil success:aSuccess failure:aFailure];
}

#pragma mark - 账单相关

- (void)orderOnline:(NSString *)userNo serviceCategoryId:(long long)serviceCategoryId appointTime:(long long)appointTime mobile:(NSString *)mobile contact:(NSString *)contact shopId:(long long)shopId vehicleNo:(NSString *)vehicleNo remark:(NSString *)remark faultInfoItems:(NSArray *)faultInfoItems viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    
    TGSetParamString(params, @"userNo", userNo, NO);
    TGSetParamLongLong(params, @"serviceCategoryId", serviceCategoryId, NO);
    TGSetParamLongLong(params, @"appointTime", appointTime, NO);
    TGSetParamString(params, @"mobile", mobile, NO);
    TGSetParamString(params, @"contact", contact, NO);
    TGSetParamLongLong(params, @"shopId", shopId, NO);
    TGSetParamString(params, @"vehicleNo", vehicleNo, NO);
    TGSetParamString(params, @"remark", remark, NO);
    
    if (faultInfoItems) {
        [params setObject:faultInfoItems forKey:@"faultInfoItems"];
    }
    [self PUT:apiID_order_online parameters:params viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

- (void)getOrderList:(NSString *)userNo status:(NSString *)status pageNo:(NSInteger)pageNo pageSize:(NSInteger)pageSize viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    NSMutableArray *seqKeys = [[NSMutableArray alloc] init];
    
    TGSetSequenceParamString(seqKeys, params, @"serviceScope", @"OVERHAUL_AND_MAINTENANCE,DECORATION_BEAUTY,PAINTING,INSURANCE,WASH");
    TGSetSequenceParamString(seqKeys, params, @"status", status);
    TGSetSequenceParamString(seqKeys, params, @"userNo", userNo);
    TGSetSequenceParamInt(seqKeys, params, @"pageNo", pageNo);
    TGSetSequenceParamInt(seqKeys, params, @"pageSize", pageSize);

    [self GET:apiID_get_orderList parameters:params sequenceKeys:seqKeys containKeys:YES viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

- (void)getOrderDetail:(long long)orderId serviceScope:(NSString *)serviceScope viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    NSMutableArray *seqKeys = [[NSMutableArray alloc] init];
    
    TGSetSequenceParamLongLong(seqKeys, params, @"orderId", orderId);
    TGSetSequenceParamString(seqKeys, params, @"serviceScope", serviceScope);
    
    [self GET:apiID_get_orderDetail parameters:params sequenceKeys:seqKeys containKeys:YES viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

#pragma mark - 更新汽车信息

- (void)updateVehicleInfo:(NSString *)oilPrice vehicleNo:(NSString *)vehicleNo currentMileage:(NSString *)currentMileage maintainPeriod:(NSString *)maintainPeriod juheCityCode:(NSString *)juheCityCode juheCityName:(NSString *)juheCityName registerNo:(NSString *)registerNo engineNo:(NSString *)engineNo vehicleVin:(NSString *)vehicleVin lastMaintainMileage:(NSString *)lastMaintainMileage nextMaintainTime:(long long)nextMaintainTime nextExamineTime:(long long)nextExamineTime viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    
    TGSetParamString(params, @"oilPrice", oilPrice, NO);
    TGSetParamString(params, @"vehicleNo", vehicleNo, NO);
    TGSetParamString(params, @"currentMileage", currentMileage, NO);
    TGSetParamString(params, @"maintainPeriod", maintainPeriod, NO);
    TGSetParamString(params, @"juheCityCode", juheCityCode, NO);
    TGSetParamString(params, @"juheCityName", juheCityName, NO);
    TGSetParamString(params, @"registNo", registerNo, NO);
    TGSetParamString(params, @"engineNo", engineNo, NO);
    TGSetParamString(params, @"vehicleVin", vehicleVin, NO);
    TGSetParamString(params, @"lastMaintainMileage", lastMaintainMileage, NO);
    TGSetParamLongLong(params, @"nextMaintainTime", nextMaintainTime, NO);
    TGSetParamLongLong(params, @"nextExamineTime", nextExamineTime, NO);

    [self PUT:apiID_update_vehicle parameters:params viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

#pragma mark - 版本检测
- (void)checkNewVersion:(NSString *)platform appVersion:(NSString *)appVersion platformVersion:(NSString *)platformVersion mobileModel:(NSString *)mobileModel viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    NSMutableArray *seqKeys = [[NSMutableArray alloc] init];
    
    TGSetSequenceParamString(seqKeys, params, @"platform", platform);
    TGSetSequenceParamString(seqKeys, params, @"appVersion", appVersion);
    TGSetSequenceParamString(seqKeys, params, @"platformVersion", platformVersion);
    TGSetSequenceParamString(seqKeys, params, @"mobileModel", mobileModel);
    
    [self GET:apiID_check_newVersion parameters:params sequenceKeys:seqKeys containKeys:YES viewControllerIdentifier:VIEWCONTROLLERIDENTIFIER success:aSuccess failure:aFailure];
}

#pragma mark - 4S店公告
- (void)getAdvertList:(NSInteger)pageNo pageSize:(NSInteger)pageSize viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    NSMutableArray *seqKeys = [[NSMutableArray alloc] init];
    
    TGSetSequenceParamInt(seqKeys, params, @"pageNo", pageNo);
    TGSetSequenceParamInt(seqKeys, params, @"pageSize", pageSize);
    
    [self GET:apiID_get_advertList parameters:params sequenceKeys:seqKeys containKeys:NO viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

- (void)getAdvertDetail:(long long)advertId viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *, id))aSuccess failure:(void (^)(AFHTTPRequestOperation *, NSError *))aFailure
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    NSMutableArray *seqKeys = [[NSMutableArray alloc] init];
    
    TGSetSequenceParamLongLong(seqKeys, params, @"advertId", advertId);
    
    [self GET:apiID_get_advert_detail parameters:params sequenceKeys:seqKeys containKeys:NO viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

#pragma mark - 行程统计

- (void)getDriveStatistic:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure
{
    [self GET:apiID_get_driveStat parameters:nil sequenceKeys:nil containKeys:NO viewControllerIdentifier:aIdentifier success:aSuccess failure:aFailure];
}

- (void)dealloc
{
    NSLog(@"HTTPRequestEngine  dealloc");
}
@end
