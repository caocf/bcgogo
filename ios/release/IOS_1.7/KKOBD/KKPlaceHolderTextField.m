//
//  KKPlaceHolderTextField.m
//  KKOBD
//
//  Created by zhuyc on 13-10-16.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKPlaceHolderTextField.h"

@implementation KKPlaceHolderTextField

- (void)drawPlaceholderInRect:(CGRect)rect
{
    [[UIColor whiteColor] setFill];
    
    [[self placeholder] drawInRect:CGRectMake(0, 0.5*(rect.size.height - 15), rect.size.width, 15) withFont:[UIFont systemFontOfSize:15]];
}

@end
