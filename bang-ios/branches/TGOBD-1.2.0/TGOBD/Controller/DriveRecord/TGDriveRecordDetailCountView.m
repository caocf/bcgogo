//
//  TGDriveRecordDetailCountView.m
//  TGOBD
//
//  Created by Jiahai on 14-3-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGDriveRecordDetailCountView.h"
#import "NSDate+millisecond.h"

@implementation TGDriveRecordDetailCountView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)awakeFromNib
{
    chartView = [[TGChartView alloc] init];
    chartView.frame = CGRectMake(4, 156, 180, 146);
    
    [UIView beginAnimations:@"addView" context:nil];
    [UIView setAnimationDuration:2];
    [self addSubview:chartView];
    [UIView commitAnimations];
    averageView.layer.masksToBounds = YES;
    averageView.layer.cornerRadius = 18;
    averageView.layer.borderColor = [COLOR_DRIVERECORD_AVERAGEOILWEAR CGColor];
    averageView.layer.borderWidth = 1.0f;
}

- (void)setValueWithDistance:(CGFloat)distance travelTime:(NSInteger)travelTime totalMoney:(CGFloat)totalMoney oilWear:(CGFloat)oilWear oilCost:(CGFloat)oilCost totalOilWear:(CGFloat)totalOilWear chartItems:(NSArray *)chartItems
{
    NSNumber *total = [NSNumber numberWithFloat:totalOilWear];
    NSNumber *oil = [NSNumber numberWithFloat:oilWear];

    if([total isEqualToNumber:[NSNumber numberWithFloat:0]])
        total = oil;
    
    distanceLabel.text = [NSString stringWithFormat:@"%.1f", distance];
    travelTimeLabel.text = [NSDate dateIntervalStringWithSeconds:travelTime];
    totalMoneyLabel.text = [NSString stringWithFormat:@"%.1f", totalMoney];
    oilWearLabel.text = [NSString stringWithFormat:@"%.1f", oilCost];
    
    averageOilWearLabel.text = [NSString stringWithFormat:@"%.1f",oilWear];
    totalAverageOilWearLabel.text = [NSString stringWithFormat:@"%.1fL/100km",totalOilWear];
    
    if([total compare:oil] == NSOrderedAscending)
    {
        compareLabel.text = @"高于";
    }
    else if([total compare:oil] == NSOrderedDescending)
    {
        compareLabel.text = @"低于";
    }
    else
    {
        compareLabel.text = @"等于";
    }
    
    chartView.chartItems = [NSMutableArray arrayWithArray:chartItems];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end
