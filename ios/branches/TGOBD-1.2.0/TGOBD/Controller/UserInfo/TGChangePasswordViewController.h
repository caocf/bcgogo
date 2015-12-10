//
//  TGChangePasswordViewController.h
//  TGOBD
//
//  Created by James Yu on 14-3-24.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"

@interface TGChangePasswordViewController : TGBaseViewController <UITextFieldDelegate>
@property (weak, nonatomic) IBOutlet UITextField *oldPwd;
@property (weak, nonatomic) IBOutlet UITextField *password;
@property (weak, nonatomic) IBOutlet UITextField *repeatePwd;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;

- (IBAction)changPassword:(id)sender;

@end
