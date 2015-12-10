//
//  KKDriveRecordEngine.m
//  KKOBD
//
//  Created by Jiahai on 14-1-17.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "KKDriveRecordEngine.h"
#import "KKTBDriveRecord.h"
#import "KKProtocolEngine.h"
#import "KKAppDelegate.h"
#import "KKApplicationDefine.h"
#import "KKHelper.h"
#import "KKPreference.h"
#import "NSObject+AutoMagicCoding.h"

@interface KKDriveRecordEngine ()
@property (nonatomic, copy)     NSString            *lastAppDriveLogId;
//@property (nonatomic, retain) BGDriveRecordPoint  *driveRecordPoint;
@end

#define     stopRecordingTimeInterval           12

@implementation KKDriveRecordEngine
static KKDriveRecordEngine *_driveRecordEngine;

+(KKDriveRecordEngine *)sharedInstance
{
    @synchronized(self)
    {
        if (_driveRecordEngine == nil)
            _driveRecordEngine = [[KKDriveRecordEngine alloc] init];
    }
    return _driveRecordEngine;
}

-(id) init
{
    if(self = [super init])
    {
        _addrSearch = [[BMKSearch alloc] init];
        _addrSearch.delegate = self;
        
        NSMutableDictionary *carPointDic = [[NSUserDefaults standardUserDefaults] objectForKey:[NSString stringWithFormat:@"%@_carPoint",[KKProtocolEngine sharedPtlEngine].userName]];
        if(carPointDic)
        {
            self.carPoint = [NSObject objectWithDictionaryRepresentation:carPointDic];
        }
    }
    return self;
}

- (NSString *)createUUIDString
{
    // create a new UUID which you own
    CFUUIDRef uuid = CFUUIDCreate(kCFAllocatorDefault);
    
    // create a new CFStringRef (toll-free bridged to NSString)
    // that you own
    NSString *uuidString = (NSString *)CFUUIDCreateString(kCFAllocatorDefault, uuid);
    
    // transfer ownership of the string
    // to the autorelease pool
    [uuidString autorelease];
    
    // release the UUID
    CFRelease(uuid);
    
    return uuidString;
}


//计算单次行程消耗的油钱
-(void) countTotalOilMoney
{
    self.driveRecordDetail.totalOilMoney = (self.driveRecordDetail.oilWear / 100) * self.driveRecordDetail.oilPrice * self.driveRecordDetail.distance;
}

-(BGDriveRecordPoint *) createDriveRecordPoint:(CLLocationCoordinate2D)aCoordinate
{
    BGDriveRecordPoint *point = [[[BGDriveRecordPoint alloc] init] autorelease];
    point.lat = aCoordinate.latitude;
    point.lon = aCoordinate.longitude;
    point.recordTime = (long long)[[NSDate date] timeIntervalSince1970WithMillisecond];
    
    return point;
}

-(BOOL) startDriveRecord
{
    if(_waittingForEndPoint)
    {
        //上一次的行程未记录
        _waittingForEndPoint = NO;
        [self saveDriveRecord];
    }
    
    _recording = YES;
    _waittingForEndPoint = NO;
    self.driveRecordDetail = [[[BGDriveRecordDetail alloc] init] autorelease];
    
    //设置油价/油品
    self.driveRecordDetail.oilKind = [KKPreference sharedPreference].appUserConfig.oil_kind;
    self.driveRecordDetail.oilPrice = [[KKPreference sharedPreference].appUserConfig.oil_price floatValue];
    
    self.driveRecordDetail.pointArray = [[[NSMutableArray alloc] init] autorelease];
    
    self.driveRecordDetail.appDriveLogId = [self createUUIDString];
    self.driveRecordDetail.appUserNo = [KKProtocolEngine sharedPtlEngine].userName;
    self.driveRecordDetail.vehicleNo = KKAppDelegateSingleton.currentVehicle.vehicleNo;
    self.driveRecordDetail.appPlatform = @"IOS";
    
    return YES;
}

-(BOOL) recordDrivePoint:(CLLocationCoordinate2D)aCoordinate
{
    if(_recording)
    {
        BGDriveRecordPoint *point = [self createDriveRecordPoint:aCoordinate];
        if([self.driveRecordDetail.pointArray count] == 0)
        {
            self.driveRecordDetail.startLat = [NSString stringWithFormat:@"%f",aCoordinate.latitude];
            self.driveRecordDetail.startLon = [NSString stringWithFormat:@"%f",aCoordinate.longitude];
            self.driveRecordDetail.startTime = point.recordTime;
            point.type = DriveRecordPointType_Start;
        }
        if(self.driveRecordDetail.startPlace == nil || self.driveRecordDetail.startPlace.length == 0)
        {
            NSLog(@"开始位置地理编码");
            [self reverseGeocode:CLLocationCoordinate2DMake([self.driveRecordDetail.startLat floatValue],[self.driveRecordDetail.startLon floatValue])];
        }
        
        [self.driveRecordDetail.pointArray addObject:point];
    }
    else
    {
        if(_waittingForEndPoint)
        {
            BGDriveRecordPoint *point = [self createDriveRecordPoint:aCoordinate];
            point.type = DriveRecordPointType_End;
            
            [self.driveRecordDetail.pointArray addObject:point];
            
            [self saveDriveRecord];
            _waittingForEndPoint = NO;
        }
    }
    
    [[NSNotificationCenter defaultCenter] postNotificationName:Notification_DriveRecord_NewPoint object:nil];
    return NO;
}

-(BOOL) stopDriveRecordImmediately:(BOOL)immediately
{
    if(_recording)
    {
        _recording = NO;
        
        if(self.driveRecordDetail.distance < 1)
        {
            //距离少于1公里的，不记录
            [self stopDriveRecordWithOutSave];
            return YES;
        }
        
        if(!immediately)
        {
            _waittingForEndPoint = YES;
        }
        else
        {
            _waittingForEndPoint = NO;
            [self saveDriveRecord];
        }
        return YES;
    }
    return NO;
}

-(void) stopDriveRecordWithOutSave
{
    self.driveRecordDetail = nil;
    _waittingForEndPoint = NO;
    _recording = NO;
    
    [[NSNotificationCenter defaultCenter] postNotificationName:Notification_DriveRecord_UnSave object:nil];
}

-(void) saveDriveRecord
{
    BGDriveRecordPoint *endPoint = [self.driveRecordDetail.pointArray lastObject];
    endPoint.type = DriveRecordPointType_End;
    
    self.driveRecordDetail.endLat = [NSString stringWithFormat:@"%f",endPoint.lat];
    self.driveRecordDetail.endLon = [NSString stringWithFormat:@"%f",endPoint.lon];
    self.driveRecordDetail.endTime = endPoint.recordTime;
    
    self.driveRecordDetail.travelTime = (self.driveRecordDetail.endTime - self.driveRecordDetail.startTime);
    
    //double distance = BMKMetersBetweenMapPoints(BMKMapPointForCoordinate(CLLocationCoordinate2DMake([self.driveRecordDetail.startLat doubleValue], [self.driveRecordDetail.startLon doubleValue])), BMKMapPointForCoordinate(CLLocationCoordinate2DMake(endPoint.lat, endPoint.lon)));
    
    //行车日志存储筛选
//    if(distance > 200)
//    {
        
//        if(self.firstRecordTime == 0)
//        {
//            if(self.driveRecordDetail.startTime != 0)
//                [[NSUserDefaults standardUserDefaults] setObject:[NSString stringWithFormat:@"%lld",self.driveRecordDetail.startTime] forKey:[NSString stringWithFormat:@"%@_firstRecordTime",[KKProtocolEngine sharedPtlEngine].userName]];
//        }
    
        self.lastAppDriveLogId = self.driveRecordDetail.appDriveLogId;
        [self insertRecodeToDatabase];
//    }
    
    self.carPoint = [endPoint copy];
    self.carPoint.type = DriveRecordPointType_CarPark;
    
    self.driveRecordDetail = nil;
    
    if(_waittingForEndPoint)
        [self reverseGeocode:CLLocationCoordinate2DMake(self.carPoint.lat, self.carPoint.lon)];
    
    [[NSUserDefaults standardUserDefaults] setObject:[self.carPoint dictionaryRepresentation] forKey:[NSString stringWithFormat:@"%@_carPoint",[KKProtocolEngine sharedPtlEngine].userName]];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

#pragma mark - 反地理编码
-(void) reverseGeocode:(CLLocationCoordinate2D) aCoordinate
{
    [_addrSearch reverseGeocode:aCoordinate];
}

-(void) onGetAddrResult:(BMKAddrInfo *)result errorCode:(int)error
{
    NSLog(@"行车日志：%@",result.strAddr);
    if(_recording)
    {
        if(!_waittingForEndPoint && (self.driveRecordDetail.startPlace == nil || self.driveRecordDetail.startPlace.length == 0))
        {
            self.driveRecordDetail.startPlace = result.strAddr;
        }
    }
    else
    {
        if(self.lastAppDriveLogId)
        {
            [self updateEndPlace:result.strAddr appDriveLogId:self.lastAppDriveLogId];
            self.lastAppDriveLogId = nil;
        }
    }
}

#pragma mark - 操作数据库

-(BOOL) insertRecodeToDatabase
{
    BOOL ret = NO;
    KKTBDriveRecord *tb_driveRecord = [[KKTBDriveRecord alloc] initWithDB:[KKDB sharedDB]];
    ret = [tb_driveRecord insertRecord:self.driveRecordDetail];
    if(ret)
    {
        self.driveRecordDetail = nil;
    }
    [tb_driveRecord release];
    return  ret;
}

-(BOOL) updateLocalDriveRecord:(BGDriveRecordDetail *)aDetail
{
    //注意本地状态的改变，更新前先设置好aDetail.state
    BOOL ret = NO;
    KKTBDriveRecord *tb_driveRecord = [[KKTBDriveRecord alloc] initWithDB:[KKDB sharedDB]];
    ret = [tb_driveRecord updateLocalDriveRecord:aDetail];
    [tb_driveRecord release];
    return ret;
}

-(NSArray *) queryDriveRecordWithState:(DriveRecordState)state
{
    KKTBDriveRecord *tb_driveRecord = [[KKTBDriveRecord alloc] initWithDB:[KKDB sharedDB]];
    NSArray *array = [tb_driveRecord queryDriveRecordWithState:state UserNo:[KKProtocolEngine sharedPtlEngine].userName vehicleNo:KKAppDelegateSingleton.currentVehicle.vehicleNo];
    [tb_driveRecord release];
    return array;
}

-(NSArray *) queryDriveRecordWithTimeRange:(DateTimeRange)aTimeRange appUserNo:(NSString *)appUserNo vehicleNo:(NSString *)vehicleNo
{
    return [self queryDriveRecordWithStartTime:aTimeRange.startTime endTime:aTimeRange.endTime appUserNo:appUserNo vehicleNo:vehicleNo];
}

-(NSArray *) queryDriveRecordWithStartTime:(long long)aStartTime endTime:(long long)aEndTime appUserNo:(NSString *)appUserNo vehicleNo:(NSString *)vehicleNo
{
    KKTBDriveRecord *tb_driveRecord = [[KKTBDriveRecord alloc] initWithDB:[KKDB sharedDB]];
    NSArray *array = [tb_driveRecord queryDriveRecordWithUserNo:appUserNo vehicleNo:vehicleNo startTime:aStartTime endTime:aEndTime];
    [tb_driveRecord release];
    return array;
}

-(BOOL) updateEndPlace:(NSString *)aEndPlace appDriveLogId:(NSString *)aAppDriveLogId
{
    BOOL ret = NO;
    if(aEndPlace && aAppDriveLogId && aEndPlace.length > 0)
    {
        KKTBDriveRecord *tb_driveRecord = [[KKTBDriveRecord alloc] initWithDB:[KKDB sharedDB]];
        ret = [tb_driveRecord updateEndPlace:aEndPlace appDriveLogId:aAppDriveLogId];
        [tb_driveRecord release];
    }
    return ret;
}

#pragma mark - KKProtocolEngineDelegate
//-(void)

-(void) dealloc
{
    self.lastAppDriveLogId = nil;
    self.driveRecordDetail = nil;
    [_addrSearch release];
    [super dealloc];
}
@end
