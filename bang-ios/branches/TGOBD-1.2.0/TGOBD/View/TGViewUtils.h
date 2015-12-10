//
//  TGViewUtils.h
//  TGOBD
//
//  Created by Jiahai on 14-3-6.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TGViewUtils : NSObject

+ (UIBarButtonItem *)createNavigationBarButtonItem:(UIImage *)aImage bgImage:(UIImage *)bgImage target:(id)aTarget action:(SEL)aAction;

+ (UIBarButtonItem*)createNavigationBarButtonItemWithTitle:(NSString*)title bgImage:(UIImage*)bgImage target:(id)aTarget action:(SEL)aAction;

+ (UIBarButtonItem *)createNavigationBarButtonItem:(UIImage *)aImage bgImage:(UIImage *)bgImage target:(id)aTarget action:(SEL)aAction tipsFrame:(CGRect)tipsFrame notificationName:(NSString *)notificationName;

@end
