//
//  TGOrderListViewController.m
//  TGOBD
//
//  Created by James Yu on 14-3-7.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGOrderListViewController.h"
#import "TGOrderListViewCell.h"
#import "TGHTTPRequestEngine.h"
#import "TGDataSingleton.h"
#import "TGOrderDetailViewController.h"

@interface TGOrderListViewController ()

@property (nonatomic, assign) BOOL isFirstLoad;

@end

@implementation TGOrderListViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    [self initComopents];
    [self initVariable];
    [self setnavigationBar];
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma makr - Custom Methods

- (void)initComopents
{
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar];
    CGFloat height = [self getViewHeightWithNavigationBar];
    
    _segmentView = [[TGCustomSegmentView alloc] initWithFrame:CGRectMake(0, originY, 320, height) segmentTitles:@[@"未结算",@"已结算"]];
    _segmentView.delegate = self;
    
    _unSettleTableview = [[TGOrderListTableView alloc] initWithFrame:CGRectMake(0, 0, screenWidth, _segmentView.scrollView.frame.size.height)];
    _unSettleTableview.orderStatus = unSettlement;
    _unSettleTableview.delegate = self;
    
    _settleTableView = [[TGOrderListTableView alloc] initWithFrame:CGRectMake(screenWidth, 0, screenWidth, _segmentView.scrollView.frame.size.height)];
    _settleTableView.orderStatus = settlement;
    _settleTableView.delegate = self;
    
    [_segmentView.scrollView addSubview:_unSettleTableview];
    [_segmentView.scrollView addSubview:_settleTableView];

    
    [self.view addSubview:_segmentView];
}

- (void)initVariable
{
    
    _isFirstLoad = YES;
    
    [self unSettleOrderRequest:_unSettleTableview.pager];
}

- (void)settleOrderRequest:(TGModelPagerInfo *)pager
{
    [TGProgressHUD show];
    
    [[TGHTTPRequestEngine sharedInstance] getOrderList:[[[TGDataSingleton sharedInstance] userInfo] userNo] status:@"finished" pageNo:pager.currentPage pageSize:pager.pageSize viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if ([self httpResponseCorrect:responseObject]) {
            TGModelOrderListRsp *responseObj = (TGModelOrderListRsp *)responseObject;
            
            _settleTableView.totalMoney = responseObj.finishedServiceTotal;
            
            if (_settleTableView.pager.currentPage == 1) {
                //下拉刷新或者第一次加载
                [[_settleTableView dataSource] removeAllObjects];
                [[_settleTableView dataSource] addObjectsFromArray:responseObj.finishedServiceList__TGModelOrderList];
                [_settleTableView.tableView reloadData];
                
            }
            else
            {
                //load more
                NSMutableArray *indexPaths = [[NSMutableArray alloc] init];
                for (int i = 0; i < [responseObj.finishedServiceList__TGModelOrderList count]; i++) {
                    NSIndexPath *indexPath = [NSIndexPath indexPathForItem:[_unSettleTableview.dataSource count] inSection:0];
                    [indexPaths addObject:indexPath];
                    [_settleTableView.dataSource addObject:[responseObj.finishedServiceList__TGModelOrderList objectAtIndex:i]];
                }
                //插入新的数据
                [_settleTableView.tableView beginUpdates];
                [_settleTableView.tableView insertRowsAtIndexPaths:indexPaths withRowAnimation:UITableViewRowAnimationFade];
                [_settleTableView.tableView endUpdates];
                
            }
            //设置分页信息
            NSInteger rsNum = [responseObj.finishedServiceList__TGModelOrderList count];
            
            if (rsNum > 0) {
                _settleTableView.pager.currentPage += 1;
            }
            
            if (rsNum < _settleTableView.pager.pageSize)
            {
                _settleTableView.pager.isLastPage = YES;
            }
            //设置加载状态
            [_settleTableView stopLoading];
        }
        else
        {
            [_settleTableView stopLoading];
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [self httpRequestSystemError:error];
        
        //设置加载状态
        [_settleTableView stopLoading];
    }];
}

- (void)unSettleOrderRequest:(TGModelPagerInfo *)pager
{
    [TGProgressHUD show];
    
    [[TGHTTPRequestEngine sharedInstance] getOrderList:[[[TGDataSingleton sharedInstance] userInfo] userNo] status:@"unfinished" pageNo:pager.currentPage pageSize:pager.pageSize viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if ([self httpResponseCorrect:responseObject]) {
            TGModelOrderListRsp *responseObj = (TGModelOrderListRsp *)responseObject;
            
            if (_unSettleTableview.pager.currentPage == 1) {
                //下拉刷新或者第一次加载
                [[_unSettleTableview dataSource] removeAllObjects];
                [[_unSettleTableview dataSource] addObjectsFromArray:responseObj.unFinishedServiceList__TGModelOrderList];
                [_unSettleTableview.tableView reloadData];
                
            }
            else
            {
                //load more
                NSMutableArray *indexPaths = [[NSMutableArray alloc] init];
                for (int i = 0; i < [responseObj.unFinishedServiceList__TGModelOrderList count]; i++) {
                    NSIndexPath *indexPath = [NSIndexPath indexPathForItem:[_unSettleTableview.dataSource count] inSection:0];
                    [indexPaths addObject:indexPath];
                    [_unSettleTableview.dataSource addObject:[responseObj.unFinishedServiceList__TGModelOrderList objectAtIndex:i]];
                }
                //插入新的数据
                [_unSettleTableview.tableView beginUpdates];
                [_unSettleTableview.tableView insertRowsAtIndexPaths:indexPaths withRowAnimation:UITableViewRowAnimationFade];
                [_unSettleTableview.tableView endUpdates];
                
            }
            //设置分页信息
            NSInteger rsNum = [responseObj.unFinishedServiceList__TGModelOrderList count];
            
            if (rsNum > 0) {
                _unSettleTableview.pager.currentPage += 1;
            }
            
            if (rsNum < _unSettleTableview.pager.pageSize)
            {
                _unSettleTableview.pager.isLastPage = YES;
            }
            
            //设置加载状态
            [_unSettleTableview stopLoading];
        }
        else
        {
            [_unSettleTableview stopLoading];
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [self httpRequestSystemError:error];
        
        //设置加载状态
        [_unSettleTableview stopLoading];
    }];
}

- (void)setnavigationBar
{
    [self setNavigationTitle:@"我的账单"];
}

#pragma mark - TGOrderListTableViewDelegate
- (void)TGOrderListTableViewLoadeMore:(orderStatus)status pageInfo:(TGModelPagerInfo *)pager
{
    if (status == settlement) {
        [self settleOrderRequest:pager];
    }
    else
    {
        [self unSettleOrderRequest:pager];
    }
}

- (void)TGOrderListTableViewPullRefresh:(orderStatus)status pageInfo:(TGModelPagerInfo *)pager
{
    if (status == settlement) {
        [self settleOrderRequest:pager];
    }
    else
    {
        [self unSettleOrderRequest:pager];
    }
}

- (void)TGOrderListTableViewdidSelectRowAtIndexPath:(NSIndexPath *)indexPath orderStatus:(orderStatus)orderStatus
{
    TGModelOrderList *orderList = (TGModelOrderList *)[_settleTableView.dataSource objectAtIndex:indexPath.row];
    long long orderId = [orderList orderId];
    TGOrderDetailViewController *Vc = [[TGOrderDetailViewController alloc] init];
    Vc.orderId = orderId;
    [self.navigationController pushViewController:Vc animated:YES];
}

#pragma mark - TGCustomSegmentViewDelegate

- (void)TGCustomSegementViewDidChange:(NSInteger)currentPage
{
    if (currentPage > 0 && _isFirstLoad) {
        [self settleOrderRequest:_settleTableView.pager];
        _isFirstLoad = NO;
    }
}

@end
