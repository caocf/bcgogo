//
//  KKRegisterAccount2ViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-12.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKRegisterAccount2ViewController.h"
#import "UIViewController+extend.h"
#import "KKApplicationDefine.h"
#import "KKViewUtils.h"
#import "KKUtils.h"
#import "KKAppDelegate.h"
#import "KKCustomTextField.h"
#import "KKCustomAlertView.h"
#import "MBProgressHUD.h"
#import "KKSearchCarViewController.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKPreference.h"

@interface KKRegisterAccount2ViewController ()

@end

@implementation KKRegisterAccount2ViewController

#pragma mark -
#pragma mark View Methods

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self setVcEdgesForExtendedLayout];
    [self initVariables];
    [self initComponents];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self resignKeyboardNotification];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    
    self.superVc.vehicleDetail.vehicleNo = nilOrString(_textField1.textField.text);
    self.superVc.vehicleDetail.vehicleBrand = nilOrString(_textField2_1.textField.text);
    self.superVc.vehicleDetail.vehicleBrandId = nilOrString(_textField2_1.addtionalInfo);
    self.superVc.vehicleDetail.vehicleModel = nilOrString(_textField2_2.textField.text);
    self.superVc.vehicleDetail.vehicleModelId = nilOrString(_textField2_2.addtionalInfo);
    self.superVc.vehicleDetail.recommendShopName = nilOrString(_textField3.textField.text);
    self.superVc.vehicleDetail.recommendShopId = nilOrString(_textField3.addtionalInfo);
    self.superVc.vehicleDetail.currentMileage = nilOrString(_textField8.textField.text);
    self.superVc.vehicleDetail.nextMaintainMileage = nilOrString(_textField4.textField.text);
    self.superVc.vehicleDetail.nextInsuranceTime = nilOrString(_textField5.textField.text);
    self.superVc.nextInsuranceTime = _textField5.addtionalInfo;
    self.superVc.vehicleDetail.nextExamineTime = nilOrString(_textField6.textField.text);
    self.superVc.nextExamTime = _textField6.addtionalInfo;
    self.superVc.vehicleDetail.contact = nilOrString(_textField7.textField.text);
    
}
- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [self removeKeyboardNotification];

}


#pragma mark -
#pragma mark Custom Methods

- (void) initVariables
{
    [self initNavTitleView];
}

- (void) initComponents
{
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 320,  self.view.bounds.size.height)];
    bgImv.image = [[UIImage imageNamed:@"bg_background.png"] stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor redColor];
    [self.view addSubview:bgImv];
    [bgImv release];
    
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
    
//    UIImage *image = [UIImage imageNamed:@"icon_register.png"];
//    UIButton *skipBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, image.size.width, image.size.height)];
//    [skipBtn setBackgroundImage:image forState:UIControlStateNormal];
//    [skipBtn addTarget:self action:@selector(skipButtonClicked) forControlEvents:UIControlEventTouchUpInside];
//    [skipBtn setTitle:@"Skip" forState:UIControlStateNormal];
//    [skipBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
//    [skipBtn.titleLabel setFont:[UIFont systemFontOfSize:15.0f]];
//    self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc] initWithCustomView:skipBtn] autorelease];
//    [skipBtn release];
    
    _mainScrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, 320, currentScreenHeight - 44 - [self getOrignY])];
    _mainScrollView.backgroundColor = [UIColor clearColor];
    _mainScrollView.userInteractionEnabled = YES;

//--------------------车牌号码------------------------------------------------------------------------------------------
    
    float orignY1 = 21 ,orignY2 = 10;
    
    UILabel *label1 = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY1, 75, 15)];
    label1.backgroundColor = [UIColor clearColor];
    label1.textColor = [UIColor blackColor];
    label1.font = [UIFont systemFontOfSize:15.0f];
    label1.textAlignment = UITextAlignmentRight;
    label1.text = @"车牌号码 :";
    [_mainScrollView addSubview:label1];
    [label1 release];
    
    _textField1 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField1.index = 10;
    _textField1.textField.text = self.superVc.vehicleDetail.vehicleNo;
    [_mainScrollView addSubview:_textField1];
    [_textField1 release];
    
    orignY1 += 45;
    orignY2 += 45;
    
//--------------------品牌车型------------------------------------------------------------------------------------------
    
    UILabel *label2 = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY1, 75, 15)];
    label2.backgroundColor = [UIColor clearColor];
    label2.textColor = [UIColor blackColor];
    label2.font = [UIFont systemFontOfSize:15.0f];
    label2.textAlignment = UITextAlignmentRight;
    label2.text = @"品牌车型 :";
    [_mainScrollView addSubview:label2];
    [label2 release];
    
    _textField2_1 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 106, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField2_1.index = 11;
    _textField2_1.textField.text = self.superVc.vehicleDetail.vehicleBrand;
    _textField2_1.addtionalInfo = self.superVc.vehicleDetail.vehicleBrandId;
    _carBrandMarkView = [KKHelper creatCarBrandMarkView:_textField2_1.bounds withTitle:@"品牌"];
    if ([_textField2_1.textField.text length] > 0)
        _carBrandMarkView.hidden = YES;
    [_textField2_1 addSubview:_carBrandMarkView];
    [_mainScrollView addSubview:_textField2_1];
    [_textField2_1 release];
    
    UIButton *carBrandBtn = [[UIButton alloc] initWithFrame:_textField2_1.frame];
    [carBrandBtn addTarget:self action:@selector(carModelChooseButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    carBrandBtn.backgroundColor = [UIColor clearColor];
    carBrandBtn.tag = 21;
    [_mainScrollView addSubview:carBrandBtn];
    [carBrandBtn release];
    
    _textField2_2 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(202, orignY2, 106, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField2_2.index = 12;
    _textField2_2.textField.text = self.superVc.vehicleDetail.vehicleModel;
    _textField2_2.addtionalInfo = self.superVc.vehicleDetail.vehicleModelId;
    _carModelMarkView = [KKHelper creatCarBrandMarkView:_textField2_2.bounds withTitle:@"车型"];
    if ([_textField2_2.textField.text length] > 0)
        _carModelMarkView.hidden = YES;
    [_textField2_2 addSubview:_carModelMarkView];
    [_mainScrollView addSubview:_textField2_2];
    [_textField2_2 release];
    
    UIButton *carModelBtn = [[UIButton alloc] initWithFrame:_textField2_2.frame];
    [carModelBtn addTarget:self action:@selector(carModelChooseButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
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
    label3.text = @"扫描店铺 :";
    [_mainScrollView addSubview:label3];
    [label3 release];
    
    _textField3 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 223, 38) WithType:eTextFieldImage WithPlaceholder:nil WithImage:[UIImage imageNamed:@"icon_scan.png"] WithRightInsetWidth:10.f];
    _textField3.textField.minimumFontSize = 10;
    _textField3.index = 13;
    _textField3.textField.text = self.superVc.vehicleDetail.recommendShopName;
    _textField3.addtionalInfo = self.superVc.vehicleDetail.recommendShopId;
    
    UIButton *button3 = [[UIButton alloc] initWithFrame:_textField3.bounds];
    button3.backgroundColor = [UIColor clearColor];
    [button3 addTarget:self action:@selector(scanButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [_textField3 addSubview:button3];
    [button3 release];
    
    [_mainScrollView addSubview:_textField3];
    [_textField3 release];

    
    orignY1 += 45;
    orignY2 += 45;
    
//--------------------当前里程------------------------------------------------------------------------------------------
    
    UILabel *label8 = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY1, 75, 15)];
    label8.backgroundColor = [UIColor clearColor];
    label8.textColor = [UIColor blackColor];
    label8.font = [UIFont systemFontOfSize:15.0f];
    label8.textAlignment = UITextAlignmentRight;
    label8.text = @"当前里程 :";
    [_mainScrollView addSubview:label8];
    [label8 release];
    
    _textField8 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0.0f];
    _textField8.index = 14;
    _textField8.textField.text = self.superVc.vehicleDetail.currentMileage;
    _textField8.textField.keyboardType = UIKeyboardTypeNumbersAndPunctuation;
    [_mainScrollView addSubview:_textField8];
    [_textField8 release];

    
    orignY1 += 45;
    orignY2 += 45;

//--------------------下次保养里程------------------------------------------------------------------------------------------
    
    UILabel *label4 = [[UILabel alloc] initWithFrame:CGRectMake(7, orignY1 - 8, 60, 16)];
    label4.backgroundColor = [UIColor clearColor];
    label4.textColor = [UIColor blackColor];
    label4.font = [UIFont systemFontOfSize:15.0f];
    label4.textAlignment = UITextAlignmentRight;
    label4.text = @"下次保养";
    [_mainScrollView addSubview:label4];
    [label4 release];
    
    UILabel *label4_2 = [[UILabel alloc] initWithFrame:CGRectMake(10, orignY1+ 8, 65, 16)];
    label4_2.backgroundColor = [UIColor clearColor];
    label4_2.textColor = [UIColor blackColor];
    label4_2.font = [UIFont systemFontOfSize:15.0f];
    label4_2.textAlignment = UITextAlignmentRight;
    label4_2.text = @"里程 :";
    [_mainScrollView addSubview:label4_2];
    [label4_2 release];
    
    _textField4 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField4.index = 15;
    _textField4.textField.text = self.superVc.vehicleDetail.nextMaintainMileage;
    _textField4.textField.keyboardType = UIKeyboardTypeNumbersAndPunctuation;
    [_mainScrollView addSubview:_textField4];
    [_textField4 release];
    
    orignY1 += 45;
    orignY2 += 45;
    
//--------------------下次保险时间------------------------------------------------------------------------------------------
    
    UILabel *label5 = [[UILabel alloc] initWithFrame:CGRectMake(7, orignY1 - 8, 60, 16)];
    label5.backgroundColor = [UIColor clearColor];
    label5.textColor = [UIColor blackColor];
    label5.font = [UIFont systemFontOfSize:15.0f];
    label5.textAlignment = UITextAlignmentRight;
    label5.text = @"下次保险";
    [_mainScrollView addSubview:label5];
    [label5 release];
    
    UILabel *label5_2 = [[UILabel alloc] initWithFrame:CGRectMake(10, orignY1+ 8, 65, 16)];
    label5_2.backgroundColor = [UIColor clearColor];
    label5_2.textColor = [UIColor blackColor];
    label5_2.font = [UIFont systemFontOfSize:15.0f];
    label5_2.textAlignment = UITextAlignmentRight;
    label5_2.text = @"时间 :";
    [_mainScrollView addSubview:label5_2];
    [label5_2 release];
    
    _textField5 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField5.index = 16;
    _textField5.textField.text = self.superVc.vehicleDetail.nextInsuranceTime;
    _textField5.addtionalInfo = self.superVc.nextInsuranceTime;
    UIButton  *button5 = [[UIButton alloc] initWithFrame:_textField5.bounds];
    button5.tag = 16;
    button5.backgroundColor = [UIColor clearColor];
    [button5 addTarget:self action:@selector(textFieldButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_textField5 addSubview:button5];
    [button5 release];
    
    [_mainScrollView addSubview:_textField5];
    [_textField5 release];
    
    orignY1 += 45;
    orignY2 += 45;
 
//--------------------下次验车时间------------------------------------------------------------------------------------------
    
    UILabel *label6 = [[UILabel alloc] initWithFrame:CGRectMake(7, orignY1 - 11, 60, 16)];
    label6.backgroundColor = [UIColor clearColor];
    label6.textColor = [UIColor blackColor];
    label6.font = [UIFont systemFontOfSize:15.0f];
    label6.textAlignment = UITextAlignmentRight;
    label6.text = @"下次验车";
    [_mainScrollView addSubview:label6];
    [label6 release];
    
    UILabel *label6_2 = [[UILabel alloc] initWithFrame:CGRectMake(10, orignY1+ 8, 65, 16)];
    label6_2.backgroundColor = [UIColor clearColor];
    label6_2.textColor = [UIColor blackColor];
    label6_2.font = [UIFont systemFontOfSize:15.0f];
    label6_2.textAlignment = UITextAlignmentRight;
    label6_2.text = @"时间 :";
    [_mainScrollView addSubview:label6_2];
    [label6_2 release];
    
    _textField6 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField6.index = 17;
    _textField6.textField.text = self.superVc.vehicleDetail.nextExamineTime;
    _textField6.addtionalInfo = self.superVc.nextExamTime;
    
    UIButton  *button6 = [[UIButton alloc] initWithFrame:_textField6.bounds];
    button6.tag = 17;
    button6.backgroundColor = [UIColor clearColor];
    [button6 addTarget:self action:@selector(textFieldButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_textField6 addSubview:button6];
    [button6 release];
    
    [_mainScrollView addSubview:_textField6];
    [_textField6 release];
    
    orignY1 += 45;
    orignY2 += 45;
//--------------------代办人------------------------------------------------------------------------------------------
    
    UILabel *label7 = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY1, 75, 15)];
    label7.backgroundColor = [UIColor clearColor];
    label7.textColor = [UIColor blackColor];
    label7.font = [UIFont systemFontOfSize:15.0f];
    label7.textAlignment = UITextAlignmentRight;
    label7.text = @"代办人 :";
    [_mainScrollView addSubview:label7];
    [label7 release];
    
    _textField7 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField7.index = 18;
    _textField7.textField.text = self.superVc.vehicleDetail.contact;
    [_mainScrollView addSubview:_textField7];
    [_textField7 release];
    
    orignY2 += 55;

//--------------------提交按钮------------------------------------------------------------------------------------------
    
    UIImage *image = [UIImage imageNamed:@"icon_submitBtn.png"];
    
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), orignY2, image.size.width, image.size.height)];
    [button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [button setTitle:@"提交" forState:UIControlStateNormal];
    [button.titleLabel setFont:[UIFont systemFontOfSize:15.0f]];
    [button setBackgroundColor:[UIColor clearColor]];
    [button setBackgroundImage:image forState:UIControlStateNormal];
    [button addTarget:self action:@selector(submitButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [_mainScrollView addSubview:button];
    [button release];
    
    orignY2 += 80;
    
    [_mainScrollView setContentSize:CGSizeMake(320, MAX(orignY2, currentScreenHeight - 44 - [self getOrignY]))];
    [self.view addSubview:_mainScrollView];
    [_mainScrollView release];
    
}

- (void)initNavTitleView
{
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    [self setNavigationBarTitle:@"用户注册"];
}

- (void)showDataPicker:(NSInteger)index
{
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
            KKCustomTextField *textFieldView = [self getCurrentFirstResponderTextFieldFromView:_mainScrollView];
            CGRect sRect = _mainScrollView.frame;
            float height = currentScreenHeight - 44 - [self getOrignY] - keyboardHeight;
            sRect.size.height = height;
            [_mainScrollView setFrame:CGRectMake(sRect.origin.x, 0, sRect.size.width, height)];
            [_mainScrollView setContentOffset:CGPointZero animated:NO];
            if (!CGRectContainsPoint(sRect, CGPointMake(textFieldView.frame.origin.x, textFieldView.frame.origin.y + textFieldView.frame.size.height)) ) {
                    [_mainScrollView scrollRectToVisible:textFieldView.frame animated:YES];
            }
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
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)skipButtonClicked
{
    [KKAppDelegateSingleton ShowRootView];
}

- (void)submitButtonClicked
{
    [self resignVcFirstResponder];
    
    if ([[_textField1.textField.text uppercaseString] length] > 0)
    {
        if ([_textField2_2.textField.text length] ==0 )
        {
            [KKCustomAlertView showAlertViewWithMessage:@"请选择车辆车型！"];
            return;
        }
        
        if ([_textField2_1.textField.text length] ==0 )
        {
            [KKCustomAlertView showAlertViewWithMessage:@"请选择车辆品牌！"];
            return;
        }
    }
    
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    
    [[KKProtocolEngine sharedPtlEngine] registerWithUserNo:self.aAccountName
                                                  password:self.aPasswordNum
                                                    mobile:self.aPhoneNum
                                                      name:self.aUserName
                                                 vehicleNo:nilOrString([_textField1.textField.text uppercaseString])
                                              vehicleModel:nilOrString(_textField2_2.textField.text)
                                            vehicleModelId:(NSString *)_textField2_2.addtionalInfo
                                              vehicleBrand:nilOrString(_textField2_1.textField.text)
                                            vehicleBrandId:(NSString *)_textField2_1.addtionalInfo
                                       nextMaintainMileage:[nilOrString(_textField4.textField.text) integerValue]
                                         nextInsuranceTime:[_textField5.textField.text length] > 0 ? (NSDate *)_textField5.addtionalInfo : nil
                                           nextExamineTime:[_textField6.textField.text length] > 0 ? (NSDate *)_textField6.addtionalInfo : nil
                                            currentMileage:nilOrString(_textField8.textField.text)
                                                    shopId:(NSString *)_textField3.addtionalInfo
                                              shopEmployee:nilOrString(_textField7.textField.text)
                                                 loginInfo:[KKHelper getMobilePlatform]
                                                  delegate:self];
}

- (void)carModelChooseButtonClicked:(id)sender
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

- (void)textFieldButtonClicked:(id)sender
{
    [self resignVcFirstResponder];
    
    NSInteger tag = ((UIButton *)sender).tag;
    [self showDataPicker:tag];
}

- (void)registerSuccess
{
    KKSearchCarViewController *Vc = [[KKSearchCarViewController alloc] initWithNibName:@"KKSearchCarViewController" bundle:nil];
    Vc.isFromRegister = YES;
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
}

#pragma mark -
#pragma mark KKCustomDataPickerDelegate

- (void)KKCustomDataPickerDataSelected:(NSDate *)timeData
{
    NSString *time = [KKUtils ConvertDataToString:timeData];
    
    if (_viewIndex == 16)
    {
        _textField5.textField.text = time;
        _textField5.addtionalInfo = timeData;
    }
    if (_viewIndex == 17)
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
     [_mainScrollView setContentOffset:CGPointMake(0, 50)];
}

- (void)KKCustomTextFieldBeginEditing
{
    [_mainScrollView setContentOffset:CGPointMake(0, 50)];
}

#pragma mark -
#pragma mark KKScanViewControllerDelegate

- (void)KKScanViewControllerSuccessWithResult:(NSArray *)array
{
    _textField3.textField.text= [array objectAtIndex:1];
    _textField3.addtionalInfo = [array objectAtIndex:0];
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

- (NSNumber *)userRegisterResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    KKModelProtocolRsp *proRsp = (KKModelProtocolRsp *)rsp;
    
    KKModelPreferenceUserInfo *userInfo = [KKPreference sharedPreference].userInfo;
    userInfo.userNo = self.aAccountName;
    userInfo.password = self.aPasswordNum;
    userInfo.mobile = self.aPhoneNum;
    userInfo.username = self.aUserName;
    [KKPreference sharedPreference].userInfo = userInfo;
    
    [KKCustomAlertView showAlertViewWithMessage:proRsp.header.desc block:^{
        if ([_textField1.textField.text length] == 0 || [_textField2_1.textField.text length] == 0 || [_textField2_2.textField.text length] == 0)
            [KKAppDelegateSingleton ShowRootView];
        else
        {
            [self registerSuccess];
        }
    }];
    
    return KKNumberResultEnd;
}

#pragma mark -
#pragma mark Handle Memory Methods

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    _textField1 = nil;
    _textField2_1 = nil;
    _textField2_2 = nil;
    _textField3 = nil;
    _textField4 = nil;
    _textField5 = nil;
    _textField6 = nil;
    _textField7 = nil;
    _textField8 = nil;
    _mainScrollView = nil;
}

- (void)dealloc
{
    _textField1 = nil;
    _textField2_1 = nil;
    _textField2_2 = nil;
    _textField3 = nil;
    _textField4 = nil;
    _textField5 = nil;
    _textField6 = nil;
    _textField7 = nil;
    _textField8 = nil;
    _mainScrollView = nil;
    [super dealloc];
}
@end
