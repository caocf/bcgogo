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

typedef void(^TGFaultBlock)(AFHTTPRequestOperation *operation, NSError *error);

@interface TGBaseViewController : UIViewController

@property (nonatomic, strong)   id              viewControllerIdentifier;
@property (copy)                TGFaultBlock    faultBlock;


- (BOOL)httpResponseCorrect:(id)responseObject;
@end
