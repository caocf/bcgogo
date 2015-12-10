//
//  KKSmallRatingView.m
//  KKOBD
//
//  Created by zhuyc on 13-8-21.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKSmallRatingView.h"
#import "KKApplicationDefine.h"

@implementation KKSmallRatingView

- (id)initWithRank:(float)rank
{
    self = [super init];
    if (self) {
        // Initialization code
        [self setFrame:CGRectMake(0, 0, 90, 15)];
        UIImage *image = [UIImage imageNamed:@"icon_smallStar_full.png"];
        float x = 0;
        for (int index = 1000 ; index < 1005 ; index++)
        {
            UIImageView *imv = [[UIImageView alloc]initWithFrame:CGRectMake(x, 0.5*(15 - image.size.height), image.size.width, image.size.height)];
            imv.userInteractionEnabled = YES;
            imv.tag = index;
            imv.image = image;
            [self addSubview:imv];
            [imv release];
            
            x += image.size.width;
        }
        
        _rateLabel = [[UILabel alloc] initWithFrame:CGRectMake(62, 2.5, 100, 10)];
        _rateLabel.textAlignment = UITextAlignmentLeft;
        _rateLabel.textColor = KKCOLOR_fe7701;
        _rateLabel.font = [UIFont systemFontOfSize:10.0f];
        _rateLabel.backgroundColor = [UIColor clearColor];
        [self addSubview:_rateLabel];
        [_rateLabel release];
        
    }
    
    [self setSmallRankViewWithRank:rank];
    
    return self;
}

- (void) setSmallRankViewWithRank:(float)aLevel
{
    _rank = (aLevel > 5) ? 5 : aLevel;
    _rank = (aLevel < 0) ? 0 : aLevel;
    
    if (_rank == 0)
    {
        [self setStarViewHidden:YES];
        _rateLabel.text = @"暂无评分";
        _rateLabel.textColor = [UIColor grayColor];
        [_rateLabel setFrame:CGRectMake(0, 5, 100, 10)];
        return;
    }
    else
    {
        [self setStarViewHidden:NO];
        [_rateLabel setFrame:CGRectMake(62, 2.5, 100, 10)];
        _rateLabel.textColor = KKCOLOR_fe7701;
        
    }
    _rateLabel.text = [NSString stringWithFormat:@"%.1f分",_rank];
    
    //转化为10刻度
    NSInteger tmpLevel = aLevel * 2.0f;
    
    NSInteger fullCount = 0;
    NSInteger halfCount = 0;
    NSInteger noneCount = 0;
    fullCount = tmpLevel / 2;
    
    if (tmpLevel % 2 != 0)
    {
        halfCount = 1;
    }
    noneCount = 5 - fullCount - halfCount;
    
    NSInteger viewTag = 1000;
    UIImageView* imgView = nil;
    
    for (NSInteger i = 0; i < fullCount; i++)
    {
        imgView = (UIImageView*)[self viewWithTag:viewTag];
        imgView.image = [UIImage imageNamed:@"icon_smallStar_full.png"];
        viewTag++;
    }
    
    if (halfCount > 0)
    {
        imgView = (UIImageView*)[self viewWithTag:viewTag];
        imgView.image = [UIImage imageNamed:@"icon_smallStar_half.png"];
        viewTag++;
    }
    
    for (NSInteger i = 0; i < noneCount; i++)
    {
        imgView = (UIImageView*)[self viewWithTag:viewTag];
        imgView.image = [UIImage imageNamed:@"icon_smallStar_none.png"];
        viewTag++;
    }

}

- (void)setStarViewHidden:(BOOL)hidden
{
    for (UIView *view in self.subviews)
    {
        if ([view isKindOfClass:[UIImageView class]])
            view.hidden = hidden;
    }
}

- (void)dealloc
{
    _rateLabel = nil;
    [super dealloc];
}
@end
