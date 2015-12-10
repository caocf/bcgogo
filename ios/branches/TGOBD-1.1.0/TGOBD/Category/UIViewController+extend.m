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
    return screenHeight;
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
        if([self.navigationController.navigationBar backgroundImageForBarMetrics:UIBarMetricsDefault])
        {
            return 0;
        }
        return 64;
    }
    else
    {
        return 0;
    }
}

- (CGFloat)getNavigationBarHeight
{
    if(systemVersionAboveiOS7)
    {
        return 64;
    }
    else
        return 44;
}

- (void)setNavigationTitle:(NSString *)aTitle
{
    if(![[self.navigationItem.titleView class] isSubclassOfClass:[UILabel class]])
    {
        UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 200, 30)];
        titleLabel.center = CGPointMake(320/2, 44/2);
        titleLabel.textColor = [UIColor whiteColor];
        titleLabel.font = [UIFont boldSystemFontOfSize:22.0f];
        titleLabel.backgroundColor = [UIColor clearColor];
        titleLabel.textAlignment = UITextAlignmentCenter;
        self.navigationItem.titleView = titleLabel;
    }
    ((UILabel *)self.navigationItem.titleView).text = aTitle;
}

- (void)setNavigationBackGroundImage:(NSString *)bgImageName
{
    UINavigationBar *naviBar = nil;
    
    if([[self class] isSubclassOfClass:[UINavigationController class]])
    {
        naviBar = ((UINavigationController *)self).navigationBar;
    }
    else
    {
        naviBar = self.navigationController.navigationBar;
    }
    
    if(systemVersionAboveiOS7)
    {
        [naviBar setBackgroundImage:[UIImage imageNamed:[NSString stringWithFormat:@"ios7_%@",bgImageName]] forBarPosition:UIBarPositionAny barMetrics:UIBarMetricsDefault];
    }
    else
    {
        [naviBar setBackgroundImage:[UIImage imageNamed:bgImageName] forBarMetrics:UIBarMetricsDefault];
    }
}

#pragma mark - 键盘相关

- (void)dismissKeyboard
{
    [[UIApplication sharedApplication] sendAction:@selector(resignFirstResponder) to:nil from:nil forEvent:nil];
}

- (void)registerKeyboardNotification
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didKeyboardNotification:) name:UIKeyboardDidShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didKeyboardNotification:) name:UIKeyboardWillHideNotification object:nil];
}

- (void)removeKeyboardNotification
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardDidShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillHideNotification object:nil];
}

- (CGFloat)keyboardHeightDeltaWith:(NSNotification *)notification
{
    float deltaHeight = 0;
    CGRect beginRect = [[notification.userInfo objectForKey:UIKeyboardFrameBeginUserInfoKey] CGRectValue];
    CGRect endRect = [[notification.userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    
    deltaHeight = endRect.origin.y - beginRect.origin.y;
    
    return deltaHeight;
}

- (void)keyboardHeightChangedToMoveView:(UIScrollView *)scrollView notification:(NSNotification *)notification
{
    CGRect endRect = [[notification.userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    
    CGFloat viewHeight; //底层View的高度
    if(self.navigationController.navigationBar.hidden)
    {
        viewHeight = [self getViewHeight];
    }
    else
    {
        viewHeight = [self getViewHeightWithNavigationBar];
    }
    
    CGRect rect = scrollView.frame;
    rect.size.height = viewHeight - (screenHeight - endRect.origin.y);
    
    scrollView.frame = rect;
    
    NSArray *subViews = [scrollView subviews];
    
    for(UIView *view in subViews)
    {
//        if([[view subviews] count] > 1)
//        {
//            [self keyboardHeightChangedToMoveView:scrollView notification:notification];
//        }
        if([view isFirstResponder])
        {
            CGFloat originY = view.frame.origin.y + view.frame.size.height;
            CGFloat scrollViewOirginY = scrollView.contentOffset.y + rect.size.height;
            CGFloat scrollDelta = originY - scrollViewOirginY;
            
            if(scrollDelta > 0)
            {
                if(scrollDelta < 120)
                    scrollDelta += 120;
                [scrollView setContentOffset:CGPointMake(scrollView.contentOffset.x, scrollView.contentOffset.y + scrollDelta)];
            }
            break;
        }
    }
}

- (BOOL)updateScrollViewContentOffset:(UIView *)aView scrollView:(UIScrollView *)scrollView rect:(CGRect)rect
{
    NSArray *subViews = [aView subviews];
    
    for(UIView *view in subViews)
    {
        if([[view subviews] count] > 0)
        {
            return [self updateScrollViewContentOffset:aView scrollView:scrollView rect:rect];
        }
        
        if([view isFirstResponder])
        {
//            CGFloat originY = view.frame.origin.y + view.frame.size.height;
//            CGFloat scrollViewOirginY = scrollView.contentOffset.y + rect.size.height;
//            CGFloat scrollDelta = originY - scrollViewOirginY;
//            if(scrollDelta > 0)
//            {
//                [scrollView setContentOffset:CGPointMake(scrollView.contentOffset.x, scrollView.contentOffset.y + scrollDelta)];
//            }
            return YES;
        }
    }
    return NO;
}

@end

