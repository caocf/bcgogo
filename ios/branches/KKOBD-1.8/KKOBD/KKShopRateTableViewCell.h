//
//  KKShopRateTableViewCell.h
//  KKOBD
//
//  Created by Jiahai on 14-2-23.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKSmallRatingView.h"
@class KKModelComment;

@interface KKShopRateTableViewCell : UITableViewCell
{
    UILabel             *_nameLabel;
    UILabel             *_timeLabel;
    KKSmallRatingView        *_ratingView;
    UILabel             *_descLabel;
}

-(void) setCommentAndRefreshUI:(KKModelComment *)aComment;

+ (float)calculateVehicleConditionTableViewCellHeightWithContent:(KKModelComment *)aComment;
@end
