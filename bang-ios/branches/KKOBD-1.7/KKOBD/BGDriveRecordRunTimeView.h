//
//  BGDriveRecordRunTimeView.h
//  KKOBD
//
//  Created by Jiahai on 14-2-10.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
@class KKModelVehicleRealtimeData,BGDriveRecordDetail;

@interface BGDriveRecordRunTimeView : UIView
{
    UILabel     *_oilOverplusLabel;                 //剩余油量
    UILabel     *_waterTemperatureLabel;            //水箱温度
    UILabel     *_drivingMileageLabel;
    UILabel     *_oilWearLabel;
//    UILabel     *_oilCostsLabel;
    UILabel     *_drivingTimeLabel;
}

-(void) setContentWithRealTimeData:(KKModelVehicleRealtimeData *)aRunTimeData recordDetail:(BGDriveRecordDetail *)aRecordDetail;
@end
