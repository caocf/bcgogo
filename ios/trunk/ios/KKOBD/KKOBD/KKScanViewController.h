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

@protocol KKScanViewControllerDelegate;

@interface KKScanViewController : ZBarReaderViewController<ZBarReaderDelegate>
{
    BOOL        _hiddenStatusBar;
}
@property (nonatomic ,assign)id <KKScanViewControllerDelegate> delegate;

@end

@protocol KKScanViewControllerDelegate
- (void)KKScanViewControllerSuccessWithResult:(NSArray *)array;

@end