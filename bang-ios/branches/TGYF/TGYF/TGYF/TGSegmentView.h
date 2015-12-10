//
//  TGSegmentView.h
//  TGYF
//
//  Created by James Yu on 14-5-19.
//  Copyright (c) 2014年 james. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol TGSegmentViewDelegate;

@interface TGSegmentView : UIView

@property (nonatomic, strong) UISegmentedControl *segment;
@property (nonatomic, strong) id <TGSegmentViewDelegate> delegate;

- (id)initWithFrame:(CGRect)frame segmentTitles:(NSArray *)segmentTitles;

@end



@protocol TGSegmentViewDelegate <NSObject>

- (void)TGSegmentViewDidChange:(NSInteger)currentSelect;

@end