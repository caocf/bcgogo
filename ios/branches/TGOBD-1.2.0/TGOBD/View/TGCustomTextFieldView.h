//
//  TGCustomTextFieldView.h
//  TGOBD
//
//  Created by James Yu on 14-3-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol TGCustomTextFieldDelegate;

@interface TGCustomTextFieldView : UIView <UITextFieldDelegate>

@property (nonatomic, strong) UIImageView *bgImgView;
@property (nonatomic, strong) UITextField *textField;
@property (nonatomic, strong) UILabel *leftLabel;
@property (nonatomic, strong) UIImageView *rightImg;
@property (nonatomic, strong) UILabel *rightLabel;

- (id)initWithFrame:(CGRect)frame leftTitle:(NSString *)leftTitle placeholder:(NSString *)placeholder rightTitle:(NSString *)rightTitle rightImage:(UIImage *)rightImage;

@end