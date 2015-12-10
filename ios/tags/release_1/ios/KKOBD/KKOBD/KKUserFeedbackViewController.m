//
//  KKUserFeedbackViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-16.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKUserFeedbackViewController.h"
#import "KKViewUtils.h"
#import "KKApplicationDefine.h"
#import "UIViewController+extend.h"
#import "KKCustomTextField.h"
#import "KKPreference.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "MBProgressHUD.h"
#import "KKUtils.h"

@interface KKUserFeedbackViewController ()

@end

@implementation KKUserFeedbackViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self setVcEdgesForExtendedLayout];
    [self initVariables];
    [self initComponents];
    
    [[KKProtocolEngine sharedPtlEngine] userInformation:[KKProtocolEngine sharedPtlEngine].userName delegate:self];
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
#pragma mark Custom Methods

- (void) initVariables
{
    
}

- (void) initComponents
{
    [self setBachGroundView];
    [self setNavgationBar];
    float orignY = 14;
    UIImage *image = [UIImage imageNamed:@"bg_setting_uf_msg.png"];
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, orignY, image.size.width, image.size.height)];
    bgImv.userInteractionEnabled = YES;
    bgImv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    
    _messageTextView = [[GCPlaceholderTextView alloc] initWithFrame:CGRectMake(30, 10, image.size.width - 60, image.size.height - 20)];
    _messageTextView.delegate = self;
    _messageTextView.keyboardType = UIKeyboardTypeDefault;
    _messageTextView.returnKeyType = UIReturnKeyDone;
    _messageTextView.font = [UIFont systemFontOfSize:15.0f];
    _messageTextView.placeholder = @"欢迎您提出宝贵的意见和建议";
    _messageTextView.textColor = KKCOLOR_A7a6a6;
    _messageTextView.backgroundColor = [UIColor clearColor];
    [bgImv addSubview:_messageTextView];
    [self.view addSubview:bgImv];
    [_messageTextView release];
    [bgImv release];
    
    orignY += image.size.height;
    orignY += 8;
    
    UILabel *titleLb = [[UILabel alloc] initWithFrame:CGRectMake(26, orignY, 150, 15)];
    titleLb.backgroundColor =[UIColor clearColor];
    titleLb.textColor = KKCOLOR_7b7b7b;
    [titleLb setFont:[UIFont systemFontOfSize:15.0f]];
    titleLb.textAlignment = UITextAlignmentLeft;
    titleLb.text = @"请填写联系方式";
    [self.view addSubview:titleLb];
    [titleLb release];
    
    orignY += 24;
    image = [UIImage imageNamed:@"bg_setting_uf_phone.png"];
    
    _inputBgView =[[UIImageView alloc] initWithFrame:CGRectMake(0, orignY, image.size.width, image.size.height)];
    _inputBgView.userInteractionEnabled = YES;
    _inputBgView.image = [image stretchableImageWithLeftCapWidth:5 topCapHeight:5];
    
    _phoneNumTextfield = [[UITextField alloc] initWithFrame:CGRectMake(31, 0, image.size.width - 62, image.size.height)];
    _phoneNumTextfield.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    _phoneNumTextfield.textColor = KKCOLOR_1c1c1c;
    _phoneNumTextfield.delegate = self;
    _phoneNumTextfield.returnKeyType = UIReturnKeyDefault;
    _phoneNumTextfield.textAlignment = UITextAlignmentLeft;
    _phoneNumTextfield.backgroundColor = [UIColor clearColor];
    _phoneNumTextfield.keyboardType = UIKeyboardTypeNumbersAndPunctuation;
    _phoneNumTextfield.text = [KKPreference sharedPreference].userInfo.mobile;
    [_inputBgView addSubview:_phoneNumTextfield];
    [_phoneNumTextfield release];
    [self.view addSubview:_inputBgView];
    [_inputBgView release];
    
    orignY += image.size.height;
    orignY += 10;
    
    image = [UIImage imageNamed:@"bg_setting_uf_send.png"];
    
    UIButton *sendBtn = [[UIButton alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), orignY, image.size.width, image.size.height)];
    [sendBtn setBackgroundImage:image forState:UIControlStateNormal];
    [sendBtn.titleLabel setFont:[UIFont boldSystemFontOfSize:17.0f]];
    [sendBtn setTitle:@"发送" forState:UIControlStateNormal];
    [sendBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [sendBtn addTarget:self action:@selector(sendButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:sendBtn];
    [sendBtn release];
    
    orignY += image.size.height;
    orignY += 12;
    
    image = [UIImage imageNamed:@"bg_setting_uf_line.png"];
    UIImageView *imv = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), orignY, image.size.width, image.size.height)];
    imv.image = image;
    [self.view addSubview:imv];
    [imv release];
    
    orignY += image.size.height;
    orignY += 12;
    image = [UIImage imageNamed:@"bg_setting_uf_send.png"];
    
    UIButton *callBtn = [[UIButton alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), orignY, image.size.width, image.size.height)];
    [callBtn setBackgroundImage:image forState:UIControlStateNormal];
    [callBtn addTarget:self action:@selector(callServiceButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    
    UIImage *iconImage = [UIImage imageNamed:@"bg_setting_uf_call.png"];
    UIImageView *iconImv = [[UIImageView alloc] initWithFrame:CGRectMake(105, 0.5*(image.size.height - iconImage.size.height), iconImage.size.width, iconImage.size.height)];
    iconImv.image = iconImage;
    iconImv.userInteractionEnabled = YES;
    [callBtn addSubview:iconImv];
    [iconImv release];
    
    UILabel *btnlb = [[UILabel alloc] initWithFrame:CGRectMake(131, 0.5*(image.size.height - 17), 150, 17)];
    btnlb.backgroundColor = [UIColor clearColor];
    btnlb.textAlignment = UITextAlignmentLeft;
    btnlb.textColor = [UIColor whiteColor];
    btnlb.text = @"呼叫客服";
    btnlb.font =[UIFont boldSystemFontOfSize:17.0f];
    [callBtn addSubview:btnlb];
    [btnlb release];
    
    [self.view addSubview:callBtn];
    [callBtn release];
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"用户反馈";
    
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
}

#pragma mark -
#pragma mark Events
- (void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)sendButtonClicked
{
    if ([_messageTextView.text length] == 0)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"建议栏不能为空，请输入您的建议！"];
        return;
    }
    
    NSString *phoneStr = nilOrString(_phoneNumTextfield.text);
    if ([phoneStr length] == 0)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请输入您的手机号！"];
        return;
    }
    
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    [[KKProtocolEngine sharedPtlEngine] userFeedback:[KKProtocolEngine sharedPtlEngine].userName content:_messageTextView.text  mobile:phoneStr delegate:self];
}

- (void)callServiceButtonClicked
{
    NSString *telephone = [KKPreference sharedPreference].appConfig.customerServicePhone;
    if ([telephone length] == 0)
        telephone = @"0512-66733331";
    [KKUtils makePhone:telephone];
    
}

- (void) didKeyboardNotification:(NSNotification*)notification
{
    if (iPhone5)
        return;
    
    NSString* nName = notification.name;
    NSDictionary* nUserInfo = notification.userInfo;
    if ([nName isEqualToString:UIKeyboardDidShowNotification])
    {
        NSString* sysStr = [[UIDevice currentDevice] systemVersion];
        sysStr = [sysStr substringToIndex:1];
        NSInteger ver = [sysStr intValue];
        if (ver >= 5)
        {
            if ([_phoneNumTextfield isFirstResponder])
            {
                NSValue* value = [nUserInfo objectForKey:UIKeyboardFrameEndUserInfoKey];
                CGRect rect = CGRectZero;
                [value getValue:&rect];
                float keyboardHeight = rect.size.height;
                CGRect viewRect = [self.view convertRect:_inputBgView.frame toView:self.view];
                rect = self.view.frame;
                float var = (rect.size.height - (viewRect.origin.y + viewRect.size.height));
                rect.origin.y =  var > keyboardHeight ? 0 : - (keyboardHeight - var - (currentSystemVersion >= 7.0 ? 64 : 0));
                [self.view setFrame:rect];
            }
        }
    }
    
    if ([nName isEqualToString:UIKeyboardWillHideNotification])
    {
        CGRect rect = self.view.frame;
        if (currentSystemVersion >= 7.0)
            rect.origin.y = 64;
        else
            rect.origin.y = 0;
        [self.view setFrame:rect];
    }
}

#pragma mark -
#pragma mark UITextFieldDelegate
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

#pragma mark -
#pragma mark UITextViewDelegate
-(BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text
{
    if ([text isEqualToString:@"\n"]) {
        [textView resignFirstResponder];
        return NO;
    }
    return YES;
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate
- (NSNumber *)userFeedbackResponse:(NSNumber *)aReqId withObject:(id)aRspObj
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
    
    if ([_phoneNumTextfield.text length] == 0)
        _phoneNumTextfield.text = userInfoRsp.userInfo.mobile;
    
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
    _inputBgView = nil;
    _messageTextView = nil;
    _phoneNumTextfield = nil;
    
}

- (void)dealloc
{
    _inputBgView = nil;
    _messageTextView = nil;
    _phoneNumTextfield = nil;
    [super dealloc];
}
@end
