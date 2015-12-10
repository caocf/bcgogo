//
//  XSHTTPRequest.h
//  
//
//  Created by Jiahai on 14-2-26.
//
//

#import <Foundation/Foundation.h>
#import "AFNetworking.h"
#import <CoreLocation/CLLocation.h>
#import "TGBasicModel.h"

@class TGHTTPRequestOperationManager;
//typedef AFHTTPRequestOperationManager TGHTTPRequestManager;

typedef NS_ENUM(NSInteger, TGHTTPRequest_ApiID){
    apiID_user_login            =   1,          //用户登录
    apiID_user_register,                        //用户注册
    apiID_user_validateIMEI,                    //校验IMEI号
    apiID_change_password,                      //修改密码
    apiID_user_logout,                          //用户注销
    apiID_user_forgetPassword,                  //找回密码
    
    apiID_driveRecord_getVehicle,               //GSM卡手机用户获取车辆信息（包含车辆位置）
    apiID_driveRecord_downloadList,             //下载行车日志列表
    apiID_driveRecord_downloadDetail,           //下载行车日志详情
    
    apiID_oilStation_getList,                   //获取加油站列表
    apiID_violate_getCityList,                  //获取支持违章查询的城市列表
    apiID_violate_juheQuery,                    //从聚合获取违章信息
    
    apiID_dtc_getList,                          //获取故障码列表
    apiID_dtc_operate,                          //操作故障码
    
    apiID_get_newMessage,                       //消息轮询
    apiID_order_online,                         //在线预约
    apiID_get_orderList,                        //获取账单列表
    apiID_get_orderDetail,                      //获取账单详情
    apiID_update_vehicle,                       //更新汽车信息
    apiID_check_newVersion,                     //版本检测更新
    apiID_Shop_serviceCategoty,
};

typedef enum {
    TGRequestType_Get   = 1,
    TGRequestType_Post,
    TGRequestType_Put
}TGRequestType;

typedef struct {
    TGHTTPRequest_ApiID                 apiID;
    __unsafe_unretained NSString        *urlStr;
}TGApiIDDictItem;


@interface TGHTTPRequestEngine : NSObject
{
    TGHTTPRequestOperationManager   *_requestManager;
}

+(TGHTTPRequestEngine *)sharedInstance;

- (void)cancleAllRequests;
- (void)cancleRequestWithViewControllerIdentifier:(id)aIdentifier;

#pragma mark - 登录/注册

- (void)userLogin:(NSString *)userNo password:(NSString *)password viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;

- (void)userValidateIMEI:(NSString *)IMEI mobile:(NSString *)mobile password:(NSString *)password viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;
/**
 *  注册用户
 *
 *  @param mobile              手机号码
 *  @param password            密码
 *  @param imei                imei号
 *  @param oilPrice            油价
 *  @param currentMileage      当前里程
 *  @param maintainPeriod      保养周期
 *  @param lastMaintainMileage 上次保养里程
 *  @param nextMaintainTime    下次保养时间
 *  @param nextExamineTime     下次年检时间
 *  @param loginInfo           登录附带信息
 *  @param aSuccess
 *  @param aFailure
 */
- (void)userRegister:(NSString *)mobile
            password:(NSString *)password
                imei:(NSString *)imei
            oilPrice:(NSString *)oilPrice
      currentMileage:(NSString *)currentMileage
      maintainPeriod:(NSString *)maintainPeriod
 lastMaintainMileage:(NSString *)lastMaintainMileage
    nextMaintainTime:(long long)nextMaintainTime
     nextExamineTime:(long long)nextExamineTime
           loginInfo:(TGModelLoginInfo *)loginInfo
viewControllerIdentifier:(id)aIdentifier
             success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess
             failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;

/**
 *  找回密码
 *
 *  @param mobile      手机号
 *  @param aIdentifier aIdentifier description
 *  @param aSuccess    aSuccess description
 *  @param aFailure    aFailure description
 */
- (void)userForgetPassword:(NSString *)mobile
  viewControllerIdentifier:(id)aIdentifier
                   success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess
                   failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;

#pragma mark - 行车日志相关

- (void)driveRecordGetVehicle:(id)aIdentifier
                      success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess
                      failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;

- (void)driveRecordDownLoadList:(long long)startTime
                        endTime:(long long)endTime
       viewControllerIdentifier:(id)aIdentifier
                        success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess
                        failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;

- (void)driveRecordDownLoadDetail:(long long)aID
         viewControllerIdentifier:(id)aIdentifier
                          success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess
                          failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;

#pragma mark - 加油站相关
/**
 *  获取加油站列表
 *
 *  @param coordinate 当前位置坐标
 *  @param radius     获取周边加油站的半径
 *  @param pageNO     当前页数
 */
- (void)oilStationGetList:(CLLocationCoordinate2D)coordinate radius:(NSInteger)radius pageNo:(NSInteger)pageNo viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;

#pragma mark - 违章相关
/**
 *  获取违章查询城市列表
 */
- (void)violateGetCityList:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;

/**
 *  从聚合获取违章结果
 *
 *  @param aVehicleNo  车牌号
 *  @param aVeNoType   车辆类型：小型车、中型车等
 *  @param aCity       城市
 *  @param aEngineNo   发动机号
 *  @param aClassNo    车架号
 *  @param aRegistNo   行驶证号
 */
- (void)violateJuheQuery:(NSString *)aVehicleNo veNoType:(NSString *)aVeNoType city:(NSString *)aCity engineNo:(NSString *)aEngineNo classNo:(NSString *)aClassNo registNo:(NSString *)aRegistNo viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;

#pragma mark - 故障码相关
/**
 *  下载故障码列表
 *
 *  @param aStatus     故障状态：（UNTREATED|FIXED|IGNORED|DELETED）多个状态逗号隔开
 *  @param aPageNo     当前页数
 *  @param aPageSize   每页包含的数据条数
 */
- (void)dtcGetList:(NSString *)aStatus pageNo:(NSInteger)aPageNo pageSize:(NSInteger)aPageSize viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;
/**
 *  操作故障码
 *
 *  @param aID         故障码ID
 *  @param errorCode   故障码
 *  @param oldStatus   操作之前的状态
 *  @param newStatus   操作后的状态
 *  @param vehicleId   车辆的ID
 */
- (void)dtcOperate:(long long)aID errorCode:(NSString *)errorCode oldStatus:(NSString *)oldStatus newStatus:(NSString *)newStatus vehicleId:(long long)vehicleId viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;


#pragma mark - 个人资料
/**
 *  用户修改密码
 *
 *  @param userNo      用户账户
 *  @param oldPwd      旧密码
 *  @param newPwd      新密码
 */
- (void)changePassword:(NSString *)userNo oldPwd:(NSString *)oldPwd newPwd:(NSString *)newPwd viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;
/**
 *  用户注销
 *
 *  @param userNo      用户名
 */
- (void)logout:(NSString *)userNo viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;

#pragma mark - 消息
/**
 *  获取用户最新消息
 *
 *  @param userNo      用户名
 *  @param type        消息类型
 */
- (void)getNewMessage:(NSString *)userNo types:(NSString *)types viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;

#pragma mark - 账单相关
/**
 * 在线预约
 *
 *  @param userNo            用户名
 *  @param serviceCategoryId 服务Id
 *  @param appointTime       预约时间
 *  @param mobile            联系方式
 *  @param contact           联系人
 *  @param shopId            预约商店名称
 *  @param vehicleNo         车牌号
 *  @param remark            备注
 *  @param faultInfoItems    故障信息
 */
- (void)orderOnline:(NSString *)userNo serviceCategoryId:(long long)serviceCategoryId appointTime:(long long)appointTime mobile:(NSString *)mobile contact:(NSString *)contact shopId:(long long)shopId vehicleNo:(NSString *)vehicleNo remark:(NSString *)remark faultInfoItems:(NSArray *)faultInfoItems viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;

/**
 *  获取我的账单列表
 *
 *  @param userNo      用户名
 *  @param status      账单状态
 *  @param pageNo      当前分页
 *  @param pageSize    分页大小
 */
- (void)getOrderList:(NSString *)userNo status:(NSString *)status pageNo:(NSInteger)pageNo pageSize:(NSInteger)pageSize viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;
/**
 *  获取账单详情
 *
 *  @param orderId     账单ID
 */
- (void)getOrderDetail:(long long)orderId serviceScope:(NSString *)serviceScope viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;

#pragma mark - 更新车辆信息
/**
 *  更新汽车信息
 *
 *  @param oilPrice            油价
 *  @param currentMileage      当前里程
 *  @param maintainPeriod      保养周期
 *  @param lastMaintainMileage 上次保养里程
 *  @param nextMaintainTime    下次保养里程
 *  @param nextExamineTime     下次验车时间
 */
- (void)updateVehicleInfo:(NSString *)oilPrice
           currentMileage:(NSString *)currentMileage
           maintainPeriod:(NSString *)maintainPeriod
      lastMaintainMileage:(NSString *)lastMaintainMileage
         nextMaintainTime:(long long)nextMaintainTime
          nextExamineTime:(long long)nextExamineTime
 viewControllerIdentifier:(id)aIdentifier
                  success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess
                  failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;

#pragma mark - 版本检查
/**
 *  版本检查
 *
 *  @param platform             用户手机系统平台类型(ANDROID,IOS)
 *  @param appVersion           APP版本号
 *  @param platformVersion      用户手机系统平台版本
 *  @param mobileModel          用户手机型号
 */
- (void)checkNewVersion:(NSString *)platform appVersion:(NSString *)appVersion platformVersion:(NSString *)platformVersion mobileModel:(NSString *)mobileModel viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;

@end
