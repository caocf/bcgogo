//
//  UIViewController+extend.m
//  TGOBD
//
//  Created by Jiahai on 14-3-5.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "UIViewController+extend.h"

@implementation UIViewController (extend)

- (CGFloat)getViewHeight
{
    if(systemVersionAboveiOS7)
    {
        return screenHeight;
    }
    else
        return screenHeight - 44;
}

- (CGFloat)getViewLayoutStartOriginY
{
    return 0;
}

- (CGFloat)getViewHeightWithNavigationBar
{
    if(systemVersionAboveiOS7)
    {
        /**
         *  iOS7以上系统带导航的View高度
         *  iOS7导航的高度也是 44
         */
        return screenHeight - 44 - 20;
    }
    else
        return screenHeight - 44;
}

- (CGFloat)getViewLayoutStartOriginYWithNavigationBar
{
    if(systemVersionAboveiOS7)
    {
        return 64;
    }
    else
    {
        return 0;
    }
}

- (void)setNavigationTitle:(NSString *)aTitle
{
    self.navigationItem.title = aTitle;
}

@end
