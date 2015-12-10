//
//  KKAutoTableView.m
//  KKOBD
//
//  Created by zhuyc on 13-9-12.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKAutoTableView.h"
#import "KKApplicationDefine.h"

@implementation KKAutoTableView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        _control = [[UIControl alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
        _control.backgroundColor = [UIColor whiteColor];
        [_control addTarget:self action:@selector(remove) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:_control];
        [_control release];
        
        UIView *footView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, 15)];
        footView.backgroundColor = [UIColor clearColor];
        
        _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height) style:UITableViewStylePlain];
        _tableView.backgroundColor = [UIColor clearColor];
        _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        _tableView.tableFooterView = footView;
        _tableView.delegate = self;
        _tableView.dataSource = self;
        [self addSubview:_tableView];
        [footView release];
        [_tableView release];
    }
    
    self.backgroundColor = [UIColor clearColor];
    
    return self;
}

- (void)setClipsFrame:(CGRect)rect
{
    [_control setFrame:rect];
    [_tableView setFrame:rect];
}

- (void)reloadAllData
{
    [_tableView reloadData];
}

- (void)remove
{
    if (self.autoDelegate && [(NSObject *)self.autoDelegate respondsToSelector:@selector(KKAutoTableViewRemoveButtonClicked)])
        [self.autoDelegate KKAutoTableViewRemoveButtonClicked];
}

#pragma mark -
#pragma mark UITableViewDataSource,UITableViewDelegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    NSInteger count = 0;
    if (self.autoDelegate && [(NSObject *)self.autoDelegate respondsToSelector:@selector(KKAutoTableViewnumberOfRows)])
    {
        count = [self.autoDelegate KKAutoTableViewnumberOfRows];
    }
    return count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"cell_mutiInputCell_isSearching";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
	if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
        
        UIView *grayView = [[UIView alloc] initWithFrame:CGRectZero];
        grayView.backgroundColor = [UIColor clearColor];
        grayView.tag = 104;
        [cell.contentView addSubview:grayView];
        [grayView release];
        
        UIView *leftLine = [[UIView alloc] initWithFrame:CGRectMake(17, 0, 1, 39)];
        leftLine.backgroundColor = KKCOLOR_d4d4d4;
        leftLine.tag = 100;
        [cell.contentView addSubview:leftLine];
        [leftLine release];
        
        UIView *rightLine = [[UIView alloc] initWithFrame:CGRectMake(303, 0, 1, 39)];
        rightLine.backgroundColor = KKCOLOR_d4d4d4;
        rightLine.tag = 101;
        [cell.contentView addSubview:rightLine];
        [rightLine release];
        
        UIView *downline = [[UIView alloc] initWithFrame:CGRectMake(17, 38, 286, 1)];
        downline.backgroundColor = KKCOLOR_d4d4d4;
        downline.tag = 102;
        [cell.contentView addSubview:downline];
        [downline release];
        
        UILabel *textLabel = [[UILabel alloc] initWithFrame:CGRectMake(50, 11.5, 220, 16)];
        textLabel.backgroundColor = [UIColor clearColor];
        textLabel.font = [UIFont systemFontOfSize:14.0];
        textLabel.textColor = [UIColor blackColor];
        textLabel.tag = 103;
        [cell.contentView addSubview:textLabel];
        [textLabel release];
    }
    UIView *leftView = (UIView *)[cell.contentView viewWithTag:100];
    UIView *rightView = (UIView *)[cell.contentView viewWithTag:101];
    UIView *downView = (UIView *)[cell.contentView viewWithTag:102];
    UILabel *textLabel = (UILabel *)[cell.contentView viewWithTag:103];
    UIView *graView = (UIView *)[cell.contentView viewWithTag:104];
    graView.backgroundColor = [UIColor clearColor];
    
    NSString *string = [self.autoDelegate KKAutoTableViewCellShowString:indexPath.row];
    textLabel.text = string;
    
    CGFloat height = 39;
    
    if (indexPath.row == 0)
    {
        [leftView setFrame:CGRectMake(17, -150, 1, height + 150)];
        [rightView setFrame:CGRectMake(303, -150, 1, height + 150)];
    }
    else
    {
        [leftView setFrame:CGRectMake(17, -1, 1, height)];
        [rightView setFrame:CGRectMake(303, -1, 1, height)];
    }
    [downView setFrame:CGRectMake(17, height - 1, 286, 1)];
    [graView setFrame:CGRectMake(17, 0, 286, height)];
    
    cell.backgroundColor = [UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.clipsToBounds = NO;
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 39;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    UIView *bgView = [cell viewWithTag:104];
    if (bgView)
    {        
        [UIView animateWithDuration:0.3 animations:^{
            bgView.backgroundColor = KKCOLOR_dedede;
            
        } completion:^(BOOL finished) {
            bgView.backgroundColor = [UIColor clearColor];
            if (self.autoDelegate && [(NSObject *)self.autoDelegate respondsToSelector:@selector(KKAutoTableViewCellClicked:)])
                [self.autoDelegate KKAutoTableViewCellClicked:indexPath.row];
        }];
    }
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    
}

@end
