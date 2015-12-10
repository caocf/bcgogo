//
//  TGSetVilolationQueryConditionViewController.m
//  TGOBD
//
//  Created by James Yu on 14-4-10.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGSetVilolationQueryConditionViewController.h"
#import "TGCustomPickerView.h"
#import "TGHelper.h"

@interface TGSetVilolationQueryConditionViewController ()
@property (nonatomic, copy) NSString *juheCityCode;
@end

@implementation TGSetVilolationQueryConditionViewController

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
    [self initComponents];
    [self setNavigationTitle:@"查询设置"];
    [self initVariable];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Custom Methods
- (void)initComponents
{
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar] + 10;
    
    _queryCity = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"查询城市:" placeholder:nil rightTitle:nil rightImage:nil];
    UIButton *btn = [[UIButton alloc] initWithFrame:_queryCity.bounds];
    [btn addTarget:self action:@selector(selectCity) forControlEvents:UIControlEventTouchUpInside];
    [_queryCity addSubview:btn];
    
//    originY += 50;
//    
//    _registerNo = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"登记证书号:" placeholder:nil rightTitle:nil rightImage:nil];
//    _registerNo.textField.delegate = self;
    
    originY += 50;
    _engineNo = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"发动机号:" placeholder:nil rightTitle:nil rightImage:nil];
    _engineNo.textField.delegate = self;
    
    originY += 50;
    
    _vehicleVin = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"车架号:" placeholder:nil rightTitle:nil rightImage:nil];
    _vehicleVin.textField.delegate = self;
    
    originY += 80;
    
    UIButton *commitBtn = [[UIButton alloc] initWithFrame:CGRectMake(42, originY, 238, 39.5)];
    [commitBtn setBackgroundImage:[UIImage imageNamed:@"btn_submit.png"] forState:UIControlStateNormal];
    [commitBtn addTarget:self action:@selector(commitContidion) forControlEvents:UIControlEventTouchUpInside];
    
    [self.view addSubview:_queryCity];
    [self.view addSubview:_registerNo];
    [self.view addSubview:_engineNo];
    [self.view addSubview:_vehicleVin];
    [self.view addSubview:commitBtn];
}

- (void)initVariable
{
    TGModelVehicleInfo *vehicleInfo = [[TGDataSingleton sharedInstance] vehicleInfo];
    _juheCityCode = vehicleInfo.juheCityCode;
    _queryCity.textField.text = vehicleInfo.juheCityName;
    _registerNo.textField.text = vehicleInfo.registNo;
    _engineNo.textField.text = vehicleInfo.engineNo;
    _vehicleVin.textField.text = vehicleInfo.vehicleVin;
}

- (void)commitContidion
{
    NSString *juheCityName = _queryCity.textField.text;
    NSString *registNo = [TGHelper stringWithNoSpaceAndNewLine:_registerNo.textField.text];
    NSString *engineNo = [TGHelper stringWithNoSpaceAndNewLine:_engineNo.textField.text];
    NSString *vehicleVin = [TGHelper stringWithNoSpaceAndNewLine:_vehicleVin.textField.text];
    
    if (juheCityName.length == 0) {
        [TGAlertView showAlertViewWithTitle:nil message:@"请选择查询城市"];
        return;
    }
    
//    if (registNo.length == 0) {
//        [TGAlertView showAlertViewWithTitle:nil message:@"请输入登记证书号"];
//        return;
//    }
    
    if (engineNo.length == 0) {
        [TGAlertView showAlertViewWithTitle:nil message:@"请输入发动机号"];
        return;
    }
    
    if (vehicleVin.length == 0) {
        [TGAlertView showAlertViewWithTitle:nil message:@"请输入车架号"];
        return;
    }
    
    TGModelVehicleInfo *vehicleInfo = [[TGDataSingleton sharedInstance] vehicleInfo];
    
    [TGProgressHUD show];
    
    [[TGHTTPRequestEngine sharedInstance] updateVehicleInfo:vehicleInfo.oilPrice currentMileage:[NSString stringWithFormat:@"%d",vehicleInfo.currentMileage] maintainPeriod:[NSString stringWithFormat:@"%d", vehicleInfo.maintainPeriod] juheCityCode:_juheCityCode juheCityName:juheCityName registerNo:registNo engineNo:engineNo vehicleVin:vehicleVin lastMaintainMileage:[NSString stringWithFormat:@"%d",vehicleInfo.lastMaintainMileage] nextMaintainTime:vehicleInfo.nextMaintainTime nextExamineTime:vehicleInfo.nextExamineTime viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if ([self httpResponseCorrect:responseObject]) {
            [TGProgressHUD showSuccessWithStatus:@"设置成功"];
            vehicleInfo.juheCityName = juheCityName;
            vehicleInfo.juheCityCode = _juheCityCode;
            vehicleInfo.registNo = registNo;
            vehicleInfo.engineNo = engineNo;
            vehicleInfo.vehicleVin = vehicleVin;
            
            [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_UpdateTrafficViolation object:nil userInfo:nil];
            
            [self.navigationController popViewControllerAnimated:YES];
        }
    } failure:self.faultBlock];
}

#pragma mark - pickView delegate

- (void)selectVlues:(NSString *)cityName juheCode:(NSString *)juheCode
{
    _queryCity.textField.text = cityName;
    _juheCityCode = juheCode;
}

- (void)selectCity
{
    [self.view endEditing:YES];
    
    [[TGHTTPRequestEngine sharedInstance] violateGetCityList:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        TGModelViolateCityInfoListRsp *rsp = (TGModelViolateCityInfoListRsp *)responseObject;
        TGCustomPickerView *pick = [[TGCustomPickerView alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
        pick.city = rsp.areaList__TGModelViolateCityInfo;
        pick.delegate = self;
        [pick show];
        
    } failure:self.faultBlock];
}

#pragma mark - UITextField delegate
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    if (textField == _vehicleVin.textField) {
        textField.text = [textField.text uppercaseString];
    }
    return YES;
}


@end
