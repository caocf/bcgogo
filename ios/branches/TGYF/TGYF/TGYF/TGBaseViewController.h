//
//  TGBaseViewController.h
//  TGYIFA
//
//  Created by James Yu on 14-5-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TGNavigationBar.h"

typedef void (^TGFaultBlock)(NSError *error);

@interface TGBaseViewController : UIViewController

@property (nonatomic, strong) TGNavigationBar *titleBar;
@property (copy)        TGFaultBlock  faultBlock;

- (void)backButtonClicked:(id)sender;

- (void)hideNavigationBar;

- (void)setNavigationBarTitle:(NSString *)title;

/**
 *  Http 请求响应
 *
 *  @param responseObject jsonObject
 *
 *  @return YES :返回格式正确，为已定义类对象； 返回NO：表示解析错误或者为未定义的类
 */
- (BOOL)httpResponseCorrect:(id)responseObject;

/**
 *  HTTP 请求错误，为系统级错误，如：请求超时，无网络连接等
 *
 *  @param aError 返回的错误对象
 */
- (void)httpRequestSystemError:(NSError *)aError;

@end
