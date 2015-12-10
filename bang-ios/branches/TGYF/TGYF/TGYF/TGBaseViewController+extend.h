//
//  TGBaseViewController+extend.h
//  TGYF
//
//  Created by James Yu on 14-5-19.
//  Copyright (c) 2014年 james. All rights reserved.
//

#import "TGBaseViewController.h"

@interface TGBaseViewController (extend)

- (CGFloat)getViewHeight;

- (CGFloat)getViewOriginY;

- (CGFloat)getViewHeightWithNavigatioinBar;

- (void)registerKeyboardNotification;

- (void)removeKeyboardNotification;

@end
