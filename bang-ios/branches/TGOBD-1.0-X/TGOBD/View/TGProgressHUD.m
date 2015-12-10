//
//  TGProgressHUD.m
//  TGOBD
//
//  Created by Jiahai on 14-3-6.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGProgressHUD.h"
#import "MMProgressHUD.h"

@implementation TGProgressHUD

+ (void)show
{
    [MMProgressHUD show];
}

+ (void)dismiss
{
    [MMProgressHUD dismiss];
}
@end
