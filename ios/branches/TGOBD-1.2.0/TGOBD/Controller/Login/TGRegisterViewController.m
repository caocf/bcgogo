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

@property (nonatomic, assign) BOOL isScanning;

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
    
    _isScanning = NO;
    
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
    [scanBtn setFrame:CGRectMake(270, 0, 45, 50)];
    [scanBtn setBackgroundColor:[UIColor clearColor]];
    [scanBtn addTarget:self action:@selector(scanButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [imeiTextFieldView addSubview:scanBtn];
    
    originY += 50;
    
    mobilTextFieldView = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, screenWidth, 40) leftTitle:@"手机号：" placeholder:nil rightTitle:nil rightImage:nil];
    mobilTextFieldView.textField.delegate = self;
    [self.view addSubview:mobilTextFieldView];
    
    originY += 50;
    
    pwdTextFieldView = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, screenWidth, 40) leftTitle:@"密码：" placeholder:nil rightTitle:nil rightImage:nil];
    pwdTextFieldView.textField.secureTextEntry = YES;
    pwdTextFieldView.textField.delegate = self;
    [self.view addSubview:pwdTextFieldView];
    
    originY += 68;
    
    UIButton *submitBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [submitBtn setFrame:CGRectMake(60, originY, 200, 50)];
    [submitBtn setBackgroundImage:[UIImage imageNamed:@"bg_button_blue.png"] forState:UIControlStateNormal];
    [submitBtn setTitle:@"下一步" forState:UIControlStateNormal];
    [submitBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [submitBtn addTarget:self action:@selector(submitButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:submitBtn];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(scanFinish) name:NOTIFICATION_FINISHED_SCAN object:nil];
}

- (void)scanFinish
{
    _isScanning = NO;
}

- (void)submitButtonClicked
{
    NSString *emei = imeiTextFieldView.textField.text;
    if (emei.length == 0) {
        [TGAlertView showAlertViewWithTitle:nil message:@"请输入IMEI号"];
        return;
    }
    
    NSString *mobile = mobilTextFieldView.textField.text;
    
    if (![TGHelper isValidateMobile:mobile]) {
        [TGAlertView showAlertViewWithTitle:nil message:@"请输入正确的手机号码"];
        return;
    }
    [TGProgressHUD show];
    [[TGHTTPRequestEngine sharedInstance] userValidateIMEI:imeiTextFieldView.textField.text mobile:mobile password:pwdTextFieldView.textField.text viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
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
    if (NO == _isScanning) {
        _isScanning = YES;
        TGScanViewController *scanVc = [[TGScanViewController alloc] init];
        scanVc.delegate = self;
        [self.navigationController pushViewController:scanVc animated:NO];
    }
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

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    textField.text = [TGHelper stringWithNoSpaceAndNewLine:textField.text];
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
