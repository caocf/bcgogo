//
//  NSDate+millisecond.h
//  TGOBD
//
//  Created by Jiahai on 14-3-11.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSDate (millisecond)

- (long long)timeIntervalSince1970WithMillisecond;      //毫秒
+ (instancetype)dateWithTimeIntervalSince1970WithMillisecond:(NSTimeInterval)secs;
+ (instancetype)dateWithTimeIntervalSince1970WithMillisecondString:(NSString *)secsStr;
+ (NSString *)dateStringWithTimeIntervalSince1970WithMillisecond:(NSTimeInterval)secs formatter:(NSString *)formatter;
+ (long long)timeIntervalSince1970WithMillisecondFromString:(NSString *)time formatter:(NSString *)formatter;

/**
 *  根据时间间隔（单位：秒）获取时间的字符串
 *
 *  @param secs 时间间隔
 *
 *  @return 时间间隔字符串 单位为 Min
 */
+ (NSString *)dateIntervalStringWithSeconds:(NSInteger)secs;

/**
 *  根据时间间隔（单位：秒）获取时间的字符串
 *
 *  @param secs 时间间隔
 *
 *  @return 时间间隔字符串 单位为 H
 */
+ (NSString *)dateIntervalHourStringWithSeconds:(NSInteger)secs;
@end
