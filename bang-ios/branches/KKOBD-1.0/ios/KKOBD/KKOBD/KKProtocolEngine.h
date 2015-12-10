//
//  KKProtocolEngine.h
//  KKShowBooks
//
//  Created by zhuyc on 12-10-11.
//  Copyright (c) 2012年 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>
#import "KKModelBaseElement.h"

// ================================================================================================
// type define
// ================================================================================================
@class ASIHTTPRequest;
@class ASIFormDataRequest;
@class ASINetworkQueue;

typedef ASIHTTPRequest KKHTTPRequest;
typedef ASIFormDataRequest KKFormDataRequest;
typedef ASINetworkQueue KKNetworkQueue;

typedef enum {
	eKKResultEnd = 0,							// 截止处理,其它delegate将没机会处理此事件
	eKKResultGoOn								// 继续遍历delegates去处理相应的事件
} _KKHandleResult;
typedef NSUInteger KKHandleResult;

typedef enum {
    ePtlApi_user_register = 0,                  // 用户注册
    ePtlApi_user_login,                         // 登录
    ePtlApi_obd_bind,                           // obd 绑定
    ePtlApi_user_password,                      // 找回密码
    ePtlApi_new_version,                        // 升级检测
    
    ePtlApi_vehicle_list,                       // 获取车辆列表
    ePtlApi_vehicle_saveInfo,                   // 保存车辆信息
    ePtlApi_vehicle_getInfo,                    // 获取车辆信息
    ePtlApi_vehicle_delete,                     // 删除车辆
    ePtlApi_vehicle_getModelByKey,              // 根据关键字获取车辆品牌和车型
    ePtlApi_vehicle_fault,                      // 发送车辆故障信息
    ePtlApi_vehicle_faultDic,                   // 故障字典更新
    ePtlApi_vehicle_condition,                  // 发送车况信息
   
    ePtlApi_area_list,                          // 获取地区列表
    
    ePtlApi_shop_searchList,                    // 查询推荐店铺
    ePtlApi_shop_suggestionsByKey,              // 根据关键字获取店铺建议列表
    ePtlApi_shop_detail,                        // 根据店铺ID获取店铺详情
    
    ePtlApi_message_polling,                    // 消息轮询
    
    ePtlApi_service_appointment,                // 预约服务
    ePtlApi_service_historyList,                // 服务历史查询
    ePtlApi_service_historyDetail,              // 服务历史详情
    ePtlApi_service_delete,                     // 取消服务
	
    ePtlApi_shop_score,                         // 评价店铺
    ePtlApi_user_information,                   // 查看个人资料
	ePtlApi_user_passwordModify,                // 修改密码
    ePtlApi_user_informationModify,             // 修改个人资料
    ePtlApi_vehicle_maintainModify,             // 修改保养信息
    ePtlApi_user_logout,                        // 注销
    ePtlApi_user_feedback,                      // 用户反馈
    ePtlApi_serviceCategory_list,               // 获取服务范围
    ePtlApi_vehicle_updateDefault,              // 修改默认车辆
    ePtlApi_vehicle_singleVehicle_vehicleVin,   // 根据一辆车Vin获取车辆信息
    ePtlApi_Register_SuggestVehicle,            // 注册时获取后台车辆信息建议
    
    ePtlApi_oil_stationList,                    //加油站列表
    ePtlApi_Register_shopBinding,               //绑定店铺，仅仅适用于注册只填店铺
    
    ePtlApi_violate_juheAreaList,               //聚合支持的地区列表
    ePtlApi_violate_query,                      //查询违章信息
    
    ePtlApiNotSupport  = 1000
}_KKProtocolApiId;
typedef NSInteger KKProtocolApiId;

typedef struct _KKPtlApiDictionary {
	NSString *strUrl;
	KKProtocolApiId apiId;
} KKPtlApiDictionary;

#define KKInvalidRequestID (-1)

#pragma mark -
// ================================================================================================
// KKProtocolEngine
// ================================================================================================
@interface KKProtocolEngine : NSObject {
	NSMutableArray *_requestQueue;
	NSMutableArray *_delegates;
	NSString *_userName;
	NSString *_password;
	NSString *_language;
    NSHTTPCookie *_sessionCookie;
}

@property (nonatomic, readonly) NSMutableArray *delegates;
@property (nonatomic, copy) NSString *userName;
@property (nonatomic, copy) NSString *password;
@property (nonatomic, copy) NSString *language;
@property (nonatomic, retain) NSHTTPCookie *sessionCookie;
@property (nonatomic, assign) BOOL serviceEnvironment;          //后台环境：YES-正式，NO-测试

// creator, only support zh_cn, english in current
- (id) initWithLanguage:(NSString*)aLanguage;
+ (id) KKPtlEngineWithLanguage:(NSString*)aLanguage;
- (void) removeDelegate:(id)aDelegate;
- (void) cancelRequest:(NSInteger)aRequestID;

// signleton
+ (KKProtocolEngine*)sharedPtlEngine; 
//+ (NSString*) getUUIDSecret;
// URL related
+ (NSString *) getKKServerDomain;
+ (KKProtocolApiId) url2Api:(NSURL*)url;
// value of basic auth header, key is 'Authorization'
- (NSString *) basicAuthHeaderValue;


/* all APIs return value is a ID of the request, may be useful later for requestor. 
 if request failed, return KKInvalidRequestId.
 either you can specify a individual delegate for request to seperately handle response by this delegate.
 or you can directly add a delegate in _delegates(queue) to handle response by all the delegates, in this case, 
 you must pass parameter aDelegate=nil, and add delegate to _delegates by your self
 */
#pragma mark -
#pragma mark protocol APIS
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

-(NSInteger) registerShopBind:(NSString *)aShopId vehicleId:(NSString *)aVehicleId delegate:(id)aDelegate;

// Function:    登陆
// Note:        POST
// Params:      *userNo：用户账号
//              *password：用户密码
//              *platform：用户手机系统平台类型
//              platformVersion：用户手机系统平台版本
//              mobileModel：用户手机型号
//              *appVersion：APP版本号
//              imageVersion：图片版本，APP需要根据手机硬件分辨率等信息告知后台需要的图片版本
//                            枚举(IV_320X480,IV_480X800,IV_720X1280,IV_640X960,IV_640X1136) 如果没有这5种找个最接近的
//              手机分辨率与图片版本枚举值的映射关系如下：
//              320 X 480：[109X109][63X63]
//              480 X 800：[164X164][94X94]
//              720 X 1280：[245X245][141X141]
//              640 X 960：[218X218][125X125]
//              640 X 1136：[218X218][125X125]
- (NSInteger)userLoginWithUser:(NSString *)aUserNo password:(NSString *)aPassword platform:(NSString *)aPlatform platformVersion:(NSString *)aPlatformVer mobileModel:(NSString *)aMobileModel appVersion:(NSString *)aVersion imageVersion:(NSString *)aImgVer delegate:(id)aDelegate;


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
               delegate:(id)aDelegate;


// Function:    找回密码
// Note:        GET
// Params:
//              *userNo：用户账号 String
- (NSInteger)userPassword:(NSString *)aUserNo delegate:(id)aDelegate;

// Function:    升级检测
// Note:        GET
// Params:
//              *platform：用户手机系统平台类型(ANDROID,IOS)
//              platformversion：用户手机系统平台版本
//              mobileModel：用户手机型号
//              *appVersion：APP版本号
- (NSInteger)newVersion:(NSString *)aPlatform appVersion:(NSString *)aAppVersion platformVersion:(NSString *)aPlatformVersion mobileModel:(NSString *)aMobileModel delegate:(id)aDelegate;


// Function:    获取车辆列表
// Note:        GET
// Params:
//              *userNo：用户账号 String

- (NSInteger)vehicleListInfo:(NSString *)aUserNo delegate:(id)aDelegate;


// Function:    保存车辆信息
// URL:         https://shop.bcgogo.com/api/vehicle/vehicleInfo
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

- (NSInteger)vehicleSaveInfo:(NSString *)aVehicleId vehicleVin:(NSString *)aVehicleVin vehicleNo:(NSString *)aVehicleNo vehicleModel:(NSString *)aVehicleModel vehicleModelId:(NSString *)aVehicleModelId vehicleBrand:(NSString *)aVehicleBrand vehicleBrandId:(NSString *)aVehicleBrandId obdSN:(NSString *)aObdSN bindingShopId:(NSString *)aShopId userNo:(NSString *)aUserNo engineNo:(NSString *)aEngineNo registNo:(NSString *)aRegistNo nextMaintainMileage:(NSString *)aNextMaintainMileage nextInsuranceTime:(NSDate *)aNextInsuranceTime nextExamineTime:(NSDate *)aNextExamineTime currentMileage:(NSString *)aCurrentMileage delegate:(id)aDelegate;


// Function:    获取车辆信息
// Note:        GET
// Params:
//              *vehicleId：后台数据主键
- (NSInteger)vehicleGetInfo:(NSString *)aVehicleId delegate:(id)aDelegate;

// Function:    删除车辆
// URL:         https://shop.bcgogo.com/api/vehicle/singlevehicle/vehicleId/{vehicleId}
// Note:        DELETE
// Params:      vehicleId：后台数据主键
- (NSInteger)vehicleDeleteWithId:(NSString *)aVehicleId delegate:(id)aDelegate;

// Function:    根据关键字获取车辆品牌和车型
// URL:         https://shop.bcgogo.com/api/vehicle/brandModel/keywords/{keywords}/type/{type}/brandId/{brandId}
// Note:        GET
// Params:
//              keywords：车型或者车辆品牌关键字
//              *type：车辆品牌或者车型  （brand|model）
//              brandid：车辆品牌ID，当type值为"model"时生效
- (NSInteger)vehicleBrandModel:(NSString *)keywords type:(NSString *)aType brandId:(NSString *)aBrandId delegate:(id)aDelegate;

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
- (NSInteger)vehicleFault:(NSString *)aFaultCode userNo:(NSString *)aUserNo vehicleVin:(NSString *)aVehicleVin obdSN:(NSString *)aObdSN reportTime:(NSDate *)aReportTime vehicleId:(NSString *)aVehicleId delegate:(id)aDelegate;

// Function:    故障字典信息更新
// URL:         https://shop.bcgogo.com/api/vehicle/faultDic/dicVersion/{dicVersion}/vehicleModelId/{vehicleModelId}
// Note:        GET
// Params:      vehicleModelId：车型ID ( 如果车型Id为空则取通用字典)
//              dicVersion：字典版本 (如果版本号为空则取最新版）
- (NSInteger)vehicleFaultDict:(NSString *)aVehicleModelId dictVersion:(NSString *)aDictVersion delegate:(id)aDelegate;

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
                     delegate:(id)aDelegate;
//- (NSInteger)vehicleCondition:(NSString *)aVehicleVin obdSN:(NSString *)aObdSN oilWear:(NSString *)aOilWear currentMileage:(NSString *)aCurrentMileage instantOilWear:(NSString *)aInstantOilWear oilWearPerHundred:(NSString *)aOilWearPerHundred oilMass:(NSString *)aOilMass engineCoolantTemperature:(NSString *)aTemperature batteryVoltage:(NSString *)aVoltage reportTime:(NSDate*)aReportTime delegate:(id)aDelegate;




// Function:    获取地区列表
// Note:        GET
// URL:         https://shop.bcgogo.com/api/area/list/{type}/{provinceId}
// Params:
//                provinceId：省份(或城市)ID
//                type：类型 "PROVINCE/CITY"
- (NSInteger)areaList:(NSString *)aType provinceId:(NSString *)aProvinceId delegate:(id)aDelegate;

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
//                Deprecated  旧版接口，使用shopList替代
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
                   pageSize:(NSInteger)aPageSize delegate:(id)aDelegate;


// Function:    根据关键字获取店铺建议列表
// URL:         https://shop.bcgogo.com/api/shop/suggestions/keywords/keywords/{keywords}/cityCode/{cityCode}/areaId/{areaId}
// Note:        GET
// Params:
//              keywords：店铺名称关键字
//              *cityCode：地图数据中的城市编号
- (NSInteger)shopSuggestionsByKey:(NSString *)aKeyword cityCode:(NSString *)aCityCode areaId:(NSString *)aAreaId delegate:(id)aDelegate;


// Function:    根据店铺ID获取店铺详情
// URL:         https://shop.bcgogo.com/api/shop/detail/{shopId}/userNo/{userNo}
// Note:        GET
// Params:
//              *shopId：店铺ID
//              *userNo：用户账号
- (NSInteger)shopDetailWithId:(NSString *)aShopId delegate:(id)aDelegate;

// Function:    消息轮询
// URL:         https://shop.bcgogo.com/api/message/polling/type/{type1,type2...}/userNo/{userNo}
// Note:        GET
// Params:      types：消息类型（多个），用逗号隔开
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
- (NSInteger)messagePollingWithType:(NSString *)aTypes delegate:(id)aDelegate;

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
                                 delegate:(id)aDelegate;


// Function:    服务历史查询
// URL:         https://shop.bcgogo.com/api/service/historyList/type/{type1,type2...}/status/{status1,status2...}/userNo/{userNo}/pageNo/{pageNo}/pageSize/{pageSize}
// Note:        GET
// Params:      serviceScope：服务范围（多个，用逗号分隔） ServiceScope: OVERHAUL_AND_MAINTENANCE、DECORATION_BEAUTY、PAINTING、INSURANCE、WASH
//              status：状态（多个）（已完成：finished|未完成：unfinished） String
//              *userNo：用户账号 String
//              *pageNo：当前页 String
//              *pageSize：分页大小 String
//- (NSInteger)serviceHistoryListWithServiceScope:(NSString *)aServiceScope status:(NSString *)aStatus pageNo:(NSInteger)aPageNo pageSize:(NSInteger)aPageSize delegate:(id)aDelegate;
-(NSInteger)serviceAllHistoryList:(NSInteger)aPageNo pageSize:(NSInteger)aPageSize delegate:(id)aDelegate;

// Function:    服务历史详情
// URL:         https://shop.bcgogo.com/api/service/historyDetail/orderId/{orderId}/type/{type}
// Note:        GET
// Params:      serviceScope：服务范围 ServiceScope: OVERHAUL_AND_MAINTENANCE、DECORATION_BEAUTY、PAINTING、INSURANCE、WASH
//              *orderId：单据ID Long
- (NSInteger)serviceHistoryDetail:(NSString *)aOrderId serviceScope:(NSString *)aServiceScope delegate:(id)aDelegate;

// Function:    取消服务
// URL:         https://shop.bcgogo.com/api/service/singleService/orderId/{orderId}/userNo/{userNo}
// Note:        DELETE
// Params:      *orderId：单据ID Long
//              *userNo：用户账号 String
- (NSInteger)serviceCancel:(NSString *)aOrderId delegate:(id)aDelegate;


// Function:    评价店铺
// URL:         https://shop.bcgogo.com/api/shop/score
// Note:        PUT
// Params:
//              *userNo：用户账号 String
//              *orderId：单据ID Long
//              *commentScore：评分 Integer
//              commentContent：评论 String
- (NSInteger)shopScore:(NSString *)aOrderId commentScore:(NSInteger)aCommentScore commentContent:(NSString *)aCommentContent delegate:(id)aDelegate;


// Function:    查看个人资料
// URL:         https://shop.bcgogo.com/api/user/information/userNo/{userNo}
// Note:        GET
// Params:      *userNo：用户账号 String
- (NSInteger)userInformation:(NSString *)aUserNo delegate:(id)aDelegate;

// Function:    修改密码
// URL:         https://shop.bcgogo.com/api/user/password
// Note:        PUT
// Params:      *userNo：用户账号 String
//              *oldPassword：旧密码 String
//              *newPassword：新密码 String
- (NSInteger)userPasswordModify:(NSString *)aUserNo oldPassword:(NSString *)aOldPassword newPassword:(NSString *)aNewPassword delegate:(id)aDelegate;

// Function:    修改个人资料
// URL:         https://shop.bcgogo.com/api/user/information
// Note:        PUT
// Params:      *userNo：用户账号 String
//              *mobile：手机号 String
//              *name：客户名 String
- (NSInteger)userInformationModify:(NSString *)aUserNo mobile:(NSString *)aMobile name:(NSString *)aName delegate:(id)aDelegate;

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
                              delegate:(id)aDelegate;

// Function:    注销
// URL:         https://shop.bcgogo.com/api/logout
// Note:        PUT
// Params:      *userNo：用户账号     String
- (NSInteger)userLogout:(NSString *)aUserNo delegate:(id)aDelegate;

// Function:    用户反馈
// URL:         https://shop.bcgogo.com/api/user/feedback
// Note:        POST
// Params:      *userNo：用户账号     StrinG
//              *content：反馈内容 String
//              *mobile：联系方式   String
- (NSInteger)userFeedback:(NSString *)aUserNo content:(NSString *)aContent mobile:(NSString *)aMobile delegate:(id)delegate;


// Function:    获取服务范围
// URL:         https://shop.bcgogo.com/api/serviceCategory/list
// Note:        GET
// Params:      serviceScope ：可以为空（为空的时候返回的是所有的二级分类）

- (NSInteger)getServiceCategoryList:(NSString *)aServiceScope delegate:(id)aDelegate;


// Function:    修改默认车辆
// URL:         https://shop.bcgogo.com/api/vehicle/updateDefault
// Note:        POST
// Params:      *vehicleId ：车辆Id
- (NSInteger)updateDefaultVehicle:(NSString *)aVehicleId delegate:(id)aDelegate;

// Function:    根据一辆车vin获取车辆信息--------接口已废弃
// URL:         https://shop.bcgogo.com/api/vehicle/singleVehicle/vehicleVin/{vehicleVin}/userNo/{userNo}
// Note:        GET
// Params:      *vehicleVin :
//              *userNO:
- (NSInteger)getVehicleInfoWithVehicleVin:(NSString *)aVehicleVin withUserNo:(NSString *)aUserNo delegate:(id)aDelegate;

// Function:    获取后台车辆信息建议
// URL:         {URL}/api/vehicle/info/suggestion/{mobile}/{vehicleNo}
// Note:        POST
// Params:
//
-(NSInteger) getSuggestVehicleWithMobile:(NSString *)aMobile VehicleNo:(NSString *)aVehicleNo delegate:(id)aDelegate;

// Function:    获取加油站列表
// URL:         http://apis.juhe.cn/oil/local?key=c8adc805a4a1fdeb7a79798d03d06a46&dtype=json&lon=120.799379&lat=31.271484&r=10000&page=1
// Note:        GET
// Params:      lon,lat 经纬度
//              radius  搜索半径
//              page    搜索分页
-(NSInteger) getOilStationList:(CLLocationCoordinate2D)coordinate Radius:(NSInteger)radius Page:(NSInteger)page delegate:(id)aDelegate;

// Function:    获取聚合支持的违章查询地区列表
// URL:         {URL}/api/area/juhe/list
// Note:        GET
// Params:
-(NSInteger) getViolateJuheAreaList:(id) aDelegate;

// Function:    获取聚合支持的违章查询地区列表
// URL:         http://v.juhe.cn/wz/query
// Note:        GET
// Params:
-(NSInteger) getViolateJuheQuery:(NSString *)aVehicleNo veNoType:(NSString *)aVeNoType city:(NSString *)aCity engineNo:(NSString *)aEngineNo classNo:(NSString *)aClassNo registNo:(NSString *)aRegistNo delegate:(id) aDelegate;

@end


#pragma mark -
#pragma mark handling request complete / failure
@interface KKProtocolEngine(KKHTTPRequestDelegate)
// Called when a request starts, lets the delegate know via didStartSelector
//- (void)requestStart:(KKHTTPRequest *)request

// Called when a request completes successfully, lets the delegate know via didFinishSelector
- (void)requestFinished:(KKHTTPRequest *)request;

// Called when a request fails, and lets the delegate know via didFailSelector
- (void)requestFailed:(KKHTTPRequest *)request;

- (KKHTTPRequest*) httpRequestById:(NSInteger)aRequestID;

@end
