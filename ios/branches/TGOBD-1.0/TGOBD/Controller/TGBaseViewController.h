//
//  TGBaseViewController.h
//  TGOBD
//
//  Created by Jiahai on 14-3-1.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TGComplexModel.h"
#import "TGProgressHUD.h"
#import "UIViewController+extend.h"
#import "TGHTTPRequestEngine.h"
#import "TGHelper.h"
#import "TGViewUtils.h"
#import "TGAlertView.h"
#import "TGDataSingleton.h"

typedef void(^TGFaultBlock)(AFHTTPRequestOperation *operation, NSError *error);

@interface TGBaseViewController : UIViewController

@property (nonatomic, strong)   id              viewControllerIdentifier;
@property (copy)                TGFaultBlock    faultBlock;



- (void)backButtonClicked:(id)sender;

/**
 *  HTTP请求正确响应
 *
 *  @param responseObject 解析后的返回值
 *
 *  @return YES:返回格式正确，为已定义类的对象；返回NO:表示解析错误或者为未定义的类
 */
- (BOOL)httpResponseCorrect:(id)responseObject;
/**
 *  HTTP请求错误，为系统级错误，如：请求超时、无网络连接等
 *
 *  @param aError 错误对象
 */
- (void)httpRequestSystemError:(NSError *)aError;
@end
