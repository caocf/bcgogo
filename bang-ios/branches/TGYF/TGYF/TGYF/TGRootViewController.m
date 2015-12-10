//
//  TGRootViewController.m
//  TGYF
//
//  Created by James Yu on 14-5-19.
//  Copyright (c) 2014年 james. All rights reserved.
//

#import "TGRootViewController.h"
#import "TGSegmentView.h"
#import "TGFaultViewController.h"
#import "TGMaintenanceViewController.h"
#import "TGOrderViewController.h"

@interface TGRootViewController () <TGSegmentViewDelegate, UIPageViewControllerDataSource, UIPageViewControllerDelegate, UIScrollViewDelegate>

@property (nonatomic, strong) TGSegmentView *segmentView;
@property (nonatomic, strong) UIPageViewController *pageController;
@property (nonatomic, strong) NSMutableArray *viewControllers;
@property (nonatomic, strong) UIScrollView *scrollView;

@end

@implementation TGRootViewController

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
    [self setNavigationBarTitle:@"代办事项"];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Custom Method

- (void)initComponents
{
    CGFloat originY = [self getViewOriginY] + 10;
    
    _segmentView = [[TGSegmentView alloc] initWithFrame:CGRectMake(0, originY, SCREEN_WIDTH, 30) segmentTitles:@[@"故障", @"保养", @"预约"]];
    _segmentView.delegate = self;
    [self.view addSubview:_segmentView];
    
    originY += 40;
    
    TGFaultViewController *vc1 = [[TGFaultViewController alloc] init];
    TGMaintenanceViewController *vc2 = [[TGMaintenanceViewController alloc] init];
    TGOrderViewController *vc3 = [[TGOrderViewController alloc] init];
    
    _viewControllers = [[NSMutableArray alloc] initWithObjects:vc1, vc2, vc3, nil];
    
    _pageController = [[UIPageViewController alloc] initWithTransitionStyle:UIPageViewControllerTransitionStyleScroll navigationOrientation:UIPageViewControllerNavigationOrientationHorizontal options:nil];
    _pageController.delegate = self;
    _pageController.dataSource = self;
    
    [[_pageController view] setFrame:CGRectMake(0, originY, SCREEN_WIDTH, [self getViewHeight] - originY)];
    [self addChildViewController:_pageController];
    [self.view addSubview:[_pageController view]];
    [_pageController didMoveToParentViewController:self];
    NSArray *vcArray = [NSArray arrayWithObject:vc1];
    [_pageController setViewControllers:vcArray direction:UIPageViewControllerNavigationDirectionForward animated:NO completion:nil];

    _scrollView = (UIScrollView *)[[_pageController.view subviews] objectAtIndex:0];
    _scrollView.delegate = self;
    
}

- (TGBaseViewController *)viewControllerAtIndex:(NSInteger)index
{
    _segmentView.segment.selectedSegmentIndex = index;
    return [_viewControllers objectAtIndex:index];
}

- (NSInteger)indexOfViewController:(TGBaseViewController *)viewController
{
    return [_viewControllers indexOfObject:viewController];
}

#pragma mark - TGSegmentViewDelegate

- (void)TGSegmentViewDidChange:(NSInteger)currentSelect
{
    TGBaseViewController *vc = [self viewControllerAtIndex:currentSelect];
    
    [_pageController setViewControllers:@[vc] direction:UIPageViewControllerNavigationDirectionForward animated:NO completion:nil];
}

#pragma mark - UIPageViewControllerDataSource

- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerBeforeViewController:(UIViewController *)viewController
{
    NSInteger index = [self indexOfViewController:(TGBaseViewController *)viewController];
    
    if ( index == 0 || index == NSNotFound) {
        return nil;
    }
    return [_viewControllers objectAtIndex:--index];
}

- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerAfterViewController:(UIViewController *)viewController
{
    NSInteger index = [self indexOfViewController:(TGBaseViewController *)viewController];
    
    if (index == ([_viewControllers count] - 1) || index == NSNotFound) {
        return nil;
    }
    return [_viewControllers objectAtIndex:++index];
}

- (void)pageViewController:(UIPageViewController *)pageViewController didFinishAnimating:(BOOL)finished previousViewControllers:(NSArray *)previousViewControllers transitionCompleted:(BOOL)completed
{
    TGBaseViewController *vc = _pageController.viewControllers[0];
    NSInteger n = [self indexOfViewController:vc];
    _segmentView.segment.selectedSegmentIndex = n;
}

@end
