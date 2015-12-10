//
//  KKProtocolParser.h
//  KKShowBooks
//
//  Created by shugq on 13-08-11.
//  Copyright (c) 2012年 kk. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TBXML.h"

@class KKError;
@class KKProtocolAbstractParser;

// ================================================================================================
// 解析器类厂
// ================================================================================================
#pragma mark -
#pragma mark ParserFactory

@interface KKProtocolParserFactory : NSObject {
	
}
+ (KKProtocolAbstractParser*) createParser:(NSInteger)aProtocolApiID;

@end

// ================================================================================================
// 解析器基类（抽象类）
// ================================================================================================
#pragma mark -
#pragma mark abstractParser

@interface KKProtocolAbstractParser : NSObject {
}
- (id)parse:(id)jsonObject;
-(id) parseObjectWithDict:(NSDictionary *)dict classname:(NSString *)classname;

@end

#pragma mark -
#pragma mark base parser 
@interface KKRspHeaderParser : KKProtocolAbstractParser
@end

@interface KKEmptyBodyRspParser : KKProtocolAbstractParser
{
	NSString			*_apiServiceName;
}

-(id) initWithSerivceName:(NSString*)aServiceName;

@end

// ================================================================================================
// 基本元素解析类
// ================================================================================================

#pragma mark -
#pragma mark 基本元素
@interface KKStringParser : KKProtocolAbstractParser
@end


// ================================================================================================
// 业务相关解析类
// ================================================================================================
#pragma mark -
#pragma mark 业务相关解析类

@interface helloclass : NSObject {
    @private int varName;
}
@property (readwrite,assign) int propName;

+ (void)testMethod;
@end

#pragma mark -
#pragma mark 返回KKModelProtocolRsp的通用方法

@interface KKModelProtocolRspParser : KKProtocolAbstractParser

@end

// setting and system
@interface KKUserRegisterRspParser : KKProtocolAbstractParser
@end

@interface KKUserLoginRspParser : KKProtocolAbstractParser
@end

@interface KKObdBindRspParser : KKProtocolAbstractParser
@end

@interface KKUserPasswordRspParser : KKProtocolAbstractParser
@end

@interface KKNewVersionRspParser : KKProtocolAbstractParser
@end

// vehicle
@interface KKVehicleListRspParser : KKProtocolAbstractParser
@end

@interface KKVehicleSaveInfoRspParser : KKProtocolAbstractParser
@end

@interface KKVehicleGetInfoRspParser : KKProtocolAbstractParser
@end

@interface KKVehicleDeleteRspParser : KKProtocolAbstractParser
@end

@interface KKVehicleFaultRspParser : KKProtocolAbstractParser
@end

@interface KKVehicleFaultCodeListRspParser : KKProtocolAbstractParser
@end

@interface KKVehicleFaultCodeOperateRspParser : KKProtocolAbstractParser
@end

@interface KKVehicleFaultDictRspParser : KKProtocolAbstractParser
@end

@interface KKVehicleGetBrandModelRspParser : KKProtocolAbstractParser
@end

@interface KKVehicleConditionRspParser : KKProtocolAbstractParser
@end

// shop
@interface KKAreaListRspParser : KKProtocolAbstractParser
@end

@interface KKShopSearchListRspParser : KKProtocolAbstractParser
@end

@interface KKShopSuggestionsRspParser : KKProtocolAbstractParser
@end

@interface KKShopDetailRspParser : KKProtocolAbstractParser
@end

@interface KKShopCommentListRspParser : KKProtocolAbstractParser

@end

@interface KKMessagePollingRspParser : KKProtocolAbstractParser
@end

@interface KKServiceCategoryListRspParser : KKProtocolAbstractParser
@end


// service
@interface KKServiceAppointmentRspParser : KKProtocolAbstractParser
@end

@interface KKServiceHistoryListRspParser : KKProtocolAbstractParser
@end

@interface KKServiceHistoryDetailRspParser : KKProtocolAbstractParser
@end

@interface KKServiceDeleteRspParser : KKProtocolAbstractParser
@end

// setting
@interface KKShopScoreRspParser : KKProtocolAbstractParser
@end

@interface KKUserPasswordModifyRspParser : KKProtocolAbstractParser
@end

@interface KKUserInformationModifyRspParser : KKProtocolAbstractParser
@end

@interface KKVehicleMaintainModifyRspParser : KKProtocolAbstractParser
@end

@interface KKUserLogoutRspParser : KKProtocolAbstractParser
@end

@interface KKUserFeedbackRspParser : KKProtocolAbstractParser
@end

@interface KKUserInformationRspParser : KKProtocolAbstractParser
@end

@interface KKVehicleUpdateDefaultRspParser : KKProtocolAbstractParser
@end

@interface KKSuggestionVehicleRspParser : KKProtocolAbstractParser
@end

@interface KKOilStationListRspParser : KKProtocolAbstractParser
@end

//违章
@interface KKViolateAreaListRspParser : KKProtocolAbstractParser
@end

@interface KKViolateQueryResultRspParser : KKProtocolAbstractParser

@end

@interface KKViolateVehicleTypeParser : KKProtocolAbstractParser

@end

//行车日志
@interface BGDriveRecordUploadParser : KKProtocolAbstractParser

@end

