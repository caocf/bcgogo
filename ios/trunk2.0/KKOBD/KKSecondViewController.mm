//
//  KKSecondViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-5.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKSecondViewController.h"
#import "KKReservationServiceViewController.h"
#import "KKViewUtils.h"
#import "BMapKit.h"
#import "KKApplicationDefine.h"
#import "UIViewController+extend.h"
#import "KKMessagePromptCell.h"
#import "KKReviewViewController.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKPreference.h"
#import "MBProgressHUD.h"
#import "KKDB.h"
#import "KKTBMessage.h"
#import "KKShopQueryViewController.h"
#import "KKServiceDetailViewController.h"
#import "KKMessagePollingManager.h"
#import "KKAppDelegate.h"
#import "KKShopQueryViewController.h"

@interface KKSecondViewController ()

@end

@implementation KKSecondViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateDataSource) name:KKMessagePollingNotification object:nil];
    [self setVcEdgesForExtendedLayout];
    [self initVariables];
    [self initComponents];
    [self updateDataSource];
    
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [KKAppDelegateSingleton setNewMsgBadgeValue:0];
    [self updateDataSource];
//    [KKAppDelegateSingleton vehicleDTCReport:[NSArray arrayWithObject:@"C0032"]];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    _mainTableView.editing = NO;
    _clearEnable = NO;
}

#pragma mark -
#pragma mark custom methods

- (void) initVariables
{
    _dataArray = [[NSMutableArray alloc] init];
}

- (void) initComponents
{
    [self setNavgationBar];
    [self creatTableView];
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"消息中心";
    
    self.navigationItem.rightBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_clearMsg.png"] bgImage:nil target:self action:@selector(clearButtonClicked)];
    
}

- (void)creatTableView
{
    _mainTableView = [[UITableView alloc] initWithFrame:CGRectMake(0,0, 320, currentScreenHeight - 44 - 49- [self getOrignY]) style:UITableViewStylePlain];
    _mainTableView.delegate = self;
    _mainTableView.dataSource = self;
    _mainTableView.backgroundColor = [UIColor clearColor];
    _mainTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_mainTableView];
    [_mainTableView release];
}

- (void)updateDataSource
{
    if ([_dataArray count] > 0)
        [_dataArray removeAllObjects];
    
    KKTBMessage *tbMessage = [[KKTBMessage alloc] initWithDB:[KKDB sharedDB]];
    [_dataArray addObjectsFromArray:[tbMessage getPollMessagesWithUserNo:[KKProtocolEngine sharedPtlEngine].userName]];
    [tbMessage release];
    
    [_mainTableView reloadData];
}

#pragma mark -
#pragma mark Events

- (void)clearButtonClicked
{
    _clearEnable = !_clearEnable;
    if (_clearEnable)
        _mainTableView.editing = YES;
    else
        _mainTableView.editing = NO;
}

#pragma mark -
#pragma mark UITableViewDelegate,UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_dataArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"cell_infoPrompt";
    KKMessagePromptCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil)
    {
        cell = [[[KKMessagePromptCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
        cell.delegate = self;
    }
    KKPollMessage *message = [_dataArray objectAtIndex:indexPath.row];
    [cell setContent:message];
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    KKPollMessage *message = [_dataArray objectAtIndex:indexPath.row];
    return [KKMessagePromptCell calculateCellHeightWith:message];
}

- (NSString *)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return @"删除";
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if ([_dataArray count] > 0 && indexPath.row < [_dataArray count])
    {
        KKPollMessage *message = [_dataArray objectAtIndex:indexPath.row];
        KKTBMessage *tbMsg = [[KKTBMessage alloc] initWithDB:[KKDB sharedDB]];
        [tbMsg deleteOneMessages:message.id andUserNo:[KKProtocolEngine sharedPtlEngine].userName];
        [tbMsg release];
        
        [_dataArray removeObjectAtIndex:indexPath.row];
        [_mainTableView reloadData];
    }
}
#pragma mark -
#pragma mark KKMessagePromptCellDelegate

- (void)KKMessagePromptCellButtonClicked:(KKPollMessage *)message
{
    NSArray *params = [KKHelper getArray:message.params BySeparateString:@","];
    
    if ([message.actionType isEqualToString:@"SEARCH_SHOP"])
    {
        KKShopQueryViewController *Vc = [[KKShopQueryViewController alloc] initWithNibName:@"KKShopQueryViewController" bundle:nil];
        NSArray *objects =[KKServiceScopeFirstCategoryDict allValues];
        BOOL have = NO;
        NSInteger index;
        
        for (int t=0;t < [objects count];t++)
        {
            NSString *string = [objects objectAtIndex:t];

            if ([string isEqualToString:[params objectAtIndex:0]])
            {
                index = t;
                have = YES;
                break;
            }
        }
        NSString *keyString = @"服务范围";
        if (have)
            keyString = [[KKServiceScopeFirstCategoryDict allKeys] objectAtIndex:index];
        Vc.serviceTypeKey = keyString;
        [self.navigationController pushViewController:Vc animated:YES];
        [Vc release];
        
        
    }
    else if ([message.actionType isEqualToString:@"SERVICE_DETAIL"])
    {
        KKServiceDetailViewController *Vc = [[KKServiceDetailViewController alloc] initWithNibName:@"KKServiceDetailViewController" bundle:nil];
        Vc.orderId = [params objectAtIndex:0];
        [self.navigationController pushViewController:Vc animated:YES];
        [Vc release];
    }
    else if ([message.actionType isEqualToString:@"CANCEL_ORDER"])
    {
        [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        [[KKProtocolEngine sharedPtlEngine] serviceCancel:[params objectAtIndex:0] delegate:self];
    }
    else if ([message.actionType isEqualToString:@"ORDER_DETAIL"])
    {
        KKServiceDetailViewController *Vc = [[KKServiceDetailViewController alloc] initWithNibName:@"KKServiceDetailViewController" bundle:nil];
        Vc.orderId = [params objectAtIndex:0];
        [self.navigationController pushViewController:Vc animated:YES];
        [Vc release];
    }
    else if ([message.actionType isEqualToString:@"COMMENT_SHOP"])  //评价
    {
        KKReviewViewController *Vc = [[KKReviewViewController alloc] initWithNibName:@"KKReviewViewController" bundle:nil];
        if ([params count] == 3)
            Vc.orderId = [params objectAtIndex:2];
        Vc.messageId = message.id;
        [self.navigationController pushViewController:Vc animated:YES];
        [Vc release];
    }

}

#pragma mark -
#pragma mark KKProtocolEngineDelegate

- (NSNumber *)serviceDeleteResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}    
    return KKNumberResultEnd;
}

#pragma mark -
#pragma mark Handle Memory

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:KKMessagePollingNotification object:nil];
    
    _mainTableView = nil;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:KKMessagePollingNotification object:nil];
    
    _mainTableView = nil;
    
    [_dataArray release];
    _dataArray = nil;
    [super dealloc];
}

@end
