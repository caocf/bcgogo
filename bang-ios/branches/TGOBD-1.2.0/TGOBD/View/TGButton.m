//
//  TGButton.m
//  TGOBD
//
//  Created by James Yu on 14-3-22.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGButton.h"

@implementation TGButton

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}

- (id)initWithFrame:(CGRect)frame tipsFrame:(CGRect)tipsFrame notificationName:(NSString *)notificationName
{
    if (self = [super initWithFrame:frame]) {
        
        _titleImageView = [[UIImageView alloc] initWithFrame:tipsFrame];
        
        [self addSubview:_titleImageView];
        [self addSubview:_titleTip];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleNotification:) name:notificationName object:nil];
    }
    return self;
}

- (void)handleNotification:(NSNotification *)notification
{
    id obj = notification.object;
    if (obj == nil) {
        _titleTip.hidden = YES;
        _titleImageView.hidden = YES;
        return;
    }
    NSAssert([obj isKindOfClass:[NSDictionary class]], @"TGButton 通知传值必须是 字典类型");
    
    NSDictionary *dict = (NSDictionary *)obj;
    
    if ([[dict objectForKey:SHOW_TIPIMG] boolValue]) {
        _titleImageView.hidden = NO;
        if ([dict objectForKey:TIPIMG_NAME] != nil) {
            _titleImageView.image = [UIImage imageNamed:[dict objectForKey:TIPIMG_NAME]];
        }
    }
    else
    {
        _titleImageView.hidden = YES;
    }
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
