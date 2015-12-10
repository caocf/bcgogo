//
//  KKServiceSeekingTableViewCell.m
//  KKOBD
//
//  Created by zhuyc on 13-8-21.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKServiceSeekingTableViewCell.h"
#import <QuartzCore/QuartzCore.h>
#import "KKApplicationDefine.h"
#import "KKUtils.h"

@implementation KKServiceSeekingTableViewCell
@synthesize iconImv = _iconImv;

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
        UIImage *image = [UIImage imageNamed:@"icon_serviceSeeking_circle.png"];
        _circleImv = [[UIImageView alloc] initWithFrame:CGRectMake(13, 13, image.size.width, image.size.height)];
        _circleImv.backgroundColor = [UIColor clearColor];
        _circleImv.userInteractionEnabled = YES;
        _circleImv.image = image;
        
        
        UIImageView *iconImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 64, 64)];
        iconImv.backgroundColor = [UIColor clearColor];
        iconImv.userInteractionEnabled = YES;
        iconImv.layer.cornerRadius = 32;
        iconImv.layer.masksToBounds = YES;
        iconImv.center= CGPointMake(image.size.width/2, image.size.height/2);
        self.iconImv = iconImv;
        [_circleImv addSubview:iconImv];
        [iconImv release];
        
        [self addSubview:_circleImv];
        [_circleImv release];
        
        float height = 15;
        
        _nameLabel = [[UILabel alloc] initWithFrame:CGRectMake(98, height, 130, 15)];
        _nameLabel.backgroundColor = [UIColor clearColor];
        _nameLabel.font = [UIFont boldSystemFontOfSize:15.f];
        _nameLabel.textColor = KKCOLOR_3359ac;
        _nameLabel.textAlignment = UITextAlignmentLeft;
        [self addSubview:_nameLabel];
        [_nameLabel release];
        
        _timelabel = [[UILabel alloc] initWithFrame:CGRectMake(235, height+8, 80, 8)];
        _timelabel.backgroundColor = [UIColor clearColor];
        _timelabel.font = [UIFont systemFontOfSize:8.f];
        _timelabel.textAlignment = UITextAlignmentLeft;
        _timelabel.textColor = KKCOLOR_717171;
        [self addSubview:_timelabel];
        [_timelabel release];
        
        height += 25;
        
        image = [UIImage imageNamed:@"icon_serviceSeeking_repair.png"];
        _mark2Imv = [[UIImageView alloc] initWithFrame:CGRectMake(100, height, image.size.width, image.size.height)];
        _mark2Imv.backgroundColor = [UIColor clearColor];
        _mark2Imv.userInteractionEnabled = YES;
        _mark2Imv.image = image;
        [self addSubview:_mark2Imv];
        [_mark2Imv release];
        
        _mark2Label = [[UILabel alloc] initWithFrame:CGRectMake(115, height, 160, 10)];
        _mark2Label.backgroundColor = [UIColor clearColor];
        _mark2Label.font = [UIFont systemFontOfSize:10.f];
        _mark2Label.numberOfLines = 0;
        _mark2Label.textAlignment = UITextAlignmentLeft;
        _mark2Label.textColor = KKCOLOR_717171;
        [self addSubview:_mark2Label];
        [_mark2Label release];
        
        height += 18;
        
        image = [UIImage imageNamed:@"icon_serviceSeeking_heart.png"];
        _mark3Imv = [[UIImageView alloc] initWithFrame:CGRectMake(100, height, image.size.width, image.size.height)];
        _mark3Imv.backgroundColor = [UIColor clearColor];
        _mark3Imv.userInteractionEnabled = YES;
        _mark3Imv.image = image;
        [self addSubview:_mark3Imv];
        [_mark3Imv release];
        
        _mark3Label = [[UILabel alloc] initWithFrame:CGRectMake(115, height, 160, 10)];
        _mark3Label.backgroundColor = [UIColor clearColor];
        _mark3Label.font = [UIFont systemFontOfSize:10.f];
        _mark3Label.textAlignment = UITextAlignmentLeft;
        _mark3Label.textColor = KKCOLOR_717171;
        _mark3Label.numberOfLines = 0;
        [self addSubview:_mark3Label];
        [_mark3Label release];
        
        image = [UIImage imageNamed:@"icon_serviceSeeking_arrow.png"];
        _rightArrowImv = [[UIImageView alloc] initWithFrame:CGRectMake(292, 0.5*(95 - image.size.height), image.size.width, image.size.height)];
        _rightArrowImv.userInteractionEnabled = YES;
        _rightArrowImv.backgroundColor = [UIColor clearColor];
        _rightArrowImv.image = image;
        [self addSubview:_rightArrowImv];
        [_rightArrowImv release];
        
        image = [UIImage imageNamed:@"icon_setting_separateLine.png"];
        _lineImv = [[UIImageView alloc] initWithFrame:CGRectMake(10, 95-image.size.height, 300, image.size.height)];
        _lineImv.backgroundColor = [UIColor clearColor];
        _lineImv.userInteractionEnabled = YES;
        _lineImv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
        [self addSubview:_lineImv];
        [_lineImv release];
    }
    return self;
}

- (void)setCellContent:(KKModelService *)obj
{
    _nameLabel.text = obj.shopName;
    _timelabel.text = [KKUtils ConvertDataToString:[NSDate dateWithTimeIntervalSince1970:obj.orderTime/1000]];
    _mark2Label.text = obj.content;
    _mark3Label.text = obj.status;

}

- (void)dealloc
{
    self.iconImv = nil;
    _circleImv = nil;
    _nameLabel = nil;
    _timelabel = nil;
    _mark1Imv = nil;
    _mark1Label = nil;
    _mark2Imv = nil;
    _mark2Label = nil;
    _mark3Imv = nil;
    _mark3Label = nil;
    _rightArrowImv = nil;
    _lineImv = nil;

    [super dealloc];
}
@end
