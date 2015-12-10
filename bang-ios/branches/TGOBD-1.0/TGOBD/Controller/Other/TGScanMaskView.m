//
//  TGScanMaskView.m
//  TGOBD
//
//  Created by Jiahai on 14-3-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGScanMaskView.h"

@implementation TGScanMaskView

- (id)initWithFrame:(CGRect)frame WithHoleImage:(UIImage *)image
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        _holeImage = image;
        _backColor = [UIColor blackColor];
        _alpha = 0.6;
        [self initial];
    }
    return self;
}

- (void)initial
{
    CGPoint holePoint = CGPointMake(0.5*(self.bounds.size.width - _holeImage.size.width), 70);
    UIView *upView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.bounds.size.width, holePoint.y)];
    upView.backgroundColor = _backColor;
    upView.alpha = _alpha;
    [self addSubview:upView];
    
    UIView *leftView = [[UIView alloc] initWithFrame:CGRectMake(0, holePoint.y, 0.5*(self.bounds.size.width - _holeImage.size.width), _holeImage.size.height)];
    leftView.backgroundColor = _backColor;
    leftView.alpha = _alpha;
    [self addSubview:leftView];
    
    UIImageView *holeImv = [[UIImageView alloc] initWithFrame:CGRectMake(holePoint.x, holePoint.y, _holeImage.size.width, _holeImage.size.height)];
    holeImv.image = _holeImage;
    holeImv.backgroundColor = [UIColor clearColor];
    [self addSubview:holeImv];
    
    UIView *rightView = [[UIView alloc] initWithFrame:CGRectMake(holePoint.x+_holeImage.size.width,holePoint.y ,holePoint.x, _holeImage.size.height)];
    rightView.backgroundColor = _backColor;
    rightView.alpha = _alpha;
    [self addSubview:rightView];
    
    UIView  *downView = [[UIView alloc] initWithFrame:CGRectMake(0, holePoint.y + _holeImage.size.height, self.bounds.size.width, self.bounds.size.height - (holePoint.y + _holeImage.size.height))];
    downView.backgroundColor = _backColor;
    downView.alpha = _alpha;
    [self addSubview:downView];
    
    UIImage *image = [UIImage imageNamed:@"bg_scan_promt.png"];
    UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(self.bounds.size.width - image.size.width), holePoint.y+ _holeImage.size.height + 33, image.size.width, image.size.height)];
    imageView.image = image;
    
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(10, 7.5, image.size.width - 23, image.size.height - 10)];
    label.numberOfLines = 2;
    label.textAlignment = UITextAlignmentLeft;
    label.text = @"将二维码图案放在取景框内，即可自动扫描";
    label.backgroundColor = [UIColor clearColor];
    label.textColor = [UIColor whiteColor];
    label.font= [UIFont systemFontOfSize:12.0];
    label.center = CGPointMake(0.5*image.size.width, 0.5*image.size.height);
    [imageView addSubview:label];
    [self addSubview:imageView];
    
}

@end
