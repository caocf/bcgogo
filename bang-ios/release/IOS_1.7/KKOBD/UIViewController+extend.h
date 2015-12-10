//
//  UIViewController+extend.h
//  KKShowBooks
//
//  Created by zhuyc on 13-7-8.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
@class KKCustomTextField;

@interface UIViewController (extend)

- (void)setVcEdgesForExtendedLayout;

- (void)setNavigationBarTitle:(NSString *)text;

- (void)initTitleView;

- (float)getOrignY;

- (void)addStatusBarBackgroundView;

- (void)resignKeyboardNotification;

- (void)removeKeyboardNotification;

- (void)setBachGroundView;

- (void)resignVcFirstResponder;

- (KKCustomTextField *)getCurrentFirstResponderTextFieldFromView:(UIView *)view;

- (KKCustomTextField *)findNextTextFieldViewFromView:(UIView *)view WithIndex:(NSInteger)index;

@end
