//
//  BGDTCTableViewCell.m
//  KKOBD
//
//  Created by Jiahai on 14-2-7.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "BGDTCTableViewCell.h"
#import "KKModelBaseElement.h"
#import "KKTBDTCMessage.h"
#import "KKHelper.h"
#import "KKApplicationDefine.h"

@implementation BGDTCTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        UIImageView *iconImgV = [[UIImageView alloc] init];
        iconImgV.image = [UIImage imageNamed:@"warning_icon_type1.png"];
        iconImgV.frame = CGRectMake(12, 11, 20, 16);
        [self addSubview:iconImgV];
        [iconImgV release];
        
        _titleLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        [_titleLabel setFrame:CGRectMake(52, 14, 160, 13)];
        _titleLabel.backgroundColor = [UIColor clearColor];
        _titleLabel.textColor = KKCOLOR_3359ac;
        _titleLabel.textAlignment = UITextAlignmentLeft;
        _titleLabel.font = [UIFont boldSystemFontOfSize:13.f];
        [self addSubview:_titleLabel];
        [_titleLabel release];
        
        _timeLabel = [[UILabel alloc] initWithFrame:CGRectMake(220, 18, 100, 12)];
        _timeLabel.backgroundColor= [UIColor clearColor];
        _timeLabel.textColor = KKCOLOR_777777;
        _timeLabel.textAlignment = UITextAlignmentLeft;
        _timeLabel.font = [UIFont systemFontOfSize:12.f];
        [self addSubview:_timeLabel];
        [_timeLabel release];
        
        _descTagLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        _descTagLabel.backgroundColor= [UIColor clearColor];
        _descTagLabel.textColor = KKCOLOR_777777;
        _descTagLabel.textAlignment = UITextAlignmentRight;
        _descTagLabel.font = [UIFont systemFontOfSize:13.f];
        _descTagLabel.text = @"故障描述:";
        [self addSubview:_descTagLabel];
        [_descTagLabel release];

        _descLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        _descLabel.backgroundColor= [UIColor clearColor];
        _descLabel.textColor = KKCOLOR_777777;
        _descLabel.textAlignment = UITextAlignmentLeft;
        _descLabel.font = [UIFont systemFontOfSize:13.f];
        _descLabel.numberOfLines = 0;
        [self addSubview:_descLabel];
        [_descLabel release];
        
        
        _categoryTagLabe = [[UILabel alloc] initWithFrame:CGRectZero];
        _categoryTagLabe.backgroundColor= [UIColor clearColor];
        _categoryTagLabe.textColor = KKCOLOR_777777;
        _categoryTagLabe.textAlignment = UITextAlignmentRight;
        _categoryTagLabe.font = [UIFont systemFontOfSize:13.f];
        _categoryTagLabe.text = @"故障分类:";
        [self addSubview:_categoryTagLabe];
        [_categoryTagLabe release];
        
        _categoryLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        _categoryLabel.backgroundColor= [UIColor clearColor];
        _categoryLabel.textColor = KKCOLOR_777777;
        _categoryLabel.textAlignment = UITextAlignmentLeft;
        _categoryLabel.font = [UIFont systemFontOfSize:13.f];
        _categoryLabel.numberOfLines = 0;
        [self addSubview:_categoryLabel];
        [_categoryLabel release];
        
        _arrowImv = [[UIImageView alloc] initWithFrame:CGRectZero];
        [self addSubview:_arrowImv];
        [_arrowImv release];
        
        _controlView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 38)];
        _controlView.backgroundColor = [UIColor clearColor];
        _controlView.hidden = YES;
        
        
        UIImageView *cbgImgV = [[UIImageView alloc] init];
        cbgImgV.image = [UIImage imageNamed:@"icon_dtcManager_controlbg.png"];
        cbgImgV.frame = _controlView.bounds;
        [_controlView addSubview:cbgImgV];
        [cbgImgV release];
        
        _operateBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_operateBtn setFrame:CGRectMake(0, 0, 160, 38)];
        [_operateBtn.titleLabel setFont:[UIFont systemFontOfSize:14]];
        [_operateBtn addTarget:self action:@selector(controlBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
        [_controlView addSubview:_operateBtn];
        
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
        [btn setImage:[UIImage imageNamed:@"icon_dtcManager_bginfoBtn.png"] forState:UIControlStateNormal];
        btn.tag = 2;
        [btn setFrame:CGRectMake(160, 0, 160, 38)];
        [btn.titleLabel setFont:[UIFont systemFontOfSize:14]];
        [btn addTarget:self action:@selector(controlBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
        [_controlView addSubview:btn];
        
        [self addSubview:_controlView];
        [_controlView release];
        
        
        _lineImv = [[UIImageView alloc] initWithFrame:CGRectZero];
        _lineImv.image = [UIImage imageNamed:@"icon_setting_separateLine.png"];
        _lineImv.userInteractionEnabled = YES;
        [self addSubview:_lineImv];
        [_lineImv release];
        
        dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm"];
    }
    return self;
}

-(NSString *) getDateString:(NSString *)longDate
{
    return [dateFormatter stringFromDate:[NSDate dateWithTimeIntervalSince1970WithMillisecond:[longDate longLongValue]]];
}

-(void) setDTCMessage:(KKModelDTCMessage *)aDTCMessage selected:(BOOL) selected isHistory:(BOOL)isHistory
{
    float height = 14;
    NSString *string = aDTCMessage.faultCode;
    
    _titleLabel.text = string;
    
    _timeLabel.text = [self getDateString:aDTCMessage.warnTimeStamp];
    
    height += 26;
    
    string = [NSString string];
    if([aDTCMessage.desArray count] > 0)
    {
        KKModelFaultCodeInfo *faultInfo = [aDTCMessage.desArray objectAtIndex:0];
        string = [string stringByAppendingString:[NSString stringWithFormat:@"%@",faultInfo.category]];
    }
    CGSize size = [string sizeWithFont:[UIFont systemFontOfSize:13.f] constrainedToSize:CGSizeMake(230, MAXFLOAT)];
    
    _categoryTagLabe.frame = CGRectMake(0, height, 66, 13);
    _categoryLabel.frame = CGRectMake(70, height-1, 230, size.height);
    _categoryLabel.text = string;
    
    height += size.height;
    height += 6;
    
    string = [NSString string];
    for (int t= 0; t < [aDTCMessage.desArray count]; t++) {
        KKModelFaultCodeInfo *faultInfo = [aDTCMessage.desArray objectAtIndex:t];
        if (t != [aDTCMessage.desArray count] - 1)
            string = [string stringByAppendingString:[NSString stringWithFormat:@"%@\r\n",faultInfo.description]];
        else
            string = [string stringByAppendingString:[NSString stringWithFormat:@"%@",faultInfo.description]];
    }
    
    size = [string sizeWithFont:[UIFont systemFontOfSize:13.f] constrainedToSize:CGSizeMake(230, MAXFLOAT)];
    _descTagLabel.frame = CGRectMake(0, height, 66, 13);
    [_descLabel setFrame:CGRectMake(70, height-1, 230, size.height)];
    _descLabel.text = string;
    
    height += size.height;
    height += 6;
    
//    UIButton *btn = (UIButton *)[_controlView viewWithTag:1];
    if(isHistory)
    {
        //如果是历史故障，修复按钮改为删除
        _operateBtn.tag = 3;
        [_operateBtn setImage:[UIImage imageNamed:@"icon_dtcManager_deleteBtn.png"] forState:UIControlStateNormal];
    }
    else
    {
        _operateBtn.tag = 1;
        [_operateBtn setImage:[UIImage imageNamed:@"icon_dtcManager_doneBtn.png"] forState:UIControlStateNormal];
    }
    
    _arrowImv.frame = CGRectMake(155, height, 10, 8);
    
    height += 10;
    
    if(selected)
    {
        _controlView.frame = CGRectMake(0, height, _controlView.bounds.size.width, _controlView.bounds.size.height);
        height += _controlView.bounds.size.height + 2;
        
        _arrowImv.image = [UIImage imageNamed:@"icon_arrow_up.png"];
    }
    else
    {
        _arrowImv.image = [UIImage imageNamed:@"icon_arrow_down.png"];
    
        height += 8;
    }
    _controlView.hidden = !selected;
    
    if (height < 70)
        height = 70;
    
    [_lineImv setFrame:CGRectMake(10, height - 1, 300, 1)];
}

-(void) controlBtnClicked:(UIButton *)sender
{
    if(self.delegate && [self.delegate respondsToSelector:@selector(controlBtnClicked:)])
    {
        [self.delegate controlBtnClicked:sender.tag];
    }
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

+ (float)calculateVehicleConditionTableViewCellHeightWithContent:(KKModelDTCMessage *)content selected:(BOOL)selected
{
    float height = 14;
    height += 26;
    
    NSString *string = [NSString string];
    if([content.desArray count] > 0)
    {
        KKModelFaultCodeInfo *faultInfo = [content.desArray objectAtIndex:0];
        string = [string stringByAppendingString:[NSString stringWithFormat:@"%@",faultInfo.category]];
    }
    CGSize size = [string sizeWithFont:[UIFont systemFontOfSize:13.f] constrainedToSize:CGSizeMake(230, MAXFLOAT)];
    
    height += size.height;
    height += 6;
    
    string = [NSString string];
    for (int t= 0; t < [content.desArray count]; t++) {
        KKModelFaultCodeInfo *faultInfo = [content.desArray objectAtIndex:t];
        if (t != [content.desArray count] - 1)
            string = [string stringByAppendingString:[NSString stringWithFormat:@"%@\r\n",faultInfo.description]];
        else
            string = [string stringByAppendingString:[NSString stringWithFormat:@"%@",faultInfo.description]];
    }
    
    size = [string sizeWithFont:[UIFont systemFontOfSize:13.f] constrainedToSize:CGSizeMake(230, MAXFLOAT)];
    
    height += size.height;
    height += 6;
    
    height += 10;
    
    if(selected)
        height += 40;
    else
        height += 8;
    
    if (height < 70)
        height = 70;
    
    return height;
}

-(void) dealloc
{
    [dateFormatter release],dateFormatter = nil;
    [super dealloc];
}

@end
