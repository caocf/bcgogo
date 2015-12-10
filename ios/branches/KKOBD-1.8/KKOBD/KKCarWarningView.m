//
//  KKCarWarningView.m
//  KKOBD
//
//  Created by zhuyc on 13-8-20.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKCarWarningView.h"

@implementation KKCarWarningView
@synthesize delegate;

- (id)initWithTitle:(NSString *)title
        WithMessage:(NSString *)message
    WithWarningType:(KKCarWarningType)warningType
{
    self = [super init];
    if (self) {
        // Initialization code
        _title = [title retain];
        _message = [message retain];
        _warningType = warningType;
        
        [self initial];
    }
    return self;
}

- (void)initial
{
    CGRect rect = [UIApplication sharedApplication].keyWindow.bounds;
    [self setFrame:rect];
    
    _bgView = [[UIView alloc] initWithFrame:rect];
    _bgView.backgroundColor = [UIColor blackColor];
    _bgView.alpha = 0.6;
    [self addSubview:_bgView];
    [_bgView release];
    
    UIImage *bgImage = [UIImage imageNamed:@"warningView_bg.png"];
    UIImage *image = [UIImage imageNamed:@"warningView_bg.png"];
    CGSize size = image.size;
    
    _contentView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, size.width, size.height)];
    _contentView.backgroundColor = [UIColor clearColor];
    _contentView.userInteractionEnabled = YES;
    
    switch (_warningType) {
        case KKCarWarningType_0:
            image = [UIImage imageNamed:@"warning_icon_type1.png"];
            break;
        case KKCarWarningType_1:
            image = [UIImage imageNamed:@"warning_icon_type2.png"];
            break;
        default:
            break;
    }
    
    _height = 44;
    
    _warningImv = [[UIImageView alloc] initWithFrame:CGRectMake(113, _height, image.size.width, image.size.height)];
    _warningImv.backgroundColor = [UIColor clearColor];
    _warningImv.image = image;
    [_contentView addSubview:_warningImv];
    [_warningImv release];
    
    _titleLb = [[UILabel alloc] initWithFrame:CGRectMake(142, _height, 130, 15)];
    _titleLb.font = [UIFont boldSystemFontOfSize:15.f];
    _titleLb.textColor = [UIColor whiteColor];
    _titleLb.backgroundColor = [UIColor clearColor];
    _titleLb.text = _title;
    _titleLb.textAlignment = UITextAlignmentLeft;
    [_contentView addSubview:_titleLb];
    [_titleLb release];
    
    _height += 30;
    
    _messageLb = [[UILabel alloc] initWithFrame:CGRectMake(42.5, _height, 235, 30)];
    _messageLb.text = _message;
    _messageLb.numberOfLines = 0;
    _messageLb.backgroundColor = [UIColor clearColor];
    _messageLb.textColor = [UIColor whiteColor];
    _messageLb.textAlignment = UITextAlignmentCenter;
    _messageLb.font = [UIFont systemFontOfSize:15.f];
    size = [_message sizeWithFont:[UIFont systemFontOfSize:15.f] constrainedToSize:CGSizeMake(235, MAXFLOAT)];
    [_messageLb setFrame:CGRectMake(42.5, _height, 235, size.height)];
    [_contentView addSubview:_messageLb];
    [_messageLb release];
    
    _height += size.height;
    _height += 15;
    
    image = [UIImage imageNamed:@"warningView_btn_blue.png"];
    UIButton *leftBtn = [[UIButton alloc] initWithFrame:CGRectMake(32, _height, image.size.width, image.size.height)];
    [leftBtn setTitle:@"知道了" forState:UIControlStateNormal];
    [leftBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [leftBtn.titleLabel setFont:[UIFont systemFontOfSize:20.f]];
    leftBtn.tag = 100;
    [leftBtn setBackgroundImage:image forState:UIControlStateNormal];
    [leftBtn addTarget:self action:@selector(buttonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_contentView addSubview:leftBtn];
    [leftBtn release];
    
    image = [UIImage imageNamed:@"warningView_btn_red.png"];
    UIButton *rightBtn = [[UIButton alloc] initWithFrame:CGRectMake(166, _height, image.size.width, image.size.height)];
//    UIButton *rightBtn = [[UIButton alloc] initWithFrame:CGRectMake((_contentView.bounds.size.width-image.size.width)*0.5, _height, image.size.width, image.size.height)];
    [rightBtn setTitle:@"立即处理" forState:UIControlStateNormal];
    [rightBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [rightBtn.titleLabel setFont:[UIFont systemFontOfSize:20.f]];
    rightBtn.tag = 101;
    [rightBtn setBackgroundImage:image forState:UIControlStateNormal];
    [rightBtn addTarget:self action:@selector(buttonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_contentView addSubview:rightBtn];
    [rightBtn release];
    
    _height += image.size.height;
    _height += 35;
    
    [_contentView setFrame:CGRectMake(0, _height, bgImage.size.width, MAX(_height, bgImage.size.height))];
    _contentView.image = [bgImage stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    _contentView.center = CGPointMake(160, 0.5*self.frame.size.height);
    [self addSubview:_contentView];
    [_contentView release];
    
}

- (void)buttonClicked:(id)sender
{
    NSInteger tag = [sender tag];
    
    if (self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(KKCarWarningViewButtonClicked:andFaultCode:)])
        [self.delegate KKCarWarningViewButtonClicked:tag andFaultCode:_title];
    
    [self removeFromSuperview];
}

- (void)show
{
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    [window addSubview:self];
}

- (void)dealloc
{
    if (_title)
        [_title release];
    if (_message)
        [_message release];
    
    [super dealloc];
}
@end
