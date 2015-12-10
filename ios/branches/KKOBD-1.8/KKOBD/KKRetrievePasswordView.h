//
//  KKRetrievePasswordView.h
//  KKOBD
//
//  Created by zhuyc on 13-8-12.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKPlaceHolderTextField.h"

@protocol KKRetrievePasswordViewDelegate;

@interface KKRetrievePasswordView : UIView<UITextFieldDelegate>
{
    UIView                      *_contentView;
    KKPlaceHolderTextField      *_textField;
}
@property (nonatomic ,assign)id<KKRetrievePasswordViewDelegate> delegate;

- (void)show;

@end

@protocol KKRetrievePasswordViewDelegate
@optional
- (void)KKRetrievePasswordViewCancelButtonClicked;
- (void)KKRetrievePasswordViewSureButtonClicked:(NSString *)text;

@end