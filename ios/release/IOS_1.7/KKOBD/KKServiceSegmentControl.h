//
//  KKServiceSegmentControl.h
//  KKOBD
//
//  Created by zhuyc on 13-8-20.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol KKServiceSegmentControlDelegate;

typedef enum{
    KKServiceSegmentControlType_ServiceSeeking = 0,
    KKServiceSegmentControlType_OilStation,
    KKServiceSegmentControlType_VehicleCondition
}KKServiceSegmentControlType;

@interface KKServiceSegmentControl : UIView
{
    UIButton        *_leftButton;
    UIButton        *_rightButton;
    
    UILabel         *_leftLabel;
    UILabel         *_rightLabel;
    
}
@property (nonatomic ,assign)NSInteger  selectedIndex;
@property (nonatomic ,assign)NSInteger  unfinishedNum;
@property (nonatomic ,assign)NSInteger  finishedNum;
@property (nonatomic ,assign)KKServiceSegmentControlType  type;
@property (nonatomic ,assign)id<KKServiceSegmentControlDelegate> delegate;

- (void)updateInfo;

@end


@protocol KKServiceSegmentControlDelegate
@optional
- (void)KKServiceSegmentControlSegmentChanged:(NSInteger)index;

@end