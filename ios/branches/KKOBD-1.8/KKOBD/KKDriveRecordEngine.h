//
//  KKDriveRecordEngine.h
//  KKOBD
//
//  Created by Jiahai on 14-1-17.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>
#import "BMapKit.h"
#import "KKModelBaseElement.h"

typedef struct {
    long long   startTime;
    long long   endTime;
}DateTimeRange;

@interface KKDriveRecordEngine : NSObject <BMKSearchDelegate>
{
    BOOL            _recording;             //行车日志已启动
    BOOL            _waittingForEndPoint;   //等待结束定位点
    NSTimer         *_stopRecordingTimer;   //行车日志停止记录计时器，当收到停止事件后，特定时间内未重新开始记录，则视为结束当前日志
    BMKSearch       *_addrSearch;
    
}

@property(nonatomic,readonly)   BOOL                recording;
@property (nonatomic, assign)   long long        firstRecordTime;       //第一条记录的开始时间
@property (nonatomic, retain)   BGDriveRecordDetail *driveRecordDetail;      //当前行车日志实例
@property (nonatomic, retain)   BGDriveRecordPoint  *carPoint;          //车辆停放所在位置

+(KKDriveRecordEngine *)sharedInstance;

//计算单次行程消耗的油钱
-(void) countTotalOilMoney;

-(BOOL) startDriveRecord;
-(BOOL) recordDrivePoint:(CLLocationCoordinate2D) aCoordinate;
-(BOOL) stopDriveRecordImmediately:(BOOL) immediately;
-(void) stopDriveRecordWithOutSave;

//更新/添加本地日志记录
-(BOOL) updateLocalDriveRecord:(BGDriveRecordDetail *)aDetail;
-(BOOL) updateEndPlace:(NSString *)aEndPlace appDriveLogId:(NSString *)aAppDriveLogId;

-(NSArray *) queryDriveRecordWithState:(DriveRecordState)state;
-(NSArray *) queryDriveRecordWithTimeRange:(DateTimeRange)aTimeRange appUserNo:(NSString *)appUserNo vehicleNo:(NSString *)vehicleNo;
@end
