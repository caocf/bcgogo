//
//  KKServiceDetailViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-21.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKServiceDetailViewController.h"
#import "KKApplicationDefine.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKServiceDetailView.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKPreference.h"
#import "MBProgressHUD.h"
#import "UIImageView+WebCache.h"
#import "KKShopDetailViewController.h"
#import "KKReviewViewController.h"

@interface KKServiceDetailViewController ()

@end

@implementation KKServiceDetailViewController
- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self setVcEdgesForExtendedLayout];
    [self initVariables];
    [self initComponents];
    [self getInfo];
}

#pragma mark -
#pragma mark Custom methods

- (void) initVariables
{
    
}

- (void) initComponents
{
    [self setNavgationBar];
    
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:self.view.bounds];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor clearColor];
    bgImv.image = [UIImage imageNamed:@"bg_serviceSeeking.png"];
    [self.view addSubview:bgImv];
    [bgImv release];
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"单据详情";
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
}

- (void)addContentView
{
    KKServiceDetailView *detailView = [[KKServiceDetailView alloc] initWithFrame:CGRectMake(10, 0, 300, currentScreenHeight - 44 - 49 - [self getOrignY]) WithContent:self.detail];
    detailView.actionDelegate = self;
    detailView.showsVerticalScrollIndicator = NO;
    [self.view addSubview:detailView];
    [detailView release];
}

- (void)getInfo
{
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    [[KKProtocolEngine sharedPtlEngine] serviceHistoryDetail:self.orderId serviceScope:nil delegate:self];
}
#pragma mark -
#pragma mark Events

- (void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark -
#pragma mark KKServiceDetailViewDelegate

- (void)KKServiceDetailViewCancelButtonClicked
{
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    [[KKProtocolEngine sharedPtlEngine] serviceCancel:self.detail.id delegate:self];
}

- (void)KKServiceDetailViewEvaluatButtonClicked
{
    KKReviewViewController *Vc = [[KKReviewViewController alloc] initWithNibName:@"KKReviewViewController" bundle:nil];
    Vc.orderId = self.orderId;
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
}

- (void)KKServiceDetailViewShopNameButtonClicked
{
    KKShopDetailViewController *Vc= [[KKShopDetailViewController alloc] initWithNibName:@"KKShopDetailViewController" bundle:nil];
    Vc.shopId = self.detail.shopId;
    Vc.serviceName = self.detail.serviceType;
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate
- (NSNumber *)serviceHistoryDetailResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    
    KKModelServiceDetailRsp *detailRsp = (KKModelServiceDetailRsp *)rsp;
    self.detail = detailRsp.serviceDetail;
    [self addContentView];
    
    return KKNumberResultEnd;
}

- (NSNumber *)serviceDeleteResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    KKModelProtocolRsp *proRsp = (KKModelProtocolRsp *)rsp;
    [KKCustomAlertView showAlertViewWithMessage:proRsp.header.desc block:^{
        [[NSNotificationCenter defaultCenter] postNotificationName:@"refreshUnfinishedServiceNotification" object:nil];
        [self backButtonClicked];
    }];
    return KKNumberResultEnd;
}

#pragma mark -
#pragma mark Handle memory

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
}

- (void)dealloc
{
    
    [super dealloc];
}
@end
