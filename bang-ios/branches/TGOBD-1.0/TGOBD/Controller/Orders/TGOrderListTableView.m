//
//  TGOrderListTableview.m
//  TGOBD
//
//  Created by James Yu on 14-3-18.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGOrderListTableView.h"

#define PAGESIZE 25

@implementation TGOrderListTableView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height) style:UITableViewStylePlain];
        _tableView.delegate = self;
        _tableView.dataSource = self;
        _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        
        [self addSubview:_tableView];
        
        _refreshTableHeaderView = [[EGORefreshTableHeaderView alloc] initWithFrame:CGRectMake(0, -_tableView.bounds.size.height, _tableView.bounds.size.width, _tableView.bounds.size.height)];
        _refreshTableHeaderView.delegate = self;
        _refreshTableHeaderView.refreshTableName = [NSString stringWithFormat:@"orderList-%d",_orderStatus];
        [_refreshTableHeaderView refreshLastUpdatedDate];
        
        [_tableView addSubview:_refreshTableHeaderView];
        
        _dataSource = [[NSMutableArray alloc] init];
        
        _pager = [[TGModelPagerInfo alloc] init];
        _pager.currentPage = 1;
        _pager.pageSize = PAGESIZE;
        _pager.isLastPage = NO;
    }
    return self;
}


#pragma mark - Custom methods
- (UIView *)createSectionHeaderView
{
    if (_orderStatus == settlement) {
        UIView *view = [[UIView alloc] initWithFrame:CGRectMake(10, 10, 300, 50)];
        view.backgroundColor = [UIColor colorWithRed:255/255.0 green:237/255.0 blue:191/255.0 alpha:1];
        view.layer.cornerRadius = 10;
        
        UIImageView *imgView = [[UIImageView alloc] initWithFrame:CGRectMake(30, 10, 25, 25)];
        imgView.image = [UIImage imageNamed:@"icon_money_total.png"];
        
        UILabel *lbl = [[UILabel alloc] initWithFrame:CGRectMake(70, 5, 60, 40)];
        lbl.text = @"总计：";
        lbl.backgroundColor = [UIColor clearColor];
        
        UILabel *totalMoney = [[UILabel alloc] initWithFrame:CGRectMake(130, 5, 200, 40)];
        totalMoney.backgroundColor = [UIColor clearColor];
        totalMoney.textColor = [UIColor redColor];
        totalMoney.text = [NSString stringWithFormat:@"¥ %.2f", _totalMoney];
        
        [view addSubview:imgView];
        [view addSubview:lbl];
        [view addSubview:totalMoney];
        
        UIView *returnView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 50)];
        returnView.backgroundColor = [UIColor whiteColor];
        [returnView addSubview:view];
        
        return returnView;
    }
    
    return nil;
}

#pragma mark - UITableView delegate

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{

     if (_orderStatus == settlement && [_dataSource count])
    {
        return 60;
    }
    
    return 0;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    if (_orderStatus == settlement && [_dataSource count]) {
        return [self createSectionHeaderView];
    }
    return nil;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    return [UIView new];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 140;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if ([_dataSource count] == 0) {
        if (_noDateImageView == nil) {
            UIImage *img = [UIImage imageNamed:@"no_data.png"];
            _noDateImageView = [[UIImageView alloc] initWithImage:img];
            _noDateImageView.frame = CGRectMake((320-img.size.width)*0.5, 60, img.size.width, img.size.height);
            [_tableView addSubview:_noDateImageView];
        }
        _noDateImageView.hidden = NO;
    }
    else
    {
        _noDateImageView.hidden = YES;
    }
    return [_dataSource count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString *identify = @"tableviewCell";
    TGOrderListViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identify];
    
    if (cell == nil) {
        cell = [[TGOrderListViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identify];
        cell.accessoryType = UITableViewCellAccessoryNone;
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    
    [cell setCellContent:[_dataSource objectAtIndex:indexPath.row] orderStatus:_orderStatus];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (_orderStatus == settlement) {
        if (_delegate && [_delegate respondsToSelector:@selector(TGOrderListTableViewdidSelectRowAtIndexPath:orderStatus:)]) {
            [_delegate TGOrderListTableViewdidSelectRowAtIndexPath:indexPath orderStatus:_orderStatus];
        }
    }
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

#pragma mark - UIScrollView delegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    [_refreshTableHeaderView egoRefreshScrollViewDidScroll:scrollView];
    
    CGPoint offset = scrollView.contentOffset;
    CGSize size = scrollView.frame.size;
    CGSize contentSize = scrollView.contentSize;
    
    float yMargin = offset.y + size.height - contentSize.height;
    
    if (yMargin > -1 && !_isLoading && !_pager.isLastPage) {
        //上拉加载更多
        _isLoading = YES;
        
        if (_delegate && [_delegate respondsToSelector:@selector(TGOrderListTableViewLoadeMore:pageInfo:)]) {
            [_delegate TGOrderListTableViewLoadeMore:_orderStatus pageInfo:_pager];
        }
        else
        {
            [self stopLoading];
        }
    }
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    [_refreshTableHeaderView egoRefreshScrollViewDidEndDragging:scrollView];
}

#pragma mark - EGORefreshTableHeaderDelegate
- (void)egoRefreshTableHeaderDidTriggerRefresh:(EGORefreshTableHeaderView *)view
{
    _isLoading = YES;
    
    if (_delegate && [_delegate respondsToSelector:@selector(TGOrderListTableViewPullRefresh:pageInfo:)]) {
        _pager.currentPage = 1;
        _pager.pageSize = PAGESIZE;
        _pager.isLastPage = NO;
        [_delegate TGOrderListTableViewPullRefresh:_orderStatus pageInfo:_pager];
    }
    else
    {
        [self performSelector:@selector(stopLoading) withObject:nil afterDelay:2];
    }
}

- (BOOL)egoRefreshTableHeaderDataSourceIsLoading:(EGORefreshTableHeaderView *)view
{
    return _isLoading;
}

- (NSDate *)egoRefreshTableHeaderDataSourceLastUpdated:(EGORefreshTableHeaderView *)view
{
    return [NSDate date];
}

#pragma mark - Custom Method

- (void)stopLoading
{
    _isLoading = NO;
    
    [_refreshTableHeaderView egoRefreshScrollViewDataSourceDidFinishedLoading:_tableView];
}

@end
