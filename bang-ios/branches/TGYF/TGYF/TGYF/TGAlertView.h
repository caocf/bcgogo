//
//  TGAlertView.h
//  TGYF
//
//  Created by James Yu on 14-5-15.
//  Copyright (c) 2014年 james. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SIAlertView.h"

@interface TGAlertView : NSObject

+ (void)showAlertViewWithTitle:(NSString *)title message:(NSString *)message;

+ (void)showAlertViewWithTitle:(NSString *)title message:(NSString *)message leftBtnTitle:(NSString *)leftBtnTitle rightBtnTitle:(NSString *) rightBtnTitle leftHandler:(SIAlertViewHandler)leftHandler rightHandler:(SIAlertViewHandler)rightHandler;

+ (void)showAlertViewWithTitle:(NSString *)title message:(NSString *)message buttonTitle:(NSString *)buttonTitle handler:(SIAlertViewHandler)handler;

@end
