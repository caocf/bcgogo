//
//  KKHelper.h
//  KKOBD
//
//  Created by zhuyc on 13-9-2.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>
#import "KKModelBaseElement.h"

#define nilOrString(text) \
    ([text length] > 0)? ([NSString stringWithFormat:@"%@",text]):(nil)

#define nilToDefaultString(text,replaceStr) \
    ([text length] > 0)? ([NSString stringWithFormat:@"%@",text]):(replaceStr)

#define KKPollInfoArray 

typedef enum
{
    e_RegexType_All,
    e_RegexType_LetterAndNum,
    e_RegexType_Num,
    E_RegexType_Float
}KKRegexMatchType;

@interface KKHelper : NSObject

+(BOOL)KKHelpRegexMatch:(NSString *)matchString withType:(KKRegexMatchType)type withMinLength:(NSInteger)minLength withMaxLength:(NSInteger)maxLength;
+(BOOL)KKHElpRegexMatchForTelephone:(NSString *)phoneStr;
+(BOOL)KKHElpRegexMatchForVehicleNo:(NSString *)vehicleNo;
+(BOOL)KKHElpRegexMatchForEmail:(NSString *)email;
+(BOOL)KKHElpRegexMatchForFloatValue:(NSString *)floatValue;

+ (NSString *) platformString;
+ (NSString *) imageVersion;
+ (NSArray *)getArray:(NSString *)sepStr BySeparateString:(NSString *)str;
+ (UIView *)creatCarBrandMarkView:(CGRect)rect withTitle:(NSString *)title;
+ (NSString*) getPathWithinDocumentDir:(NSString*) aPath;
+ (BOOL)isBlueSupport;
+ (NSString *)covertCFUUIDRefToString:(CFUUIDRef)uuid;
+(double)distanceBetweenOrderBy:(double)lat1 :(double)lat2 :(double)lng1 :(double)lng2;
+(BOOL)haveSubStringWithPstr:(NSString *)pStr andCstr:(NSString *)cstr;
+ (KKModelPlatform *)getMobilePlatform;
+ (NSString *)getVehicleFaultDesWithFaultCode:(NSString *)faultCode andVehicleModelId:(NSString *)modelId;
+ (NSString *)getShopListCoordinate2DString:(CLLocationCoordinate2D)currentCoo;

@end
