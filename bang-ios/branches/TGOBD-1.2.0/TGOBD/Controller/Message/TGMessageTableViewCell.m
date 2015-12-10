//
//  TGMessageTableViewCell.m
//  TGOBD
//
//  Created by James Yu on 14-3-10.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGMessageTableViewCell.h"
#import "TGBasicModel.h"
#import "UIColor+FromHex.h"

@implementation TGMessageTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        _titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, 6, 114, 21)];
        _titleLabel.font = [UIFont systemFontOfSize:17];
        _titleLabel.textColor = [UIColor colorWithHex:0x1dbaf2];
        _titleLabel.textAlignment = NSTextAlignmentLeft;
        _titleLabel.minimumFontSize = 7;
        _titleLabel.backgroundColor = [UIColor clearColor];
        
        _timeLabel = [[UILabel alloc] initWithFrame:CGRectMake(147, 7, 135, 20)];
        _timeLabel.font = [UIFont systemFontOfSize:13];
        _timeLabel.textAlignment = NSTextAlignmentRight;
        _timeLabel.minimumFontSize = 7;
        _timeLabel.textColor = [UIColor grayColor];
        _timeLabel.backgroundColor = [UIColor clearColor];
        
        _contentLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, 35, 262, 1000)];
        _contentLabel.font = [UIFont systemFontOfSize:15];
        _contentLabel.textAlignment = NSTextAlignmentLeft;
        _contentLabel.numberOfLines = 0;
        _contentLabel.lineBreakMode = NSLineBreakByWordWrapping;
        _contentLabel.backgroundColor = [UIColor clearColor];
        
        _line = [[UIImageView alloc] initWithFrame:CGRectMake(0, 20, 320, 2)];
        _line.image = [UIImage imageNamed:@"cell_separateLine.png"];
        
        [self.contentView addSubview:_titleLabel];
        [self.contentView addSubview:_timeLabel];
        [self.contentView addSubview:_contentLabel];
        [self.contentView addSubview:_line];
        
    }
    
    self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    
    return self;
}

- (void)setCellContent:(TGModelMessage *)message
{
    if ([message.type isEqualToString:SHOP_REJECT_APPOINT]
        || [message.type isEqualToString:SHOP_CANCEL_APPOINT]
        || [message.type isEqualToString:CUSTOM_MESSAGE_2_APP]
        || [message.type isEqualToString:APP_VEHICLE_INSURANCE_TIME]
        || [message.type isEqualToString:APP_VEHICLE_EXAMINE_TIME]) {
        self.accessoryType = UITableViewCellAccessoryNone;
    }
    else
    {
        self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    
    _titleLabel.text = message.title;
    _timeLabel.text = message.time;
    _contentLabel.text = message.content;
    
    CGSize size = [message.content sizeWithFont:[UIFont systemFontOfSize:15] constrainedToSize:CGSizeMake(262, 200)];
    [_contentLabel setFrame:CGRectMake(20, 35, 262, size.height)];
    
    [_line setFrame:CGRectMake(0, _contentLabel.frame.origin.y + size.height + 7 , 320, 2)];
}

+ (float)getCellHeightWithContent:(NSString *)content
{
    float height = 35;
    
    CGSize size = [content sizeWithFont:[UIFont systemFontOfSize:15] constrainedToSize:CGSizeMake(262, 2000)];
    
    return height + size.height + 8.4;
}

@end
