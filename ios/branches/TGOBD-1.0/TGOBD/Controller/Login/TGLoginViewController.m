//
//  TGLoginViewController.m
//  TGOBD
//
//  Created by Jiahai on 14-3-11.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGLoginViewController.h"
#import "TGScanViewController.h"
#import "TGRegisterViewController.h"
#import "TGVehicleManageViewController.h"
#import "TGAppDelegate.h"
#import "TGMessageRollingHandler.h"
#import "TGForgetPwdViewController.h"
#import "PopoverView.h"

@interface TGLoginViewController ()

@end

@implementation TGLoginViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewWillAppear:(BOOL)animated
{
    self.navigationController.navigationBar.hidden = YES;
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self registerKeyboardNotification];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    self.navigationController.navigationBar.hidden = NO;
    [self removeKeyboardNotification];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    CGFloat originY = [self getViewLayoutStartOriginY];
    CGFloat height = [self getViewHeight];
    
    UIImageView *bgImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, originY, screenWidth, height)];
    bgImageView.image = [UIImage imageNamed:@"bg_login.png"];
    [self.view addSubview:bgImageView];
    
    CGFloat sOriginY = 0;
//    [scrollView setFrame:CGRectMake(0, originY, screenWidth, height)];
    [scrollView setContentSize:CGSizeMake(screenWidth, height)];
    scrollView.bounces = NO;
    [self.view addSubview:scrollView];
    
    sOriginY = height - 50;
    
    UIButton *fgpwBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [fgpwBtn setFrame:CGRectMake(160- 60 -10 -5, sOriginY, 60, 32)];
    [fgpwBtn.titleLabel setFont:[UIFont systemFontOfSize:15.0]];
    [fgpwBtn setTitle:@"忘记密码" forState:UIControlStateNormal];
    [fgpwBtn setBackgroundColor:[UIColor clearColor]];
    [fgpwBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [fgpwBtn addTarget:self action:@selector(forgetButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [scrollView addSubview:fgpwBtn];
    
    UIImage *image = [UIImage imageNamed:@"icon_login_separateLine.png"];
    UIImageView *separateLine = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), sOriginY + 2, image.size.width, image.size.height)];
    separateLine.image = image;
    [scrollView addSubview:separateLine];
    
    UIButton *registerBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [registerBtn setFrame:CGRectMake(160+15, sOriginY, 60, 32)];
    [registerBtn.titleLabel setFont:[UIFont systemFontOfSize:15.0]];
    [registerBtn setTitle:@"注册账号" forState:UIControlStateNormal];
    [registerBtn setBackgroundColor:[UIColor clearColor]];
    [registerBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [registerBtn addTarget:self action:@selector(registerButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [scrollView addSubview:registerBtn];
    
    userNoText.text = [[NSUserDefaults standardUserDefaults] objectForKey:USER_MOBILE];
    
    userNoText.delegate = self;
    passwordText.delegate = self;
    
    //忘记密码
    forgetPwdView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 280, 200)];
    forgetPwdView.backgroundColor = [UIColor clearColor];
    
    UILabel *label1 = [[UILabel alloc] initWithFrame:CGRectMake(12, 4, 260, 16)];
    label1.backgroundColor = [UIColor clearColor];
    label1.font = [UIFont systemFontOfSize:16];
    label1.textColor = [UIColor whiteColor];
    label1.text = @"请输入您的注册手机号";
    [forgetPwdView addSubview:label1];
    
    forgetText = [[UITextField alloc] init];
    forgetText.delegate = self;
    [forgetText setTextColor:[UIColor whiteColor]];
    if (systemVersionAboveiOS7) {
        [forgetText setTintColor:[UIColor whiteColor]];
    }
    forgetText.layer.masksToBounds = YES;
    forgetText.layer.cornerRadius = 18;
    forgetText.layer.borderColor = [[UIColor whiteColor] CGColor];
    forgetText.layer.borderWidth = 1.0f;
    forgetText.frame = CGRectMake(12, 32, 256, 36);
    [forgetPwdView addSubview:forgetText];
    
    UILabel *label2 = [[UILabel alloc] initWithFrame:CGRectMake(12, 80, 260, 32)];
    label2.backgroundColor = [UIColor clearColor];
    label2.font = [UIFont systemFontOfSize:16];
    label2.textColor = [UIColor whiteColor];
    label2.numberOfLines = 0;
    label2.text = @"您的密码将会被重置，新密码将会发送到您的手机中。";
    [forgetPwdView addSubview:label2];
    [label2 sizeToFit];
    
    UIButton *submitForgetBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    submitForgetBtn.frame = CGRectMake(12, 140, 260, 42);
    [submitForgetBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [submitForgetBtn setTitle:@"发送新密码" forState:UIControlStateNormal];
    [submitForgetBtn setBackgroundImage:[[UIImage imageNamed:@"bg_button_blue.png"] resizableImageWithCapInsets:UIEdgeInsetsMake(30, 60, 30, 60) resizingMode:UIImageResizingModeStretch] forState:UIControlStateNormal];
    [submitForgetBtn addTarget:self action:@selector(submitForgetButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [forgetPwdView addSubview:submitForgetBtn];
}

- (void)didKeyboardNotification:(NSNotification *)notification
{
    [self keyboardHeightChangedToMoveView:scrollView notification:notification];
}

#pragma mark - Event
- (IBAction)loginButtonClicked:(id)sender {
    [TGProgressHUD show];
    [[TGHTTPRequestEngine sharedInstance] userLogin:userNoText.text password:passwordText.text viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if([self httpResponseCorrect:responseObject])
        {
            TGModelLoginRsp *rsp = (TGModelLoginRsp *)responseObject;
            [TGDataSingleton sharedInstance].shopInfo = rsp.shopDTO;
            [TGDataSingleton sharedInstance].vehicleInfo = rsp.appVehicleDTO;
            [TGDataSingleton sharedInstance].userInfo = rsp.appUserDTO;
            //保存用户名
            [[NSUserDefaults standardUserDefaults] setObject:[TGDataSingleton sharedInstance].userInfo.mobile forKey:USER_MOBILE];
            
            [TGMessageRollingHandler startRolling];
            [TGAppDelegateSingleton showRootView];
        }
    } failure:self.faultBlock];
}

- (void)forgetButtonClicked:(UIButton *)sender
{
    //隐藏键盘
    [self.view endEditing:YES];
//    [popoverView showAtPoint:CGPointMake(sender.center.x, sender.center.y - 10) inView:scrollView withContentView:forgetPwdView];
    [PopoverView showPopoverAtPoint:CGPointMake(sender.center.x, sender.center.y - 10) inView:scrollView withContentView:forgetPwdView delegate:nil];
    
}

- (void)submitForgetButtonClicked
{
    if (![TGHelper isValidateMobile:forgetText.text]) {
        [TGAlertView showAlertViewWithTitle:nil message:@"请输入正确的手机号码"];
        return;
    }
    
    [[TGHTTPRequestEngine sharedInstance] userForgetPassword:forgetText.text viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if([self httpResponseCorrect:responseObject])
        {
            TGComplexObject *rsp = (TGComplexObject *)responseObject;
            [TGAlertView showAlertViewWithTitle:nil message:rsp.header.message];
            [forgetText resignFirstResponder];
        }
    } failure:self.faultBlock];
}

- (void)registerButtonClicked
{
    [self dismissKeyboard];
    
    TGRegisterViewController *registerVc = [[TGRegisterViewController alloc] init];
    [self.navigationController pushViewController:registerVc animated:YES];
}

#pragma mark - UITextFieldDelegate
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if(textField == userNoText)
    {
        [passwordText becomeFirstResponder];
        return NO;
    }
    else if(textField == passwordText)
    {
        [self loginButtonClicked:nil];
        return [passwordText resignFirstResponder];
    }
    return YES;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
@end
