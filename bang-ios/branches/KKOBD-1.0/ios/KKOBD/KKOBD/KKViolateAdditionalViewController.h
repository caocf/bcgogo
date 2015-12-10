//
//  KKViolateAdditionalViewController.h
//  KKOBD
//
//  Created by Jiahai on 13-12-16.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKViolateViewController.h"

@interface KKViolateAdditionalViewController : UIViewController<KKProtocolEngineDelegate>
{
    UIScrollView            *_mainScrollView;
    
    KKCustomTextField       *_engineText;       //发动机号
    KKCustomTextField       *_registText;       //行驶证号
    KKCustomTextField       *_classaText;       //车架号
}

@property (nonatomic ,retain) KKModelVehicleDetailInfo  *vehicleDetailInfo;
@property (nonatomic, assign) ViolateSearchCondition violateSearchCondition;
@end
