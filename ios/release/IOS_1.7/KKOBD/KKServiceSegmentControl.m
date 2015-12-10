//
//  KKServiceSegmentControl.m
//  KKOBD
//
//  Created by zhuyc on 13-8-20.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKServiceSegmentControl.h"

@implementation KKServiceSegmentControl
@synthesize selectedIndex;
@synthesize unfinishedNum;
@synthesize finishedNum;
@synthesize delegate;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        
        CGSize size = frame.size;
        
        UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, size.width, size.height)];
        bgImv.userInteractionEnabled = YES;
        bgImv.image = [[UIImage imageNamed:@"seg_background.png"] stretchableImageWithLeftCapWidth:0 topCapHeight:0];
        
        UIImage *image = [UIImage imageNamed:@"seg_separateLine.png"];
        UIImageView *lineImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, image.size.width, size.height)];
        lineImv.center = CGPointMake(0.5*size.width, 0.5*size.height);
        lineImv.userInteractionEnabled = YES;
        lineImv.image = image;
        [bgImv addSubview:lineImv];
        [lineImv release];
        
        [self addSubview:bgImv];
        [bgImv release];
        
        float btnWidth = 0.5*(size.width - image.size.width);
        
        _leftButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, btnWidth ,size.height)];
        _leftButton.backgroundColor = [UIColor clearColor];
        _leftButton.tag = 10;
        [_leftButton addTarget:self action:@selector(buttonClicked:) forControlEvents:UIControlEventTouchUpInside];
        
        _leftLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, btnWidth, size.height)];
        _leftLabel.backgroundColor = [UIColor clearColor];
        _leftLabel.font = [UIFont systemFontOfSize:15.f];
        _leftLabel.textAlignment = UITextAlignmentCenter;
        _leftLabel.textColor = [UIColor whiteColor];
        _leftLabel.center = CGPointMake(0.5*btnWidth , 0.5*size.height);
        [_leftButton addSubview:_leftLabel];
        [_leftLabel release];
        
        [self addSubview:_leftButton];
        [_leftButton release];
        
        _rightButton = [[UIButton alloc] initWithFrame:CGRectMake(0.5*(frame.size.width + image.size.width),0, lineImv.frame.origin.x , frame.size.height)];
        _rightButton.backgroundColor = [UIColor clearColor];
        _rightButton.tag = 11;
        [_rightButton addTarget:self action:@selector(buttonClicked:) forControlEvents:UIControlEventTouchUpInside];
        
        _rightLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, lineImv.frame.origin.x, 15)];
        _rightLabel.backgroundColor = [UIColor clearColor];
        _rightLabel.font = [UIFont systemFontOfSize:15.f];
        _rightLabel.textAlignment = UITextAlignmentCenter;
        _rightLabel.textColor = [UIColor whiteColor];
        _rightLabel.center = CGPointMake(0.5*lineImv.frame.origin.x , 0.5*frame.size.height);
        [_rightButton addSubview:_rightLabel];
        [_rightLabel release];
        
        [self addSubview:_rightButton];
        [_rightButton release];
    
        self.selectedIndex = 0;
        _leftLabel.text = [NSString stringWithFormat:@"未完成"];
        _rightLabel.text = [NSString stringWithFormat:@"已完成"];
        
    }
    return self;
}

- (void)buttonClicked:(id)sender
{
    NSInteger tag = [sender tag] - 10;
    BOOL changed = (tag == self.selectedIndex) ? NO : YES;
    self.selectedIndex = tag;
    [self updateInfo];
    if (changed && self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(KKServiceSegmentControlSegmentChanged:)])
    {
        [self.delegate KKServiceSegmentControlSegmentChanged:tag];
    }
}

- (void)updateInfo
{
    NSString *leftStr = nil, *rightStr = nil;
    switch (_type) {
        case KKServiceSegmentControlType_ServiceSeeking:
        {
                leftStr = [NSString stringWithFormat:@"未完成(%d)",self.unfinishedNum];
            
                rightStr = [NSString stringWithFormat:@"已完成(%d)",self.finishedNum];
            
        }
            break;
        case KKServiceSegmentControlType_OilStation:
        {
                leftStr = @"按距离排列";
            
                rightStr = @"按价格排列";
        }
            break;
        case KKServiceSegmentControlType_VehicleCondition:
        {
            leftStr = @"当前车况";
            rightStr = @"行车日志";
        }
            break;
        default:
            break;
    }
    _leftLabel.text = leftStr;
    _rightLabel.text = rightStr;
    if (self.selectedIndex == 0)
    {
        _leftLabel.textColor = [UIColor whiteColor];
        _rightLabel.textColor = [UIColor grayColor];
    }
    else
    {
        _leftLabel.textColor = [UIColor grayColor];
        _rightLabel.textColor = [UIColor whiteColor];
    }

}

- (void)dealloc
{
    _leftButton = nil;
    _rightButton = nil;
    [super dealloc];
}
@end
