//
//  KKCustomAlertView.h
//  KKOBD
//
//  Created by zhuyc on 13-8-15.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
typedef enum
{
    KKCustomAlertView_default = 0,
    KKCustomAlertView_error
}KKCustomAlertViewType;

@interface KKCustomAlertView : UIView{
@protected
    UIImageView *_contentView;
    NSMutableArray *_blocks;
    CGFloat _height;
    
@private
    KKCustomAlertViewType  _type;
    NSString    *_message;
}

+ (void)showAlertViewWithMessage:(NSString *)message;
+ (void)showAlertViewWithMessage:(NSString *)message block:(void (^)())block;
+ (void)showErrorAlertViewWithMessage:(NSString *)message block:(void (^)())block;
- (id)initWithMessage:(NSString *)message WithType:(KKCustomAlertViewType)type;
- (void)addButtonWithTitle:(NSString *)title imageName:(NSString*)name block:(void (^)())block;
- (void)show;
- (void)showWithTextAlignment:(NSTextAlignment)textAlignment;
- (void)showInView:(UIView *)superView;
- (void)dismissWithClickedButtonIndex:(NSInteger)buttonIndex animated:(BOOL)animated;

@end
