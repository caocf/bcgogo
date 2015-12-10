//
//  KKTBDictFault.h
//  KKOBD
//
//  Created by codeshu on 9/26/13.
//  Copyright (c) 2013 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KKTBBase.h"

@interface KKTBDictFaultVerItem : NSObject

@property (nonatomic, copy) NSString *dictId;
@property (nonatomic, copy) NSString *version;
@property (nonatomic, copy) NSString *tableName;
@property (nonatomic, assign) BOOL  isCommon;
@property (nonatomic, copy) NSString *vehicleModelId;

@end


// ==========================================================================================
@interface KKTBDictFaultVersion : KKTBBase

+(void) updateLocalDBWithVehicleModelId:(NSString *)aVehicleModelId;
-(void) updateLocalDBWithVehicleModelId:(NSString *)aVehicleModelId;
// Must call this function to create dictionary version manage table before use KKTBDictFault
- (BOOL)createTable;

// Note: if aModelId is nil, mean adding a common dict
- (BOOL)addDictItemWithDictId:(NSString *)aDictId Version:(NSString *)aVersion vehicleModelId:(NSString *)aModelId;

// Note: if aModelId is nil, mean adding a common dict
- (BOOL)deleteDictItemWithVer:(NSString *)aVersion vehicleModelId:(NSString *)aModelId;

// Get the newest fault dictionary table item
- (KKTBDictFaultVerItem *)getNewestDictTableItemForVehicle:(NSString *)aVehicleModelId;

// Get all the version in the table for modelID
- (NSArray *)getDictVerArrayForVehicle:(NSString *)aVehicleModelId;

@end


// ==========================================================================================
@class KKModelVehicleFaultDictRsp;

@interface KKTBDictFault: KKTBBase
{
    
}

// return:  NO when dict has been existed and version higher then parameter aDict.dictionaryVersion
- (BOOL)createTableWithVehicleModel:(NSString *)aModel dictDetail:(KKModelVehicleFaultDictRsp*)aDict;

// Get FaultCodeInfo from dict table by vehicleModelId and error code, each code maybe has more than 1 items
// Find sequece: find from dict specfied by modelId first, then from common dict
// return: array, inside are KKModelFaultCodeInfo
- (NSArray *)getFaultInfoWithCode:(NSString *)aCode vehicleModelId:(NSString *)aVehicleModelId;

// Get the newest version in local database with specified vehicle model id
+ (NSString *)getNewestVersionForVehicle:(NSString *)aVehicleModelId;

// tablename: tb_dictFault+_vehicleModel+_version
+ (NSString *)tableNameWithVehicleModel:(NSString *)aModel version:(NSString *)aVersion;


@end
