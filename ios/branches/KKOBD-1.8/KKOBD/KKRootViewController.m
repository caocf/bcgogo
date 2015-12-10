//
//  KKRootViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-5.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKRootViewController.h"
#import "UIViewController+extend.h"
#import "KKFirstViewController.h"
#import "KKSecondViewController.h"
#import "KKThirdViewController.h"
#import "KKOilStationMapViewController.h"
#import "KKDTCManagerViewController.h"
#import "KKReviewViewController.h"
#import "KKHelper.h"

const NSString *constTabBarSelectedImageNames[] = {@"home_blue.png",@"message_blue.png",@"setting_blue.png"};
const NSString *constTabBarImageNames[] = {@"home_gray.png",@"message_gray.png",@"setting_gray.png"};
const NSString *constTitleStrings[] = {@"首页",@"消息中心",@"系统设置"};


@interface KKRootViewController ()

@end

@implementation KKRootViewController
@synthesize tabbarContentView = _tabbarContentView;

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    KKFirstViewController *Vc1 = [[KKFirstViewController alloc] init];
    UITabBarItem *tabItem1 = [[UITabBarItem alloc] initWithTitle:@"主页" image:nil tag:0];
    Vc1.tabBarItem = tabItem1;
    [tabItem1 release];
    
    UINavigationController *nav1 = [[UINavigationController alloc] initWithRootViewController:Vc1];
    
    KKSecondViewController *Vc2 = [[KKSecondViewController alloc] init];
    UITabBarItem *tabItem2 = [[UITabBarItem alloc] initWithTitle:@"预约" image:nil tag:1];
    Vc2.tabBarItem = tabItem2;
    [tabItem2 release];
    
    UINavigationController *nav2 = [[UINavigationController alloc] initWithRootViewController:Vc2];
    
    KKThirdViewController *Vc3 = [[KKThirdViewController alloc] init];
    UITabBarItem *tabItem3 = [[UITabBarItem alloc] initWithTitle:@"设置" image:nil tag:2];
    Vc3.tabBarItem = tabItem3;
    [tabItem3 release];
    
    UINavigationController *nav3 = [[UINavigationController alloc] initWithRootViewController:Vc3];
    
    self.viewControllers = [NSArray arrayWithObjects:nav1,nav2,nav3, nil];
    
    [Vc1 release];
    [Vc2 release];
    [Vc3 release];
    
    [nav1 release];
    [nav2 release];
    [nav3 release];
    
    [self setTabBarView];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(jumpToOilMapViewController) name:@"jumpToOilMapViewController" object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(jumpToDTCManagerViewController) name:@"jumpToDTCManagerViewController" object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(jumpToReviewViewController:) name:@"jumpToReviewViewController" object:nil];
    
}


- (void)setTabBarView
{
    _tabbarContentView = [[KKCustomTabbarContentView alloc] initWithFrame:CGRectMake(0, 0, 320, 49)];
    _tabbarContentView.backgroundColor = [UIColor clearColor];
    _tabbarContentView.userInteractionEnabled = YES;
    
    float width = 320.f/3;

    [_tabbarContentView setbgImageView:[UIImage imageNamed:@"bg_tabbar.png"]];
    [_tabbarContentView setSelectedBgImageViewWithImage:[UIImage imageNamed:@"bg_tabbarItem.png"] andWidth:width];
    
    for (int index = 0 ; index < 3 ; index ++)
    {
        KKCustomTabbarItem *item = [[KKCustomTabbarItem alloc] initWithFrame:CGRectMake(index * width, 0, width, 49)];
        item.delegate = self;
        item.tag = 1000+index;
        item.title = (NSString *)constTitleStrings[index];
        item.selectedImage = [UIImage imageNamed:(NSString *)constTabBarSelectedImageNames[index]];
        item.noselectedImage = [UIImage imageNamed:(NSString *)constTabBarImageNames[index]];
        item.selectedColor = [UIColor whiteColor];
        item.noselectedColor = [UIColor grayColor];
        [item setContentViews];
        [_tabbarContentView addItem:item];
        [item release];
    }
    self.selectedIndex = 0;
    [_tabbarContentView setItemSelected:self.selectedIndex];
    [self.tabBar addSubview:_tabbarContentView];
    [_tabbarContentView release];
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)didSelectedItem:(KKCustomTabbarItem*)item
{
    NSInteger index = item.tag - 1000;
    if (index >= 0 && index < 3)
    {
        if (index == self.selectedIndex)
        {
            UINavigationController *nav = [self.viewControllers objectAtIndex:index];
            [nav popToRootViewControllerAnimated:YES];
        }
        else
        {
            [_tabbarContentView setItemSelected:index];
        }
        self.selectedIndex = index;
    }
}

- (void)popToRootViewWithIndex:(NSInteger)index
{
    [_tabbarContentView setItemSelected:index];
    self.selectedIndex = index;
    
    UINavigationController *nav = [self.viewControllers objectAtIndex:index];
    [nav popToRootViewControllerAnimated:NO];
    
}

-(void) jumpToOilMapViewController
{
    KKOilStationMapViewController *oilMapVc = [[KKOilStationMapViewController alloc] init];
    UINavigationController *nav = [self.viewControllers objectAtIndex:self.selectedIndex];
    [nav pushViewController:oilMapVc animated:YES];
    [oilMapVc release];
}

-(void) jumpToDTCManagerViewController
{
    KKDTCManagerViewController *dtcManVc = [[KKDTCManagerViewController alloc] init];
    UINavigationController *nav = [self.viewControllers objectAtIndex:self.selectedIndex];
    [nav pushViewController:dtcManVc animated:YES];
    [dtcManVc release];
}

-(void) jumpToReviewViewController:(NSNotification *)notification
{
    KKModelMessage *msg = [notification object];
    NSArray *params = [KKHelper getArray:msg.params BySeparateString:@","];
    KKReviewViewController *Vc = [[KKReviewViewController alloc] initWithNibName:@"KKReviewViewController" bundle:nil];
    if ([params count] == 3)
        Vc.orderId = [params objectAtIndex:2];
    Vc.messageId = msg.id;
    UINavigationController *nav = [self.viewControllers objectAtIndex:self.selectedIndex];
    [nav pushViewController:Vc animated:YES];
    [Vc release];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    _tabbarContentView = nil;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    _tabbarContentView = nil;
    [super dealloc];
}
@end
