//
//  KKSearchCarTableViewCell.m
//  KKOBD
//
//  Created by zhuyc on 13-8-19.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKSearchCarTableViewCell.h"
#import "KKApplicationDefine.h"
#import <QuartzCore/CAAnimation.h>

@implementation KKSearchCarTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
        UIImage *bgimage = [UIImage imageNamed:@"bg_sBt_cellForm.png"];
        UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(11.5, 4.5, bgimage.size.width, bgimage.size.height)];
        bgImv.userInteractionEnabled = YES;
        bgImv.image = bgimage;
        
        UIImage *image = [UIImage imageNamed:@"icon_sBt_activity_blue.png"];
        _activityImv = [[UIImageView alloc] initWithFrame:CGRectMake(4, 0.5*(bgimage.size.height - image.size.height), image.size.width, image.size.height)];
        _activityImv.image = image;
        _activityImv.hidden = YES;
        [bgImv addSubview:_activityImv];
        [_activityImv release];
        
        image = [UIImage imageNamed:@"icon_sBt_linked.png"];
        _linkedImv = [[UIImageView alloc] initWithFrame:CGRectMake(4, 0.5*(bgimage.size.height - image.size.height), image.size.width, image.size.height)];
        _linkedImv.image = image;
        _linkedImv.hidden = YES;
        [bgImv addSubview:_linkedImv];
        [_linkedImv release];
        
        _deviceNameLb = [[UILabel alloc] initWithFrame:CGRectMake(19, 0.5*(bgimage.size.height - 10), 100, 10)];
        _deviceNameLb.font = [UIFont systemFontOfSize:10.f];
        _deviceNameLb.textColor = KKCOLOR_00a2cd;
        _deviceNameLb.textAlignment = UITextAlignmentLeft;
        _deviceNameLb.backgroundColor = [UIColor clearColor];
        [bgImv addSubview:_deviceNameLb];
        [_deviceNameLb release];
        
        [self addSubview:bgImv];
        [bgImv release];
        
    }
    [self setFrame:CGRectMake(0, 0, 272, 44)];
    return self;
}

- (void)setDeviceName:(NSString *)name
{
    _deviceNameLb.text = name;
    [_deviceNameLb sizeToFit];
}

- (void)setDeviceLinked:(BOOL)link
{
    self.isLinked = link;
    if (link)
    {
        [_linkedImv setHidden:NO];
        [_activityImv setHidden:YES];
    }
    else
        [_linkedImv setHidden:YES];
    
}

- (void)spin
{
    CABasicAnimation *spinAnimation = [CABasicAnimation animationWithKeyPath:@"transform.rotation"];
    spinAnimation.byValue = [NSNumber numberWithFloat:2*M_PI];
    spinAnimation.duration = 1.0f;
    spinAnimation.delegate = self;
    [_activityImv.layer addAnimation:spinAnimation forKey:@"spinAnimation"];
}


- (void)startAnimating
{
    self.isAnimating = YES;
    
    [_linkedImv setHidden:YES];
    [_activityImv setHidden:NO];
    
    [self spin];
}

- (void)stopAnimation
{
    self.isAnimating = NO;
}

- (void)animationDidStop:(CAAnimation *)anim finished:(BOOL)flag
{
    if (flag && self.isAnimating)
    {
        [self spin];
    }
}

- (void)dealloc
{
    _activityImv = nil;
    _linkedImv = nil;
    _deviceNameLb = nil;
    
    [super dealloc];
}
@end
