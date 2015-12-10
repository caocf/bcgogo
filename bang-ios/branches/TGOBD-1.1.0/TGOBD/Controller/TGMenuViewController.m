//
//  TGMenuViewController.m
//  TGOBD
//
//  Created by Jiahai on 14-3-5.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGMenuViewController.h"
#import "TGAppDelegate.h"
#import "TGNavigationController.h"
#import "UIViewController+MMDrawerController.h"
#import "TGDriveRecordViewController.h"
#import "TGOilStationMapViewController.h"
#import "TGMainViewController.h"
#import "TGDTCManagerViewController.h"
#import "TGOrderOnlineViewController.h"
#import "TGMessageViewController.h"
#import "TGPublicNoticeViewController.h"
#import "TGTrafficViolationViewController.h"
#import "TGMessageDBManager.h"
#import "UIColor+FromHex.h"
#import "TGSetVilolationQueryConditionViewController.h"

#define CELL_HEIGHT 66
#define UNREAD_MESSAGE_LABEL_TAG 9000

@interface TGMenuViewController ()
@property (nonatomic, strong) NSDictionary *paneViewControllerTitles;
@property (nonatomic, strong) NSDictionary *paneViewControllerClasses;
@property (nonatomic, strong) NSDictionary *paneViewControllerIcons;
@property (nonatomic, strong) UIBarButtonItem *paneStateBarButtonItem;
@property (nonatomic, strong) UIBarButtonItem *paneRevealLeftBarButtonItem;
@property (nonatomic, strong) UIBarButtonItem *paneRevealRightBarButtonItem;
@property (nonatomic, strong) UITableView *tableView;
@end

@implementation TGMenuViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        [self initialize];
    }
    return self;
}

- (void)initialize
{
    self.paneViewControllerType = NSUIntegerMax;
    self.paneViewControllerTitles = @{
                                      @(TGPaneViewControllerTypeDriveRecord)        : @"行车轨迹",
                                      @(TGPaneViewControllerTypeOnlineOrder)        : @"预约服务",
                                      @(TGPaneViewControllerTypeMessage)            : @"消息中心",
                                      @(TGPaneViewControllerTypeDTCManager)         : @"故障查询",
                                     // @(TGPaneViewControllerTypePublicNotice)       : @"4S店公告",
                                      @(TGPaneViewControllerTypeTrafficViolation)   : @"违章查询"
                                      };

    self.paneViewControllerClasses = @{
                                       @(TGPaneViewControllerTypeDriveRecord)       : [TGDriveRecordViewController class],
                                       @(TGPaneViewControllerTypeOnlineOrder)       : [TGOrderOnlineViewController class],
                                       @(TGPaneViewControllerTypeMessage)           : [TGMessageViewController class],
                                       @(TGPaneViewControllerTypeDTCManager)        : [TGDTCManagerViewController class],
                                       //@(TGPaneViewControllerTypePublicNotice)      : [TGPublicNoticeViewController class],
                                       @(TGPaneViewControllerTypeTrafficViolation)  : [TGTrafficViolationViewController class]
                                       };
    self.paneViewControllerIcons = @{
                                     @(TGPaneViewControllerTypeDriveRecord)         : [UIImage imageNamed:@"icon_guiji.png"],
                                     @(TGPaneViewControllerTypeOnlineOrder)         : [UIImage imageNamed:@"icon_clock.png"],
                                     @(TGPaneViewControllerTypeMessage)             : [UIImage imageNamed:@"icon_message.png"],
                                     @(TGPaneViewControllerTypeDTCManager)          : [UIImage imageNamed:@"icon_search.png"],
                                   //  @(TGPaneViewControllerTypePublicNotice)        : [UIImage imageNamed:@"icon_notice.png"],
                                     @(TGPaneViewControllerTypeTrafficViolation)    : [UIImage imageNamed:@"icon_traffic_violation.png"],
                                     };
    
    self.paneRevealLeftBarButtonItem = [TGViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_navibar_menu.png"] bgImage:nil target:self action:@selector(leftDrawerButtonPress:) tipsFrame:CGRectMake(10, 3, 7.5, 7.5) notificationName:NOTIFICATION_updateLeftBarItemTip];
    
    self.paneRevealRightBarButtonItem = [TGViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_navibar_userinfo.png"] bgImage:nil target:self action:@selector(rightDrawerButtonPress:) tipsFrame:CGRectMake(27, 2, 7.5, 7.5) notificationName:NOTIFICATION_updateRightBarItemTip];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    _selectedViewController = TGPaneViewControllerTypeDriveRecord;
    
    CGFloat originY = [self getViewLayoutStartOriginY];
    CGFloat height = [self getViewHeight];
    
    UIImageView *bgImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, originY, 260, height)];
    bgImageView.image = [UIImage imageNamed:@"bg_menu_nav.png"];
    [self.view addSubview:bgImageView];
    
    UIView *titleView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 260, [self getNavigationBarHeight])];
    titleView.backgroundColor = [UIColor clearColor];
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, titleView.bounds.size.height-44, titleView.bounds.size.width, 44)];
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.font = [UIFont systemFontOfSize:22];
    titleLabel.textAlignment = NSTextAlignmentCenter;
    titleLabel.textColor = [UIColor whiteColor];
    titleLabel.text = @"常用功能";
    [titleView addSubview:titleLabel];
    UIImageView *line = [[UIImageView alloc] initWithFrame:CGRectMake(0, titleView.bounds.size.height-1, titleView.bounds.size.width, 1)];
    line.image = [UIImage imageNamed:@"left_line.png"];
    [titleView addSubview:line];
    
    UIButton *helpBtn = [[UIButton alloc] initWithFrame:CGRectMake(30, 5, 152*1.3, 26.5*1.3)];
    [helpBtn setBackgroundImage:[UIImage imageNamed:@"btn_help.png"] forState:UIControlStateNormal];
    [helpBtn addTarget:self action:@selector(makeHelpCall) forControlEvents:UIControlEventTouchUpInside];
    [_tableView addSubview:helpBtn];
    
    UIView *footView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 260, 60)];
    [footView addSubview:helpBtn];
    
    _tableView = [[UITableView alloc] initWithFrame:bgImageView.bounds];
    _tableView.bounces = NO;
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _tableView.tableHeaderView = titleView;
    _tableView.tableFooterView = footView;
    [self.view addSubview:_tableView];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setUnreadMessageNum) name:NOTIFICATION_SetUnreadMessageNum object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(transitionToVilolationController) name:NOTIFICATION_UpdateTrafficViolation object:nil];

}

- (void)viewWillAppear:(BOOL)animated
{
    [self updateLeftBarItemRedRemindShow:NO];
    [self setUnreadMessageNum];
}

- (void)updateLeftBarItemRedRemindShow:(BOOL)show
{
    NSDictionary *dict = @{SHOW_TIPIMG: [NSNumber numberWithBool:show],
                            TIPIMG_NAME: @"icon_red_remind.png"};
    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_updateLeftBarItemTip object:dict];
}

- (void)transitionToViewController:(TGPaneViewControllerType)paneViewControllerType
{
    // Close pane if already displaying the pane view controller
    
    BOOL animateTransition = self.mm_drawerController != nil;
    
    Class paneViewControllerClass = self.paneViewControllerClasses[@(paneViewControllerType)];
    UIViewController *paneViewController = (UIViewController *)[paneViewControllerClass new];
    
    [paneViewController setNavigationTitle:self.paneViewControllerTitles[@(paneViewControllerType)]];
    
    paneViewController.navigationItem.leftBarButtonItem = self.paneRevealLeftBarButtonItem;
    
    if(paneViewControllerType == TGPaneViewControllerTypeDriveRecord)
    {
        paneViewController.navigationItem.rightBarButtonItem = self.paneRevealRightBarButtonItem;
        self.mm_drawerController.rightDrawerViewController = [[TGMainViewController alloc] init];
    }
    else
    {
        self.mm_drawerController.rightDrawerViewController = nil;
    }
    
    UINavigationController *paneNavigationViewController = [[TGNavigationController alloc] initWithRootViewController:paneViewController];
    [self.mm_drawerController setCenterViewController:paneNavigationViewController withCloseAnimation:animateTransition completion:nil];
    
    self.paneViewControllerType = paneViewControllerType;
}

- (void)leftDrawerButtonPress:(id)sender
{
    [self.mm_drawerController toggleDrawerSide:MMDrawerSideLeft animated:YES completion:nil];
}

- (void)rightDrawerButtonPress:(id)sender
{
    [self.mm_drawerController toggleDrawerSide:MMDrawerSideRight animated:YES completion:nil];
}

#pragma mark - UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.paneViewControllerTitles count];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return CELL_HEIGHT;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [[UITableViewCell alloc] init];

    UIImageView *iconImgView = [[UIImageView alloc] initWithFrame:CGRectMake(10, 21, 24, 24)];
    [cell addSubview:iconImgView];
    
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(56, 10, 180, 46)];
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.textColor = [UIColor whiteColor];
    titleLabel.font = [UIFont systemFontOfSize:18];
    [cell addSubview:titleLabel];
    
    UIImageView *line = [[UIImageView alloc] initWithFrame:CGRectMake(0, 65, cell.bounds.size.width, 1)];
    line.image = [UIImage imageNamed:@"left_line.png"];
    [cell addSubview:line];
    
    iconImgView.image = [self.paneViewControllerIcons objectForKey:@(indexPath.row)];
    
    titleLabel.text = [self.paneViewControllerTitles objectForKey:@(indexPath.row)];
    
    if(indexPath.row == TGPaneViewControllerTypeMessage)
    {
        UILabel *msgCountLabel = [[UILabel alloc] initWithFrame:CGRectMake(25, 15, 100, 10)];
        msgCountLabel.tag = UNREAD_MESSAGE_LABEL_TAG;
        msgCountLabel.backgroundColor = [UIColor redColor];
        msgCountLabel.textColor = [UIColor whiteColor];
        msgCountLabel.textAlignment = NSTextAlignmentCenter;
        msgCountLabel.font = [UIFont systemFontOfSize:12];
        NSInteger unreadNum = [[TGMessageDBManager sharedMessageDBManager] getUnreadMessageNumberWithUserNo:[[[TGDataSingleton sharedInstance] userInfo] userNo]];
        if (unreadNum != 0) {
            msgCountLabel.hidden = NO;
            msgCountLabel.text = [NSString stringWithFormat:@"%d",unreadNum];
        }
        else
        {
            msgCountLabel.hidden = YES;
        }
        msgCountLabel.layer.masksToBounds = YES;
        msgCountLabel.layer.cornerRadius = 4;
        [msgCountLabel sizeToFit];
        [cell addSubview:msgCountLabel];
        CGRect rect = msgCountLabel.frame;
        rect.size.width += 6;
        msgCountLabel.frame = rect;
    }
    
    cell.backgroundColor = [UIColor clearColor];
    cell.selectedBackgroundView = [[UIView alloc] initWithFrame:cell.bounds];
    cell.selectedBackgroundView.backgroundColor = [UIColor colorWithHex:0x092448];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    _selectedViewController = indexPath.row;
    
    switch (indexPath.row) {
        case TGPaneViewControllerTypeOnlineOrder:
        {
            [TGAppDelegateSingleton.rootViewController pushViewController:[self.paneViewControllerClasses[@(TGPaneViewControllerTypeOnlineOrder)] new] animated:YES];
        }
            break;
        case TGPaneViewControllerTypeTrafficViolation:
        {
            TGModelVehicleInfo *vehicleInfo = [[TGDataSingleton sharedInstance] vehicleInfo];
            
            if (vehicleInfo.juheCityCode.length == 0
                || vehicleInfo.engineNo.length == 0
                || vehicleInfo.vehicleVin.length == 0)
            {
                [TGAppDelegateSingleton.rootViewController pushViewController:[[TGSetVilolationQueryConditionViewController alloc] init] animated:YES];
            }
            else
            {
                [self transitionToViewController:indexPath.row];
            };
        }
            break;
        default:
        {
            [self transitionToViewController:indexPath.row];
        }
            break;
    }
}

- (void)transitionToVilolationController
{
    [self transitionToViewController:TGPaneViewControllerTypeTrafficViolation];
}

- (void)setUnreadMessageNum
{
    NSInteger unreadNum = [[TGMessageDBManager sharedMessageDBManager] getUnreadMessageNumberWithUserNo:[[[TGDataSingleton sharedInstance] userInfo] userNo]];
    
    UITableViewCell *cell = [_tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:TGPaneViewControllerTypeMessage inSection:0]];
    UILabel *unReadLbl = (UILabel *)[cell viewWithTag:UNREAD_MESSAGE_LABEL_TAG];
    
    if (unreadNum != 0) {
        [self updateLeftBarItemRedRemindShow:YES];
        unReadLbl.hidden = NO;
        [unReadLbl setFrame:CGRectMake(25, 15, 100, 10)];
        unReadLbl.text = [NSString stringWithFormat:@"%d", unreadNum];
        [unReadLbl sizeToFit];
        CGRect rect = unReadLbl.frame;
        rect.size.width += 6;
        unReadLbl.frame = rect;
    }
    else
    {
        [self updateLeftBarItemRedRemindShow:NO];
        unReadLbl.text = nil;
        unReadLbl.hidden = YES;
    }

}

- (void)makeHelpCall
{
    if([TGDataSingleton sharedInstance].shopInfo.accidentMobile.length > 0)
    {
        [TGHelper makePhone:[TGDataSingleton sharedInstance].shopInfo.accidentMobile];
    }
    else if([TGDataSingleton sharedInstance].shopInfo.landLine.length > 0)
    {
        [TGHelper makePhone:[TGDataSingleton sharedInstance].shopInfo.landLine];
    }
    else if([TGDataSingleton sharedInstance].shopInfo.mobile.length > 0)
    {
        [TGHelper makePhone:[TGDataSingleton sharedInstance].shopInfo.mobile];
    }
    else
    {
        [TGAlertView showAlertViewWithTitle:nil message:@"暂无救援电话"];
    }

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
