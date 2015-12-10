//
//  KKCarStatusView.m
//  KKOBD
//
//  Created by zhuyc on 13-8-9.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKCarStatusView.h"
#import "KKApplicationDefine.h"

@implementation KKCarStatusView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        [self initial];
    }
    self.backgroundColor = [UIColor clearColor];
    return self;
}

- (void)dealloc
{
    _bgImv = nil;
    _carStatusImv = nil;
    self.carModelLb = nil;
    [super dealloc];
}

- (void)initial
{    
    _bgImv = [[UIImageView alloc] initWithFrame:self.bounds];
    _bgImv.backgroundColor = [UIColor clearColor];
    _bgImv.image = [[UIImage imageNamed:@"bg_carStatusBar.png"] stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    [self addSubview:_bgImv];
    [_bgImv release];
    
    _carModelLb = [[UILabel alloc] initWithFrame:CGRectMake(15, 6.5, 150, 14)];
    _carModelLb.textAlignment = UITextAlignmentLeft;
    _carModelLb.textColor = [UIColor whiteColor];
    _carModelLb.backgroundColor = [UIColor clearColor];
    _carModelLb.font = [UIFont boldSystemFontOfSize:12.0f];
    _carModelLb.text = @"车型:";
    [self addSubview:_carModelLb];
    [_carModelLb release];
    
    _carStatusImv = [[UIImageView alloc] initWithFrame:CGRectMake(self.frame.size.width - 50, 0, 50, self.frame.size.height)];
    _carStatusImv.contentMode = UIViewContentModeCenter;
    _carStatusImv.backgroundColor = [UIColor clearColor];
    [self addSubview:_carStatusImv];
    [_carStatusImv release];
    
    UIButton *shopButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 0.5*self.frame.size.width, self.frame.size.height)];
    [shopButton addTarget:self action:@selector(shopButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    shopButton.backgroundColor = [UIColor clearColor];
    [self addSubview:shopButton];
    
    
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(self.frame.size.width - 50, 0, 50, self.frame.size.height)];
    button.backgroundColor = [UIColor clearColor];
    [button addTarget:self action:@selector(buttonClicked) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:button];
    [button release];
}

- (void)buttonClicked
{
    if (self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(KKCarStatusViewStatusButtonClicked:)])
    {
        [self.delegate KKCarStatusViewStatusButtonClicked:_carStatus];
    }
}

- (void)shopButtonClicked
{
    if (self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(KKCarStatusViewShopButtonClicked)])
        [self.delegate KKCarStatusViewShopButtonClicked];
}

- (void)setCarStatus:(KKCarStatusType)carStatus
{
    _carStatus = carStatus;
    [self setCarStatusImage];
}

- (void)setCarStatusImage
{
    UIImage *image = nil;
    switch (_carStatus) {
        case e_CarWell:
            image = [UIImage imageNamed:@"icon_carStatusBar_well.png"];
            break;
        case e_CarAlarm:
            image = [UIImage imageNamed:@"icon_carStatusBar_warning.png"];
            break;
        case e_CarNotOnLine:
            image = [UIImage imageNamed:@"icon_carStatusBar_None.png"];
            break;
        default:
            break;
    }
    _carStatusImv.image = image;
}


@end
