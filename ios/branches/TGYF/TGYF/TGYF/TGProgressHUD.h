//
//  TGProgressHUD.h
//  TGYF
//
//  Created by James Yu on 14-5-15.
//  Copyright (c) 2014年 james. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TGProgressHUD : NSObject

+ (void)show;

+ (void)showWithStatus:(NSString *)status;

+ (void)dismiss;

//+ (void)dismissWithError:(NSString *)errorMsg title:(NSString *)title;

+ (void)showSuccessWithStatus:(NSString *)status;

+ (void)showErrorWithStatus:(NSString *)status;


@end
