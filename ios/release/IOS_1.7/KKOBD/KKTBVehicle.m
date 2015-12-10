//
//  KKTBVehicle.m
//  KKOBD
//
//  Created by Jiahai on 14-1-3.
//  Copyright (c) 2014年 zhuyc. All rights reserved.
//

#import "KKTBVehicle.h"
#import "KKModelBaseElement.h"

#define KKVehicleTbName       @"tb_Vehicle"
#define nilOrString(text) \
(text && [[text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]] length] > 0)? ([NSString stringWithFormat:@"'%@'",[text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]]]):@"NULL"

#define sqlField_userNo(text) \
(text) ? ([NSString stringWithFormat:@" userNo = '%@' ",text]) : @" userNo is NULL "

#define sqlField_vehicleNo(text) \
(text) ? ([NSString stringWithFormat:@" vehicleNo = '%@' ",text]) : @" vehicleNo is NULL "

#define sqlField_vehicleVin(text) \
(text && [[text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]] length] > 0) ? ([NSString stringWithFormat:@" vehicleVin = '%@' ",[text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]]]) : @" vehicleVin is NULL "


@implementation KKTBVehicle

-(BOOL) createTable
{
    BOOL ret = NO;
    
    if (_db == nil)
        return ret;
    
    NSString *sqlFmt =  @"CREATE TABLE IF NOT EXISTS %@("
    @"[localId] INTEGER PRIMARY KEY AUTOINCREMENT,"
    @"[userNo] VARCHAR(20),"
    @"[mobile] VARCHAR(20),"
    @"[appUserId] VARCHAR(20),"
    @"[status] VARCHAR(20),"
    @"[vehicleId] VARCHAR(20),"
    @"[vehicleNo] VARCHAR(20),"
    @"[vehicleBrandId] VARCHAR(20),"
    @"[vehicleBrand] VARCHAR(20),"
    @"[vehicleModelId] VARCHAR(20),"
    @"[vehicleModel] VARCHAR(20),"
    @"[vehicleVin] VARCHAR(20),"
    @"[obdSN] VARCHAR(20),"
    @"[recommendShopId] VARCHAR(20),"
    @"[recommendShopName] VARCHAR(20),"
    @"[nextMaintainMileage] VARCHAR(20),"
    @"[nextInsuranceTime] VARCHAR(20),"
    @"[nextExamineTime] VARCHAR(20),"
    @"[currentMileage] VARCHAR(20),"
    @"[email] VARCHAR(20),"
    @"[contact] VARCHAR(50),"
    @"[nextMaintainTime] VARCHAR(20),"
    @"[oilWear] VARCHAR(20),"
    @"[reportTime] VARCHAR(20),"
    @"[engineNo] VARCHAR(20),"
    @"[registNo] VARCHAR(20),"
    @"[instantOilWear] VARCHAR(20),"
    @"[oilWearPerHundred] VARCHAR(20),"
    @"[oilMass] VARCHAR(20),"
    @"[engineCoolantTemperature] VARCHAR(20),"
    @"[batteryVoltage] VARCHAR(20),"
    @"[isDefault] VARCHAR(20)"
    @")";
    NSString *sql = [NSString stringWithFormat:sqlFmt, KKVehicleTbName];
    
    @try {
        ret = [_db executeUpdate:sql];
    }
    @catch (NSException *exception) {
        NSLog(@"create table %@ exception:%@", KKVehicleTbName,exception.description);
        ret = NO;
    }
    
    return ret;
}

- (NSMutableArray *)getVehicleWithSQL:(NSString *)aSQL
{
    NSMutableArray *resultArr = [[NSMutableArray alloc] init];
    if ([aSQL length] == 0)
        return [resultArr autorelease];
    
    @try {
		FMResultSet *rs = [_db executeQuery:aSQL];
		while ([rs next])
		{
			KKModelVehicleDetailInfo *info = [[KKModelVehicleDetailInfo alloc] init];
            info.localId = [rs intForColumn:@"localId"];
            info.userNo = [rs stringForColumn:@"userNo"];
            info.mobile = [rs stringForColumn:@"mobile"];
            info.appUserId = [rs stringForColumn:@"appUserId"];
            info.status = [rs stringForColumn:@"status"];
            info.vehicleId = [rs stringForColumn:@"vehicleId"];
            info.vehicleNo = [rs stringForColumn:@"vehicleNo"];
            info.vehicleBrandId = [rs stringForColumn:@"vehicleBrandId"];
            info.vehicleBrand = [rs stringForColumn:@"vehicleBrand"];
            info.vehicleModelId = [rs stringForColumn:@"vehicleModelId"];
            info.vehicleModel = [rs stringForColumn:@"vehicleModel"];
            info.vehicleVin = [rs stringForColumn:@"vehicleVin"];
            info.obdSN = [rs stringForColumn:@"obdSN"];
            info.recommendShopId = [rs stringForColumn:@"recommendShopId"];
            info.recommendShopName = [rs stringForColumn:@"recommendShopName"];
            info.nextMaintainMileage = [rs stringForColumn:@"nextMaintainMileage"];
            info.nextInsuranceTime = [rs stringForColumn:@"nextInsuranceTime"];
            info.nextExamineTime = [rs stringForColumn:@"nextExamineTime"];
            info.currentMileage = [rs stringForColumn:@"currentMileage"];
            info.email = [rs stringForColumn:@"email"];
            info.contact = [rs stringForColumn:@"contact"];
            info.nextMaintainTime = [rs stringForColumn:@"nextMaintainTime"];
            info.oilWear = [rs stringForColumn:@"oilWear"];
            info.reportTime = [rs stringForColumn:@"reportTime"];
            info.engineNo = [rs stringForColumn:@"engineNo"];
            info.registNo = [rs stringForColumn:@"registNo"];
            info.instantOilWear = [rs stringForColumn:@"instantOilWear"];
            info.oilWearPerHundred = [rs stringForColumn:@"oilWearPerHundred"];
            info.oilMass = [rs stringForColumn:@"oilMass"];
            info.engineCoolantTemperature = [rs stringForColumn:@"engineCoolantTemperature"];
            info.batteryVoltage = [rs stringForColumn:@"batteryVoltage"];
            info.isDefault = [rs stringForColumn:@"isDefault"];
            [resultArr addObject:info];
            [info release];
        }
    }
    @catch (NSException *exception) {
        NSLog(@"get table %@ messages exception:%@",KKVehicleTbName, exception.description);
    }
    
    return [resultArr autorelease];
}

-(NSArray *) getVehicleWithVehicleNo:(NSString *)aVehicleNo
{
    NSString *sql = [NSString stringWithFormat:@"select * from %@ where %@",KKVehicleTbName,sqlField_vehicleNo(aVehicleNo)];
    return [self getVehicleWithSQL:sql];
}

-(NSArray *) getVehicleWithUserNo:(NSString *)aUserNo
{
    NSString *sql = [NSString stringWithFormat:@"select * from %@ where %@",KKVehicleTbName,sqlField_userNo(aUserNo)];
    
    return [self getVehicleWithSQL:sql];
}

-(NSArray *) getVehicleWithUserNo:(NSString *)aUserNo vehicleNo:(NSString *)aVehicleNo
{
    NSString *sql = [NSString stringWithFormat:@"select * from %@ where %@ and %@",KKVehicleTbName,sqlField_userNo(aUserNo),sqlField_vehicleNo(aVehicleNo)];
    
    return [self getVehicleWithSQL:sql];
}

-(NSArray *) getVehicleWithUserNo:(NSString *)aUserNo vehicleVin:(NSString *)aVehicleVin localId:(int)aLocalId
{
    NSString *sql = nil;
    if(aLocalId == 0)
        sql = [NSString stringWithFormat:@"select * from %@ where %@ and %@",KKVehicleTbName,sqlField_userNo(aUserNo),sqlField_vehicleVin(aVehicleVin)];
    else
        sql = [NSString stringWithFormat:@"select * from %@ where %@ and %@ and localId <> %d",KKVehicleTbName,sqlField_userNo(aUserNo),sqlField_vehicleVin(aVehicleVin),aLocalId];
    
    return [self getVehicleWithSQL:sql];
}

-(BOOL) insertVehicle:(KKModelVehicleDetailInfo *)aVehicle
{
    BOOL ret = [self createTable];
    if(ret)
    {
        NSString *sql =[NSString stringWithFormat:@"insert into %@ values(NULL,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@)",KKVehicleTbName,
                        nilOrString(aVehicle.userNo),
                        nilOrString(aVehicle.mobile),
                        nilOrString(aVehicle.appUserId),
                        nilOrString(aVehicle.status),
                        nilOrString(aVehicle.vehicleId),
                        nilOrString(aVehicle.vehicleNo),
                        nilOrString(aVehicle.vehicleBrandId),
                        nilOrString(aVehicle.vehicleBrand),
                        nilOrString(aVehicle.vehicleModelId),
                        nilOrString(aVehicle.vehicleModel),
                        nilOrString(aVehicle.vehicleVin),
                        nilOrString(aVehicle.obdSN),
                        nilOrString(aVehicle.recommendShopId),
                        nilOrString(aVehicle.recommendShopName),
                        nilOrString(aVehicle.nextMaintainMileage),
                        nilOrString(aVehicle.nextInsuranceTime),
                        nilOrString(aVehicle.nextExamineTime),
                        nilOrString(aVehicle.currentMileage),
                        nilOrString(aVehicle.email),
                        nilOrString(aVehicle.contact),
                        nilOrString(aVehicle.nextMaintainTime),
                        nilOrString(aVehicle.oilWear),
                        nilOrString(aVehicle.reportTime),
                        nilOrString(aVehicle.engineNo),
                        nilOrString(aVehicle.registNo),
                        nilOrString(aVehicle.instantOilWear),
                        nilOrString(aVehicle.oilWearPerHundred),
                        nilOrString(aVehicle.oilMass),
                        nilOrString(aVehicle.engineCoolantTemperature),
                        nilOrString(aVehicle.batteryVoltage),
                        nilOrString(aVehicle.isDefault)];
        
        @try {
            [_db executeUpdate:sql];
        }
        @catch (NSException *exception) {
            NSLog(@"insert vehicle (%@) to VehicleList error %@", sql, [exception description]);
        }
    }
    return ret;
}

-(BOOL) updateVehicle:(KKModelVehicleDetailInfo *)aVehicle
{
    BOOL ret = [self createTable];
    if(ret)
    {
        //NSArray *resultArr = [self getVehicleWithUserNo:aVehicle.userNo vehicleNo:aVehicle.vehicleNo];
        
        NSString *sql =[NSString stringWithFormat:@"update %@ set userNo=%@,mobile=%@,appUserId=%@,status=%@,vehicleId=%@,vehicleNo=%@,vehicleBrandId=%@,vehicleBrand=%@,vehicleModelId=%@,vehicleModel=%@,vehicleVin=%@,obdSN=%@,recommendShopId=%@,recommendShopName=%@,nextMaintainMileage=%@,nextInsuranceTime=%@,nextExamineTime=%@,currentMileage=%@,email=%@,contact=%@,nextMaintainTime=%@,oilWear=%@,reportTime=%@,engineNo=%@,registNo=%@,instantOilWear=%@,oilWearPerHundred=%@,oilMass=%@,engineCoolantTemperature=%@,batteryVoltage=%@,isDefault=%@ where localId=%d",KKVehicleTbName,nilOrString(aVehicle.userNo),nilOrString(aVehicle.mobile),nilOrString(aVehicle.appUserId),nilOrString(aVehicle.status),nilOrString(aVehicle.vehicleId),nilOrString(aVehicle.vehicleNo),nilOrString(aVehicle.vehicleBrandId),nilOrString(aVehicle.vehicleBrand),nilOrString(aVehicle.vehicleModelId),nilOrString(aVehicle.vehicleModel),nilOrString(aVehicle.vehicleVin),nilOrString(aVehicle.obdSN),
                        nilOrString(aVehicle.recommendShopId),
                        nilOrString(aVehicle.recommendShopName),
                        nilOrString(aVehicle.nextMaintainMileage),
                        nilOrString(aVehicle.nextInsuranceTime),
                        nilOrString(aVehicle.nextExamineTime),
                        nilOrString(aVehicle.currentMileage),
                        nilOrString(aVehicle.email),
                        nilOrString(aVehicle.contact),
                        nilOrString(aVehicle.nextMaintainTime),
                        nilOrString(aVehicle.oilWear),
                        nilOrString(aVehicle.reportTime),
                        nilOrString(aVehicle.engineNo),
                        nilOrString(aVehicle.registNo),
                        nilOrString(aVehicle.instantOilWear),
                        nilOrString(aVehicle.oilWearPerHundred),
                        nilOrString(aVehicle.oilMass),
                        nilOrString(aVehicle.engineCoolantTemperature),
                        nilOrString(aVehicle.batteryVoltage),
                        nilOrString(aVehicle.isDefault),
                        aVehicle.localId];
        
//        if ([resultArr count] != 0)
//        {
//            if(![self deleteVehicle:aVehicle])
//                return NO;
//        }
        
        @try {
            [_db executeUpdate:sql];
        }
        @catch (NSException *exception) {
            NSLog(@"update vehicle (%@) to VehicleList error %@", sql, [exception description]);
        }
    }
    return ret;
}

-(BOOL) deleteVehicle:(KKModelVehicleDetailInfo *)aVehicle
{
    NSString *sql = [NSString stringWithFormat:@"delete from %@ where %@ and %@",KKVehicleTbName,sqlField_vehicleNo(aVehicle.vehicleNo),sqlField_userNo(aVehicle.userNo)];

    @try {
        return [_db executeUpdate:sql];
    }
    @catch (NSException *exception) {
        NSLog(@"delete vehicle (%@) to VehicleList error %@", sql, [exception description]);
    }
    return NO;
}

-(BOOL) existVehicle:(KKModelVehicleDetailInfo *)aVehicle
{
    BOOL ret = [self createTable];
    if(ret)
    {
        if([[self getVehicleWithUserNo:aVehicle.userNo vehicleNo:aVehicle.vehicleNo] count] == 0)
            return NO;
        else
            return YES;
    }
    return ret;
}

-(BOOL) setDefaultVehicleWithUserNo:(NSString *)aUserNo vehicleNo:(NSString *)aVehicleNo
{
    NSString *sql1 = [NSString stringWithFormat:@"update %@ set isDefault = 'NO' where %@ ",KKVehicleTbName,sqlField_userNo(aUserNo)];
    NSString *sql2 = [NSString stringWithFormat:@"update %@ set isDefault = 'YES' where %@ and %@",KKVehicleTbName,sqlField_userNo(aUserNo),sqlField_vehicleNo(aVehicleNo)];
    
    @try {
        [_db executeUpdate:sql1];
        return [_db executeUpdate:sql2];
    }
    @catch (NSException *exception) {
        NSLog(@"set default vehicle (%@) to VehicleList error %@", sql1, [exception description]);
    }
    return NO;
}

-(BOOL) existVehicleVin:(KKModelVehicleDetailInfo *)aVehicle;
{
    BOOL ret = [self createTable];
    if(ret)
    {
        if(aVehicle.vehicleVin && [[aVehicle.vehicleVin stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]] length] > 0)
        {
            if([[self getVehicleWithUserNo:aVehicle.userNo vehicleVin:aVehicle.vehicleVin localId:aVehicle.localId] count] == 0)
                return NO;
            else
                return YES;
        }
        else
            return NO;
    }
    return ret;
}
@end
