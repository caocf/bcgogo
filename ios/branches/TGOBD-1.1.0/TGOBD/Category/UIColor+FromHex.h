//
//  UIColor+FromHex.h
//  TGOBD
//
//  Created by James Yu on 14-3-20.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIColor (FromHex)

+ (UIColor*)colorWithHex:(long)hexColor;

+ (UIColor *)colorWithHex:(long)hexColor alpha:(float)opacity;

@end
