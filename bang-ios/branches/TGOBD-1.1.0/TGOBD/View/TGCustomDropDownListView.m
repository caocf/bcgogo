//
//  TGCustomDropDownListView.m
//  TGOBD
//
//  Created by James Yu on 14-3-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGCustomDropDownListView.h"

@implementation TGCustomDropDownListView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setFrame:[[UIScreen mainScreen] bounds]];
        
        _view = [[UIView alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
        _view.backgroundColor = [UIColor clearColor];
        _view.alpha = 0.6;
        
        UITapGestureRecognizer *tapGr = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(removeView)];
        [_view addGestureRecognizer:tapGr];
        
        _tableView = [[UITableView alloc] initWithFrame:frame style:UITableViewStylePlain];
        _tableView.delegate = self;
        _tableView.dataSource = self;
        _tableView.scrollEnabled = YES;
        //_tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        _tableView.layer.borderColor = [UIColor colorWithRed:190/255.0 green:190/255.0 blue:190/255.0 alpha:1].CGColor;
        _tableView.layer.borderWidth = 1;
        _tableView.layer.cornerRadius = 6;
        _tableView.bounces = NO;
        if (systemVersionAboveiOS7) {
            _tableView.separatorInset = UIEdgeInsetsZero;
        }
        
        _dataSource = [[NSArray alloc] initWithObjects:@"维修", @"保养", @"美容", nil];
        
        [self addSubview:_view];
        [self addSubview:_tableView];
        
        [_tableView reloadData];
    }
    return self;
}
#pragma mark - UITableView delegate

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_dataSource count];
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    return [UIView new];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identify = @"cell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identify];
    
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identify];
    }
    
    cell.textLabel.text = [_dataSource objectAtIndex:indexPath.row];
    cell.textLabel.textAlignment = NSTextAlignmentCenter;
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    NSString *value = [_dataSource objectAtIndex:indexPath.row];
    
    if (_delegate && [_delegate respondsToSelector:@selector(TGCustomDropDownListSelected:)]) {
        [_delegate TGCustomDropDownListSelected:value];
    }
    
    [self removeFromSuperview];
}

#pragma mark - gesture
- (void)removeView
{
    [self removeFromSuperview];
}

#pragma mark - delegate

@end
