//
//  KKWaittingView.m
//  KKOBD
//
//  Created by zhuyc on 13-8-14.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKWaittingView.h"
#import "KKApplicationDefine.h"
#import <QuartzCore/CAAnimation.h>

@implementation KKWaittingView

- (id)initWithViewWidth:(float)width WithMessage:(NSString *)msg
{
    self = [super init];
    if (self) {
        // Initialization code
        _maxWidth = MAX(200, width);
        _messageStr = msg;
        [self initial];
    }
    self.backgroundColor = [UIColor clearColor];
    return self;
}

- (void)initial
{
    [self setFrame:CGRectMake(0, 0, 320, currentScreenHeight)];
    
    UIImage *bgImage = [UIImage imageNamed:@"bg_waitting.png"];
    CGSize bgSize = CGSizeMake(_maxWidth, bgImage.size.height);
    _contentView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, bgSize.width, bgSize.height)];
    
    UIImage *image = [UIImage imageNamed:@"icon_waitting.png"];
    CGFloat orignY = 25;
    
    _iconImv = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(bgSize.width - image.size.width), orignY, image.size.width, image.size.height)];
    _iconImv.image = image;
    [_contentView addSubview:_iconImv];
    [_iconImv release];
    
    orignY += image.size.height;
    orignY += 25;
    
    _messageLb = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY, _maxWidth, 15)];
    _messageLb.numberOfLines = 0;
    _messageLb.backgroundColor = [UIColor clearColor];
    _messageLb.textColor = [UIColor whiteColor];
    _messageLb.font = [UIFont boldSystemFontOfSize:15.0f];
    _messageLb.textAlignment = UITextAlignmentCenter;
    _messageLb.text = _messageStr;
    
    CGSize size = [_messageStr sizeWithFont:[UIFont boldSystemFontOfSize:15.0f] constrainedToSize:CGSizeMake(_maxWidth - 50, MAXFLOAT)];
    [_messageLb setFrame:CGRectMake(25, orignY, _maxWidth - 50, size.height)];
    [_contentView addSubview:_messageLb];
    [_messageLb release];
    
    orignY += size.height;
    orignY += 30;
    
    [_contentView setFrame:CGRectMake(0, 0, bgSize.width, MAX(orignY, bgSize.height))];
    _contentView.image = [bgImage stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    
    [self addSubview:_contentView];
    [_contentView release];
}

- (void)rotateTheIconView
{
    CABasicAnimation *rotateAnimation = [CABasicAnimation animationWithKeyPath:@"transform.rotation"];
    rotateAnimation.byValue = [NSNumber numberWithFloat:2*M_PI];
    rotateAnimation.duration = 1.5f;
    rotateAnimation.repeatCount=FLT_MAX;
    rotateAnimation.removedOnCompletion=NO;
    [_iconImv.layer addAnimation:rotateAnimation forKey:@"rotateAnimation"];
}

- (void)showInView:(UIView *)superView
{
    [self rotateTheIconView];
    
    UIView *View = (superView != nil) ? superView : [[UIApplication sharedApplication] keyWindow];
    self.center = View.center;
    _contentView.center = CGPointMake(self.center.x, self.center.y - ((superView != nil) ? 70 : 0));
    [View addSubview:self];
}

- (void)show
{
    [self showInView:nil];
}


- (void)hide
{
    [self removeFromSuperview];
}

- (void)dealloc
{
    _contentView = nil;
    _iconImv = nil;
    _messageLb = nil;
    _messageStr = nil;
    [super dealloc];
}
@end
