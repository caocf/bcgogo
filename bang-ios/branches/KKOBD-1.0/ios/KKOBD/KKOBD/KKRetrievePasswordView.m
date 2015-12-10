//
//  KKRetrievePasswordView.m
//  KKOBD
//
//  Created by zhuyc on 13-8-12.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKRetrievePasswordView.h"
#import "KKApplicationDefine.h"
#import "KKCustomAlertView.h"

@implementation KKRetrievePasswordView
@synthesize delegate;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        [self initial];
    }
    self.backgroundColor = [UIColor clearColor];
    return self;
}

- (void)initial
{
    [self setFrame:CGRectMake(0, 0, 320, currentScreenHeight)];
    UIImage *image = [UIImage imageNamed:@"bg_retrievePw.png"];
    
    _contentView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, image.size.width, image.size.height)];
    _contentView.backgroundColor = [UIColor clearColor];
    
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, image.size.width, image.size.height)];
    bgImv.backgroundColor = [UIColor clearColor];
    bgImv.userInteractionEnabled = YES;
    bgImv.image = image;
    [_contentView addSubview:bgImv];
    [bgImv release];
    
    CGPoint orign = CGPointMake(40, 70);
    image = [UIImage imageNamed:@"icon_lock_white.png"];
    UIImageView *iconImv = [[UIImageView alloc] initWithFrame:CGRectMake(orign.x+5, orign.y, image.size.width, image.size.height)];
    iconImv.backgroundColor = [UIColor clearColor];
    iconImv.image = image;
    [_contentView addSubview:iconImv];
    [iconImv release];
    
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(orign.x + 30,orign.y, 100, 17)];
    label.text = @"找回密码";
    label.textColor = [UIColor whiteColor];
    label.backgroundColor = [UIColor clearColor];
    label.font = [UIFont boldSystemFontOfSize:16.0f];
    [label sizeToFit];
    [_contentView addSubview:label];
    [label release];
    
    orign.y += 38;
    
    image = [UIImage imageNamed:@"form_gray.png"];
    UIImageView *bgTextFieldImv = [[UIImageView alloc] initWithFrame:CGRectMake(orign.x, orign.y, image.size.width, image.size.height)];
    bgTextFieldImv.image = image;
    bgTextFieldImv.backgroundColor = [UIColor clearColor];
    bgTextFieldImv.userInteractionEnabled = YES;
    
    _textField = [[KKPlaceHolderTextField alloc] initWithFrame:CGRectMake(10, 0, image.size.width - 20, image.size.height)];
    _textField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    _textField.backgroundColor = [UIColor clearColor];
    [_textField setTextColor:[UIColor whiteColor]];
    _textField.returnKeyType = UIReturnKeyDefault;
    _textField.font = [UIFont systemFontOfSize:15.0f];
    _textField.placeholder = @"请输入你的用户名";
    _textField.delegate = self;
    [bgTextFieldImv addSubview:_textField];
    [_textField release];
    
    [_contentView addSubview:bgTextFieldImv];
    [bgTextFieldImv release];
    
    orign.y += 54;
    [self addButtons:orign];
    
    [self addSubview:_contentView];
    [_contentView release];
    
}

- (void)addButtons:(CGPoint)point
{
    UIImage *image = [UIImage imageNamed:@"icon_fgpwBtn.png"];
    float xInset = 65;
    
    UIButton *cancelBtn = [[UIButton alloc] initWithFrame:CGRectMake(xInset, point.y, image.size.width, image.size.height)];
    [cancelBtn setTitle:@"取消" forState:UIControlStateNormal];
    [cancelBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [cancelBtn setBackgroundImage:image forState:UIControlStateNormal];
    [cancelBtn.titleLabel setFont:[UIFont systemFontOfSize:15.0f]];
    [cancelBtn addTarget:self action:@selector(cancelButtonClickd) forControlEvents:UIControlEventTouchUpInside];
    [_contentView addSubview:cancelBtn];
    [cancelBtn release];
    
    UIButton *sureBtn = [[UIButton alloc] initWithFrame:CGRectMake(320 - xInset - image.size.width, point.y, image.size.width, image.size.height)];
    [sureBtn setTitle:@"确定" forState:UIControlStateNormal];
    [sureBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [sureBtn setBackgroundImage:image forState:UIControlStateNormal];
    [sureBtn.titleLabel setFont:[UIFont systemFontOfSize:15.0f]];
    [sureBtn addTarget:self action:@selector(sureButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [_contentView addSubview:sureBtn];
    [sureBtn release];
    

}

- (void)sureButtonClicked
{
    if ([[_textField.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]] length] > 0)
    {
        if (self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(KKRetrievePasswordViewSureButtonClicked:)])
            [self.delegate KKRetrievePasswordViewSureButtonClicked:[NSString stringWithFormat:@"%@",_textField.text]];
        [self remove];
    }
    else
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请输入用户名！！！"];
    }

}

- (void)cancelButtonClickd
{
    if (self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(KKRetrievePasswordViewCancelButtonClicked)])
        [self.delegate KKRetrievePasswordViewCancelButtonClicked];
    
    [self remove];
}

- (void)show
{
    UIWindow *window = [[UIApplication sharedApplication] keyWindow];
    CGRect rect = _contentView.frame;
    rect.origin = CGPointMake(0, 75);
    [_contentView setFrame:rect];
    
    [window addSubview:self];
    [_textField becomeFirstResponder];
    
}

- (void)remove
{
    [self removeFromSuperview];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    
    return YES;
}

- (void)dealloc
{
    _textField = nil;
    _contentView = nil;
    
    [super dealloc];
}
@end
