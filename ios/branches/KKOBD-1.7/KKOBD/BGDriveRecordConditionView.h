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
    UIImageView *_bgImgView;
    UILabel     *_drivingMileageLabel;
    UILabel     *_oilWearLabel;
    UILabel     *_oilCostsLabel;
    UILabel     *_drivingTimeLabel;
}


-(void) setbgImage:(UIImage *)aImage;
-(void) setContentWithRealTimeData:(BGDriveRecordDetail *)aRecordDetail;
-(void) setContentWithDrivedMileage:(CGFloat)aDistance oilConsumption:(CGFloat)aOilWear oilCosts:(CGFloat)aOilCosts drivedTime:(NSInteger)aDrivedTime;
@end
