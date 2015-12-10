//
//  TGDataListTableViewCell.h
//  TGOBD
//
//  Created by James Yu on 14-4-28.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>

@class TGModelDriveStatisticInfo;

@interface TGDataListTableViewCell : UITableViewCell

@property (nonatomic, strong) UILabel *time;
@property (nonatomic, strong) UILabel *distance;
@property (nonatomic, strong) UILabel *oilCost;
@property (nonatomic, strong) UILabel *oilWear;
@property (nonatomic, strong) UILabel *oilMoney;

- (void)setCellContent:(TGModelDriveStatisticInfo *)statisticInfo;
- (void)setCellYearContent:(TGModelDriveStatisticInfo *)statisticInfo;

@end
