//
//  TGPublicNoticeTableViewCell.m
//  TGOBD
//
//  Created by James Yu on 14-4-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGPublicNoticeTableViewCell.h"
#import "TGHelper.h"
#import "NSDate+millisecond.h"
#import "UIImageView+AFNetworking.h"

@implementation TGPublicNoticeTableViewCell

- (void)awakeFromNib
{
    _bgView.backgroundColor = [UIColor whiteColor];
    _bgView.layer.cornerRadius = 6;
    _bgView.layer.borderWidth = 0.5;
    _bgView.layer.borderColor = COLOR_LAYER_BORDER.CGColor;
    
    _time.backgroundColor = [UIColor colorWithRed:194/250.0 green:194/250.0 blue:194/250.0 alpha:0.3];
    _time.textColor = [UIColor whiteColor];
    _time.layer.cornerRadius = 6;
    
    _title.backgroundColor = [UIColor clearColor];
    
    _image.backgroundColor = [UIColor colorWithRed:170/250.0 green:230/250.0 blue:241/250.0 alpha:0.5];
    
}

- (void)setCellContent:(TGModelPublicNoticeInfo *)notice
{
    [_image setImageWithURL:[NSURL URLWithString:notice.imageUrl] placeholderImage:nil];
    _time.text = [NSDate dateStringWithTimeIntervalSince1970WithMillisecond:notice.editDate formatter:nil];
    _title.text = notice.title;
}

@end
