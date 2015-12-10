//
//  KKMessagePromptCell.h
//  KKOBD
//
//  Created by zhuyc on 13-8-15.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
@protocol KKMessagePromptCellDelegate;
@class KKPollMessage;

@interface KKMessagePromptCell : UITableViewCell
{
    UILabel         *_titlelabel;
    UILabel         *_timeLabel;
    UILabel         *_messageLabel;
    UIButton        *_actionButton;
    UIView          *_lineView;
}
@property (nonatomic ,assign)id<KKMessagePromptCellDelegate> delegate;
@property (nonatomic ,retain)KKPollMessage  *message;

- (void)setContent:(KKPollMessage *)sender;
+ (float)calculateCellHeightWith:(KKPollMessage *)sender;

@end

@protocol KKMessagePromptCellDelegate
- (void)KKMessagePromptCellButtonClicked:(KKPollMessage *)message;

@end