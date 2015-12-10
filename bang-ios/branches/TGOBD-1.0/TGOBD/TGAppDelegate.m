//
//  TGAppDelegate.m
//  TGOBD
//
//  Created by Jiahai on 14-2-28.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGAppDelegate.h"
#import "TGDrawerViewController.h"
#import "MMDrawerVisualState.h"
#import "UIViewController+MMDrawerController.h"
#import "TGMainViewController.h"
#import "TGMenuViewController.h"
#import "TGBaseViewController.h"
#import "AKLocationManager.h"
#import "BMKMapManager.h"
#import "BMKGeometry.h"
#import "TGDataSingleton.h"
#import "TGLoginViewController.h"
#import "TGMessageRollingHandler.h"
#import "SIAlertView.h"
#import "TGGetVehicleRollingHandler.h"

@interface TGAppDelegate ()
@property (nonatomic, strong) MMDrawerController        *drawerController;
@end

static BMKMapManager        *g_mapManager = nil;

@implementation TGAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    g_mapManager = [[BMKMapManager alloc] init];
    
    BOOL ret = [g_mapManager start:@"rtwjc8WqVGQWNFMv0kUjtk3Y"  generalDelegate:nil];
    if (!ret) {
        NSLog(@"manager start failed!");
    }
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    // Override point for customization after application launch.
    self.window.backgroundColor = [UIColor whiteColor];
    [self.window makeKeyAndVisible];
    
    [self showLoginView];
    
    [self startLocating];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(logout) name:NOTIFICATION_LoginOut object:nil];
    
    [self setBMPUnBackupByiCloud];
    
    return YES;
}
//设置百度地图不允许被iCloud备份
- (void)setBMPUnBackupByiCloud
{
    NSString *bmpDirectory = [TGHelper getPathWithinDocumentDir:@"cfg"];
    
    NSFileManager *fileManager = [NSFileManager defaultManager];
    
    if ([fileManager fileExistsAtPath:bmpDirectory]) {
        [TGHelper addSkipBackupAttributeToItemAtURL:[NSURL fileURLWithPath:bmpDirectory]];
    }
}

- (void)showLoginView
{
    TGNavigationController *naviVc = [[TGNavigationController alloc] initWithRootViewController:[[TGLoginViewController alloc] initWithNibName:@"TGLoginViewController" bundle:nil]];
    self.rootViewController = naviVc;
    self.window.rootViewController = self.rootViewController;
}

- (void)showRootView
{
    TGMenuViewController *leftSideDrawerViewController = [[TGMenuViewController alloc] init];
    
    TGBaseViewController *centerViewController = [[TGBaseViewController alloc] init];
    
    TGNavigationController *rightSideDrawerViewController = [[TGNavigationController alloc] initWithRootViewController:[[TGMainViewController alloc] init]];
    
    TGNavigationController * navigationController = [[TGNavigationController alloc] initWithRootViewController:centerViewController];
    
    self.drawerController = [[TGDrawerViewController alloc]
                             initWithCenterViewController:navigationController
                             leftDrawerViewController:leftSideDrawerViewController
                             rightDrawerViewController:rightSideDrawerViewController];
    
    [self.drawerController setShouldStretchDrawer:NO];
    [self.drawerController setMaximumLeftDrawerWidth:260];
    [self.drawerController setMaximumRightDrawerWidth:260.0];
    [self.drawerController setOpenDrawerGestureModeMask:MMOpenDrawerGestureModePanningNavigationBar];
    [self.drawerController setCloseDrawerGestureModeMask:MMCloseDrawerGestureModeAll];
    
    [self.drawerController setDrawerVisualStateBlock:[MMDrawerVisualState slideVisualStateBlock]];
    
    self.rootViewController = [[TGNavigationController alloc] initWithRootViewController:self.drawerController];
    self.rootViewController.navigationBar.hidden = YES;
    
    self.window.rootViewController = self.rootViewController;
    
    //设置首页，注意在Menu界面中配置右菜单出现的条件
    [((TGMenuViewController *)self.drawerController.leftDrawerViewController) transitionToViewController:TGPaneViewControllerTypeDriveRecord];
    //检测更新
    [self checkNewVersion];
    
    [TGGetVehicleRollingHandler startRolling]; 
}

- (void)logout
{
    [TGMessageRollingHandler stopRolling];
    [TGGetVehicleRollingHandler stopRolling];
    [[TGHTTPRequestEngine sharedInstance] cancleAllRequests];
    
    [self showLoginView];
}

- (void)startLocating
{
    [AKLocationManager startLocatingWithUpdateBlock:^(CLLocation *location) {
        NSLog(@"%@",location);
        CLLocationCoordinate2D coo = BMKCoorDictionaryDecode(BMKBaiduCoorForWgs84(location.coordinate));
        
        [TGDataSingleton sharedInstance].currentCoordinate2D = coo;
        
    } failedBlock:^(NSError *error) {
        if (error.code == AKLocationManagerErrorCannotLocate)
        {
            [AKLocationManager stopLocating];
        }
    }];
}

- (void)stopLocating
{
    [AKLocationManager stopLocating];
}

- (void)checkNewVersion
{
    TGModelLoginInfo *loginInfo = [[TGModelLoginInfo alloc] init];
    
    [[TGHTTPRequestEngine sharedInstance] checkNewVersion:loginInfo.platform appVersion:loginInfo.appVersion platformVersion:loginInfo.platformVersion mobileModel:loginInfo.mobileModel viewControllerIdentifier:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if ([responseObject isKindOfClass:[TGModelCheckNewVersionRsp class]]) {
            TGModelCheckNewVersionRsp *rsp = (TGModelCheckNewVersionRsp *)responseObject;
            if (rsp.header.status == rspStatus_Succeed) {
                if ([rsp.action isEqualToString:@"force"]) {
                    SIAlertView *alert = [[SIAlertView alloc] initWithTitle:@"检测到新版本" andMessage:rsp.description];
                    [alert addButtonWithTitle:@"升级" type:SIAlertViewButtonTypeDefault handler:^(SIAlertView *alertView) {
                        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:rsp.url]];
                    }];
                    alert.dismissAfterClick = NO;
                    [alert show];
                }
                else if ([rsp.action isEqualToString:@"alert"])
                {
                    NSDictionary *dict1 = @{SHOW_TIPIMG: [NSNumber numberWithBool:YES],
                                            TIPIMG_NAME: @"icon_red_remind.png"};
                    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_updateRightBarItemTip object:dict1];
                    
                    NSDictionary *dict2 = @{SHOW_TIPIMG: [NSNumber numberWithBool:YES],
                                            TIPIMG_NAME: @"icon_new.png"};
                    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_updateVersionCheckTip object:dict2];
                }
            }
        }
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error){
        //
    }];
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
