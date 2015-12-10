//
//  KKCustomDataPicker.m
//  KKOBD
//
//  Created by zhuyc on 13-8-14.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKCustomDataPicker.h"
#import "KKApplicationDefine.h"
#import "KKUtils.h"

@implementation KKCustomDataPicker
@synthesize delegate;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        [self initial];
    }
    self.backgroundColor = [UIColor clearColor];
    return self;
}

- (void)initial
{
    [self setFrame:CGRectMake(0, 0, 320, [UIScreen mainScreen].bounds.size.height)];
    
    _contentView = [[UIView alloc] initWithFrame:CGRectMake(0, self.bounds.size.height - 260, 320, 216+44)];
    _contentView.backgroundColor = [UIColor clearColor];
    
    UIView *headView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 44)];
    headView.backgroundColor = [UIColor blackColor];
    headView.alpha = 0.7;
    [_contentView addSubview:headView];
    [headView release];
    
    UIView *footView = [[UIView alloc] initWithFrame:CGRectMake(0, 44, 320, 216)];
    footView.backgroundColor = [UIColor whiteColor];
    [_contentView addSubview:footView];
    [footView release];
    
    UIImage *image = [UIImage imageNamed:@"bg_msg_service.png"];
    
    UIButton *cancelBtn = [[UIButton alloc] initWithFrame:CGRectMake(10, 0.5*(44 - image.size.height), image.size.width, image.size.height)];
    [cancelBtn addTarget:self action:@selector(cancelButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [cancelBtn setTitle:@"取消" forState:UIControlStateNormal];
    [cancelBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [cancelBtn setBackgroundImage:image forState:UIControlStateNormal];
    [_contentView addSubview:cancelBtn];
    [cancelBtn release];
    
    UIButton *sureBtn = [[UIButton alloc] initWithFrame:CGRectMake(320 - 10 - image.size.width, 0.5*(44 - image.size.height), image.size.width, image.size.height)];
    [sureBtn addTarget:self action:@selector(sureButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [sureBtn setTitle:@"确定" forState:UIControlStateNormal];
    [sureBtn setBackgroundImage:image forState:UIControlStateNormal];
    [sureBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [_contentView addSubview:sureBtn];
    [sureBtn release];
    
    NSDate *now = [[NSDate alloc]init];
    _dataPicker = [[UIDatePicker alloc] initWithFrame:CGRectMake(0, 44, 320, 216)];
    _dataPicker.datePickerMode = UIDatePickerModeDateAndTime;
    _dataPicker.minimumDate = now;
    [now release];
    [_contentView addSubview:_dataPicker];
    [_dataPicker release];
    
    [self addSubview:_contentView];
    [_contentView release];
    
}

- (void)sureButtonClicked
{    
    if (self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(KKCustomDataPickerDataSelected:)])
        [self.delegate KKCustomDataPickerDataSelected:[_dataPicker date]];
    
    [self removeFromSuperview];
}

- (void)cancelButtonClicked
{
    [self removeFromSuperview];
}

- (void)show
{
    UIWindow *window = [[UIApplication sharedApplication] keyWindow];
    [window addSubview:self];
}

- (void)dealloc
{
    _dataPicker = nil;
    _contentView = nil;
    
    [super dealloc];
}
@end
