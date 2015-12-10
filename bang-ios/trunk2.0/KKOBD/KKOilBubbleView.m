//
//  KKOilBubbleView.m
//  KKOBD
//
//  Created by Jiahai on 13-12-11.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKOilBubbleView.h"
#import "KKModelBaseElement.h"
#import "KKHelper.h"

@implementation KKOilBubbleView

//static CGFloat kTransitionDuration = 0.45f;
static const float kBorderWidth = 10.0f;
static const float kEndCapWidth = 20.0f;
static const float kMaxLabelWidth = 220.0f;

- (id)init
{
    self = [super init];
    if (self) {
        // Initialization code
        titleLabel = [[UILabel alloc] init];
        titleLabel.backgroundColor = [UIColor clearColor];
        titleLabel.font = [UIFont systemFontOfSize:14.0f];
        titleLabel.textColor = [UIColor whiteColor];
        [self addSubview:titleLabel];
        
        distanceLabel = [[UILabel alloc] init];
        distanceLabel.backgroundColor = [UIColor clearColor];
        distanceLabel.numberOfLines = 0;
        distanceLabel.textColor = [UIColor whiteColor];
        distanceLabel.font = [UIFont systemFontOfSize:12.0f];
        [self addSubview:distanceLabel];
        
        detailLabel = [[UILabel alloc] init];
        detailLabel.backgroundColor = [UIColor clearColor];
        detailLabel.numberOfLines = 0;
        detailLabel.textColor = [UIColor whiteColor];
        detailLabel.font = [UIFont systemFontOfSize:12.0f];
        [self addSubview:detailLabel];
        
        UIImage *disclosureImage = [UIImage imageNamed:@"btn_Disclosure.png"];
        CGRect rect = CGRectZero;
        rect.size = disclosureImage.size;
        rightButton = [[UIButton alloc] initWithFrame:rect];
        [rightButton setImage:disclosureImage forState:UIControlStateNormal];
        [rightButton addTarget:self action:@selector(rightBtnClicked) forControlEvents:UIControlEventTouchUpInside];
        rightButton.userInteractionEnabled = YES;
        [self addSubview:rightButton];
        rightButton.hidden = NO;
        
        UIImage *imageNormal, *imageHighlighted;
        imageNormal = [[UIImage imageNamed:@"mapapi.bundle/images/icon_paopao_middle_left.png"] stretchableImageWithLeftCapWidth:10 topCapHeight:13];
        imageHighlighted = [[UIImage imageNamed:@"mapapi.bundle/images/icon_paopao_middle_left_highlighted.png"]
                            stretchableImageWithLeftCapWidth:10 topCapHeight:13];
        UIImageView *leftBgd = [[UIImageView alloc] initWithImage:imageNormal
                                                 highlightedImage:imageHighlighted];
        leftBgd.tag = 11;
        
        imageNormal = [[UIImage imageNamed:@"mapapi.bundle/images/icon_paopao_middle_right.png"] stretchableImageWithLeftCapWidth:10 topCapHeight:13];
        imageHighlighted = [[UIImage imageNamed:@"mapapi.bundle/images/icon_paopao_middle_right_highlighted.png"]
                            stretchableImageWithLeftCapWidth:10 topCapHeight:13];
        UIImageView *rightBgd = [[UIImageView alloc] initWithImage:imageNormal
                                                  highlightedImage:imageHighlighted];
        rightBgd.tag = 12;
        
        [self addSubview:leftBgd];
        [self sendSubviewToBack:leftBgd];
        [self addSubview:rightBgd];
        [self sendSubviewToBack:rightBgd];
        [leftBgd release];
        [rightBgd release];
    }
    return self;
}

/*
 // Only override drawRect: if you perform custom drawing.
 // An empty implementation adversely affects performance during animation.
 - (void)drawRect:(CGRect)rect
 {
 // Drawing code
 }
 */

- (void)dealloc {
    self.oilStation = nil;
    
    [titleLabel release];
    [detailLabel release];
    [rightButton release];
    
    [super dealloc];
}

-(void) setUIFit
{
    [self showFromRect:CGRectZero];
}

- (BOOL)showFromRect:(CGRect)rect {
    if (self.oilStation == nil) {
        return NO;
    }
    
    titleLabel.text = self.oilStation.name;
    titleLabel.frame = CGRectZero;
    [titleLabel sizeToFit];
    CGRect rect1 = titleLabel.frame;
    rect1.origin = CGPointMake(kBorderWidth, kBorderWidth);
    if (rect1.size.width > kMaxLabelWidth) {
        rect1.size.width = kMaxLabelWidth;
    }
    titleLabel.frame = rect1;
    
    distanceLabel.text = [NSString stringWithFormat:@"距离：%@",[KKHelper meterToKiloFromInt:self.oilStation.distance]];
    distanceLabel.frame = CGRectZero;
    [distanceLabel sizeToFit];
    CGRect rect13 = distanceLabel.frame;
    rect13.origin.x = kBorderWidth;
    rect13.origin.y = rect1.size.height + 2*kBorderWidth;
    if (rect13.size.width > kMaxLabelWidth) {
        rect13.size.width = kMaxLabelWidth;
    }
    distanceLabel.frame = rect13;
    
    detailLabel.text = [NSString stringWithFormat:@"今日油价：0#:%@, 93#:%@, 97#:%@",self.oilStation.E0,self.oilStation.E93,self.oilStation.E97];
    detailLabel.frame = CGRectZero;
//    l.lineBreakMode = UILineBreakModeWordWrap; l.numberOfLines = 0;
    [detailLabel sizeToFit];
    CGRect rect2 = detailLabel.frame;
    rect2.origin.x = kBorderWidth;
    rect2.origin.y = rect1.size.height + rect13.size.height + 2*kBorderWidth;
    if (rect2.size.width > kMaxLabelWidth) {
        rect2.size.width = kMaxLabelWidth;
    }
    detailLabel.frame = rect2;
    
    CGFloat longWidth = (rect1.size.width > rect2.size.width) ? rect1.size.width : rect2.size.width;
    CGRect rect0 = self.frame;
    rect0.size.height = rect1.size.height + rect2.size.height + rect13.size.height + 2*kBorderWidth + kEndCapWidth;
    rect0.size.width = longWidth + 2*kBorderWidth;
    if (rightButton.hidden == NO) {
        CGRect rect3 = rightButton.frame;
        rect3.origin.x = longWidth + 2*kBorderWidth;
        rect3.origin.y = kBorderWidth;
        rightButton.frame = rect3;
        rect0.size.width += rect3.size.width + kBorderWidth;
    }
    
    self.frame = rect0;
 
    CGFloat halfWidth = rect0.size.width/2;
    UIView *image = [self viewWithTag:11];
    CGRect iRect = CGRectZero;
    iRect.size.width = halfWidth;
    iRect.size.height = rect0.size.height;
    image.frame = iRect;
    image = [self viewWithTag:12];
    iRect.origin.x = halfWidth;
    image.frame = iRect;
    
    return YES;
}
-(void) rightBtnClicked
{
    if([self.delegate respondsToSelector:@selector(rightBtnClicked)])
       [self.delegate rightBtnClicked];
}
@end
