//
//  TGBaseViewController+extend.m
//  TGYF
//
//  Created by James Yu on 14-5-19.
//  Copyright (c) 2014年 james. All rights reserved.
//

#import "TGBaseViewController+extend.h"

@implementation TGBaseViewController (extend)
- (CGFloat)getViewHeight
{

    return (SCREEN_HEIGHT - (isIOS7 ? 0 : 20));
}

- (CGFloat)getViewOriginY
{
    if (self.titleBar) {
        return (isIOS7 ? 64 : 44);
    }
    else
    {
        return 0;
    }
}

- (CGFloat)getViewHeightWithNavigatioinBar
{
    return (SCREEN_HEIGHT - 64);
}

- (void)registerKeyboardNotification
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardNotification:) name:UIKeyboardDidShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardNotification) name:UIKeyboardWillHideNotification object:nil];
}

- (void)removeKeyboardNotification
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardDidShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillHideNotification object:nil];
}

@end
