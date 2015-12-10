//
//  TGVehicleManageViewController.m
//  TGOBD
//
//  Created by James Yu on 14-3-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGVehicleManageViewController.h"
#import "TGDataSingleton.h"
#import "TGHTTPRequestEngine.h"
#import "TGProgressHUD.h"
#import "TGAppDelegate.h"
#import "TGAlertView.h"
#import "NSDate+millisecond.h"
#import "TGMessageRollingHandler.h"
#import "TGMyDeviceViewController.h"

@interface TGVehicleManageViewController ()

@property (nonatomic, assign) selectTimeType timeType;
@property (nonatomic, assign) float offset;
@property (nonatomic, copy) NSString *juheCityCode;

@end

@implementation TGVehicleManageViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        _manageType = updateVehicle;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    [self initComponents];
    [self setNavigationBar];
    [self initVehicleInfo];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Custom Methods

- (void)initComponents
{
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar];
    
    _scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, originY, 320, [self getViewHeightWithNavigationBar])];
    _scrollView.scrollEnabled = YES;
    
    originY = 10;
    
    UIImageView *bgTitleView = [[UIImageView alloc] initWithFrame:CGRectMake(0, originY, 320, 32)];
    bgTitleView.image = [UIImage imageNamed:@"bg_title.png"];
    
    UILabel *basicTips = [[UILabel alloc] initWithFrame:CGRectMake(5, 0, 320, 32)];
    basicTips.textAlignment = NSTextAlignmentLeft;
    basicTips.text = @"基本信息(必填)";
    basicTips.backgroundColor = [UIColor clearColor];
    
    [bgTitleView addSubview:basicTips];
    
    originY += 32;
    
    _vehicleNo = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"车牌号码:" placeholder:nil rightTitle:nil rightImage:nil];
    _vehicleNo.textField.enabled = NO;
    
    originY += 50;
    
    _modelName = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"车型:" placeholder:nil rightTitle:nil rightImage:nil];
    _modelName.textField.enabled = NO;
    
    originY += 50;
    
    _oilPrice = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"油价:" placeholder:nil rightTitle:@"元/L" rightImage:nil];
    _oilPrice.textField.delegate = self;
    _oilPrice.textField.keyboardType = UIKeyboardTypeDecimalPad;
    
    originY += 50;
    
    _currentMileage = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"当前里程:" placeholder:@"如:5000" rightTitle:@"KM" rightImage:nil];
    _currentMileage.textField.delegate = self;
    _currentMileage.textField.keyboardType = UIKeyboardTypeNumberPad;
    
    originY += 50;
    
    _maintainCycle = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"保养周期:" placeholder:@"如:8000"  rightTitle:@"KM" rightImage:nil];
    _maintainCycle.textField.delegate = self;
    _maintainCycle.textField.keyboardType = UIKeyboardTypeNumberPad;
    
    originY += 50;
    
    UIImageView *bgVehicleInfo = [[UIImageView alloc] initWithFrame:CGRectMake(0, originY, 320, 32)];
    bgVehicleInfo.image = [UIImage imageNamed:@"bg_title.png"];
    
    UILabel *vehicleTips = [[UILabel alloc] initWithFrame:CGRectMake(5, 0, 320, 32)];
    vehicleTips.textAlignment = NSTextAlignmentLeft;
    vehicleTips.text = @"车辆信息(选填，查询违章使用)";
    vehicleTips.backgroundColor = [UIColor clearColor];
    
    [bgVehicleInfo addSubview:vehicleTips];
    
    originY += 32;
    
    _queryCity = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"查询城市:" placeholder:nil rightTitle:nil rightImage:nil];
    UIButton *queryCityBtn = [[UIButton alloc] initWithFrame:_queryCity.bounds];
    queryCityBtn.backgroundColor = [UIColor clearColor];
    [queryCityBtn addTarget:self action:@selector(selectCity) forControlEvents:UIControlEventTouchUpInside];
    [_queryCity addSubview:queryCityBtn];
    
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
    
    originY += 50;
    
    UIImageView *bgRemind = [[UIImageView alloc] initWithFrame:CGRectMake(0, originY, 320, 32)];
    bgRemind.image = [UIImage imageNamed:@"bg_title.png"];
    
    UILabel *maintainTips = [[UILabel alloc] initWithFrame:CGRectMake(5, 0, 320, 32)];
    maintainTips.textAlignment = NSTextAlignmentLeft;
    maintainTips.text = @"保养提醒(选填)";
    maintainTips.backgroundColor = [UIColor clearColor];
    
    [bgRemind addSubview:maintainTips];
    
    originY += 32;
    
    _lastMaintainMileage = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"上次保养里程:" placeholder:nil rightTitle:@"KM" rightImage:nil];
    _lastMaintainMileage.textField.delegate = self;
    _lastMaintainMileage.textField.keyboardType = UIKeyboardTypeNumberPad;
    
    originY += 50;
    
    _nextInsuranceTime = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"下次保养时间:" placeholder:nil rightTitle:nil rightImage:[UIImage imageNamed:@"icon_calendar.png"]];
    _nextInsuranceTime.textField.enabled = NO;
    UIButton *insuranceBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 320, 50)];
    insuranceBtn.backgroundColor = [UIColor clearColor];
    [insuranceBtn addTarget:self action:@selector(selectTimeClicked:) forControlEvents:UIControlEventTouchUpInside];
    insuranceBtn.tag = insuranceTime;
    [_nextInsuranceTime addSubview:insuranceBtn];
    
    originY += 50;
    
    _nextExamineTime = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"下次验车时间:" placeholder:nil rightTitle:nil rightImage:[UIImage imageNamed:@"icon_calendar.png"]];
    _nextExamineTime.textField.enabled = NO;
    UIButton *examineBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 320, 50)];
    examineBtn.tag = examTime;
    examineBtn.backgroundColor = [UIColor clearColor];
    [examineBtn addTarget:self action:@selector(selectTimeClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_nextExamineTime addSubview:examineBtn];
    
    [_scrollView addSubview:bgTitleView];
    [_scrollView addSubview:_vehicleNo];
    [_scrollView addSubview:_modelName];
    [_scrollView addSubview:_oilPrice];
    [_scrollView addSubview:_currentMileage];
    [_scrollView addSubview:_maintainCycle];
    [_scrollView addSubview:bgVehicleInfo];
    [_scrollView addSubview:_queryCity];
    //[_scrollView addSubview:_registerNo];
    [_scrollView addSubview:_engineNo];
    [_scrollView addSubview:_vehicleVin];
    [_scrollView addSubview:bgRemind];
    [_scrollView addSubview:_lastMaintainMileage];
    [_scrollView addSubview:_nextInsuranceTime];
    [_scrollView addSubview:_nextExamineTime];
    [self.view addSubview:_scrollView];
    
    originY += 60;
    
    [_scrollView setContentSize:CGSizeMake(320, originY > [self getViewHeightWithNavigationBar] ? originY : [self getViewHeightWithNavigationBar])];
    
    //碰屏幕之外的地方隐藏键盘
    UITapGestureRecognizer *tapGr = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(hideKeyboard)];
    [self.view addGestureRecognizer:tapGr];
}

- (void)setNavigationBar
{
    [self setNavigationTitle:@"车辆管理"];
    self.navigationItem.rightBarButtonItem = [TGViewUtils createNavigationBarButtonItemWithTitle:@"保存"
                                                                                         bgImage:nil target:self action:@selector(saveVehicleInfo)];
    
}

- (void)initVehicleInfo
{
    TGDataSingleton *singleton = [TGDataSingleton sharedInstance];
    _vehicleNo.textField.text = singleton.vehicleInfo.vehicleNo;
    _modelName.textField.text = [NSString stringWithFormat:@"%@ %@", singleton.vehicleInfo.vehicleBrand, singleton.vehicleInfo.vehicleModel];
    _oilPrice.textField.text = singleton.vehicleInfo.oilPrice;
    _currentMileage.textField.text = [NSString stringWithFormat:@"%d",singleton.vehicleInfo.currentMileage];
    _maintainCycle.textField.text = [NSString stringWithFormat:@"%d",singleton.vehicleInfo.maintainPeriod];
    _lastMaintainMileage.textField.text = [NSString stringWithFormat:@"%d",singleton.vehicleInfo.lastMaintainMileage];
    _queryCity.textField.text = singleton.vehicleInfo.juheCityName;
    _juheCityCode = singleton.vehicleInfo.juheCityCode;
    _engineNo.textField.text = singleton.vehicleInfo.engineNo;
    _registerNo.textField.text = singleton.vehicleInfo.registNo;
    _vehicleVin.textField.text = singleton.vehicleInfo.vehicleVin;
    
    if (singleton.vehicleInfo.nextMaintainTime) {
        _nextInsuranceTime.textField.text = [NSDate dateStringWithTimeIntervalSince1970WithMillisecond:singleton.vehicleInfo.nextMaintainTime formatter:nil];
    }
    
    if (singleton.vehicleInfo.nextExamineTime) {
        _nextExamineTime.textField.text = [NSDate dateStringWithTimeIntervalSince1970WithMillisecond:singleton.vehicleInfo.nextExamineTime formatter:nil];
    }
}

- (void)selectTimeClicked:(id)sender
{
    UIButton *btn = (UIButton *)sender;
    _timeType = btn.tag;
    [self showCustomDatePickder];
    [self hideKeyboard];
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

- (void)showCustomDatePickder
{
    TGCustomDataPickerView *picker = [[TGCustomDataPickerView alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    picker.delegate = self;
    [picker show];
    [self hideKeyboard];
}

- (void)hideKeyboard
{
    [self.view endEditing:YES];
}

- (void)saveVehicleInfo
{
    NSString *oilcePrice = _oilPrice.textField.text;
    NSString *currentMileage = _currentMileage.textField.text;
    NSString *maintainPeriod = _maintainCycle.textField.text;
    NSString *lastMaintainMileage = _lastMaintainMileage.textField.text;
    NSString *nextMaintainTime = _nextInsuranceTime.textField.text;
    NSString *nextExamineTime = _nextExamineTime.textField.text;
    
    NSString *registerNo = _registerNo.textField.text;
    NSString *engineNo = _engineNo.textField.text;
    NSString *vehicleVin = _vehicleVin.textField.text;
    
    //数据有效性判断
    if ([oilcePrice floatValue] < 5.0 || [oilcePrice floatValue] > 15.0) {
        [TGAlertView showAlertViewWithTitle:nil message:@"请设置正确的油价"];
        return;
    }
    
    if ([currentMileage integerValue] <= 0 ) {
        [TGAlertView showAlertViewWithTitle:nil message:@"请设置当前里程"];
        return;
    }
    
    if ([maintainPeriod integerValue] <= 0) {
        [TGAlertView showAlertViewWithTitle:nil message:@"请设置保养周期"];
        return;
    }
    
    TGHTTPRequestEngine *request = [TGHTTPRequestEngine sharedInstance];
    TGModelLoginInfo *loginInfo = [[TGModelLoginInfo alloc] init];
    TGDataSingleton *dataSingleton = [TGDataSingleton sharedInstance];
    
    [TGProgressHUD show];
    
    if (_manageType == registerUser) {
        [request userRegister:dataSingleton.userInfo.userNo
                     password:dataSingleton.userInfo.password
                         imei:dataSingleton.userInfo.imei
                     oilPrice:oilcePrice
               currentMileage:currentMileage
               maintainPeriod:maintainPeriod
                 juheCityCode:_juheCityCode
                 juheCityName:_queryCity.textField.text
                   registerNo:registerNo engineNo:engineNo vehicleVin:vehicleVin
          lastMaintainMileage:lastMaintainMileage
             nextMaintainTime:[NSDate timeIntervalSince1970WithMillisecondFromString:nextMaintainTime formatter:nil]
              nextExamineTime:[NSDate timeIntervalSince1970WithMillisecondFromString:nextExamineTime formatter:nil]
                    loginInfo:loginInfo
     viewControllerIdentifier:self.viewControllerIdentifier
                      success:^(AFHTTPRequestOperation *operation, id responseObject)
         {
             if ([self httpResponseCorrect:responseObject]) {
                 if ([responseObject isKindOfClass:[TGModelRegisterRsp class]]) {
                     TGModelRegisterRsp *rsp = (TGModelRegisterRsp *)responseObject;
                     dataSingleton.userInfo = rsp.appUserDTO;
                     dataSingleton.vehicleInfo = rsp.appVehicleDTO;
                     dataSingleton.shopInfo = rsp.appShopDTO;
                     
                     //保存用户名
                     [[NSUserDefaults standardUserDefaults] setObject:dataSingleton.userInfo.mobile forKey:USER_MOBILE];
                     
                     //跳到我的设备进行设定主控号码
                     TGMyDeviceViewController *Vc = [[TGMyDeviceViewController alloc] init];
                     Vc.isRegister = YES;
                     [TGAppDelegateSingleton.rootViewController pushViewController:Vc animated:YES];
                 }
             }
         }
                      failure:self.faultBlock];
    }
    else
    {
        [request updateVehicleInfo:oilcePrice currentMileage:currentMileage maintainPeriod:maintainPeriod juheCityCode:_juheCityCode juheCityName:_queryCity.textField.text registerNo:registerNo engineNo:engineNo vehicleVin:vehicleVin lastMaintainMileage:lastMaintainMileage nextMaintainTime:[NSDate timeIntervalSince1970WithMillisecondFromString:nextMaintainTime formatter:nil] nextExamineTime:[NSDate timeIntervalSince1970WithMillisecondFromString:nextExamineTime formatter:nil] viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
            
            if ([self httpResponseCorrect:responseObject]) {
                dataSingleton.vehicleInfo.oilPrice = oilcePrice;
                dataSingleton.vehicleInfo.maintainPeriod = [maintainPeriod integerValue];
                dataSingleton.vehicleInfo.currentMileage = [currentMileage integerValue];
                dataSingleton.vehicleInfo.lastMaintainMileage = [lastMaintainMileage integerValue];
                dataSingleton.vehicleInfo.nextExamineTime = [NSDate timeIntervalSince1970WithMillisecondFromString:nextExamineTime formatter:nil];
                dataSingleton.vehicleInfo.nextMaintainTime = [NSDate timeIntervalSince1970WithMillisecondFromString:nextMaintainTime formatter:nil];
                
                dataSingleton.vehicleInfo.juheCityName = _queryCity.textField.text;
                dataSingleton.vehicleInfo.juheCityCode = _juheCityCode;
                dataSingleton.vehicleInfo.vehicleVin = vehicleVin;
                dataSingleton.vehicleInfo.engineNo = engineNo;
                dataSingleton.vehicleInfo.registNo = registerNo;
                
                [self.navigationController popViewControllerAnimated:YES];
            }
            
        } failure:self.faultBlock];
    }
}

#pragma mark - CustomDataPicker delegate

- (void)TGDataPickerSelected:(NSDate *)timeDate
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd HH:mm"];
    NSString *time = [formatter stringFromDate:timeDate];
    
    if (_timeType == insuranceTime) {
        _nextInsuranceTime.textField.text = time;
    } else
    {
        _nextExamineTime.textField.text = time;
    }
}

#pragma mark - CustomPickerView delegate

- (void)selectVlues:(NSString *)cityName juheCode:(NSString *)juheCode
{
    _juheCityCode = juheCode;
    _queryCity.textField.text = cityName;
}

#pragma mark - UITextField delegate

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    
    return YES;
}

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    CGPoint point = [textField convertPoint:textField.frame.origin toView:[[UIApplication sharedApplication] keyWindow]];
    
    float height = [[UIScreen mainScreen] bounds].size.height;
    
    _offset = (height - point.y - 50) > 216 ? 0 : 216 - (height - point.y - 50);
    
    if (_offset > 0) {
        [_scrollView setContentOffset:CGPointMake(_scrollView.contentOffset.x, _scrollView.contentOffset.y + _offset) animated:NO];
        [_scrollView setContentSize:CGSizeMake(_scrollView.contentSize.width, _scrollView.contentSize.height + _offset + 40)];
    }
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    if (_offset > 0) {
        [_scrollView setContentOffset:CGPointMake(_scrollView.contentOffset.x, _scrollView.contentOffset.y - _offset) animated:NO];
        [_scrollView setContentSize:CGSizeMake(_scrollView.contentSize.width, _scrollView.contentSize.height - _offset - 40)];
    }
    
    if (textField == _oilPrice.textField) {
        float oilPrice = [textField.text floatValue];
        textField.text = [[NSString stringWithFormat:@"%.2f", oilPrice] isEqualToString:@"0.00"] ? nil : [NSString stringWithFormat:@"%.2f", oilPrice];
    }
    else if (textField == _currentMileage.textField)
    {
        NSInteger currentMileage = [textField.text integerValue];
        textField.text = [[NSString stringWithFormat:@"%d", currentMileage] isEqualToString:@"0"] ? nil : [NSString stringWithFormat:@"%d", currentMileage];
    }
    else if (textField == _maintainCycle.textField)
    {
        NSInteger maintainCycle = [textField.text integerValue];
        textField.text = [[NSString stringWithFormat:@"%d", maintainCycle] isEqualToString:@"0"] ? nil : [NSString stringWithFormat:@"%d", maintainCycle];
    }
    else if (textField == _lastMaintainMileage.textField)
    {
        NSInteger lastMaintainMileage = [textField.text integerValue];
        textField.text = [[NSString stringWithFormat:@"%d", lastMaintainMileage] isEqualToString:@"0"] ? nil : [NSString stringWithFormat:@"%d", lastMaintainMileage];
    }
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    if (textField == _vehicleVin.textField) {
        textField.text = [textField.text uppercaseString];
    }
    return YES;
}

@end
