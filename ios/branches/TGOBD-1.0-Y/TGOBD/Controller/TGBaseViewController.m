//
//  TGBaseViewController.m
//  TGOBD
//
//  Created by Jiahai on 14-3-1.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
#import "TGComplexModel.h"
#import "MMProgressHUD.h"
#import "TGAlertView.h"
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
    
    UIImageView *bgImgView = [[UIImageView alloc] initWithFrame:self.view.bounds];
    bgImgView.image = [UIImage imageNamed:@"bg_serviceSeeking.png"];
    [self.view addSubview:bgImgView];
    
    __weak TGBaseViewController *weakSelf = self;
    
    self.viewControllerIdentifier = [TGHelper createUUIDString];
    
    self.faultBlock = ^(AFHTTPRequestOperation *operation,NSError *error){
        [weakSelf httpRequestSystemError:error];
    };
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
                [TGAlertView showAlertViewWithTitle:@"请求失败" message:obj.header.message];
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
            case NSURLErrorCancelled:
            {
                msg = @"用户取消网络请求";
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
                msg = @"网络连接未打开";
            }
                break;
            default:
                break;
        }
        [TGAlertView showAlertViewWithTitle:@"网络错误" message:msg];
    }
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
