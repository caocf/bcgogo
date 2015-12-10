//
//  TGLineChartView.h
//  TGOBD
//
//  Created by James Yu on 14-4-30.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TGLineChartView : UIView

- (id)initWithFrame:(CGRect)frame dataArray:(NSArray *)dataArray titleArray:(NSArray *)titleArray Ytitle:(NSString *)Ytitle;

@property (nonatomic, assign) CGFloat marginLeft;
//@property (nonatomic, assign) CGFloat marginBottom;
//@property (nonatomic, assign) CGFloat marginTop;
//@property (nonatomic, assign) CGFloat defaultCount;
@property (nonatomic, assign) CGFloat maxValue;
//@property (nonatomic, assign) CGFloat defaultLblHeight;

@end
