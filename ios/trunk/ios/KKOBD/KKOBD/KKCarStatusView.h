//
//  KKCarStatusView.h
//  KKOBD
//
//  Created by zhuyc on 13-8-9.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef enum
{
    e_CarWell,
    e_CarAlarm,
    e_CarNotOnLine
}KKCarStatusType;

@protocol KKCarStatusViewDelegate;

@interface KKCarStatusView : UIView
{
    @private
        UIImageView     *_bgImv;
        UIImageView     *_carStatusImv;
}
@property (nonatomic ,assign)UILabel    *carModelLb;
@property (nonatomic ,assign)KKCarStatusType    carStatus;
@property (nonatomic ,assign)id<KKCarStatusViewDelegate>    delegate;

- (void)setCarStatus:(KKCarStatusType)carStatus;

@end

@protocol KKCarStatusViewDelegate
@optional
- (void)KKCarStatusViewStatusButtonClicked:(KKCarStatusType)carStatus;
- (void)KKCarStatusViewShopButtonClicked;


@end