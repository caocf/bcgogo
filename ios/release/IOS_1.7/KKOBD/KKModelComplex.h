//
//  KKModelComplex.h
//  KKOBD
//
//  Created by zhuyc on 13-8-7.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "KKModelBaseElement.h"

#pragma mark -
#pragma mark 基础类
// ================================================================================================
// KKModelProtocolRsp
// ================================================================================================
@interface KKModelProtocolRsp : KKModelObject
{
	KKModelRspHeader		*_header;
}
@property (nonatomic, retain) KKModelRspHeader *header;

@end

// ================================================================================================
// KKModelLoginRsp
// ================================================================================================

@interface KKMOdelLoginVehicleInfo : KKModelProtocolRsp
@property (nonatomic, retain)KKModelVehicleDetailInfo *vehicleInfo;
@property (nonatomic, retain)NSString *obdSN;
@property (nonatomic, retain)NSString *obdId;
@property (nonatomic, retain)NSString *isDefault;

@end

// ================================================================================================
// 获取后台车辆信息建议
// ================================================================================================
@interface KKModelSuggestionVehicleRsp : KKModelProtocolRsp
@property(nonatomic,retain) KKModelSuggestionVehicle *result;
@end


@interface KKModelLoginRsp : KKModelProtocolRsp

@property (nonatomic, retain) KKModelAppConfig *appConfig;
@property (nonatomic, retain) NSMutableArray* KKArrayFieldName(obdList,KKMOdelLoginVehicleInfo);     

@end

// ================================================================================================
// KKVehicleListRsp
// ================================================================================================
@interface KKVehicleListRsp : KKModelProtocolRsp
@property (nonatomic, retain) NSMutableArray* KKArrayFieldName(vehicleList,KKModelVehicleDetailInfo);

@end

// ================================================================================================
// KKModelNewVersionRsp
// ================================================================================================
@interface KKModelNewVersionRsp : KKModelProtocolRsp
{
    
}
@property (nonatomic, copy) NSString *url;              // 电子市场下载地址
@property (nonatomic, copy) NSString *action;           // 交互类型（force：强制|alert：提醒|normal：正常）

@end

// ================================================================================================
// KKModelVehicleGetInfoRsp
// ================================================================================================
@interface KKModelVehicleGetInfoRsp : KKModelProtocolRsp
{
    
}
@property (nonatomic, retain) KKModelVehicleDetailInfo *vehicleInfo;

@end

// ================================================================================================
// KKModelGetBrandModelRsp
// ================================================================================================
@interface KKModelGetBrandModelRsp : KKModelProtocolRsp
{
    
}
@property (nonatomic, retain) NSMutableArray *KKArrayFieldName(result, KKModelCarInfo);

@end

// ================================================================================================
// KKModelAreaListRsp
// ================================================================================================
@interface KKModelAreaListRsp : KKModelProtocolRsp

@property (nonatomic, retain) NSMutableArray *KKArrayFieldName(areaList, KKModelAreaInfo);

@end

// ================================================================================================
// KKModelShopSearchListRsp
// ================================================================================================
@interface KKModelShopSearchListRsp : KKModelProtocolRsp

@property (nonatomic, retain) NSMutableArray *KKArrayFieldName(shopList, KKModelShopInfo);
@property (nonatomic, retain) KKModelPagerInfo *pager;

@end

// ================================================================================================
// KKModelShopDetailRsp
// ================================================================================================
@interface KKModelShopDetailRsp : KKModelProtocolRsp

@property (nonatomic, retain) KKModelShopDetail *shop;

@end

// ================================================================================================
// KKModelShopSuggestionsRsp
// ================================================================================================
@interface KKModelShopSuggestionsRsp : KKModelProtocolRsp

@property (nonatomic, retain) NSMutableArray *KKArrayFieldName(shopSuggestionList, KKModelShopInfo);

@end


// ================================================================================================
// KKModelVehicleFaultDictRsp
// ================================================================================================
@interface KKModelVehicleFaultDictRsp : KKModelProtocolRsp

@property (nonatomic, copy) NSString *dictionaryId;                 // 字典ID  Long
@property (nonatomic, copy) NSString *dictionaryVersion;            // 字典版本  String
@property (nonatomic, assign) BOOL isCommon;                        // 是否通用  String  TRUE:通用  ,  FALSE:不通用
@property (nonatomic, retain) NSMutableArray *KKArrayFieldName(faultCodeList, KKModelFaultCodeInfo); //   字典错误码列表

@end

// ================================================================================================
// KKModelMessagePollingRsp
// ================================================================================================
@interface KKModelMessagePollingRsp : KKModelProtocolRsp

@property (nonatomic, retain) NSMutableArray *KKArrayFieldName(messageList, KKModelMessage);         // 消息列表，按时间顺序排列


@end

// ================================================================================================
// KKModelServiceHistoryListRsp
// ================================================================================================

@interface KKModelServiceHistoryListRsp : KKModelProtocolRsp
@property (nonatomic, retain) NSMutableArray *KKArrayFieldName(results,KKModelserviceDetail);
@property (nonatomic, retain) KKModelServiceHistoryPager *pager;
//@property (nonatomic, assign) NSInteger unFinishedServiceCount;                 // 未完成服务数量    int
//@property (nonatomic, assign) NSInteger finishedServiceCount;                   // 已完成服务数量      int
//@property (nonatomic, retain) NSMutableArray *KKArrayFieldName(unFinishedServiceList, KKModelService);         // 未完成服务列表
//@property (nonatomic, retain) NSMutableArray *KKArrayFieldName(finishedServiceList, KKModelService);           // 已完成服务列表
//
@end


// ================================================================================================
// KKModelServiceDetailRsp
// ================================================================================================
@interface KKModelServiceDetailRsp : KKModelProtocolRsp

@property (nonatomic, retain) KKModelserviceDetail *serviceDetail;              // 服务单据详情

@end

// ================================================================================================
// KKModelUserInfomationRsp
// ================================================================================================
@interface KKModelUserInfomationRsp : KKModelProtocolRsp

@property (nonatomic, retain) KKModelUserInfo *userInfo;                        // 个人资料


@end

// ================================================================================================
// KKModelServiceCategoryListRsp        
// ================================================================================================
@interface KKModelServiceCategoryListRsp : KKModelProtocolRsp                   //服务范围
@property (nonatomic ,retain) NSMutableArray *KKArrayFieldName(serviceCategoryDTOList,KKModelServiceCategory);
@end

@interface KKModelSaveVehicleInfoRsp : KKModelProtocolRsp
@property (nonatomic, retain) KKModelVehicleDetailInfo *vehicleInfo;

@end
// ================================================================================================
// KKModelOilStationListRsp
// ================================================================================================
@interface KKModelOilStationListRsp : NSObject
@property (nonatomic, copy) NSString *resultcode;
@property (nonatomic, copy) NSString *reason;
@property (nonatomic, assign) BOOL isEnd;
@property (nonatomic, retain) KKModelOilStationList *result;
@end

// ================================================================================================
// 违章信息
// ================================================================================================

@interface KKViolateAreaListRsp : KKModelProtocolRsp
@property (nonatomic, retain) NSMutableArray *KKArrayFieldName(result,KKViolateCityInfo);
@end

@interface KKViolateResultInfo : KKModelObject
@property(nonatomic, copy) NSString *province;
@property(nonatomic, copy) NSString *city;
@property(nonatomic, copy) NSString *hphm;
@property(nonatomic, copy) NSString *hpzl;
@property(nonatomic, retain) NSMutableArray *KKArrayFieldName(lists,KKViolateDetailInfo);
@end

@interface KKViolateResultRsp : KKModelProtocolRsp
@property(nonatomic, copy) NSString *resultcode;
@property(nonatomic, copy) NSString *reason;
@property(nonatomic, retain) KKViolateResultInfo *result;
@end

@interface KKViolateVehicleTypeRsp : KKModelProtocolRsp
@property (nonatomic, retain) NSMutableArray *KKArrayFieldName(result,KKViolateVehicleType);
@end

@interface BGDriveRecordUploadRsp : KKModelProtocolRsp
@property (nonatomic, retain) BGDriveRecordDetail *result;

@end
