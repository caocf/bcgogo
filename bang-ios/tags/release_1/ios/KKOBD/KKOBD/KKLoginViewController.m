//
//  KKLoginViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-12.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKLoginViewController.h"
#import "UIViewController+extend.h"
#import "KKAppDelegate.h"
#import "KKViewUtils.h"
#import "KKRegisterAccountViewController.h"
#import "KKApplicationDefine.h"
#import "KKWaittingView.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKPreference.h"

@interface KKLoginViewController ()

@end

@implementation KKLoginViewController

#pragma mark -
#pragma mark View methods

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self.navigationController setNavigationBarHidden:YES animated:NO];
    [self initVariables];
    [self initComponents];
    
    KKModelPreferenceUserInfo *userInfo = [KKPreference sharedPreference].userInfo;
    if ([userInfo.userNo length] > 0 && [userInfo.password length] > 0)
    {
        _accountTextField.textField.text = userInfo.userNo;
        _passwordTextField.textField.text = userInfo.password;
//        [self loginButtonClicked];
    }
}

- (void)viewWillAppear:(BOOL)animated
{
    if (!self.navigationController.navigationBarHidden)
        [self.navigationController setNavigationBarHidden:YES animated:YES];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self resignKeyboardNotification];
}
- (void)viewWillDisappear:(BOOL)animated
{
    [self.navigationController setNavigationBarHidden:NO animated:YES];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [self removeKeyboardNotification];
}
#pragma mark -
#pragma mark Custom methods

- (void)initNavTitleView
{
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    [self setNavigationBarTitle:@"登录"];
}

- (void) initVariables
{
    _receiveKeyboardNotification = YES;
}

- (void) initComponents
{
    float orignY = [self getOrignY] + 20;
    
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0,0, 320,  currentScreenHeight)];
    bgImv.image = [[UIImage imageNamed:@"bg_login.jpg"] stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor clearColor];
    [self.view addSubview:bgImv];
    [bgImv release];
    
    UIImage *image = [UIImage imageNamed:@"logo.png"];
    UIImageView *logoImv = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width),orignY, image.size.width, image.size.height)];
    logoImv.userInteractionEnabled = YES;
    logoImv.image = image;
    [self.view addSubview:logoImv];
    [logoImv release];
    
    orignY += (image.size.height + 29);
    
    image = [UIImage imageNamed:@"bg_inputFieldOflogin.png"];
    UIImageView *bg_inputField = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), orignY, image.size.width, image.size.height)];
    bg_inputField.userInteractionEnabled = YES;
    bg_inputField.image = image;
    
    _accountTextField = [[KKCustomTextField alloc] initWithFrame:CGRectMake(0, 0, image.size.width, 0.5*image.size.height)
                                                        WithType:eTextFieldImage
                                                 WithPlaceholder:@"用户名"
                                                       WithImage:[UIImage imageNamed:@"icon_loginAccount.png"]
                                             WithRightInsetWidth:5];
    _accountTextField.textField.returnKeyType = UIReturnKeyNext;
    _accountTextField.textField.delegate = nil;
    _accountTextField.textField.delegate = self;
    _accountTextField.backgroundColor = [UIColor clearColor];
    [bg_inputField addSubview:_accountTextField];
    [_accountTextField release];
    
    _passwordTextField = [[KKCustomTextField alloc] initWithFrame:CGRectMake(0, 0.5*image.size.height, image.size.width, 0.5*image.size.height)
                                                        WithType:eTextFieldImage
                                                 WithPlaceholder:@"密码"
                                                       WithImage:[UIImage imageNamed:@"icon_loginPassword.png"]
                                             WithRightInsetWidth:5];
    _passwordTextField.backgroundColor = [UIColor clearColor];
    _passwordTextField.textField.delegate = nil;
    _passwordTextField.textField.delegate = self;
    _passwordTextField.textField.secureTextEntry = YES;
    _passwordTextField.textField.returnKeyType = UIReturnKeyDone;
    [bg_inputField addSubview:_passwordTextField];
    [_passwordTextField release];
    
    [self.view addSubview:bg_inputField];
    [bg_inputField release];
    
    orignY += (image.size.height + 19);
    
    image = [UIImage imageNamed:@"icon_loginBtn.png"];
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), orignY, image.size.width, image.size.height)];
    [button setTitle:@"登录" forState:UIControlStateNormal];
    [button setBackgroundImage:image forState:UIControlStateNormal];
    [button addTarget:self action:@selector(loginButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:button];
    [button release];
    
    orignY = currentScreenHeight - 22.5 - 15;
    
    UIButton *fgpwBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [fgpwBtn setFrame:CGRectMake(160- 60 -10 -5, orignY, 60, 15)];
    [fgpwBtn.titleLabel setFont:[UIFont systemFontOfSize:15.0]];
    [fgpwBtn setTitle:@"忘记密码" forState:UIControlStateNormal];
    [fgpwBtn setBackgroundColor:[UIColor clearColor]];
    [fgpwBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [fgpwBtn addTarget:self action:@selector(forgetButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:fgpwBtn];
    
    image = [UIImage imageNamed:@"icon_login_separateLine.png"];
    UIImageView *separateLine = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), orignY + 2, image.size.width, image.size.height)];
    separateLine.image = image;
    [self.view addSubview:separateLine];
    [separateLine release];
    
    UIButton *registerBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [registerBtn setFrame:CGRectMake(160+15, orignY, 60, 15)];
    [registerBtn.titleLabel setFont:[UIFont systemFontOfSize:15.0]];
    [registerBtn setTitle:@"注册账号" forState:UIControlStateNormal];
    [registerBtn setBackgroundColor:[UIColor clearColor]];
    [registerBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [registerBtn addTarget:self action:@selector(resignButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:registerBtn];
    
}
#pragma mark -
#pragma mark Events

- (void)resignButtonClicked
{
    KKRegisterAccountViewController *Vc = [[KKRegisterAccountViewController alloc] initWithNibName:@"KKRegisterAccountViewController" bundle:nil];
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
}

- (void)loginButtonClicked
{
    [self resignVcFirstResponder];
    
    NSString *errorStr = nil;
    if ([_accountTextField.textField.text length] == 0 || [_passwordTextField.textField.text length] == 0)
    {
        if ([_accountTextField.textField.text length] == 0 && [_passwordTextField.textField.text length] == 0)
            errorStr = @"请输入用户名和密码";
        else if ([_accountTextField.textField.text length] == 0)
            errorStr = @"请输入用户名!";
        else if ([_passwordTextField.textField.text length] == 0)
            errorStr = @"请输入密码!";
        
        [KKCustomAlertView showAlertViewWithMessage:errorStr];
        
        return;
    }
    
    _hudProgressView = [[MBProgressHUD alloc] initWithView:self.view];
    _hudProgressView.delegate = self;
	_hudProgressView.labelText = @"正在登录";
	_hudProgressView.minSize = CGSizeMake(135.f, 135.f);
    [_hudProgressView show:YES];
    [self.view addSubview:_hudProgressView];
    [_hudProgressView release];
    
    [[KKProtocolEngine sharedPtlEngine] userLoginWithUser:[NSString stringWithFormat:@"%@",_accountTextField.textField.text] password:[NSString stringWithFormat:@"%@",_passwordTextField.textField.text] platform:CurrentSystemPlatform platformVersion:CurrentSystemVersion mobileModel:[KKHelper platformString] appVersion:KK_Version imageVersion:[KKHelper imageVersion] delegate:self];
}

- (void)forgetButtonClicked
{
    _receiveKeyboardNotification = NO;
    
    KKRetrievePasswordView *rpView = [[KKRetrievePasswordView alloc] initWithFrame:self.view.frame];
    rpView.delegate = self;
    [rpView show];
    [rpView release];
}

- (void)removeWaittingView
{
    [_waittingView hide];
}

- (void) didKeyboardNotification:(NSNotification*)notification
{
    if (!_receiveKeyboardNotification)
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
            NSValue* value = [nUserInfo objectForKey:UIKeyboardFrameEndUserInfoKey];
            CGRect rect = CGRectZero;
            [value getValue:&rect];
            float keyboardHeight = rect.size.height;
            CGRect viewRect = [_passwordTextField convertRect:_accountTextField.frame toView:self.view];
            rect = self.view.frame;
            float var = (currentScreenHeight - (viewRect.origin.y + viewRect.size.height));
            rect.origin.y =  var > keyboardHeight ? 0 : - (keyboardHeight - var);
            [self.view setFrame:rect];
        }
    }
    if ([nName isEqualToString:UIKeyboardWillHideNotification])
    {
        CGRect rect = self.view.frame;
        rect.origin.y = 0;
        [self.view setFrame:rect];
    }
}

#pragma mark -
#pragma mark UITextFieldDelegate
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (textField.returnKeyType == UIReturnKeyNext)
    {
        [textField resignFirstResponder];
        [_passwordTextField.textField becomeFirstResponder];
    }
    else
        [textField resignFirstResponder];
    
    return YES;
}

#pragma mark -
#pragma mark KKRetrievePasswordViewDelegate

- (void)KKRetrievePasswordViewCancelButtonClicked
{
    _receiveKeyboardNotification = YES;
}

- (void)KKRetrievePasswordViewSureButtonClicked:(NSString *)text
{
    _receiveKeyboardNotification = YES;
    
    _hudProgressView = [[MBProgressHUD alloc] initWithView:self.view];
    _hudProgressView.delegate = self;
    _hudProgressView.labelText = @"正在找回...";
	_hudProgressView.minSize = CGSizeMake(135.f, 135.f);
    [self.view addSubview:_hudProgressView];
    [_hudProgressView show:YES];
    [_hudProgressView release];

    [[KKProtocolEngine sharedPtlEngine] userPassword:text delegate:self];

}

#pragma mark -
#pragma mark MBProgressHUDDelegate

- (void)hudWasHidden:(MBProgressHUD *)hud
{
    [_hudProgressView removeFromSuperview];
    _hudProgressView = nil;
}


#pragma mark -
#pragma mark KKProtocolEngineDelegate

- (NSNumber *)userLoginResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [_hudProgressView hide:YES];
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    KKModelLoginRsp *loginRsp = (KKModelLoginRsp *)rsp;
    [KKPreference sharedPreference].appConfig = loginRsp.appConfig;
    [KKPreference sharedPreference].globalValues = nil;
    
    KKModelPreferenceUserInfo *userInfo = [KKPreference sharedPreference].userInfo;
    userInfo.userNo = [NSString stringWithFormat:@"%@",_accountTextField.textField.text];
    userInfo.password = [NSString stringWithFormat:@"%@",_passwordTextField.textField.text];
    [KKPreference sharedPreference].userInfo = userInfo;
    
    KKAppDelegateSingleton.loginRsp = loginRsp;
    [KKAppDelegateSingleton detachVehicleListAndObdList];
    [KKAppDelegateSingleton ShowRootView];
    
    return KKNumberResultEnd;
}

- (NSNumber *)userPasswordResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSObject *rsp = (NSObject*)aRspObj;
    [_hudProgressView hide:YES];
    
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
        
		return KKNumberResultEnd;
	}
    KKModelProtocolRsp *proRsp = (KKModelProtocolRsp *)rsp;
    [KKCustomAlertView showAlertViewWithMessage:proRsp.header.desc];
    
    return KKNumberResultEnd;
}

#pragma mark -
#pragma mark Handle memory methods

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
