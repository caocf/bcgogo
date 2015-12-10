//
//  TGOrderTableViewCell.m
//  TGYF
//
//  Created by James Yu on 14-5-19.
//  Copyright (c) 2014年 james. All rights reserved.
//

#import "TGOrderTableViewCell.h"

@implementation TGOrderTableViewCell

- (void)awakeFromNib
{
    _bgView.layer.borderWidth = 1;
    _bgView.layer.masksToBounds = YES;
    _bgView.layer.borderColor = COLOR_BORDER_LAYER.CGColor;
    _bgView.layer.cornerRadius = 5;
    _bgView.backgroundColor = [UIColor whiteColor];
    
    _titleBgView.backgroundColor = COLOR_CELL_TITLE;
    
    _horizonalLine.backgroundColor = COLOR_BORDER_LAYER;
    _veticleLine.backgroundColor = COLOR_BORDER_LAYER;
}


@end
