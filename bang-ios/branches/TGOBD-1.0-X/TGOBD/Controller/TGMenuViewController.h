//
//  TGMenuViewController.h
//  TGOBD
//
//  Created by Jiahai on 14-3-5.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TGBaseViewController.h"

typedef NS_ENUM(NSUInteger, TGPaneViewControllerType) {
    TGPaneViewControllerTypeDriveRecord = 0,
    TGPaneViewControllerTypeOilStation,
    TGPaneViewControllerTypeViolate,
};


@interface TGMenuViewController : TGBaseViewController

@property (nonatomic, assign) TGPaneViewControllerType      paneViewControllerType;

- (void)transitionToViewController:(TGPaneViewControllerType)paneViewControllerType;
@end
