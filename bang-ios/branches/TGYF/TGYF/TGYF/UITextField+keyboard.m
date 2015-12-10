//
//  UITextField+keyboard.m
//  TGYF
//
//  Created by James Yu on 14-5-15.
//  Copyright (c) 2014年 james. All rights reserved.
//

#import "UITextField+keyboard.h"

@implementation UITextField (keyboard)
- (void)observeForKeyboard:(UIScrollView *)scrollView
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardDidChangeFrame:) name:UIKeyboardDidChangeFrameNotification object:nil];
}

- (void)keyboardWillShow:(NSNotification *)notification
{
    
}

- (void)keyboardWillHide:(NSNotification *)notification
{
    if ([self isFirstResponder]) {
        
    }
}

- (void)keyboardDidChangeFrame:(NSNotification *)notification
{
    
}

@end
