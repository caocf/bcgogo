//
//  KKVehicleConditionTableViewCell.m
//  KKOBD
//
//  Created by zhuyc on 13-8-22.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKVehicleConditionTableViewCell.h"
#import "KKApplicationDefine.h"
#import "KKTBDTCMessage.h"
#import "KKHelper.h"
#import "KKUtils.h"
#import "KKModelBaseElement.h"

@implementation KKVehicleConditionTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
        _categoryImv = [[UIImageView alloc] initWithFrame:CGRectZero];
        _categoryImv.backgroundColor = [UIColor clearColor];
        _categoryImv.userInteractionEnabled = YES;
        [self addSubview:_categoryImv];
        [_categoryImv release];
        
        _titleLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        _titleLabel.backgroundColor = [UIColor clearColor];
        _titleLabel.textColor = KKCOLOR_3359ac;
        _titleLabel.textAlignment = UITextAlignmentLeft;
        _titleLabel.font = [UIFont boldSystemFontOfSize:13.f];
        [self addSubview:_titleLabel];
        [_titleLabel release];
        
        _timeLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        _timeLabel.backgroundColor = [UIColor clearColor];
        _timeLabel.textColor = KKCOLOR_c0c0c0;
        _timeLabel.textAlignment = UITextAlignmentRight;
        _timeLabel.font = [UIFont systemFontOfSize:9.f];
        [self addSubview:_timeLabel];
        [_timeLabel release];
     
        _detailLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        _detailLabel.backgroundColor= [UIColor clearColor];
        _detailLabel.textColor = KKCOLOR_777777;
        _detailLabel.textAlignment = UITextAlignmentLeft;
        _detailLabel.font = [UIFont systemFontOfSize:13.f];
        _detailLabel.numberOfLines = 0;
        [self addSubview:_detailLabel];
        [_detailLabel release];
        
        _arrowImv = [[UIImageView alloc] initWithFrame:CGRectZero];
        _arrowImv.userInteractionEnabled = YES;
        [self addSubview:_arrowImv];
        [_arrowImv release];
        
        _lineImv = [[UIImageView alloc] initWithFrame:CGRectZero];
        _lineImv.userInteractionEnabled = YES;
        [self addSubview:_lineImv];
        [_lineImv release];
    }
    return self;
}

- (void)setContent:(KKModelDTCMessage *)content
{
    float height = 14;
    NSString *string = content.faultCode;
    
    UIImage *image = [UIImage imageNamed:@"warning_icon_type1.png"];
    
    [_categoryImv setFrame:CGRectMake(15, height, image.size.width, image.size.height)];
    _categoryImv.image = image;
    
    [_titleLabel setFrame:CGRectMake(50, height, 90, 13)];
    _titleLabel.text = string;
    
    string = [KKUtils ConvertDataToString:[NSDate dateWithTimeIntervalSince1970:[content.timeStamp doubleValue]]];
    [_timeLabel setFrame:CGRectMake(180, height + 2, 100, 9)];
    _timeLabel.text = string;
    
    height += 17;
    string = [NSString string];
    for (int t= 0; t < [content.desArray count]; t++) {
        KKModelFaultCodeInfo *faultInfo = [content.desArray objectAtIndex:t];
        if (t != [content.desArray count])
            string = [string stringByAppendingString:[NSString stringWithFormat:@"%@\n\n",faultInfo.description]];
        else
            string = [string stringByAppendingString:[NSString stringWithFormat:@"%@",faultInfo.description]];
    }
    
    CGSize size = [string sizeWithFont:[UIFont systemFontOfSize:13.f] constrainedToSize:CGSizeMake(234, MAXFLOAT)];
    [_detailLabel setFrame:CGRectMake(50, height, 234, size.height)];
    _detailLabel.text = string;
    
    height += size.height;
    height += 15;
    
    
    if (height < 70)
        height = 70;
    
    image = [UIImage imageNamed:@"icon_setting_separateLine.png"];
    _lineImv.image = image;
    [_lineImv setFrame:CGRectMake(10, height - image.size.height, 300, image.size.height)];
    
    image = [UIImage imageNamed:@"icon_serviceSeeking_arrow.png"];
    [_arrowImv setFrame:CGRectMake(292, 0.5*(height - image.size.height), image.size.width, image.size.height)];
    _arrowImv.image = image;
    
}


+ (float)calculateVehicleConditionTableViewCellHeightWithContent:(KKModelDTCMessage *)content
{
    float height = 14;
    height += 17;
    
    NSString *string = [NSString string];
    for (int t= 0; t < [content.desArray count]; t++) {
        KKModelFaultCodeInfo *faultInfo = [content.desArray objectAtIndex:t];
        if (t != [content.desArray count])
            string = [string stringByAppendingString:[NSString stringWithFormat:@"%@\n\n",faultInfo.description]];
        else
            string = [string stringByAppendingString:[NSString stringWithFormat:@"%@",faultInfo.description]];
    }
    
    CGSize size = [string sizeWithFont:[UIFont systemFontOfSize:13.f] constrainedToSize:CGSizeMake(234, MAXFLOAT)];
    
    height += size.height;
    height += 15;
    
    
    if (height < 70)
        height = 70;

    return height;
}


- (void)dealloc
{
    _categoryImv = nil;
    _titleLabel = nil;
    _timeLabel = nil;
    _detailLabel = nil;
    _arrowImv = nil;
    _lineImv = nil;
    
    [super dealloc];
}
@end
