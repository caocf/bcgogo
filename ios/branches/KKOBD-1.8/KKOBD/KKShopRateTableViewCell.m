//
//  KKShopRateTableViewCell.m
//  KKOBD
//
//  Created by Jiahai on 14-2-23.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "KKShopRateTableViewCell.h"
#import "KKHelper.h"
#import "KKApplicationDefine.h"

@implementation KKShopRateTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        _nameLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        [_nameLabel setFrame:CGRectMake(12, 5, 160, 13)];
        _nameLabel.backgroundColor = [UIColor clearColor];
        _nameLabel.textColor = KKCOLOR_3359ac;
        _nameLabel.textAlignment = UITextAlignmentLeft;
        _nameLabel.font = [UIFont boldSystemFontOfSize:13.f];
        [self addSubview:_nameLabel];
        [_nameLabel release];
        
        _timeLabel = [[UILabel alloc] initWithFrame:CGRectMake(180, 6, 100, 12)];
        _timeLabel.backgroundColor= [UIColor clearColor];
        _timeLabel.textColor = KKCOLOR_777777;
        _timeLabel.textAlignment = UITextAlignmentLeft;
        _timeLabel.font = [UIFont systemFontOfSize:12.f];
        [self addSubview:_timeLabel];
        [_timeLabel release];

        _ratingView = [[KKSmallRatingView alloc] initWithRank:0];
        _ratingView.frame = CGRectMake(12, 20, 200, 16);
        [self addSubview:_ratingView];
        [_ratingView release];
        
        _descLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        _descLabel.backgroundColor= [UIColor clearColor];
        _descLabel.textColor = KKCOLOR_777777;
        _descLabel.textAlignment = UITextAlignmentLeft;
        _descLabel.font = [UIFont systemFontOfSize:13.f];
        _descLabel.numberOfLines = 0;
        [self addSubview:_descLabel];
        [_descLabel release];
    }
    return self;
}

-(void) setCommentAndRefreshUI:(KKModelComment *)aComment
{
    _nameLabel.text = aComment.commentatorName;
    _timeLabel.text = aComment.commentTimeStr;
    [_ratingView setSmallRankViewWithRank:aComment.commentScore];
    
    NSInteger height = 38;
    
    CGSize size = [aComment.commentContent sizeWithFont:[UIFont systemFontOfSize:13.f] constrainedToSize:CGSizeMake(230, MAXFLOAT)];
    NSLog(@"%f",size.height);
    _descLabel.frame = CGRectMake(12, height, size.width, size.height);
    _descLabel.text = aComment.commentContent;
    
    height += size.height;
    height += 8;
    
    if(height < 46)
        height = 46;
}

+ (float)calculateVehicleConditionTableViewCellHeightWithContent:(KKModelComment *)aComment
{
    float height = 38;
    CGSize size = [aComment.commentContent sizeWithFont:[UIFont systemFontOfSize:13.f] constrainedToSize:CGSizeMake(230, MAXFLOAT)];
    
    height += size.height;
    height += 8;
    
    if(height < 46)
        height = 46;
    
    return height;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
