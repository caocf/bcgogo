//
//  KKServiceSeekingViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-20.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKServiceSeekingViewController.h"
#import "KKViewUtils.h"
#import "UIViewController+extend.h"
#import "KKApplicationDefine.h"
#import "KKServiceSeekingTableViewCell.h"
#import "KKServiceDetailViewController.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKPreference.h"
#import "MBProgressHUD.h"
#import "UIImageView+WebCache.h"

@interface KKServiceSeekingViewController ()

@end

@implementation KKServiceSeekingViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshUnfinishedService) name:@"refreshUnfinishedServiceNotification" object:nil];
    
    [self setVcEdgesForExtendedLayout];
    [self initVariables];
    [self initComponents];
    [self getInfo];
}

#pragma mark -
#pragma mark Custom methods

- (void) initVariables
{
    _dataArray1 = [[NSMutableArray alloc] init];
    _dataArray2 = [[NSMutableArray alloc] init];

    _page0 = 1;
    _page1 = 1;
    _size = 10;
    _selectedIndex = 0;
    _isLoading = NO;
    _enableRefresh0 = YES;
    _enableRefresh1 = YES;
}

- (void) initComponents
{
    [self setNavgationBar];
    
    _segmentControl = [[KKServiceSegmentControl alloc] initWithFrame:CGRectMake(0, 0, 320, 35)];
    _segmentControl.delegate = self;
    [self.view addSubview:_segmentControl];
    [_segmentControl release];
    
    _mainTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 33, 320, currentScreenHeight - 33 - 44 - 49 - [self getOrignY]) style:UITableViewStylePlain];
    _mainTableView.delegate = self;
    _mainTableView.dataSource = self;
    _mainTableView.backgroundColor = [UIColor clearColor];
    _mainTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:_mainTableView.bounds];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor clearColor];
    bgImv.image = [UIImage imageNamed:@"bg_serviceSeeking.png"];
    _mainTableView.backgroundView = bgImv;
    [bgImv release];
    
    [self.view addSubview:_mainTableView];
    [_mainTableView release];
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"服务查询";
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
}

- (void)getInfo
{
    _isLoading = YES;
    
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    
    if (_selectedIndex == 0)
    {        
        [[KKProtocolEngine sharedPtlEngine] serviceHistoryListWithServiceScope:nil status:@"unfinished" pageNo:_page0 pageSize:_size delegate:self];
    }
    else
        [[KKProtocolEngine sharedPtlEngine] serviceHistoryListWithServiceScope:nil status:@"finished" pageNo:_page1 pageSize:_size delegate:self];
}

- (void)refreshUnfinishedService
{
    _page0 = 1;
    _isLoading = YES;
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    
    [[KKProtocolEngine sharedPtlEngine] serviceHistoryListWithServiceScope:nil status:@"unfinished" pageNo:_page0 pageSize:_size delegate:self];
    
}

#pragma mark -
#pragma mark Events

- (void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark -
#pragma mark UITableViewDataSource, UITableViewDelegate
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    NSInteger count = [_dataArray1 count];
    
    if(_selectedIndex == 1)
        count = [_dataArray2 count];
    
    return count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString* reuseID = @"Service_Seeking_cell";
    KKServiceSeekingTableViewCell * cell = [tableView dequeueReusableCellWithIdentifier:reuseID];
    
    if (nil == cell)
    {
        cell = [[[KKServiceSeekingTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseID] autorelease];
    }
    
    KKModelService *service = nil;
    if (_selectedIndex == 0)
        service = [_dataArray1 objectAtIndex:indexPath.row];
    else
        service = [_dataArray2 objectAtIndex:indexPath.row];
    
    [cell setCellContent:service];
    [cell.iconImv setImageWithURL:[NSURL URLWithString:service.shopImageUrl]];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.backgroundColor = [UIColor clearColor];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 95;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    KKModelService *service = nil;
    if (_selectedIndex == 0)
        service = [_dataArray1 objectAtIndex:indexPath.row];
    else
        service = [_dataArray2 objectAtIndex:indexPath.row];
    
    
    KKServiceDetailViewController *Vc = [[KKServiceDetailViewController alloc] initWithNibName:@"KKServiceDetailViewController" bundle:nil];
    Vc.orderId = service.orderId;
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
}

#pragma mark -
#pragma mark - UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{    
    CGPoint offset = scrollView.contentOffset;
    CGSize size = scrollView.frame.size;
    CGSize contentSize = scrollView.contentSize;
    float yMargin = offset.y + size.height - contentSize.height;
    if (!_isLoading && yMargin > -60)
    {
        if (_selectedIndex == 0 && _enableRefresh0)
            [self getInfo];
        if (_selectedIndex == 1 && _enableRefresh1)
            [self getInfo];
    }
}

#pragma mark -
#pragma mark KKServiceSegmentControlDelegate

- (void)KKServiceSegmentControlSegmentChanged:(NSInteger)index
{
    _selectedIndex = index;
    if (_selectedIndex == 0 )
    {
        if ([_dataArray1 count] == 0)
        {
            _page0 = 1;
            [self getInfo];
        }
    }
    else
    {
        if ([_dataArray2 count] == 0)
        {
            _page1 = 1;
            [self getInfo];
        }
    }
    
    [_mainTableView reloadData];
}


#pragma mark -
#pragma mark KKProtocolEngineDelegate
- (NSNumber *)serviceHistoryListResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    _isLoading = NO;
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    KKModelServiceHistoryListRsp *listRsp = (KKModelServiceHistoryListRsp *)rsp;
    
    if (_selectedIndex == 0)
    {
        _segmentControl.unfinishedNum = listRsp.unFinishedServiceCount;
        
        if (_page0 == 1)
            [_dataArray1 removeAllObjects];
        _page0 ++;
        
        if ([listRsp.KKArrayFieldName(unFinishedServiceList, KKModelService) count] < _size)
            _enableRefresh0 = NO;
        [_dataArray1 addObjectsFromArray:listRsp.KKArrayFieldName(unFinishedServiceList, KKModelService)];
    }
    else
    {
        _segmentControl.finishedNum = listRsp.finishedServiceCount;
        
        if (_page1 == 1)
            [_dataArray2 removeAllObjects];
        _page1 ++;
        
        if ([listRsp.KKArrayFieldName(finishedServiceList, KKModelService) count] < _size)
            _enableRefresh1 = NO;
        
         [_dataArray2 addObjectsFromArray:listRsp.KKArrayFieldName(finishedServiceList, KKModelService)];
    }
    [_segmentControl updateInfo];
    [_mainTableView reloadData];
    
    return KKNumberResultEnd;
}

#pragma mark -
#pragma mark Handle memory

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    _mainTableView = nil;
    _segmentControl = nil;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
    _mainTableView = nil;
    _segmentControl = nil;
    
    [_dataArray1 release];
    _dataArray1 = nil;
    [_dataArray2 release];
    _dataArray2 = nil;
    
    [super dealloc];
}
@end
