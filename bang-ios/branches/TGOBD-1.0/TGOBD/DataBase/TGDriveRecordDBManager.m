//
//  TGDriveRecordDBManager.m
//  TGOBD
//
//  Created by Jiahai on 14-3-17.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGDriveRecordDBManager.h"
#import "TGDBHelper.h"
#import "TGBasicModel.h"
#import "TGMacro.h"
#import "TGHelper.h"

#define TGTABLENAME         @"tg_driveRecord"

static TGDriveRecordDBManager *_driveRecordDB = nil;

@implementation TGDriveRecordDBManager

+ (TGDriveRecordDBManager *)sharedInstance
{
    @synchronized(self)
    {
        if (_driveRecordDB == nil)
        {
            _driveRecordDB = [[TGDriveRecordDBManager alloc] init];
        }
        
    }
    return _driveRecordDB;
}

- (FMDatabase *)db
{
    if (_db == nil) {
        _db = [[TGDBHelper sharedDBHelper] db];
    }
    return _db;
}

- (id)init
{
    if(self = [super init])
    {
        
    }
    return self;
}

- (BOOL)existRecord:(long long)aID
{
    NSString *sql = [NSString stringWithFormat:@"select count(*) from %@ where id = ?",TGTABLENAME];
    
    NSInteger count = 0;
    @try {
        FMResultSet *rs = [self.db executeQuery:sql,[NSNumber numberWithLongLong:aID]];
        while ([rs next]) {
            count = [[rs stringForColumnIndex:0] integerValue];
        }
        [rs close];
    }
    @catch (NSException *exception) {
        return NO;
    }
    
    return count != 0;
}

- (NSArray *)getDriveRecordList:(NSString *)userNo startTime:(long long)startTime endTime:(long long)endTime
{
    NSString *sql = [NSString stringWithFormat:@"select id,appUserNo,lastUpdateTime,vehicleNo,startTime,startLat,startLon,startPlace,endTime,endLat,endLon,endPlace,travelTime,distance,oilCost,oilWear,oilPrice,oilKind,totalOilMoney,appPlatform,status from %@ where appUserNo = ? and startTime between ? and ? order by startTime desc",TGTABLENAME];
    NSMutableArray *array = [[NSMutableArray alloc] init];
    @try {
        FMResultSet *rs = [self.db executeQuery:sql,userNo,[NSNumber numberWithLongLong:startTime],[NSNumber numberWithLongLong:endTime]];
        while([rs next]) {
            TGModelDriveRecordDetail *detail = [[TGModelDriveRecordDetail alloc] init];
            detail.id = [[rs stringForColumn:@"id"] longLongValue];
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
            detail.oilCost = [[rs stringForColumn:@"oilCost"] floatValue];
            detail.oilWear = [[rs stringForColumn:@"oilWear"] floatValue];
            detail.oilPrice = [[rs stringForColumn:@"oilPrice"] floatValue];
            detail.oilKind = [rs stringForColumn:@"oilKind"];
            detail.totalOilMoney = [[rs stringForColumn:@"totalOilMoney"] floatValue];
            detail.appPlatform = [rs stringForColumn:@"appPlatform"];
            detail.status = [rs stringForColumn:@"status"];
            
            [array addObject:detail];
        }
        [rs close];
    }
    @catch (NSException *exception) {
        TGLog(@"query driveRecord (%@) error %@", sql, [exception description]);
    }
    return [array count] > 0 ? array : nil;
}

- (NSArray *)getPlaceNotesArray:(long long)aID
{
    NSString *sql = [NSString stringWithFormat:@"select placeNotes from %@ where id = ?",TGTABLENAME];
    NSMutableArray *pointArray = [[NSMutableArray alloc] init];
    
    @try {
        FMResultSet *rs = [self.db executeQuery:sql,[NSNumber numberWithLongLong:aID]];
        while ([rs next]) {
            NSString *placeNotes = [rs stringForColumn:@"placeNotes"];
            NSArray *array = [placeNotes componentsSeparatedByString:@"|"];
            int i = 0;
            for(NSString *str in array)
            {
                NSArray *subArr = [str componentsSeparatedByString:@","];
                if([subArr count] > 1)
                {
                    CLLocationCoordinate2D bdCoor = [TGHelper getBaiDuCoordinate2DWithStringLat:[subArr objectAtIndex:0] stringLon:[subArr objectAtIndex:1]]; //BMKCoorDictionaryDecode(BMKBaiduCoorForWgs84(CLLocationCoordinate2DMake([[subArr objectAtIndex:0] doubleValue], [[subArr objectAtIndex:1] doubleValue])));
                    
                    TGModelDriveRecordPoint *point = [[TGModelDriveRecordPoint alloc] init];
                    point.lat = bdCoor.latitude;
                    point.lon = bdCoor.longitude;
                    point.recordTime = [[subArr objectAtIndex:2] longLongValue];
                    if(i == 0)
                        point.type = DriveRecordPointType_Start;
                    [pointArray addObject:point];
                    i++;
                }
            }
            ((TGModelDriveRecordPoint *)[pointArray lastObject]).type = DriveRecordPointType_End;
        }
        [rs close];
    }
    @catch (NSException *exception) {
        TGLog(@"query driveRecord placeNotes (%@) error %@", sql, [exception description]);
    }
    return [pointArray count] > 0 ? pointArray : nil;
}

-(BOOL) insertRecord:(TGModelDriveRecordDetail *)aDriveRecordDetail
{
    NSString *sql = [NSString stringWithFormat:@"insert into %@ values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",TGTABLENAME];
    
    @try {
        [self.db executeUpdate:sql,
         [NSNumber numberWithLongLong:aDriveRecordDetail.id],
         aDriveRecordDetail.appUserNo,
         [NSNumber numberWithLongLong:aDriveRecordDetail.lastUpdateTime],
         aDriveRecordDetail.vehicleNo,
         [NSNumber numberWithLongLong:aDriveRecordDetail.startTime],
         aDriveRecordDetail.startLat,
         aDriveRecordDetail.startLon,
         aDriveRecordDetail.startPlace,
         [NSNumber numberWithLongLong:aDriveRecordDetail.endTime],
         aDriveRecordDetail.endLat,
         aDriveRecordDetail.endLon,
         aDriveRecordDetail.endPlace,
         [NSNumber numberWithInteger:aDriveRecordDetail.travelTime],
         [NSNumber numberWithFloat:aDriveRecordDetail.distance],
         [NSNumber numberWithFloat:aDriveRecordDetail.oilCost],
         [NSNumber numberWithFloat:aDriveRecordDetail.oilWear],
         [NSNumber numberWithFloat:aDriveRecordDetail.oilPrice],
         aDriveRecordDetail.oilKind,
         [NSNumber numberWithFloat:aDriveRecordDetail.totalOilMoney],
         aDriveRecordDetail.placeNotes,
         aDriveRecordDetail.appPlatform,
         aDriveRecordDetail.status];
        
        return YES;
    }
    @catch (NSException *exception) {
        TGLog(@"insert driveRecord (%@) error %@", sql, [exception description]);
        return NO;
    }
}

- (BOOL)updateDriveRecord:(TGModelDriveRecordDetail *)aDetail
{
    if([self existRecord:aDetail.id])
    {
        NSString *sql = [NSString stringWithFormat:@"update %@ set appUserNo=?,lastUpdateTime=?,vehicleNo=?,startTime=?,startLat=?,startLon=?,startPlace=?,endTime=?,endLat=?,endLon=?,endPlace=?,travelTime=?,distance=?,oilCost=?,oilWear=?,oilPrice=?,oilKind=?,totalOilMoney=?,appPlatform=?,status=?",TGTABLENAME];
        @try {
            [self.db executeUpdate:sql,
             aDetail.appUserNo,
             [NSNumber numberWithLongLong:aDetail.lastUpdateTime],
             aDetail.vehicleNo,
             [NSNumber numberWithLongLong:aDetail.startTime],
             aDetail.startLat,
             aDetail.startLon,
             aDetail.startPlace,
             [NSNumber numberWithLongLong:aDetail.endTime],
             aDetail.endLat,
             aDetail.endLon,
             aDetail.endPlace,
             [NSNumber numberWithInteger:aDetail.travelTime],
             [NSNumber numberWithFloat:aDetail.distance],
             [NSNumber numberWithFloat:aDetail.oilCost],
             [NSNumber numberWithFloat:aDetail.oilWear],
             [NSNumber numberWithFloat:aDetail.oilPrice],
             aDetail.oilKind,
             [NSNumber numberWithFloat:aDetail.totalOilMoney],
             aDetail.appPlatform,
             aDetail.status];
            
            return YES;
        }
        @catch (NSException *exception) {
            TGLog(@"update driveRecord (%@) error %@", sql, [exception description]);
            return NO;
        }
    }
    else
    {
        return [self insertRecord:aDetail];
    }
    return NO;
}

- (BOOL)updateDriveRecordWithArray:(NSMutableArray *)detailArray
{
    BOOL ret = YES;
    for(TGModelDriveRecordDetail *detail in detailArray)
    {
        ret = ret | [self updateDriveRecord:detail];
    }
    return ret;
}

- (BOOL)updateDriveRecordPlaceNotes:(NSString *)placeNotes id:(long long)aID
{
    NSString *sql =[NSString stringWithFormat:@"update %@ set placeNotes = ? where id = ?",TGTABLENAME];
    @try {
        [_db executeUpdate:sql,placeNotes,[NSNumber numberWithLongLong:aID]];
        return YES;
    }
    @catch (NSException *exception) {
        NSLog(@"update driveRecordPlaceNotes (%@) error %@", sql, [exception description]);
        return NO;
    }
}

@end
