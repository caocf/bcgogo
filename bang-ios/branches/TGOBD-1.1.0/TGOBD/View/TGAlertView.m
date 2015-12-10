//
//  TGAlertView.m
//  TGOBD
//
//  Created by Jiahai on 14-3-4.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGAlertView.h"
#import "SIAlertView.h"

#define BAGROUNDVIEW_COLOR [[UIColor alloc] initWithRed:160/255.0 green:219/255.0 blue:237/255.0 alpha:1]

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
//    UIImage *imgDefault = [[UIImage imageNamed:@"btn_blue.png"] resizableImageWithCapInsets:UIEdgeInsetsMake(10, 30, 10, 30) resizingMode:UIImageResizingModeStretch];
//    
//    [alertView setDefaultButtonImage:imgDefault forState:UIControlStateNormal];
//    [alertView setDefaultButtonImage:imgDefault forState:UIControlStateHighlighted];
//    
//    UIImage *imgDestructive = [[UIImage imageNamed:@"btn_orange_alert.png"] resizableImageWithCapInsets:UIEdgeInsetsMake(10, 30, 10, 30) resizingMode:UIImageResizingModeStretch];
//    
//    [alertView setDestructiveButtonImage:imgDestructive forState:UIControlStateNormal];
//    [alertView setDestructiveButtonImage:imgDestructive forState:UIControlStateHighlighted];
    
    [alertView setButtonColor:[UIColor whiteColor]];
    [alertView setMessageColor:[UIColor blackColor]];
    
    alertView.cornerRadius = 6;
    alertView.viewBackgroundColor = BAGROUNDVIEW_COLOR;
}

@end
