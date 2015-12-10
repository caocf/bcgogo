//
//  TGDTCTableCell.m
//  TGOBD
//
//  Created by Jiahai on 14-3-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGDTCTableCell.h"
#import "TGBasicModel.h"
#import "NSDate+millisecond.h"
#import "TGMacro.h"

#define FONTSIZE    14
#define CELLWIDTH   300

@implementation TGDTCTableCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.backgroundColor = [UIColor clearColor];
        
        _view = [[UIView alloc] initWithFrame:CGRectMake(10, 4, CELLWIDTH, 200)];
        _view.backgroundColor = [UIColor whiteColor];
        _view.layer.masksToBounds = YES;
        _view.layer.cornerRadius = 2;
        _view.layer.borderWidth = 1.0f;
        _view.layer.borderColor = [COLOR_777777 CGColor];
        
        [self addSubview:_view];
        
        UIImageView *titleBgImgV = [[UIImageView alloc] init];
        titleBgImgV.frame = CGRectMake(0, 0, CELLWIDTH, 40);
        titleBgImgV.image = [UIImage imageNamed:@"bg_title.png"];
        [_view addSubview:titleBgImgV];
        
        UIImageView *iconImgV = [[UIImageView alloc] init];
        iconImgV.image = [UIImage imageNamed:@"icon_guzhang.png"];
        iconImgV.frame = CGRectMake(12, 10, 20, 16);
        [_view addSubview:iconImgV];
        
        UILabel *gzmLabel = [[UILabel alloc] initWithFrame:CGRectMake(34, 10, 56, 16)];
        gzmLabel.backgroundColor = [UIColor clearColor];
        gzmLabel.font = [UIFont systemFontOfSize:FONTSIZE];
        gzmLabel.text = @"故障码:";
        [_view addSubview:gzmLabel];
        
        _titleLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        [_titleLabel setFrame:CGRectMake(100, 12, 160, 13)];
        _titleLabel.backgroundColor = [UIColor clearColor];
        _titleLabel.textColor = COLOR_DRIVERECORD_AVERAGEOILWEAR;
        _titleLabel.textAlignment = UITextAlignmentLeft;
        _titleLabel.font = [UIFont boldSystemFontOfSize:FONTSIZE];
        [_view addSubview:_titleLabel];
        
        _timeLabel = [[UILabel alloc] initWithFrame:CGRectMake(180, 12, 100, 12)];
        _timeLabel.backgroundColor= [UIColor clearColor];
        _timeLabel.textColor = COLOR_777777;
        _timeLabel.textAlignment = UITextAlignmentLeft;
        _timeLabel.font = [UIFont systemFontOfSize:12.f];
        [_view addSubview:_timeLabel];
        
        _line1Imv = [[UIImageView alloc] initWithFrame:CGRectMake(0, titleBgImgV.frame.size.height, CELLWIDTH, 1)];
        _line1Imv.image = [UIImage imageNamed:@"cell_separateLine.png"];
        [_view addSubview:_line1Imv];
        
        
        _descTagLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        _descTagLabel.backgroundColor= [UIColor clearColor];
        _descTagLabel.textColor = COLOR_777777;
        _descTagLabel.textAlignment = UITextAlignmentRight;
        _descTagLabel.font = [UIFont systemFontOfSize:FONTSIZE];
        _descTagLabel.text = @"故障描述:";
        [_view addSubview:_descTagLabel];
        
        _descLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        _descLabel.backgroundColor= [UIColor clearColor];
        _descLabel.textColor = COLOR_TEXT_000000;
        _descLabel.textAlignment = UITextAlignmentLeft;
        _descLabel.font = [UIFont systemFontOfSize:FONTSIZE];
        _descLabel.numberOfLines = 0;
        [_view addSubview:_descLabel];
        
        
        _categoryTagLabe = [[UILabel alloc] initWithFrame:CGRectZero];
        _categoryTagLabe.backgroundColor= [UIColor clearColor];
        _categoryTagLabe.textColor = COLOR_777777;
        _categoryTagLabe.textAlignment = UITextAlignmentRight;
        _categoryTagLabe.font = [UIFont systemFontOfSize:FONTSIZE];
        _categoryTagLabe.text = @"故障类型:";
        [_view addSubview:_categoryTagLabe];
        
        _categoryLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        _categoryLabel.backgroundColor= [UIColor clearColor];
        _categoryLabel.textColor = COLOR_TEXT_000000;
        _categoryLabel.textAlignment = UITextAlignmentLeft;
        _categoryLabel.font = [UIFont systemFontOfSize:FONTSIZE];
        _categoryLabel.numberOfLines = 0;
        [_view addSubview:_categoryLabel];
        
        _controlView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, CELLWIDTH, 38)];
        _controlView.backgroundColor = [UIColor clearColor];
        
        UIImageView *cbgImgV = [[UIImageView alloc] init];
        cbgImgV.image = [UIImage imageNamed:@"icon_dtcManager_controlbg.png"];
        cbgImgV.frame = _controlView.bounds;
        [_controlView addSubview:cbgImgV];
        
        _operateBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_operateBtn setFrame:CGRectMake(30, 10, 100, 30)];
        [_operateBtn setBackgroundImage:[UIImage imageNamed:@"btn_green.png"] forState:UIControlStateNormal];
        [_operateBtn.titleLabel setFont:[UIFont systemFontOfSize:FONTSIZE]];
        [_operateBtn addTarget:self action:@selector(controlBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
        [_controlView addSubview:_operateBtn];
        
        _bgInfoBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_bgInfoBtn setBackgroundImage:[UIImage imageNamed:@"btn_orange.png"] forState:UIControlStateNormal];
        _bgInfoBtn.tag = 2;
        [_bgInfoBtn setFrame:CGRectMake(170, 10, 100, 30)];
        [_bgInfoBtn.titleLabel setFont:[UIFont systemFontOfSize:FONTSIZE]];
        [_bgInfoBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_bgInfoBtn setTitle:@"背景知识" forState:UIControlStateNormal];
        [_bgInfoBtn addTarget:self action:@selector(controlBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
        [_controlView addSubview:_bgInfoBtn];
        
        [_view addSubview:_controlView];
        
        _line2Imv = [[UIImageView alloc] initWithFrame:CGRectZero];
        _line2Imv.image = [UIImage imageNamed:@"cell_separateLine.png"];
        [_view addSubview:_line2Imv];
        
        _line3Imv = [[UIImageView alloc] initWithFrame:CGRectZero];
        _line3Imv.image = [UIImage imageNamed:@"cell_separateLine.png"];
        [_view addSubview:_line3Imv];
    }
    return self;
}

-(void) setDTCMessage:(TGModelDTCInfo *)aDTCInfo isHistory:(BOOL)isHistory
{
    self.dtcInfo = aDTCInfo;
    
    _titleLabel.text = aDTCInfo.errorCode;
    
    CGFloat height = 52;

    CGSize size = CGSizeMake(200, FONTSIZE);
    if(aDTCInfo.category && aDTCInfo.category.length > 0)
        size = [aDTCInfo.category sizeWithFont:[UIFont systemFontOfSize:FONTSIZE] constrainedToSize:CGSizeMake(200, MAXFLOAT)];
    
    _categoryTagLabe.frame = CGRectMake(12, height, 66, FONTSIZE);
    _categoryLabel.frame = CGRectMake(82, height-1, 200, size.height);
    _categoryLabel.text = aDTCInfo.category;
    
    height += size.height;
    height += 10;
    
    [_line2Imv setFrame:CGRectMake(0, height, CELLWIDTH, 1)];
  
    height += 10;
    
    size = CGSizeMake(200, FONTSIZE);
    if(aDTCInfo.content && aDTCInfo.content.length > 0)
        size = [aDTCInfo.content sizeWithFont:[UIFont systemFontOfSize:FONTSIZE] constrainedToSize:CGSizeMake(200, MAXFLOAT)];
    _descTagLabel.frame = CGRectMake(12, height, 66, FONTSIZE);
    [_descLabel setFrame:CGRectMake(82, height-1, 200, size.height)];
    _descLabel.text = aDTCInfo.content;
    
    height += size.height;
    height += 10;
    
    [_line3Imv setFrame:CGRectMake(0, height, CELLWIDTH, 1)];
    
    height += 8;
    
    _controlView.frame = CGRectMake(_controlView.frame.origin.x, height, _view.bounds.size.width, 58);
    
    height += 58;
    
    if(isHistory)
    {
        //如果是历史故障，修复按钮改为删除
        _operateBtn.tag = 3;
        _timeLabel.text = [NSDate dateStringWithTimeIntervalSince1970WithMillisecond:aDTCInfo.lastOperateTime formatter:nil];
        [_operateBtn setTitle:@"删除" forState:UIControlStateNormal];
    }
    else
    {
        _operateBtn.tag = 1;
        _timeLabel.text = [NSDate dateStringWithTimeIntervalSince1970WithMillisecond:aDTCInfo.reportTime formatter:nil];
        [_operateBtn setTitle:@"已处理" forState:UIControlStateNormal];
    }
    
    if(aDTCInfo.backgroundInfo == nil || aDTCInfo.backgroundInfo.length == 0)
    {
        _bgInfoBtn.enabled = NO;
    }
    
    CGRect rect = _view.frame;
    rect.size.height = height;
    _view.frame = rect;
}

-(void) controlBtnClicked:(UIButton *)sender
{
    if(self.delegate && [self.delegate respondsToSelector:@selector(controlBtnClicked:dtcInfo:)])
    {
        [self.delegate controlBtnClicked:sender.tag dtcInfo:self.dtcInfo];
    }
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
    
    // Configure the view for the selected state
}

+ (float)calculateVehicleConditionTableViewCellHeightWithContent:(TGModelDTCInfo *)aDTCInfo
{
    CGFloat height = 52;
    
    CGSize size = CGSizeMake(200, FONTSIZE);
    if(aDTCInfo.category && aDTCInfo.category.length > 0)
        size = [aDTCInfo.category sizeWithFont:[UIFont systemFontOfSize:FONTSIZE] constrainedToSize:CGSizeMake(200, MAXFLOAT)];
    
    height += size.height;
    height += 10;
    
    height += 10;
    
    size = CGSizeMake(200, FONTSIZE);
    if(aDTCInfo.content && aDTCInfo.content.length > 0)
        size = [aDTCInfo.content sizeWithFont:[UIFont systemFontOfSize:FONTSIZE] constrainedToSize:CGSizeMake(200, MAXFLOAT)];
    
    height += size.height;
    height += 10;
    
    height += 8;
    
    height += 58;
    
    height += 12;
    
    return height;
}

@end
