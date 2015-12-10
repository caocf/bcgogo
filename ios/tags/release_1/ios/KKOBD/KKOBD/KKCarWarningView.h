//
//  KKCarWarningView.h
//  KKOBD
//
//  Created by zhuyc on 13-8-20.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
typedef enum
{
    KKCarWarningType_0,
    KKCarWarningType_1,
}KKCarWarningType;

@protocol KKCarWarningViewDelegate;

@interface KKCarWarningView : UIView
{
    UIView              *_bgView;
    UIImageView         *_contentView;
    UIImageView         *_warningImv;
    UILabel             *_titleLb;
    UILabel             *_messageLb;
 
    NSString            *_title;
    NSString            *_message;
    KKCarWarningType     _warningType;
    float                _height;
}
@property (nonatomic ,assign) id<KKCarWarningViewDelegate> delegate;

- (id)initWithTitle:(NSString *)title
        WithMessage:(NSString *)message
    WithWarningType:(KKCarWarningType)warningType;

- (void)show;

@end

@protocol KKCarWarningViewDelegate
@optional
- (void)KKCarWarningViewButtonClicked:(NSInteger)index andFaultCode:(NSString *)faultCode;

@end