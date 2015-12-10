//
//  TGViewUtils.m
//  TGOBD
//
//  Created by Jiahai on 14-3-6.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGViewUtils.h"

@implementation TGViewUtils
+ (UIBarButtonItem*)createNavigationBarButtonItem:(UIImage*)aImage bgImage:(UIImage*)bgImage
                                           target:(id)aTarget action:(SEL)aAction
{
    UIButton* button = [UIButton buttonWithType:UIButtonTypeCustom];
	UIImage *img = aImage?aImage:bgImage;

    [button setFrame:CGRectMake(0, 0, 44, 44)];
    [button setImage:img forState:UIControlStateNormal];
    [button addTarget:aTarget action:aAction forControlEvents:UIControlEventTouchUpInside];
    
    [button setExclusiveTouch:YES];
    
    UIBarButtonItem* item = [[UIBarButtonItem alloc] initWithCustomView:button];
    
    return item;
}

+ (UIBarButtonItem*)createNavigationBarButtonItemWithTitle:(NSString*)title bgImage:(UIImage*)bgImage
                                                    target:(id)aTarget action:(SEL)aAction
{
    UIButton* button = [UIButton buttonWithType:UIButtonTypeCustom];
    button.tag = 2012;
    UIFont* font = [UIFont systemFontOfSize:13.0f];
    button.titleLabel.font = font;
    CGSize size = [title sizeWithFont:font];
    CGFloat buttonWidth = 0, buttonHeight = 30.f;
    if (size.width < 43)
    {
		if ([title length]==0 && bgImage.size.width > 0) {
			buttonHeight = bgImage.size.height;
			buttonWidth = bgImage.size.width;
		}
		else
			buttonWidth = 43;
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
    
    UIBarButtonItem* item = [[UIBarButtonItem alloc] initWithCustomView:button];
    item.target = aTarget;
    item.action = aAction;
    return item;
}

@end
