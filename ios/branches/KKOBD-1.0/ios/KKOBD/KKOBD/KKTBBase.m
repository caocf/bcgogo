//
//  KKTBBase.m
//  KKShowBooks
//
//  Created by  on 12-10-19.
//  Copyright (c) 2012年 zhuyc. All rights reserved.
//

#import "KKTBBase.h"
#import "KKDB.h"

@implementation KKTBBase

-(id)initWithDB:(KKDB*)db
{
	self = [super init];
	if (self == nil)
		return self;
	
	if (_db)
		[_db release];
	_db = [db.db retain];
	return self;
}

+ (NSString*)dateTime2String:(NSDate*)aDateTime
{
	NSString *ret = nil;
	if (aDateTime == nil)
		return ret;
	
	NSDateFormatter *dateFormatter = [[NSDateFormatter alloc]init]; 
	[dateFormatter setTimeStyle:NSDateFormatterFullStyle]; 
	[dateFormatter setDateStyle:NSDateFormatterFullStyle]; 
	[dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"]; 
	ret = [dateFormatter stringFromDate:aDateTime]; 
	[dateFormatter release]; 
	
	return ret;
}

+ (NSDate*)string2DateTime:(NSString*)aString
{
	NSDate *ret = nil;
	if ([aString length] == 0)
		return ret;
	
	NSDateFormatter *dateFormatter = [[NSDateFormatter alloc]init]; 
	[dateFormatter setTimeStyle:NSDateFormatterFullStyle]; 
	[dateFormatter setDateStyle:NSDateFormatterFullStyle]; 
	[dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"]; 
	ret = [dateFormatter dateFromString:aString]; 
	[dateFormatter release]; 
	
	return ret;
}

- (void)dealloc
{
	[_db release];
	_db = nil;
	
	[super dealloc];
}

@end
