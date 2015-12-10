//
//  TGNavigationBar.m
//  TGYF
//
//  Created by James Yu on 14-5-19.
//  Copyright (c) 2014年 james. All rights reserved.
//

#import "TGNavigationBar.h"

#define BUTTON_MARGIN 8

@implementation TGNavigationBar

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        _bagView = [[UIView alloc] initWithFrame:frame];
        [self addSubview:_bagView];
    }
    return self;
}

- (void)setNavigationBarBagColor:(UIColor *)color
{
    _bagView.backgroundColor = color;
}

- (void)setNavigationBarTitle:(NSString *)title
{
    if (_titleLbl == nil) {
        [self createTitle];
    }
    _titleLbl.text = title;
}

- (void)addNavigationBarLeftButton:(UIButton *)button
{
    CGRect rect = button.frame;
    
    button.center = CGPointMake(BUTTON_MARGIN + rect.size.width/2, 40/2 + (isIOS7 ? 20 : 0));
    
    [self addSubview:button];
}

- (void)addNavigationBarRightButton:(UIButton *)button
{
    CGRect rect = button.frame;
    
    button.center = CGPointMake(SCREEN_WIDTH - BUTTON_MARGIN - rect.size.width/2, 40/2 + (isIOS7 ? 20 : 0));
    
    [self addSubview:button];
}

- (void)createTitle
{
    _titleLbl = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 200, 30)];
    _titleLbl.center = CGPointMake(SCREEN_WIDTH/2, 44/2 + (isIOS7 ? 20 : 0));
    _titleLbl.textColor = [UIColor whiteColor];
    _titleLbl.font = [UIFont boldSystemFontOfSize:22.0f];
    _titleLbl.backgroundColor = [UIColor clearColor];
    _titleLbl.textAlignment = UITextAlignmentCenter;
    [self addSubview:_titleLbl];
}

@end
