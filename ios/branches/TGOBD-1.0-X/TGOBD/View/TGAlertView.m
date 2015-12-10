//
//  TGAlertView.m
//  TGOBD
//
//  Created by Jiahai on 14-3-4.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGAlertView.h"
#import "SIAlertView.h"

@implementation TGAlertView

+ (void)showAlertViewWithTitle:(NSString *)title message:(NSString *)message
{
    SIAlertView *alertView = [[SIAlertView alloc] initWithTitle:title andMessage:message];
    [alertView addButtonWithTitle:@"确定" type:SIAlertViewButtonTypeDefault handler:^(SIAlertView *alertView) {
        [alertView dismissAnimated:YES];
    }];
    [alertView show];
}

@end
