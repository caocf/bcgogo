//
//  TGUtils.m
//  TGYIFA
//
//  Created by James Yu on 14-5-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGUtils.h"

@implementation TGUtils

+ (UIButton *)createNavigationBarButtonItem:(UIImage *)aImage bgImage:(UIImage *)bgImage target:(id)aTarget action:(SEL)aAction
{
    UIButton* button = [UIButton buttonWithType:UIButtonTypeCustom];
	UIImage *img = aImage;
    
    if(aImage == nil || bgImage == nil)
        img = aImage?aImage:bgImage;
    
    [button setFrame:CGRectMake(0, 0, 40, 40)];
    
    [button setImage:img forState:UIControlStateNormal];
    if(bgImage)
        [button setBackgroundImage:bgImage forState:UIControlStateNormal];
    
    [button addTarget:aTarget action:aAction forControlEvents:UIControlEventTouchUpInside];
    
    [button setExclusiveTouch:YES];
    
    return button;
}

+ (UIButton *)createNavigationBarButtonItemWithTitle:(NSString*)title bgImage:(UIImage*)bgImage target:(id)aTarget action:(SEL)aAction
{
    UIButton* button = [UIButton buttonWithType:UIButtonTypeCustom];
    button.tag = 2012;
    UIFont* font = [UIFont systemFontOfSize:16.0f];
    button.titleLabel.font = font;
    CGSize size = [title sizeWithFont:font];
    CGFloat buttonWidth = 0, buttonHeight = 40.f;
    if (size.width < 40)
    {
		if ([title length]==0 && bgImage.size.width > 0) {
			buttonHeight = bgImage.size.height;
			buttonWidth = bgImage.size.width;
		}
		else
			buttonWidth = 40;
    }
    else
    {
        buttonWidth = size.width + 10;
    }
    button.frame = CGRectMake(0,0,buttonWidth,buttonHeight);
    
    [button setTitle:title forState:UIControlStateNormal];
    [button setBackgroundImage:bgImage forState:UIControlStateNormal];
    [button addTarget:aTarget action:aAction forControlEvents:UIControlEventTouchUpInside];
    
    [button setExclusiveTouch:YES];
    
    return button;
}

+ (NSString *)getImageVersion
{
    CGRect rect_screen = [[UIScreen mainScreen]bounds];
    NSInteger width = rect_screen.size.width , height = rect_screen.size.height;
    NSInteger scale_screen = [UIScreen mainScreen].scale;
    
    return [NSString stringWithFormat:@"%ldX%ld",width*scale_screen,height*scale_screen];
}

+ (NSString *)stringWithNoSpaceAndNewLine:(NSString *)sting
{
    NSCharacterSet *charSet = [NSCharacterSet whitespaceAndNewlineCharacterSet];
    
    NSString *tmp = [sting stringByTrimmingCharactersInSet:charSet];
    
    return [tmp stringByReplacingOccurrencesOfString:@" " withString:@""];
}

@end
