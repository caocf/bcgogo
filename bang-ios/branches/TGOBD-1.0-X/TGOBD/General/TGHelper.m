//
//  TGHelper.m
//  TGOBD
//
//  Created by Jiahai on 14-3-3.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGHelper.h"
#import <CoreFoundation/CoreFoundation.h>

@implementation TGHelper


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

+(NSString *) meterToKiloFromInt:(int)aMeter
{
    float kilo = (float)aMeter/1000;
    return [NSString stringWithFormat:@"%.2fkm",kilo];
}

+(NSString *) meterToKiloFromString:(NSString *)aMeter
{
    return [self meterToKiloFromInt:[aMeter integerValue]];
}

@end
