//
//  KKTBDriveRecord.m
//  KKOBD
//
//  Created by Jiahai on 14-1-14.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "KKTBDriveRecord.h"

@implementation KKTBDriveRecord

- (NSString *)createUUIDString
{
    // create a new UUID which you own
    CFUUIDRef uuid = CFUUIDCreate(kCFAllocatorDefault);
    
    // create a new CFStringRef (toll-free bridged to NSString)
    // that you own
    NSString *uuidString = (NSString *)CFUUIDCreateString(kCFAllocatorDefault, uuid);
    
    // transfer ownership of the string
    // to the autorelease pool
    [uuidString autorelease];
    
    // release the UUID
    CFRelease(uuid);
    
    return uuidString;
}
@end
