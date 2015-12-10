//
//  KKCustomTextField.h
//  KKOBD
//
//  Created by zhuyc on 13-8-12.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
typedef enum
{
    eTextFieldNone,
    eTextFieldImage,
    eTextFieldButton
}KKCustomTextFieldType;

@protocol KKCustomTextFieldDelegate;

@interface KKCustomTextField : UIView<UITextFieldDelegate>
{
    KKCustomTextFieldType    _type;
    UIImage                 *_iconImage;
    CGFloat                  _rightInset;
    NSString                *_placeholder;
    UIImageView              *_bgImv;
    
    NSInteger                 _bgImgType;
}
@property (nonatomic ,assign)id <KKCustomTextFieldDelegate> delegate;
@property (nonatomic ,retain)UITextField   *textField;
@property (nonatomic ,assign)NSInteger  index;
@property (nonatomic ,assign)BOOL   transEditNoti;
@property (nonatomic ,copy)id addtionalInfo;


- (id)initWithFrame:(CGRect)frame
           WithType:(KKCustomTextFieldType)type
    WithPlaceholder:(NSString *)placeholder
          WithImage:(UIImage *)image
WithRightInsetWidth:(CGFloat)inset;

- (id)initWithFrame:(CGRect)frame
    backgroundImage:(NSInteger)bgImgType
           WithType:(KKCustomTextFieldType)type
    WithPlaceholder:(NSString *)placeholder
          WithImage:(UIImage *)image
WithRightInsetWidth:(CGFloat)inset;

- (void)setBgImvToNil;

@end

@protocol KKCustomTextFieldDelegate <NSObject>
@optional
- (void)KKCustomTextFieldButtonClicked:(id)sender;
- (void)KKCustomTextFieldTextDidChanged:(NSString *)string andIndex:(NSInteger)index;
- (void)KKCustomTextFieldBeginEditing;
- (void)KKCustomTextFieldDidEndEditing:(KKCustomTextField *)sender;

@end