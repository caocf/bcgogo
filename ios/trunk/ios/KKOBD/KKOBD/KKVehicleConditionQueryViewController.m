//
//  KKVehicleConditionQueryViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-22.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKVehicleConditionQueryViewController.h"
#import "KKApplicationDefine.h"
#import "KKViewUtils.h"
#import "UIViewController+extend.h"
#import "KKVehicleConditionTableViewCell.h"
#import "KKAppDelegate.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKPreference.h"
#import "MBProgressHUD.h"
#import "KKTBDictFault.h"
#import "KKDB.h"
#import "KKTBDTCMessage.h"
#import "KKShopQueryViewController.h"
#import "KKBindCarViewController.h"

@interface KKVehicleConditionQueryViewController ()

@end

@implementation KKVehicleConditionQueryViewController
- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self setVcEdgesForExtendedLayout];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateVehicleCondition) name:@"updateVehicleRealTimeDataNotification" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateDtcArray) name:@"updateDtcArrayNotication" object:nil];
    
    [self initVariables];
    [self initComponents];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

#pragma mark -
#pragma mark Custom methods

- (void) initVariables
{
    _dataArray = [[NSMutableArray alloc] init];
    [self getDtcMessages];
}

- (void) initComponents
{
    [self setNavgationBar];
    
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:self.view.bounds];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor clearColor];
    bgImv.image = [UIImage imageNamed:@"bg_serviceSeeking.png"];
    [self.view addSubview:bgImv];
    [bgImv release];
    
    _carStatusView = [[KKVehicleConditionView alloc] initWithFrame:CGRectMake(0, 0, 320, 55)];
    [_carStatusView setContent:KKAppDelegateSingleton.vehicleRealtimeData];
    
    _mainTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, 320, currentScreenHeight - 44 - 49 - [self getOrignY]) style:UITableViewStylePlain];
    _mainTableView.delegate = self;
    _mainTableView.dataSource = self;
    _mainTableView.backgroundColor = [UIColor clearColor];
    _mainTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _mainTableView.tableHeaderView = _carStatusView;
    [self.view addSubview:_mainTableView];
    [_carStatusView release];
    [_mainTableView release];
    
    
    if (_refreshHeaderView == nil) {
		
		EGORefreshTableHeaderView *view = [[EGORefreshTableHeaderView alloc] initWithFrame:CGRectMake(0.0f, 0.0f - _mainTableView.bounds.size.height, self.view.frame.size.width, _mainTableView.bounds.size.height)];
		view.delegate = self;
		[_mainTableView addSubview:view];
		_refreshHeaderView = view;
		[view release];
		
	}
	//  update the last update date
	[_refreshHeaderView refreshLastUpdatedDate];
    
    
    if ([_dataArray count] == 0)
    {
        if (KKAppDelegateSingleton.connectStatus == e_CarNotOnLine)
        {
            _unLinkPromptView = [[UIView alloc] initWithFrame:CGRectMake(0, 55, 320, 95)];
            _unLinkPromptView.backgroundColor = [UIColor clearColor];
            
            UIImage *image = [UIImage imageNamed:@"icon_prompt.png"];
            UIImageView *iconImv = [[UIImageView alloc] initWithFrame:CGRectMake(40, 0.5*(95-image.size.height), image.size.width, image.size.height)];
            iconImv.image = image;
            [_unLinkPromptView addSubview:iconImv];
            [iconImv release];
            
            UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(100, 25, 220, 20)];
            titleLabel.text = @"未完成连接车辆准备！";
            titleLabel.backgroundColor = [UIColor clearColor];
            titleLabel.textAlignment = UITextAlignmentLeft;
            titleLabel.textColor = [UIColor blackColor];
            titleLabel.font = [UIFont boldSystemFontOfSize:19.0f];
            [_unLinkPromptView addSubview:titleLabel];
            [titleLabel release];
            
            UIButton *bindBtn = [[UIButton alloc] initWithFrame:CGRectMake(95, 35, 44, 60)];
            [bindBtn setTitle:@"绑定" forState:UIControlStateNormal];
            [bindBtn.titleLabel setFont:[UIFont boldSystemFontOfSize:19.0]];
            [bindBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
            [bindBtn addTarget:self action:@selector(bindButtonClicked) forControlEvents:UIControlEventTouchUpInside];
            [_unLinkPromptView addSubview:bindBtn];
            [bindBtn release];
            
            [_mainTableView addSubview:_unLinkPromptView];
            [_unLinkPromptView release];
        }
        else
        {
            _linkWellPromptView = [[UIView alloc] initWithFrame:CGRectMake(0, 55, 320, 290)];
            _linkWellPromptView.backgroundColor = [UIColor clearColor];
            
            UIImage *image = [UIImage imageNamed:@"icon_vehicleCondition_well.png"];
            UIImageView *iconImv = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), 35, image.size.width, image.size.height)];
            iconImv.image = image;
            [_linkWellPromptView addSubview:iconImv];
            [iconImv release];
            
            UILabel *textLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 35+image.size.height + 20, 320, 20)];
            textLabel.backgroundColor = [UIColor clearColor];
            textLabel.textColor = [UIColor blackColor];
            textLabel.text = @"车况指数良好，请您继续保持！";
            textLabel.textAlignment = UITextAlignmentCenter;
            [_linkWellPromptView addSubview:textLabel];
            [textLabel release];
            
            [_mainTableView addSubview:_linkWellPromptView];
            [_linkWellPromptView release];
        }
    }
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"车况查询";
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
    
    self.navigationItem.rightBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_clearMsg.png"] bgImage:nil target:self action:@selector(clearButtonClicked)];
}

- (void)updateVehicleCondition
{
    if (_unLinkPromptView == nil)
    {
        [_unLinkPromptView removeFromSuperview];
        _unLinkPromptView = nil;
    }
    [_carStatusView setContent:KKAppDelegateSingleton.vehicleRealtimeData];
}

- (void)updateDtcArray
{
    if (_unLinkPromptView)
    {
        [_unLinkPromptView removeFromSuperview];
        _unLinkPromptView = nil;
    }
    if (_linkWellPromptView)
    {
        [_linkWellPromptView removeFromSuperview];
        _unLinkPromptView = nil;
    }
    
    [self getDtcMessages];
}

- (void)getDtcMessages
{
    [_dataArray removeAllObjects];
    
    KKTBDTCMessage *dtcTb = [[KKTBDTCMessage alloc] initWithDB:[KKDB sharedDB]];
    [_dataArray addObjectsFromArray:[dtcTb getDTCMessageByUserNo:[KKProtocolEngine sharedPtlEngine].userName vehicleModelId:KKAppDelegateSingleton.currentVehicle.vehicleModelId]];
    [dtcTb release];
    
    KKTBDictFault *faultDict = [[KKTBDictFault alloc] initWithDB:[KKDB sharedDB]];
    for (KKModelDTCMessage *message in _dataArray)
    {
        NSArray *arr = [faultDict getFaultInfoWithCode:message.faultCode vehicleModelId:message.vehicleModelId];
        if ([arr count] == 0)
            arr = [NSArray arrayWithObjects:@"未知故障", nil];
        message.desArray = arr;
    }
    [faultDict release];
    
    [_mainTableView reloadData];
}

#pragma mark -
#pragma mark Events

- (void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)clearButtonClicked
{
    if ([_dataArray count] > 0)
    {
        KKCustomAlertView *alertView = [[KKCustomAlertView alloc] initWithMessage:@"您是否想删除所有的车况信息？" WithType:KKCustomAlertView_default];
        [alertView addButtonWithTitle:@"取消" imageName:@"alert-blue2-button.png" block:nil];
        [alertView addButtonWithTitle:@"确定" imageName:@"alert-blue2-button.png" block:^{
            KKTBDTCMessage *dtcTb = [[KKTBDTCMessage alloc] initWithDB:[KKDB sharedDB]];
            [dtcTb deleteDTCMessagesWithUserNo:[KKProtocolEngine sharedPtlEngine].userName];
            [dtcTb release];
            
            [_dataArray removeAllObjects];
            [_mainTableView reloadData];
        }];
        [alertView show];
        [alertView release];
    }
}

- (void)refreshButtonClicked
{
    if (KKAppDelegateSingleton.bleEngine && [KKAppDelegateSingleton.bleEngine supportBLE] && KKAppDelegateSingleton.currentConnectedPeripheral != nil)
    {
        MBProgressHUD *hud = [[MBProgressHUD alloc] initWithView:self.view];
        hud.labelText = @"正在更新车况";
        [self.view addSubview:hud];
        [hud show:YES];
        [hud hide:YES afterDelay:5];
        [hud release];
        
        [KKAppDelegateSingleton getVehicleRealData:YES];
    }
    else
        [KKCustomAlertView showAlertViewWithMessage:@"您的手机未连接OBD，不能更新车况信息"];
}


- (void)bindButtonClicked
{
    KKBindCarViewController *bindVc = [[KKBindCarViewController alloc] init];
    [self.navigationController pushViewController:bindVc animated:YES];
    [bindVc release];
}

#pragma mark -
#pragma mark UITableViewDataSource,UITableViewDelegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_dataArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"cell_searchCar";
    KKVehicleConditionTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil)
    {
        cell = [[[KKVehicleConditionTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
    }
    KKModelDTCMessage *dtcMsg = (KKModelDTCMessage *)[_dataArray objectAtIndex:indexPath.row];
    [cell setContent:dtcMsg];
    
    cell.backgroundColor = [UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    KKModelDTCMessage *dtcMsg = (KKModelDTCMessage *)[_dataArray objectAtIndex:indexPath.row];
    return [KKVehicleConditionTableViewCell calculateVehicleConditionTableViewCellHeightWithContent:dtcMsg];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    KKModelDTCMessage *dtcMsg = (KKModelDTCMessage *)[_dataArray objectAtIndex:indexPath.row];
    
    KKShopQueryViewController *Vc = [[KKShopQueryViewController alloc] initWithNibName:@"KKShopQueryViewController" bundle:nil];
    Vc.serviceTypeKey = @"服务范围";
    Vc.remarkString = [KKHelper getVehicleFaultDesWithFaultCode:dtcMsg.faultCode andVehicleModelId:KKAppDelegateSingleton.currentVehicle.vehicleModelId];
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    KKModelDTCMessage *message = [_dataArray objectAtIndex:indexPath.row];
    KKTBDTCMessage *dtcTb = [[KKTBDTCMessage alloc] initWithDB:[KKDB sharedDB]];
    [dtcTb deleteDTCMessage:message.faultCode WithUserNo:message.userNo];
    [dtcTb release];
    
    [_dataArray removeObjectAtIndex:indexPath.row];
    [_mainTableView reloadData];
}

- (NSString *)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return @"删除";
}

#pragma mark -
#pragma mark Data Source Loading / Reloading Methods

- (void)reloadTableViewDataSource{
	
	//  should be calling your tableviews data source model to reload
	//  put here just for demo
	_reloading = YES;
	
}

- (void)doneLoadingTableViewData{
	
	//  model should call this when its done loading
	_reloading = NO;
	[_refreshHeaderView egoRefreshScrollViewDataSourceDidFinishedLoading:_mainTableView];
}


#pragma mark -
#pragma mark UIScrollViewDelegate Methods

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
	
	[_refreshHeaderView egoRefreshScrollViewDidScroll:scrollView];
    
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate{
	
	[_refreshHeaderView egoRefreshScrollViewDidEndDragging:scrollView];
	
}


#pragma mark -
#pragma mark EGORefreshTableHeaderDelegate Methods

- (void)egoRefreshTableHeaderDidTriggerRefresh:(EGORefreshTableHeaderView*)view{
	
	[self reloadTableViewDataSource];
    
    if (KKAppDelegateSingleton.bleEngine && [KKAppDelegateSingleton.bleEngine supportBLE] && KKAppDelegateSingleton.currentConnectedPeripheral != nil)
    {
        [self performSelector:@selector(doneLoadingTableViewData) withObject:nil afterDelay:3.0];
        [KKAppDelegateSingleton getVehicleRealData:YES];
    }
    else
    {
        [self performSelector:@selector(doneLoadingTableViewData) withObject:nil afterDelay:0.5];
        [KKCustomAlertView showAlertViewWithMessage:@"您的手机未连接OBD，不能更新车况信息"];
        
    }
	
}

- (BOOL)egoRefreshTableHeaderDataSourceIsLoading:(EGORefreshTableHeaderView*)view{
	
	return _reloading; // should return if data source model is reloading
	
}

- (NSDate*)egoRefreshTableHeaderDataSourceLastUpdated:(EGORefreshTableHeaderView*)view{
	
	return [NSDate date]; // should return date data source was last changed
	
}


- (NSNumber *)vehicleFaultDictResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        NSLog(@"vehicleFaultDict error is %@",error.description);
//        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    KKModelVehicleFaultDictRsp *faultDictRsp = (KKModelVehicleFaultDictRsp *)rsp;
    KKTBDictFault *faultDict = [[KKTBDictFault alloc] initWithDB:[KKDB sharedDB]];
    [faultDict createTableWithVehicleModel:KKAppDelegateSingleton.currentVehicle.vehicleModelId dictDetail:faultDictRsp];
    [faultDict release];
    
    [self getDtcMessages];
    
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
    _mainTableView = nil;
    _refreshHeaderView=nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)dealloc
{
    [_dataArray release];
    _dataArray = nil;
    _mainTableView = nil;
    _refreshHeaderView=nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
    [super dealloc];
}

@end