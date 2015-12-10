//
//  TGDrawLineView.m
//  TGOBD
//
//  Created by James Yu on 14-4-30.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGDrawLineView.h"

#define LINE_COLOR TGRGBA(29,186,242,1)
#define FILL_COLOR TGRGBA(191,226,245,0.5)

@implementation TGDrawLineView

- (id)initWithFrame:(CGRect)frame dataArray:(NSArray *)dataArray titleArray:(NSArray *)titleArray
{
    if (self = [super initWithFrame:frame]) {
        
        self.backgroundColor = [UIColor clearColor];
        
        _titleMargin = 60;
        
        self.dataArray = [NSArray arrayWithArray:dataArray];
        self.titleArray = [NSArray arrayWithArray:titleArray];
        self.frame = CGRectMake(0, 0, [_dataArray count] * _titleMargin, frame.size.height);
    }
    
    return self;
}

// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    CGFloat height = rect.size.height;
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    CGContextSetLineWidth(context, 2);
    CGContextSetStrokeColorWithColor(context, [LINE_COLOR CGColor]);
    
    //画折线
    CGContextBeginPath(context);
    
    for (int i = 0; i < [_dataArray count]; i ++) {
        CGFloat X = _titleMargin * (i + 1);
        CGFloat Y = [self transformY:[[_dataArray objectAtIndex:i] floatValue]];
        
        CGFloat titleY = height - _marginBottom;
        
        if (i == 0) {
            CGContextMoveToPoint(context, X, Y);
            [self createCenterWithPoint:CGPointMake(X, Y)];
            [self createPopTitleWithPoint:CGPointMake(X, Y) title:[self nsnumberToString:[_dataArray objectAtIndex:i]]];
            [self createXtitleWithPoint:CGPointMake(X, titleY) title:[_titleArray objectAtIndex:i]];
            continue;
        }
        
        [self createCenterWithPoint:CGPointMake(X, Y)];
        [self createXtitleWithPoint:CGPointMake(X, titleY) title:[_titleArray objectAtIndex:i]];
        [self createPopTitleWithPoint:CGPointMake(X, Y) title:[self nsnumberToString:[_dataArray objectAtIndex:i]]];
        CGContextAddLineToPoint(context, X, Y);
    }
    
    CGContextStrokePath(context);
    
    //画填充的颜色
    CGContextBeginPath(context);
    
    for (int i = 0; i < [_dataArray count]; i ++) {
        
        CGFloat Y = [self transformY:[[_dataArray objectAtIndex:i] floatValue]];
        
        if (i == 0) {
            CGContextMoveToPoint(context, _titleMargin, Y);
            if (i == ([_dataArray count] - 1)) {
                
                CGContextSetStrokeColorWithColor(context, [FILL_COLOR CGColor]);
                CGContextSetLineWidth(context, 1);
                CGContextSetFillColorWithColor(context, [FILL_COLOR CGColor]);
                
                CGContextAddLineToPoint(context, _titleMargin * (i + 1), height - _marginBottom -1);
                CGContextAddLineToPoint(context, _titleMargin, height - _marginBottom - 1);
                CGContextClosePath(context);
                CGContextDrawPath(context, kCGPathFillStroke);
            }
            continue;
        }
        
        CGContextAddLineToPoint(context, _titleMargin * (i + 1), Y);
        
        
        if (i == ([_dataArray count] - 1)) {
            
            CGContextSetStrokeColorWithColor(context, [[UIColor clearColor] CGColor]);
            CGContextSetLineWidth(context, 1);
            CGContextSetFillColorWithColor(context, [FILL_COLOR CGColor]);
            
            CGContextAddLineToPoint(context, _titleMargin * (i + 1), height - _marginBottom -1);
            CGContextAddLineToPoint(context, _titleMargin, height - _marginBottom - 1);
            CGContextClosePath(context);
            CGContextDrawPath(context, kCGPathFillStroke);
        }
    }
    
}

- (NSString *)nsnumberToString:(NSNumber *)number
{
    return [NSString stringWithFormat:@"%.2f", [number floatValue]];
}

- (CGFloat)transformY:(CGFloat)value
{
    CGFloat Y = 0;
    
    CGFloat height = self.frame.size.height;
    
    CGFloat n = value/_maxVlue;
    
    CGFloat height1 = (height - _marginTop - _marginBottom)*n;
    
    Y = (height - _marginBottom - height1);
    
    return Y;
}

- (void)createXtitleWithPoint:(CGPoint)point title:(NSString *)title
{
    UILabel *lbl = [[UILabel alloc] initWithFrame:CGRectMake(point.x, point.y, 40, 30)];
    lbl.center = CGPointMake(point.x, lbl.center.y);
    lbl.text = title;
    lbl.textAlignment = NSTextAlignmentCenter;
    lbl.backgroundColor = [UIColor clearColor];
    [self addSubview:lbl];
}

- (void)createPopTitleWithPoint:(CGPoint)point title:(NSString *)title
{
    UILabel *lbl = [[UILabel alloc] initWithFrame:CGRectMake(point.x, point.y, 60, 30)];
    lbl.center = CGPointMake(point.x, point.y - 15);
    lbl.text = title;
    lbl.font = [UIFont systemFontOfSize:13];
    lbl.minimumFontSize = 8;
    lbl.backgroundColor = [UIColor clearColor];
    lbl.textAlignment = NSTextAlignmentCenter;
    
    [self addSubview:lbl];
}

- (void)createCenterWithPoint:(CGPoint)point
{
    UILabel *lbl = [[UILabel alloc] initWithFrame:CGRectMake(point.x, point.y, 8, 8)];
    lbl.center = point;
    lbl.backgroundColor = [UIColor whiteColor];
    lbl.layer.borderColor = LINE_COLOR.CGColor;
    lbl.layer.borderWidth = 1;
    lbl.layer.cornerRadius = 4;
    
    [self addSubview:lbl];
    
}

@end
