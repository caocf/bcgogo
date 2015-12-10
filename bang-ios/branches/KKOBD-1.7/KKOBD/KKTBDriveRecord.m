//
//  KKTBDriveRecord.m
//  KKOBD
//
//  Created by Jiahai on 14-1-14.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "KKTBDriveRecord.h"
#import "KKModelBaseElement.h"

#define     tb_Name     @"tb_driveRecord"

#define nilOrString(text) \
    (text && [[text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]] length] > 0)? ([NSString stringWithFormat:@"'%@'",[text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]]]):@"NULL"

#define nilOrIntString(value) (value == 0) ? @"NULL" : [NSString stringWithFormat:@"%d",value]

#define nilOrLonglongString(value) (value == 0) ? @"NULL" : [NSString stringWithFormat:@"%lld",value]
@implementation KKTBDriveRecord


-(BOOL) insertRecord:(BGDriveRecordDetail *)aDriveRecordDetail
{
    NSMutableString *placeBuf = [[NSMutableString alloc] init];
    for(BGDriveRecordPoint *point in aDriveRecordDetail.pointArray)
    {
        [placeBuf appendString:[NSString stringWithFormat:@"%f,%f,%lld|",point.lat,point.lon,point.recordTime]];
    }
    aDriveRecordDetail.placeNotes = [placeBuf substringWithRange:NSMakeRange(0, placeBuf.length-1)];
    [placeBuf release];
    
    NSString *sql = [NSString stringWithFormat:@"insert into %@ values (%lld,%@,%@,%lld,%@,%lld,%@,%@,%@,%lld,%@,%@,%@,%d,%f,%f,%f,%@,%f,%@,%@,%@,%d) ",tb_Name,
                     aDriveRecordDetail.id,
                     nilOrString(aDriveRecordDetail.appDriveLogId),
                     nilOrString(aDriveRecordDetail.appUserNo),
                     aDriveRecordDetail.lastUpdateTime,
                     nilOrString(aDriveRecordDetail.vehicleNo),
                     aDriveRecordDetail.startTime,
                     nilOrString(aDriveRecordDetail.startLat),
                     nilOrString(aDriveRecordDetail.startLon),
                     nilOrString(aDriveRecordDetail.startPlace),
                     aDriveRecordDetail.endTime,
                     nilOrString(aDriveRecordDetail.endLat),
                     nilOrString(aDriveRecordDetail.endLon),
                     nilOrString(aDriveRecordDetail.endPlace),
                     aDriveRecordDetail.travelTime,
                     aDriveRecordDetail.distance,
                     aDriveRecordDetail.oilWear,
                     aDriveRecordDetail.oilPrice,
                     nilOrString(aDriveRecordDetail.oilKind),
                     aDriveRecordDetail.totalOilMoney,
                     nilOrString(aDriveRecordDetail.placeNotes),
                     nilOrString(aDriveRecordDetail.appPlatform),
                     nilOrString(aDriveRecordDetail.status),
                     aDriveRecordDetail.state];
    
    @try {
        [_db executeUpdate:sql];
        return YES;
    }
    @catch (NSException *exception) {
        NSLog(@"insert driveRecord (%@) error %@", sql, [exception description]);
        return NO;
    }
}

-(BOOL) updateDriveRecord:(BGDriveRecordDetail *)aDetail
{
    NSString *sql = [NSString stringWithFormat:@"update %@ set id = %lld , lastUpdateTime = %lld , status = %@ , state = %d , oilPrice = %f, oilKind = %@, oilWear = %f, totalOilMoney = %f, distance = %f where appDriveLogId = %@",tb_Name,aDetail.id,aDetail.lastUpdateTime,nilOrString(aDetail.status),aDetail.state,aDetail.oilPrice,nilOrString(aDetail.oilKind),aDetail.oilWear,aDetail.totalOilMoney,aDetail.distance, nilOrString(aDetail.appDriveLogId)];
    
    @try {
        [_db executeUpdate:sql];
        return YES;
    }
    @catch (NSException *exception) {
        NSLog(@"insert driveRecord (%@) error %@", sql, [exception description]);
        return NO;
    }

}

-(BOOL) updateLocalDriveRecord:(BGDriveRecordDetail *)aDetail
{
    if([self existsDriveRecordWithAppDriveLogId:aDetail.appDriveLogId])
    {
        return [self updateDriveRecord:aDetail];
    }
    else
    {
        return [self insertRecord:aDetail];
    }
}

-(BOOL) existsDriveRecordWithAppDriveLogId:(NSString *)appDriveLogId
{
    NSString *sql = [NSString stringWithFormat:@"select * from %@ where appDriveLogId = '%@'",tb_Name,appDriveLogId];
    NSArray *array = [self queryDriveRecordWithSQL:sql];
    
    if([array count]>0)
        return YES;
    else
        return NO;
}

-(NSArray *) queryDriveRecordWithSQL:(NSString *)sql
{
    NSMutableArray *resultArr = [[NSMutableArray alloc] init];
    if ([sql length] == 0)
        return [resultArr autorelease];
    
    @try {
		FMResultSet *rs = [_db executeQuery:sql];
		while ([rs next])
		{
            BGDriveRecordDetail *detail = [[BGDriveRecordDetail alloc] init];
            detail.appDriveLogId = [rs stringForColumn:@"appDriveLogId"];
            detail.appUserNo = [rs stringForColumn:@"appUserNo"];
            detail.lastUpdateTime = [rs longLongIntForColumn:@"lastUpdateTime"];
            detail.vehicleNo = [rs stringForColumn:@"vehicleNo"];
            detail.startTime = [rs longLongIntForColumn:@"startTime"];
            detail.startLat = [rs stringForColumn:@"startLat"];
            detail.startLon = [rs stringForColumn:@"startLon"];
            detail.startPlace = [rs stringForColumn:@"startPlace"];
            detail.endTime = [rs longLongIntForColumn:@"endTime"];
            detail.endLat = [rs stringForColumn:@"endLat"];
            detail.endLon = [rs stringForColumn:@"endLon"];
            detail.endPlace = [rs stringForColumn:@"endPlace"];
            detail.travelTime = [rs intForColumn:@"travelTime"];
            detail.distance = [[rs stringForColumn:@"distance"] floatValue];
            detail.oilWear = [[rs stringForColumn:@"oilWear"] floatValue];
            detail.oilPrice = [[rs stringForColumn:@"oilPrice"] floatValue];
            detail.oilKind = [rs stringForColumn:@"oilKind"];
            detail.totalOilMoney = [[rs stringForColumn:@"totalOilMoney"] floatValue];
            detail.appPlatform = [rs stringForColumn:@"appPlatform"];
            detail.status = [rs stringForColumn:@"status"];
            detail.state = [rs intForColumn:@"state"];
            
            NSString *placeNotes = [rs stringForColumn:@"placeNotes"];
            
            detail.placeNotes = placeNotes;
            
            NSArray *array = [placeNotes componentsSeparatedByString:@"|"];
            NSMutableArray *pointArray = [[NSMutableArray alloc] init];
            int i = 0;
            for(NSString *str in array)
            {
                NSArray *subArr = [str componentsSeparatedByString:@","];
                BGDriveRecordPoint *point = [[BGDriveRecordPoint alloc] init];
                point.lat = [[subArr objectAtIndex:0] doubleValue];
                point.lon = [[subArr objectAtIndex:1] doubleValue];
                point.recordTime = [[subArr objectAtIndex:2] longLongValue];
                if(i == 0)
                    point.type = DriveRecordPointType_Start;
                [pointArray addObject:point];
                [point release];
                i++;
            }
            ((BGDriveRecordPoint *)[pointArray lastObject]).type = DriveRecordPointType_End;
            detail.pointArray = pointArray;
            [pointArray release];
            
            [resultArr addObject:detail];
            [detail release];
        }
    }
    @catch (NSException *exception) {
        NSLog(@"get table %@ DriveRecordList exception:%@",tb_Name, exception.description);
    }
    
    return [resultArr autorelease];
}

-(NSArray *) queryDriveRecordWithState:(NSInteger)aState UserNo:(NSString *)aUserNo vehicleNo:(NSString *)aVehicle
{
    NSString *sql = [NSString stringWithFormat:@"select * from %@ where appUserNo = '%@' and vehicleNo = '%@' and state = %d",tb_Name,aUserNo,aVehicle,aState];
    return [self queryDriveRecordWithSQL:sql];
}

-(NSArray *) queryDriveRecordWithUserNo:(NSString *)aUserNo vehicleNo:(NSString *)aVehicleNo startTime:(long long)aStartTime endTime:(long long)aEndTime
{
    NSString *sql = [NSString stringWithFormat:@"select * from %@ where startTime >= %lld and startTime <= %lld and appUserNo = '%@' and vehicleNo = '%@' order by startTime desc",tb_Name,aStartTime,aEndTime,aUserNo,aVehicleNo];
    
    return [self queryDriveRecordWithSQL:sql];
}

-(BOOL) updateEndPlace:(NSString *)aEndPlace appDriveLogId:(NSString *)aAppDriveLogId
{
    NSString *sql = [NSString stringWithFormat:@"update %@ set endPlace = '%@', state = %d where appDriveLogId = '%@' ",tb_Name,aEndPlace,DriveRecordState_UnUploaded,aAppDriveLogId];
    
    @try {
        [_db executeUpdate:sql];
        return YES;
    }
    @catch (NSException *exception) {
        NSLog(@"update End Place error %@", [exception description]);
        return NO;
    }
}
@end
