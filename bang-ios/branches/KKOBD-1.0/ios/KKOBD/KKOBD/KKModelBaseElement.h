//
//  KKModelBaseElement.h
//  KKOBD
//
//  Created by zhuyc on 13-8-7.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>

#pragma mark -
#pragma mark macro define

// Macro define
// 以下定义主要是为了json解析方便，请遵循
#define KKReservedKeyExt(key) key##__               // 成员名字与保留关键字冲突时
#define KKArrayFieldName(name, objectClassname)   name##__##objectClassname      // 定义array


#pragma mark -
#pragma mark 基本元素

typedef NS_ENUM(NSInteger, KKRespStatusCode) {
    eRsp_succeed = 0,
    eRsp_failed,
    eRsp_unknown = 10
};

// ================================================================================================
//  KKModelObject
//  Note:   base class, all our class should inherit from this class
// ================================================================================================
@interface KKModelObject : NSObject

@end

// ================================================================================================
//  KKModelRspHeader
//
// ================================================================================================
@interface KKModelRspHeader : KKModelObject
{
	NSInteger			_code;                      // 状态错误代码, KKRespStatusCode
    NSInteger           _msgCode;                   // 应答，应答或错误代码，取值详见主数据中的编码 KKError.h
	NSString 			*_request;					// 请求
	NSString			*_desc;						// 应答/错误描述	应答或错误描述
}
@property (nonatomic, assign) NSInteger code;
@property (nonatomic, assign) NSInteger msgCode;
@property (nonatomic, copy) NSString *request;
@property (nonatomic, copy) NSString *desc;

@end

// ================================================================================================
// ODB信息
// ================================================================================================

@interface KKModelObdDetailInfo : KKModelObject
@property (nonatomic ,copy) NSString *obdSN;
@property (nonatomic ,copy) NSString *obdId;
@property (nonatomic ,assign) BOOL isDefault;
@end


// ================================================================================================
// ODB绑定的车辆信息
// ================================================================================================
@interface KKModelObdInfo : KKModelObject
{
}
@property (nonatomic, assign) BOOL KKReservedKeyExt(default);
@property (nonatomic, copy) NSString *vehicleVin;        // 与当前ODB绑定的车辆唯一标识号
@property (nonatomic, copy) NSString *vehicleModelId;   // 与当前ODB绑定的车辆的品牌ID
@property (nonatomic, copy) NSString *vehicleBrandId;   // 与当前ODB绑定的车辆的车型ID

@end

// ================================================================================================
// app端系统配置信息
// ================================================================================================
@interface KKModelAppConfig : KKModelObject
{
}
@property (nonatomic, assign) NSInteger  obdReadInterval;       // app从obd读取数据的周期间隔，单位为毫秒 
@property (nonatomic, assign) NSInteger  serverReadInterval;    // app从服务端读取数据的周期间隔，单位为毫秒
@property (nonatomic, assign) NSInteger  mileageInformInterval; // app向服务端发送车辆里程数的公里数间隔，单位为公里
@property (nonatomic, copy) NSString     *customerServicePhone; // 客服电话
@property (nonatomic, assign) NSInteger  appVehicleErrorCodeWarnIntervals; //故障码弹跳间隔时间 ，单位为小时
@property (nonatomic, copy) NSString     *remainOilMassWarn;    // 油量警告界限

@end

// ================================================================================================
// 车辆基本信息
// ================================================================================================
@interface KKModelVehicleInfo : KKModelObject
{
}
@property (nonatomic, copy) NSString *vehicleVin;               // 车辆唯一标识号
@property (nonatomic, copy) NSString *vehicleNo;                // 车牌号
@property (nonatomic, copy) NSString *vehicleModel;             // 车型
@property (nonatomic, copy) NSString *vehicleModelId;           // 车型ID
@property (nonatomic, copy) NSString *vehicleBrand;             // 车辆品牌
@property (nonatomic, copy) NSString *vehicleBrandId;           // 车辆品牌ID

@end

// ================================================================================================
// 品牌或车型
// ================================================================================================
@interface KKModelBrandModel : KKModelObject
{
}
@property (nonatomic, copy) NSString *name;
@property (nonatomic, copy) NSString *id;

@end

// ================================================================================================
// 车辆车型信息
// ================================================================================================
@interface KKModelCarInfo : KKModelObject
@property (nonatomic ,copy) NSString *brandName;        //品牌
@property (nonatomic ,copy) NSString *modelName;        //车型
@property (nonatomic ,copy) NSString *modelId;
@property (nonatomic ,copy) NSString *brandId;

@end

// ================================================================================================
// 车辆详细信息
// ================================================================================================

@interface KKModelVehicleDetailInfo : KKModelObject<NSCopying>
@property (nonatomic ,copy) NSString    *status;
@property (nonatomic ,copy) NSString    *appUserId;
@property (nonatomic ,copy) NSString    *userNo;
@property (nonatomic ,copy) NSString    *mobile;
@property (nonatomic ,copy) NSString    *obdSN;
@property (nonatomic ,copy) NSString    *vehicleVin;
@property (nonatomic ,copy) NSString    *vehicleNo;
@property (nonatomic ,copy) NSString    *vehicleModel;
@property (nonatomic ,copy) NSString    *vehicleModelId;
@property (nonatomic ,copy) NSString    *vehicleBrand;
@property (nonatomic ,copy) NSString    *vehicleBrandId;
@property (nonatomic ,copy) NSString    *vehicleId;
@property (nonatomic ,copy) NSString    *nextMaintainMileage;
@property (nonatomic ,copy) NSString    *nextInsuranceTime;
@property (nonatomic ,copy) NSString    *nextExamineTime;
@property (nonatomic ,copy) NSString    *currentMileage;
@property (nonatomic ,copy) NSString    *email;
@property (nonatomic ,copy) NSString    *contact;
@property (nonatomic ,copy) NSString    *nextMaintainTime;
@property (nonatomic ,copy) NSString    *oilWear;
@property (nonatomic ,copy) NSString    *reportTime;
@property (nonatomic ,copy) NSString    *engineNo;
@property (nonatomic ,copy) NSString    *registNo;
@property (nonatomic ,copy) NSString    *instantOilWear;
@property (nonatomic ,copy) NSString    *oilWearPerHundred;
@property (nonatomic ,copy) NSString    *oilMass;
@property (nonatomic ,copy) NSString    *engineCoolantTemperature;
@property (nonatomic ,copy) NSString    *batteryVoltage;
@property (nonatomic ,copy) NSString    *isDefault;
@property (nonatomic ,copy) NSString    *recommendShopName;
@property (nonatomic ,copy) NSString    *recommendShopId;

@end

// ================================================================================================
// 店铺信息
// ================================================================================================

@interface KKModelShopInfo : KKModelObject

@property (nonatomic, copy) NSString   *id;                    // 店铺ID
@property (nonatomic, copy) NSString   *name;                  // 店铺名称       String
@property (nonatomic, copy) NSString   *serviceScope;          // 服务范围       String（服务范围以","分割）
@property (nonatomic, assign) CGFloat  distance;               // 距离（单位：公里）  double
@property (nonatomic, copy) NSString   *coordinate;            // 地理坐标（经纬度）String
@property (nonatomic, assign) CGFloat  totalScore;             // 评分总分               double
@property (nonatomic, copy) NSString   *bigImageUrl;           // 大图片地址             String
@property (nonatomic, copy) NSString   *smallImageUrl;         // 小图片地址             String
@property (nonatomic, copy) NSString   *address;               // 地址

@end

// ================================================================================================
// 分页信息
// ================================================================================================
@interface KKModelPagerInfo : KKModelObject

@property (nonatomic, assign) NSInteger  currentPage;          // 当前分页位置  int
@property (nonatomic, assign) NSInteger  pageSize;             // 分页大小    int

@end

// ================================================================================================
// 地区信息
// ================================================================================================
@interface KKModelAreaInfo : KKModelObject

@property (nonatomic, copy) NSString   *id;                     // 主键                          long
@property (nonatomic, copy) NSString   *name;                   // 地名                          String
@property (nonatomic, copy) NSString   *cityCode;               // 地图数据中的城市编号             String

@end


// ================================================================================================
// 会员卡中购买的几次服务列表
// ================================================================================================
@interface KKModelMemberService : KKModelObject

@property (nonatomic, copy) NSString   *serviceId;              // 服务ID（后台数据主键）long
@property (nonatomic, copy) NSString   *consumeType;            // 消费类型        String
@property (nonatomic, assign) NSInteger  times;                 // 剩余次数           int
@property (nonatomic, copy) NSString    *timesStr;              // 剩余次数         string
@property (nonatomic, assign) NSInteger  deadline;              // 有效期          long
@property (nonatomic, copy) NSString   *deadlineStr;            // 有效期          string
@property (nonatomic, copy) NSString   *serviceName;            // 服务名称       String
@property (nonatomic, copy) NSString   *vehicles;               // 限定服务车辆        String
@property (nonatomic, copy) NSString   *status;                 // 状态             String
@property (nonatomic, assign) BOOL  expired;                    // 是否过期     boolean

@end

// ================================================================================================
// 会员信息
// ================================================================================================
@interface KKModelMemberInfo : KKModelObject
@property (nonatomic, copy) NSString    *memberNo;
@property (nonatomic, copy) NSString   *type;                   // 会员类型                        String
@property (nonatomic, copy) NSString   *status;                 // 状态                          String
@property (nonatomic, assign) CGFloat   balance;                // 余额                         double
@property (nonatomic, assign) CGFloat   memberConsumeTotal;     // 会员卡累计消费    double
@property (nonatomic, assign) CGFloat   accumulatePoints;       // 会员积分            int
@property (nonatomic, assign) CGFloat   memberDiscount;         // 会员卡折扣            double
@property (nonatomic, assign) CGFloat   serviceDiscount;        // 服务折扣             double
@property (nonatomic, assign) CGFloat   materialDiscount;       // 商品折扣            double
@property (nonatomic, assign) CGFloat   joinDate;               // 办理时间       long  unixtime
@property (nonatomic, assign) CGFloat   deadline;               // 有效期          long  unixtime
@property (nonatomic, retain) NSMutableArray *KKArrayFieldName(memberServiceList, KKModelMemberService); // 会员卡中购买的几次服务列表

@end

// ================================================================================================
// 店铺详情服务范围
// ================================================================================================
@interface KKModelShopServiceScope : KKModelObject
@property (nonatomic ,copy)NSString *serviceCategoryName;
@property (nonatomic ,copy)NSString *shopId;
@property (nonatomic ,copy)NSString *serviceCategoryId;
@property (nonatomic ,copy)NSString *deleted;
@property (nonatomic ,copy)NSString *id;
@property (nonatomic ,copy)NSString *idStr;

@end

// ================================================================================================
// 店铺详细信息
// ================================================================================================
@interface KKModelShopDetail : KKModelObject

@property (nonatomic, copy) NSString   *id;                    // 店铺ID
@property (nonatomic, copy) NSString   *name;                  // 店铺名称       String
@property (nonatomic, copy) NSString   *serviceScope;          // 服务范围       String（服务范围以","分割）
@property (nonatomic, assign) CGFloat  distance;               // 距离（单位：公里）  double
@property (nonatomic, copy) NSString   *coordinate;            // 地理坐标（经纬度）String
@property (nonatomic, assign) CGFloat  totalScore;             // 评分总分               double
@property (nonatomic, copy) NSString   *imageUrl;              // 图片地址             String

@property (nonatomic, copy) NSString   *mobile;                 // 电话             String
@property (nonatomic, copy) NSString   *landLine;               // 固话             string
@property (nonatomic, copy) NSString   *address;                // 地址                 String
@property (nonatomic, retain) KKModelMemberInfo *memberInfo;    // 会员信息
@property (nonatomic, retain)NSMutableArray *KKArrayFieldName(productCategoryList,KKModelShopServiceScope);//服务范围Array
@end


// ================================================================================================
// 字典错误码
// ================================================================================================
@interface KKModelFaultCodeInfo : KKModelObject

@property (nonatomic, copy) NSString *faultCode;                // 故障码    String
@property (nonatomic, copy) NSString *description;              // 故障描述    String

@end


// ================================================================================================
// 消息
// ================================================================================================
@interface KKModelMessage : KKModelObject

@property (nonatomic, copy) NSString *id;                       // 唯一标识号      long
@property (nonatomic, copy) NSString *type;                     // 消息类型   String
@property (nonatomic, copy) NSString *content;                  // 内容描述   String
@property (nonatomic, copy) NSString *actionType;               // 操作类型   String
//                                                                          SEARCH_SHOP：跳转到店铺查询
//                                                                          SERVICE_DETAIL：跳转到具体的服务
//                                                                          CANCEL_ORDER：取消服务
//                                                                          ORDER_DETAIL：查看单据详情
//                                                                          COMMENT_SHOP：评价单据
@property (nonatomic, copy) NSString *params;                   // actionType所依赖的业务数据      String
@property (nonatomic, copy) NSString *title;                    // 消息名称
@end

// ================================================================================================
// 服务信息
// ================================================================================================
@interface KKModelService : KKModelObject

@property (nonatomic, copy) NSString *shopId;                   // 店铺ID                long
@property (nonatomic, copy) NSString *shopName;                 // 店面名称               String
@property (nonatomic, copy) NSString *shopImageUrl;             // 店面图片地址           String
@property (nonatomic, copy) NSString *content;                  // 服务内容              String
@property (nonatomic, assign) double orderTime;                 // 服务时间              long
@property (nonatomic, copy) NSString *status;                   // 服务状态              String
@property (nonatomic, copy) NSString *orderId;                  // 单据ID               long
@property (nonatomic, copy) NSString *orderType;                // 单据类型              String

@end


// ================================================================================================
// 单据项
// ================================================================================================
@interface KKModelOrderItem: KKModelObject

@property (nonatomic, copy) NSString *content;                  // 内容        String
@property (nonatomic, copy) NSString *type;                     // 类型           String
@property (nonatomic, assign) NSInteger amount;                // 金额         double

@end

// ================================================================================================
// 结算信息
// ================================================================================================
@interface KKModelSettleAccounts: KKModelObject

@property (nonatomic, assign) CGFloat totalAmount;              // 单据总额      double
@property (nonatomic, assign) CGFloat settledAmount;            // 实收         double
@property (nonatomic, assign) CGFloat discount;                 // 优惠         double
@property (nonatomic, assign) CGFloat debt;                     // 挂账         double

@end

// ================================================================================================
// 评价信息
// ================================================================================================
@interface KKModelComment: KKModelObject

@property (nonatomic, assign) NSInteger commentScore;           // 评分            int
@property (nonatomic, copy) NSString *commentContent;           // 评论            String

@end

// ================================================================================================
// 服务单据分页
// ================================================================================================
@interface KKModelServiceHistoryPager : KKModelObject
@property (nonatomic, assign) NSInteger currentPage;
@property (nonatomic, assign) NSInteger nextPage;
@property (nonatomic, assign) BOOL isLastPage;
@end

// ================================================================================================
// 服务单据详情
// ================================================================================================
@interface KKModelserviceDetail: KKModelObject

@property (nonatomic, copy) NSString *id;                       // 单据ID            long
@property (nonatomic, copy) NSString *receiptNo;                // 单据号             long
@property (nonatomic, copy) NSString *status;                   // 状态               String
@property (nonatomic, copy) NSString *vehicleNo;                // 车牌号             String
@property (nonatomic, copy) NSString *customerName;             // 客户名             String
@property (nonatomic, copy) NSString *shopId;                   // 店面ID             long
@property (nonatomic, copy) NSString *shopName;                 // 店面名称            String
@property (nonatomic, copy) NSString *shopImageUrl;
@property (nonatomic, assign) CGFloat shopTotalScore;           // 店面总评分          double
@property (nonatomic, copy) NSString *serviceType;              // 服务类型            洗车、保养、保险、验车、维修 其中一种 String
@property (nonatomic, copy) NSString *orderType;                // 单据类型            String 洗车美容单、施工单
@property (nonatomic, retain) NSMutableArray *KKArrayFieldName(orderItems, KKModelOrderItem);   // 单据项列表
@property (nonatomic, retain) KKModelSettleAccounts *settleAccounts;                            // 结算信息
@property (nonatomic, retain) KKModelComment *comment;          // 评价信息
@property (nonatomic, assign) CGFloat orderTime;                // 单据时间             long
@property (nonatomic, copy) NSString *actionType;               // 操作类型             String
@property (nonatomic, copy) NSString *vehicleBrandModelStr;     // 车型                 string
@property (nonatomic, copy) NSString *vehicleContact;           // 联系人                string
@property (nonatomic, copy) NSString *vehicleMobile;            // 联系电话               string
@property (nonatomic, copy) NSString *remark;                   // 备注                   string
@end


// ================================================================================================
// 个人资料
// ================================================================================================
@interface KKModelUserInfo: KKModelObject

@property (nonatomic, copy) NSString *userNo;                   // 用户账号                        String
@property (nonatomic, copy) NSString *mobile;                   // 手机号                          String
@property (nonatomic, copy) NSString *name;                     // 用户名字                        String

@end


// ================================================================================================
// 服务范围类别
// ================================================================================================

@interface KKModelServiceCategory : KKModelObject

@property (nonatomic ,copy) NSString *id;            //主键                   long
@property (nonatomic ,copy) NSString *name;          //服务名字                String
@property (nonatomic ,copy) NSString *parentId;      //该服务的上一级id         long
@property (nonatomic ,copy) NSString *categoryType;  //FIRST_CATEGORY 或 SECOND_CATEGORY
@property (nonatomic ,copy) NSString *seviceScope;

@end

// ================================================================================================
// 平台信息
// ================================================================================================

@interface KKModelPlatform : KKModelObject
@property (nonatomic ,copy) NSString *platform;
@property (nonatomic ,copy) NSString *platformVersion;
@property (nonatomic ,copy) NSString *mobileModel;
@property (nonatomic ,copy) NSString *appVersion;
@property (nonatomic ,copy) NSString *imageVersion;

@end


@interface KKModelSuggestionVehicle : KKModelObject

@property (nonatomic ,copy) NSString *shopId;
@property (nonatomic ,copy) NSString *vehicleNo;
@property (nonatomic, retain) KKModelCarInfo *brandModel;
@end

// ================================================================================================
// 加油站信息
// ================================================================================================
@interface KKModelOilPageInfo : KKModelObject
@property (nonatomic, assign) NSInteger pnums;
@property (nonatomic, copy) NSString *current;
@end

@interface KKModelOilStation : KKModelObject
@property (nonatomic, copy) NSString *id;
@property (nonatomic, copy) NSString *name;
@property (nonatomic, copy) NSString *address;
@property (nonatomic, copy) NSString *type;
@property (nonatomic, copy) NSString *discount;
@property (nonatomic, copy) NSString *lon;
@property (nonatomic, copy) NSString *lat;
@property (nonatomic, copy) NSString *E90;
@property (nonatomic, copy) NSString *E93;
@property (nonatomic, copy) NSString *E97;
@property (nonatomic, copy) NSString *E0;
@property (nonatomic, assign) NSInteger distance;
@end

@interface KKModelOilStationList : KKModelObject
@property (nonatomic, retain) NSMutableArray *KKArrayFieldName(data, KKModelOilStation);
@property (nonatomic, retain) KKModelOilPageInfo *pageinfo;
@end


// ================================================================================================
// 违章信息
// ================================================================================================
@interface KKViolateSearchCondition : KKModelObject<NSCoding>
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

@interface KKViolateCityInfo : KKModelObject<NSCoding>
@property (nonatomic, copy) NSString *name;
@property (nonatomic, assign) NSInteger id;
@property (nonatomic, retain) NSMutableArray *KKArrayFieldName(children,KKViolateCityInfo);
@property (nonatomic, assign) NSInteger cityCode;
@property (nonatomic, copy) NSString *juheCityCode;
@property (nonatomic, copy) NSString *juheStatus;
@property (nonatomic, retain) KKViolateSearchCondition *KKArrayFieldName(juheViolateRegulationCitySearchCondition,KKViolateSearchCondition);

@end

@interface KKViolateDetailInfo : KKModelObject
@property(nonatomic, copy) NSString *date;
@property(nonatomic, copy) NSString *area;
@property(nonatomic, copy) NSString *act;
@property(nonatomic, copy) NSString *code;
@property(nonatomic, copy) NSString *fen;
@property(nonatomic, copy) NSString *money;
@end

@interface KKViolateVehicleType : KKModelObject
@property (nonatomic, copy) NSString *car;
@property (nonatomic, copy) NSString *id;

@end


