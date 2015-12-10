//
//  TGLoginViewController.h
//  TGOBD
//
//  Created by Jiahai on 14-3-11.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
@class PopoverView;

@interface TGLoginViewController : TGBaseViewController <UITextFieldDelegate>
{
    __weak IBOutlet UITextField *userNoText;
    __weak IBOutlet UITextField *passwordText;
    __weak IBOutlet UIScrollView *scrollView;
    
    UIView *forgetPwdView;
    UITextField *forgetText;
}
@end
