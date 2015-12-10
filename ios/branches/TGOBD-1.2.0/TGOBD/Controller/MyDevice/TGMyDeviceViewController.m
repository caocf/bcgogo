//
//  TGMyDeviceViewController.m
//  TGOBD
//
//  Created by James Yu on 14-3-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGMyDeviceViewController.h"
#import "TGAppDelegate.h"
#import "TGHelper.h"

typedef enum {
    mainPhoneMessage = 10000,
    rescuePhoneMessage,
}sendMsgType;

@interface TGMyDeviceViewController ()

@property (nonatomic, assign) float offset;
@property (nonatomic, assign) sendMsgType currentSendMsgType;
@property (nonatomic, assign) BOOL bSendMainPhoneMessage;
@property (nonatomic, assign) BOOL bSendRescuePhoneMessage;

@end

@implementation TGMyDeviceViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        _isRegister = NO;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    [self initComponents];
    [self setNavigationBar];
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
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar];
    
    _scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, originY, 320, [self getViewHeightWithNavigationBar])];
    
    originY = 10;
    
    _deviceNo = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"设备编号:" placeholder:@"" rightTitle:@"" rightImage:nil];
    _deviceNo.textField.enabled = NO;
    _deviceNo.textField.textColor = COLOR_TEXTLEFT_6C6C6C;
    
    originY += 50;
    
    _mainPhoneNum = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"主控号码:" placeholder:@"请输入主控号码" rightTitle:@"" rightImage:nil];
    _mainPhoneNum.textField.keyboardType = UIKeyboardTypeNumberPad;
    _mainPhoneNum.textField.delegate = self;
    
    UIButton *setMainPhoneBtn = [[UIButton alloc] initWithFrame:CGRectMake(115, 50, 150, 40)];
    [setMainPhoneBtn setTitle:@"发送短信" forState:UIControlStateNormal];
    [setMainPhoneBtn setBackgroundImage:[UIImage imageNamed:@"bg_button_blue"] forState:UIControlStateNormal];
    [setMainPhoneBtn addTarget:self action:@selector(sendMainPhoneNum) forControlEvents:UIControlEventTouchUpInside];
    
    UILabel *tipLbl = [[UILabel alloc] initWithFrame:CGRectMake(10, 100, 300, 1000)];
    tipLbl.backgroundColor = [UIColor clearColor];
    tipLbl.textColor = [UIColor redColor];
    tipLbl.font = [UIFont systemFontOfSize:15];
    tipLbl.numberOfLines = 0;
    tipLbl.text = @"主控号码：车辆故障及报警信息的主要接收方，建议设置为车主手机号码。";
    [tipLbl sizeToFit];

    [_mainPhoneNum setFrame:CGRectMake(0, originY, 320, tipLbl.frame.size.height + 120)];
    [_mainPhoneNum addSubview:setMainPhoneBtn];
    [_mainPhoneNum addSubview:tipLbl];
    
    originY += _mainPhoneNum.frame.size.height;
    
    _rescuePhoneNum1 = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"救援号码1:" placeholder:@"请设置救援号码1" rightTitle:@"" rightImage:nil];
    _rescuePhoneNum1.textField.keyboardType = UIKeyboardTypeNumberPad;
    _rescuePhoneNum1.textField.delegate = self;
    
    originY += 50;
    
    _rescuePhoneNum2 = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"救援号码2:" placeholder:@"请设置救援号码2" rightTitle:@"" rightImage:nil];
    _rescuePhoneNum2.textField.keyboardType = UIKeyboardTypeNumberPad;
    _rescuePhoneNum2.textField.delegate = self;
    
    UIButton *setRescuePhoneBtn = [[UIButton alloc] initWithFrame:CGRectMake(115, 50, 150, 40)];
    [setRescuePhoneBtn setTitle:@"发送短信" forState:UIControlStateNormal];
    [setRescuePhoneBtn setBackgroundImage:[UIImage imageNamed:@"bg_button_blue"] forState:UIControlStateNormal];
    [setRescuePhoneBtn addTarget:self action:@selector(sendRescuePhoneNum) forControlEvents:UIControlEventTouchUpInside];
    
    UILabel *tipLbl1 = [[UILabel alloc] initWithFrame:CGRectMake(10, 100, 300, 1000)];
    tipLbl1.backgroundColor = [UIColor clearColor];
    tipLbl1.textColor = [UIColor redColor];
    tipLbl1.font = [UIFont systemFontOfSize:15];
    tipLbl1.numberOfLines = 0;
    tipLbl1.text = @"救援号码：车辆故障及报警信息接收方，建议设置为亲人手机号码及4S店救援号码。";
    [tipLbl1 sizeToFit];
    
    [_rescuePhoneNum2 setFrame:CGRectMake(0, originY, 320, tipLbl1.frame.size.height + 120)];
    [_rescuePhoneNum2 addSubview:setRescuePhoneBtn];
    [_rescuePhoneNum2 addSubview:tipLbl1];
    
    [_scrollView addSubview:_deviceNo];
    [_scrollView addSubview:_mainPhoneNum];
    [_scrollView addSubview:_rescuePhoneNum1];
    [_scrollView addSubview:_rescuePhoneNum2];
    [self.view addSubview:_scrollView];
    
    originY += 70;
    
    [_scrollView setContentSize:CGSizeMake(320, originY > [self getViewHeightWithNavigationBar] ? originY : [self getViewHeightWithNavigationBar])];
    
    UITapGestureRecognizer *tapGr = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(hideKeyboard)];
    [self.view addGestureRecognizer:tapGr];
}

- (void)initVariable
{
    _deviceNo.textField.text = [[[TGDataSingleton sharedInstance] userInfo] imei];
    _mainPhoneNum.textField.text = [[[TGDataSingleton sharedInstance] userInfo] mobile];
    _bSendMainPhoneMessage = NO;
    _bSendRescuePhoneMessage = NO;
}

- (void)setNavigationBar
{
    [self setNavigationTitle:@"我的设备"];
}

- (void)sendMainPhoneNum
{
    NSString *phone = _mainPhoneNum.textField.text;
    
    if (![TGHelper isValidateMobile:phone]) {
        [TGAlertView showAlertViewWithTitle:nil message:@"请输入正确的主控号码"];
        return;
    }
    
    if ([self checkCurrentDeviceCanSendMessage]) {
        NSString *body = [NSString stringWithFormat:@"adm123456,%@", phone];
        NSArray *recipients = [NSArray arrayWithObjects:[[[TGDataSingleton sharedInstance] userInfo] gsmObdImeiMoblie], nil];
        
        _currentSendMsgType = mainPhoneMessage;
        
        [self sendMessage:body recipients:recipients];
    }

}
- (BOOL)checkCurrentDeviceCanSendMessage
{
    Class messageClass = (NSClassFromString(@"MFMessageComposeViewController"));
    
    if (messageClass != nil) {
        if ([messageClass canSendText]) {
            return YES;
        } else
        {
            [TGAlertView showAlertViewWithTitle:nil message:@"当前手机不能发送短信"];
            return NO;
        }
    }
    else {
        [TGAlertView showAlertViewWithTitle:nil message:@"当前系统不支持程序内发送短信"];
        return NO;
    }
}

- (void)sendRescuePhoneNum
{
    NSString *phone1 = _rescuePhoneNum1.textField.text;
    NSString *phone2 = _rescuePhoneNum2.textField.text;
    
    NSArray *recipients = [[NSArray alloc] initWithObjects:[[[TGDataSingleton sharedInstance] userInfo] gsmObdImeiMoblie], nil];
    NSString *body = @"sos123456";
    
    if (phone1.length == 0 && phone2.length == 0) {
        [TGAlertView showAlertViewWithTitle:nil message:@"请输入救援号码"];
        return;
    }
    
    if (phone1.length > 0) {
        if ([TGHelper isValidateMobile:phone1]) {
            body = [NSString stringWithFormat:@"%@,%@", body, phone1];
        }
        else
        {
            [TGAlertView showAlertViewWithTitle:nil message:@"救援号码1手机号码有误，请重新输入"];
            return;
        }
    }
    
    if (phone2.length > 0) {
        if ([TGHelper isValidateMobile:phone2]) {
            body = [NSString stringWithFormat:@"%@,%@", body, phone2];
        }
        else
        {
            [TGAlertView showAlertViewWithTitle:nil message:@"救援号码2手机号码有误，请重新输入"];
            return;
        }
    }
    
    if ([self checkCurrentDeviceCanSendMessage]) {
        _currentSendMsgType = rescuePhoneMessage;
        [self sendMessage:body recipients:recipients];
    }
}

- (void)hideKeyboard
{
    [self.view endEditing:YES];
}

- (void)sendMessage:(NSString *)msgBody recipients:(NSArray *)recipients
{
    MFMessageComposeViewController *picker = [[MFMessageComposeViewController alloc] init];
    picker.messageComposeDelegate = self;
    
    picker.body = msgBody;
    picker.recipients = recipients;
    
    [self presentModalViewController:picker animated:YES];
}

- (void)backButtonClicked:(id)sender
{
    if (_isRegister) {
        if (!_bSendMainPhoneMessage || !_bSendRescuePhoneMessage) {
            
            NSString *message = !_bSendMainPhoneMessage ? @"您还未设置主控号码，确定不设置吗？" : @"您还未设置救援号码，确定不设置吗？";
            
            [TGAlertView showAlertViewWithTitle:nil message:message leftBtnTitle:@"取消" rightBtnTitle:@"设置" leftHandler:^(SIAlertView *alertView) {
                [TGAppDelegateSingleton showRootView];
            } rightHandler:^(SIAlertView *alertView) {
                return;
            }];
        } else
        {
            [TGAppDelegateSingleton showRootView];
        }
    }
    else
    {
        [self.navigationController popViewControllerAnimated:YES];
    }
}

#pragma mark - sendMessage delegate

- (void)messageComposeViewController:(MFMessageComposeViewController *)controller didFinishWithResult:(MessageComposeResult)result
{
    switch (result) {
        case MessageComposeResultCancelled:
            NSLog(@"cancel");
            break;
        case MessageComposeResultSent:
            NSLog(@"sent");
            if (_currentSendMsgType == mainPhoneMessage) {
                _bSendMainPhoneMessage = YES;
            } else if (_currentSendMsgType == rescuePhoneMessage) {
                _bSendRescuePhoneMessage = YES;
            }
            
            break;
        case MessageComposeResultFailed:
            NSLog(@"failed");
        default:
            break;
    }
    [self dismissModalViewControllerAnimated:YES];
}

#pragma mark - UITextField delegate

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
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
}

@end
