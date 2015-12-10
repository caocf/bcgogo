//
//  NSDate+millisecond.m
//  TGOBD
//
//  Created by Jiahai on 14-3-11.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "NSDate+millisecond.h"

@implementation NSDate (millisecond)

-(long long) timeIntervalSince1970WithMillisecond
{
    return [self timeIntervalSince1970]*1000;
}

+ (instancetype)dateWithTimeIntervalSince1970WithMillisecond:(NSTimeInterval)secs
{
    return [NSDate dateWithTimeIntervalSince1970:secs/1000];
}

+ (instancetype) dateWithTimeIntervalSince1970WithMillisecondString:(NSString *)secsStr
{
    return [NSDate dateWithTimeIntervalSince1970WithMillisecond:[secsStr longLongValue]];
}

+ (NSString *)dateStringWithTimeIntervalSince1970WithMillisecond:(NSTimeInterval)secs formatter:(NSString *)formatter
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:formatter == nil ? @"yyyy-MM-dd HH:mm" : formatter];
    return [dateFormatter stringFromDate:[NSDate dateWithTimeIntervalSince1970WithMillisecond:secs]];
}

+ (long long)timeIntervalSince1970WithMillisecondFromString:(NSString *)time formatter:(NSString *)formatter
{
    if (time == nil) {
        return 0;
    }
    
    NSDateFormatter *dateFormatter =[[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:formatter == nil ? @"yyyy-MM-dd HH:mm" : formatter];
    NSDate *date = [dateFormatter dateFromString:time];
    
    return [date timeIntervalSince1970WithMillisecond];
}

+ (NSString *)dateIntervalStringWithSeconds:(NSInteger)secs
{
    NSMutableString *str = [[NSMutableString alloc] init];
    
    [str appendString:[NSString stringWithFormat:@"%.1f",secs/60.0]];
    return str;
}

+ (NSString *)dateIntervalHourStringWithSeconds:(NSInteger)secs
{
    NSMutableString *str = [[NSMutableString alloc] init];
    
    [str appendString:[NSString stringWithFormat:@"%.1f",secs/3600.0]];
    return str;
}
@end
