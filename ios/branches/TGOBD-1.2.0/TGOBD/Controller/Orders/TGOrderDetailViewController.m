//
//  TGOrderDetailViewController.m
//  TGOBD
//
//  Created by James Yu on 14-3-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGOrderDetailViewController.h"
#import "TGHTTPRequestEngine.h"

@interface TGOrderDetailViewController ()

@end

@implementation TGOrderDetailViewController

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
    
    [self setNavigationTitle:@"账单详情"];
    [self getOrderDetail];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - Custom Methods

- (void)initComponentsWithDetailInfo:(TGModelOrderDetail *)detailInfo
{
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar];
    CGFloat viewHeight = [self getViewHeightWithNavigationBar];
    
    _scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, originY, screenWidth, viewHeight)];
    
    _orderHeadView = [[TGOrderDetailView alloc] initViewHeaderWithFrame:CGRectMake(10, 10, 300, 400) headInfo:detailInfo];
    _orderDetailView = [[TGOrderDetailView alloc] initViewDetailWithFrame:CGRectMake(10, 20 + _orderHeadView.viewHeight, 300, 400) detailInfo:detailInfo];
    
    [_scrollView addSubview:_orderHeadView];
    [_scrollView addSubview:_orderDetailView];
    
    CGFloat detailViewHeight = _orderDetailView.viewHeight + _orderHeadView.viewHeight + 30;
    
    [_scrollView setContentSize:CGSizeMake(screenWidth, viewHeight > detailViewHeight ? viewHeight : detailViewHeight)];
    [self.view addSubview:_scrollView];
    _scrollView.bounces = NO;
}

- (void)getOrderDetail
{
    [TGProgressHUD show];
    
    [[TGHTTPRequestEngine sharedInstance] getOrderDetail:_orderId serviceScope:_serviceScope viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if ([self httpResponseCorrect:responseObject]) {
            TGModelOrderDetailRsp *resObj = (TGModelOrderDetailRsp *)responseObject;
            [self initComponentsWithDetailInfo:resObj.serviceDetail];
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [self httpRequestSystemError:error];
        //TODO
    }];
}

@end
