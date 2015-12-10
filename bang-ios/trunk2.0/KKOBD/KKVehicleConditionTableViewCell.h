//
//  KKVehicleConditionTableViewCell.h
//  KKOBD
//
//  Created by zhuyc on 13-8-22.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
@class KKModelDTCMessage;

@interface KKVehicleConditionTableViewCell : UITableViewCell
{
    UIImageView         *_categoryImv;
    UILabel             *_titleLabel;
    UILabel             *_timeLabel;
    UILabel             *_detailLabel;
    UIImageView         *_arrowImv;
    UIImageView         *_lineImv;
}

- (void)setContent:(KKModelDTCMessage *)content;

+ (float)calculateVehicleConditionTableViewCellHeightWithContent:(KKModelDTCMessage *)content;

@end
