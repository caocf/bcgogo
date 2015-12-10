//
//  TGDTCManagerViewController.m
//  TGOBD
//
//  Created by Jiahai on 14-3-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGDTCManagerViewController.h"
#import "UATitledModalPanel.h"
#import "TGOrderOnlineViewController.h"
#import "TGMessageDBManager.h"
#import "TGDataSingleton.h"

#define DTCSTATUS_FIXED         @"FIXED"
#define DTCSTATUS_UNTREATED     @"UNTREATED"
#define DTCSTATUS_DELETED       @"DELETED"
#define FaultCodeListPageSize       10

@interface TGDTCManagerViewController ()
@property (nonatomic, strong) NSMutableArray        *dtcArray;
@property (nonatomic, strong) TGModelPagerInfo      *pager;                 //分页信息
@property (nonatomic, strong) NSMutableArray        *historyDTCArray;       //历史故障
@property (nonatomic, strong) TGModelPagerInfo      *historyPager;          //历史故障分页信息
@end

@implementation TGDTCManagerViewController

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
	// Do any additional setup after loading the view.
    
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar];
    CGFloat height = [self getViewHeightWithNavigationBar];
    
    _enableRefresh = _enableRefreshHistory = YES;
    
    self.dtcArray = [[NSMutableArray alloc] init];
    self.historyDTCArray = [[NSMutableArray alloc] init];
    
    [self setNavigationTitle:@"故障查询"];
    
    UIEdgeInsets edge;
    edge.left = 20;
    _createOrderBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [_createOrderBtn setFrame:CGRectMake(0, 0, 82, 30)];
    _createOrderBtn.titleLabel.font = [UIFont systemFontOfSize:13];
    [_createOrderBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [_createOrderBtn setTitleColor:[UIColor blackColor] forState:UIControlStateHighlighted];
    [_createOrderBtn setTitle:@"预约维修" forState:UIControlStateNormal];
    //[_createOrderBtn setTitleEdgeInsets:edge];
    [_createOrderBtn setBackgroundImage:[UIImage imageNamed:@"bg_service.png"] forState:UIControlStateNormal];
    [_createOrderBtn addTarget:self action:@selector(createOrderOnline) forControlEvents:UIControlEventTouchUpInside];
    _createOrderBtn.hidden = YES;
    
    //edge.left = 40;
    //[((UILabel *)self.navigationItem.titleView) setTextAlignment:NSTextAlignmentRight];
    
   // self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:_createOrderBtn];
    self.navigationItem.rightBarButtonItem = [TGViewUtils createNavigationBarButtonItemWithTitle:@"预约维修" bgImage:nil target:self action:@selector(createOrderOnline)];
    
    TGCustomSegmentView *segmentView = [[TGCustomSegmentView alloc] initWithFrame:CGRectMake(0, originY, screenWidth, height) segmentTitles:@[@"当前故障",@"历史故障"]];
    segmentView.delegate = self;
    
    _mainTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, segmentView.scrollView.frame.size.width, segmentView.scrollView.frame.size.height) style:UITableViewStylePlain];
    _mainTableView.delegate = self;
    _mainTableView.dataSource = self;
    _mainTableView.backgroundColor = [UIColor clearColor];
    _mainTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _mainTableView.showsVerticalScrollIndicator = NO;
    [segmentView.scrollView addSubview:_mainTableView];
    
    _refreshHeaderView = [[EGORefreshTableHeaderView alloc] initWithFrame:CGRectMake(0, -_mainTableView.bounds.size.height, _mainTableView.bounds.size.width, _mainTableView.bounds.size.height)];
    _refreshHeaderView.delegate = self;
    _refreshHeaderView.refreshTableName = @"dtcTableView";
    [_mainTableView addSubview:_refreshHeaderView];
    
    _historyTableView = [[UITableView alloc] initWithFrame:CGRectMake(segmentView.scrollView.frame.size.width, 0, segmentView.scrollView.frame.size.width, segmentView.scrollView.frame.size.height) style:UITableViewStylePlain];
    _historyTableView.delegate = self;
    _historyTableView.dataSource = self;
    _historyTableView.backgroundColor = [UIColor clearColor];
    _historyTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _historyTableView.showsVerticalScrollIndicator = NO;
    [segmentView.scrollView addSubview:_historyTableView];
    
    _historyRefreshHeaderView = [[EGORefreshTableHeaderView alloc] initWithFrame:CGRectMake(0, -_historyTableView.bounds.size.height, _historyTableView.bounds.size.width, _historyTableView.bounds.size.height)];
    _historyRefreshHeaderView.delegate = self;
    _historyRefreshHeaderView.refreshTableName = @"historyDTCTableView";
    [_historyTableView addSubview:_historyRefreshHeaderView];
    
    [self.view addSubview:segmentView];
    
    [self getCurrentDTCList:1];
    
    [self setDTCMessageRead];
}

- (void)setDTCMessageRead
{
    [[TGMessageDBManager sharedMessageDBManager] setMessageRead:[[[TGDataSingleton sharedInstance] userInfo] userNo] messageType:VEHICLE_FAULT_2_APP];
    
    //发送重新设置未读消息的数目通知
    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_SetUnreadMessageNum object:nil];
}

- (void)createOrderOnline
{
    NSMutableArray *array = [NSMutableArray array];
    for(TGModelDTCInfo *info in self.dtcArray)
    {
        NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
        [dict setObject:info.errorCode forKey:@"faultCode"];
        [dict setObject:[NSString stringWithFormat:@"%lld", info.appVehicleId] forKey:@"appVehicleId"];
        [dict setObject:info.content forKey:@"description"];
        [array addObject:dict];
    }
    
    TGOrderOnlineViewController *orderVc = [[TGOrderOnlineViewController alloc] init];
    orderVc.faultInfoItems = array;
    [self.navigationController pushViewController:orderVc animated:YES];
}

#pragma mark - Event

- (void)isShowCreateOrderBtn
{
    if(_currentSegmentIndex == 0)
    {
        //_createOrderBtn.hidden = !([self.dtcArray count] > 0);
        if ([self.dtcArray count] > 0) {
            self.navigationItem.rightBarButtonItem = [TGViewUtils createNavigationBarButtonItemWithTitle:@"预约维修" bgImage:nil target:self action:@selector(createOrderOnline)];
        }
        else
        {
            self.navigationItem.rightBarButtonItem = nil;
        }
    }
    else
    {
        //_createOrderBtn.hidden = YES;
        self.navigationItem.rightBarButtonItem = nil;
    }
}

//获取当前Segment下的DTC数据
-(NSMutableArray *)getCurrentDataArray
{
    NSMutableArray *array = nil;

    if(_currentSegmentIndex == 0)
    {
        array = self.dtcArray;
    }
    else
        array = self.historyDTCArray;

    return array;
}

-(void) refreshView:(NSInteger)index
{
    [self downLoadingTableViewData:index];
    
    UITableView *tableView = nil;
    NSArray *data = index == 0 ? self.dtcArray : self.historyDTCArray;
    switch (index) {
        case 0:
        {
            tableView = _mainTableView;
            if([data count] == 0)
            {
                if(_noDataImageView == nil)
                {
                    UIImage *img = [UIImage imageNamed:@"no_data.png"];
                    _noDataImageView = [[UIImageView alloc] initWithImage:img];
                    _noDataImageView.frame = CGRectMake((320-img.size.width)*0.5, 60, img.size.width, img.size.height);
                }
                [tableView addSubview:_noDataImageView];
            }
            else
            {
                if(_noDataImageView != nil)
                {
                    [_noDataImageView removeFromSuperview];
                }
                _noDataImageView = nil;
            }

        }
            break;
        case 1:
        {
            tableView = _historyTableView;
            if([data count] == 0)
            {
                if(_noDataHistoryImageView == nil)
                {
                    UIImage *img = [UIImage imageNamed:@"no_data.png"];
                    _noDataHistoryImageView = [[UIImageView alloc] initWithImage:img];
                    _noDataHistoryImageView.frame = CGRectMake((320-img.size.width)*0.5, 60, img.size.width, img.size.height);
                }
                [tableView addSubview:_noDataHistoryImageView];
            }
            else
            {
                if(_noDataHistoryImageView != nil)
                {
                    [_noDataHistoryImageView removeFromSuperview];
                }
                _noDataHistoryImageView = nil;
            }
        }
            break;
        default:
            break;
    }
    
    [tableView reloadData];
}

- (void)getCurrentDTCList:(NSInteger)aPageNo
{
    _isLoading = YES;
    [TGProgressHUD show];
    [[TGHTTPRequestEngine sharedInstance] dtcGetList:DTCSTATUS_UNTREATED pageNo:aPageNo pageSize:FaultCodeListPageSize viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if([self httpResponseCorrect:responseObject])
        {
            TGModelDTCListRsp *dtcRsp = (TGModelDTCListRsp *)responseObject;
            if(_reGetFlag)
            {
                _reGetFlag = NO;
                [self.dtcArray removeAllObjects];
            }
            
            self.pager = dtcRsp.pager;
            
            [self.dtcArray addObjectsFromArray:dtcRsp.result__TGModelDTCInfo];
            
            [self isShowCreateOrderBtn];
            
            _enableRefresh = !self.pager.isLastPage;
            
            [self refreshView:0];
        }
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        _reGetFlag = NO;
        [self downLoadingTableViewData:0];
        [self httpRequestSystemError:error];
    }];
}

- (void)getHistoryFixedDTCList:(NSInteger)aPageNo
{
    _isLoading = YES;
    [TGProgressHUD show];
    [[TGHTTPRequestEngine sharedInstance] dtcGetList:DTCSTATUS_FIXED pageNo:aPageNo pageSize:FaultCodeListPageSize viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if([self httpResponseCorrect:responseObject])
        {
            TGModelDTCListRsp *dtcRsp = (TGModelDTCListRsp *)responseObject;
            if(_reGetFlagHistory)
            {
                _reGetFlagHistory = NO;
                [self.historyDTCArray removeAllObjects];
            }
            
            self.historyPager = dtcRsp.pager;
            
            [self.historyDTCArray addObjectsFromArray:dtcRsp.result__TGModelDTCInfo];
            
            _enableRefreshHistory = !self.historyPager.isLastPage;
            
            [self refreshView:1];
        }
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        _reGetFlagHistory = NO;
        [self downLoadingTableViewData:1];
        [self httpRequestSystemError:error];
    }];
}

- (void)reGetCurrentDTCList
{
    _reGetFlag = YES;
    
    [self getCurrentDTCList:1];
}

- (void)reGetHistoryFixedDTCList
{
    _reGetFlagHistory = YES;
    
    [self getHistoryFixedDTCList:1];
}

- (void)reloadDTCData
{
    _isLoading = YES;
    
    if(_currentSegmentIndex == 0)
    {
        _reGetFlag = YES;
        [self getCurrentDTCList:1];
    }
    else
    {
        _reGetFlagHistory = YES;
        [self getHistoryFixedDTCList:1];
    }
}

- (void)downLoadingTableViewData:(NSInteger)index
{
    _isLoading = NO;
    
    if(index == 0)
    {
        [_refreshHeaderView egoRefreshScrollViewDataSourceDidFinishedLoading:_mainTableView];
    }
    else
    {
        [_historyRefreshHeaderView egoRefreshScrollViewDataSourceDidFinishedLoading:_historyTableView];
    }
}

#pragma mark - TGCustomSegmentViewDelegate
- (void)TGCustomSegementViewDidChange:(NSInteger)currentPage
{
    _currentSegmentIndex = currentPage;
    
    [self isShowCreateOrderBtn];
    
    if(_currentSegmentIndex == 1 && _enableRefreshHistory && self.historyPager == nil)
    {
        [self getHistoryFixedDTCList:1];
    }
}

#pragma mark - UITableViewDelegate
-(CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    CGFloat height = 0;
    
    TGModelDTCInfo *dtcInfo = [[self getCurrentDataArray] objectAtIndex:indexPath.row];
    //赋值需放在此处，否则计算cell高度会有误差
    if(dtcInfo.category == nil)
    {
        dtcInfo.category = @"暂无分类";
    }
    
    if(dtcInfo.content == nil)
    {
        dtcInfo.content = @"暂无描述信息";
    }
    
//    if(dtcInfo.backgroundInfo == nil)
//    {
//        dtcInfo.backgroundInfo = @"暂无背景知识";
//    }

    height = [TGDTCTableCell calculateVehicleConditionTableViewCellHeightWithContent:dtcInfo];
    
    return height;
}

-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [[self getCurrentDataArray] count];
}

-(TGDTCTableCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identifier1 = @"dtcTableCell";
    static NSString *identifier2 = @"dtcHistoryTableCell";
    
    NSString *identifier = nil;
    
    if(_currentSegmentIndex == 0)
    {
        identifier = identifier1;
    }
    else
    {
        identifier = identifier2;
    }
    
    TGDTCTableCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if(cell == nil)
    {
        cell = [[TGDTCTableCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
        cell.delegate = self;
    }
    
    TGModelDTCInfo *dtcInfo = [[self getCurrentDataArray] objectAtIndex:indexPath.row];
    
    [cell setDTCMessage:dtcInfo isHistory:(_currentSegmentIndex == 1)];
    
    return cell;
}

#pragma mark -
#pragma mark TGDTCTableCellDelegate
-(void) controlBtnClicked:(NSInteger)btnType dtcInfo:(TGModelDTCInfo *)dtcInfo
{
    switch (btnType) {
        case 1:
        {
            //修复按钮
            [TGProgressHUD show];
            [[TGHTTPRequestEngine sharedInstance] dtcOperate:dtcInfo.id errorCode:dtcInfo.errorCode oldStatus:DTCSTATUS_UNTREATED newStatus:DTCSTATUS_FIXED vehicleId:dtcInfo.appVehicleId viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
                [self reGetCurrentDTCList];
            } failure:self.faultBlock];
        }
            break;
        case 2:
        {
            //背景知识
            UATitledModalPanel *panel = [[UATitledModalPanel alloc] initWithFrame:CGRectMake(0, [self getViewLayoutStartOriginYWithNavigationBar], screenWidth, [self getViewHeightWithNavigationBar])];
            panel.headerLabel.text = @"背景知识";
            CGRect rect = [panel contentViewFrame];
            rect.origin.x = rect.origin.y = 0;
            UITextView *textView = [[UITextView alloc] initWithFrame:rect];
            textView.backgroundColor = [UIColor clearColor];
            textView.textColor = [UIColor blackColor];
            textView.font = [UIFont systemFontOfSize:16];
            textView.editable = NO;
            textView.text = [NSString stringWithFormat:@"\t%@",dtcInfo.backgroundInfo];
            [panel.contentView addSubview:textView];
            
            [self.view addSubview:panel];
            [panel show];
        }
            break;
        case 3:
        {
            //删除按钮
            [TGProgressHUD show];
            [[TGHTTPRequestEngine sharedInstance] dtcOperate:dtcInfo.id errorCode:dtcInfo.errorCode oldStatus:DTCSTATUS_FIXED newStatus:DTCSTATUS_DELETED vehicleId:dtcInfo.appVehicleId viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
                [self reGetHistoryFixedDTCList];
            } failure:self.faultBlock];
        }
            break;
    }
}

#pragma mark - UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    CGPoint offset = scrollView.contentOffset;
    CGSize size = scrollView.frame.size;
    CGSize contentSize = scrollView.contentSize;
    float yMargin = offset.y + size.height - contentSize.height;
    
    if(_currentSegmentIndex == 0)
    {
        if (_enableRefresh && !_isLoading && yMargin > -1 && contentSize.height > scrollView.bounds.size.height)
        {
            if(!self.pager.isLastPage)
            {
                [self getCurrentDTCList:self.pager.nextPage];
            }
        }
        [_refreshHeaderView egoRefreshScrollViewDidScroll:scrollView];
    }
    else
    {
        if (_enableRefreshHistory && !_isLoading && yMargin > -1 && contentSize.height > scrollView.bounds.size.height)
        {
            if(!self.historyPager.isLastPage)
            {
                [self getHistoryFixedDTCList:self.historyPager.nextPage];
            }
        }
        [_historyRefreshHeaderView egoRefreshScrollViewDidScroll:scrollView];
    }
    
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    if(_currentSegmentIndex == 0)
        [_refreshHeaderView egoRefreshScrollViewDidEndDragging:scrollView];
    else
        [_historyRefreshHeaderView egoRefreshScrollViewDidEndDragging:scrollView];
}

#pragma mark - EGORefreshTableHeaderDelegate
- (void)egoRefreshTableHeaderDidTriggerRefresh:(EGORefreshTableHeaderView *)view
{
    [self reloadDTCData];
}

- (BOOL)egoRefreshTableHeaderDataSourceIsLoading:(EGORefreshTableHeaderView *)view
{
    return  _isLoading;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
