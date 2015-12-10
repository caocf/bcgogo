//
//  KKShowOrAddNewBindCarViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-19.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKShowOrAddNewBindCarViewController.h"
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

@interface KKShowOrAddNewBindCarViewController ()

@end

@implementation KKShowOrAddNewBindCarViewController
@synthesize type;

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self setVcEdgesForExtendedLayout];
    [self initVariables];
    [self initComponents];
    
    if ([self.vehicleId length] > 0)
    {
        [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        [[KKProtocolEngine sharedPtlEngine] vehicleGetInfo:self.vehicleId delegate:self];
    }
    else
        [self addScrollView];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self resignKeyboardNotification];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [self removeKeyboardNotification];
}

#pragma mark -
#pragma mark Custom methods
- (void) initVariables
{
    self.hiddenMoreInfo = YES;
    
    if (self.vehicleDetailInfo == nil)
    {
        KKModelVehicleDetailInfo *vehicleInfo = [[KKModelVehicleDetailInfo alloc] init];
        self.vehicleDetailInfo = vehicleInfo;
        [vehicleInfo release];
    }
    else{
        self.vehicleVin = self.vehicleDetailInfo.vehicleVin;
        self.obdSN = self.vehicleDetailInfo.obdSN;
    }
}

- (void) initComponents
{
    [self setNavgationBar];
    [self setBachGroundView];
}

- (void)addScrollView
{
    UIImage *image = nil;
    
    _mainScrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, 320, currentScreenHeight - 44 - 49 - [self getOrignY])];
    _mainScrollView.backgroundColor = [UIColor clearColor];
    
    float orignY1 = 21 ,orignY2 = 10;
    
    UILabel *label0 = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY1, 75, 15)];
    label0.backgroundColor = [UIColor clearColor];
    label0.textColor = [UIColor blackColor];
    label0.font = [UIFont systemFontOfSize:15.0f];
    label0.textAlignment = UITextAlignmentRight;
    label0.text = @"绑定OBD :";
    [_mainScrollView addSubview:label0];
    [label0 release];
    
    _textField0 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField0.index = 10;
    _textField0.textField.text = self.obdSN;
    _textField0.userInteractionEnabled = NO;
    _showAddButtonView = [[UIView alloc] initWithFrame:_textField0.bounds];
    _showAddButtonView.backgroundColor = [UIColor clearColor];
    _showAddButtonView.userInteractionEnabled = YES;
    
    image = [UIImage imageNamed:@"icon_setting_bind_cross.png"];
    UIImageView *iconImv = [[UIImageView alloc] initWithFrame:CGRectMake(75, 0.5*(38 - image.size.height), image.size.width, image.size.height)];
    iconImv.image = image;
    [_showAddButtonView addSubview:iconImv];
    [iconImv release];
    
    UILabel *txtLb = [[UILabel alloc] initWithFrame:CGRectMake(90, 11, 100, 15)];
    txtLb.textAlignment = UITextAlignmentLeft;
    txtLb.textColor = KKCOLOR_A7a6a6;
    txtLb.text = @"添加设备";
    txtLb.font = [UIFont systemFontOfSize:13.f];
    [_showAddButtonView addSubview:txtLb];
    [txtLb release];
    
    [_textField0 addSubview:_showAddButtonView];
    [_showAddButtonView release];

    
    if ([self.obdSN length] == 0)
    {
        _showAddButtonView.hidden = NO;
    }
    else
    {
        _showAddButtonView.hidden = YES;
        _textField0.textField.textColor = KKCOLOR_A7a6a6;
        [_textField0 setBgImvToNil];
    }
    [_mainScrollView addSubview:_textField0];
    [_textField0 release];
    
    _addNewButton = [[UIButton alloc] initWithFrame:_textField0.bounds];
    _addNewButton.backgroundColor = [UIColor clearColor];
    [_addNewButton addTarget:self action:@selector(addNewButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [_mainScrollView addSubview:_addNewButton];
    [_addNewButton release];
    
    
    orignY1 += 45;
    orignY2 += 45;
    
    UILabel *label1 = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY1, 75, 15)];
    label1.backgroundColor = [UIColor clearColor];
    label1.textColor = [UIColor blackColor];
    label1.font = [UIFont systemFontOfSize:15.0f];
    label1.textAlignment = UITextAlignmentRight;
    label1.text = @"车牌号 :";
    [_mainScrollView addSubview:label1];
    [label1 release];
    
    _textField1 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField1.index = 11;
    _textField1.delegate = self;
    _textField1.textField.text = self.vehicleDetailInfo.vehicleNo;
    [_mainScrollView addSubview:_textField1];
    [_textField1 release];
    
    orignY1 += 45;
    orignY2 += 45;
    
    UILabel *label2 = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY1, 75, 15)];
    label2.backgroundColor = [UIColor clearColor];
    label2.textColor = [UIColor blackColor];
    label2.font = [UIFont systemFontOfSize:15.0f];
    label2.textAlignment = UITextAlignmentRight;
    label2.text = @"品牌车型 :";
    [_mainScrollView addSubview:label2];
    [label2 release];
    
    _textField2_1 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 106, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField2_1.index = 12;
    _textField2_1.textField.text = self.vehicleDetailInfo.vehicleBrand;
    _textField2_1.addtionalInfo = self.vehicleDetailInfo.vehicleBrandId;
    
    _carBrandMarkView = [KKHelper creatCarBrandMarkView:_textField2_1.bounds withTitle:@"品牌"];
    [_textField2_1 addSubview:_carBrandMarkView];
    if ([self.vehicleDetailInfo.vehicleBrand length] > 0)
        _carBrandMarkView.hidden = YES;
    
    [_mainScrollView addSubview:_textField2_1];
    [_textField2_1 release];
    
    UIButton *carBrandBtn = [[UIButton alloc] initWithFrame:_textField2_1.frame];
    [carBrandBtn addTarget:self action:@selector(carBrandModelButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    carBrandBtn.backgroundColor = [UIColor clearColor];
    carBrandBtn.tag = 21;
    [_mainScrollView addSubview:carBrandBtn];
    [carBrandBtn release];
    
    _textField2_2 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(202, orignY2, 106, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField2_2.index = 13;
    _textField2_2.textField.text = self.vehicleDetailInfo.vehicleModel;
    _textField2_2.addtionalInfo = self.vehicleDetailInfo.vehicleModelId;
    
    _carModelMarkView = [KKHelper creatCarBrandMarkView:_textField2_2.bounds withTitle:@"车型"];
    [_textField2_2 addSubview:_carModelMarkView];
    if ([self.vehicleDetailInfo.vehicleModel length] > 0)
        _carModelMarkView.hidden = YES;
    
    [_mainScrollView addSubview:_textField2_2];
    [_textField2_2 release];
    
    UIButton *carModelBtn = [[UIButton alloc] initWithFrame:_textField2_2.frame];
    [carModelBtn addTarget:self action:@selector(carBrandModelButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    carModelBtn.backgroundColor = [UIColor clearColor];
    carModelBtn.tag = 22;
    [_mainScrollView addSubview:carModelBtn];
    [carModelBtn release];
    
    orignY1 += 45;
    orignY2 += 45;
    
    //--------------------扫描店铺------------------------------------------------------------------------------------------
    
    UILabel *label3 = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY1, 75, 15)];
    label3.backgroundColor = [UIColor clearColor];
    label3.textColor = [UIColor blackColor];
    label3.font = [UIFont systemFontOfSize:15.0f];
    label3.textAlignment = UITextAlignmentRight;
    label3.text = @"绑定店铺 :";
    [_mainScrollView addSubview:label3];
    [label3 release];
    
    _textField3 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 223, 38) WithType:eTextFieldImage WithPlaceholder:nil WithImage:[UIImage imageNamed:@"icon_scan.png"] WithRightInsetWidth:10.f];
    _textField3.textField.minimumFontSize = 10;
    _textField3.index = 13;
    _textField3.textField.text = nilOrString(self.vehicleDetailInfo.recommendShopName);
    _textField3.addtionalInfo = nilOrString(self.vehicleDetailInfo.recommendShopId);
    
    UIButton *button3 = [[UIButton alloc] initWithFrame:_textField3.bounds];
    button3.backgroundColor = [UIColor clearColor];
    [button3 addTarget:self action:@selector(scanButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [_textField3 addSubview:button3];
    [button3 release];
    
    [_mainScrollView addSubview:_textField3];
    [_textField3 release];
    
    orignY1 += 45;
    orignY2 += 45;
    
    
    _moreMaskView = [[UIView alloc] initWithFrame:CGRectMake(0, orignY2, 320, currentScreenHeight - 44 - 49 - [self getOrignY])];
    _moreMaskView.backgroundColor = [UIColor whiteColor];
    _moreMaskView.hidden = !self.hiddenMoreInfo;
    [_mainScrollView addSubview:_moreMaskView];
    [_moreMaskView release];
        
    
    image = self.hiddenMoreInfo ? [UIImage imageNamed:@"icon_showMore.png"] : [UIImage imageNamed:@"icon_hiddenMore.png"];
    UIButton *hideBtn = [[UIButton alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), orignY2, image.size.width, image.size.height)];
    [hideBtn setImage:image forState:UIControlStateNormal];
    [hideBtn addTarget:self action:@selector(hideButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_mainScrollView addSubview:hideBtn];
    
    orignY1 += hideBtn.frame.size.height;
    orignY2 += hideBtn.frame.size.height;

    _moreInfoView = [[UIView alloc] init];
    _moreInfoView.backgroundColor = [UIColor whiteColor];
    _moreInfoView.hidden = self.hiddenMoreInfo;
    
    int moreInfoHeight = 10;
    
    UILabel *label19 = [[UILabel alloc] initWithFrame:CGRectMake(0, moreInfoHeight+11, 75, 15)];
    label19.backgroundColor = [UIColor clearColor];
    label19.textColor = [UIColor blackColor];
    label19.font = [UIFont systemFontOfSize:15.0f];
    label19.textAlignment = UITextAlignmentRight;
    label19.text = @"车架号 :";
    [_moreInfoView addSubview:label19];
    [label19 release];
    
    _vehicleVinText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, moreInfoHeight, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _vehicleVinText.index = 14;
    _vehicleVinText.textField.text = self.vehicleVin;
    [_moreInfoView addSubview:_vehicleVinText];
    [_vehicleVinText release];
    
    moreInfoHeight+=45;

    UILabel *label20 = [[UILabel alloc] initWithFrame:CGRectMake(0, moreInfoHeight+11, 75, 15)];
    label20.backgroundColor = [UIColor clearColor];
    label20.textColor = [UIColor blackColor];
    label20.font = [UIFont systemFontOfSize:15.0f];
    label20.textAlignment = UITextAlignmentRight;
    label20.text = @"发动机号 :";
    [_moreInfoView addSubview:label20];
    [label20 release];
    
    _engineText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, moreInfoHeight, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _engineText.index = 15;
    _engineText.textField.text = self.vehicleDetailInfo.engineNo;
    [_moreInfoView addSubview:_engineText];
    [_engineText release];
    
    moreInfoHeight+=45;

    UILabel *label21 = [[UILabel alloc] initWithFrame:CGRectMake(0, moreInfoHeight+11, 75, 15)];
    label21.backgroundColor = [UIColor clearColor];
    label21.textColor = [UIColor blackColor];
    label21.font = [UIFont systemFontOfSize:15.0f];
    label21.textAlignment = UITextAlignmentRight;
    label21.text = @"行驶证号 :";
    [_moreInfoView addSubview:label21];
    [label21 release];
    
    _registText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, moreInfoHeight, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _registText.index = 16;
    _registText.textField.text = self.vehicleDetailInfo.registNo;
    [_moreInfoView addSubview:_registText];
    [_registText release];
    
    moreInfoHeight += 45;
    
    UILabel *label7 = [[UILabel alloc] initWithFrame:CGRectMake(0, moreInfoHeight+11, 75, 15)];
    label7.backgroundColor = [UIColor clearColor];
    label7.textColor = [UIColor blackColor];
    label7.font = [UIFont systemFontOfSize:15.0f];
    label7.textAlignment = UITextAlignmentRight;
    label7.text = @"当前里程 :";
    [_moreInfoView addSubview:label7];
    [label7 release];
    
    _textField7 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, moreInfoHeight, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField7.index = 17;
    _textField7.textField.text = self.vehicleDetailInfo.currentMileage;
    _textField7.textField.keyboardType= UIKeyboardTypeNumberPad;
    [_moreInfoView addSubview:_textField7];
    [_textField7 release];
    
    moreInfoHeight += 45;
    
    UILabel *label4 = [[UILabel alloc] initWithFrame:CGRectMake(7, moreInfoHeight+3, 60, 16)];
    label4.backgroundColor = [UIColor clearColor];
    label4.textColor = [UIColor blackColor];
    label4.font = [UIFont systemFontOfSize:15.0f];
    label4.textAlignment = UITextAlignmentRight;
    label4.text = @"下次保养";
    [_moreInfoView addSubview:label4];
    [label4 release];
    
    UILabel *label4_2 = [[UILabel alloc] initWithFrame:CGRectMake(10, moreInfoHeight+19, 65, 16)];
    label4_2.backgroundColor = [UIColor clearColor];
    label4_2.textColor = [UIColor blackColor];
    label4_2.font = [UIFont systemFontOfSize:15.0f];
    label4_2.textAlignment = UITextAlignmentRight;
    label4_2.text = @"里程 :";
    [_moreInfoView addSubview:label4_2];
    [label4_2 release];
    
    _textField4 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, moreInfoHeight, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField4.index = 18;
    _textField4.textField.text = self.vehicleDetailInfo.nextMaintainMileage;
    _textField4.textField.keyboardType= UIKeyboardTypeNumberPad;
    [_moreInfoView addSubview:_textField4];
    [_textField4 release];
    
    moreInfoHeight += 45;
    
    UILabel *label5 = [[UILabel alloc] initWithFrame:CGRectMake(7, moreInfoHeight+3, 60, 16)];
    label5.backgroundColor = [UIColor clearColor];
    label5.textColor = [UIColor blackColor];
    label5.font = [UIFont systemFontOfSize:15.0f];
    label5.textAlignment = UITextAlignmentRight;
    label5.text = @"下次保险";
    [_moreInfoView addSubview:label5];
    [label5 release];
    
    UILabel *label5_2 = [[UILabel alloc] initWithFrame:CGRectMake(10, moreInfoHeight+19, 65, 16)];
    label5_2.backgroundColor = [UIColor clearColor];
    label5_2.textColor = [UIColor blackColor];
    label5_2.font = [UIFont systemFontOfSize:15.0f];
    label5_2.textAlignment = UITextAlignmentRight;
    label5_2.text = @"时间 :";
    [_moreInfoView addSubview:label5_2];
    [label5_2 release];
    
    _textField5 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, moreInfoHeight, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField5.index = 19;
    _textField5.textField.text = [self.vehicleDetailInfo.nextInsuranceTime length] > 0 ? [KKUtils ConvertDataToString:[KKUtils convertStringToDate:self.vehicleDetailInfo.nextInsuranceTime]] : nil;
    _textField5.addtionalInfo = [self.vehicleDetailInfo.nextInsuranceTime length] > 0 ? [KKUtils convertStringToDate:self.vehicleDetailInfo.nextInsuranceTime]: nil;
    UIButton  *button5 = [[UIButton alloc] initWithFrame:_textField5.bounds];
    button5.tag = 15;
    button5.backgroundColor = [UIColor clearColor];
    [button5 addTarget:self action:@selector(textFieldButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_textField5 addSubview:button5];
    [button5 release];
    
    [_moreInfoView addSubview:_textField5];
    [_textField5 release];
    
    moreInfoHeight += 45;
    
    UILabel *label6 = [[UILabel alloc] initWithFrame:CGRectMake(7, moreInfoHeight+3, 60, 16)];
    label6.backgroundColor = [UIColor clearColor];
    label6.textColor = [UIColor blackColor];
    label6.font = [UIFont systemFontOfSize:15.0f];
    label6.textAlignment = UITextAlignmentRight;
    label6.text = @"下次验车";
    [_moreInfoView addSubview:label6];
    [label6 release];
    
    UILabel *label6_2 = [[UILabel alloc] initWithFrame:CGRectMake(10, moreInfoHeight+19, 65, 16)];
    label6_2.backgroundColor = [UIColor clearColor];
    label6_2.textColor = [UIColor blackColor];
    label6_2.font = [UIFont systemFontOfSize:15.0f];
    label6_2.textAlignment = UITextAlignmentRight;
    label6_2.text = @"时间 :";
    [_moreInfoView addSubview:label6_2];
    [label6_2 release];
    
    _textField6 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, moreInfoHeight, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField6.index = 20;
    _textField6.textField.text = [self.vehicleDetailInfo.nextExamineTime length] > 0 ? [KKUtils ConvertDataToString:[KKUtils convertStringToDate:self.vehicleDetailInfo.nextExamineTime]] : nil;
    _textField6.addtionalInfo = [self.vehicleDetailInfo.nextExamineTime length] > 0 ? [KKUtils convertStringToDate:self.vehicleDetailInfo.nextExamineTime] : nil;
    
    UIButton  *button6 = [[UIButton alloc] initWithFrame:_textField6.bounds];
    button6.tag = 16;
    button6.backgroundColor = [UIColor clearColor];
    [button6 addTarget:self action:@selector(textFieldButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_textField6 addSubview:button6];
    [button6 release];
    
    [_moreInfoView addSubview:_textField6];
    [_textField6 release];
    
    moreInfoHeight += 45;
    
    _moreInfoView.frame = CGRectMake(0, orignY2, 320, moreInfoHeight);
    [_mainScrollView addSubview:_moreInfoView];
    [_moreInfoView release];
    
    orignY2 += 18;
    
    image = [UIImage imageNamed:@"bg_setting_bind_btn.png"];
    _submitBtn = [[UIButton alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), orignY2, image.size.width, image.size.height)];
    [_submitBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [_submitBtn setTitle:@"确定" forState:UIControlStateNormal];
    [_submitBtn setBackgroundImage:image forState:UIControlStateNormal];
    [_submitBtn.titleLabel setFont:[UIFont boldSystemFontOfSize:17.f]];
    [_submitBtn addTarget:self action:@selector(sureButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [_mainScrollView addSubview:_submitBtn];
    [_submitBtn release];
    
    orignY2 += 40;
    
    [_mainScrollView setContentSize:CGSizeMake(320, MAX(currentScreenHeight - 44 - 49 - [self getOrignY], orignY2+10))];
    [self.view addSubview:_mainScrollView];
    [_mainScrollView release];

}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = (self.type == KKBindCar_show ) ? @"修改车辆" : @"新增绑定";
    
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
}

- (void)showDataPicker:(NSInteger)index
{
    [self resignVcFirstResponder];
    
    _viewIndex = index;
    
    KKCustomDataPicker *dataPicker = [[KKCustomDataPicker alloc] initWithFrame:self.view.bounds];
    dataPicker.delegate = self;
    [dataPicker show];
    [dataPicker release];
}

- (void) didKeyboardNotification:(NSNotification*)notification
{
    NSString* nName = notification.name;
    NSDictionary* nUserInfo = notification.userInfo;
    if ([nName isEqualToString:UIKeyboardDidShowNotification])
    {
        NSString* sysStr = [[UIDevice currentDevice] systemVersion];
        sysStr = [sysStr substringToIndex:1];
        NSInteger ver = [sysStr intValue];
        if (ver >= 5)
        {
            NSValue* value = [nUserInfo objectForKey:UIKeyboardFrameEndUserInfoKey];
            CGRect rect = CGRectZero;
            [value getValue:&rect];
            float keyboardHeight = rect.size.height;
            [_mainScrollView setFrame:CGRectMake(0, 0, 320, currentScreenHeight - keyboardHeight - 44 - [self getOrignY])];
        }
    }
    if ([nName isEqualToString:UIKeyboardWillHideNotification])
    {
        [_mainScrollView setFrame:self.view.bounds];
    }
}

#pragma mark -
#pragma mark Events

- (void)backButtonClicked
{
    if(self.popViewControllerNum == 0)
        [self.navigationController popViewControllerAnimated:YES];
    else
    {
        [self.navigationController popToViewController:[self.navigationController.viewControllers objectAtIndex:([self.navigationController.viewControllers count]-self.popViewControllerNum-1-1)] animated:YES];
    }
}

- (void)textFieldButtonClicked:(id)sender
{
    [self resignFirstResponder];
    NSInteger tag = ((UIButton *)sender).tag;
    [self showDataPicker:tag];
}

-(void) hideButtonClicked:(UIButton *)btn
{
    self.hiddenMoreInfo = !self.hiddenMoreInfo;
    CGRect rect = _submitBtn.frame;
    
    if(self.hiddenMoreInfo)
    {
        [btn setImage:[UIImage imageNamed:@"icon_showMore.png"] forState:UIControlStateNormal];
        rect.origin.y = _moreInfoView.frame.origin.y + 18;
    }
    else
    {
        [btn setImage:[UIImage imageNamed:@"icon_hiddenMore.png"] forState:UIControlStateNormal];
        rect.origin.y = _moreInfoView.frame.origin.y + _moreInfoView.frame.size.height + 10;
    }
    
    _moreInfoView.hidden = self.hiddenMoreInfo;
    _moreMaskView.hidden = !self.hiddenMoreInfo;
    _submitBtn.frame = rect;
    [_mainScrollView setContentSize:CGSizeMake(320, MAX(currentScreenHeight - 44 - 49 - [self getOrignY], _submitBtn.frame.origin.y + _submitBtn.frame.size.height +10))];
}

- (void)sureButtonClicked
{
    [self resignVcFirstResponder];
    
    NSString *vehicleNum = nilOrString([_textField1.textField.text uppercaseString]);
    vehicleNum = [vehicleNum stringByReplacingOccurrencesOfString:@" " withString:@""];
    
    if ([vehicleNum length] == 0)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请输入车牌号！"];
        return;
    }
    
    NSString *vehicleBrand  = nilOrString(_textField2_1.textField.text);
    if ([vehicleBrand length] == 0)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请选择车辆品牌！"];
        return;
    }
    NSString *vehicleModel = nilOrString(_textField2_2.textField.text);
    if ([vehicleModel length] == 0)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请选择车型"];
        return;
    }
    
    NSString *currentMile = nilOrString(_textField7.textField.text);
    if ([currentMile length] > 0)
    {
        if (![KKHelper KKHElpRegexMatchForFloatValue:currentMile])
        {
            [KKCustomAlertView showAlertViewWithMessage:@"当前里程格式不正确！"];
            return;
        }

        if ([currentMile integerValue] >= 1000000)
        {
            [KKCustomAlertView showAlertViewWithMessage:@"您输入的当前里程数过大，请输入小于1000000的数字！"];
            return;
        }
    }
    
    NSString *nextMaintanceMile = nilOrString(_textField4.textField.text);
    if ([nextMaintanceMile length] > 0)
    {
        if (![KKHelper KKHElpRegexMatchForFloatValue:nextMaintanceMile])
        {
            [KKCustomAlertView showAlertViewWithMessage:@"下次保养里程格式不正确！"];
            return;
        }
        
        if ([nextMaintanceMile integerValue] >= 1000000)
        {
            [KKCustomAlertView showAlertViewWithMessage:@"您输入的下次保养里程数过大，请输入小于1000000的数字！"];
            return;
        }
    }
    
    NSDate *insuranceDate = (NSDate *)_textField5.addtionalInfo;
    NSDate *examineDate = (NSDate *)_textField6.addtionalInfo;
    
    self.vehicleVin = _vehicleVinText.textField.text;
    
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];

    if ([self.obdSN length] != 0)
    {
        if ([currentMile length] > 0 )
        {
            KKModelPreferenceGlobalValue *globalValue  = [KKPreference sharedPreference].globalValues;
            globalValue.currentVehicleMile = currentMile;
            [KKPreference sharedPreference].globalValues = globalValue;
        }
        
        [[KKProtocolEngine sharedPtlEngine] obdBinding:[KKProtocolEngine sharedPtlEngine].userName
                                                 obdSN:self.obdSN
                                            vehicleVin:self.vehicleVin
                                             vehicleId:self.vehicleDetailInfo.vehicleId
                                             vehicleNo:vehicleNum
                                          vehicleModel:vehicleModel
                                        vehicleModelId:(NSString *)_textField2_2.addtionalInfo
                                          vehicleBrand:vehicleBrand
                                        vehicleBrandId:(NSString *)_textField2_1.addtionalInfo
                                            sellShopId:nilOrString(_textField3.addtionalInfo)
                                              engineNo:_engineText.textField.text
                                              registNo:_registText.textField.text
                                   nextMaintainMileage:nilOrString(_textField4.textField.text)
                                     nextInsuranceTime:insuranceDate
                                       nextExamineTime:examineDate
                                        currentMileage:currentMile delegate:self];
        
    }
    else
    {
        [[KKProtocolEngine sharedPtlEngine] vehicleSaveInfo:self.vehicleDetailInfo.vehicleId
                                                 vehicleVin:self.vehicleVin
                                                  vehicleNo:vehicleNum
                                               vehicleModel:vehicleModel
                                             vehicleModelId:(NSString *)_textField2_2.addtionalInfo
                                               vehicleBrand:vehicleBrand
                                             vehicleBrandId:(NSString *)_textField2_1.addtionalInfo
                                                      obdSN:nil
                                             bindingShopId:nilOrString(_textField3.addtionalInfo)
                                                     userNo:[KKProtocolEngine sharedPtlEngine].userName
                                                   engineNo:_engineText.textField.text
                                                   registNo:_registText.textField.text
                                        nextMaintainMileage:nilOrString(_textField4.textField.text)
                                          nextInsuranceTime:insuranceDate
                                            nextExamineTime:examineDate
                                             currentMileage:currentMile delegate:self];

    }
}

- (void)addNewButtonClicked
{
    if (KKAppDelegateSingleton.bleEngine && [KKAppDelegateSingleton.bleEngine supportBLE])
    {
        [KKAppDelegateSingleton getVehicleRealData:NO];
        [KKAppDelegateSingleton.bleEngine disConnectActivePeripheral];
    }
    
    KKSearchCarViewController *Vc = [[KKSearchCarViewController alloc] initWithNibName:@"KKSearchCarViewController" bundle:nil];
    Vc.delegate = self;
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
}

- (void)carBrandModelButtonClicked:(id)sender
{
    [self resignVcFirstResponder];
    
    NSInteger tag = [sender tag];
    
    KKSearchCarModelViewController *Vc = [[KKSearchCarModelViewController alloc] initWithNibName:@"KKSearchCarModelViewController" bundle:nil];
    Vc.delegate = self;
    Vc.haveTabbar = NO;
    if (tag == 21)
        Vc.isBrand = YES;
    else
    {
        Vc.brandID = (NSString *)_textField2_1.addtionalInfo;
        Vc.isBrand = NO;
    }
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];

}

- (void)scanButtonClicked
{
    KKScanViewController *reader = [[KKScanViewController alloc] init];
    reader.delegate = self;
    reader.showsZBarControls = NO;
    reader.tracksSymbols = YES;
    ZBarImageScanner *scanner = reader.scanner;
    [scanner setSymbology: ZBAR_QRCODE
                   config: ZBAR_CFG_ENABLE
                       to: 1];
    [self presentModalViewController: reader
                            animated: YES];
    [reader release];
    
}

#pragma mark -
#pragma mark KKCustomDataPickerDelegate

- (void)KKCustomDataPickerDataSelected:(NSDate *)timeData
{
    NSString *time = [KKUtils ConvertDataToString:timeData];
    
    if (_viewIndex == 15)
    {
        _textField5.textField.text = time;
        _textField5.addtionalInfo = timeData;
    }
    if (_viewIndex == 16)
    {
        _textField6.textField.text = time;
        _textField6.addtionalInfo = timeData;
    }
    
    _viewIndex = 0;
}

#pragma mark -
#pragma mark KKCustomTextFieldDelegate
- (void)KKCustomTextFieldTextDidChanged:(NSString *)string andIndex:(NSInteger)index
{
    if (index == 12 || index == 13)
        [_mainScrollView setContentOffset:CGPointMake(0, 90)];
}

- (void)KKCustomTextFieldBeginEditing
{
    [_mainScrollView setContentOffset:CGPointMake(0, 90)];
}

- (void)KKCustomTextFieldDidEndEditing:(KKCustomTextField *)sender
{
    if (sender.index == 11 && [sender.textField.text length] > 0)
    {
        sender.textField.text = [sender.textField.text uppercaseString];
    }
}

#pragma mark -
#pragma mark KKScanViewControllerDelegate
- (void)KKScanViewControllerSuccessWithResult:(NSArray *)array
{
    _textField3.textField.text= [array objectAtIndex:1];
    _textField3.addtionalInfo = [array objectAtIndex:0];
}

#pragma mark -
#pragma mark KKSearchCarViewControllerDelegate

- (void)KKSearchCarViewControllerTransferInfoByOperation:(NSString *)info
{
    if (info != nil)
    {
        _showAddButtonView.hidden = YES;
        
        self.vehicleVin = info;
        self.obdSN = KKAppDelegateSingleton.currentConnectedPeripheral.systemId;
        _textField0.textField.text = self.obdSN;
        _vehicleVinText.textField.text = self.vehicleVin;
    }
}

#pragma mark -
#pragma mark KKSearchCarModelDelegate

- (void)KKSearchCarModelViewDidSelected:(id)obj isBrand:(BOOL)isbrand
{
    KKModelCarInfo *carInfo = (KKModelCarInfo *)obj;
    
    if (isbrand)
    {
        _textField2_1.textField.text = carInfo.brandName;
        _textField2_1.addtionalInfo = carInfo.brandId;
        
        _textField2_2.textField.text = nil;
        _textField2_2.addtionalInfo = nil;
        
        _carBrandMarkView.hidden = YES;
        
    }
    else
    {
        _textField2_2.textField.text = carInfo.modelName;
        _textField2_2.addtionalInfo = carInfo.modelId;
    
        _carModelMarkView.hidden = YES;
    }
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
    
    [[NSNotificationCenter defaultCenter] postNotificationName:Notification_UpdateVehicleList object:nil];
    KKModelSaveVehicleInfoRsp *proRsp = (KKModelSaveVehicleInfoRsp *)rsp;
    [KKCustomAlertView showAlertViewWithMessage:proRsp.header.desc block:^{
        //[[NSNotificationCenter defaultCenter] postNotificationName:@"refreshBindCarsView" object:nil];
        [self backButtonClicked];
    }];
    
    return KKNumberResultEnd;
}

- (NSNumber *)obdBindResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    NSLog(@"bind view obdsn is %@",self.obdSN);
    [KKAppDelegateSingleton setDefaultObdSN:self.obdSN];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:Notification_UpdateVehicleList object:nil];
    KKModelProtocolRsp *proRsp = (KKModelProtocolRsp *)rsp;
    [KKCustomAlertView showAlertViewWithMessage:proRsp.header.desc block:^{
        //[[NSNotificationCenter defaultCenter] postNotificationName:@"refreshBindCarsView" object:nil];
        [self backButtonClicked];
    }];
    
    return KKNumberResultEnd;
}

- (NSNumber *)vehicleGetInfoResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    KKModelVehicleGetInfoRsp *vehicleInfoRsp = (KKModelVehicleGetInfoRsp *)rsp;
    self.vehicleDetailInfo = vehicleInfoRsp.vehicleInfo;
    self.vehicleVin = self.vehicleDetailInfo.vehicleVin;
    self.obdSN = self.vehicleDetailInfo.obdSN;
    
    [self addScrollView];
    
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
    self.vehicleDetailInfo = nil;
    [super dealloc];
}
@end
