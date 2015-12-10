//
//  TGChartView.h
//  TGOBD
//
//  Created by Jiahai on 14-3-17.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef NS_ENUM (NSInteger,TGChartItemType)
{
    TGChartItemTypeWorst = 1,
    TGChartItemTypeBest,
    TGChartItemTypeAverage,
    TGChartItemTypeCurrent
};

@interface TGChartItem : NSObject
{
    NSString    *_title;
    CGFloat     _value;
    TGChartItemType _type;
}
@property (nonatomic, copy)     NSString *title;
@property (nonatomic, assign) CGFloat value;
@property (nonatomic, readonly) TGChartItemType type;

- (id)initWithTitle:(NSString *)aTitle value:(CGFloat)aValue type:(TGChartItemType)aType;
@end

@interface TGChartView : UIView
{
    CGFloat         maxValue;
    CGFloat         averageValue;
}

@property (nonatomic, strong) NSMutableArray *chartItems;

- (id)initWithChartItems:(NSMutableArray *)aChartItems;

@end
