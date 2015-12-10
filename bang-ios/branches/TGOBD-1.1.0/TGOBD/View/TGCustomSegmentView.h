//
//  TGCustomSegmentView.h
//  TGOBD
//
//  Created by James Yu on 14-3-18.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol TGCustomSegmentViewDelegate;

@interface TGCustomSegmentView : UIView <UIScrollViewDelegate>

@property (nonatomic, strong) UISegmentedControl *segment;
@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, assign) id <TGCustomSegmentViewDelegate> delegate;

- (id)initWithFrame:(CGRect)frame segmentTitles:(NSArray *)segmentTitles;

@end

@protocol TGCustomSegmentViewDelegate <NSObject>

- (void)TGCustomSegementViewDidChange:(NSInteger)currentPage;

@end