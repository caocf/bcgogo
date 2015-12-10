//
//  UIViewController+extend.h
//  TGOBD
//
//  Created by Jiahai on 14-3-5.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TGMacro.h"

@interface UIViewController (extend)

/**
 *  获取底层View的高度
 *
 *  @return CGFloat
 */
- (CGFloat)getViewHeight;

/**
 *  获取布局时，Y轴的起始坐标点
 *
 *  @return CGFloat
 */
- (CGFloat)getViewLayoutStartOriginY;

/**
 *  获取带导航栏时，底层View的高度
 *
 *  @return CGFloat
 */
- (CGFloat)getViewHeightWithNavigationBar;

/**
 *  获取带导航栏布局时，Y轴的起始坐标点
 *
 *  @return CGFloat
 */
- (CGFloat)getViewLayoutStartOriginYWithNavigationBar;

/**
 *  获取导航栏高度
 *
 *  @return CGFloat
 */
- (CGFloat)getNavigationBarHeight;
/**
 *  设置导航栏标题
 *
 *  @param aTitle 标题名
 */
- (void)setNavigationTitle:(NSString *)aTitle;

/**
 *  设置导航栏背景图片，图片需做两张，iOS7的图片以 "ios7_" 做前缀
 *
 *  @param bgImageName 背景图片名称
 */
- (void)setNavigationBackGroundImage:(NSString *)bgImageName;

#pragma mark - 键盘相关

- (void)dismissKeyboard;

/**
 *  注册键盘事件监听，在- (void)viewDidAppear:(BOOL)animated中调用;
 */
- (void)registerKeyboardNotification;

/**
 *  移除键盘事件监听，在- (void)viewDidDisappear:(BOOL)animated中调用;
 */
- (void)removeKeyboardNotification;

/**
 *  键盘高度改变调整View的frame，需使用scrollview嵌套
 *
 *  @param view         要改变rect的scrollview
 *  @param notification notification
 */
- (void)keyboardHeightChangedToMoveView:(UIView *)view notification:(NSNotification *)notification;
@end
