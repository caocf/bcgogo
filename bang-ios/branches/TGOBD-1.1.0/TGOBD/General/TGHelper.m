//
//  TGHelper.m
//  TGOBD
//
//  Created by Jiahai on 14-3-3.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGHelper.h"
#import <CoreFoundation/CoreFoundation.h>
#import "NSDate+millisecond.h"
#include <sys/sysctl.h>
#import "BMKGeometry.h"

@implementation TGHelper

#define CalendarUnit_Month      NSMonthCalendarUnit|NSYearCalendarUnit
#define CalendarUnit_Date       NSDayCalendarUnit|NSMonthCalendarUnit|NSYearCalendarUnit
#define CalendarUnit_DateTime   NSDayCalendarUnit|NSMonthCalendarUnit|NSYearCalendarUnit|NSHourCalendarUnit|NSMinuteCalendarUnit|NSSecondCalendarUnit

#define OneDayMilliSeconds           (long long)86400000           //一天有多少毫秒，等于 24*60*60*1000

+ (NSString *)createUUIDString
{
    // create a new UUID which you own
    CFUUIDRef uuid = CFUUIDCreate(kCFAllocatorDefault);
    
    // create a new CFStringRef (toll-free bridged to NSString)
    // that you own
    NSString *uuidString = (NSString *)CFBridgingRelease(CFUUIDCreateString(kCFAllocatorDefault, uuid));
    
//    // transfer ownership of the string
//    // to the autorelease pool
//    [uuidString autorelease];
//    
//    // release the UUID
//    CFRelease(uuid);
    
    return uuidString;
}

+ (NSString *)getPathWithinDocumentDir:(NSString *)aPath
{
	
	NSString *fullPath = nil;
	NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	
	if ([paths count] > 0) {
		fullPath = (NSString *)[paths objectAtIndex:0];
		if(aPath != nil && [aPath compare:@""] != NSOrderedSame) {
			fullPath = [fullPath stringByAppendingPathComponent:aPath];
		}
	}
	
	return fullPath;
}

+ (NSString *)meterToKiloFromInt:(int)aMeter
{
    float kilo = (float)aMeter/1000;
    return [NSString stringWithFormat:@"%.2fkm",kilo];
}

+ (NSString *)meterToKiloFromString:(NSString *)aMeter
{
    return [self meterToKiloFromInt:[aMeter integerValue]];
}

+(long long) getDayStartTime:(long long)aTime
{
    //根据给定的时间，获取当天的 yyyy-MM-dd 00:00:00 时间
    NSDate *date = [NSDate dateWithTimeIntervalSince1970WithMillisecond:aTime];
    NSDateComponents *comps = [[NSCalendar currentCalendar] components:CalendarUnit_Date fromDate:date];
    NSDate *date1 = [[NSCalendar currentCalendar] dateFromComponents:comps];
    return [date1 timeIntervalSince1970WithMillisecond];
}

+(long long) getDayEndTime:(long long)aTime
{
    //根据给定的时间，获取当天的 yyyy-MM-dd 23:59:59 时间
    NSDate *date = [NSDate dateWithTimeIntervalSince1970WithMillisecond:aTime];
    NSDateComponents *comps = [[NSCalendar currentCalendar] components:CalendarUnit_DateTime fromDate:date];
    [comps setHour:23];
    [comps setMinute:59];
    [comps setSecond:59];
    return [[[NSCalendar currentCalendar] dateFromComponents:comps] timeIntervalSince1970WithMillisecond];
}

+ (NSString *)getFormateTimeFromTime:(NSDate *)date
{
    NSAssert(date != nil, @"给定的时间不能为空");
    
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd hh:mm:ss"];
    
    return [formatter stringFromDate:date];
}

+ (DateTimeRange)getDayTimeRangeWithIndex:(NSInteger)index
{
    DateTimeRange timeRange;
    
    return timeRange;
}

+ (DateTimeRange)getWeekTimeRangeWithIndex:(NSInteger)index
{
    NSCalendar *calendar = [NSCalendar currentCalendar];
    calendar.firstWeekday = 2;
    
    DateTimeRange timeRange,currentTimeRange;
    
    currentTimeRange.startTime  = [TGHelper getDayStartTime:[[NSDate date] timeIntervalSince1970WithMillisecond]];
    
    int startIndexInWeek = [calendar ordinalityOfUnit:NSDayCalendarUnit inUnit:NSWeekCalendarUnit forDate:[NSDate dateWithTimeIntervalSince1970WithMillisecond:currentTimeRange.startTime]];
    
//    if(index == 0)
//    {
//        timeRange.startTime = currentTimeRange.startTime;
//        timeRange.endTime = currentTimeRange.startTime + 7*OneDayMilliSeconds -1;
//    }
//    else
//    {
    timeRange.startTime = currentTimeRange.startTime + (-startIndexInWeek + 1 + index*7)*OneDayMilliSeconds;
    timeRange.endTime = currentTimeRange.startTime + (-startIndexInWeek + 1 + (index + 1)*7)*OneDayMilliSeconds-1;
//    }
    return timeRange;
}

+ (NSString *)getImageVersion
{
    CGRect rect_screen = [[UIScreen mainScreen]bounds];
    NSInteger width = rect_screen.size.width , height = rect_screen.size.height;
    NSInteger scale_screen = [UIScreen mainScreen].scale;
    
    return [NSString stringWithFormat:@"%dX%d",width*scale_screen,height*scale_screen];
}

+ (NSString *)getPlatformModel {
    // Gets a string with the device model
    size_t size;
    sysctlbyname("hw.machine", NULL, &size, NULL, 0);
    char *machine = malloc(size);
    sysctlbyname("hw.machine", machine, &size, NULL, 0);
    NSString *platform = [NSString stringWithCString:machine encoding:NSUTF8StringEncoding];
    free(machine);
    
    if ([platform isEqualToString:@"iPhone1,1"])    return @"iPhone 2G";
    if ([platform isEqualToString:@"iPhone1,2"])    return @"iPhone 3G";
    if ([platform isEqualToString:@"iPhone2,1"])    return @"iPhone 3GS";
    if ([platform isEqualToString:@"iPhone3,1"])    return @"iPhone 4";
    if ([platform isEqualToString:@"iPhone3,2"])    return @"iPhone 4";
    if ([platform isEqualToString:@"iPhone3,3"])    return @"iPhone 4 (CDMA)";
    if ([platform isEqualToString:@"iPhone4,1"])    return @"iPhone 4S";
    if ([platform isEqualToString:@"iPhone5,1"])    return @"iPhone 5";
    if ([platform isEqualToString:@"iPhone5,2"])    return @"iPhone 5 (GSM+CDMA)";
    
    if ([platform isEqualToString:@"iPod1,1"])      return @"iPod Touch (1 Gen)";
    if ([platform isEqualToString:@"iPod2,1"])      return @"iPod Touch (2 Gen)";
    if ([platform isEqualToString:@"iPod3,1"])      return @"iPod Touch (3 Gen)";
    if ([platform isEqualToString:@"iPod4,1"])      return @"iPod Touch (4 Gen)";
    if ([platform isEqualToString:@"iPod5,1"])      return @"iPod Touch (5 Gen)";
    
    if ([platform isEqualToString:@"iPad1,1"])      return @"iPad";
    if ([platform isEqualToString:@"iPad1,2"])      return @"iPad 3G";
    if ([platform isEqualToString:@"iPad2,1"])      return @"iPad 2 (WiFi)";
    if ([platform isEqualToString:@"iPad2,2"])      return @"iPad 2";
    if ([platform isEqualToString:@"iPad2,3"])      return @"iPad 2 (CDMA)";
    if ([platform isEqualToString:@"iPad2,4"])      return @"iPad 2";
    if ([platform isEqualToString:@"iPad2,5"])      return @"iPad Mini (WiFi)";
    if ([platform isEqualToString:@"iPad2,6"])      return @"iPad Mini";
    if ([platform isEqualToString:@"iPad2,7"])      return @"iPad Mini (GSM+CDMA)";
    if ([platform isEqualToString:@"iPad3,1"])      return @"iPad 3 (WiFi)";
    if ([platform isEqualToString:@"iPad3,2"])      return @"iPad 3 (GSM+CDMA)";
    if ([platform isEqualToString:@"iPad3,3"])      return @"iPad 3";
    if ([platform isEqualToString:@"iPad3,4"])      return @"iPad 4 (WiFi)";
    if ([platform isEqualToString:@"iPad3,5"])      return @"iPad 4";
    if ([platform isEqualToString:@"iPad3,6"])      return @"iPad 4 (GSM+CDMA)";
    
    if ([platform isEqualToString:@"i386"])         return @"Simulator";
    if ([platform isEqualToString:@"x86_64"])       return @"Simulator";
    
    return platform;
}

+ (BOOL)isValidateMobile:(NSString *)mobile
{
    NSString *phoneRegex = @"^(\\+86)?0?1[3|4|5|8]\\d{9}$";
    NSPredicate *phoneTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", phoneRegex];
    return [phoneTest evaluateWithObject:mobile];
}

+ (BOOL)makePhone:(NSString *)phoneNumber
{
    NSString* number = [NSString stringWithString:phoneNumber];
    NSString* numberAfterClear = [[[number stringByReplacingOccurrencesOfString:@" " withString:@""]
                                   stringByReplacingOccurrencesOfString:@"(" withString:@""]
                                  stringByReplacingOccurrencesOfString:@")" withString:@""];
    
    NSURL *phoneNumberURL = [NSURL URLWithString:[NSString stringWithFormat:@"tel://%@", numberAfterClear]];
        
    return [[UIApplication sharedApplication] openURL:phoneNumberURL];
}

+ (CLLocationCoordinate2D)getCoordinate2DWithStringLat:(NSString *)lat stringLon:(NSString *)lon
{
    return CLLocationCoordinate2DMake([lat floatValue], [lon floatValue]);
}

+ (CLLocationCoordinate2D)getBaiDuCoordinate2DWithLat:(CGFloat)lat lon:(CGFloat)lon
{
    return BMKCoorDictionaryDecode(BMKBaiduCoorForWgs84(CLLocationCoordinate2DMake(lat, lon)));
}

+ (CLLocationCoordinate2D)getBaiDuCoordinate2DWithStringLat:(NSString *)lat stringLon:(NSString *)lon
{
    CGFloat latF = [lat doubleValue];
    CGFloat lonF = [lon doubleValue];
    
    return [TGHelper getBaiDuCoordinate2DWithLat:latF lon:lonF];
}

+ (BOOL)addSkipBackupAttributeToItemAtURL:(NSURL *)URL
{
    assert([[NSFileManager defaultManager] fileExistsAtPath: [URL path]]);
    
    NSError *error = nil;
    BOOL success = [URL setResourceValue: [NSNumber numberWithBool: YES]
                                  forKey: NSURLIsExcludedFromBackupKey error: &error];
    if(!success){
        NSLog(@"Error excluding %@ from backup %@", [URL lastPathComponent], error);
    }
    return success;
}

+ (NSString *)stringWithNoSpaceAndNewLine:(NSString *)sting
{
    NSCharacterSet *charSet = [NSCharacterSet whitespaceAndNewlineCharacterSet];
    
    NSString *tmp = [sting stringByTrimmingCharactersInSet:charSet];
    
    return [tmp stringByReplacingOccurrencesOfString:@" " withString:@""];
}

@end
