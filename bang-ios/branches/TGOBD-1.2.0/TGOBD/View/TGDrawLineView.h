//
//  TGDrawLineView.h
//  TGOBD
//
//  Created by James Yu on 14-4-30.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TGDrawLineView : UIView
@property (nonatomic, strong) NSArray *dataArray;
@property (nonatomic, strong) NSArray *titleArray;

@property (nonatomic, assign) CGFloat marginBottom;
@property (nonatomic, assign) CGFloat marginTop;
@property (nonatomic, assign) CGFloat maxVlue;
@property (nonatomic, assign) CGFloat titleMargin;

- (id)initWithFrame:(CGRect)frame dataArray:(NSArray *)dataArray titleArray:(NSArray *)titleArray;
@end
