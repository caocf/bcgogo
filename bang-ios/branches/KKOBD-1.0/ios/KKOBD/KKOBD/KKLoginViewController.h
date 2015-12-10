//
//  KKLoginViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-12.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKWaittingView.h"
#import "KKCustomTextField.h"
#import "MBProgressHUD.h"
#import "KKProtocolEngineDelegate.h"
#import "KKRetrievePasswordView.h"

@interface KKLoginViewController : UIViewController<UITextFieldDelegate,MBProgressHUDDelegate,KKProtocolEngineDelegate,KKRetrievePasswordViewDelegate,UITableViewDataSource,UITableViewDelegate>
{
    KKCustomTextField     *_accountTextField;
    KKCustomTextField     *_passwordTextField;
    KKWaittingView        *_waittingView;
    MBProgressHUD         *_hudProgressView;
    
    UITableView           *_matchingTableView;
    NSArray               *_userInfoArray;
    BOOL                   _firstLoadMatchingTableView;
    
    BOOL                   _receiveKeyboardNotification;
}

@end
