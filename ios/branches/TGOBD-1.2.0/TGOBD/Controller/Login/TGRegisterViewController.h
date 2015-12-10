//
//  TGRegisterViewController.h
//  TGOBD
//
//  Created by Jiahai on 14-3-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
#import "TGScanViewController.h"
@class TGCustomTextFieldView;

@interface TGRegisterViewController : TGBaseViewController <UITextFieldDelegate,TGScanViewControllerDelegate>
{
    TGCustomTextFieldView *imeiTextFieldView;
    TGCustomTextFieldView *mobilTextFieldView;
    TGCustomTextFieldView *pwdTextFieldView;
}
@end
