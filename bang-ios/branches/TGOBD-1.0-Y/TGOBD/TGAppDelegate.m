//
//  TGAppDelegate.m
//  TGOBD
//
//  Created by Jiahai on 14-2-28.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGAppDelegate.h"
#import "MMDrawerController.h"
#import "MMDrawerVisualState.h"
#import "UIViewController+MMDrawerController.h"
#import "TGNavigationController.h"
#import "TGMainViewController.h"
#import "TGMenuViewController.h"
#import "TGDriveRecordViewController.h"
#import "AKLocationManager.h"
#import "BMKMapManager.h"
#import "BMKGeometry.h"
#import "TGDataSingleton.h"

@interface TGAppDelegate ()
@property (nonatomic, strong) MMDrawerController        *drawerController;

@end

static BMKMapManager        *g_mapManager = nil;

@implementation TGAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    TGMenuViewController *leftSideDrawerViewController = [[TGMenuViewController alloc] init];
    
    TGDriveRecordViewController *centerViewController = [[TGDriveRecordViewController alloc] init];
    
    TGMainViewController *rightSideDrawerViewController = [[TGMainViewController alloc] init];

    UINavigationController * navigationController = [[TGNavigationController alloc] initWithRootViewController:centerViewController];

    self.drawerController = [[MMDrawerController alloc]
                             initWithCenterViewController:navigationController
                             leftDrawerViewController:leftSideDrawerViewController
                             rightDrawerViewController:rightSideDrawerViewController];
    
    [self.drawerController setMaximumLeftDrawerWidth:120];
    [self.drawerController setMaximumRightDrawerWidth:260.0];
    [self.drawerController setOpenDrawerGestureModeMask:MMOpenDrawerGestureModePanningNavigationBar];
    [self.drawerController setCloseDrawerGestureModeMask:MMCloseDrawerGestureModeAll];
    
    [((TGMenuViewController *)self.drawerController.leftDrawerViewController) transitionToViewController:TGPaneViewControllerTypeDriveRecord];
    
    [self.drawerController setDrawerVisualStateBlock:[MMDrawerVisualState slideVisualStateBlock]];
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    // Override point for customization after application launch.
    self.window.backgroundColor = [UIColor whiteColor];
    self.window.rootViewController = self.drawerController;
    [self.window makeKeyAndVisible];
    
    g_mapManager = [[BMKMapManager alloc] init];
    
    BOOL ret = [g_mapManager start:@"27066f966039c833fdd7c9a46f3921e4"  generalDelegate:nil];
    if (!ret) {
        NSLog(@"manager start failed!");
    }
    
    [self startLocating];
    
    return YES;
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
