//
//  KKGradeView.m
//  KKOBD
//
//  Created by zhuyc on 13-8-8.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKGradeView.h"

@implementation KKGradeView
@synthesize rank;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        [self initial];
    }
    self.backgroundColor = [UIColor clearColor];
    self.rank = 0;
    
    return self;
}

- (void)initial
{
    CGSize size = CGSizeMake(40, 33);
    _scoreView = [[UIView alloc] initWithFrame:CGRectMake(0, 3.5, size.width*5, size.height)];
    _scoreView.backgroundColor = [UIColor clearColor];
    
    for (int index = 1 ; index <= 5; index ++)
    {
        UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake((index - 1)*size.width, 0, size.width, size.height)];
        button.backgroundColor = [UIColor clearColor];
        [button setTitle:[NSString stringWithFormat:@"%d",index] forState:UIControlStateNormal];
        [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        button.tag = index;
        [button setBackgroundImage:[self getImageByIndex:index andIsSelected:NO] forState:UIControlStateNormal];
        [button setBackgroundImage:[self getImageByIndex:index andIsSelected:NO] forState:UIControlStateHighlighted];
        [button addTarget:self action:@selector(buttonClicked:) forControlEvents:UIControlEventTouchUpInside];
        [_scoreView addSubview:button];
        [button release];
    }
    
    [self addSubview:_scoreView];
    [_scoreView release];
    
    _valuateLabel = [[UILabel alloc] initWithFrame:CGRectMake(_scoreView.bounds.size.width + 5, 13.5, self.bounds.size.width -_scoreView.bounds.size.width , 13)];
    _valuateLabel.backgroundColor = [UIColor clearColor];
    _valuateLabel.textColor = [UIColor grayColor];
    _valuateLabel.font = [UIFont systemFontOfSize:12.5f];
    _valuateLabel.textAlignment = UITextAlignmentLeft;
    [self addSubview:_valuateLabel];
    [_valuateLabel release];
}

- (UIImage *)getImageByIndex:(NSInteger)index andIsSelected:(BOOL)selected
{
    UIImage *image = nil;
    switch (index) {
        case 1:
        {
            if (selected)
                image = [UIImage imageNamed:@"reviewScore_L_D.png"];
            else
                image = [UIImage imageNamed:@"reviewScore_L.png"];
            break;
        }
        case 5:
        {
            if (selected)
                image = [UIImage imageNamed:@"reviewScore_R_D.png"];
            else
                image = [UIImage imageNamed:@"reviewScore_R.png"];
            break;
        }
        default:
        {
            if (selected)
                image = [UIImage imageNamed:@"reviewScore_M_D.png"];
            else
                image = [UIImage imageNamed:@"reviewScore_M.png"];
        }
            break;
    }
    
    return image;
}

- (void)setValuateString
{
    NSString *string = nil;
    
    switch (self.rank) {
        case 0:
            string = nil;
            break;
        case 1:
            string = @"差";
            break;
        case 2:
            string = @"一般";
            break;
        case 3:
            string = @"好";
            break;
        case 4:
            string = @"很好";
            break;
        case 5:
            string = @"非常好";
            break;
        default:
            break;
    }
    _valuateLabel.text = string;
    [_valuateLabel sizeToFit];
}

- (void)buttonClicked:(UIButton *)sender{
    NSInteger tag = sender.tag;
    
    if (tag == 1 && self.rank == 1)
    {
        for (UIView *subview in _scoreView.subviews)
        {
            if ([subview isKindOfClass:[UIButton class]])
            {
                UIButton *button = (UIButton *)subview;
                [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
                [button setBackgroundImage:[self getImageByIndex:button.tag andIsSelected:NO] forState:UIControlStateNormal];
                [button setBackgroundImage:[self getImageByIndex:button.tag andIsSelected:NO] forState:UIControlStateHighlighted];
            }
        }
        self.rank = 0;
    }
    else
    {
        for (UIView *subview in _scoreView.subviews)
        {
            if ([subview isKindOfClass:[UIButton class]])
            {
                UIButton *button = (UIButton *)subview;
                if (button.tag > tag)
                {
                    [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
                    [button setBackgroundImage:[self getImageByIndex:button.tag andIsSelected:NO] forState:UIControlStateNormal];
                    [button setBackgroundImage:[self getImageByIndex:button.tag andIsSelected:NO] forState:UIControlStateHighlighted];
                }
                else
                {
                    [button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
                    [button setBackgroundImage:[self getImageByIndex:button.tag andIsSelected:YES] forState:UIControlStateNormal];
                    [button setBackgroundImage:[self getImageByIndex:button.tag andIsSelected:YES] forState:UIControlStateHighlighted];
                }
            }
        }
        self.rank = tag;
    }
    
    [self setValuateString];
}

- (void)dealloc
{
    _valuateLabel = nil;
    _scoreView = nil;
    
    [super dealloc];
}
@end
