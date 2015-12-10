//
//  TGBaseViewController.m
//  TGOBD
//
//  Created by Jiahai on 14-3-1.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
#import "TGComplexModel.h"
#import "TGHelper.h"

@interface TGBaseViewController ()

- (void)httpRequestSystemError:(NSError *)aError;

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
	// Do any additional setup after loading the view.
    
    if(self.navigationItem.leftBarButtonItem == nil)
    {
        self.navigationItem.leftBarButtonItem = [TGViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_navibar_backbtn.png"] bgImage:nil target:self action:@selector(backButtonClicked:)];
    }
    
    UIImageView *bgImgView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 320, 570)];
    bgImgView.image = [UIImage imageNamed:@"bg_serviceSeeking.png"];
    [self.view addSubview:bgImgView];
    
    [self.view sendSubviewToBack:bgImgView];
    
    __weak TGBaseViewController *weakSelf = self;
    
    self.viewControllerIdentifier = [TGHelper createUUIDString];
    
    self.faultBlock = ^(AFHTTPRequestOperation *operation,NSError *error){
        [weakSelf httpRequestSystemError:error];
    };
    
    [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleLightContent];
}

- (void)backButtonClicked:(id)sender
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (BOOL)httpResponseCorrect:(id)responseObject
{
    [TGProgressHUD dismiss];
    if([[responseObject class] isSubclassOfClass:[TGComplexObject class]])
    {
        TGComplexObject *obj = (TGComplexObject *)responseObject;
        
        //header 为空时为非统购后台返回数据
        if(obj.header == nil)
            return YES;
        
        switch (obj.header.status) {
            case rspStatus_Succeed:
            {
                return YES;
            }
                break;
            default:
            {
                [TGAlertView showAlertViewWithTitle:nil message:obj.header.message];
            }
                break;
        }
    }
    return NO;
}

- (void)httpRequestSystemError:(NSError *)aError
{
    [TGProgressHUD dismiss];
    if(aError)
    {
        NSString *msg = nil;
        switch (aError.code) {
//            case NSURLErrorCancelled:
//            {
//                msg = @"用户取消网络请求";
//            }
//                break;
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


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
