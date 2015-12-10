//
//  TGCustomTextFieldView.m
//  TGOBD
//
//  Created by James Yu on 14-3-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGCustomTextFieldView.h"
#import "TGMacro.h"

@implementation TGCustomTextFieldView

- (id)initWithFrame:(CGRect)frame leftTitle:(NSString *)leftTitle placeholder:(NSString *)placeholder rightTitle:(NSString *)rightTitle rightImage:(UIImage *)rightImage
{
    self = [super initWithFrame:frame];
    
    if (self) {
        [self setFrame:frame];
        
        _bgImgView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 320, 50)];
        UIImage *bgImg = [UIImage  imageNamed:@"bg_textField.png"];
        bgImg = [bgImg resizableImageWithCapInsets:UIEdgeInsetsMake(10, 10, 10, 10) resizingMode:UIImageResizingModeStretch];
        _bgImgView.image = bgImg;
        _bgImgView.userInteractionEnabled = NO;
        _bgImgView.autoresizingMask = UIViewAutoresizingFlexibleHeight;
        
        _leftLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 105, 50)];
        _leftLabel.text = leftTitle;
        _leftLabel.font = [UIFont systemFontOfSize:15];
        _leftLabel.backgroundColor = [UIColor clearColor];
        _leftLabel.textAlignment = NSTextAlignmentRight;
        _leftLabel.textColor = COLOR_TEXTLEFT_6C6C6C;
        _leftLabel.numberOfLines = 2;
        _leftLabel.lineBreakMode = NSLineBreakByWordWrapping;
        _leftLabel.minimumScaleFactor = 0.5;
        
        _textField = [[UITextField alloc] initWithFrame:CGRectMake(113, 10, 153, 30)];
        _textField.borderStyle = UITextBorderStyleNone;
        _textField.placeholder = placeholder;
        _textField.textColor = COLOR_TEXT_000000;
        _textField.font = [UIFont systemFontOfSize:14];
        [_textField setClearButtonMode:UITextFieldViewModeNever];
        _textField.autocorrectionType = UITextAutocorrectionTypeNo;
        _textField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        
        _rightImg = [[UIImageView alloc] initWithFrame:CGRectMake(280, 13.5, 19, 23)];
        if (rightImage != nil) {
            _rightImg.image = rightImage;
        }
        
        _rightLabel = [[UILabel alloc] initWithFrame:CGRectMake(271, 5, 40, 40)];
        _rightLabel.text = rightTitle;
        _rightLabel.backgroundColor = [UIColor clearColor];
        _rightLabel.font = [UIFont systemFontOfSize:14];
        _rightLabel.textAlignment = NSTextAlignmentCenter;
        
        [self addSubview:_bgImgView];
        [self addSubview:_leftLabel];
        [self addSubview:_textField];
        [self addSubview:_rightImg];
        [self addSubview:_rightLabel];
    }
    return self;
}

@end
