//
//  TGMyDeviceViewController.h
//  TGOBD
//
//  Created by James Yu on 14-3-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
#import "TGCustomTextFieldView.h"
#import <MessageUI/MessageUI.h>

@interface TGMyDeviceViewController : TGBaseViewController <UITextFieldDelegate, MFMessageComposeViewControllerDelegate>

@property (nonatomic, assign) BOOL isRegister;
@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) TGCustomTextFieldView *deviceNo;
@property (nonatomic, strong) TGCustomTextFieldView *mainPhoneNum;
@property (nonatomic, strong) TGCustomTextFieldView *rescuePhoneNum1;
@property (nonatomic, strong) TGCustomTextFieldView *rescuePhoneNum2;

@end
