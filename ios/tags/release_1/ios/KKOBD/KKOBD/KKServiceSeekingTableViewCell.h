//
//  KKServiceSeekingTableViewCell.h
//  KKOBD
//
//  Created by zhuyc on 13-8-21.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKModelComplex.h"

@interface KKServiceSeekingTableViewCell : UITableViewCell
{
    UIImageView         *_iconImv;
    UIImageView         *_circleImv;
    UILabel             *_nameLabel;
    UILabel             *_timelabel;
    UIImageView         *_mark1Imv;
    UILabel             *_mark1Label;
    UIImageView         *_mark2Imv;
    UILabel             *_mark2Label;
    UIImageView         *_mark3Imv;
    UILabel             *_mark3Label;
    UIImageView         *_rightArrowImv;
    UIImageView         *_lineImv;
}
@property (nonatomic ,retain)UIImageView    *iconImv;

- (void)setCellContent:(KKModelService *)obj;

@end
