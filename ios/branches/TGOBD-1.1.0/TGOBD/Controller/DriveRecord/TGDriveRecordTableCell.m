//
//  TGDriveRecordTableCell.m
//  TGOBD
//
//  Created by Jiahai on 14-3-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGDriveRecordTableCell.h"
#import "TGBasicModel.h"
#import "NSDate+millisecond.h"

#define DW_FONTSIZE             11          //单位Label的字体大小
#define CONTENT_FONTSIZE        18          //内容的字体大小

@implementation TGDriveRecordTableCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
        
        _dateFormatter = [[NSDateFormatter alloc] init];
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        _endTimeLabel1 = [[UILabel alloc] initWithFrame:CGRectMake(2, 1, 46, 24)];
        _endTimeLabel1.backgroundColor = [UIColor clearColor];
        _endTimeLabel1.font = [UIFont systemFontOfSize:14];
        _endTimeLabel1.textAlignment = UITextAlignmentRight;
        _endTimeLabel1.text = @"13:30";
        [self addSubview:_endTimeLabel1];
        
        _endTimeLabel2 = [[UILabel alloc] initWithFrame:CGRectMake(2, 18, 48, 21)];
        _endTimeLabel2.backgroundColor = [UIColor clearColor];
        _endTimeLabel2.font = [UIFont systemFontOfSize:11];
        _endTimeLabel2.textAlignment = UITextAlignmentRight;
        _endTimeLabel2.text = @"11-10";
        _endTimeLabel2.textColor = COLOR_777777;
        [self addSubview:_endTimeLabel2];
        
        
        _startTimeLabel1 = [[UILabel alloc] initWithFrame:CGRectMake(2, 84, 46, 24)];
        _startTimeLabel1.backgroundColor = [UIColor clearColor];
        _startTimeLabel1.font = [UIFont systemFontOfSize:14];
        _startTimeLabel1.textAlignment = UITextAlignmentRight;
        _startTimeLabel1.text = @"12:10";
        [self addSubview:_startTimeLabel1];
        
        _startTimeLabel2 = [[UILabel alloc] initWithFrame:CGRectMake(2, 100, 48, 21)];
        _startTimeLabel2.backgroundColor = [UIColor clearColor];
        _startTimeLabel2.font = [UIFont systemFontOfSize:11];
        _startTimeLabel2.textAlignment = UITextAlignmentRight;
        _startTimeLabel2.text = @"11-10";
        _startTimeLabel2.textColor = COLOR_777777;
        [self addSubview:_startTimeLabel2];
        
        _lineImageView = [[UIImageView alloc] initWithFrame:CGRectMake(50, 4, 16, 110)];
        _lineImageView.image = [UIImage imageNamed:@"driveRecord_unselected_s_p.png"];
        [self addSubview:_lineImageView];
        
        _endAddressLabel = [[UILabel alloc] initWithFrame:CGRectMake(68, 2, 170, 21)];
        _endAddressLabel.backgroundColor = [UIColor clearColor];
        _endAddressLabel.font = [UIFont systemFontOfSize:14];
        _endAddressLabel.text = @"创意产业园";
        [self addSubview:_endAddressLabel];
        
        _endAddressCityLabel = [[UILabel alloc] initWithFrame:CGRectMake(238, 2, 70, 21)];
        _endAddressCityLabel.backgroundColor = [UIColor clearColor];
        _endAddressCityLabel.font = [UIFont systemFontOfSize:14];
        _endAddressCityLabel.textAlignment = NSTextAlignmentRight;
        _endAddressCityLabel.text = @"乌鲁木齐市";
        [self addSubview:_endAddressCityLabel];
        
        _startAddressLabel = [[UILabel alloc] initWithFrame:CGRectMake(68, 96, 170, 21)];
        _startAddressLabel.backgroundColor = [UIColor clearColor];
        _startAddressLabel.font = [UIFont systemFontOfSize:14];
        _startAddressLabel.text = @"星湖街首末站";
        [self addSubview:_startAddressLabel];
        
        _startAdressCityLabel = [[UILabel alloc] initWithFrame:CGRectMake(238, 96, 70, 21)];
        _startAdressCityLabel.backgroundColor = [UIColor clearColor];
        _startAdressCityLabel.font = [UIFont systemFontOfSize:14];
        _startAdressCityLabel.textAlignment = NSTextAlignmentRight;
        _startAdressCityLabel.text = @"乌鲁木齐市";
        [self addSubview:_startAdressCityLabel];
        
        countBgView = [[UIImageView alloc] initWithFrame:CGRectMake(70, 26, 240, 66)];
        countBgView.userInteractionEnabled = YES;
        countBgView.image = [UIImage imageNamed:@"bg_driverecord_cell_gray.png"];
        [self addSubview:countBgView];
        
        _distanceImgView = [[UIImageView alloc] initWithFrame:CGRectMake(6, 2, 26, 26)];
        _distanceImgView.image = [UIImage imageNamed:@"icon_licheng_gray.png"];
        [countBgView addSubview:_distanceImgView];
        _distanceLabel = [[UILabel alloc] initWithFrame:CGRectMake(40, 1, 52, 30)];
        _distanceLabel.font = [UIFont systemFontOfSize:CONTENT_FONTSIZE];
        _distanceLabel.textAlignment = NSTextAlignmentCenter;
        _distanceLabel.text = @"1111";
        _distanceLabel.backgroundColor = [UIColor clearColor];
        _distanceLabel.textColor = COLOR_BLUE_0099CC;
        [countBgView addSubview:_distanceLabel];
        UILabel *countDwLabel1 = [[UILabel alloc] initWithFrame:CGRectMake(90, 8, 24, 21)];
        countDwLabel1.backgroundColor = [UIColor clearColor];
        countDwLabel1.font = [UIFont systemFontOfSize:DW_FONTSIZE];
        countDwLabel1.text = @"KM";
        [countBgView addSubview:countDwLabel1];
        
        _travelTimeImgView = [[UIImageView alloc] initWithFrame:CGRectMake(142, 2, 26, 26)];
        _travelTimeImgView.image = [UIImage imageNamed:@"icon_time_gray.png"];
        [countBgView addSubview:_travelTimeImgView];
        _travelTimeLabel = [[UILabel alloc] initWithFrame:CGRectMake(170, 1, 48, 30)];
        _travelTimeLabel.font = [UIFont systemFontOfSize:CONTENT_FONTSIZE];
        _travelTimeLabel.textAlignment = NSTextAlignmentCenter;
        _travelTimeLabel.text = @"99.9";
        _travelTimeLabel.backgroundColor = [UIColor clearColor];
        _travelTimeLabel.textColor = [UIColor blueColor];
        [countBgView addSubview:_travelTimeLabel];
        UILabel *countDwLabel2 = [[UILabel alloc] initWithFrame:CGRectMake(217, 8, 24, 21)];
        countDwLabel2.backgroundColor = [UIColor clearColor];
        countDwLabel2.font = [UIFont systemFontOfSize:DW_FONTSIZE];
        countDwLabel2.text = @"Min";
        [countBgView addSubview:countDwLabel2];
        
        _averageOilWearImgView = [[UIImageView alloc] initWithFrame:CGRectMake(6, 35, 26, 26)];
        _averageOilWearImgView.image = [UIImage imageNamed:@"icon_pinzhunyouhao_gray.png"];
        [countBgView addSubview:_averageOilWearImgView];
        _averageOilWearLabel = [[UILabel alloc] initWithFrame:CGRectMake(40, 36, 48, 30)];
        _averageOilWearLabel.font = [UIFont systemFontOfSize:CONTENT_FONTSIZE];
        _averageOilWearLabel.textAlignment = NSTextAlignmentCenter;
        _averageOilWearLabel.text = @"1111";
        _averageOilWearLabel.backgroundColor = [UIColor clearColor];
        _averageOilWearLabel.textColor = [UIColor orangeColor];
        [countBgView addSubview:_averageOilWearLabel];
        UILabel *countDwLabel3 = [[UILabel alloc] initWithFrame:CGRectMake(90, 44, 52, 21)];
        countDwLabel3.backgroundColor = [UIColor clearColor];
        countDwLabel3.font = [UIFont systemFontOfSize:DW_FONTSIZE];
        countDwLabel3.text = @"L/100KM";
        [countBgView addSubview:countDwLabel3];
        
        _oilWearImgView = [[UIImageView alloc] initWithFrame:CGRectMake(142, 35, 26, 26)];
        _oilWearImgView.image = [UIImage imageNamed:@"icon_youhao_gray.png"];
        [countBgView addSubview:_oilWearImgView];
        _oilWearLabel = [[UILabel alloc] initWithFrame:CGRectMake(172, 36, 48, 30)];
        _oilWearLabel.font = [UIFont systemFontOfSize:CONTENT_FONTSIZE];
        _oilWearLabel.textAlignment = NSTextAlignmentCenter;
        _oilWearLabel.text = @"99.9";
        _oilWearLabel.backgroundColor = [UIColor clearColor];
        _oilWearLabel.textColor = [UIColor orangeColor];
        [countBgView addSubview:_oilWearLabel];
        UILabel *countDwLabel4 = [[UILabel alloc] initWithFrame:CGRectMake(220, 44, 24, 21)];
        countDwLabel4.backgroundColor = [UIColor clearColor];
        countDwLabel4.font = [UIFont systemFontOfSize:DW_FONTSIZE];
        countDwLabel4.text = @"L";
        [countBgView addSubview:countDwLabel4];
    }
    return self;
}

-(void) refreshUIWithDriveRecordDetail:(TGModelDriveRecordDetail *)aDetail
{   
    _startTimeLabel1.text = [NSDate dateStringWithTimeIntervalSince1970WithMillisecond:aDetail.startTime formatter:@"HH:mm"];
    _startTimeLabel2.text = [NSDate dateStringWithTimeIntervalSince1970WithMillisecond:aDetail.startTime formatter:@"M月d日"];
    
    NSArray *startArray = [aDetail.startPlace componentsSeparatedByString:@","];
    _startAddressLabel.text = [startArray firstObject];
    _startAdressCityLabel.text = [startArray count] > 1 ? [startArray lastObject] : nil;
    
    _endTimeLabel1.text = [NSDate dateStringWithTimeIntervalSince1970WithMillisecond:aDetail.endTime formatter:@"HH:mm"];
    _endTimeLabel2.text = [NSDate dateStringWithTimeIntervalSince1970WithMillisecond:aDetail.endTime formatter:@"M月d日"];
    
    NSArray *endArray = [aDetail.endPlace componentsSeparatedByString:@","];
    _endAddressLabel.text = [endArray firstObject];
    _endAddressCityLabel.text = [endArray count] > 1 ? [endArray lastObject] : nil;
    
    _distanceLabel.text = [NSString stringWithFormat:@"%.1f",aDetail.distance];
    _travelTimeLabel.text = [NSDate dateIntervalStringWithSeconds:aDetail.travelTime];
    _averageOilWearLabel.text = [NSString stringWithFormat:@"%.1f",aDetail.oilWear];
    _oilWearLabel.text = [NSString stringWithFormat:@"%.1f",aDetail.oilCost];
}

- (void)changeUI:(BOOL)highlighted
{
    if(highlighted)
    {
        countBgView.image = [UIImage imageNamed:@"bg_driverecord_cell.png"];
        
        [_distanceLabel setTextColor:COLOR_DRIVERECORD_DISTANCE];
        [_travelTimeLabel setTextColor:COLOR_DRIVERECORD_TRAVELTIME];
        [_averageOilWearLabel setTextColor:COLOR_DRIVERECORD_AVERAGEOILWEAR];
        [_oilWearLabel setTextColor:COLOR_DRIVERECORD_OILWEAR];
        
        _distanceImgView.image = [UIImage imageNamed:@"icon_licheng.png"];
        _travelTimeImgView.image = [UIImage imageNamed:@"icon_time.png"];
        _averageOilWearImgView.image = [UIImage imageNamed:@"icon_pinzhunyouhao.png"];
        _oilWearImgView.image = [UIImage imageNamed:@"icon_youhao.png"];
        
        _lineImageView.image = [UIImage imageNamed:@"driveRecord_selected_s_p.png"];
    }
    else
    {
        countBgView.image = [UIImage imageNamed:@"bg_driverecord_cell_gray.png"];
        
        [_distanceLabel setTextColor:COLOR_777777];
        [_travelTimeLabel setTextColor:COLOR_777777];
        [_averageOilWearLabel setTextColor:COLOR_777777];
        [_oilWearLabel setTextColor:COLOR_777777];
        
        _distanceImgView.image = [UIImage imageNamed:@"icon_licheng_gray.png"];
        _travelTimeImgView.image = [UIImage imageNamed:@"icon_time_gray.png"];
        _averageOilWearImgView.image = [UIImage imageNamed:@"icon_pinzhunyouhao_gray.png"];
        _oilWearImgView.image = [UIImage imageNamed:@"icon_youhao_gray.png"];
        
        _lineImageView.image = [UIImage imageNamed:@"driveRecord_unselected_s_p.png"];
    }
}

- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated
{
    [super setHighlighted:highlighted animated:animated];

    [self changeUI:highlighted];
    
    if (self.selected) {
        [self changeUI:self.selected];
    }
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    [self changeUI:selected];
}

@end
