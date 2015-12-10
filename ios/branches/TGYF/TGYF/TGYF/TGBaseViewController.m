//
//  TGBaseViewController.m
//  TGYIFA
//
//  Created by James Yu on 14-5-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
#import "TGComplexModel.h"
#import "TGHttpManager.h"
#import "TGAlertView.h"
#import "TGGlobalDataSingleton.h"
#import "TGProgressHUD.h"
#import "TGAppDelegate.h"
#import "TGUtils.h"

@interface TGBaseViewController ()

@end

@implementation TGBaseViewController

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

    self.view.backgroundColor = [UIColor whiteColor];
    //网络请求错误处理
    __weak TGBaseViewController *weakSelf = self;
    
    self.faultBlock = ^(NSError *error) {
        [weakSelf httpRequestSystemError:error];
    };
    
    [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleLightContent];
    
    [self defaultNavigationBar];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - 自定义导航栏样式（隐藏掉系统自带的导航栏）

- (void)defaultNavigationBar
{
    [self initNavigationBar];
}

- (void)initNavigationBar
{
    if (!_titleBar) {
        _titleBar = [[TGNavigationBar alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, isIOS7 ? 64 : 44)];
        [_titleBar setBackgroundColor:COLOR_NAVIGATION_BAR];
        [self.view addSubview:_titleBar];
    }
}

- (void)hideNavigationBar
{
    [_titleBar removeFromSuperview];
    _titleBar = nil;
}

- (void)setNavigationBarTitle:(NSString *)title
{
    if (_titleBar == nil) {
        [self defaultNavigationBar];
    }
    [_titleBar setNavigationBarTitle:title];
}

- (void)addRightButton:(UIButton *)button
{
    if (_titleBar == nil) {
        [self defaultNavigationBar];
    }
    
    [_titleBar addNavigationBarRightButton:button];
}

- (void)addLeftButton:(UIButton *)button
{
    if (_titleBar == nil) {
        [self defaultNavigationBar];
    }
    
    [_titleBar addNavigationBarLeftButton:button];
}

#pragma mark - Custom Method

- (void)backButtonClicked:(id)sender
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (BOOL)httpResponseCorrect:(id)responseObject
{
    [TGProgressHUD dismiss];
    
    if ([[responseObject class] isSubclassOfClass:[TGComplexModel class]]) {
        TGComplexModel *obj = (TGComplexModel *)responseObject;
        
        //header 为空时为非统购后台返回数据，如 访问第三方地址
        if (obj.header == nil) {
            return YES;
        }
        
        if ([obj.header.status isEqualToString:@"SUCCESS"]) {
            return YES;
        }
        else
        {
            [TGAlertView showAlertViewWithTitle:nil message:(obj.header.message.length != 0 ? obj.header.message : @"请求失败")];
            return NO;
        }
    }
    
    return NO;
}

- (void)httpRequestSystemError:(NSError *)aError
{
    [TGProgressHUD dismiss];
    
    if (aError) {
        NSString *msg = nil;
        
        switch (aError.code) {
            case NSURLErrorCancelled:
            {
                msg = @"网路请求取消";
            }
                break;
            case NSURLErrorBadURL:
            {
                msg = @"网址错误";
            }
                break;
            case NSURLErrorTimedOut:
            {
                msg = @"请求超时";
            }
                break;
            case NSURLErrorCannotFindHost:
            {
                msg = @"找不到服务器";
            }
                break;
            case NSURLErrorCannotConnectToHost:
            {
                msg = @"连接不到服务器";
            }
                break;
            case NSURLErrorBadServerResponse:
            {
                msg = @"服务器响应失败";
            }
                break;
            case NSURLErrorNotConnectedToInternet:
            {
                msg = @"不能连接到网络";
            }
                break;
            default:
                break;
        }
        
        if (msg) {
            [TGAlertView showAlertViewWithTitle:nil message:msg];
        }
    }
}

@end
