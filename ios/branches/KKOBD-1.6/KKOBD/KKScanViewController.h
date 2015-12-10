//
//  KKScanViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-14.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ZBarReaderViewController.h"
#import <AVFoundation/AVFoundation.h>
#import <AudioToolbox/AudioToolbox.h>
#import "KKApplicationDefine.h"
#import "KKProtocolEngine.h"
#import "KKProtocolEngineDelegate.h"

@protocol KKScanViewControllerDelegate;
@class KKModelVehicleDetailInfo;

@interface KKScanViewController : ZBarReaderViewController<ZBarReaderDelegate,KKProtocolEngineDelegate>
{
    BOOL        _hiddenStatusBar;
}
@property (nonatomic ,assign)id <KKScanViewControllerDelegate> delegate;
@property (nonatomic, assign) BOOL isFromRegister;
@property (nonatomic, assign) BOOL isInNavigationController;        //控制外观
@property (nonatomic, assign) NextViewControllerEnum nextVc;
//@property (nonatomic, retain) KKModelVehicleDetailInfo *regVehicleDetailInfo;
@end

@protocol KKScanViewControllerDelegate
- (void)KKScanViewControllerSuccessWithResult:(NSArray *)array;

@end