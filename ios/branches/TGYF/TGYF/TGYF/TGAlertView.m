//
//  TGAlertView.m
//  TGYF
//
//  Created by James Yu on 14-5-15.
//  Copyright (c) 2014年 james. All rights reserved.
//

#import "TGAlertView.h"

@implementation TGAlertView

+ (void)showAlertViewWithTitle:(NSString *)title message:(NSString *)message
{
    SIAlertView *alertView = [[SIAlertView alloc] initWithTitle:title andMessage:message];
    
    [alertView addButtonWithTitle:@"确定" type:SIAlertViewButtonTypeDefault handler:^(SIAlertView *alertView) {
        [alertView dismissAnimated:YES];
    }];
    
    [TGAlertView setAlertViewAttribute:alertView];
    [alertView show];
    
}

+ (void)showAlertViewWithTitle:(NSString *)title message:(NSString *)message leftBtnTitle:(NSString *)leftBtnTitle rightBtnTitle:(NSString *)rightBtnTitle leftHandler:(SIAlertViewHandler)leftHandler rightHandler:(SIAlertViewHandler)rightHandler
{
    SIAlertView *alertView = [[SIAlertView alloc] initWithTitle:title andMessage:message];
    if (leftBtnTitle) {
        [alertView addButtonWithTitle:leftBtnTitle type:SIAlertViewButtonTypeDefault handler:leftHandler];
    }
    
    if (rightBtnTitle) {
        [alertView addButtonWithTitle:rightBtnTitle type:SIAlertViewButtonTypeDestructive handler:rightHandler];
    }
    
    [TGAlertView setAlertViewAttribute:alertView];
    
    [alertView show];
    
}

+ (void)showAlertViewWithTitle:(NSString *)title message:(NSString *)message buttonTitle:(NSString *)buttonTitle handler:(SIAlertViewHandler)handler
{
    SIAlertView *alertView = [[SIAlertView alloc] initWithTitle:title andMessage:message];
    [alertView addButtonWithTitle:buttonTitle type:SIAlertViewButtonTypeDefault handler:handler];
    [TGAlertView setAlertViewAttribute:alertView];
    [alertView show];
}

+ (void)setAlertViewAttribute:(SIAlertView *)alertView
{
    [alertView setButtonColor:[UIColor whiteColor]];
    [alertView setMessageColor:[UIColor blackColor]];
    
    alertView.cornerRadius = 6;
    alertView.viewBackgroundColor = COLOR_ALERTVIEW_BACKROUND;
}

@end
