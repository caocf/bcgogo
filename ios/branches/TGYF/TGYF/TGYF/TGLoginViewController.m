//
//  TGLoginViewController.m
//  TGYIFA
//
//  Created by James Yu on 14-5-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGLoginViewController.h"
#import "TGHttpManager.h"
#import "TGRootViewController.h"
#import "TPKeyboardAvoidingScrollView.h"

@interface TGLoginViewController () <UITextFieldDelegate>

@property (nonatomic, strong) TPKeyboardAvoidingScrollView *scrollView;
@property (nonatomic, strong) UITextField *mobile;
@property (nonatomic, strong) UITextField *password;

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

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self hideNavigationBar];
    [self initComponent];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Custom Method

- (void)initComponent
{
    
    _scrollView = [[TPKeyboardAvoidingScrollView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, [self getViewHeight])];
    _scrollView.bounces = NO;
    _scrollView.showsVerticalScrollIndicator = NO;
    _scrollView.backgroundColor = RGBA(47, 168, 198, 1);
    [self.view addSubview:_scrollView];

    UIImageView *bgImgView = [[UIImageView alloc] initWithFrame:_scrollView.bounds];
    bgImgView.image = ImageNamed((SCREEN_HEIGHT > 480 ? @"bg_login4.png" : @"bg_login3.5.png"));
    [_scrollView addSubview:bgImgView];
    
    UIImageView *logo = [[UIImageView alloc] initWithFrame:CGRectMake(100, 51, 120, 100)];
    logo.image = ImageNamed(@"ico_logo.png");
    [_scrollView addSubview:logo];
    
    UIImageView *mobileBg = [[UIImageView alloc] initWithFrame:CGRectMake(41, 176, 238, 40)];
    mobileBg.image = ImageNamed(@"bg_mobile.png");
    [_scrollView addSubview:mobileBg];
    
    _mobile = [[UITextField alloc] initWithFrame:CGRectMake(82, 181, 177, 30)];
    _mobile.borderStyle = UITextBorderStyleNone;
    _mobile.placeholder = @"请输入手机号码";
    _mobile.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    [_scrollView addSubview:_mobile];
    
    UIImageView *passwordBg = [[UIImageView alloc] initWithFrame:CGRectMake(41, 229, 238, 40)];
    passwordBg.image = ImageNamed(@"bg_password.png");
    [_scrollView addSubview:passwordBg];
    
    _password = [[UITextField alloc] initWithFrame:CGRectMake(82, 234, 177, 30)];
    _password.borderStyle = UITextBorderStyleNone;
    _password.placeholder = @"请输入密码";
    _password.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    [_scrollView addSubview:_password];
    
    UILabel *lbl = [[UILabel alloc] initWithFrame:CGRectMake(73, 300, 76, 21)];
    lbl.backgroundColor = [UIColor clearColor];
    lbl.textColor = [UIColor whiteColor];
    lbl.text = @"记住密码";
    [_scrollView addSubview:lbl];
    
    UIButton *btn = [[UIButton alloc] initWithFrame:CGRectMake(165, 296, 48, 30)];
    [btn setBackgroundImage:ImageNamed(@"btn_remberPwd.png") forState:UIControlStateNormal];
    [_scrollView addSubview:btn];
    
    UIButton *login = [[UIButton alloc] initWithFrame:CGRectMake(41, 351, 238, 40)];
    [login setTitle:@"登录" forState:UIControlStateNormal];
    [login addTarget:self action:@selector(login) forControlEvents:UIControlEventTouchUpInside];
    [login setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
    [login setBackgroundImage:ImageNamed(@"btn_login") forState:UIControlStateNormal];
    [_scrollView addSubview:login];
}

- (void)login
{
    NSString *mobil = _mobile.text;
    NSString *password = _password.text;
    
    __weak TGLoginViewController *weakSelf = self;
    
    [[TGHttpManager sharedInstance] login:mobil password:password success:^(id rspObject) {
        if ([weakSelf httpResponseCorrect:rspObject]) {
            //
        }
    } error:weakSelf.faultBlock viewControllerClass:[self class]];
}

#pragma mark - UITextFieldDelegate

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    textField.text = [TGUtils stringWithNoSpaceAndNewLine:textField.text];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

@end
