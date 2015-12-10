//
//  TGMessageViewController.m
//  TGOBD
//
//  Created by James Yu on 14-3-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGMessageViewController.h"
#import "TGMessageDBManager.h"
#import "TGMacro.h"
#import "TGMessageTableViewCell.h"
#import "TGOrderListViewController.h"
#import "TGDTCManagerViewController.h"
#import "TGOrderOnlineViewController.h"
#import "TGOrderDetailViewController.h"
#import "TGAppDelegate.h"
#import "TGPublicNoticeViewController.h"
#import "TGTrafficViolationViewController.h"

#define PAGE_NUM 25
#define TG_USER_NO [[[TGDataSingleton sharedInstance] userInfo] userNo]

@interface TGMessageViewController ()

@property (nonatomic, assign) BOOL isLoading;
@property (nonatomic, assign) BOOL isLastMessage;

@end

@implementation TGMessageViewController

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
    
    [self setnavigationBar];
    [self initComponents];
    [self initVariable];

}

- (void)viewDidAppear:(BOOL)animated
{
    //更新数据库，并发送重新设置未读消息的数目
    [self setMessageRead];
    
    [self postNotification];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(getNewMessage)
                                                 name:NOTIFICATION_GetNewMessage
                                               object:nil];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NOTIFICATION_GetNewMessage object:nil];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Custom Methods

- (void)initComponents
{
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar];
    CGFloat height = [self getViewHeightWithNavigationBar];
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, originY, screenWidth, height) style:UITableViewStylePlain];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.backgroundView = nil;
    _tableView.scrollEnabled = YES;
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    
    [self.view addSubview:_tableView];
}

- (void)initVariable
{
    _isLoading = YES;
    _dateSource = nil;
    _dateSource = [[NSMutableArray alloc] init];
    
    NSMutableArray *tmp = [[TGMessageDBManager sharedMessageDBManager] getMessageWithUserNo:TG_USER_NO start:0 num:PAGE_NUM];
    
    [_dateSource addObjectsFromArray:tmp];
    
    if ([tmp count] < PAGE_NUM) {
        _isLastMessage = YES;
    }
    else
    {
        _isLastMessage = NO;
    }
    
    _isLoading = NO;
    
    [_tableView reloadData];
}

- (void)setnavigationBar
{
    [self setNavigationTitle:@"我的消息"];
    [self setNavigationItemRightBarButtonItem];
}

- (void)setNavigationItemRightBarButtonItem
{
    self.navigationItem.rightBarButtonItem = nil;
    
    if (_tableView.editing == YES) {
        self.navigationItem.rightBarButtonItem = [TGViewUtils createNavigationBarButtonItemWithTitle:@"完成"
                                                                                             bgImage:nil
                                                                                              target:self
                                                                                              action:@selector(deleteMessageDone)];
    }
    else
    {
        self.navigationItem.rightBarButtonItem = [TGViewUtils createNavigationBarButtonItemWithTitle:@"删除"
                                                                                             bgImage:nil
                                                                                              target:self
                                                                                              action:@selector(deleteMessage)];
    }
}

- (void)deleteMessage
{
    _tableView.editing = YES;
    [self setNavigationItemRightBarButtonItem];
}

- (void)deleteMessageDone
{
    _tableView.editing = NO;
    [self setNavigationItemRightBarButtonItem];
}

- (void)setMessageRead
{
    [[TGMessageDBManager sharedMessageDBManager] setMessageRead:TG_USER_NO];
}

- (void)getNewMessage
{
    if (_isLoading) {
        [self performSelector:@selector(getNewMessage) withObject:nil afterDelay:1];
    }
    [self initVariable];

    [self setMessageRead];
    
    [self postNotification];
    
    [TGAlertView showAlertViewWithTitle:nil message:@"您获得了新的消息"];
}

- (void)loadMoreMessage
{
    [TGProgressHUD showWithStatus:@"加载更多..."];
    
     _isLoading = YES;
    
    NSMutableArray *tmp = [[TGMessageDBManager sharedMessageDBManager] getMessageWithUserNo:TG_USER_NO start:[_dateSource count] num:PAGE_NUM];
    
    if ([tmp count] > 0)
    {
        NSMutableArray *indexPaths = [[NSMutableArray alloc] init];
        
        for (int i = 0; i < [tmp count]; i ++) {
            NSIndexPath *indexPath = [NSIndexPath indexPathForItem:[_dateSource count] inSection:0];
            [indexPaths addObject:indexPath];
            
            [_dateSource addObject:[tmp objectAtIndex:i]];
        }
        //刷新数据
        [_tableView beginUpdates];
        [_tableView insertRowsAtIndexPaths:indexPaths withRowAnimation:UITableViewRowAnimationFade];
        [_tableView endUpdates];
    }
    else
    {
        [TGAlertView showAlertViewWithTitle:nil message:@"没有更多消息了"];
    }
    
    if ([tmp count] < PAGE_NUM) {
        _isLastMessage = YES;
    }
    else
    {
        _isLastMessage = NO;
    }
    
    _isLoading = NO;
    
    [TGProgressHUD dismiss];
}

#pragma mark - Message Notification
- (void)postNotification
{
    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_SetUnreadMessageNum object:nil];
}

#pragma maark - UIScrollView delegate
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    CGPoint offset = scrollView.contentOffset;
    CGSize size = scrollView.frame.size;
    CGSize contentSize = scrollView.contentSize;
    
    float yMargin = offset.y + size.height - contentSize.height;
  
    if (yMargin > -1 && !_isLoading && !_isLastMessage) {
        
        [self loadMoreMessage];
    }
}

#pragma mark - UITableview delegate

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_dateSource count];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    TGModelMessage *message = [_dateSource objectAtIndex:indexPath.row];
    
    return [TGMessageTableViewCell getCellHeightWithContent:message.content];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identify = @"cellIdentify";
    
    TGMessageTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identify];
    
    if (cell == nil) {
        cell = [[TGMessageTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identify];
    }
    
    [cell setCellContent:[_dateSource objectAtIndex:indexPath.row]];
    
    return cell;
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return UITableViewCellEditingStyleDelete;
}

- (NSString *)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return @"删除";
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        [_tableView beginUpdates];
        
        TGModelMessage *msg = (TGModelMessage *)[_dateSource objectAtIndex:indexPath.row];
        [[TGMessageDBManager sharedMessageDBManager] deleteMessageWithId:msg.id userNo:TG_USER_NO];
        
        [_dateSource removeObjectAtIndex:indexPath.row];
        [_tableView deleteRowsAtIndexPaths:[NSArray arrayWithObjects:indexPath, nil] withRowAnimation:UITableViewRowAnimationNone];
        [_tableView endUpdates];
        
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    TGModelMessage *message = [_dateSource objectAtIndex:indexPath.row];
    
    NSString *type = message.type;
    //跳账单详情
    if ([type isEqualToString:SHOP_FINISH_APPOINT])
    {
        TGOrderDetailViewController *Vc = [[TGOrderDetailViewController alloc] init];
        NSArray *strArray = [message.params componentsSeparatedByString:@","];
        Vc.orderId = [[strArray objectAtIndex:2] longLongValue];
        [TGAppDelegateSingleton.rootViewController pushViewController:Vc animated:YES];
    }
    //跳列表
    else if ([type isEqualToString:SHOP_CHANGE_APPOINT]
             || [type isEqualToString:SHOP_ACCEPT_APPOINT])
    {
        [TGAppDelegateSingleton.rootViewController pushViewController:[[TGOrderListViewController alloc] init] animated:YES];
    }
    //跳预约
    else if ([type isEqualToString:OVERDUE_APPOINT_TO_APP]
             || [type isEqualToString:APP_VEHICLE_MAINTAIN_MILEAGE]
             || [type isEqualToString:APP_VEHICLE_MAINTAIN_TIME])
    {
        [TGAppDelegateSingleton.rootViewController pushViewController:[[TGOrderOnlineViewController alloc] init] animated:YES];
    }
    //跳故障列表
    else if ([type isEqualToString:VEHICLE_FAULT_2_APP])
    {
        [TGAppDelegateSingleton.rootViewController pushViewController:[[TGDTCManagerViewController alloc] init] animated:YES];
    }
    //跳公告列表
    else if ([type isEqualToString:SHOP_ADVERT_TO_APP])
    {
        [TGAppDelegateSingleton.rootViewController pushViewController:[[TGPublicNoticeViewController alloc] init] animated:YES];
    }
    else if ([type isEqualToString:VIOLATE_REGULATION_RECORD_2_APP])
    {
        [TGAppDelegateSingleton.rootViewController pushViewController:[[TGTrafficViolationViewController alloc] init] animated:YES];
    }
    
}

@end
