//
//  TGProgressHUD.h
//  TGOBD
//
//  Created by Jiahai on 14-3-6.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
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
