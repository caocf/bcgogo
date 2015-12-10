//
//  TGHelper.h
//  TGOBD
//
//  Created by Jiahai on 14-3-3.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CLLocation.h>

typedef struct {
    long long   startTime;
    long long   endTime;
}DateTimeRange;

@interface TGHelper : NSObject

/**
 *  生成UUID
 *
 *  @return NSString
 */
+ (NSString *)createUUIDString;

/**
 *  获取Document下的完整文件路径
 *
 *  @param aPath document下的路径
 *
 *  @return NSString
 */
+ (NSString *)getPathWithinDocumentDir:(NSString *)aPath;

/**
 *  Int型 米转换成千米
 *
 *  @param aMeter 米
 *
 *  @return NSString
 */
+ (NSString *)meterToKiloFromInt:(int) aMeter;

/**
 *  String型 米转换成千米
 *
 *  @param aMeter 米
 *
 *  @return NSString
 */
+ (NSString *)meterToKiloFromString:(NSString *)aMeter;

/**
 *  根据给定的时间，获取当天的 yyyy-MM-dd 00:00:00 时间
 *
 *  @param aTime 时间毫秒数
 *
 *  @return 毫秒数
 */
+(long long) getDayStartTime:(long long)aTime;

/**
 *  根据给定的时间，获取当天的 yyyy-MM-dd 23:59:59 时间
 *
 *  @param aTime 时间毫秒数
 *
 *  @return 毫秒数
 */
+(long long) getDayEndTime:(long long)aTime;

/**
 *  根据给定的时间格式化成 yyyy-MM-dd hh:mm:ss
 *
 *  @param date 给定的时间
 *
 *  @return 格式化的时间
 */
+ (NSString *)getFormateTimeFromTime:(NSDate *)date;

/**
 *  获取当前时间，N周之前的时间间隔。
 *
 *  @param index 前几周
 *
 *  @return DateTimeRange
 */
+ (DateTimeRange)getWeekTimeRangeWithIndex:(NSInteger)index;

/**
 *  获取所需图片分辨率
 *
 *  @return 320x480 640x960 640x1136
 */
+ (NSString *)getImageVersion;

/**
 *  获取登录平台
 *
 *  @return iphone,ipad,等
 */
+ (NSString *)getPlatformModel;

/**
 *  验证手机号码的合法性
 *
 *  @param mobile 手机号码
 *
 *  @return yes, no
 */
+ (BOOL)isValidateMobile:(NSString *)mobile;


+ (BOOL)makePhone:(NSString *)phoneNumber;

+ (CLLocationCoordinate2D)getCoordinate2DWithStringLat:(NSString *)lat stringLon:(NSString *)lon;
+ (CLLocationCoordinate2D)getBaiDuCoordinate2DWithLat:(CGFloat)lat lon:(CGFloat)lon;
+ (CLLocationCoordinate2D)getBaiDuCoordinate2DWithStringLat:(NSString *)lat stringLon:(NSString *)lon;

/**
 *  将document里面的数据禁止iCloud备份
 *
 *  @param URL 文件目录
 *
 *  @return yes，no 
 */
+ (BOOL)addSkipBackupAttributeToItemAtURL:(NSURL *)URL;

@end
