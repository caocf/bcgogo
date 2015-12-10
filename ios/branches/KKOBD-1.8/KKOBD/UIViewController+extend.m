//
//  UIViewController+extend.m
//  KKShowBooks
//
//  Created by zhuyc on 13-7-8.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "UIViewController+extend.h"
#import "KKApplicationDefine.h"
#import "KKCustomTextField.h"

@implementation UIViewController (extend)

- (void)setVcEdgesForExtendedLayout
{
    if (currentSystemVersion >= 7.0)
        if ([self respondsToSelector:@selector(setEdgesForExtendedLayout:)])
            [self setEdgesForExtendedLayout:0];
}

- (void)setNavigationBarTitle:(NSString *)text
{
    UILabel *titleLb = (UILabel *)self.navigationItem.titleView;
    if (titleLb)
    {
        [titleLb setText:text];
    }
}

- (void)initTitleView
{
    UILabel *titleLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0, 0, 200, 30)] autorelease];
	titleLabel.center = CGPointMake(320/2, 44/2);
	titleLabel.textColor = [UIColor whiteColor];
	titleLabel.font = [UIFont boldSystemFontOfSize:20.0f];
	titleLabel.backgroundColor = [UIColor clearColor];
	titleLabel.textAlignment = UITextAlignmentCenter;
	self.navigationItem.titleView = titleLabel;
}

- (float)getOrignY
{
    return (currentSystemVersion >= 7.0) ? 20 : 0;
}

- (void)addStatusBarBackgroundView
{
    if (currentSystemVersion >= 7.0)
    {
        UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 20)];
        view.backgroundColor = [UIColor blackColor];
        [self.view addSubview:view];
        [view release];
    }
}

- (void)resignKeyboardNotification
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didKeyboardNotification:) name:UIKeyboardDidShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didKeyboardNotification:) name:UIKeyboardWillHideNotification object:nil];
}

- (void)removeKeyboardNotification
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardDidShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillHideNotification object:nil];
}

- (void)setBachGroundView
{
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 320,  self.view.bounds.size.height)];
    bgImv.image = [[UIImage imageNamed:@"bg_background.png"] stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor redColor];
    [self.view addSubview:bgImv];
    [bgImv release];
}

- (void)resignVcFirstResponder
{
     [[UIApplication sharedApplication] sendAction:@selector(resignFirstResponder) to:nil from:nil forEvent:nil];
}

- (KKCustomTextField *)getCurrentFirstResponderTextFieldFromView:(UIView *)view
{
    for (UIView *subview in view.subviews)
    {
        if ([subview isKindOfClass:[KKCustomTextField class]])
        {
            KKCustomTextField *textField = (KKCustomTextField *)subview;
            if ([textField.textField isFirstResponder])
                return textField;
        }
    }
    return nil;
}

- (KKCustomTextField *)findNextTextFieldViewFromView:(UIView *)view WithIndex:(NSInteger)index
{
    for (UIView *subview in view.subviews)
    {
        if ([subview isKindOfClass:[KKCustomTextField class]])
        {
            KKCustomTextField *textField = (KKCustomTextField *)subview;
            if (textField.index == index)
                return textField;
        }
    }
    return nil;
}

@end
