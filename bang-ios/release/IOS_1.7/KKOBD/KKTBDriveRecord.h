//
//  KKTBDriveRecord.h
//  KKOBD
//
//  Created by Jiahai on 14-1-14.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "KKTBBase.h"
@class BGDriveRecordDetail;

@interface KKTBDriveRecord : KKTBBase


-(BOOL) insertRecord:(BGDriveRecordDetail *)aDriveRecordDetail;

-(BOOL) updateLocalDriveRecord:(BGDriveRecordDetail *)aDetail;

-(NSArray *) queryDriveRecordWithState:(NSInteger)aState UserNo:(NSString *)aUserNo vehicleNo:(NSString *)aVehicle;
-(NSArray *) queryDriveRecordWithUserNo:(NSString *)aUserNo vehicleNo:(NSString *)aVehicleNo startTime:(long long)aStartTime endTime:(long long)aEndTime;

-(BOOL) updateEndPlace:(NSString *)aEndPlace appDriveLogId:(NSString *)aAppDriveLogId;
@end
