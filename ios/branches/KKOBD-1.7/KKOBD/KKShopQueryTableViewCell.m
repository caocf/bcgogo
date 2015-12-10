//
//  KKShopQueryTableViewCell.m
//  KKOBD
//
//  Created by zhuyc on 13-8-26.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKShopQueryTableViewCell.h"
#import <QuartzCore/QuartzCore.h>
#import "KKApplicationDefine.h"

@implementation KKShopQueryTableViewCell
@synthesize iconImv = _iconImv;

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
        UIImage *image = [UIImage imageNamed:@"bg_shopq_cell_circle.png"];
        UIImageView *bgIconImv = [[UIImageView alloc] initWithFrame:CGRectMake(15, 10, image.size.width, image.size.height)];
        bgIconImv.image = image;
        
        UIImageView *iconImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, image.size.width - 1, image.size.height - 1)];
        iconImageView.layer.cornerRadius = 0.5*(image.size.width - 1);
        iconImageView.layer.masksToBounds = YES;
        iconImageView.tag = 100;
        iconImageView.center=  CGPointMake(0.5*image.size.width, 0.5*image.size.height);
        self.iconImv = iconImageView;
        [bgIconImv addSubview:iconImageView];
        [iconImageView release];
        
        [self addSubview:bgIconImv];
        [bgIconImv release];
        
        float height = 10;
        
        _nameLabel = [[UILabel alloc] initWithFrame:CGRectMake(98, height , 130, 15)];
        _nameLabel.backgroundColor = [UIColor clearColor];
        _nameLabel.textColor = KKCOLOR_3359ac;
        _nameLabel.textAlignment = UITextAlignmentLeft;
        _nameLabel.font = [UIFont boldSystemFontOfSize:15.f];
        [self addSubview:_nameLabel];
        [_nameLabel release];
        
        _distanceLabel = [[UILabel alloc] initWithFrame:CGRectMake(245, height+7, 70, 8)];
        _distanceLabel.backgroundColor = [UIColor clearColor];
        _distanceLabel.font = [UIFont systemFontOfSize:8.f];
        _distanceLabel.textAlignment = UITextAlignmentLeft;
        _distanceLabel.textColor = KKCOLOR_717171;
        [self addSubview:_distanceLabel];
        [_distanceLabel release];
        
        height += 18;
        
        _rateView = [[KKSmallRatingView alloc] initWithRank:0];
        [_rateView setFrame:CGRectMake(98, height, 100, 15)];
        [self addSubview:_rateView];
        [_rateView release];
        
        height += 21;
        
        image = [UIImage imageNamed:@"icon_shopq_cell_map.png"];
        UIImageView *mark2Imv = [[UIImageView alloc] initWithFrame:CGRectMake(98, height, image.size.width, image.size.height)];
        mark2Imv.backgroundColor = [UIColor clearColor];
        mark2Imv.userInteractionEnabled = YES;
        mark2Imv.image = image;
        [self addSubview:mark2Imv];
        [mark2Imv release];
        
        _addressLabel = [[UILabel alloc] initWithFrame:CGRectMake(116, height, 165, 12)];
        _addressLabel.backgroundColor = [UIColor clearColor];
        _addressLabel.font = [UIFont systemFontOfSize:11.f];
        _addressLabel.textAlignment = UITextAlignmentLeft;
        _addressLabel.textColor = KKCOLOR_717171;
        [self addSubview:_addressLabel];
        [_addressLabel release];

        height += 18;
        
        image = [UIImage imageNamed:@"icon_shopq_cell_rep.png"];
        UIImageView *mark3Imv = [[UIImageView alloc] initWithFrame:CGRectMake(98, height, image.size.width, image.size.height)];
        mark3Imv.backgroundColor = [UIColor clearColor];
        mark3Imv.userInteractionEnabled = YES;
        mark3Imv.image = image;
        [self addSubview:mark3Imv];
        [mark3Imv release];
        
        _repairLabel = [[UILabel alloc] initWithFrame:CGRectMake(116, height, 145, 12)];
        _repairLabel.backgroundColor = [UIColor clearColor];
        _repairLabel.font = [UIFont systemFontOfSize:11.f];
        _repairLabel.textAlignment = UITextAlignmentLeft;
        _repairLabel.textColor = KKCOLOR_717171;
        [self addSubview:_repairLabel];
        [_repairLabel release];
        
        image = [UIImage imageNamed:@"icon_shopq_cell_arr.png"];
        _arrowImv = [[UIImageView alloc] initWithFrame:CGRectMake(292, 0.5*(87 - image.size.height), image.size.width, image.size.height)];
        _arrowImv.userInteractionEnabled = YES;
        _arrowImv.backgroundColor = [UIColor clearColor];
        _arrowImv.image = image;
        [self addSubview:_arrowImv];
        [_arrowImv release];
        
        image = [UIImage imageNamed:@"icon_setting_separateLine.png"];
        _lineImv = [[UIImageView alloc] initWithFrame:CGRectMake(10, 87-image.size.height, 300, image.size.height)];
        _lineImv.backgroundColor = [UIColor clearColor];
        _lineImv.userInteractionEnabled = YES;
        _lineImv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
        [self addSubview:_lineImv];
        [_lineImv release];
    }
    return self;
}

- (void)setContentWith:(KKModelShopInfo *)shopInfo
{
    _nameLabel.text = shopInfo.name;
    _distanceLabel.text = [NSString stringWithFormat:@"距离: %.2fkm",shopInfo.distance];
    [_rateView setSmallRankViewWithRank:shopInfo.totalScore];
    _addressLabel.text = shopInfo.address;
    _repairLabel.text = shopInfo.serviceScope;
}

- (void)dealloc
{
    self.iconImv = nil;
    [super dealloc];
}

@end
