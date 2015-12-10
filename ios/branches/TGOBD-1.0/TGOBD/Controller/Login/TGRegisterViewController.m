//
//  TGRegisterViewController.m
//  TGOBD
//
//  Created by Jiahai on 14-3-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGRegisterViewController.h"
#import "TGCustomTextFieldView.h"
#import "TGVehicleManageViewController.h"
#import "TGDataSingleton.h"
#import "TGVehicleManageViewController.h"

@interface TGRegisterViewController ()

@end

@implementation TGRegisterViewController

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
    
    [self registerKeyboardNotification];
    
    [self setNavigationTitle:@"用户注册"];
    self.navigationController.navigationBar.hidden = NO;
    
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar];
    
    originY += 12;
    
    imeiTextFieldView = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, screenWidth, 40) leftTitle:@"IMEI：" placeholder:nil rightTitle:nil rightImage:[UIImage imageNamed:@"img_twos.png"]];
    imeiTextFieldView.textField.delegate = self;
    [self.view addSubview:imeiTextFieldView];
    
    CGRect rect = imeiTextFieldView.rightImg.frame;
    rect.size.width = rect.size.height + 4;
    imeiTextFieldView.rightImg.frame = rect;
    
    UIButton *scanBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [scanBtn setFrame:rect];
    [scanBtn addTarget:self action:@selector(scanButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [imeiTextFieldView addSubview:scanBtn];
    
    originY += 50;
    
    mobilTextFieldView = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, screenWidth, 40) leftTitle:@"手机号：" placeholder:nil rightTitle:nil rightImage:nil];
    imeiTextFieldView.textField.delegate = self;
    [self.view addSubview:mobilTextFieldView];
    
    originY += 50;
    
    pwdTextFieldView = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, screenWidth, 40) leftTitle:@"密码：" placeholder:nil rightTitle:nil rightImage:nil];
    pwdTextFieldView.textField.secureTextEntry = YES;
    imeiTextFieldView.textField.delegate = self;
    [self.view addSubview:pwdTextFieldView];
    
    originY += 68;
    
    UIButton *submitBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [submitBtn setFrame:CGRectMake(60, originY, 200, 50)];
    [submitBtn setBackgroundImage:[UIImage imageNamed:@"bg_button_blue.png"] forState:UIControlStateNormal];
    [submitBtn setTitle:@"下一步" forState:UIControlStateNormal];
    [submitBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [submitBtn addTarget:self action:@selector(submitButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:submitBtn];
    
}

- (void)submitButtonClicked
{
    [TGProgressHUD show];
    [[TGHTTPRequestEngine sharedInstance] userValidateIMEI:imeiTextFieldView.textField.text mobile:mobilTextFieldView.textField.text password:pwdTextFieldView.textField.text viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if([self httpResponseCorrect:responseObject])
        {
            TGModelValidateIMEIRsp *rsp = (TGModelValidateIMEIRsp *)responseObject;
            [TGDataSingleton sharedInstance].shopInfo = rsp.shopDTO;
            [TGDataSingleton sharedInstance].vehicleInfo = rsp.appVehicleDTO;
            TGModelUserInfo *userInfo = [[TGModelUserInfo alloc] init];
            userInfo.userNo = mobilTextFieldView.textField.text;
            userInfo.password = pwdTextFieldView.textField.text;
            userInfo.imei = imeiTextFieldView.textField.text;
            [TGDataSingleton sharedInstance].userInfo = userInfo;
            
            TGVehicleManageViewController *Vc = [[TGVehicleManageViewController alloc] init];
            Vc.manageType = registerUser;
            [self.navigationController pushViewController:Vc animated:YES];
        }
    } failure:self.faultBlock];
}

- (void)scanButtonClicked
{
    TGScanViewController *scanVc = [[TGScanViewController alloc] init];
    scanVc.delegate = self;
    [self.navigationController pushViewController:scanVc animated:YES];
}

#pragma mark - TGScanViewControllerDelegate
- (void)scanSuccess:(NSString *)dataStr
{
    imeiTextFieldView.textField.text = dataStr;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

- (void)didKeyboardNotification:(NSNotification *)notification
{
    [self keyboardHeightChangedToMoveView:nil notification:notification];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
