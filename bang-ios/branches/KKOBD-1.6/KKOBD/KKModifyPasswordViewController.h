//
//  KKModifyPasswordViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-16.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKCustomTextField.h"
#import "KKProtocolEngineDelegate.h"

@interface KKModifyPasswordViewController : UIViewController<UITextFieldDelegate,KKProtocolEngineDelegate>
{
    NSMutableArray      *_titles;
    KKCustomTextField   *_textField1;
    KKCustomTextField   *_textField2;
    KKCustomTextField   *_textField3;
}

@end
