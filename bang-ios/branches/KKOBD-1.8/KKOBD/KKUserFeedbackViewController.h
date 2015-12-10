//
//  KKUserFeedbackViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-16.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GCPlaceholderTextView.h"
#import "KKProtocolEngineDelegate.h"

@interface KKUserFeedbackViewController : UIViewController<UITextFieldDelegate,UITextViewDelegate,KKProtocolEngineDelegate>
{
    GCPlaceholderTextView           *_messageTextView;
    UIImageView                     *_inputBgView;
    UITextField                     *_phoneNumTextfield;
}
@end
