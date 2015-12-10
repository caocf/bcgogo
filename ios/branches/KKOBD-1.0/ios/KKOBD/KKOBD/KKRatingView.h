//
//  KKRatingView.h
//  KKOBD
//
//  Created by zhuyc on 13-8-16.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface KKRatingView : UIView
{
    UILabel     *_rateLabel;
}
@property (nonatomic ,assign)NSInteger  rank;

- (id)initWithRank:(NSInteger)rank;

@end
