//
//  TGChartView.m
//  TGOBD
//
//  Created by Jiahai on 14-3-17.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGChartView.h"


@implementation TGChartItem
- (id)initWithTitle:(NSString *)aTitle value:(CGFloat)aValue type:(TGChartItemType)aType
{
    if(self = [super init])
    {
        self.title = aTitle;
        _value = aValue;
        _type = aType;
    }
    return self;
}

@end

#define WIDTH_TITLE        40
#define HEIGTH_TITLE        20
#define MIN_RECT_WIDTH      30
#define TITLE_MIN_ORIGINX       40

@implementation TGChartView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}

- (id)initWithChartItems:(NSMutableArray *)aChartItems
{
    if(self = [super init])
    {
        self.chartItems = aChartItems;
    }
    return self;
}

- (void)setChartItems:(NSMutableArray *)chartItems
{
    _chartItems = nil;
    _chartItems = chartItems;
    for(TGChartItem *item in self.chartItems)
    {
        if(maxValue < item.value)
        {
            maxValue = item.value;
        }
        if(item.type == TGChartItemTypeAverage)
        {
            averageValue = item.value;
        }
    }
    
    //当平均、最好、最差无数据时，默认设置为本次
    for(TGChartItem *item in self.chartItems)
    {
        if(item.value == 0)
            item.value = averageValue;
        
        if(maxValue < item.value)
        {
            maxValue = item.value;
        }
    }
    
    [self setNeedsDisplay];
}

// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
    
    CGFloat width = rect.size.width - WIDTH_TITLE - MIN_RECT_WIDTH;
    CGFloat heightDelta = 2;
    CGFloat itemHeight = (rect.size.height - ([self.chartItems count] + 1) * heightDelta)/[self.chartItems count];
    
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetRGBFillColor(context, 1.0, 1.0, 1.0, 1.0);
    CGContextFillRect(context, rect);
    //画边框
//    CGContextSetRGBStrokeColor(context, 0, 0, 0, 1);
//    CGContextSetLineWidth(context, 1.0);
//    CGContextAddRect(context,rect);
//    CGContextStrokePath(context);
    
    
    CGRect rangeRect,titleRect,valueRect;
    
    int i = 0;
    for(TGChartItem *item in self.chartItems)
    {
        rangeRect = CGRectMake(0, heightDelta*(i+1)+itemHeight*i, ((item.value/maxValue)*width + MIN_RECT_WIDTH), itemHeight);
        titleRect = CGRectMake(rangeRect.origin.x+rangeRect.size.width, rangeRect.origin.y + (rangeRect.size.height-HEIGTH_TITLE)*0.5, WIDTH_TITLE, HEIGTH_TITLE);
        valueRect = CGRectMake(5, titleRect.origin.y, WIDTH_TITLE, HEIGTH_TITLE);
        
        if(titleRect.origin.x < TITLE_MIN_ORIGINX)
            titleRect.origin.x = TITLE_MIN_ORIGINX;
        
        switch (item.type) {
            case TGChartItemTypeWorst:
                CGContextSetRGBFillColor(context, 83/255.0, 81/255.0, 144/255.0, 1.0);
                break;
            case TGChartItemTypeAverage:
                CGContextSetRGBFillColor(context, 58/255.0, 165/255.0, 35/255.0, 1.0);
                break;
            case TGChartItemTypeCurrent:
                CGContextSetRGBFillColor(context, 242/255.0, 92/255.0, 75/255.0, 1.0);
                break;
            case TGChartItemTypeBest:
                CGContextSetRGBFillColor(context, 40/255.0, 118/255.0, 216/255.0, 1.0);
                break;
            default:
                break;
        }
        //填充矩形
        CGContextFillRect(context, rangeRect);
        
        CGContextSetRGBFillColor(context, 1.0, 1.0, 1.0, 1.0);
        //设置字体
        UIFont *font = [UIFont boldSystemFontOfSize:14.0];
        //在指定的矩形区域内画文字
        [[NSString stringWithFormat:@"%.1f",item.value] drawInRect:valueRect withFont:font lineBreakMode:NSLineBreakByWordWrapping alignment:NSTextAlignmentLeft];
        
        if(item.type == TGChartItemTypeCurrent)
        {
            CGContextSetRGBFillColor(context, 0, 0, 0, 1.0);
        }
        else
        {
            CGContextSetRGBFillColor(context, 0.87, 0.87, 0.87, 1.0);
        }
        [item.title drawInRect:titleRect withFont:font lineBreakMode:NSLineBreakByWordWrapping alignment:NSTextAlignmentCenter];
        
        i++;
    }
}

@end
