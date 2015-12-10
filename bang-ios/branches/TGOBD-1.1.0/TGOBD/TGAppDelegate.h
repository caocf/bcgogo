//
//  TGAppDelegate.h
//  TGOBD
//
//  Created by Jiahai on 14-2-28.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TGNavigationController.h"

@interface TGAppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;
@property (nonatomic, strong) TGNavigationController *rootViewController;

- (void)showRootView;

- (void)showLoginView;

- (void)logout;

@end
