//
//  TGMessageTableViewCell.h
//  TGOBD
//
//  Created by James Yu on 14-3-10.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
@class TGModelMessage;

@interface TGMessageTableViewCell : UITableViewCell

@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UILabel *contentLabel;
@property (nonatomic, strong) UILabel *timeLabel;
@property (nonatomic, strong) UIImageView *line;

+ (float)getCellHeightWithContent:(NSString *)content;
- (void)setCellContent:(TGModelMessage *)message;

@end
