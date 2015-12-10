//
//  KKBindShopViewController.m
//  KKOBD
//
//  Created by Jiahai on 13-12-19.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKBindShopViewController.h"
#import "KKViewUtils.h"
#import "KKAppDelegate.h"
#import "KKApplicationDefine.h"
#import "KKModelComplex.h"
#import "UIViewController+extend.h"
#import "KKUtils.h"
#import "KKGlobal.h"
#import "KKError.h"
#import "KKCustomAlertView.h"

#define     KKPopViewTag			10001

@interface KKBindShopViewController ()
@property (nonatomic, retain) KKModelVehicleDetailInfo *currentVehicle;
@end

@implementation KKBindShopViewController

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
	// Do any additional setup after loading the view.
    
    [self setVcEdgesForExtendedLayout];
    
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
    
    [self setNavigationBarTitle:@"绑定店铺"];
    
    
    self.currentVehicle = KKAppDelegateSingleton.currentVehicle;

    
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 320,  self.view.bounds.size.height)];
    bgImv.image = [[UIImage imageNamed:@"bg_background.png"] stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor redColor];
    [self.view addSubview:bgImv];
    [bgImv release];

    
    float originY = 21;
    //--------------------店铺信息------------------------------------------------------------------------------------------
    
    UILabel *label3 = [[UILabel alloc] initWithFrame:CGRectMake(0, originY, 76, 15)];
    label3.backgroundColor = [UIColor clearColor];
    label3.textColor = [UIColor blackColor];
    label3.font = [UIFont systemFontOfSize:15.0f];
    label3.textAlignment = UITextAlignmentRight;
    label3.text = @"店铺名称 :";
    [self.view addSubview:label3];
    [label3 release];
    
    _shopText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, originY-11, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _shopText.index = 10;
    _shopText.textField.enabled = NO;
    _shopText.textField.text = self.shopName;
    [self.view addSubview:_shopText];
    [_shopText release];
    
    originY += 50;
    
    UILabel *vehicleNoLable = [[UILabel alloc] initWithFrame:CGRectMake(0, originY, 76, 26)];
    vehicleNoLable.text = @"车牌号:";
    vehicleNoLable.backgroundColor = [UIColor clearColor];
    vehicleNoLable.textAlignment = UITextAlignmentRight;
    [self.view addSubview:vehicleNoLable];
    [vehicleNoLable release];
    
    _vehicleNoText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, originY - 11, 223, 38) WithType:eTextFieldImage WithPlaceholder:nil WithImage:[UIImage imageNamed:@"icon_shopq_downArrow.png"] WithRightInsetWidth:10];
    _vehicleNoText.index = 10;
    //_vehicleNoText.textField.delegate = self;
    _vehicleNoText.textField.text = self.currentVehicle.vehicleNo;
    
    UIButton *vehicleNoBtn = [[UIButton alloc] initWithFrame:_vehicleNoText.bounds];
    [vehicleNoBtn addTarget:self action:@selector(setPopviewAndShow:) forControlEvents:UIControlEventTouchUpInside];
    vehicleNoBtn.backgroundColor = [UIColor clearColor];
    vehicleNoBtn.tag = 2;
    [_vehicleNoText addSubview:vehicleNoBtn];
    [vehicleNoBtn release];
    
    [self.view addSubview:_vehicleNoText];
    [_vehicleNoText release];
    
    originY += 60;
    
    UIImage *image = [UIImage imageNamed:@"bg_registerBtn.png"];
    UIButton *submitButton = [[UIButton alloc] initWithFrame:CGRectMake(14, originY, image.size.width, image.size.height)];
    [submitButton setBackgroundColor:[UIColor clearColor]];
    [submitButton setBackgroundImage:[UIImage imageNamed:@"bg_registerBtn.png"] forState:UIControlStateNormal];
    [submitButton setTitle:@"提交" forState:UIControlStateNormal];
    [submitButton addTarget:self action:@selector(submitButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:submitButton];
    [submitButton release];
    
}

-(void) backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

-(void) submitButtonClicked
{
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    [[KKProtocolEngine sharedPtlEngine] registerShopBind:self.shopId vehicleId:self.currentVehicle.vehicleId delegate:self];
//    [[KKProtocolEngine sharedPtlEngine] vehicleSaveInfo:self.currentVehicle.vehicleId
//                                             vehicleVin:self.currentVehicle.vehicleVin
//                                              vehicleNo:self.currentVehicle.vehicleNo
//                                           vehicleModel:self.currentVehicle.vehicleModel
//                                         vehicleModelId:self.currentVehicle.vehicleModelId
//                                           vehicleBrand:self.currentVehicle.vehicleBrand
//                                         vehicleBrandId:self.currentVehicle.vehicleBrandId
//                                                  obdSN:self.currentVehicle.obdSN
//                                          bindingShopId:self.shopId
//                                                 userNo:[KKProtocolEngine sharedPtlEngine].userName
//                                               engineNo:self.currentVehicle.engineNo
//                                               registNo:self.currentVehicle.registNo
//                                    nextMaintainMileage:self.currentVehicle.nextMaintainMileage
//                                      nextInsuranceTime:[self.currentVehicle.
//                                                         nextInsuranceTime length] > 0 ? [KKUtils convertStringToDate:self.currentVehicle.nextInsuranceTime]:nil
//                                        nextExamineTime:[self.currentVehicle.nextExamineTime length] > 0 ? [KKUtils convertStringToDate:self.currentVehicle.nextExamineTime]:nil
//                                         currentMileage:self.currentVehicle.currentMileage
//                                               delegate:self];

}

#pragma mark -
#pragma mark KKProtocolEngineDelegate
-(NSNumber *) registerShopBindResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    
    [KKCustomAlertView showAlertViewWithMessage:((KKModelProtocolRsp *)rsp).header.desc block:^{
        [self.navigationController popToRootViewControllerAnimated:YES];
    }];
    
    return KKNumberResultEnd;
}

-(NSNumber *) vehicleSaveInfoResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    
    [KKCustomAlertView showAlertViewWithMessage:@"绑定成功！" block:^{
        [self.navigationController popToRootViewControllerAnimated:YES];
    }];
    
    return KKNumberResultEnd;
}

#pragma mark -
#pragma mark PopoverView
- (KKShopFilterPopView*) popMenuView
{
	for (UIView *subView in [self.view subviews]) {
		if (subView.tag == KKPopViewTag && [subView isKindOfClass:[KKShopFilterPopView class]])
			return (KKShopFilterPopView*)subView;
	}
	return nil;
}
-(void) setPopviewAndShow:(UIButton *)btn
{
    KKShopFilterPopView *popView = [self popMenuView];
    
    if (popView)
		[popView removeFromSuperview];
    
    int originY = btn.superview.frame.origin.y + btn.superview.frame.size.height*0.7;
    KKShopFilterPopView *FilterPopView = [[KKShopFilterPopView alloc] initWithFrame:CGRectMake(0, originY, 320, currentScreenHeight - originY- 44 - 49 - [self getOrignY]) WithArrowOrignX:160 WithRowHeight:33];
    FilterPopView.popViewDelegate = self;
    //选择车牌号
    FilterPopView.popId = 1001;
    
    NSMutableArray *parr = [NSMutableArray arrayWithCapacity:10];
    NSInteger pid = 0;
    
    for (KKModelVehicleDetailInfo *info in KKAppDelegateSingleton.vehicleList) {
        
        KKPopMenuItem *item = [[KKPopMenuItem alloc] initWithId:pid parentId:-1 title:info.vehicleNo others:nil];
        if([info.vehicleNo isEqualToString:self.currentVehicle.vehicleNo])
            FilterPopView.selectedInFirstList = pid;
        [parr addObject:item];
        [item release];
        pid++;
    }
    [FilterPopView setLeftDataArray:parr RightDataArray:nil];
    FilterPopView.tag = KKPopViewTag;
    [self.view addSubview:FilterPopView];
    [FilterPopView release];

}

#pragma mark KKShopFilterPopViewDelegate
-(void) KKShopFilterPopView:(KKShopFilterPopView *)popView WithItem:(KKPopMenuItem *)item AndParentItem:(KKPopMenuItem *)pItem
{
    self.currentVehicle = [KKAppDelegateSingleton.vehicleList objectAtIndex:item.itemId];
    _vehicleNoText.textField.text = self.currentVehicle.vehicleNo;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) dealloc
{
    self.shopId = nil;
    self.shopName = nil;
    self.currentVehicle = nil;
    [super dealloc];
}

@end
