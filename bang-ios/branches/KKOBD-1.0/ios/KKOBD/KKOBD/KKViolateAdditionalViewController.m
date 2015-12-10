//
//  KKViolateAdditionalViewController.m
//  KKOBD
//
//  Created by Jiahai on 13-12-16.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKViolateAdditionalViewController.h"
#import "KKApplicationDefine.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKPreference.h"
#import "KKUtils.h"
#import "MBProgressHUD.h"
#import "KKAppDelegate.h"
#import "KKCustomTextField.h"

@interface KKViolateAdditionalViewController ()

@end

@implementation KKViolateAdditionalViewController

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
    
    [self setNavgationBar];
    [self setBackGroundView];
    
    [self addMainScrollView];
    
    [_engineText.textField becomeFirstResponder];
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"违章查询信息";
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
    
}

- (void)setBackGroundView
{
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:self.view.bounds];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor clearColor];
    bgImv.image = [UIImage imageNamed:@"bg_serviceSeeking.png"];
    [self.view addSubview:bgImv];
    [bgImv release];
}

-(void) addMainScrollView
{
    
    _mainScrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, 320, currentScreenHeight - 44 - 49 - [self getOrignY])];
    _mainScrollView.backgroundColor = [UIColor clearColor];
    
    float orignY1 = 21 ,orignY2 = 10;
    
    UILabel *label12 = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY1, 75, 15)];
    label12.backgroundColor = [UIColor clearColor];
    label12.textColor = self.violateSearchCondition.needEngine ? [UIColor redColor] : [UIColor blackColor];
    label12.font = [UIFont systemFontOfSize:15.0f];
    label12.textAlignment = UITextAlignmentRight;
    label12.text = @"发动机号 :";
    [_mainScrollView addSubview:label12];
    [label12 release];
    
    _engineText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _engineText.index = 17;
    _engineText.textField.text = self.vehicleDetailInfo.engineNo;
    [_mainScrollView addSubview:_engineText];
    [_engineText release];
    
    orignY1 += 45;
    orignY2 += 45;
    
    UILabel *label13 = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY1, 75, 15)];
    label13.backgroundColor = [UIColor clearColor];
    label13.textColor = self.violateSearchCondition.needClassa ? [UIColor redColor] : [UIColor blackColor];
    label13.font = [UIFont systemFontOfSize:15.0f];
    label13.textAlignment = UITextAlignmentRight;
    label13.text = @"车架号 :";
    [_mainScrollView addSubview:label13];
    [label13 release];
    
    _classaText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _classaText.index = 14;
    _classaText.textField.text = self.vehicleDetailInfo.vehicleVin;
    [_mainScrollView addSubview:_classaText];
    [_classaText release];
    
    orignY1 += 45;
    orignY2 += 45;
    
    
    UILabel *label14 = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY1, 75, 15)];
    label14.backgroundColor = [UIColor clearColor];
    label14.textColor = self.violateSearchCondition.needRegist ? [UIColor redColor] : [UIColor blackColor];
    label14.font = [UIFont systemFontOfSize:15.0f];
    label14.textAlignment = UITextAlignmentRight;
    label14.text = @"行驶证号 :";
    [_mainScrollView addSubview:label14];
    [label14 release];
    
    _registText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _registText.index = 15;
    _registText.textField.text = self.vehicleDetailInfo.registNo;
    [_mainScrollView addSubview:_registText];
    [_registText release];
    
    orignY2 += 55;
    
    UIImage *image = [UIImage imageNamed:@"bg_setting_bind_btn.png"];
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), orignY2, image.size.width, image.size.height)];
    [button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [button setTitle:@"提交" forState:UIControlStateNormal];
    [button setBackgroundImage:image forState:UIControlStateNormal];
    [button.titleLabel setFont:[UIFont boldSystemFontOfSize:17.f]];
    [button addTarget:self action:@selector(submitBtnClicked) forControlEvents:UIControlEventTouchUpInside];
    [_mainScrollView addSubview:button];
    [button release];

    
    orignY2 += 55;
    
    [_mainScrollView setContentSize:CGSizeMake(320, MAX(currentScreenHeight - 44 - 49 - [self getOrignY], orignY2+10))];
    [self.view addSubview:_mainScrollView];
    [_mainScrollView release];
}


-(void) submitBtnClicked
{
    if(self.violateSearchCondition.needEngine && _engineText.textField.text.length < 1)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请填写发动机号！"];
        return;
    }
    if(self.violateSearchCondition.needClassa && _classaText.textField.text.length < 1)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请填写车架号！"];
        return;
    }
    if(self.violateSearchCondition.needRegist && _registText.textField.text.length < 1)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请填写行驶证号！"];
        return;
    }
    
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    
    [[KKProtocolEngine sharedPtlEngine] vehicleSaveInfo:self.vehicleDetailInfo.vehicleId
                                             vehicleVin:_classaText.textField.text
                                              vehicleNo:self.vehicleDetailInfo.vehicleNo
                                           vehicleModel:self.vehicleDetailInfo.vehicleModel
                                         vehicleModelId:self.vehicleDetailInfo.vehicleModelId
                                           vehicleBrand:self.vehicleDetailInfo.vehicleBrand
                                         vehicleBrandId:self.vehicleDetailInfo.vehicleBrandId
                                                  obdSN:self.vehicleDetailInfo.obdSN
                                          bindingShopId:self.vehicleDetailInfo.recommendShopId
                                                 userNo:[KKProtocolEngine sharedPtlEngine].userName
                                               engineNo:_engineText.textField.text
                                               registNo:_registText.textField.text
                                    nextMaintainMileage:self.vehicleDetailInfo.nextMaintainMileage
                                      nextInsuranceTime:[self.vehicleDetailInfo.nextExamineTime length] > 0 ? [KKUtils convertStringToDate:self.vehicleDetailInfo.nextExamineTime]:nil
                                        nextExamineTime:[self.vehicleDetailInfo.nextExamineTime length] > 0 ? [KKUtils convertStringToDate:self.vehicleDetailInfo.nextExamineTime]:nil
                                         currentMileage:self.vehicleDetailInfo.currentMileage delegate:self];
}

-(void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate

- (NSNumber *)vehicleSaveInfoResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    
    self.vehicleDetailInfo.engineNo = _engineText.textField.text;
    self.vehicleDetailInfo.vehicleVin = _classaText.textField.text;
    self.vehicleDetailInfo.registNo = _registText.textField.text;
    
    KKModelSaveVehicleInfoRsp *proRsp = (KKModelSaveVehicleInfoRsp *)rsp;
    [KKCustomAlertView showAlertViewWithMessage:proRsp.header.desc block:^{
        
        [self backButtonClicked];
    }];
    
    return KKNumberResultEnd;
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) dealloc
{
    self.vehicleDetailInfo = nil;
    [super dealloc];
}

@end
