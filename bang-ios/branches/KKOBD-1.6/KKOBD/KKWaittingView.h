//
//  KKWaittingView.h
//  KKOBD
//
//  Created by zhuyc on 13-8-14.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface KKWaittingView : UIView
{
    UIImageView     *_contentView;
    UIImageView     *_iconImv;
    UILabel         *_messageLb;
    NSString        *_messageStr;
    
    float            _maxWidth;
}
- (id)initWithViewWidth:(float)width WithMessage:(NSString *)msg;
- (void)show;
- (void)showInView:(UIView *)superView;
- (void)hide;

@end
