//
//  TGMenuViewController.m
//  TGOBD
//
//  Created by Jiahai on 14-3-5.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGMenuViewController.h"
#import "UIViewController+MMDrawerController.h"
#import "TGDriveRecordViewController.h"
#import "TGOilStationMapViewController.h"
#import "TGViolateViewController.h"

@interface TGMenuViewController ()
@property (nonatomic, strong) NSDictionary *paneViewControllerTitles;
@property (nonatomic, strong) NSDictionary *paneViewControllerClasses;
@property (nonatomic, strong) UIBarButtonItem *paneStateBarButtonItem;
@property (nonatomic, strong) UIBarButtonItem *paneRevealLeftBarButtonItem;
@property (nonatomic, strong) UIBarButtonItem *paneRevealRightBarButtonItem;
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
                                      @(TGPaneViewControllerTypeDriveRecord) : @"行车日志",
                                      @(TGPaneViewControllerTypeOilStation) : @"加油站",
                                      @(TGPaneViewControllerTypeViolate) : @"违章查询"
                                      };

    self.paneViewControllerClasses = @{
                                       @(TGPaneViewControllerTypeDriveRecord) : [TGDriveRecordViewController class],
                                       @(TGPaneViewControllerTypeOilStation) : [TGOilStationMapViewController class],
                                       @(TGPaneViewControllerTypeViolate) : [TGViolateViewController class]
                                       };

}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    NSInteger count = [self.paneViewControllerTitles count];
    
    for(int i=0; i<count; i++)
    {
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
        [btn setFrame:CGRectMake(20, 80+i*60, 80, 40)];
        [btn setTitle:[self.paneViewControllerTitles objectForKey:@(i)] forState:UIControlStateNormal];
        btn.tag = i;
        [btn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        [btn addTarget:self action:@selector(btnClicked:) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:btn];
    }
}

- (void)btnClicked:(UIButton *)sender
{
    [self transitionToViewController:sender.tag];
}

- (void)transitionToViewController:(TGPaneViewControllerType)paneViewControllerType
{
    // Close pane if already displaying the pane view controller
    
    
    BOOL animateTransition = self.mm_drawerController != nil;
    
    Class paneViewControllerClass = self.paneViewControllerClasses[@(paneViewControllerType)];
    UIViewController *paneViewController = (UIViewController *)[paneViewControllerClass new];
    
    paneViewController.navigationItem.title = self.paneViewControllerTitles[@(paneViewControllerType)];
    
    self.paneRevealLeftBarButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"LeftRevealIcon.png"] style:UIBarButtonItemStyleBordered target:self action:@selector(leftDrawerButtonPress:)];
    paneViewController.navigationItem.leftBarButtonItem = self.paneRevealLeftBarButtonItem;
    
    self.paneRevealRightBarButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"RightRevealIcon.png"] style:UIBarButtonItemStyleBordered target:self action:@selector(rightDrawerButtonPress:)];
    paneViewController.navigationItem.rightBarButtonItem = self.paneRevealRightBarButtonItem;
    
    UINavigationController *paneNavigationViewController = [[UINavigationController alloc] initWithRootViewController:paneViewController];
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


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
