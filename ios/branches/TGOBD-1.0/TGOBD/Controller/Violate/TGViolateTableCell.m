//
//  TGViolateTableCell.m
//  TGOBD
//
//  Created by Jiahai on 14-3-10.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGViolateTableCell.h"
#import "TGBasicModel.h"

@implementation TGViolateTableCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
        int originY = 6;
        
        UILabel *label1 = [[UILabel alloc] initWithFrame:CGRectMake(8, originY, 56, 22)];
        label1.font = [UIFont systemFontOfSize:12];
        label1.text = @"违章时间:";
        [label1 sizeToFit];
        [self addSubview:label1];
        _dateLabel = [[UILabel alloc] initWithFrame:CGRectMake(66, originY, 56, 22)];
        _dateLabel.font = [UIFont systemFontOfSize:12];
        [self addSubview:_dateLabel];
        
        originY += 22;
        
        UILabel *label2 = [[UILabel alloc] initWithFrame:CGRectMake(8, originY, 56, 22)];
        label2.font = [UIFont systemFontOfSize:12];
        label2.text = @"违章地点:";
        [label2 sizeToFit];
        [self addSubview:label2];
        _areaLabel = [[UILabel alloc] initWithFrame:CGRectMake(66, originY, 56, 22)];
        _areaLabel.font = [UIFont systemFontOfSize:12];
        [self addSubview:_areaLabel];
        
        originY += 22;
        
        UILabel *label3 = [[UILabel alloc] initWithFrame:CGRectMake(8, originY, 56, 22)];
        label3.font = [UIFont systemFontOfSize:12];
        label3.text = @"违章行为:";
        [label3 sizeToFit];
        [self addSubview:label3];
        _actLabel = [[UILabel alloc] initWithFrame:CGRectMake(66, originY, 56, 22)];
        _actLabel.font = [UIFont systemFontOfSize:12];
        [self addSubview:_actLabel];
        
        originY += 22;
        
        UILabel *label4 = [[UILabel alloc] initWithFrame:CGRectMake(8, originY, 56, 22)];
        label4.font = [UIFont systemFontOfSize:12];
        label4.text = @"违章代码:";
        [label4 sizeToFit];
        [self addSubview:label4];
        _codeLabel = [[UILabel alloc] initWithFrame:CGRectMake(66, originY, 56, 22)];
        _codeLabel.font = [UIFont systemFontOfSize:12];
        [self addSubview:_codeLabel];
        
        originY += 22;
        UILabel *label5 = [[UILabel alloc] initWithFrame:CGRectMake(8, originY, 56, 22)];
        label5.font = [UIFont systemFontOfSize:12];
        label5.text = @"违章记分:";
        [label5 sizeToFit];
        [self addSubview:label5];
        _fenLabel = [[UILabel alloc] initWithFrame:CGRectMake(66, originY, 56, 22)];
        _fenLabel.font = [UIFont systemFontOfSize:12];
        [self addSubview:_fenLabel];
        
        originY += 22;
        UILabel *label6 = [[UILabel alloc] initWithFrame:CGRectMake(8, originY, 56, 22)];
        label6.font = [UIFont systemFontOfSize:12];
        label6.text = @"违章罚款:";
        [label6 sizeToFit];
        [self addSubview:label6];
        _moneyLabel = [[UILabel alloc] initWithFrame:CGRectMake(66, originY, 56, 22)];
        _moneyLabel.font = [UIFont systemFontOfSize:12];
        [self addSubview:_moneyLabel];
    }
    return self;
}

-(void) setUIFit:(TGModelViolateDetailInfo *)aViolateDetailInfo
{
    self.violateDetailInfo = aViolateDetailInfo;
    
    _dateLabel.text = aViolateDetailInfo.date;
    _areaLabel.text = aViolateDetailInfo.area;
    _actLabel.text = aViolateDetailInfo.act;
    _codeLabel.text = aViolateDetailInfo.code;
    _fenLabel.text = aViolateDetailInfo.fen;
    _moneyLabel.text = aViolateDetailInfo.money;
    
    [_dateLabel sizeToFit];
    [_areaLabel sizeToFit];
    [_actLabel sizeToFit];
    [_codeLabel sizeToFit];
    [_fenLabel sizeToFit];
    [_moneyLabel sizeToFit];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
