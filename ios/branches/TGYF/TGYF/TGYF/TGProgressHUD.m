//
//  TGProgressHUD.m
//  TGYF
//
//  Created by James Yu on 14-5-15.
//  Copyright (c) 2014年 james. All rights reserved.
//

#import "TGProgressHUD.h"
#import "SVProgressHUD.h"

@implementation TGProgressHUD

+ (void)show
{
    [SVProgressHUD showWithStatus:nil maskType:SVProgressHUDMaskTypeGradient];
}

+ (void)showWithStatus:(NSString *)status
{
    [SVProgressHUD showWithStatus:status maskType:SVProgressHUDMaskTypeGradient];
}

+ (void)showSuccessWithStatus:(NSString *)status
{
    [SVProgressHUD showSuccessWithStatus:status];
}

+ (void)showErrorWithStatus:(NSString *)status
{
    [SVProgressHUD showErrorWithStatus:status];
}

+ (void)dismiss
{
    [SVProgressHUD dismiss];
}

+ (void)dismissWithError:(NSString *)errorMsg title:(NSString *)title
{
    [SVProgressHUD showErrorWithStatus:errorMsg];
}

@end
