//
//  TGSegmentView.m
//  TGYF
//
//  Created by James Yu on 14-5-19.
//  Copyright (c) 2014年 james. All rights reserved.
//

#import "TGSegmentView.h"

@implementation TGSegmentView

- (id)initWithFrame:(CGRect)frame segmentTitles:(NSArray *)segmentTitles
{
    self = [super initWithFrame:frame];
    if (self) {
        _segment = [[UISegmentedControl alloc] initWithItems:segmentTitles];
        _segment.frame = CGRectMake(10, 0, frame.size.width - 20, frame.size.height);
        [_segment addTarget:self action:@selector(segmentClicked:) forControlEvents:UIControlEventValueChanged];
        _segment.segmentedControlStyle = UISegmentedControlStyleBar;
        _segment.selectedSegmentIndex = 0;
        [_segment setBackgroundImage:[UIImage imageNamed:@"bg_segment_unSelect.png"] forState:UIControlStateNormal barMetrics:UIBarMetricsDefault];
        
        [_segment setBackgroundImage:[UIImage imageNamed:@"bg_segment_select.png"] forState:UIControlStateSelected barMetrics:UIBarMetricsDefault];
        [_segment setBackgroundImage:[UIImage imageNamed:@"bg_segment_select.png"] forState:UIControlStateHighlighted barMetrics:UIBarMetricsDefault];
        [_segment setDividerImage:[UIImage imageNamed:@"bg_segment_line.png"] forLeftSegmentState:UIControlStateSelected rightSegmentState:UIControlStateSelected barMetrics:UIBarMetricsDefault];
        NSDictionary *unSelect =[[NSDictionary alloc] initWithObjectsAndKeys:[UIFont fontWithName:@"Helvetica" size:15],UITextAttributeFont,RGB(0x1d, 0xba, 0xf2),UITextAttributeTextColor,nil];
        [_segment setTitleTextAttributes:unSelect forState:UIControlStateNormal];
        NSDictionary *select = [[NSDictionary alloc] initWithObjectsAndKeys:[UIFont fontWithName:@"Helvetica" size:15],UITextAttributeFont,[UIColor whiteColor],UITextAttributeTextColor, nil];
        [_segment setTitleTextAttributes:select forState:UIControlStateSelected];
        
        [self addSubview:_segment];
    }
    return self;
}

- (void)segmentClicked:(id)sender
{
    NSInteger n = _segment.selectedSegmentIndex;
    [self pageDidChange:n];
}

- (void)pageDidChange:(NSInteger)page
{
    if (_delegate && [_delegate respondsToSelector:@selector(TGSegmentViewDidChange:)]) {
        [_delegate TGSegmentViewDidChange:page];
    }
}

@end
