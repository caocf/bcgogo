//
//  KKModifyPasswordViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-16.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKModifyPasswordViewController.h"
#import "KKApplicationDefine.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKPreference.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "MBProgressHUD.h"

@interface KKModifyPasswordViewController ()

@end

@implementation KKModifyPasswordViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self initVariables];
    [self initComponents];
    [_textField1.textField becomeFirstResponder];
}

#pragma mark -
#pragma mark Custom methods

- (void) initVariables
{
    _titles = [[NSMutableArray alloc] initWithObjects:@"旧密码 :",@"新密码 :",@"确认密码 :", nil];
    
}

- (void) initComponents
{
    [self setVcEdgesForExtendedLayout];
    [self setBachGroundView];
    [self setNavgationBar];
    
    float orignY1 = 25,orignY2 = 13;
    UIImage *formImage = [UIImage imageNamed:@"bg_form_modifypw.png"];
    
    for (int i = 0 ; i < [_titles count] ; i++)
    {
        UILabel *textLb = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY1, 75, 15)];
        textLb.textColor = [UIColor blackColor];
        textLb.font = [UIFont systemFontOfSize:15.0f];
        textLb.backgroundColor = [UIColor clearColor];
        textLb.textAlignment = UITextAlignmentRight;
        textLb.text = [_titles objectAtIndex:i];
        [self.view addSubview:textLb];
        [textLb release];
        
        KKCustomTextField *textField = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, formImage.size.width, formImage.size.height) WithType:eTextFieldNone WithPlaceholder:nil WithImage:formImage WithRightInsetWidth:0];
        textField.textField.secureTextEntry = YES;
        if (i == 0)
            _textField1 = textField;
        else if (i == 1)
            _textField2 = textField;
        else
            _textField3 = textField;
        
        [self.view addSubview:textField];
        [textField release];
        
        orignY2 += 45;
        orignY1 += 45;
        
    }
    
    UIImage *image = [UIImage imageNamed:@"bg_btn_modifypw.png"];
    UIButton *modifyButton = [[UIButton alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), 165, image.size.width, image.size.height)];
    [modifyButton setBackgroundImage:image forState:UIControlStateNormal];
    [modifyButton.titleLabel setFont:[UIFont boldSystemFontOfSize:17.0f]];
    [modifyButton setTitle:@"确定" forState:UIControlStateNormal];
    [modifyButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [modifyButton addTarget:self action:@selector(modifiButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:modifyButton];
    [modifyButton release];
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"修改密码";
    
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
}
#pragma mark -
#pragma mark Events

- (void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)modifiButtonClicked
{
    [self resignVcFirstResponder];

    NSString *oldPassword = nilOrString(_textField1.textField.text);
    NSString *newPassword = nilOrString(_textField2.textField.text);
    NSString *newPassword2 = nilOrString(_textField3.textField.text);

    NSRange range = [newPassword rangeOfString:@" "];
    
    if ([oldPassword length] == 0)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请输入旧密码!"];
        return;
    }
    
    if ([newPassword length] == 0 )
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请输入密码！"];
        return;
    }
    else if ([newPassword length] < 6)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"密码过短，请输入至少6位的密码!"];
        return;
    }
    else if (range.location != NSNotFound)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"密码格式不正确，不能输入空格!"];
        return;
    }
    else if ([newPassword2 length] == 0 || ![newPassword isEqualToString:newPassword2])
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请输入相同的密码！"];
        return;
    }
        
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    [[KKProtocolEngine sharedPtlEngine]  userPasswordModify:[KKProtocolEngine sharedPtlEngine].userName oldPassword: oldPassword newPassword:newPassword2 delegate:self];
}

#pragma mark -
#pragma mark UITextFieldDelegate

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate
- (NSNumber *)userPasswordModifyResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
        return KKNumberResultEnd;
    }
    KKModelProtocolRsp *proRsp = (KKModelProtocolRsp *)rsp;
    [KKCustomAlertView showAlertViewWithMessage:proRsp.header.desc];
    
    NSString *newPassword2 = nilOrString(_textField3.textField.text);
    [KKProtocolEngine sharedPtlEngine].password = newPassword2;
    KKModelPreferenceUserInfo *userInfo = [KKPreference sharedPreference].userInfo;
    userInfo.password = newPassword2;
    [KKPreference sharedPreference].userInfo = userInfo;
    
    return KKNumberResultEnd;
}

#pragma mark -
#pragma mark Handle Memory
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    
}

-(void)dealloc
{
    [super dealloc];
}
@end
