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
#import "KKAppDelegate.h"
#import "KKApplicationDefine.h"

@interface KKServiceSeekingViewController ()
@property (nonatomic, retain) NSMutableArray *servicesList;
@end

@implementation KKServiceSeekingViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    if(![KKAuthorization sharedInstance].accessAuthorization.serviceSeeking)
    {
        [KKAppDelegateSingleton jumpToLoginVc];
        [self backButtonClicked];
    }
    
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
    _page = 1;
    _size = 10;
    _isLoading = NO;
    _enableRefresh = YES;
}

- (void) initComponents
{
    [self setNavgationBar];
    
//    _segmentControl = [[KKServiceSegmentControl alloc] initWithFrame:CGRectMake(0, 0, 320, 35)];
//    _segmentControl.delegate = self;
//    [self.view addSubview:_segmentControl];
//    [_segmentControl release];
    
    _mainTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, 320, currentScreenHeight - 44 - 49 - [self getOrignY]) style:UITableViewStylePlain];
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
    
    [[KKProtocolEngine sharedPtlEngine] serviceAllHistoryList:_page pageSize:_size delegate:self];
}

- (void)refreshUnfinishedService
{
    [self.servicesList removeAllObjects];
    _page = 1;
    _isLoading = YES;
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    
    [[KKProtocolEngine sharedPtlEngine] serviceAllHistoryList:_page pageSize:_size delegate:self];
    
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
    return [self.servicesList count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString* reuseID = @"Service_Seeking_cell";
    KKServiceSeekingTableViewCell * cell = [tableView dequeueReusableCellWithIdentifier:reuseID];
    
    if (nil == cell)
    {
        cell = [[[KKServiceSeekingTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseID] autorelease];
    }
    
    KKModelserviceDetail *service = [self.servicesList objectAtIndex:indexPath.row];
    
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
    KKModelserviceDetail *service = [self.servicesList objectAtIndex:indexPath.row];
    
    
    KKServiceDetailViewController *Vc = [[KKServiceDetailViewController alloc] initWithNibName:@"KKServiceDetailViewController" bundle:nil];
    Vc.orderId = service.id;
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
    if (!_isLoading && yMargin > -60 && contentSize.height > scrollView.bounds.size.height && _enableRefresh)
    {
        [self getInfo];
    }
}

#pragma mark -
#pragma mark KKServiceSegmentControlDelegate

- (void)KKServiceSegmentControlSegmentChanged:(NSInteger)index
{
    
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
    if(self.servicesList)
    {
        [self.servicesList addObjectsFromArray:listRsp.results__KKModelserviceDetail];
    }
    else
        self.servicesList = listRsp.results__KKModelserviceDetail;
    
    _page = listRsp.pager.nextPage;
    if(listRsp.pager.isLastPage)
    {
        _enableRefresh = NO;
    }
    
//    [_segmentControl updateInfo];
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
    
    self.servicesList = nil;
    
    [super dealloc];
}
@end
