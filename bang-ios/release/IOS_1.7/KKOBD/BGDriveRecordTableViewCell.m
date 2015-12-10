//
//  BGDriveRecordTableViewCell.m
//  KKOBD
//
//  Created by Jiahai on 14-1-20.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "BGDriveRecordTableViewCell.h"
#import "KKModelBaseElement.h"
#import "KKHelper.h"

@implementation BGDriveRecordTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
        
        _dateFormatter = [[NSDateFormatter alloc] init];
        
        self.backgroundColor = [UIColor lightGrayColor];
        
        _endTimeLabel1 = [[UILabel alloc] initWithFrame:CGRectMake(2, 3, 46, 24)];
        _endTimeLabel1.backgroundColor = [UIColor clearColor];
        _endTimeLabel1.font = [UIFont systemFontOfSize:14];
        _endTimeLabel1.textAlignment = UITextAlignmentRight;
        [self addSubview:_endTimeLabel1];
        [_endTimeLabel1 release];
        
        _endTimeLabel2 = [[UILabel alloc] initWithFrame:CGRectMake(2, 24, 46, 21)];
        _endTimeLabel2.backgroundColor = [UIColor clearColor];
        _endTimeLabel2.font = [UIFont systemFontOfSize:12];
        _endTimeLabel2.textAlignment = UITextAlignmentRight;
        [self addSubview:_endTimeLabel2];
        [_endTimeLabel2 release];
        
        
        _startTimeLabel1 = [[UILabel alloc] initWithFrame:CGRectMake(2, 75, 46, 24)];
        _startTimeLabel1.backgroundColor = [UIColor clearColor];
        _startTimeLabel1.font = [UIFont systemFontOfSize:14];
        _startTimeLabel1.textAlignment = UITextAlignmentRight;
        [self addSubview:_startTimeLabel1];
        [_startTimeLabel1 release];
        
        _startTimeLabel2 = [[UILabel alloc] initWithFrame:CGRectMake(2, 96, 46, 21)];
        _startTimeLabel2.backgroundColor = [UIColor clearColor];
        _startTimeLabel2.font = [UIFont systemFontOfSize:12];
        _startTimeLabel2.textAlignment = UITextAlignmentRight;
        [self addSubview:_startTimeLabel2];
        [_startTimeLabel2 release];
        
        _lineImageView = [[UIImageView alloc] initWithFrame:CGRectMake(54, 6, 22, 106)];
        _lineImageView.image = [UIImage imageNamed:@"driveRecord_unselected_s_p.png"];
        [self addSubview:_lineImageView];
        [_lineImageView release];
        
        _endAddressLabel = [[UILabel alloc] initWithFrame:CGRectMake(82, 6, 228, 21)];
        _endAddressLabel.backgroundColor = [UIColor clearColor];
        _endAddressLabel.font = [UIFont systemFontOfSize:14];
        [self addSubview:_endAddressLabel];
        [_endAddressLabel release];
        
        _startAddressLabel = [[UILabel alloc] initWithFrame:CGRectMake(82, 92, 228, 21)];
        _startAddressLabel.backgroundColor = [UIColor clearColor];
        _startAddressLabel.font = [UIFont systemFontOfSize:14];
        [self addSubview:_startAddressLabel];
        [_startAddressLabel release];
        
        UIImageView *distanceImgV = [[UIImageView alloc] initWithFrame:CGRectMake(77, 47, 26, 26)];
        distanceImgV.image = [UIImage imageNamed:@"icon_driveRecord_distance.png"];
        [self addSubview:distanceImgV];
        [distanceImgV release];
        
        _distanceLabel = [[UILabel alloc] initWithFrame:CGRectMake(104, 48, 66, 25)];
        _distanceLabel.font = [UIFont systemFontOfSize:14];
        _distanceLabel.backgroundColor = [UIColor clearColor];
        [self addSubview:_distanceLabel];
        [_distanceLabel release];
        
        UIImageView *priceImgV = [[UIImageView alloc] initWithFrame:CGRectMake(171, 47, 26, 26)];
        priceImgV.image = [UIImage imageNamed:@"icon_driveRecord_money.png"];
        [self addSubview:priceImgV];
        [priceImgV release];
        
        _oilPriceLabel = [[UILabel alloc] initWithFrame:CGRectMake(199, 48, 44, 25)];
        _oilPriceLabel.font = [UIFont systemFontOfSize:14];
        _oilPriceLabel.backgroundColor = [UIColor clearColor];
        [self addSubview:_oilPriceLabel];
        [_oilPriceLabel release];
        
        _editBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _editBtn.frame = CGRectMake(248, 55, 62, 32);
        [_editBtn setImage:[UIImage imageNamed:@"driveRecord_edit.png"] forState:UIControlStateNormal];
        [_editBtn addTarget:self action:@selector(editBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:_editBtn];
    }
    return self;
}

-(NSString *) getTimeString:(long long)time type:(NSInteger) type
{
    NSString *str = nil;
    NSDate *date = [NSDate dateWithTimeIntervalSince1970WithMillisecond:time];
    switch (type) {
        case 1:
        {
            //获取时间
            [_dateFormatter setDateFormat:@"HH:mm"];
        }
            break;
        case 2:
        {
            //获取日期
            [_dateFormatter setDateFormat:@"MM-dd"];
        }
            break;
        default:
            break;
    }
    str = [_dateFormatter stringFromDate:date];
    return str;
}

-(void) refreshUIWithDriveRecordDetail:(BGDriveRecordDetail *)aDetail selected:(BOOL)selected
{
    self.driveRecordDetail = aDetail;
    _startTimeLabel1.text = [self getTimeString:aDetail.startTime type:1];
    _startTimeLabel2.text = [self getTimeString:aDetail.startTime type:2];
    _startAddressLabel.text = aDetail.startPlace;
    _endTimeLabel1.text = [self getTimeString:aDetail.endTime type:1];
    _endTimeLabel2.text = [self getTimeString:aDetail.endTime type:2];
    _endAddressLabel.text = aDetail.endPlace;
    _distanceLabel.text = [NSString stringWithFormat:@"%.f公里",aDetail.distance];
    _oilPriceLabel.text = [NSString stringWithFormat:@"%.1f",aDetail.oilPrice];
    
    if(selected)
    {
        _lineImageView.image = [UIImage imageNamed:@"driveRecord_selected_s_p.png"];
    }
    else
    {
        _lineImageView.image = [UIImage imageNamed:@"driveRecord_unselected_s_p.png"];
    }
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

-(void) dealloc
{
    [_dateFormatter release];
    [super dealloc];
}

@end
