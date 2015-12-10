//
//  TGCustomSegmentView.m
//  TGOBD
//
//  Created by James Yu on 14-3-18.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGCustomSegmentView.h"
#import "UIColor+FromHex.h"

@implementation TGCustomSegmentView

- (id)initWithFrame:(CGRect)frame segmentTitles:(NSArray *)segmentTitles
{
    if (self = [super initWithFrame:frame]) {
        CGFloat originY = 10;
        
        _segment = [[UISegmentedControl alloc] initWithItems:segmentTitles];
        _segment.frame = CGRectMake(10, originY, 300, 30);
        [_segment addTarget:self action:@selector(segmentClicked:) forControlEvents:UIControlEventValueChanged];
        _segment.segmentedControlStyle = UISegmentedControlStyleBar;
        _segment.selectedSegmentIndex = 0;
       // [_segment sett];
        [_segment setBackgroundImage:[UIImage imageNamed:@"bg_segment_unSelect.png"] forState:UIControlStateNormal barMetrics:UIBarMetricsDefault];
       
        [_segment setBackgroundImage:[UIImage imageNamed:@"bg_segment_select.png"] forState:UIControlStateSelected barMetrics:UIBarMetricsDefault];
        [_segment setBackgroundImage:[UIImage imageNamed:@"bg_segment_select.png"] forState:UIControlStateHighlighted barMetrics:UIBarMetricsDefault];
        [_segment setDividerImage:[UIImage imageNamed:@"bg_segment_line.png"] forLeftSegmentState:UIControlStateSelected rightSegmentState:UIControlStateSelected barMetrics:UIBarMetricsDefault];
        NSDictionary *unSelect =[[NSDictionary alloc] initWithObjectsAndKeys:[UIFont fontWithName:@"Helvetica" size:15],UITextAttributeFont,[UIColor colorWithHex:0x1dbaf2],UITextAttributeTextColor,nil];
        [_segment setTitleTextAttributes:unSelect forState:UIControlStateNormal];
        NSDictionary *select = [[NSDictionary alloc] initWithObjectsAndKeys:[UIFont fontWithName:@"Helvetica" size:15],UITextAttributeFont,[UIColor whiteColor],UITextAttributeTextColor, nil];
        [_segment setTitleTextAttributes:select forState:UIControlStateSelected];
        
        originY += 42;
        
        _scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, originY, 320, frame.size.height - originY)];
        [_scrollView setContentSize:CGSizeMake(320 * [segmentTitles count], frame.size.height - originY)];
        _scrollView.bounces = NO;
        _scrollView.pagingEnabled = YES;
        _scrollView.delegate = self;
        _scrollView.showsHorizontalScrollIndicator = NO;
        _scrollView.showsVerticalScrollIndicator = NO;
        
        [self addSubview:_segment];
        [self addSubview:_scrollView];
    }
    return self;
}

#pragma mark - Custom Methods

- (void)segmentClicked:(id)sender
{
    NSInteger n = _segment.selectedSegmentIndex;
    [_scrollView setContentOffset:CGPointMake(320 * n, 0)];
    [self pageDidChange:n];
}

- (void)pageDidChange:(NSInteger)page
{
    if (_delegate && [_delegate respondsToSelector:@selector(TGCustomSegementViewDidChange:)]) {
        [_delegate TGCustomSegementViewDidChange:page];
    }
}

#pragma mark - UIScrollView delegate

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    NSInteger n = _scrollView.contentOffset.x / 320;
    _segment.selectedSegmentIndex = n;
    [self pageDidChange:n];
}



@end
