
//
//  BTUtils.m
//  Better
//
//  Created by apple on 10-3-9.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <math.h>
#import <sys/sysctl.h>  
#import <mach/mach.h>
#import "KKUtils.h"
#import "GTMBase64.h" 

#include <sys/socket.h> // Per msqr
#include <sys/sysctl.h>
#include <net/if.h>
#include <net/if_dl.h>

#import "KKGlobal.h"

@implementation KKUtils

#pragma mark -
#pragma mark base64

+(NSString*) encodeBase64:(NSString*)input 
{ 
    NSData *data = [input dataUsingEncoding:NSUTF8StringEncoding allowLossyConversion:YES]; 
    //转换到base64 
    data = [GTMBase64 encodeData:data]; 
    NSString * base64String = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]; 
    return [base64String autorelease] ; 
} 

+(NSData *) decodeString:(NSString *)string
{
	NSData *data = [GTMBase64 decodeString:string];
	return data;
}

#pragma mark -
#pragma mark wrapper of c stdlib

+(NSInteger) random
{
	srandom(time(nil));
	return (NSInteger)random();
}

#pragma mark -

+ (double) distanceFrom:(CLLocationCoordinate2D)cordFrom to:(CLLocationCoordinate2D)cordTo
{
	//distance = (R=6371004) * acos( cos(lat1)*cos(lat2)*cos(lon1-lon2) + sin(lat1)*sin(lat2) )
	
	const int earthR = 6371004;
	double lat1 = (M_PI*cordFrom.latitude/180);
	double lat2 = (M_PI*cordTo.latitude/180);
	double lon1 = (M_PI*cordFrom.longitude/180);
	double lon2 = (M_PI*cordTo.longitude/180);
	
	double distance = earthR * acos( cos(lat1)*cos(lat2)*cos(lon1-lon2) + sin(lat1)*sin(lat2) );
	return distance;
}

+ (NSString*) GMT2LocaleString:(NSDate*)date
{
	NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
	[formatter setAMSymbol:@"AM"];
	[formatter setPMSymbol:@"PM"];
	[formatter setDateFormat:@"yyyy-MM-dd HH:mm"];
	NSTimeZone *zone = [NSTimeZone systemTimeZone];
	NSInteger interval = [zone secondsFromGMTForDate:date];
	NSDate *localeDate = [date dateByAddingTimeInterval:interval];
	NSString *localeString = [formatter stringFromDate:localeDate];
	[formatter release];
	return localeString;
}

+ (NSString*) GMT2LocaleString_2:(NSDate*)date
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
	[formatter setAMSymbol:@"AM"];
	[formatter setPMSymbol:@"PM"];
	[formatter setDateFormat:@"yyyy.MM.dd"];
	NSTimeZone *zone = [NSTimeZone systemTimeZone];
	NSInteger interval = [zone secondsFromGMTForDate:date];
	NSDate *localeDate = [date dateByAddingTimeInterval:interval];
	NSString *localeString = [formatter stringFromDate:localeDate];
	[formatter release];
	return localeString;
}

+ (NSDate*) LocaleDT2GMT:(NSDate*)aLocaleDt
{
	if (nil == aLocaleDt)
		return nil;
	
	NSTimeZone *zone = [NSTimeZone systemTimeZone];
	NSInteger interval = [zone secondsFromGMTForDate:aLocaleDt];
	interval = 0 - interval;
	NSDate *gmtDt = [aLocaleDt dateByAddingTimeInterval:interval];
	return gmtDt;
}

+ (NSString*)getLocalizeDateTimeFormat:(NSDate*)timeDate {
	NSTimeZone *zone = [NSTimeZone systemTimeZone];
	NSInteger interval = [zone secondsFromGMTForDate:timeDate];
	NSDate *localeDate = [timeDate dateByAddingTimeInterval:interval];
	
	double timeIntervalSince1970 = [localeDate timeIntervalSince1970];
	double curTime = [[NSDate date] timeIntervalSince1970];
	double diffSeconds = curTime - timeIntervalSince1970;
	NSString *result = @"";
	
	if(diffSeconds < 60) {
		result = [NSString stringWithFormat:@"%@秒前",[[NSNumber numberWithInt:diffSeconds] stringValue]];
	} else if(diffSeconds < 3600) {
		result = [NSString stringWithFormat:@"%@分钟前",[[NSNumber numberWithInt:(diffSeconds / 60.0)] stringValue]];
	} else if(diffSeconds < 3600 * 24) {
		result = [NSString stringWithFormat:@"%@小时前",[[NSNumber numberWithInt:(diffSeconds / 3600.0)] stringValue]];
	} else if(diffSeconds < 3600 * 24 * 30) {
		result = [NSString stringWithFormat:@"%@天前",[[NSNumber numberWithInt:(diffSeconds / (3600.0 * 24))] stringValue]];
	} else {
		NSCalendar *calendar = [NSCalendar currentCalendar];
		unsigned unitFlags = NSYearCalendarUnit | NSMonthCalendarUnit |  NSDayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit;
		NSDateComponents *dateCompontents1 = [calendar components:unitFlags fromDate:[NSDate dateWithTimeIntervalSince1970:timeIntervalSince1970]];
		NSDateComponents *dateCompontents2 = [calendar components:unitFlags fromDate:[NSDate dateWithTimeIntervalSince1970:curTime]];
		
		if([dateCompontents2 year] - [dateCompontents1 year] == 0) {
			result = [NSString stringWithFormat:@"%d-%d",
					  [dateCompontents1 month],
					  [dateCompontents1 day]];
		} else {
			result = [NSString stringWithFormat:@"%d-%d-%d %d:%d",
					  [dateCompontents1 year],
					  [dateCompontents1 month],
					  [dateCompontents1 day],
					  [dateCompontents1 hour],
					  [dateCompontents1 minute]];			
		}
	}
	
	return result;
}

+ (NSString*) CGM2LocalString2:(NSDate *)date {
	NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
	[formatter setAMSymbol:@"AM"];
	[formatter setPMSymbol:@"PM"];
	[formatter setDateFormat:@"yyyy-MM-dd HH:mm"];
	NSTimeZone *zone = [NSTimeZone systemTimeZone];
	NSInteger interval = [zone secondsFromGMTForDate:date];
	NSDate *localeDate = [date addTimeInterval:interval];
	NSString *localeString = [formatter stringFromDate:localeDate];
	[formatter release];
	return localeString;
}

+ (NSString*)ConvertDataToString:(NSDate *)timeData
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm"];
    NSString *timeString = [dateFormatter stringFromDate:timeData];
    [dateFormatter release];
    return timeString;
}

+ (NSString*)convertDateTOString2:(NSDate *)date
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy年MM月dd日"];
    NSString *timeString = [dateFormatter stringFromDate:date];
    [dateFormatter release];
    return timeString;
}

+ (NSDate*)convertStringToDate:(NSString *)string
{
    double timeStamp = [string doubleValue];
    if ([string length] == 13)
        timeStamp = timeStamp/1000;
    return [NSDate dateWithTimeIntervalSince1970:timeStamp];
}

#pragma mark -
#pragma mark PLATFORM
+ (NSString*) platform2String:(/*KKPlatform*/NSInteger)aPlatform
{
	NSString *ret = @"WIN";
	if (aPlatform >= PLATFORM_UNKNOWN)
		return ret;
	return KKPlatformName[aPlatform];
}

+ (/*KKPlatform*/NSInteger) string2Platform:(NSString*)aString
{
	if (aString == nil)
		return PLATFORM_UNKNOWN;
	
	for (NSInteger i=0; i<sizeof(KKPlatformName)/sizeof(NSString*); i++) {
		if (NSOrderedSame == [aString caseInsensitiveCompare:KKPlatformName[i]])
			return i;
	}
	return PLATFORM_UNKNOWN;
}


+ (void)drawRoundRect:(CGRect)rrect radius:(CGFloat)radius context:(CGContextRef)context drawingMode:(CGPathDrawingMode)drawMode {
	// NOTE: At this point you may want to verify that your radius is no more than half
	// the width and height of your rectangle, as this technique degenerates for those cases.
	
	// In order to draw a rounded rectangle, we will take advantage of the fact that
	// CGContextAddArcToPoint will draw straight lines past the start and end of the arc
	// in order to create the path from the current position and the destination position.
	
	// In order to create the 4 arcs correctly, we need to know the min, mid and max positions
	// on the x and y lengths of the given rectangle.
	CGFloat minx = CGRectGetMinX(rrect), midx = CGRectGetMidX(rrect), maxx = CGRectGetMaxX(rrect);
	CGFloat miny = CGRectGetMinY(rrect), midy = CGRectGetMidY(rrect), maxy = CGRectGetMaxY(rrect);
	
	// Next, we will go around the rectangle in the order given by the figure below.
	//       minx    midx    maxx
	// miny    2       3       4
	// midy   1 9              5
	// maxy    8       7       6
	// Which gives us a coincident start and end point, which is incidental to this technique, but still doesn't
	// form a closed path, so we still need to close the path to connect the ends correctly.
	// Thus we start by moving to point 1, then adding arcs through each pair of points that follows.
	// You could use a similar tecgnique to create any shape with rounded corners.
	
	// Start at 1
	CGContextMoveToPoint(context, minx, midy);
	// Add an arc through 2 to 3
	CGContextAddArcToPoint(context, minx, miny, midx, miny, radius);
	// Add an arc through 4 to 5
	CGContextAddArcToPoint(context, maxx, miny, maxx, midy, radius);
	// Add an arc through 6 to 7
	CGContextAddArcToPoint(context, maxx, maxy, midx, maxy, radius);
	// Add an arc through 8 to 9
	CGContextAddArcToPoint(context, minx, maxy, minx, midy, radius);
	// Close the path
	CGContextClosePath(context);
	// Fill & stroke the path
	CGContextDrawPath(context, drawMode);
}

+ (double)availableMemory {
	vm_statistics_data_t vmStats;
	mach_msg_type_number_t infoCount = HOST_VM_INFO_COUNT;
	kern_return_t kernReturn = host_statistics(mach_host_self(), HOST_VM_INFO, (host_info_t)&vmStats, &infoCount);
	
	if(kernReturn != KERN_SUCCESS) {
		return NSNotFound;
	}
	
	return ((vm_page_size * vmStats.free_count) / 1024.0) / 1024.0;
}

//打电话
+ (BOOL)makePhone:(NSString*)phoneNumber
{
    NSString* number = [NSString stringWithString:phoneNumber];
    NSString* numberAfterClear = [[[number stringByReplacingOccurrencesOfString:@" " withString:@""]
                          stringByReplacingOccurrencesOfString:@"(" withString:@""]
                         stringByReplacingOccurrencesOfString:@")" withString:@""];
    
    NSURL *phoneNumberURL = [NSURL URLWithString:[NSString stringWithFormat:@"tel://%@", numberAfterClear]];
    NSLog(@"make call, URL= %@", phoneNumberURL);
    
    return [[UIApplication sharedApplication] openURL:phoneNumberURL];  
}

+ (BOOL)makeSMS:(NSString*)phoneNumber
{
    NSString* number = [NSString stringWithString:phoneNumber];
    NSString* numberAfterClear = [[[number stringByReplacingOccurrencesOfString:@" " withString:@""]
                                   stringByReplacingOccurrencesOfString:@"(" withString:@""]
                                  stringByReplacingOccurrencesOfString:@")" withString:@""];
    
    NSURL *phoneNumberURL = [NSURL URLWithString:[NSString stringWithFormat:@"sms://%@", numberAfterClear]];
    NSLog(@"make sms, URL= %@", phoneNumberURL);
    
    return [[UIApplication sharedApplication] openURL:phoneNumberURL]; 
}

#pragma mark MAC addy
// Return the local MAC addy
// Courtesy of FreeBSD hackers email list
// Accidentally munged during previous update. Fixed thanks to mlamb.
+ (NSString *) macaddress
{
    int                    mib[6];
    size_t                len;
    char                *buf;
    unsigned char        *ptr;
    struct if_msghdr    *ifm;
    struct sockaddr_dl    *sdl;
    
    mib[0] = CTL_NET;
    mib[1] = AF_ROUTE;
    mib[2] = 0;
    mib[3] = AF_LINK;
    mib[4] = NET_RT_IFLIST;
    
    if ((mib[5] = if_nametoindex("en0")) == 0) {
        printf("Error: if_nametoindex error/n");
        return NULL;
    }
    
    if (sysctl(mib, 6, NULL, &len, NULL, 0) < 0) {
        printf("Error: sysctl, take 1/n");
        return NULL;
    }
    
    if ((buf = malloc(len)) == NULL) {
        printf("Could not allocate memory. error!/n");
        return NULL;
    }
    
    if (sysctl(mib, 6, buf, &len, NULL, 0) < 0) {
        printf("Error: sysctl, take 2");
        return NULL;
    }
    
    ifm = (struct if_msghdr *)buf;
    sdl = (struct sockaddr_dl *)(ifm + 1);
    ptr = (unsigned char *)LLADDR(sdl);
    // NSString *outstring = [NSString stringWithFormat:@"%02x:%02x:%02x:%02x:%02x:%02x", *ptr, *(ptr+1), *(ptr+2), *(ptr+3), *(ptr+4), *(ptr+5)];
    NSString *outstring = [NSString stringWithFormat:@"%02x%02x%02x%02x%02x%02x", *ptr, *(ptr+1), *(ptr+2), *(ptr+3), *(ptr+4), *(ptr+5)];
    free(buf);
    return [outstring uppercaseString];
}

+ (UIImage *)coverGenerateWithBackground:(UIImage *)aBackgroundImg title:(NSString *)aTitle author:(NSString *)aAuthor
{
	if (aBackgroundImg == nil)
		return nil;
	
	CGSize size = aBackgroundImg.size;
	
	//UIGraphicsBeginImageContextWithOptions(size, YES, 0.0f);
	UIGraphicsBeginImageContext(size);
	CGContextRef context = UIGraphicsGetCurrentContext();
	CGContextTranslateCTM(context, 0.0, size.height);
	CGContextScaleCTM(context, 1.0, -1.0);
	
	CGContextDrawImage(context, CGRectMake(0, 0, size.width, size.height), aBackgroundImg.CGImage);
	
	CGContextTranslateCTM(context, 0.0, size.height);
	CGContextScaleCTM(context, 1.0, -1.0);
	
	CGContextSetStrokeColorWithColor(context, [UIColor redColor].CGColor);	
	CGContextSetFillColorWithColor(context, [UIColor whiteColor].CGColor);
	CGContextSetTextDrawingMode(context, kCGTextFill);
	
	UIFont *font = [UIFont systemFontOfSize:28.f];
	if ([aTitle length] > 0) {
		CGSize titleSize = [aTitle sizeWithFont:font constrainedToSize:CGSizeMake(size.width-10, size.height*3/4) lineBreakMode:UILineBreakModeWordWrap];
        if (titleSize.height > 100)
            titleSize.height = 100;
		CGFloat y = (size.height*3/4 - titleSize.height)/2;
		if (y < 0)
			y = 5;
		[aTitle drawInRect:CGRectMake(5, y, titleSize.width, titleSize.height) withFont:font lineBreakMode:UILineBreakModeWordWrap alignment:UITextAlignmentLeft];
	}
	
	if ([aAuthor length] > 0) {
		font = [UIFont systemFontOfSize:20.f];
		CGFloat y = (size.height*5/7);
		CGSize authorSize = [aAuthor sizeWithFont:font constrainedToSize:CGSizeMake(size.width-10, size.height/4) lineBreakMode:UILineBreakModeWordWrap];
        if (authorSize.height > 60)
            authorSize.height = 60;
		[aAuthor drawInRect:CGRectMake(5, y, authorSize.width, authorSize.height) withFont:font lineBreakMode:UILineBreakModeWordWrap alignment:UITextAlignmentLeft];
	}
	
	CGImageRef coverRef = CGBitmapContextCreateImage(context);
	UIImage *cover = [UIImage imageWithCGImage:coverRef];
	CGImageRelease(coverRef);
	UIGraphicsEndImageContext();
	return cover;
}


@end



