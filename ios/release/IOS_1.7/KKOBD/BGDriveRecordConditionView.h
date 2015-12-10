//
//  BGDriveRecordConditionView.h
//  KKOBD
//
//  Created by Jiahai on 14-1-14.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
@class BGDriveRecordDetail;
@interface BGDriveRecordConditionView : UIView
{
    UILabel     *_drivingMileageLabel;
    UILabel     *_oilConsumptionLabel;
    UILabel     *_oilCostsLabel;
    UILabel     *_drivingTimeLabel;
    
}

-(void) setContentWithRealTimeData:(BGDriveRecordDetail *)aRecordDetail;
@end
