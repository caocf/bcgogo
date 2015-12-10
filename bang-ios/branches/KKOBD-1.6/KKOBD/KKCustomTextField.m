//
//  KKCustomTextField.m
//  KKOBD
//
//  Created by zhuyc on 13-8-12.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKCustomTextField.h"

@implementation KKCustomTextField
@synthesize textField;
@synthesize delegate;
@synthesize index;
@synthesize transEditNoti;

- (id)initWithFrame:(CGRect)frame
           WithType:(KKCustomTextFieldType)type
    WithPlaceholder:(NSString *)placeholder
          WithImage:(UIImage *)image
WithRightInsetWidth:(CGFloat)inset
{
    self = [super initWithFrame:frame];
    if (self) {
        _type = type;
        _iconImage = image;
        _rightInset = inset;
        _placeholder = placeholder;
        
        [self initial];
    }
    
    self.backgroundColor = [UIColor clearColor];
    return self;
}

- (void)initial
{
    CGRect rect = self.bounds;
    
    _bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, rect.size.width, rect.size.height)];
    _bgImv.backgroundColor = [UIColor clearColor];
    _bgImv.userInteractionEnabled = YES;
    _bgImv.image = [[UIImage imageNamed:@"form_white.png"] stretchableImageWithLeftCapWidth:0 topCapHeight:0];

    float width = rect.size.width;
    CGSize size = _iconImage ? _iconImage.size : CGSizeMake(rect.size.height, rect.size.height);
    if (MAX(size.width, size.height)< 33)
        size = CGSizeMake(33, 33);
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(width - _rightInset - size.width, 0.5*(rect.size.height - size.height), size.width, size.height)];
    if (_iconImage)
        [button setImage:_iconImage forState:UIControlStateNormal];
    [button setBackgroundColor:[UIColor clearColor]];
    [button addTarget:self action:@selector(ButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    
    switch (_type) {
        case eTextFieldNone:
        {
            width = width - 10*2;
            break;
        }
        case eTextFieldImage:
        {
            width = width - 10 - size.width - _rightInset;
            button.userInteractionEnabled = NO;
            [_bgImv addSubview:button];
            break;
        }
        case eTextFieldButton:
        {
            width = width - 10 - size.width - _rightInset - 5;
            button.userInteractionEnabled = YES;
            [_bgImv addSubview:button];
            break;
        }
        default:
            break;
    }
    [button release];
    
    UITextField *aTextField = [[UITextField alloc] initWithFrame:CGRectMake(10, 0, width, rect.size.height)];
    aTextField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    aTextField.backgroundColor = [UIColor clearColor];
    aTextField.textColor = [UIColor blackColor];
    aTextField.autocapitalizationType = UITextAutocapitalizationTypeNone;
    aTextField.font = [UIFont systemFontOfSize:15.0f];
    aTextField.returnKeyType = UIReturnKeyDefault;
    aTextField.placeholder =_placeholder;
    aTextField.userInteractionEnabled = YES;
    aTextField.delegate = self;
    self.textField = aTextField;
    [_bgImv addSubview:aTextField];
    [aTextField release];
    
    [self addSubview:_bgImv];
    [_bgImv release];
}

- (void)setBgImvToNil
{
    _bgImv.image = nil;
    [self setNeedsDisplay];
}

#pragma mark -
#pragma mark UITextFieldDelegate

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField
{    
    if (self.transEditNoti)
    {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(textFieldTextDidChanged) name:UITextFieldTextDidChangeNotification object:nil];
        
        if (self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(KKCustomTextFieldBeginEditing)])
        {
            [self.delegate KKCustomTextFieldBeginEditing];
        }
    }
    return YES;
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UITextFieldTextDidChangeNotification object:nil];

    if (self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(KKCustomTextFieldDidEndEditing:)])
        [self.delegate KKCustomTextFieldDidEndEditing:self];
}

- (BOOL)textFieldShouldReturn:(UITextField *)aTextField;
{
    [aTextField resignFirstResponder];
    return YES;
}

- (void)textFieldTextDidChanged
{
    if (self.transEditNoti)
    {
        if (self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(KKCustomTextFieldTextDidChanged:andIndex:)])
        {
            [self.delegate KKCustomTextFieldTextDidChanged:self.textField.text andIndex:self.index];
        }
    }
}

- (void)ButtonClicked
{
    if (self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(KKCustomTextFieldButtonClicked:)])
        [self.delegate KKCustomTextFieldButtonClicked:self];
}

- (void)dealloc
{
    _iconImage = nil;
    _placeholder = nil;
    _bgImv = nil;
    self.textField = nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UITextFieldTextDidChangeNotification object:nil];
    
    [super dealloc];
}
@end
