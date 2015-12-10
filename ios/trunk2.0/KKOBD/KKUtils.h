//
//  BTUtils.h
//  Better
//
//  Created by apple on 10-3-9.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CLLocation.h>
#import <MapKit/MapKit.h>

@interface KKUtils : NSObject {

}

// base64
+ (NSString*) encodeBase64:(NSString*)input; 
+ (NSData *) decodeString:(NSString *)string;
+ (NSInteger) random;
+ (double) distanceFrom:(CLLocationCoordinate2D)cordFrom to:(CLLocationCoordinate2D)cordTo;
+ (NSString*) GMT2LocaleString:(NSDate*)date;
+ (NSString*) GMT2LocaleString_2:(NSDate*)date;
+ (NSDate*) LocaleDT2GMT:(NSDate*)aLocaleDt;
+ (NSString*) CGM2LocalString2:(NSDate *)date;
+ (NSString*)ConvertDataToString:(NSDate *)timeData;
+ (NSString*)convertDateTOString2:(NSDate *)date;
+ (NSDate*)convertStringToDate:(NSString *)string;
+ (NSString*)getLocalizeDateTimeFormat:(NSDate*)timeDate;
+ (NSString*) platform2String:(/*KKPlatform*/NSInteger)aPlatform;
+ (/*KKPlatform*/NSInteger) string2Platform:(NSString*)aString;
+ (void)drawRoundRect:(CGRect)rrect radius:(CGFloat)radius context:(CGContextRef)context drawingMode:(CGPathDrawingMode)drawMode;
+ (double)availableMemory;

//打电话
+ (BOOL)makePhone:(NSString*)phoneNumber;
//发短信
+ (BOOL)makeSMS:(NSString*)phoneNumber;

+ (NSString *) macaddress;

//生成默认封面
+ (UIImage *)coverGenerateWithBackground:(UIImage *)aBackgroundImg title:(NSString *)aTitle author:(NSString *)aAuthor;

@end
