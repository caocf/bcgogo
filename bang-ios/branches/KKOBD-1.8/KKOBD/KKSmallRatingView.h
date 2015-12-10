//
//  KKSmallRatingView.h
//  KKOBD
//
//  Created by zhuyc on 13-8-21.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface KKSmallRatingView : UIView
{
    UILabel     *_rateLabel;
    float       _rank;
}
- (id)initWithRank:(float)rank;
- (void) setSmallRankViewWithRank:(float)aLevel;

@end
