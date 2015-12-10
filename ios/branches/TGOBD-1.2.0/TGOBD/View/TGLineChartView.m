//
//  TGLineChartView.m
//  TGOBD
//
//  Created by James Yu on 14-4-30.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGLineChartView.h"
#import "TGDrawLineView.h"

#define Y_FONT_SIZE 12
#define GRID_LINE_COLOR TGRGBA(190,190,190,1)

//static CGFloat marginLeft = 10;
static CGFloat marginBottom = 30;
static CGFloat marginTop = 30;
static CGFloat defaultCount = 15;
//static CGFloat maxValue = 80.0;
static CGFloat defaultLblHeight = 15;

@implementation TGLineChartView


- (id)initWithFrame:(CGRect)frame dataArray:(NSArray *)dataArray titleArray:(NSArray *)titleArray Ytitle:(NSString *)Ytitle
{
    if (self = [super initWithFrame:frame]) {
        //自身初始化
        self.backgroundColor = [UIColor whiteColor];
        
        _maxValue = [self getMaxValueInArray:dataArray] * 1.3;
        
        //添加Y标题
        UILabel *Ytips = [[UILabel alloc] initWithFrame:CGRectMake(5, 1, 100, 20)];
        //Ytips.backgroundColor = [UIColor redColor];
        Ytips.text = Ytitle;
        Ytips.textColor = TGRGBA(29,186,242,1);
        Ytips.font = [UIFont systemFontOfSize:14];
        [self addSubview:Ytips];
        
        CGSize size = [[NSString stringWithFormat:@"%.2f",_maxValue] sizeWithFont:[UIFont systemFontOfSize:Y_FONT_SIZE] constrainedToSize:CGSizeMake(100, defaultLblHeight) lineBreakMode:NSLineBreakByWordWrapping];
        //根据数字大小 进行设置距离左边的边距
        _marginLeft = size.width;
        
        //创建图表上面的浮动层
        TGDrawLineView *lineView = [[TGDrawLineView alloc] initWithFrame:self.bounds dataArray:dataArray titleArray:titleArray];
        lineView.marginBottom = marginBottom;
        lineView.marginTop = marginTop;
        lineView.maxVlue = _maxValue;
        
        //创建scrollview
        UIScrollView *scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(_marginLeft + 4, 0, frame.size.width - _marginLeft - 4, frame.size.height)];
        scrollView.scrollEnabled = YES;
        //scrollView.backgroundColor = [UIColor yellowColor];
        scrollView.bounces = NO;
        scrollView.showsHorizontalScrollIndicator = NO;
        [scrollView setContentSize:CGSizeMake(([lineView.dataArray count] + 1) * lineView.titleMargin, frame.size.height)];
        
        [scrollView addSubview:lineView];
        
        [self addSubview:scrollView];
    }
    return self;
}

// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    [self setClearsContextBeforeDrawing:YES];
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetStrokeColorWithColor(context, [GRID_LINE_COLOR CGColor]);
    
    CGFloat height = rect.size.height;
    //画Y轴
    CGContextBeginPath(context);
    CGContextSetLineWidth(context, 1);
    //画线起始X坐标
    CGFloat X = _marginLeft + 4;
    CGContextMoveToPoint(context, X, marginTop);
    CGContextAddLineToPoint(context, X, height - marginBottom);
    //画X轴
    CGContextAddLineToPoint(context, rect.size.width, rect.size.height- marginBottom);
    
    CGContextStrokePath(context);
    
    //画虚线
    CGContextBeginPath(context);
    CGContextSetLineWidth(context, 0.5);
    
    CGFloat lengths[] = {10,5};
    
    CGFloat seprateHeight = (height - marginTop - marginBottom) /defaultCount;
    
    //画网格线
    for (int i = 0; i < defaultCount; i ++) {
        CGContextSetLineDash(context, 0, lengths, 2);
        
        CGContextMoveToPoint(context, X, marginTop + seprateHeight * i);
        
        CGContextAddLineToPoint(context, rect.size.width, marginTop + seprateHeight * i);
        
        CGContextStrokePath(context);
        
    }
    
    //添加Y轴各个坐标
    for (int i = 0; i <= defaultCount; i ++) {
        UILabel *lbl = [[UILabel alloc] initWithFrame:CGRectMake(2, marginTop + seprateHeight * i - defaultLblHeight/2, _marginLeft, defaultLblHeight)];
        lbl.backgroundColor = [UIColor clearColor];
        lbl.textAlignment = NSTextAlignmentRight;
        lbl.font = [UIFont systemFontOfSize:Y_FONT_SIZE];
        lbl.text = [NSString stringWithFormat:@"%.2f",(_maxValue - (_maxValue/defaultCount)*i)];
        lbl.minimumScaleFactor = 0.2;
        [self addSubview:lbl];
    }
    
}

//获取数组最大元素
- (CGFloat)getMaxValueInArray:(NSArray *)array
{
    if ([array count] == 0) {
        return 0;
    }
    
    CGFloat max = [[array firstObject] floatValue];
    
    for (int i = 1; i < [array count]; i ++) {
        if ([[array objectAtIndex:i] floatValue] > max) {
            max = [[array objectAtIndex:i] floatValue];
        }
    }
    return max;
}


@end
