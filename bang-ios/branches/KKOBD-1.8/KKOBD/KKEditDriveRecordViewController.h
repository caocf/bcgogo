//
//  KKEditDriveRecordViewController.h
//  KKOBD
//
//  Created by Jiahai on 14-2-10.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKCustomTextField.h"
@class BGDriveRecordDetail;
@protocol KKEditDriveRecordViewControllDelegate;

@interface KKEditDriveRecordViewController : UIViewController<UITextFieldDelegate>
{
    KKCustomTextField       *_oilPriceText;
//    KKCustomTextField       *_oilKindText;
    KKCustomTextField       *_oilWearText;
    KKCustomTextField       *_totalOilMoneyText;
    KKCustomTextField       *_distanceText;
    
    CGFloat                 _totalMoney;                    //为零时，修改其他项则自动计算油钱
}
@property(nonatomic, assign) id<KKEditDriveRecordViewControllDelegate>      delegate;
@property(nonatomic, retain) BGDriveRecordDetail            *driveRecordDetail;
@end

@protocol KKEditDriveRecordViewControllDelegate <NSObject>

//行车日志已修改
-(void) driveRecordEdited;

@end