//
//  TGDriveRecordDetailCountView.h
//  TGOBD
//
//  Created by Jiahai on 14-3-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TGChartView.h"

@interface TGDriveRecordDetailCountView : UIView
{
    TGChartView *chartView;
    
    __weak IBOutlet UILabel *distanceLabel;
    __weak IBOutlet UILabel *travelTimeLabel;
    __weak IBOutlet UILabel *totalMoneyLabel;
    __weak IBOutlet UILabel *oilWearLabel;
    
    __weak IBOutlet UIView *averageView;
    __weak IBOutlet UILabel *averageOilWearLabel;
    __weak IBOutlet UILabel *totalAverageOilWearLabel;
    
    __weak IBOutlet UILabel *compareLabel;
}


- (void)setValueWithDistance:(CGFloat)distance travelTime:(NSInteger)travelTime totalMoney:(CGFloat)totalMoney oilWear:(CGFloat)oilWear oilCost:(CGFloat)oilCost totalOilWear:(CGFloat)totalOilWear chartItems:(NSArray *)chartItems;
@end
