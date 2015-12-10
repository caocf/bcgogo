//
//  TGTrafficViolationTableViewCell.m
//  TGOBD
//
//  Created by James Yu on 14-4-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGTrafficViolationTableViewCell.h"
#import "UIColor+FromHex.h"
#import "TGBasicModel.h"
@implementation TGTrafficViolationTableViewCell

- (void)awakeFromNib
{
    _bgView.backgroundColor = TGRGBA(218, 238, 249, 1);
    _bgView.layer.cornerRadius = 6;
    
    _content.textColor = [UIColor redColor];
}

- (void)setCellContent:(TGModelViolationInfo *)violationInfo
{
    _time.text = violationInfo.date;
    _address.text = violationInfo.area;
    _content.text = violationInfo.act;
    _money.text = violationInfo.money.length == 0 ? @"0" : violationInfo.money;
    _score.text = violationInfo.fen.length == 0 ? @"0" : violationInfo.fen;
    
    [_address setFrame:CGRectMake(56, 34, 235, 21)];
    [_contentLbl setFrame:CGRectMake(10, 72, 42, 21)];
    [_content setFrame:CGRectMake(56, 71, 235, 21)];
    
    CGSize size = [violationInfo.area sizeWithFont:[UIFont systemFontOfSize:17] constrainedToSize:CGSizeMake(235, 900) lineBreakMode:NSLineBreakByWordWrapping];
    if (size.height > 21) {
        [_address setFrame:CGRectMake(_address.frame.origin.x, _address.frame.origin.y, size.width, size.height)];
        [_content setFrame:CGRectMake(56, 71 + size.height - 21, _content.frame.size.width, _content.frame.size.height)];
        [_contentLbl setFrame:CGRectMake(10, 72 + size.height - 21, _contentLbl.frame.size.width, _contentLbl.frame.size.height)];
    }
    
    size = [violationInfo.act sizeWithFont:[UIFont systemFontOfSize:17] constrainedToSize:CGSizeMake(235, 900) lineBreakMode:NSLineBreakByWordWrapping];
    
    if (size.height > 21) {
        [_content setFrame:CGRectMake(_content.frame.origin.x, _content.frame.origin.y, size.width, size.height)];
    }
}

+ (CGFloat)getCellHeight:(TGModelViolationInfo *)violationInfo
{
    CGFloat height = 143;
    
    CGSize size = [violationInfo.area sizeWithFont:[UIFont systemFontOfSize:17] constrainedToSize:CGSizeMake(235, 900) lineBreakMode:NSLineBreakByWordWrapping];
    if (size.height > 21) {
        height = height + size.height - 21;
    }
    
    size = [violationInfo.act sizeWithFont:[UIFont systemFontOfSize:17] constrainedToSize:CGSizeMake(235, 900) lineBreakMode:NSLineBreakByWordWrapping];
    
    if (size.height > 21) {
        height = height + size.height - 21;
    }
    
    return height;
}
@end
