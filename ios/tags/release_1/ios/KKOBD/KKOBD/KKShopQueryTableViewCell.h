//
//  KKShopQueryTableViewCell.h
//  KKOBD
//
//  Created by zhuyc on 13-8-26.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKSmallRatingView.h"
#import "KKModelComplex.h"

@interface KKShopQueryTableViewCell : UITableViewCell
{
    UIImageView         *_iconImv;
    UILabel             *_nameLabel;
    UILabel             *_distanceLabel;
    KKSmallRatingView   *_rateView;
    UILabel             *_addressLabel;
    UILabel             *_repairLabel;
    UIImageView         *_arrowImv;
    UIImageView         *_lineImv;
}
@property (nonatomic ,retain) UIImageView   *iconImv;

- (void)setContentWith:(KKModelShopInfo *)shopInfo;

@end
