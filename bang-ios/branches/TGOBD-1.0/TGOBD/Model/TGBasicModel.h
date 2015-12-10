//
//  TGBasicModel.h
//  TGOBD
//
//  Created by Jiahai on 14-2-28.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CLLocation.h>

// Macro define
// 以下定义主要是为了json解析方便，请遵循
#define TGReservedKeyExt(key) key##__               // 成员名字与保留关键字冲突时
#define TGArrayFieldName(name, objectClassname)   name##__##objectClassname      // 定义array

typedef NS_ENUM(NSInteger, TGRespStatusCode) {
    rspStatus_Succeed = 1,
    rspStatus_Failed,
    rspStatus_Unknown = 10
};

@interface TGModelObject : NSObject

@end

@interface TGRspHeader : TGModelObject

@property(nonatomic, assign) NSInteger  status;
@property(nonatomic, copy) NSString     *message;
@property(nonatomic, assign) NSInteger  msgCode;
@end

/**
 *  导航前需配置的参数
 */
@interface TGModelRouteData : TGModelObject
@property (nonatomic, assign)   CLLocationCoordinate2D endCoordinate2D;
@property (nonatomic, copy)     NSString        *endTitle;
@property (nonatomic, copy)     NSString        *endAddress;
@end

#pragma mark - 通用
/**
 *  分页信息类
 */
@interface TGModelPagerInfo : TGModelObject
@property (nonatomic, assign)   NSInteger   currentPage;          // 当前分页位置  int
@property (nonatomic, assign)   NSInteger   pageSize;             // 分页大小    int

@property (nonatomic, assign)   BOOL        isLastPage;           //是否最后一页
@property (nonatomic, assign)   NSInteger   nextPage;
@property (nonatomic, assign)   NSInteger   totalRows;
@end

#pragma mark - 加油站相关

@interface TGModelOilPageInfo : TGModelObject
@property (nonatomic, assign) NSInteger pnums;
@property (nonatomic, copy) NSString *current;
@end

@interface TGModelOilPrice : TGModelObject

@property (nonatomic, copy) NSString *E90;
@property (nonatomic, copy) NSString *E93;
@property (nonatomic, copy) NSString *E97;
@property (nonatomic, copy) NSString *E0;

@end

@interface TGModelOilStation : TGModelObject
@property (nonatomic, copy) NSString *id;
@property (nonatomic, copy) NSString *name;
@property (nonatomic, copy) NSString *address;
@property (nonatomic, copy) NSString *type;
@property (nonatomic, copy) NSString *discount;
@property (nonatomic, copy) NSString *lon;
@property (nonatomic, copy) NSString *lat;
@property (nonatomic, strong) TGModelOilPrice *price;
@property (nonatomic, strong) NSMutableDictionary *gastprice;
@property (nonatomic, assign) NSInteger distance;
@property (nonatomic, assign) BOOL      isLoadToMap;            //是否已经在地图上显示
@end

@interface TGModelOilStationList : TGModelObject
@property (nonatomic, strong) NSMutableArray *TGArrayFieldName(data, TGModelOilStation);
@property (nonatomic, strong) TGModelOilPageInfo *pageinfo;
@end

#pragma mark - 违章相关

@interface TGModelViolateSearchCondition : TGModelObject
@property (nonatomic, copy) NSString *status;
@property (nonatomic, assign) BOOL engine;
@property (nonatomic, assign) NSInteger engineNo;
@property (nonatomic, assign) BOOL regist;
@property (nonatomic, assign) NSInteger registNo;
@property (nonatomic, assign) BOOL classa;
@property (nonatomic, assign) NSInteger classNo;
@property (nonatomic, copy) NSString *cityCode;
@property (nonatomic, copy) NSString *cityName;
@property (nonatomic, copy) NSString *provinceName;
@property (nonatomic, copy) NSString *provinceCode;
@end

@interface TGModelViolateCityInfo : TGModelObject
@property (nonatomic, copy) NSString *name;
@property (nonatomic, assign) NSInteger id;
@property (nonatomic, strong) NSMutableArray *TGArrayFieldName(children,TGModelViolateCityInfo);
@property (nonatomic, assign) NSInteger cityCode;
@property (nonatomic, copy) NSString *juheCityCode;
@property (nonatomic, copy) NSString *juheStatus;
@property (nonatomic, strong) TGModelViolateSearchCondition *TGArrayFieldName(juheViolateRegulationCitySearchCondition,TGModelViolateSearchCondition);

@end

@interface TGModelViolateDetailInfo : TGModelObject
@property(nonatomic, copy) NSString *date;
@property(nonatomic, copy) NSString *area;
@property(nonatomic, copy) NSString *act;
@property(nonatomic, copy) NSString *code;
@property(nonatomic, copy) NSString *fen;
@property(nonatomic, copy) NSString *money;
@end

@interface TGModelViolateResultInfo : TGModelObject
@property(nonatomic, copy) NSString *province;
@property(nonatomic, copy) NSString *city;
@property(nonatomic, copy) NSString *hphm;
@property(nonatomic, copy) NSString *hpzl;
@property(nonatomic, strong) NSMutableArray *TGArrayFieldName(lists,TGModelViolateDetailInfo);
@end

@interface TGModelViolateVehicleType : TGModelObject
@property (nonatomic, copy) NSString *car;
@property (nonatomic, copy) NSString *id;

@end


#pragma mark - 行车日志相关

@interface TGModelDriveRecordOilWear : TGModelObject
@property (nonatomic, assign) CGFloat worstOilWear;         //最差油耗
@property (nonatomic, assign) CGFloat bestOilWear;          //最好油耗
@property (nonatomic, assign) CGFloat totalOilWear;         //平均油耗

@end

@interface TGModelDriveRecordDetail : TGModelObject

@property(nonatomic, assign) long long  id;
@property(nonatomic, copy) NSString *appUserNo;
@property(nonatomic, assign) long long lastUpdateTime;
@property(nonatomic, copy) NSString *vehicleNo;
@property(nonatomic, assign) long long   startTime;
@property(nonatomic, copy) NSString *startLat;
@property(nonatomic, copy) NSString *startLon;
@property(nonatomic, copy) NSString *startPlace;
@property(nonatomic, assign) long long   endTime;
@property(nonatomic, copy) NSString *endLat;
@property(nonatomic, copy) NSString *endLon;
@property(nonatomic, copy) NSString *endPlace;
@property(nonatomic, assign) NSInteger   travelTime;
@property(nonatomic, assign) CGFloat distance;
@property(nonatomic, assign) CGFloat oilCost;               //油耗
@property(nonatomic, assign) CGFloat oilWear;               //平均油耗
@property(nonatomic, assign) CGFloat oilPrice;
@property(nonatomic, copy) NSString *oilKind;
@property(nonatomic, assign) CGFloat totalOilMoney;
@property(nonatomic, copy) NSString *placeNotes;
@property(nonatomic, copy) NSString *appPlatform;
@property(nonatomic, copy) NSString *status;
@end

typedef enum {
    DriveRecordPointType_Common = 0,        //途经点
    DriveRecordPointType_Start,             //起点
    DriveRecordPointType_End,               //终点
    DriveRecordPointType_CarPark            //车辆停靠点
}DriveRecordPointType;

@interface TGModelDriveRecordPoint : TGModelObject
@property (nonatomic, assign) double lat;
@property (nonatomic, assign) double lon;
@property (nonatomic, assign) long long   recordTime;
@property (nonatomic, assign) DriveRecordPointType  type;
@end

#pragma mark - 故障码相关

@interface TGModelDTCInfo : TGModelObject

@property(nonatomic, assign)    long long id;
@property(nonatomic, copy)      NSString *content;
@property(nonatomic, copy)      NSString *status;
@property(nonatomic, copy)      NSString *errorCode;
@property(nonatomic, copy)      NSString *category;
@property(nonatomic, copy)      NSString *appUserNo;
@property(nonatomic, assign)    long long reportTime;
@property(nonatomic, copy)      NSString *statusStr;
@property(nonatomic, assign)    long long obdId;
@property(nonatomic, assign)    long long appVehicleId;
@property(nonatomic, copy)      NSString *lastStatus;
@property(nonatomic, assign)    long long lastOperateTime;
@property(nonatomic, copy)      NSString *backgroundInfo;
@end


#pragma mark - 消息
/**
 *  消息记录
 */
@interface TGModelMessage : TGModelObject
@property (nonatomic, assign) long long id;                     // 唯一标识号      long
@property (nonatomic, copy) NSString *type;                     // 消息类型   String
@property (nonatomic, copy) NSString *content;                  // 内容描述   String
@property (nonatomic, copy) NSString *actionType;               // 操作类型   String
//                                                                          SEARCH_SHOP：跳转到店铺查询
//                                                                          SERVICE_DETAIL：跳转到具体的服务
//                                                                          CANCEL_ORDER：取消服务
//                                                                          ORDER_DETAIL：查看单据详情
//                                                                          COMMENT_SHOP：评价单据
@property (nonatomic, copy) NSString *params;                   // actionType所依赖的业务数据      String
@property (nonatomic, copy) NSString *time;
@property (nonatomic, copy) NSString *title;                    //消息标题

@end

#pragma mark - 账单相关
/**
 *  服务列表查询
 */
@interface TGModelOrderList : TGModelObject

@property (nonatomic, assign)   long long shopId;               //店铺ID
@property (nonatomic, copy)     NSString *shopName;             //店铺名字
@property (nonatomic, copy)     NSString *shopImageUrl;         //店铺图片url
@property (nonatomic, copy)     NSString *content;              //服务内容
@property (nonatomic, assign)   long long orderTime;            //下单时间
@property (nonatomic, copy)     NSString *status;               //账单状态
@property (nonatomic, assign)   long long orderId;              //订单ID
@property (nonatomic, copy)     NSString *orderType;            //订单类型
@property (nonatomic, copy)     NSString *orderTotal;           //账单消费金额

@end
/**
 *  单据消费字段
 */
@interface TGModelOrderItem : TGModelObject

@property (nonatomic, copy) NSString *content;                  // 内容        String
@property (nonatomic, copy) NSString *type;                     // 类型           String
@property (nonatomic, assign) double amount;                    // 金额         double

@end
/**
 *  结算信息
 */
@interface TGModelSettleAccounts : TGModelObject

@property (nonatomic, assign) CGFloat totalAmount;              // 单据总额      double
@property (nonatomic, assign) CGFloat settledAmount;            // 实收         double
@property (nonatomic, assign) CGFloat discount;                 // 优惠         double
@property (nonatomic, assign) CGFloat debt;                     // 挂账         double

@end
/**
 *  店铺详情
 */
@interface TGModelShopInfo : TGModelObject

@property (nonatomic, assign) long long id;                     //店铺ID
@property (nonatomic, copy) NSString *name;                     //店铺名称
@property (nonatomic, copy) NSString *address;                  //店铺地址
@property (nonatomic, copy) NSString *mobile;                   //店铺电话
@property (nonatomic, copy) NSString *accidentMobile;           //救援电话
@property (nonatomic, copy) NSString *landline;
@property (nonatomic, copy) NSString *smallImageUrl;            //店铺图片URL
@property (nonatomic, copy) NSString *coordinateLon;            //店铺地址经度
@property (nonatomic, copy) NSString *coordinateLat;            //店铺地址纬度

@end

@interface TGModelOrderDetail : TGModelObject

@property (nonatomic, assign)   long long id;                           //单据ID
@property (nonatomic, copy)     NSString *receiptNo;                    //单据号
@property (nonatomic, copy)     NSString *orderType;                    //单据类型
@property (nonatomic, copy)     NSString *vehicleNo;                    //车牌号
@property (nonatomic, copy)     NSString *vehicleBrandModelStr;         //车型
@property (nonatomic, assign)   long long orderTime;                    //单据时间
@property (nonatomic, copy)     NSString *customerName;                 //客户名
@property (nonatomic, strong)   TGModelSettleAccounts *settleAccounts;  //结算信息
@property (nonatomic, strong)   NSMutableArray *TGArrayFieldName(orderItems,TGModelOrderItem);            //具体消费信息
@property (nonatomic, copy)     NSString *vehicleMobile;                //联系号码

@end

#pragma mark - 汽车
/**
 *  汽车属性
 */
@interface TGModelVehicleInfo : TGModelObject

@property (nonatomic, assign) long long vehicleId;                //汽车ID
@property (nonatomic, copy) NSString *vehicleNo;                  //车牌号
@property (nonatomic, copy) NSString *vehicleModel;               //车型
@property (nonatomic, copy) NSString *vehicleBrand;               //品牌
@property (nonatomic, copy) NSString *oilPrice;                   //油价
@property (nonatomic, assign) NSInteger currentMileage;           //当前里程
@property (nonatomic, assign) NSInteger maintainPeriod;           //保养周期
@property (nonatomic, assign) NSInteger lastMaintainMileage;      //上次保养里程
@property (nonatomic, copy) NSString *nextMaintainTimeStr;        //下次保养时间
@property (nonatomic, copy) NSString *nextExamineTimeStr;         //下次年检时间
@property (nonatomic, assign) long long nextExamineTime;          //下次年检时间
@property (nonatomic, assign) long long nextMaintainTime;         //下次保养时间
@property (nonatomic, copy) NSString *coordinateLat;
@property (nonatomic, copy) NSString *coordinateLon;
@end

#pragma mark - 用户信息
/**
 *  用户信息
 */

@interface TGModelUserInfo : TGModelObject

@property (nonatomic, assign) long long id;                     //用户ID
@property (nonatomic, copy) NSString *password;
@property (nonatomic, copy) NSString *userNo;
@property (nonatomic, copy) NSString *mobile;                   //手机号
@property (nonatomic, copy) NSString *imei;                     //IMEI
@property (nonatomic, copy) NSString *gsmObdImeiMoblie;         //gsmOBDBindMobile

@end

#pragma mark - 登录信息

/**
 *  登录附带信息
 */
@interface TGModelLoginInfo : TGModelObject

@property (nonatomic, copy) NSString *platform;
@property (nonatomic, copy) NSString *platformVersion;
@property (nonatomic, copy) NSString *mobileModel;
@property (nonatomic, copy) NSString *appVersion;
@property (nonatomic, copy) NSString *imageVersion;

@end
