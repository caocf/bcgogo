//
//  TGDataListView.m
//  TGOBD
//
//  Created by James Yu on 14-4-28.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGDataListView.h"
#import "TGDataListTableViewCell.h"
#import "TGBasicModel.h"

@implementation TGDataListView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        _tableView = [[UITableView alloc] initWithFrame:frame];
        _tableView.dataSource = self;
        _tableView.delegate = self;
        _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        
        _dataSource = [[NSMutableArray alloc] init];
        
        [self addSubview:_tableView];
    }
    return self;
}


#pragma mark - Custom Method

- (UILabel *)createLabelWithFrame:(CGRect)frame
{
    UILabel *lbl = [[UILabel alloc] initWithFrame:frame];
    lbl.backgroundColor = [UIColor clearColor];
    return lbl;
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

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 210;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentify = @"cellIdentify";
    TGDataListTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentify];
    if (cell == nil) {
        cell = [[TGDataListTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentify];
    }
    
    if (indexPath.row == 0) {
        [cell setCellYearContent:[_dataSource objectAtIndex:indexPath.row]];
    }
    else
    {
        [cell setCellContent:[_dataSource objectAtIndex:indexPath.row]];
    }
    
    
    return cell;
}

@end
