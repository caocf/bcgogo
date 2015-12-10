/*
 *  KKEngineDelegate.h
 *  Better
 *
 *  Created by apple on 10-3-23.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */


#import <Foundation/Foundation.h>

// ================================================================================================
//  KKProtocolEngineDelegate
// ================================================================================================
@class KKError;

@interface KKProtocolEngineSelector : NSObject

+ (SEL) getResponseSEL:(NSInteger)aPtlApiId;

@end


@protocol KKProtocolEngineDelegate
@optional
// Params:    aRspObj: KKModelLoginRsp (normal)
//                     KKError (error)
- (NSNumber *)userLoginResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelProtocolRsp (normal)
//                     KKError (error)
- (NSNumber *)userRegisterResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

-(NSNumber *) registerShopBindResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelProtocolRsp (normal)
//                     KKError (error)
- (NSNumber *)obdBindResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelProtocolRsp (normal)
//                     KKError (error)
- (NSNumber *)userPasswordResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelNewVersionRsp (normal)
//                     KKError (error)
- (NSNumber *)newVersionResponse:(NSNumber *)aReqId withObject:(id)aRspObj;



// Params:    aRspObj:  KKVehicleListRsp (normal)
//                      KKError (error)
- (NSNumber *)vehicleListResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelProtocolRsp (normal)
//                     KKError (error)
- (NSNumber *)vehicleSaveInfoResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelVehicleGetInfoRsp (normal)
//                     KKError (error)
- (NSNumber *)vehicleGetInfoResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelProtocolRsp (normal)
//                     KKError (error)
- (NSNumber *)vehicleDeleteResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelGetBrandModelRsp (normal)
//                     KKError (error)
- (NSNumber *)vehicleGetBrandModelResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelProtocolRsp (normal)
//                     KKError (error)
- (NSNumber *)vehicleFaultResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

-(NSNumber *)vehicleFaultCodeListResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

-(NSNumber *)vehicleFaultCodeOperateResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelVehicleFaultDictRsp (normal)
//                     KKError (error)
- (NSNumber *)vehicleFaultDictResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelProtocolRsp (normal)
//                     KKError (error)
- (NSNumber *)vehicleConditionResponse:(NSNumber *)aReqId withObject:(id)aRspObj;



// Params:    aRspObj: KKModelAreaListRsp (normal)
//                     KKError (error)
- (NSNumber *)areaListResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelShopSearchListRsp (normal)
//                     KKError (error)
- (NSNumber *)shopSearchListResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelShopSuggestionsRsp (normal)
//                     KKError (error)
- (NSNumber *)shopSuggestionsResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelShopDetailRsp (normal)
//                     KKError (error)
- (NSNumber *)shopDetailResponse:(NSNumber *)aReqId withObject:(id)aRspObj;


// Params:    aRspObj: KKModelMessagePollingRsp (normal)
//                     KKError (error)
- (NSNumber *)messagePollingResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelProtocolRsp (normal)
//                     KKError (error)
- (NSNumber *)serviceAppointResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelServiceHistoryListRsp (normal)
//                     KKError (error)
- (NSNumber *)serviceHistoryListResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelServiceDetailRsp (normal)
//                     KKError (error)
- (NSNumber *)serviceHistoryDetailResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelProtocolRsp (normal)
//                     KKError (error)
- (NSNumber *)serviceDeleteResponse:(NSNumber *)aReqId withObject:(id)aRspObj;


// setting
// Params:    aRspObj: KKModelProtocolRsp (normal)
//                     KKError (error)
- (NSNumber *)shopScoreResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelProtocolRsp (normal)
//                     KKError (error)
- (NSNumber *)userPasswordModifyResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelProtocolRsp (normal)
//                     KKError (error)
- (NSNumber *)userInformationModifyResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelProtocolRsp (normal)
//                     KKError (error)
- (NSNumber *)vehicleMaintainModifyResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelProtocolRsp (normal)
//                     KKError (error)
- (NSNumber *)userLogoutResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelProtocolRsp (normal)
//                     KKError (error)
- (NSNumber *)userFeedbackResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelUserInformationRsp (normal)KKModelUserInfomationRsp
//                     KKError (error)
- (NSNumber *)userInformationResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelServiceCategoryListRsp (normal)
//                     KKError (error)
- (NSNumber *)serviceCategoryListResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelProtocolRsp (normal)
//                     KKError (error)
- (NSNumber *)updateDefaultVehicle:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelVehicleGetInfoRsp (normal)
//                     KKError (error)
- (NSNumber *)getVehicleInfoByVehicleVin:(NSNumber *)aReqId withObject:(id)aRspObj;

-(NSNumber *)getSuggestVehicleWithMobileResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

// Params:    aRspObj: KKModelOilStationListRsp (normal)
//                     KKError (error)
-(NSNumber *)oilStationListResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

-(NSNumber *)getViolateAreaListResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

-(NSNumber *)getViolateJuheQueryResponse:(NSNumber *)aReqId withObject:(id)aRspObj;

-(NSNumber *)driveRecord_Upload:(NSNumber *)aReqId withObject:(id)aRspObj;

-(NSNumber *)updateAppUserConfigResponse:(NSNumber *)aReqId withObject:(id)aRspObj;
@end
