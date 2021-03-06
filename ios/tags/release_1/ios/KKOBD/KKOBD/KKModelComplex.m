//
//  KKModelComplex.m
//  KKOBD
//
//  Created by zhuyc on 13-8-7.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKModelComplex.h"

#pragma mark -
#pragma mark 基础类
// ================================================================================================
//  KKModelProtocolRsp
//
// ================================================================================================
@implementation KKModelProtocolRsp
@synthesize header = _header;

- (void)dealloc
{
	self.header = nil;

	[super dealloc];
}

@end

// ================================================================================================
// KKModelLoginRsp
// ================================================================================================

@implementation KKMOdelLoginVehicleInfo

- (void)dealloc
{
    self.vehicleInfo = nil;
    self.obdSN = nil;
    self.obdId = nil;
    self.isDefault = nil;
    [super dealloc];
}
@end

@implementation KKModelLoginRsp

- (void)dealloc
{
    self.KKArrayFieldName(obdList, KKMOdelLoginVehicleInfo) = nil;
    self.appConfig = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// KKVehicleListRsp
// ================================================================================================

@implementation KKVehicleListRsp

- (void)dealloc
{
    self.KKArrayFieldName(vehicleList,KKModelVehicleDetailInfo) = nil;
    [super dealloc];
}

@end

// ================================================================================================
// KKModelNewVersionRsp
// ================================================================================================
@implementation KKModelNewVersionRsp 

- (void)dealloc
{
    self.url = nil;
    self.action = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// KKModelVehicleGetInfoRsp
// ================================================================================================
@implementation KKModelVehicleGetInfoRsp

- (void)dealloc
{
    self.vehicleInfo = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// KKModelGetBrandModelRsp
// ================================================================================================
@implementation KKModelGetBrandModelRsp

- (void)dealloc
{
    self.result__KKModelCarInfo = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// KKModelAreaListRsp
// ================================================================================================
@implementation KKModelAreaListRsp

- (void)dealloc
{
    self.KKArrayFieldName(areaList, KKModelAreaInfo) = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// KKModelShopSearchListRsp
// ================================================================================================
@implementation KKModelShopSearchListRsp

- (void)dealloc
{
    self.KKArrayFieldName(shopList, KKModelShopInfo) = nil;
    self.pager = nil;
    
    [super dealloc];
}

@end


// ================================================================================================
// KKModelShopDetailRsp
// ================================================================================================
@implementation KKModelShopDetailRsp 

- (void)dealloc
{
    self.shop = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// KKModelShopSuggestionsRsp
// ================================================================================================
@implementation KKModelShopSuggestionsRsp

- (void)dealloc
{
    self.KKArrayFieldName(shopSuggestionList, KKModelShopInfo) = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// KKModelVehicleFaultDictRsp
// ================================================================================================
@implementation KKModelVehicleFaultDictRsp

- (void)dealloc
{
    self.dictionaryId = nil;
    self.dictionaryVersion = nil;
    self.KKArrayFieldName(faultCodeList, KKModelFaultCodeInfo) = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// KKModelMessagePollingRsp
// ================================================================================================
@implementation KKModelMessagePollingRsp 

- (void)dealloc
{
    self.KKArrayFieldName(messageList, KKModelMessage) = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// KKModelServiceHistoryListRsp
// ================================================================================================
@implementation KKModelServiceHistoryListRsp

- (void)dealloc
{
    self.KKArrayFieldName(unFinishedServiceList, KKModelService) = nil;
    self.KKArrayFieldName(finishedServiceList, KKModelService) = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// KKModelServiceDetailRsp
// ================================================================================================
@implementation KKModelServiceDetailRsp

- (void)dealloc
{
    self.serviceDetail = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// KKModelUserInfomationRsp
// ================================================================================================
@implementation KKModelUserInfomationRsp

- (void)dealloc
{
    self.userInfo = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// KKModelServiceCategoryListRsp
// ================================================================================================
@implementation KKModelServiceCategoryListRsp
- (void)dealloc
{
    self.KKArrayFieldName(serviceCategoryDTOList,KKModelServiceCategory) = nil;
    
    [super dealloc];
}

@end

