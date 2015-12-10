//
//  TGCustomPickerView.m
//  TGOBD
//
//  Created by James Yu on 14-4-10.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGCustomPickerView.h"
#import "TGHTTPRequestEngine.h"

@implementation TGCustomPickerView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self initial];
        _city = [[NSMutableArray alloc] init];
        _childrenCity = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void)setCity:(NSMutableArray *)city
{
    [_city addObjectsFromArray:city];
    
    [_childrenCity addObjectsFromArray:[[_city objectAtIndex:0] children__TGModelViolateCityInfo]];
}

- (void)initial
{
    UIView *contentView = [[UIView alloc] initWithFrame:CGRectMake(0, self.bounds.size.height - 260, 320, 260)];
    contentView.backgroundColor = [UIColor whiteColor];
    
    UINavigationBar *navigationBar = [[UINavigationBar alloc] initWithFrame:CGRectMake(0, 0, 320, 44)];
    [navigationBar setBackgroundColor:[UIColor grayColor]];
    
    UIBarButtonItem *leftButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"取消" style:UIBarButtonItemStyleBordered target:self action:@selector(cancelSelect)];
    UIBarButtonItem *rightButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"确定" style:UIBarButtonItemStyleBordered target:self action:@selector(sureSelect)];
    
    UINavigationItem *navItem = [[UINavigationItem alloc] initWithTitle:@"选择查询城市"];
    navItem.leftBarButtonItem = leftButtonItem;
    navItem.rightBarButtonItem = rightButtonItem;
    
    NSArray *array = [[NSArray alloc] initWithObjects:navItem, nil];
    
    [navigationBar setItems:array];
    
    _picker = [[UIPickerView alloc] initWithFrame:CGRectMake(0, 44, 320, 216)];
    _picker.delegate = self;
    _picker.dataSource = self;
    _picker.showsSelectionIndicator = YES;
    
    [contentView addSubview:navigationBar];
    [contentView addSubview:_picker];
    [self addSubview:contentView];
}

- (void)cancelSelect
{
    [self removeFromSuperview];
}

- (void)sureSelect
{
    NSInteger rowInComponent0 = [_picker selectedRowInComponent:0];
    NSInteger rowInComponent1 = [_picker selectedRowInComponent:1];
    
    NSString *cityName = nil;
    NSString *juheCode = nil;
    
    if ([[[_city objectAtIndex:rowInComponent0] children__TGModelViolateCityInfo] count] == 0) {
        cityName = [[_city objectAtIndex:rowInComponent0] name];
        juheCode = [[_city objectAtIndex:rowInComponent0] juheCityCode];
    }
    else
    {
        cityName = [[[[_city objectAtIndex:rowInComponent0] children__TGModelViolateCityInfo] objectAtIndex:rowInComponent1] name];
        juheCode = [[[[_city objectAtIndex:rowInComponent0] children__TGModelViolateCityInfo] objectAtIndex:rowInComponent1] juheCityCode];
    }
    
    if (_delegate && [_delegate respondsToSelector:@selector(selectVlues:juheCode:)]) {
        [_delegate selectVlues:cityName juheCode:juheCode];
    }
    
    [self removeFromSuperview];
}

- (void)show
{
    UIWindow *window = [[UIApplication sharedApplication] keyWindow];
    [window addSubview:self];
    [_picker reloadAllComponents];
    [_picker selectRow:0 inComponent:0 animated:YES];
}

#pragma mark - UIPickerView delegate
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 2;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    if (0 == component) {
        return [_city count];
    }
    else
    {
        return [_childrenCity count] == 0 ? 1 : [_childrenCity count];
    }
}

- (CGFloat)pickerView:(UIPickerView *)pickerView widthForComponent:(NSInteger)component
{
    if (component == 0) {
        return 180;
    } else {
        return 140;
    }
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    if (0 == component) {
        [_childrenCity removeAllObjects];
        [_childrenCity addObjectsFromArray:[[_city objectAtIndex:row] children__TGModelViolateCityInfo]];
        [_picker reloadComponent:1];
        [_picker selectRow:0 inComponent:1 animated:YES];
        
    }
}

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    if (component == 0) {
        return [[_city objectAtIndex:row] name];
    } else
    {
        NSString *test = nil;
        if ([_childrenCity count] != 0) {
            //test = [[[[_city objectAtIndex:[_picker selectedRowInComponent:0]] children__TGModelViolateCityInfo] objectAtIndex:row] name];
            test = [[_childrenCity objectAtIndex:row] name];
        }
        
        return test;
    }
}

@end
