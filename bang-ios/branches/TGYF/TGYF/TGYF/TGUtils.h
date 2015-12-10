//
//  TGUtils.h
//  TGYIFA
//
//  Created by James Yu on 14-5-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TGUtils : NSObject
+ (UIButton *)createNavigationBarButtonItem:(UIImage *)aImage bgImage:(UIImage *)bgImage target:(id)aTarget action:(SEL)aAction;

+ (UIButton *)createNavigationBarButtonItemWithTitle:(NSString*)title bgImage:(UIImage*)bgImage target:(id)aTarget action:(SEL)aAction;

+ (NSString *)getImageVersion;

/**
 *  去除字符串中的所有空格和回车
 *
 *  @param sting 待处理字符串
 *
 *  @return 处理后的字符串
 */
+ (NSString *)stringWithNoSpaceAndNewLine:(NSString *)sting;

@end
