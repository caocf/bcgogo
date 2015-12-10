//
//  TGMainViewController.m
//  TGOBD
//
//  Created by Jiahai on 14-2-28.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGMainViewController.h"
#import "TGHTTPRequestEngine.h"
#import "TGLoginViewController.h"
#import "TGAppDelegate.h"
#import "TGDataSingleton.h"
#import "UIColor+FromHex.h"

#import "TGMyDeviceViewController.h"
#import "TGOrderListViewController.h"
#import "TGChangePasswordViewController.h"
#import "TGVehicleManageViewController.h"
#import "TGShopInfoViewController.h"
#import "SIAlertView.h"
#import "TGButton.h"

typedef enum {
    btn_myDevice = 9999,
    btn_changePassword,
    btn_vehicleManage,
    btn_myOrders,
    btn_myShop,
    btn_checkUpdate,
}buttonTag;

@interface TGMainViewController ()
@property (nonatomic, strong) UIImageView *bgImageView;

@end

@implementation TGMainViewController

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
    
    [self initComponents];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void)dealloc
{
    NSLog(@"TGMainViewController dealloc!");
}

- (void)viewWillAppear:(BOOL)animated
{
    //取消导航栏右上角的小红点提示
    NSDictionary *dict1 = @{SHOW_TIPIMG: [NSNumber numberWithBool:NO],
                            TIPIMG_NAME: @"icon_red_remind.png"};
    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_updateRightBarItemTip object:dict1];
}

#pragma mark - Custom Methods

- (void)initComponents
{
    CGFloat originY = [self getViewLayoutStartOriginY];
    CGFloat height = [self getViewHeight];
    
    _bgImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, originY, 260, height)];
    _bgImageView.image = [UIImage imageNamed:@"bg_menu_nav.png"];
    _bgImageView.userInteractionEnabled = YES;
    
    originY += 30;
    
    UIImageView *userLogo = [[UIImageView alloc] initWithFrame:CGRectMake(102.75, originY, 60, 60)];
    userLogo.image = [UIImage imageNamed:@"icon_user_logo.png"];
    
    originY += 70;
    
    UILabel *userMobile = [[UILabel alloc] initWithFrame:CGRectMake(30, originY, 200, 30)];
    userMobile.textColor = [UIColor colorWithHex:0x1dbaf2];
    userMobile.text = [NSString stringWithFormat:@"手机号:%@", [[[TGDataSingleton sharedInstance] userInfo] mobile]];
    userMobile.textAlignment = NSTextAlignmentCenter;
    userMobile.backgroundColor = [UIColor clearColor];
    
    originY += 40;
    //各个响应Button
    [self createButtonWithFrame:CGRectMake(25, originY, 100, 70) title:@"我的设备" imageName:@"icon_my_device.png" btnTag:btn_myDevice notificationName:@"#MY_DEVICE#"];
    [self createButtonWithFrame:CGRectMake(135, originY, 100, 70) title:@"修改密码" imageName:@"icon_change_password.png" btnTag:btn_changePassword notificationName:@"#CHANGE_PASSWORD#"];
    
    originY += 90;
    
    [self createButtonWithFrame:CGRectMake(25, originY, 100, 70) title:@"车辆管理" imageName:@"icon_vehicle_manage.png" btnTag:btn_vehicleManage notificationName:@"#VEHICEL_MANAGE#"];
    [self createButtonWithFrame:CGRectMake(135, originY, 100, 70) title:@"我的账单" imageName:@"icon_my_orders.png" btnTag:btn_myOrders notificationName:@"#MY_ORDERS#"];
    
    originY += 90;
    
    [self createButtonWithFrame:CGRectMake(25, originY, 100, 70) title:@"我的4S店" imageName:@"icon_my_4s.png" btnTag:btn_myShop notificationName:@"#MY_SHOP#"];
    [self createButtonWithFrame:CGRectMake(135, originY, 100, 70) title:@"检查更新" imageName:@"icon_update.png" btnTag:btn_checkUpdate notificationName:NOTIFICATION_updateVersionCheckTip];
    
    originY += 90;
    
    UIImage *img = [[UIImage imageNamed:@"btn_blue.png"] resizableImageWithCapInsets:UIEdgeInsetsMake(10, 80, 10, 80) resizingMode:UIImageResizingModeStretch];
    UIButton *logout = [[UIButton alloc] initWithFrame:CGRectMake(30, originY,img.size.width *1.3 , img.size.height *1.3)];
    [logout setTitle:@"退出登录" forState:UIControlStateNormal];
    [logout setBackgroundImage:[UIImage imageNamed:@"btn_blue.png"] forState:UIControlStateNormal];
    [logout addTarget:self action:@selector(logout) forControlEvents:UIControlEventTouchUpInside];
    
    [_bgImageView addSubview:logout];
    [_bgImageView addSubview:userLogo];
    [_bgImageView addSubview:userMobile];
    [self.view addSubview:_bgImageView];
}

- (void)createButtonWithFrame:(CGRect)frame title:(NSString *)title imageName:(NSString *)imageName btnTag:(buttonTag)btnTag notificationName:(NSString *)notificationName
{
    
    TGButton *btn = [[TGButton alloc] initWithFrame:frame tipsFrame:CGRectMake(70, 15, 15, 15) notificationName:notificationName];
    
    [btn setTitle:title forState:UIControlStateNormal];
    [btn setBackgroundImage:[UIImage imageNamed:@"icon_nav_rightBtn_active.png"] forState:UIControlStateHighlighted];
    [btn addTarget:self action:@selector(buttonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [btn setTitleEdgeInsets:UIEdgeInsetsMake(60, 10, 20, 10)];
    [btn setTitleColor:[UIColor colorWithHex:0x1dbaf2] forState:UIControlStateNormal];
    [btn setExclusiveTouch:YES];
    btn.tag = btnTag;
    
    UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake(35, 10, 30, 30)];
    imageView.image = [UIImage imageNamed:imageName];
    imageView.userInteractionEnabled = NO;
    
    [btn addSubview:imageView];
    
    [_bgImageView addSubview:btn];
}

- (void)checkNewVersion
{
    TGModelLoginInfo *loginInfo = [[TGModelLoginInfo alloc] init];
    
    [TGProgressHUD show];
    
    [[TGHTTPRequestEngine sharedInstance] checkNewVersion:loginInfo.platform appVersion:loginInfo.appVersion platformVersion:loginInfo.platformVersion mobileModel:loginInfo.mobileModel viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        
        if ([self httpResponseCorrect:responseObject]) {
            TGModelCheckNewVersionRsp *rsp = (TGModelCheckNewVersionRsp *)responseObject;
        
            if ([rsp.action isEqualToString:@"force"]) {
                [TGAlertView showAlertViewWithTitle:nil message:rsp.description buttonTitle:@"立刻升级" handler:^(SIAlertView *alertView) {
                    alertView.dismissAfterClick = NO;
                    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:rsp.url]];
                }];
            }
            else if ([rsp.action isEqualToString:@"alert"])
            {
                [TGAlertView showAlertViewWithTitle:nil message:rsp.description leftBtnTitle:@"稍后升级" rightBtnTitle:@"立刻升级" leftHandler:^(SIAlertView *alertView) {
                    //
                } rightHandler:^(SIAlertView *alertView) {
                    [self updateWithUrl:rsp.url];
                }];
            }
            else if ([rsp.action isEqualToString:@"normal"])
            {
                [TGAlertView showAlertViewWithTitle:nil message:@"当前已是最新版本！"];
            }
        }
        
    } failure:self.faultBlock];
}

- (void)updateWithUrl:(NSString *)url
{
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:url]];
}

#pragma mark - button selector
- (void)buttonClicked:(id)sender
{
    UIButton *btn = (UIButton *)sender;
    switch (btn.tag) {
        case btn_myDevice:
            [TGAppDelegateSingleton.rootViewController pushViewController:[[TGMyDeviceViewController alloc] init] animated:YES];
            break;
        case btn_changePassword:
        {
            TGChangePasswordViewController *Vc = [[TGChangePasswordViewController alloc] initWithNibName:@"TGChangePasswordViewController" bundle:nil];
            [TGAppDelegateSingleton.rootViewController pushViewController:Vc animated:YES];
            break;
        }
        case btn_vehicleManage:
            [TGAppDelegateSingleton.rootViewController pushViewController:[[TGVehicleManageViewController alloc] init] animated:YES];
            break;
        case btn_myOrders:
            [TGAppDelegateSingleton.rootViewController pushViewController:[[TGOrderListViewController alloc] init] animated:YES];
            break;
        case btn_myShop:
            [TGAppDelegateSingleton.rootViewController pushViewController:[[TGShopInfoViewController alloc] init] animated:YES];
            break;
        case btn_checkUpdate:
            [self checkNewVersion];
            break;
            
        default:
            break;
    }
}

- (void)logout
{
    [[TGHTTPRequestEngine sharedInstance] logout:[[[TGDataSingleton sharedInstance] userInfo] userNo] viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        [TGAppDelegateSingleton logout];
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [TGAppDelegateSingleton logout];
    }];
}

@end
