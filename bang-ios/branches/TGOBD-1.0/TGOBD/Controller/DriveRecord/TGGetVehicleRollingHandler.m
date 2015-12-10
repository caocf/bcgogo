//
//  TGGetVehicleRollingHandler.m
//  TGOBD
//
//  Created by Jiahai on 14-3-22.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGGetVehicleRollingHandler.h"
#import "TGDataSingleton.h"
#import "TGHTTPRequestEngine.h"
#import "TGComplexModel.h"

static TGGetVehicleRollingHandler   *_rollingManager = nil;
static NSTimer                      *_rollingTimer = nil;

@implementation TGGetVehicleRollingHandler

+ (void)startRolling
{
    if (_rollingManager == nil) {
        _rollingManager = [[TGGetVehicleRollingHandler alloc] init];
    }
    
    if (_rollingTimer == nil) {
        _rollingTimer = [NSTimer scheduledTimerWithTimeInterval:TIME_GET_VEHICLE_INFO
                                                         target:_rollingManager
                                                       selector:@selector(getVehicleInfo)
                                                       userInfo:nil
                                                        repeats:YES];
        [_rollingTimer fire];
    }
}

+ (void)stopRolling
{
    if (_rollingTimer) {
        [_rollingTimer invalidate];
    }
    _rollingTimer = nil;
}

- (void)getVehicleInfo
{
    TGDataSingleton *dataSingleton = [TGDataSingleton sharedInstance];
    
    [[TGHTTPRequestEngine sharedInstance] driveRecordGetVehicle:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if ([responseObject isKindOfClass:[TGModelDriveRecordGetVehicleRsp class]]) {
            TGModelDriveRecordGetVehicleRsp *rsp = (TGModelDriveRecordGetVehicleRsp *)responseObject;
            if ((rsp.header.status == rspStatus_Succeed)) {
                dataSingleton.vehicleInfo = rsp.vehicleInfo;
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_UpdateVehicleCoordinate object:nil];
                });
            }
        }

    } failure:nil];
}

@end
