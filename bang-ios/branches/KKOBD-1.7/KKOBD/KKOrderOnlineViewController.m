//
//  KKOrderOnlineViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-27.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKOrderOnlineViewController.h"
#import "KKApplicationDefine.h"
#import "KKViewUtils.h"
#import "UIViewController+extend.h"
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

@interface KKOrderOnlineViewController ()

@end

@implementation KKOrderOnlineViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self setVcEdgesForExtendedLayout];
    [self initVariables];
    [self initComponents];
    
    [[KKProtocolEngine sharedPtlEngine] userInformation:[KKProtocolEngine sharedPtlEngine].userName delegate:self];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self resignKeyboardNotification];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self resignVcFirstResponder];
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
    if ([self.serviceName length] > 0 && [self.detailShopInfo.KKArrayFieldName(productCategoryList,KKModelShopServiceScope) count] > 0)
    {
        for (int t = 0; t < [self.detailShopInfo.KKArrayFieldName(productCategoryList,KKModelShopServiceScope) count]; t++)
        {
            KKModelShopServiceScope *service = [self.detailShopInfo.KKArrayFieldName(productCategoryList,KKModelShopServiceScope) objectAtIndex:t];
            if ([self.serviceName isEqualToString:service.serviceCategoryName])
            {
                self.selectedIndex = t;
                break;
            }
        }
    }
        
}

- (void) initComponents
{
    [self setNavgationBar];
    [self setBackGroundView];
    [self creatScrollView];
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"在线预约";
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

- (void)creatScrollView
{
    _mainScrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, 320, currentScreenHeight - 44 - 49)];
    _mainScrollView.backgroundColor = [UIColor clearColor];
    
    CGPoint startPoint = CGPointMake(10, 10);
    CGSize size = CGSizeZero;
    
    _nameLabel = [[UILabel alloc] initWithFrame:CGRectMake(startPoint.x, startPoint.y , 300, 15)];
    _nameLabel.backgroundColor = [UIColor clearColor];
    _nameLabel.textColor = KKCOLOR_3359ac;
    _nameLabel.textAlignment = UITextAlignmentLeft;
    _nameLabel.font = [UIFont boldSystemFontOfSize:15.f];
    _nameLabel.numberOfLines = 0;
    _nameLabel.text = self.detailShopInfo.name;
    size = [self.detailShopInfo.name sizeWithFont:[UIFont boldSystemFontOfSize:15.f] constrainedToSize:CGSizeMake(300, MAXFLOAT)];
    [_nameLabel setFrame:CGRectMake(startPoint.x, startPoint.y, 300, size.height)];
    [_mainScrollView addSubview:_nameLabel];
    [_nameLabel release];

    
    startPoint.y += 27;
    
    UILabel *label1 = [[UILabel alloc] initWithFrame:CGRectMake(0, startPoint.y + 9, 75, 15)];
    label1.backgroundColor = [UIColor clearColor];
    label1.textColor = [UIColor blackColor];
    label1.font = [UIFont systemFontOfSize:15.0f];
    label1.textAlignment = UITextAlignmentRight;
    label1.text = @"服务类型 :";
    [_mainScrollView addSubview:label1];
    [label1 release];
    
    _textField1 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, startPoint.y, 223, 38) WithType:eTextFieldImage WithPlaceholder:nil WithImage:[UIImage imageNamed:@"icon_shopq_downArrow.png"] WithRightInsetWidth:3];
    _textField1.index = 1;
    _textField1.delegate = self;
    if ([self.detailShopInfo.KKArrayFieldName(productCategoryList,KKModelShopServiceScope) count] > 0)
        _textField1.textField.text = [[self.detailShopInfo.KKArrayFieldName(productCategoryList,KKModelShopServiceScope) objectAtIndex:self.selectedIndex] serviceCategoryName];
    [_mainScrollView addSubview:_textField1];
    [_textField1 release];
    
    UIButton *serviceTypeBtn = [[UIButton alloc] initWithFrame:_textField1.frame];
    [serviceTypeBtn addTarget:self action:@selector(serviceTypeButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [_mainScrollView addSubview:serviceTypeBtn];
    [serviceTypeBtn release];
    
    startPoint.y += 45;
    
    UILabel *label2 = [[UILabel alloc] initWithFrame:CGRectMake(0, startPoint.y + 9, 75, 15)];
    label2.backgroundColor = [UIColor clearColor];
    label2.textColor = [UIColor blackColor];
    label2.font = [UIFont systemFontOfSize:15.0f];
    label2.textAlignment = UITextAlignmentRight;
    label2.text = @"预约时间 :";
    [_mainScrollView addSubview:label2];
    [label2 release];
    
    _textField2 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, startPoint.y, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    NSDate *now = [NSDate date];
    NSString *time = [KKUtils ConvertDataToString:now];
    _textField2.textField.text = time;
    _textField2.addtionalInfo = now;
    [_mainScrollView addSubview:_textField2];
    [_textField2 release];
    
    UIButton *timeBtn = [[UIButton alloc] initWithFrame:_textField2.frame];
    timeBtn.backgroundColor= [UIColor clearColor];
    [timeBtn addTarget:self action:@selector(timeButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [_mainScrollView addSubview:timeBtn];
    [timeBtn release];
    
    startPoint.y += 45;
    
    UILabel *label3 = [[UILabel alloc] initWithFrame:CGRectMake(0, startPoint.y + 9, 75, 15)];
    label3.backgroundColor = [UIColor clearColor];
    label3.textColor = [UIColor blackColor];
    label3.font = [UIFont systemFontOfSize:15.0f];
    label3.textAlignment = UITextAlignmentRight;
    label3.text = @"备注 :";
    [_mainScrollView addSubview:label3];
    [label3 release];
    
    _textField3 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, startPoint.y, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField3.textField.text = self.remarkString;
    [_mainScrollView addSubview:_textField3];
    [_textField3 release];
    
    startPoint.y += 45;
    
    UILabel *label4 = [[UILabel alloc] initWithFrame:CGRectMake(0, startPoint.y + 9, 75, 15)];
    label4.backgroundColor = [UIColor clearColor];
    label4.textColor = [UIColor blackColor];
    label4.font = [UIFont systemFontOfSize:15.0f];
    label4.textAlignment = UITextAlignmentRight;
    label4.text = @"车牌号 :";
    [_mainScrollView addSubview:label4];
    [label4 release];
    
    _textField4 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, startPoint.y, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField4.textField.text = nilOrString(KKAppDelegateSingleton.currentVehicle.vehicleNo);
    [_mainScrollView addSubview:_textField4];
    [_textField4 release];
    
    startPoint.y += 45;
    
    UILabel *label5 = [[UILabel alloc] initWithFrame:CGRectMake(0, startPoint.y + 9, 75, 15)];
    label5.backgroundColor = [UIColor clearColor];
    label5.textColor = [UIColor blackColor];
    label5.font = [UIFont systemFontOfSize:15.0f];
    label5.textAlignment = UITextAlignmentRight;
    label5.text = @"品牌车型 :";
    [_mainScrollView addSubview:label5];
    [label5 release];
    
    _textField5_1 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, startPoint.y, 106, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField5_1.delegate = self;
    _carBrandMarkView = [KKHelper creatCarBrandMarkView:_textField5_1.bounds withTitle:@"品牌"];
    [_textField5_1 addSubview:_carBrandMarkView];
    if ([KKAppDelegateSingleton.currentVehicle.vehicleBrand length] > 0)
    {
        _textField5_1.textField.text = nilOrString(KKAppDelegateSingleton.currentVehicle.vehicleBrand);
        _carBrandMarkView.hidden = YES;
    }
    _textField5_1.addtionalInfo = nilOrString(KKAppDelegateSingleton.currentVehicle.vehicleBrandId);
    
    UIButton *carModelBtn1 = [[UIButton alloc] initWithFrame:_textField5_1.bounds];
    carModelBtn1.backgroundColor = [UIColor clearColor];
    carModelBtn1.tag = 51;
    [carModelBtn1 addTarget:self action:@selector(carModelButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_textField5_1 addSubview:carModelBtn1];
    [carModelBtn1 release];
    
    [_mainScrollView addSubview:_textField5_1];
    [_textField5_1 release];
    
    _textField5_2 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(202, startPoint.y, 106, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _carModelMarkView = [KKHelper creatCarBrandMarkView:_textField5_2.bounds withTitle:@"车型"];
    [_textField5_2 addSubview:_carModelMarkView];
    if ([KKAppDelegateSingleton.currentVehicle.vehicleModel length] > 0)
    {
        _textField5_2.textField.text = nilOrString(KKAppDelegateSingleton.currentVehicle.vehicleModel);
        _carModelMarkView.hidden = YES;
    }
    _textField5_2.addtionalInfo = nilOrString(KKAppDelegateSingleton.currentVehicle.vehicleModelId);
    
    [_mainScrollView addSubview:_textField5_2];
    [_textField5_2 release];
    
    UIButton *carModelBtn2 = [[UIButton alloc] initWithFrame:_textField5_2.bounds];
    carModelBtn2.backgroundColor = [UIColor clearColor];
    carModelBtn2.tag = 52;
    [carModelBtn2 addTarget:self action:@selector(carModelButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_textField5_2 addSubview:carModelBtn2];
    [carModelBtn2 release];
    
    startPoint.y += 45;
    
    UILabel *label6 = [[UILabel alloc] initWithFrame:CGRectMake(0, startPoint.y + 9, 75, 15)];
    label6.backgroundColor = [UIColor clearColor];
    label6.textColor = [UIColor blackColor];
    label6.font = [UIFont systemFontOfSize:15.0f];
    label6.textAlignment = UITextAlignmentRight;
    label6.text = @"联系人 :";
    [_mainScrollView addSubview:label6];
    [label6 release];
    
    _textField6 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, startPoint.y, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField6.textField.text = [KKPreference sharedPreference].userInfo.username;
    [_mainScrollView addSubview:_textField6];
    [_textField6 release];
    
    startPoint.y += 45;
    
    UILabel *label7 = [[UILabel alloc] initWithFrame:CGRectMake(0, startPoint.y + 9, 75, 15)];
    label7.backgroundColor = [UIColor clearColor];
    label7.textColor = [UIColor blackColor];
    label7.font = [UIFont systemFontOfSize:15.0f];
    label7.textAlignment = UITextAlignmentRight;
    label7.text = @"联系方式 :";
    [_mainScrollView addSubview:label7];
    [label7 release];
    
    _textField7 = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, startPoint.y, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _textField7.textField.text = [KKPreference sharedPreference].userInfo.mobile;
    [_mainScrollView addSubview:_textField7];
    [_textField7 release];
    
    startPoint.y += 60;
    
    UIImage *image = [UIImage imageNamed:@"bg_setting_uf_send.png"];
    UIButton *orderBtn = [[UIButton alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), startPoint.y, image.size.width, image.size.height)];
    [orderBtn setBackgroundImage:image forState:UIControlStateNormal];
    [orderBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [orderBtn setTitle:@"预约" forState:UIControlStateNormal];
    [orderBtn.titleLabel setFont:[UIFont systemFontOfSize:17.f]];
    [orderBtn addTarget:self action:@selector(orderButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [_mainScrollView addSubview:orderBtn];
    [orderBtn release];
    
    startPoint.y += 60;
    startPoint.y += 20;
    
    [_mainScrollView setContentSize:CGSizeMake(320, MAX(startPoint.y, (currentScreenHeight - 44 - 49)))];
    
    [self.view addSubview:_mainScrollView];
    [_mainScrollView release];
    
}

#pragma mark -
#pragma mark Events

- (void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)appointmentTimeBtnClicked
{

}

- (void)orderButtonClicked
{
    [self resignVcFirstResponder];
    
//  服务类型   
    NSString *serviceCategory = nilOrString(_textField1.textField.text);
    if ([serviceCategory length] == 0)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请选择服务类型!"];
        return;
    }
    else
    {
        KKModelShopServiceScope *service = [self.detailShopInfo.KKArrayFieldName(productCategoryList,KKModelShopServiceScope) objectAtIndex:self.selectedIndex];
        serviceCategory = service.serviceCategoryId;
    }
    
//  预约时间
    NSString *timeStr = nilOrString(_textField2.textField.text);
    NSDate *date = (NSDate *)_textField2.addtionalInfo;
    
    if ([timeStr length] == 0)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请选择预约时间!"];
        return;
    }
    
    if ([date timeIntervalSinceNow] < 0)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"预约的时间不能早于当前时间!"];
        return;
    }
    
    
//  车牌号
    NSString *vehicleNo = nilOrString([_textField4.textField.text uppercaseString]);
    if ([vehicleNo length] == 0)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请输入车牌号码!"];
        return;
    }
//  联系人
    NSString *contact = nilOrString(_textField6.textField.text);
    if ([contact length] == 0)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请输入联系人！"];
        return;
    }
//  联系号码
    NSString *mobile = nilOrString(_textField7.textField.text);
    if ([mobile length] == 0)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请输入联系方式！"];
        return;
    }

    //  备注
    NSString *remarkStr = nilOrString(_textField3.textField.text);
    
    
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    [[KKProtocolEngine sharedPtlEngine]serviceAppointmentWithShopId:self.detailShopInfo.id
                                                  serviceCategoryId:serviceCategory
                                                        appointTime:date
                                                             mobile:mobile
                                                          vehicleNo:vehicleNo
                                                       vehicleBrand:nilOrString(_textField5_1.textField.text)
                                                     vehicleBrandId:(NSString *)_textField5_1.addtionalInfo
                                                       vehicleModel:nilOrString(_textField5_2.textField.text)
                                                     vehicleModelId:(NSString *)_textField5_2.addtionalInfo
                                                         vehicleVin:nil
                                                             remark:remarkStr
                                                            contact:contact
                                                     faultInfoItems:self.dtcMsgArray
                                                           delegate:self];
    
}

- (void)timeButtonClicked
{
    [self resignVcFirstResponder];
    
    KKCustomDataPicker *dataPicker = [[KKCustomDataPicker alloc] initWithFrame:self.view.frame];
    dataPicker.delegate = self;
    [dataPicker show];
    [dataPicker release];
    
}

- (void)carModelButtonClicked:(id)sender
{
    NSInteger tag = [sender tag];
    
    KKSearchCarModelViewController *Vc = [[KKSearchCarModelViewController alloc] initWithNibName:@"KKSearchCarModelViewController" bundle:nil];
    Vc.delegate = self;
    
    if (tag == 51)
    {
        _selectedBrand = YES;
        Vc.isBrand = YES;
    }
    else
    {
        _selectedBrand = NO;
        Vc.isBrand = NO;
        Vc.brandID = _textField5_1.addtionalInfo;
    }
    
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
}

- (void)serviceTypeButtonClicked
{
    
    if ([self.detailShopInfo.KKArrayFieldName(productCategoryList,KKModelShopServiceScope) count] == 0)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"该店铺没有具体的服务选项，请您选择其他店铺！！！"];
        return;
    }
    
    KKShopFilterPopView *filterPopView = [[KKShopFilterPopView alloc] initWithFrame:self.view.bounds WithArrowOrignX:160 WithRowHeight:33];
    filterPopView.popViewDelegate = self;
    filterPopView.popId = 1000;
    filterPopView.arrowHidden = YES;
    filterPopView.centerView = YES;
    filterPopView.selectedInFirstList = self.selectedIndex;
    NSMutableArray *parr = [NSMutableArray arrayWithCapacity:10];
    NSInteger pid = 1;
    
    for (NSInteger i=0; i < [self.detailShopInfo.KKArrayFieldName(productCategoryList,KKModelShopServiceScope)  count]; i++) {
        KKModelShopServiceScope *service = [self.detailShopInfo.KKArrayFieldName(productCategoryList,KKModelShopServiceScope)  objectAtIndex:i];
        KKPopMenuItem *item = [[KKPopMenuItem alloc] initWithId:pid+i parentId:-1 title:service.serviceCategoryName others:nil];
        [parr addObject:item];
        [item release];
    }
    [filterPopView setLeftDataArray:parr RightDataArray:nil];
    [self.view addSubview:filterPopView];
    [filterPopView release];
    
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
        [_mainScrollView setFrame:CGRectMake(0, 0, 320, currentScreenHeight - 44 - 49)];
    }
}

#pragma mark -
#pragma mark KKCustomDataPickerDelegate

- (void)KKCustomDataPickerDataSelected:(NSDate *)timeData
{
    NSTimeInterval ts = [timeData timeIntervalSince1970];
    ts = (NSInteger)(ts/60) * 60;
    timeData = [NSDate dateWithTimeIntervalSince1970:ts];
    NSString *time = [KKUtils ConvertDataToString:timeData];
    _textField2.textField.text = time;
    _textField2.addtionalInfo = timeData;
}

#pragma mark -
#pragma mark KKCustomTextFieldDelegate

- (void)KKCustomTextFieldButtonClicked:(id)sender
{
}

- (void)KKCustomTextFieldTextDidChanged:(NSString *)string andIndex:(NSInteger)index
{
    
}

- (void)KKCustomTextFieldBeginEditing
{
    
}
#pragma mark -
#pragma mark KKSearchCarModelDelegate

- (void)KKSearchCarModelViewDidSelected:(id)obj isBrand:(BOOL)isbrand
{
    KKModelCarInfo *brandModelObj = (KKModelCarInfo *)obj;
    if (isbrand)
    {
        _textField5_1.textField.text = brandModelObj.brandName;
        _textField5_1.addtionalInfo = brandModelObj.brandId;
        
        _textField5_2.textField.text = nil;
        _textField5_2.addtionalInfo = nil;
        
        _carBrandMarkView.hidden = YES;
    }
    else
    {
        _textField5_2.textField.text = brandModelObj.modelName;
        _textField5_2.addtionalInfo = brandModelObj.modelId;
        
        _carModelMarkView.hidden = YES;
    }
}

#pragma mark -
#pragma mark KKShopFilterPopViewDelegate

- (void)KKShopFilterPopView:(KKShopFilterPopView *)popView WithItem:(KKPopMenuItem *)item AndParentItem:(KKPopMenuItem *)pItem
{
    self.selectedIndex = popView.selectedInFirstList;
    _textField1.textField.text = item.title;
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate

- (NSNumber *)serviceAppointResponse:(NSNumber *)aReqId withObject:(id)aRspObj
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
        [self backButtonClicked];
    }];
    
    return KKNumberResultEnd;
}

- (NSNumber *)userInformationResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
        return KKNumberResultEnd;
    }
    
    KKModelUserInfomationRsp *userInfoRsp = (KKModelUserInfomationRsp *)rsp;
    KKModelPreferenceUserInfo *userInfo = [KKPreference sharedPreference].userInfo;
    userInfo.username = userInfoRsp.userInfo.name;
    userInfo.mobile = userInfoRsp.userInfo.mobile;
    [KKPreference sharedPreference].userInfo = userInfo;
    
    if ([_textField6.textField.text length] == 0)
        _textField6.textField.text = userInfoRsp.userInfo.name;
    if ([_textField7.textField.text length] == 0)
        _textField7.textField.text = userInfo.mobile;
    
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
    self.dtcMsgArray = nil;
    [super dealloc];
}
@end
