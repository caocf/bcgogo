//
//  KKModelBaseElement.m
//  KKOBD
//
//  Created by zhuyc on 13-8-7.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKModelBaseElement.h"

// ================================================================================================
//  KKModelObject
//  Note:   base class, all our class should inherit from this class
// ================================================================================================
@implementation KKModelObject 

@end

// ================================================================================================
//  KKModelRspHeader
// ================================================================================================
@implementation KKModelRspHeader

@synthesize code = _code;
@synthesize request = _request;
@synthesize msgCode = _msgCode;
@synthesize desc = _desc;

-(void) dealloc
{
	self.request = nil;
	self.desc = nil;
	[super dealloc];
}

@end


// ================================================================================================
// ODB信息
// ================================================================================================
@implementation KKModelObdDetailInfo

- (void)dealloc
{
    self.obdSN = nil;
    self.obdId = nil;
    [super dealloc];
}

@end

// ================================================================================================
// ODB绑定的车辆信息
// ================================================================================================
@implementation KKModelObdInfo
@synthesize KKReservedKeyExt(default), vehicleVin, vehicleModelId, vehicleBrandId;

-(void) dealloc
{
    self.vehicleVin = nil;
    self.vehicleModelId = nil;
    self.vehicleBrandId = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// app端系统配置信息
// ================================================================================================
@implementation KKModelAppConfig 
@synthesize obdReadInterval, serverReadInterval, mileageInformInterval, customerServicePhone,imageVersion;

-(void) dealloc
{
    self.customerServicePhone = nil;
    self.remainOilMassWarn = nil;
    self.imageVersion = nil;
    [super dealloc];
}

@end

// ================================================================================================
// app端用户配置信息
// ================================================================================================
@implementation KKModelAppUserConfig

-(id) copyWithZone:(NSZone *)zone
{
    KKModelAppUserConfig *newUC = [[KKModelAppUserConfig allocWithZone:zone] init];
    newUC.first_drive_log_time = self.first_drive_log_time;
    newUC.oil_price = self.oil_price;
    newUC.oil_kind = self.oil_kind;
    return newUC;
}

-(void) dealloc
{
    self.oil_kind = nil;
    self.oil_price = nil;
    self.first_drive_log_time = nil;
    [super dealloc];
}

@end

// ================================================================================================
// 车辆基本信息
// ================================================================================================
@implementation KKModelVehicleInfo

-(void) dealloc
{
    self.vehicleVin = nil;
    self.vehicleBrand = nil;
    self.vehicleBrandId = nil;
    self.vehicleModel = nil;
    self.vehicleBrandId = nil;
    self.vehicleNo = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// 品牌或车型
// ================================================================================================
@implementation KKModelBrandModel
-(void) dealloc
{
    self.name = nil;
    self.id = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// 车辆车型信息
// ================================================================================================
@implementation KKModelCarInfo
- (void)dealloc
{
    self.brandName = nil;
    self.brandId = nil;
    self.modelName = nil;
    self.modelId = nil;
    [super dealloc];
}

@end

@implementation KKModelVehicleDetailInfo

-(id) copyWithZone:(NSZone *)zone
{
    KKModelVehicleDetailInfo *newInfo = [[KKModelVehicleDetailInfo allocWithZone:zone] init];
    newInfo.localId = self.localId;
    newInfo.status = self.status;
    newInfo.appUserId = self.appUserId;
    newInfo.userNo = self.userNo;
    newInfo.mobile=self.mobile;
    newInfo.obdSN=self.obdSN;
    newInfo.vehicleVin=self.vehicleVin;
    newInfo.vehicleNo = self.vehicleNo;
    newInfo.vehicleModel=self.vehicleModel;
    newInfo.vehicleModelId = self.vehicleModelId;
    newInfo.vehicleBrand=self.vehicleBrand;
    newInfo.vehicleBrandId = self.vehicleBrandId;
    newInfo.vehicleId=self.vehicleId;
    newInfo.nextMaintainMileage=self.nextMaintainMileage;
    newInfo.nextInsuranceTime=self.nextInsuranceTime;
    newInfo.nextExamineTime=self.nextExamineTime;
    newInfo.currentMileage=self.currentMileage;
    newInfo.email=self.email;
    newInfo.contact=self.contact;
    newInfo.nextMaintainTime=self.nextMaintainTime;
    newInfo.oilWear=self.oilWear;
    newInfo.reportTime=self.reportTime;
    newInfo.engineNo=self.engineNo;
    newInfo.registNo = self.registNo;
    newInfo.instantOilWear=self.instantOilWear;
    newInfo.oilWearPerHundred=self.oilWearPerHundred;
    newInfo.oilMass=self.oilMass;
    newInfo.engineCoolantTemperature = self.engineCoolantTemperature;
    newInfo.batteryVoltage=self.batteryVoltage;
    newInfo.isDefault=self.isDefault;
    newInfo.recommendShopName=self.recommendShopName;
    newInfo.recommendShopId=self.recommendShopId;
    return newInfo;
}

- (void)dealloc
{
    self.status = nil;
    self.appUserId = nil;
    self.userNo = nil;
    self.mobile = nil;
    self.obdSN = nil;
    self.vehicleVin = nil;
    self.vehicleNo = nil;
    self.vehicleModel = nil;
    self.vehicleModelId = nil;
    self.vehicleBrand = nil;
    self.vehicleBrandId = nil;
    self.vehicleId = nil;
    self.nextMaintainMileage = nil;
    self.nextInsuranceTime = nil;
    self.nextExamineTime = nil;
    self.currentMileage = nil;
    self.email = nil;
    self.contact = nil;
    self.nextMaintainTime = nil;
    self.oilWear = nil;
    self.reportTime = nil;
    self.engineNo = nil;
    self.registNo = nil;
    self.instantOilWear = nil;
    self.oilWearPerHundred = nil;
    self.oilMass = nil;
    self.engineCoolantTemperature = nil;
    self.batteryVoltage = nil;
    self.isDefault = nil;
    self.recommendShopName = nil;
    self.recommendShopId = nil;
    [super dealloc];
}

@end

// ================================================================================================
// 店铺信息
// ================================================================================================

@implementation KKModelShopInfo

- (void)dealloc
{
    self.id = nil;
    self.name = nil;
    self.serviceScope = nil;
    self.coordinate = nil;
    self.smallImageUrl = nil;
    self.bigImageUrl = nil;
    self.address = nil;
    
    [super dealloc];
}

@end


// ================================================================================================
// 分页信息
// ================================================================================================

@implementation KKModelPagerInfo

@end


// ================================================================================================
// 地区信息
// ================================================================================================

@implementation KKModelAreaInfo

- (void)dealloc
{
    self.id = nil;
    self.name = nil;
    self.cityCode = nil;
    
    [super dealloc];
}

@end



// ================================================================================================
// 会员卡中购买的几次服务列表
// ================================================================================================
@implementation KKModelMemberService 

- (void)dealloc
{
    self.serviceId = nil;
    self.consumeType = nil;
    self.serviceName = nil;
    self.vehicles = nil;
    self.status = nil;
    self.deadlineStr = nil;
    self.timesStr = nil;
    [super dealloc];
}

@end

// ================================================================================================
// 会员信息
// ================================================================================================
@implementation KKModelMemberInfo

- (void)dealloc
{
    self.memberNo = nil;
    self.type = nil;
    self.status = nil;
    self.KKArrayFieldName(memberServiceList, KKModelMemberService) = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// 店铺详情服务范围
// ================================================================================================
@implementation KKModelShopServiceScope

- (void)dealloc
{
    self.serviceCategoryName = nil;
    self.shopId = nil;
    self.serviceCategoryId = nil;
    self.deleted = nil;
    self.id = nil;
    self.idStr = nil;
    [super dealloc];
}

@end

// ================================================================================================
// 店铺详细信息
// ================================================================================================
@implementation KKModelShopDetail 

- (void)dealloc
{
    self.id = nil;
    self.name = nil;
    self.serviceScope = nil;
    self.coordinate = nil;
    self.imageUrl = nil;
    self.mobile = nil;
    self.landLine = nil;
    self.address = nil;
    self.memberInfo = nil;
    
    [super dealloc];
}

@end


// ================================================================================================
// 字典错误码
// ================================================================================================
@implementation KKModelFaultCodeInfo

- (void)dealloc
{
    self.faultCode = nil;
    self.description = nil;
    self.category = nil;
    self.backgroundInfo = nil;
    
    [super dealloc];
}

@end

@implementation KKModelFaultCodeGetInfo

-(void) dealloc
{
    self.errorCode = nil;
    self.content = nil;
    self.status = nil;
    self.statusStr = nil;
    self.lastOperateTime = nil;
    [super dealloc];
}

@end

// ================================================================================================
// 消息
// ================================================================================================
@implementation KKModelMessage

- (void)dealloc
{
    self.id = nil;
    self.type = nil;
    self.content = nil;
    self.actionType = nil;
    self.params = nil;
    self.title = nil;
    [super dealloc];
}

@end

// ================================================================================================
// 服务信息
// ================================================================================================
@implementation KKModelService

- (void)dealloc
{
    self.shopId = nil;
    self.shopName = nil;
    self.shopImageUrl = nil;
    self.content = nil;
    self.status = nil;
    self.orderId = nil;
    self.orderType = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// 单据项
// ================================================================================================
@implementation KKModelOrderItem
- (void)dealloc
{
    self.content = nil;
    self.type = nil;
    [super dealloc];
}
@end

// ================================================================================================
// 结算信息
// ================================================================================================
@implementation KKModelSettleAccounts: KKModelObject

@end

// ================================================================================================
// 评价信息
// ================================================================================================
@implementation KKModelComment

- (void)dealloc
{
    self.commentContent = nil;
    self.commentatorName = nil;
    self.commentTimeStr = nil;
    
    [super dealloc];
}

@end
// ================================================================================================
// 服务单据分页
// ================================================================================================
@implementation KKModelServiceHistoryPager: KKModelObject

@end

// ================================================================================================
// 服务单据详情
// ================================================================================================
@implementation KKModelserviceDetail: KKModelObject

- (void)dealloc
{
    self.id = nil;
    self.receiptNo = nil;
    self.status = nil;
    self.vehicleNo = nil;
    self.customerName = nil;
    self.shopId = nil;
    self.shopName = nil;
    self.shopImageUrl = nil;
    self.serviceType = nil;
    self.orderType = nil;
    self.KKArrayFieldName(orderItems, KKModelOrderItem) = nil;
    self.settleAccounts = nil;
    self.comment = nil;
    self.actionType = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// 个人资料
// ================================================================================================
@implementation KKModelUserInfo

- (void)dealloc
{
    self.userNo = nil;
    self.mobile = nil;
    self.name = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// 服务范围类别
// ================================================================================================

@implementation KKModelServiceCategory

- (void)dealloc
{
    self.id = nil;
    self.name = nil;
    self.parentId = nil;
    self.categoryType = nil;
    self.seviceScope = nil;
    [super dealloc];
}

@end

// ================================================================================================
// 平台信息
// ================================================================================================

@implementation KKModelPlatform

- (void)dealloc
{
    self.platform = nil;
    self.platformVersion = nil;
    self.mobileModel = nil;
    self.appVersion = nil;
    self.imageVersion = nil;
    [super dealloc];
}

@end

@implementation KKModelSuggestionVehicle

-(void) dealloc
{
    self.shopId = nil;
    self.vehicleNo = nil;
    self.brandModel = nil;
    [super dealloc];
}

@end


// ================================================================================================
// 加油站信息
// ================================================================================================
@implementation KKModelOilPageInfo
-(void) dealloc
{
    self.current = nil;
    [super dealloc];
}

@end

@implementation KKModelOilPrice

-(void) dealloc
{
    self.E90 = nil;
    self.E93 = nil;
    self.E97 = nil;
    self.E0 = nil;
    [super dealloc];
}
@end

@implementation KKModelOilStation

-(void) dealloc
{
    self.id = nil;
    self.name = nil;
    self.address = nil;
    self.type = nil;
    self.discount = nil;
    self.lon = nil;
    self.lat = nil;
    self.price = nil;
    self.gastprice = nil;
    [super dealloc];
}
@end

@implementation KKModelOilStationList

-(void) dealloc
{
    self.KKArrayFieldName(data, KKModelOilStation) = nil;
    self.pageinfo = nil;
    [super dealloc];
}

@end

// ================================================================================================
// 违章信息
// ================================================================================================

@implementation KKViolateSearchCondition
- (void)encodeWithCoder:(NSCoder *)aCoder
{
    [aCoder encodeObject:self.status forKey:@"status"];
    [aCoder encodeBool:self.engine forKey:@"engine"];
    [aCoder encodeBool:self.classa forKey:@"classa"];
    [aCoder encodeBool:self.regist forKey:@"regist"];
    [aCoder encodeInteger:self.engineNo forKey:@"engineNo"];
    [aCoder encodeInteger:self.classNo forKey:@"classNo"];
    [aCoder encodeInteger:self.registNo forKey:@"registNo"];
    [aCoder encodeObject:self.cityCode forKey:@"cityCode"];
    [aCoder encodeObject:self.cityName forKey:@"cityName"];
    [aCoder encodeObject:self.provinceCode forKey:@"provinceCode"];
    [aCoder encodeObject:self.provinceName forKey:@"provinceName"];
}
- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super init])
    {
        self.status = [aDecoder decodeObjectForKey:@"status"];
        self.engine = [aDecoder decodeBoolForKey:@"engine"];
        self.engineNo = [aDecoder decodeIntegerForKey:@"engineNo"];
        self.classa = [aDecoder decodeBoolForKey:@"classa"];
        self.classNo = [aDecoder decodeIntegerForKey:@"classNo"];
        self.regist = [aDecoder decodeBoolForKey:@"regist"];
        self.registNo = [aDecoder decodeIntegerForKey:@"registNo"];
        self.cityCode = [aDecoder decodeObjectForKey:@"cityCode"];
        self.cityName = [aDecoder decodeObjectForKey:@"cityName"];
        self.provinceCode = [aDecoder decodeObjectForKey:@"provinceCode"];
        self.provinceName = [aDecoder decodeObjectForKey:@"provinceName"];
    }
    return self;
}

-(void) dealloc
{
    self.status = nil;
    self.cityCode = nil;
    self.cityName = nil;
    self.provinceCode= nil;
    self.provinceName = nil;
    [super dealloc];
}

@end
@implementation KKViolateCityInfo

- (void)encodeWithCoder:(NSCoder *)aCoder
{
    [aCoder encodeObject:self.name forKey:@"name"];
    [aCoder encodeInteger:self.id forKey:@"id"];
    [aCoder encodeInteger:self.cityCode forKey:@"cityCode"];
    [aCoder encodeObject:self.juheCityCode forKey:@"juheCityCode"];
    [aCoder encodeObject:self.juheStatus forKey:@"juheStatus"];
    [aCoder encodeObject:self.children__KKViolateCityInfo forKey:@"children"];
    [aCoder encodeObject:self.juheViolateRegulationCitySearchCondition__KKViolateSearchCondition forKey:@"SearchCondition"];
}
- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super init])
    {
        self.name = [aDecoder decodeObjectForKey:@"name"];
        self.id = [aDecoder decodeIntegerForKey:@"id"];
        self.cityCode = [aDecoder decodeIntegerForKey:@"cityCode"];
        self.juheCityCode = [aDecoder decodeObjectForKey:@"juheCityCode"];
        self.juheStatus = [aDecoder decodeObjectForKey:@"juheStatus"];
        self.children__KKViolateCityInfo = [aDecoder decodeObjectForKey:@"children"];
        self.juheViolateRegulationCitySearchCondition__KKViolateSearchCondition = [aDecoder decodeObjectForKey:@"SearchCondition"];
    }
    return self;
}


-(void) dealloc
{
    self.name = nil;
    self.children__KKViolateCityInfo = nil;
    self.juheCityCode = nil;
    self.juheStatus = nil;
    self.juheViolateRegulationCitySearchCondition__KKViolateSearchCondition = nil;
    [super dealloc];
}

@end

@implementation KKViolateDetailInfo

-(void) dealloc
{
    self.date = nil;
    self.area = nil;
    self.act = nil;
    self.code = nil;
    self.fen = nil;
    self.money = nil;
    [super dealloc];
}

@end

@implementation KKViolateVehicleType

-(void) dealloc
{
    self.car = nil;
    self.id = nil;
    [super dealloc];
}

@end

@implementation KKCheckedButton

@end

//行车日志
@implementation BGDriveRecordDetail

-(void) dealloc
{
    self.appDriveLogId = nil;
    self.appUserNo = nil;
    self.vehicleNo = nil;
    self.startLat = nil;
    self.startLon = nil;
    self.startPlace = nil;
    self.endLat = nil;
    self.endLon = nil;
    self.endPlace = nil;
    self.oilKind = nil;
    self.placeNotes = nil;
    self.pointArray = nil;
    self.appPlatform = nil;
    self.status = nil;
    [super dealloc];
}

@end

@implementation BGDriveRecordPoint

- (id)copyWithZone:(NSZone *)zone
{
    BGDriveRecordPoint *point = [[BGDriveRecordPoint allocWithZone:zone] init];
    point.lat = self.lat;
    point.lon = self.lon;
    point.recordTime = self.recordTime;
    point.type = self.type;
    return point;
}

-(void) dealloc
{
    [super dealloc];
}
@end