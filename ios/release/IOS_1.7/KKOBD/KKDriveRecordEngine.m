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
    _recording = YES;
    _waittingForEndPoint = NO;
    self.driveRecordDetail = [[[BGDriveRecordDetail alloc] init] autorelease];
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
            
            [self reverseGeocode:aCoordinate];
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
    
    self.driveRecordDetail.travelTime = self.driveRecordDetail.endTime - self.driveRecordDetail.startTime;
    
    double distance = BMKMetersBetweenMapPoints(BMKMapPointForCoordinate(CLLocationCoordinate2DMake([self.driveRecordDetail.startLat doubleValue], [self.driveRecordDetail.startLon doubleValue])), BMKMapPointForCoordinate(CLLocationCoordinate2DMake(endPoint.lat, endPoint.lon)));
    
    //行车日志存储筛选
//    if(distance > 200)
//    {
        
        if(!self.firstRecordTime)
        {
            if(self.driveRecordDetail.startTime != 0)
                [[NSUserDefaults standardUserDefaults] setObject:[NSString stringWithFormat:@"%lld",self.driveRecordDetail.startTime] forKey:[NSString stringWithFormat:@"%@_firstRecordTime",[KKProtocolEngine sharedPtlEngine].userName]];
        }
        
        self.lastAppDriveLogId = self.driveRecordDetail.appDriveLogId;
        [self insertRecodeToDatabase];
        [self reverseGeocode:CLLocationCoordinate2DMake(endPoint.lat, endPoint.lon)];
//    }
}

#pragma mark - 反地理编码
-(void) reverseGeocode:(CLLocationCoordinate2D) aCoordinate
{
    [_addrSearch reverseGeocode:aCoordinate];
}
-(void) onGetAddrResult:(BMKAddrInfo *)result errorCode:(int)error
{
    if(_recording)
    {
        self.driveRecordDetail.startPlace = result.strAddr;
    }
    else
    {
        [self updateEndPlace:result.strAddr appDriveLogId:self.lastAppDriveLogId];
        self.lastAppDriveLogId = nil;
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
    self.firstRecordTime = nil;
    self.driveRecordDetail = nil;
    [_addrSearch release];
    [super dealloc];
}
@end
