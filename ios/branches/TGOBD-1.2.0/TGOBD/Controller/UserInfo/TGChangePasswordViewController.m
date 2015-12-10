//
//  TGChangePasswordViewController.m
//  TGOBD
//
//  Created by James Yu on 14-3-24.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGChangePasswordViewController.h"

@interface TGChangePasswordViewController ()

@end

@implementation TGChangePasswordViewController

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
    // Do any additional setup after loading the view from its nib.
    [self setNavigationTitle:@"修改密码"];
    
    [_scrollView setContentSize:CGSizeMake(320, 480)];
    
    UITapGestureRecognizer *tapGr = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(hideKeyboard)];
    [self.view addGestureRecognizer:tapGr];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self registerKeyboardNotification];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self removeKeyboardNotification];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)didKeyboardNotification:(NSNotification *)notification
{
    [self keyboardHeightChangedToMoveView:_scrollView notification:notification];
}

- (void)hideKeyboard
{
    [self.view endEditing:YES];
}

- (IBAction)changPassword:(id)sender {
    
    NSString *oldPwd = _oldPwd.text;
    NSString *newPwd = _password.text;
    NSString *repeatePwd = _repeatePwd.text;
    
    TGDataSingleton *dataSingle = [TGDataSingleton sharedInstance];
    
    if (oldPwd.length == 0)
    {
        [TGAlertView showAlertViewWithTitle:nil message:@"请输入旧密码！"];
        return;
    }
    else if (newPwd.length == 0)
    {
        [TGAlertView showAlertViewWithTitle:nil message:@"请输入新密码！"];
        return;
    }
    else if (repeatePwd.length == 0 || ![repeatePwd isEqualToString:newPwd])
    {
        [TGAlertView showAlertViewWithTitle:nil message:@"请输入相同的新密码！"];
        return;
    }

    [[TGHTTPRequestEngine sharedInstance] changePassword:dataSingle.userInfo.userNo oldPwd:oldPwd newPwd:newPwd viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        
        if ([self httpResponseCorrect:responseObject]) {
            dataSingle.userInfo.password = newPwd;
            [self.navigationController popViewControllerAnimated:YES];
        }
        
    } failure:self.faultBlock];
}

#pragma mark - UITextField delegate
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    textField.text = [TGHelper stringWithNoSpaceAndNewLine:textField.text];
}

@end
