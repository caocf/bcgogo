//
//  KKRatingView.m
//  KKOBD
//
//  Created by zhuyc on 13-8-16.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKRatingView.h"
#import "KKApplicationDefine.h"

@implementation KKRatingView

- (id)initWithRank:(NSInteger)rank
{
    self = [super init];
    if (self) {
        // Initialization code
        [self setFrame:CGRectMake(0, 0, 320, 40)];
        
        float x = 20;
        UIImage *image = [UIImage imageNamed:@"icon_rate_white.png"];
        
        for (int index = 1 ; index <= 5 ; index++)
        {
            UIButton *button = [[UIButton alloc]initWithFrame:CGRectMake(x, 0.5*(40 - image.size.height), image.size.width, image.size.height)];
            button.tag = index;
            [button addTarget:self action:@selector(buttonClicked:) forControlEvents:UIControlEventTouchUpInside];
            [button setImage:image forState:UIControlStateNormal];
            [self addSubview:button];
            [button release];
            
            x += (image.size.width + 15);
        }
        
        _rateLabel = [[UILabel alloc] initWithFrame:CGRectMake(x, 11.5, 320 - x, 17)];
        _rateLabel.textAlignment = UITextAlignmentLeft;
        _rateLabel.textColor = KKCOLOR_fe7701;
        _rateLabel.font = [UIFont systemFontOfSize:17.0f];
        _rateLabel.backgroundColor = [UIColor clearColor];
        [self addSubview:_rateLabel];
        [_rateLabel release];
        
    }
    
    [self setRankViewWithRank:rank];
    self.backgroundColor = [UIColor clearColor];
    return self;
}

- (void)buttonClicked:(id)sender
{
    NSInteger index = [sender tag];
    [self setRankViewWithRank:index];
}

- (void)setRankViewWithRank:(NSInteger)rank
{
    if (rank == 1 && self.rank == 1)
        self.rank = 0;
    else
        self.rank = rank;
    
    for (UIView *subView in self.subviews)
    {
        if ([subView isKindOfClass:[UIButton class]])
        {
            UIButton *button = (UIButton *)subView;
            if (button.tag > self.rank)
                [button setImage:[UIImage imageNamed:@"icon_rate_white.png"] forState:UIControlStateNormal];
            else
                [button setImage:[UIImage imageNamed:@"icon_rate_orange.png"] forState:UIControlStateNormal];
        }
    }
    
    [self setRankLbText];
}

- (void)setRankLbText
{
    _rateLabel.text = [NSString stringWithFormat:@"%d分",self.rank];
    [_rateLabel sizeToFit];
}

- (void)dealloc
{
    _rateLabel = nil;
    [super dealloc];
}

@end
