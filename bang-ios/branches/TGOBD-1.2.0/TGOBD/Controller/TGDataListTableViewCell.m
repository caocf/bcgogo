//
//  TGDataListTableViewCell.m
//  TGOBD
//
//  Created by James Yu on 14-4-28.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGDataListTableViewCell.h"
#import "TGBasicModel.h"

@implementation TGDataListTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        CGFloat originY = 0;
        
        //UIImageView *bgImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 1, 300, 40.5)];
        //bgImageView.image = [UIImage imageNamed:@"bg_title.png"];
        
        UILabel *bgLbl = [[UILabel alloc] initWithFrame:CGRectMake(0, 1, 300, 40.5)];
        bgLbl.backgroundColor = TGRGBA(191, 226, 245, 1);
        bgLbl.tag = 1111;
        
        _time = [[UILabel alloc] initWithFrame:CGRectMake(30, 5, 280, 30)];
        _time.backgroundColor = [UIColor clearColor];
        
        [bgLbl addSubview:_time];
        
        originY += 41.6;
        
        UIView *view1 = [[UIView alloc] initWithFrame:CGRectMake(0, originY, 300, 40)];
        view1.layer.borderWidth = 1;
        view1.layer.borderColor = COLOR_LAYER_BORDER.CGColor;
        
        UILabel *totalMileage = [[UILabel alloc] initWithFrame:CGRectMake(25, 5, 80, 30)];
        totalMileage.textAlignment = NSTextAlignmentRight;
        totalMileage.text = @"总里程:";
        
        _distance = [[UILabel alloc] initWithFrame:CGRectMake(115, 5, 170, 30)];
        _distance.backgroundColor = [UIColor clearColor];
        
        [view1 addSubview:totalMileage];
        [view1 addSubview:_distance];
        
        originY += 40;
        
        UIView *view2 = [[UIView alloc] initWithFrame:CGRectMake(0, originY, 300, 40)];
        
        UILabel *totalOil = [[UILabel alloc] initWithFrame:CGRectMake(25, 5, 80, 30)];
        totalOil.textAlignment = NSTextAlignmentRight;
        totalOil.text = @"总油耗:";
        
        _oilCost = [[UILabel alloc] initWithFrame:CGRectMake(115, 5, 170, 30)];
        _oilCost.backgroundColor = [UIColor clearColor];
        
        [view2 addSubview:totalOil];
        [view2 addSubview:_oilCost];
        
        originY += 40;
        
        UIView *view3 = [[UIView alloc] initWithFrame:CGRectMake(0, originY, 300, 40)];
        view3.layer.borderWidth = 1;
        view3.layer.borderColor = COLOR_LAYER_BORDER.CGColor;
        
        UILabel *averageOil = [[UILabel alloc] initWithFrame:CGRectMake(25, 5, 80, 30)];
        averageOil.textAlignment = NSTextAlignmentRight;
        averageOil.text = @"平均油耗:";
        
        _oilWear = [[UILabel alloc] initWithFrame:CGRectMake(115, 5, 170, 30)];
        _oilWear.backgroundColor = [UIColor clearColor];
        
        [view3 addSubview:averageOil];
        [view3 addSubview:_oilWear];
        
        originY += 40;
        
        UIView *view4 = [[UIView alloc] initWithFrame:CGRectMake(0, originY, 300, 40)];
        
        UILabel *totalCost = [[UILabel alloc] initWithFrame:CGRectMake(25, 5, 80, 30)];
        totalCost.textAlignment = NSTextAlignmentRight;
        totalCost.text = @"总花费:";
        
        _oilMoney = [[UILabel alloc] initWithFrame:CGRectMake(115, 5, 170, 30)];
        _oilMoney.backgroundColor = [UIColor clearColor];
        
        [view4 addSubview:totalCost];
        [view4 addSubview:_oilMoney];
        
        UIView *cellBgView = [[UIView alloc] initWithFrame:CGRectMake(10, 2, 300, 200)];
        cellBgView.layer.borderWidth = 1;
        cellBgView.layer.borderColor = COLOR_LAYER_BORDER.CGColor;
        cellBgView.layer.cornerRadius = 8;
        cellBgView.layer.masksToBounds = YES;
        
        [cellBgView addSubview:bgLbl];
        [cellBgView addSubview:view1];
        [cellBgView addSubview:view2];
        [cellBgView addSubview:view3];
        [cellBgView addSubview:view4];
        
        [self.contentView addSubview:cellBgView];
        self.contentView.backgroundColor = [UIColor clearColor];
        self.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    return self;
}

- (void)setCellContent:(TGModelDriveStatisticInfo *)statisticInfo
{
    UILabel *lbl = (UILabel *)[self viewWithTag:1111];
    lbl.backgroundColor = TGRGBA(191, 226, 245, 1);
    _time.text = [NSString stringWithFormat:@"%d年%d月", statisticInfo.statYear, statisticInfo.statMonth];
    _distance.text = [NSString stringWithFormat:@"%.2f KM", statisticInfo.distance];
    _oilCost.text = [NSString stringWithFormat:@"%.2f L", statisticInfo.oilCost];
    _oilWear.text = [NSString stringWithFormat:@"%.2f L/100KM", statisticInfo.oilWear];
    _oilMoney.text = [NSString stringWithFormat:@"%.2f 元", statisticInfo.oilMoney];
}

- (void)setCellYearContent:(TGModelDriveStatisticInfo *)statisticInfo
{
    UILabel *lbl = (UILabel *)[self viewWithTag:1111];
    lbl.backgroundColor = TGRGBA(255, 136, 0, 0.6);
    _time.text = [NSString stringWithFormat:@"%d年%d月至今", statisticInfo.statYear, statisticInfo.statMonth];
    _distance.text = [NSString stringWithFormat:@"%.2f KM", statisticInfo.distance];
    _oilCost.text = [NSString stringWithFormat:@"%.2f L", statisticInfo.oilCost];
    _oilWear.text = [NSString stringWithFormat:@"%.2f L/100KM", statisticInfo.oilWear];
    _oilMoney.text = [NSString stringWithFormat:@"%.2f 元", statisticInfo.oilMoney];
}

@end
