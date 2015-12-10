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
 *  设置导航栏标题
 *
 *  @param aTitle 标题名
 */
- (void)setNavigationTitle:(NSString *)aTitle;
@end
