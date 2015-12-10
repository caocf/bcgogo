//
//  KKHelper.m
//  KKOBD
//
//  Created by zhuyc on 13-9-2.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKHelper.h"
#import "RegexKitLite.h"
#include <sys/types.h>
#include <sys/sysctl.h>
#import "KKApplicationDefine.h"
#import <CoreLocation/CoreLocation.h>
#import "KKGlobal.h"
#import "KKTBDictFault.h"
#import "KKDB.h"
#import "KKTBDTCMessage.h"

@implementation KKHelper

+(BOOL)KKHelpRegexMatch:(NSString *)matchString withType:(KKRegexMatchType)type withMinLength:(NSInteger)minLength withMaxLength:(NSInteger)maxLength
{    
    NSString *regexString = nil;
    switch (type) {
        case e_RegexType_All:
            regexString = [NSString stringWithFormat:@"^.{%d,%d}$",minLength,maxLength];
            break;
        case e_RegexType_LetterAndNum:
            regexString = [NSString stringWithFormat:@"^[A-Za-z0-9]{%d,%d}$",minLength,maxLength];
            break;
        case e_RegexType_Num:
            regexString = [NSString stringWithFormat:@"^[0-9]{%d,%d}$",minLength,maxLength];
            break;
        case E_RegexType_Float:
            regexString = [NSString stringWithFormat:@"^[0-9]+(.[0-9]+)?$"];
            break;
        default:
            break;
    }
    return [matchString isMatchedByRegex:regexString];;
}

//电话号码
+(BOOL)KKHElpRegexMatchForTelephone:(NSString *)phoneStr
{
//    return [phoneStr isMatchedByRegex:@"^1[3|4|5|8][0-9]\\d{4,8}$"];
    return [phoneStr isMatchedByRegex:@"^(\\+86)?0?1[3|4|5|8]\\d{9}$"];
}

//车牌号
+(BOOL)KKHElpRegexMatchForVehicleNo:(NSString *)vehicleNo
{
    return [vehicleNo isMatchedByRegex:@"^[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$"];
}

//邮箱
+(BOOL)KKHElpRegexMatchForEmail:(NSString *)email
{
    return [email isMatchedByRegex:@"\\b([a-zA-Z0-9%_.+\\-]+)@([a-zA-Z0-9.\\-]+?\\.[a-zA-Z]{2,6})\\b"];
}

//浮点数
+(BOOL)KKHElpRegexMatchForFloatValue:(NSString *)floatValue
{
    return [floatValue isMatchedByRegex:@"^\\d+(\\.\\d+)?$"];
}

//店铺ID
+(BOOL)KKHElpRegexMatchForNum:(NSString *)numStr
{
    return [numStr isMatchedByRegex:@"^[0-9]."];
}

+ (NSString *) platformString {
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

+ (NSString *) imageVersion
{    
    CGRect rect_screen = [[UIScreen mainScreen]bounds];
    NSInteger width = rect_screen.size.width , height = rect_screen.size.height;
    NSInteger scale_screen = [UIScreen mainScreen].scale;
    
    return [NSString stringWithFormat:@"%dX%d",width*scale_screen,height*scale_screen];
}

+ (NSArray *)getArray:(NSString *)sepStr BySeparateString:(NSString *)str
{
    NSArray *array = [sepStr componentsSeparatedByString:str];
    NSMutableArray *temArr = [[NSMutableArray alloc] init];
    
    for (NSString *str in array) {
        if ([str length] > 0)
        {
            [temArr addObject:str];
        }
    }
    return [temArr autorelease];
}

+ (UIView *)creatCarBrandMarkView:(CGRect)rect withTitle:(NSString *)title
{
    UIView *markView= [[UIView alloc] initWithFrame:rect];
    markView.backgroundColor = [UIColor clearColor];
    markView.userInteractionEnabled = YES;
    
    CGSize size = [title sizeWithFont:[UIFont systemFontOfSize:13.f] constrainedToSize:CGSizeMake(100, 15)];
    float orignX = 0.5*(rect.size.width - size.width);
    
    UILabel *txtLb = [[UILabel alloc] initWithFrame:CGRectMake(orignX, 0.5*(rect.size.height - 15), size.width, 15)];
    txtLb.textAlignment = UITextAlignmentCenter;
    txtLb.textColor = KKCOLOR_A7a6a6;
    txtLb.text = title;
    txtLb.font = [UIFont systemFontOfSize:13.f];
    [markView addSubview:txtLb];
    [txtLb release];
    
    UIImage *image = [UIImage imageNamed:@"icon_setting_bind_cross.png"];
    UIImageView *iconImv = [[UIImageView alloc] initWithFrame:CGRectMake(orignX - image.size.width - 3, 0.5*(rect.size.height - image.size.height), image.size.width, image.size.height)];
    iconImv.image = image;
    [markView addSubview:iconImv];
    [iconImv release];
    
    return [markView autorelease];
}

+ (NSString*) getPathWithinDocumentDir:(NSString*) aPath {
	[aPath retain];
	
	NSString *fullPath = nil;
	NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	
	if ([paths count] > 0) {
		fullPath = (NSString *)[paths objectAtIndex:0];
		if(aPath != nil && [aPath compare:@""] != NSOrderedSame) {
			fullPath = [fullPath stringByAppendingPathComponent:aPath];
		}
	}
	
	[aPath release];
	
	return fullPath;
}

+ (BOOL)isBlueSupport
{
    return YES;
}

+ (NSString *)covertCFUUIDRefToString:(CFUUIDRef)uuid
{
    CFStringRef uuidStrRef = CFUUIDCreateString(kCFAllocatorSystemDefault, uuid);
    NSString *ret = [NSString stringWithFormat:@"%@", (NSString*)uuidStrRef];
    CFRelease(uuidStrRef);
    return ret;
}

+(double)distanceBetweenOrderBy:(double)lat1 :(double)lat2 :(double)lng1 :(double)lng2{
    CLLocation* curLocation = [[CLLocation alloc] initWithLatitude:lat1 longitude:lng1];
    CLLocation* otherLocation = [[CLLocation alloc] initWithLatitude:lat2 longitude:lng2];
    double distance  = [curLocation distanceFromLocation:otherLocation];
    [curLocation release];
    [otherLocation release];
    return distance;
}

+(BOOL)haveSubStringWithPstr:(NSString *)pStr andCstr:(NSString *)cstr
{
    NSRange range = [pStr rangeOfString:cstr];
    if (range.length > 0)
        return YES;
    return NO;
}

+ (KKModelPlatform *)getMobilePlatform
{    
    KKModelPlatform *platformInfo = [[KKModelPlatform alloc] init];
    platformInfo.platform = CurrentSystemPlatform;
    platformInfo.platformVersion = CurrentSystemVersion;
    platformInfo.mobileModel = [KKHelper platformString];
    platformInfo.appVersion = KK_Version;
    platformInfo.imageVersion = [KKHelper imageVersion];
    return [platformInfo autorelease];
}

+ (NSString *)getVehicleFaultDesWithFaultCode:(NSString *)faultCode andVehicleModelId:(NSString *)modelId
{
    if ([faultCode length] == 0)
        return nil;
    
    KKTBDictFault *faultDict = [[KKTBDictFault alloc] initWithDB:[KKDB sharedDB]];
    NSArray *arr = [faultDict getFaultInfoWithCode:faultCode vehicleModelId:modelId];
    [faultDict release];
    if ([arr count] > 0)
    {
        NSString *message = [NSString string];
        for(NSString *string in arr)
        {
            message = [message stringByAppendingString:[NSString stringWithFormat:@",%@",string]];
        }
        if ([message length] > 0)
        {
            message = [message stringByReplacingCharactersInRange:NSMakeRange(0, 1) withString:@""];
            
            return [NSString stringWithFormat:@"%@:%@",faultCode,message];
        }
        else
            return faultCode;
    }
    else
        return faultCode;
}

+ (NSString *)getShopListCoordinate2DString:(CLLocationCoordinate2D)currentCoo
{
    NSString *cooString = nil;
    if (CLLocationCoordinate2DIsValid(currentCoo))
        cooString = [NSString stringWithFormat:@"%f,%f",currentCoo.longitude,currentCoo.latitude];
    return cooString;
}

+(NSString *) meterToKiloFromInt:(int)aMeter
{
    float kilo = (float)aMeter/1000;
    return [NSString stringWithFormat:@"%.2fkm",kilo];
}

+(NSString *) meterToKiloFromString:(NSString *)aMeter
{
    return [self meterToKiloFromInt:[aMeter integerValue]];
}

+(NSDate *)dateFromString:(NSString *)dateString
{
    
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    
    [dateFormatter setDateFormat: @"yyyy-MM-dd HH:mm"];
    NSDate *destDate= [dateFormatter dateFromString:dateString];
    [dateFormatter release];
    
    return destDate;
}
@end
