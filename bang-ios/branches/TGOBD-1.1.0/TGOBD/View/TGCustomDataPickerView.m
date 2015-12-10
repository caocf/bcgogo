//
//  TGCustomDataPickerView.m
//  TGOBD
//
//  Created by James Yu on 14-3-6.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGCustomDataPickerView.h"

@implementation TGCustomDataPickerView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        [self initial];
    }
    return self;
}

- (void)initial
{
    _contentView = [[UIView alloc] initWithFrame:CGRectMake(0, self.bounds.size.height - 260, 320, 260)];
    _contentView.backgroundColor = [UIColor whiteColor];
    
    _navigationBar = [[UINavigationBar alloc] initWithFrame:CGRectMake(0, 0, 320, 44)];
    [_navigationBar setBackgroundColor:[UIColor grayColor]];
    
    UIBarButtonItem *leftButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"取消" style:UIBarButtonItemStyleBordered target:self action:@selector(cancelSelect)];
    UIBarButtonItem *rightButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"确定" style:UIBarButtonItemStyleBordered target:self action:@selector(sureSelect)];
    
    UINavigationItem *navItem = [[UINavigationItem alloc] initWithTitle:@"选择时间"];
    navItem.leftBarButtonItem = leftButtonItem;
    navItem.rightBarButtonItem = rightButtonItem;
    
    NSArray *array = [[NSArray alloc] initWithObjects:navItem, nil];
    
    [_navigationBar setItems:array];
    
    _dataPicker = [[UIDatePicker alloc] initWithFrame:CGRectMake(0, 44, 320, 216)];
    _dataPicker.datePickerMode = UIDatePickerModeDateAndTime;
    _dataPicker.timeZone = [NSTimeZone localTimeZone];
    _dataPicker.date = [NSDate date];
    _dataPicker.minimumDate = [NSDate date];
    
    [_contentView addSubview:_navigationBar];
    [_contentView addSubview:_dataPicker];
    [self addSubview:_contentView];
}

- (void)show
{
    UIWindow *window = [[UIApplication sharedApplication] keyWindow];
    [window addSubview:self];
}

- (void)cancelSelect
{
    [self removeFromSuperview];
}

- (void)sureSelect
{
    if (_delegate && [_delegate respondsToSelector:@selector(TGDataPickerSelected:)]) {
        [_delegate TGDataPickerSelected:[_dataPicker date]];
    }
    
    [self removeFromSuperview];
}

@end
