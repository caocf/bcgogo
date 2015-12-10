//
//  TGScanViewController.h
//  TGOBD
//
//  Created by Jiahai on 14-3-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "ZBarSDK.h"
@protocol TGScanViewControllerDelegate;

@interface TGScanViewController : ZBarReaderViewController <ZBarReaderDelegate>

@property (nonatomic, assign) id<TGScanViewControllerDelegate> delegate;

@end

@protocol TGScanViewControllerDelegate <NSObject>

- (void)scanSuccess:(NSString *)dataStr;

@end