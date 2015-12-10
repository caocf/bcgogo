//
//  KKRegisterAccountViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-12.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKModelBaseElement.h"

@interface KKRegisterAccountViewController : UIViewController<UITextFieldDelegate>
{
    UIScrollView        *_mainScrollView;
}
@property (nonatomic ,retain) KKModelVehicleDetailInfo *vehicleDetail;
@property (nonatomic ,retain) NSDate *nextInsuranceTime;
@property (nonatomic ,retain) NSDate *nextExamTime;

@end
