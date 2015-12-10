//
//  KKCustomAlertView.m
//  KKOBD
//
//  Created by zhuyc on 13-8-15.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKCustomAlertView.h"

@implementation KKCustomAlertView

static UIImage *failFace = nil;
static UIImage *background = nil;
static UIFont *messageFont = nil;
static UIFont *buttonFont = nil;
static float messageWidth = 185;
static CGPoint startOrign;


+ (void)showAlertViewWithMessage:(NSString *)message
{
    [self showAlertViewWithMessage:message block:nil];
}

+ (void)showAlertViewWithMessage:(NSString *)message block:(void (^)())block
{
    KKCustomAlertView *alertView = [[self alloc] initWithMessage:message WithType:KKCustomAlertView_default];
    [alertView addButtonWithTitle:@"确定" imageName:@"alert-blue2-button.png" block:block];
    [alertView show];
    [alertView release];
}

+ (void)showErrorAlertViewWithMessage:(NSString *)message block:(void (^)())block
{
    KKCustomAlertView *errAlertView = [[KKCustomAlertView alloc] initWithMessage:message WithType:KKCustomAlertView_error];
    [errAlertView addButtonWithTitle:@"确定" imageName:@"alert-blue2-button.png" block:block];
    [errAlertView show];
    [errAlertView release];
}

- (id)initWithMessage:(NSString *)message WithType:(KKCustomAlertViewType)type
{
    self = [super init];
    if (self) {
        
        startOrign = CGPointMake(24, 24);
        failFace = [[UIImage imageNamed:@"failFace.png"] retain];
        background = [[UIImage imageNamed:@"bg_alertView.png"] retain];
        messageFont = [UIFont boldSystemFontOfSize:15.0f];
        buttonFont = [UIFont boldSystemFontOfSize:16.0];
        
        _height += startOrign.y;
        _type = type;
        _message = message;
        _blocks = [[NSMutableArray alloc] init];
    }
    [self setFrame:[UIApplication sharedApplication].keyWindow.bounds];
    
    return self;
}

- (void)addButtonWithTitle:(NSString *)title imageName:(NSString*)name block:(void (^)())block
{
    [_blocks addObject:[NSArray arrayWithObjects:
                        block ? [[block copy] autorelease] : [NSNull null],
                        title,
                        name,
                        nil]];
}


- (void)show
{
    [self showInView:nil];
}

- (void)showInView:(UIView *)superView
{
    CGSize bgSize = background.size;
    
    _contentView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, bgSize.width, bgSize.height)];
    _contentView.backgroundColor = [UIColor clearColor];
    _contentView.userInteractionEnabled = YES;
    
    if (_type == KKCustomAlertView_error)
    {
        _height += 12;
        
        UIImageView *failfaceImv = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(bgSize.width - failFace.size.width), _height, failFace.size.width, failFace.size.height)];
        failfaceImv.backgroundColor = [UIColor clearColor];
        failfaceImv.image = failFace;
        [_contentView addSubview:failfaceImv];
        [failfaceImv release];
        
        _height += (failFace.size.height + 15);
        
    }
    else
        _height += 40;
    
    CGSize size = CGSizeZero;
    
    UILabel *msgLabel = [[UILabel alloc] initWithFrame:CGRectZero];
    msgLabel.backgroundColor = [UIColor clearColor];
    msgLabel.font = messageFont;
    msgLabel.text = _message;
    msgLabel.textColor = [UIColor whiteColor];
    msgLabel.textAlignment = UITextAlignmentCenter;
    msgLabel.numberOfLines = 0;
    size = [_message sizeWithFont:messageFont constrainedToSize:CGSizeMake(messageWidth, MAXFLOAT)];
    [msgLabel setFrame:CGRectMake(0.5*(bgSize.width - messageWidth), _height, messageWidth, size.height)];
    [_contentView addSubview:msgLabel];
    [msgLabel release];
    
    _height += size.height;
    
    if (_type == KKCustomAlertView_error)
        _height += 18;
    else
        _height += 25;
    
    //只计算最多两个button
    
    float imageheight = 0;
    
    for (NSUInteger i = 0; i < _blocks.count; i++)
    {
        NSArray *block = [_blocks objectAtIndex:i];
        NSString *title = [block objectAtIndex:1];
        NSString *name = [block objectAtIndex:2];
        
        UIImage *image = [UIImage imageNamed:[NSString stringWithFormat:@"%@", name]];
        imageheight = image.size.height;
        
        image = [image stretchableImageWithLeftCapWidth:(int)(image.size.width+1)>>1 topCapHeight:0];
        
//        size = [title sizeWithFont:buttonFont
//                       minFontSize:10
//                    actualFontSize:nil
//                          forWidth:0.5*(background.size.width - 2*startOrign.x)
//                     lineBreakMode:0];
        
        UIButton *button = [[UIButton alloc] initWithFrame:CGRectZero];
        [button setBackgroundImage:image forState:UIControlStateNormal];
        button.titleLabel.font = buttonFont;
        button.titleLabel.minimumFontSize = 10;
        [button setTitle:title forState:UIControlStateNormal];
        button.titleLabel.textAlignment = UITextAlignmentCenter;
        button.backgroundColor = [UIColor clearColor];
        [button addTarget:self action:@selector(buttonClicked:) forControlEvents:UIControlEventTouchUpInside];
        button.tag = i+1;
        
        if (_blocks.count == 1)
        {
            [button setFrame:CGRectMake(startOrign.x + 0.5*(background.size.width - 2*startOrign.x - image.size.width),_height, image.size.width, image.size.height)];
        }
        else
        {
            if (i == 0)
            {
                [button setFrame:CGRectMake(startOrign.x + 0.5*(0.5*background.size.width - startOrign.x - image.size.width),_height, image.size.width, image.size.height)];
            }
            else
            {
                [button setFrame:CGRectMake(0.5*background.size.width + 0.5*(0.5*background.size.width - startOrign.x - image.size.width),_height, image.size.width, image.size.height)];
            }
        }
        
        [_contentView addSubview:button];
        [button release];
    }
    
    _height += imageheight;
    _height += 30;
    [_contentView setFrame:CGRectMake(0, 0, bgSize.width, MAX(_height, bgSize.height))];
    _contentView.image = [background stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    _contentView.center = self.center;
    [self addSubview:_contentView];
    [_contentView release];
    
    if (superView == nil)
    {
        UIWindow *window = [UIApplication sharedApplication].keyWindow;
        [window addSubview:self];
    }
    else
        [superView addSubview:self];
}

- (void)dismissWithClickedButtonIndex:(NSInteger)buttonIndex animated:(BOOL)animated
{
    if (buttonIndex >= 0 && buttonIndex < [_blocks count])
    {
        id obj = [[_blocks objectAtIndex: buttonIndex] objectAtIndex:0];
        if (![obj isEqual:[NSNull null]])
        {
            ((void (^)())obj)();
        }
    }
    
    [self removeFromSuperview];
}

- (void)buttonClicked:(id)sender
{
    NSInteger tag = [sender tag] - 1;
    [self dismissWithClickedButtonIndex:tag animated:YES];
}

- (void)dealloc
{
    if (_blocks)
        [_blocks release];
    _blocks = nil;
    if (failFace)
        [failFace release];
    failFace = nil;
    if (background)
        [background release];
    background = nil;
    
    [super dealloc];
}
@end
