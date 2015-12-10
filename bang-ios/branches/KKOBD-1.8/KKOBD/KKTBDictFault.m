//
//  KKTBDictFault.m
//  KKOBD
//
//  Created by codeshu on 9/26/13.
//  Copyright (c) 2013 zhuyc. All rights reserved.
//

#import "KKDB.h"
#import "KKTBDictFault.h"
#import "FMDatabase.h"
#import "KKModelComplex.h"

#define KKDefultDictFaultTbName @"tb_dictFault"
#define KKDictVerTbName @"tb_dictVer"

// ==========================================================================================
@implementation KKTBDictFaultVerItem

- (void)dealloc
{
    self.dictId = nil;
    self.version = nil;
    self.tableName = nil;
    self.vehicleModelId = nil;
    
    [super dealloc];
}

@end

// ==========================================================================================
@implementation KKTBDictFaultVersion

+(void) updateLocalDBWithVehicleModelId:(NSString *)aVehicleModelId
{
    KKTBDictFaultVersion *tbDFV = [[KKTBDictFaultVersion alloc] initWithDB:[KKDB sharedDB]];
    [tbDFV updateLocalDBWithVehicleModelId:aVehicleModelId];
    [tbDFV release];
}

-(void) updateLocalDBWithVehicleModelId:(NSString *)aVehicleModelId
{
    KKTBDictFaultVerItem *verItem = [self getNewestDictTableItemForVehicle:aVehicleModelId];
    
    if(verItem)
    {
        NSString *alterSql = [NSString stringWithFormat:@"SELECT sql FROM sqlite_master WHERE name = '%@' and type = 'table'",verItem.tableName];
        FMResultSet *rs = nil;
        @try {
            rs = [_db executeQuery:alterSql];
        }
        @catch (NSException *exception) {
            NSLog(@"query create table sql exception:%@", exception.description);
        }
        while ([rs next]) {
            NSString *createSQL = [rs objectForColumnName:@"sql"];
            if(createSQL && ![createSQL isEqual:[NSNull null]] && [createSQL rangeOfString:@"category"].length == 0)
            {
                //有表，但是没有category和backgroundInfo字段
                NSString *sql1 = [NSString stringWithFormat:@"DROP TABLE %@;",verItem.tableName];
                NSString *sql2 = [NSString stringWithFormat:@"DELETE FROM %@ WHERE tableName = '%@'",KKDictVerTbName,verItem.tableName];
                @try {
                    [_db executeUpdate:sql1];
                    [_db executeUpdate:sql2];
                }
                @catch (NSException *exception) {
                    NSLog(@"drop table tb_dictFault exception:%@", exception.description);
                }
            }
        }
        [rs close];
    }
}

- (BOOL)createTable
{
    BOOL ret = NO;
    
    if (_db == nil)
        return ret;
        
    NSString *sqlFmt =  @"CREATE TABLE IF NOT EXISTS %@("
                        @"[dictId] CHAR(20),"
                        @"[version] VARCHAR(20),"
                        @"[tableName] VARCHAR(50),"
                        @"[isCommon] INT,"
                        @"[vehicleModelId] VARCHAR(30)"
                        @")";
    NSString *sql = [NSString stringWithFormat:sqlFmt, KKDictVerTbName];
    
    @try {
        ret = [_db executeUpdate:sql];
    }
    @catch (NSException *exception) {
        NSLog(@"create table tb_dictVer exception:%@", exception.description);
        ret = NO;
    }
    
    return ret;
}

// Note: if aModelId is nil, mean adding a common dict
- (BOOL)addDictItemWithDictId:(NSString *)aDictId Version:(NSString *)aVersion vehicleModelId:(NSString *)aModelId
{
    BOOL ret = NO;
    
    if (nil == _db)
        return ret;
    
    NSString *tbname = [KKTBDictFault tableNameWithVehicleModel:aModelId version:aVersion];
    BOOL isCommon = [aModelId length] > 0? NO:YES;
    
    NSString *sql = nil;
    if (isCommon) {
        NSString *sqlFmt = @"INSERT INTO %@ VALUES('%@', '%@', '%@', %d, '')";
        sql = [NSString stringWithFormat:sqlFmt, KKDictVerTbName, aDictId, aVersion, tbname, isCommon];
    }
    else {
        NSString *sqlFmt =  @"INSERT INTO %@ VALUES('%@', '%@', '%@', %d, '%@')";
        sql = [NSString stringWithFormat:sqlFmt, KKDictVerTbName, aDictId, aVersion, tbname, isCommon, aModelId];
    }
    
    @try {
        ret = [_db executeUpdate:sql];
    }
    @catch (NSException *exception) {
        NSLog(@"create dict item in tb_dictVer exception:%@", exception.description);
        ret = NO;
    }
    
    return ret;
}

- (BOOL)deleteDictItemWithVer:(NSString *)aVersion vehicleModelId:(NSString *)aModelId
{
    BOOL ret = NO;
    
    if (nil == _db)
        return ret;
        
    NSString *sql = nil;
    if ([aModelId length] > 0) {
        NSString *sqlFmt =  @"DELETE FROM %@ WHERE vehicleModelId='%@' AND version='%@'";
        sql = [NSString stringWithFormat:sqlFmt, KKDictVerTbName,  aModelId, aVersion];
    }
    else {
        NSString *sqlFmt =  @"DELETE FROM %@ WHERE isCommon=1 AND version='%@'";
        sql = [NSString stringWithFormat:sqlFmt, KKDictVerTbName,  aVersion];
    }
    
    @try {
        ret = [_db executeUpdate:sql];
    }
    @catch (NSException *exception) {
        NSLog(@"delete dict item in tb_dictVer exception:%@", exception.description);
        ret = NO;
    }
    
    return ret;
}

// Get the newest fault dictionary table item
- (KKTBDictFaultVerItem *)getNewestDictTableItemForVehicle:(NSString *)aVehicleModelId;
{
    KKTBDictFaultVerItem *verItem = nil;
    
    NSArray *arr = [self getDictVerArrayForVehicle:aVehicleModelId];
    for (KKTBDictFaultVerItem *item in arr) {
        CGFloat maxVer = verItem?[verItem.version floatValue]:0.f;
        CGFloat ver = [item.version floatValue];
        if (ver >= maxVer) {
            maxVer = ver;
            verItem = item;
        }
    }
    
    return verItem;
}


// Get all the version in the table for modelID
- (NSArray *)getDictVerArrayForVehicle:(NSString *)aVehicleModelId
{
    if (nil == _db)
        return nil;
    
    NSString *sql = nil;
    if ([aVehicleModelId length] > 0) {
        NSString *sqlFmt =  @"SELECT version, tableName, dictId, isCommon, vehicleModelId FROM %@ WHERE vehicleModelId='%@'";
        sql = [NSString stringWithFormat:sqlFmt, KKDictVerTbName,  aVehicleModelId];
    }
    else {
        NSString *sqlFmt =  @"SELECT version, tableName , dictId, isCommon, vehicleModelId FROM %@ WHERE isCommon=1";
        sql = [NSString stringWithFormat:sqlFmt, KKDictVerTbName];
    }
    
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:10];
    @try {
        FMResultSet *rs = [_db executeQuery:sql];
        while ([rs next]) {
            KKTBDictFaultVerItem *item = [[[KKTBDictFaultVerItem alloc] init] autorelease];
            item.version = [rs stringForColumn:@"version"];
            item.dictId = [rs stringForColumn:@"dictId"];
            item.tableName = [rs stringForColumn:@"tableName"];
            item.isCommon = [rs intForColumn:@"isCommon"] == 0?NO:YES;
            item.vehicleModelId = [rs stringForColumn:@"vehicleModelId"];
            [arr addObject:item];
        }
        [rs close];
    }
    @catch (NSException *exception) {
        NSLog(@"get all version in tb_dictVer exception:%@", exception.description);
    }
    
    return arr;
}

@end

// ==========================================================================================
@interface KKTBDictFault(_private)

// create a table in database
- (BOOL)createTableWithName:(NSString *)aTbName;

// Get fault info from specified table
- (NSArray *)getFaultInfoInTable:(NSString *)aTableName code:(NSString *)aCode;

@end

@implementation KKTBDictFault

- (BOOL)createTableWithName:(NSString *)aTbName
{
    BOOL ret = NO;
    
    if (nil == _db)
        return ret;
    
    if ([aTbName length] == 0)
        aTbName = KKDefultDictFaultTbName;
    
    NSString *sqlFmt =  @"CREATE TABLE IF NOT EXISTS %@("
                        @"[code] CHAR(20),"
                        @"[description] VARCHAR(500),"
                        @"[category] TEXT,"
                        @"[backgroundInfo] TEXT"
                        @")";
    NSString *sql = [NSString stringWithFormat:sqlFmt, aTbName];

    @try {
        ret = [_db executeUpdate:sql];
    }
    @catch (NSException *exception) {
        NSLog(@"create table tb_dictFault exception:%@", exception.description);
        ret = NO;
    }
    
    return ret;
}

- (BOOL)createTableWithVehicleModel:(NSString *)aModel dictDetail:(KKModelVehicleFaultDictRsp*)aDict;
{
    BOOL ret = NO;
    NSArray *dicts = aDict.KKArrayFieldName(faultCodeList, KKModelFaultCodeInfo);
    if (nil == _db || [dicts count] == 0)
        return NO;
        
    // Find the dict is higher than existed or not
    KKTBDictFaultVersion *tbDFV = [[KKTBDictFaultVersion alloc] initWithDB:[KKDB sharedDB]];
    [tbDFV createTable];
    KKTBDictFaultVerItem *newst = [tbDFV getNewestDictTableItemForVehicle:aModel];
    NSString *newestVer = newst.version;
    NSString *ver = aDict.dictionaryVersion;
    if (newestVer.floatValue >= ver.floatValue) {
        [tbDFV release];
        return NO;
    }
    
    @try {
        [_db beginTransaction];
        
        BOOL suc = NO;
        NSString *tbName = [KKTBDictFault tableNameWithVehicleModel:aModel version:aDict.dictionaryVersion];
        
        // Create new table
        suc = [self createTableWithName:tbName];
        if (NO == suc) {
            NSException *exc = [NSException exceptionWithName:nil reason:@"create table failed" userInfo:nil];
            @throw exc;
        }
        for (KKModelFaultCodeInfo *info in dicts) {
            NSString *sqlFmt = @"INSERT INTO %@ (code,description,category,backgroundInfo) VALUES('%@', '%@', %@, %@)";
            NSString *sql = [NSString stringWithFormat:sqlFmt, tbName, info.faultCode, info.description,SQL_Field_nilOrString(info.category),SQL_Field_nilOrString(info.backgroundInfo)];
            [_db executeUpdate:sql];
        }
        
        // Add new item in tb_dictVer
        suc = [tbDFV addDictItemWithDictId:aDict.dictionaryId Version:aDict.dictionaryVersion vehicleModelId:aModel];
        if (NO == suc) {
            NSException *exc = [NSException exceptionWithName:nil reason:@"create item in tb_dictVer failed" userInfo:nil];
            @throw exc;
        }
        
        // Delete old table
        // Delete item in tb_dictVer
        NSArray *arr = [tbDFV getDictVerArrayForVehicle:aModel];
        for (KKTBDictFaultVerItem *item in arr) {
            if ([item.version isEqualToString:aDict.dictionaryVersion] == YES)
                continue;
            
            NSString *sql = [NSString stringWithFormat:@"DROP TABLE %@", item.tableName];
            suc = [_db executeUpdate:sql];
            if (NO == suc) {
                NSLog(@"Can't drop table %@", item.tableName);
                continue;
            }
            suc = [tbDFV deleteDictItemWithVer:item.version vehicleModelId:item.vehicleModelId];
        }
        
        [_db commit];
        ret = YES;
    }
    @catch (NSException *exception) {
        [_db rollback];
        NSLog(@"create dict_fault table failed, error=%@", exception.description);
    }
    
    [tbDFV release];
    
    return ret;
}

// Get fault info from specified table
- (NSArray *)getFaultInfoInTable:(NSString *)aTableName code:(NSString *)aCode
{
    if (nil == _db || [aCode length] == 0 || [aTableName length] == 0)
        return nil;
    
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:5];

    @try {
        NSString *sql = [NSString stringWithFormat:@"SELECT * FROM %@ WHERE code='%@'", aTableName, aCode];
        FMResultSet *rs = [_db executeQuery:sql];
        while ([rs next]) {
            KKModelFaultCodeInfo *info = [[[KKModelFaultCodeInfo alloc] init] autorelease];
            info.faultCode = [rs stringForColumn:@"code"];
            info.description = [rs stringForColumn:@"description"];
            info.category = [rs stringForColumn:@"category"];
            info.backgroundInfo = [rs stringForColumn:@"backgroundInfo"];
            [arr addObject:info];
        }
        [rs close];
    }
    @catch (NSException *exception) {
        NSLog(@"get fault info failed, error=%@", exception.description);
    }

    return arr;
}

// Get FaultCodeInfo from dict table by vehicleModelId and error code, each code maybe has more than 1 items
// Find sequece: find from dict specfied by modelId first, then from common dict
// return: array, inside are KKModelFaultCodeInfo
- (NSArray *)getFaultInfoWithCode:(NSString *)aCode vehicleModelId:(NSString *)aVehicleModelId
{
    if (nil == _db || [aCode length] == 0)
        return nil;
    
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:5];
    
    KKTBDictFaultVersion *tbDFV = [[KKTBDictFaultVersion alloc] initWithDB:[KKDB sharedDB]];
    KKTBDictFaultVerItem *newest = [tbDFV getNewestDictTableItemForVehicle:aVehicleModelId];
    KKTBDictFaultVerItem *newestCommon = newest;
    if ([aVehicleModelId length] > 0)
        newestCommon = [tbDFV getNewestDictTableItemForVehicle:nil];
    
    if (nil == newest && nil == newestCommon)
        return nil;
    
    // First, search from specified table(modelId)
    NSArray *arr0 = [self getFaultInfoInTable:newest.tableName code:aCode];
    if ([arr0 count] > 0)
        [arr addObjectsFromArray:arr0];
    
    // Then, if not found, search in common table
    if ([arr count] == 0 && newestCommon != newest) {
        NSArray *arr1 = [self getFaultInfoInTable:newestCommon.tableName code:aCode];
        if ([arr1 count] > 0)
            [arr addObjectsFromArray:arr1];
    }
    
    [tbDFV release];
    
    if([arr count] == 0)
    {
        KKModelFaultCodeInfo *info = [[KKModelFaultCodeInfo alloc] init];
        info.faultCode = aCode;
        info.description = @"未知故障";
        info.category=@"未知";
        info.backgroundInfo = @"暂无背景知识";
        [arr addObject:info];
        [info release];
    }
    
    return arr;
}


// Get the newest version in local database with specified vehicle model id
+ (NSString *)getNewestVersionForVehicle:(NSString *)aVehicleModelId
{
    NSString *newest = nil;
    
    KKTBDictFaultVersion *tbDFV = [[KKTBDictFaultVersion alloc] initWithDB:[KKDB sharedDB]];
    KKTBDictFaultVerItem *verItem = [tbDFV getNewestDictTableItemForVehicle:aVehicleModelId];
    newest = verItem.version;
    [tbDFV release];
    
    return newest;
}

// tablename: tb_dictFault+_vehicleModel+_version
+ (NSString *)tableNameWithVehicleModel:(NSString *)aModel version:(NSString *)aVersion
{
    NSMutableString *tbname = nil;
    
    if ([aModel length] == 0)
        aModel = @"common";
    
    tbname = [NSMutableString stringWithFormat:@"%@_%@_%@", KKDefultDictFaultTbName, aModel, aVersion];
    [tbname replaceOccurrencesOfString:@"." withString:@"_" options:NSCaseInsensitiveSearch range:NSMakeRange(0, [tbname length])];
    
    return tbname;
}



- (void)dealloc
{
    
    [super dealloc];
}

@end
