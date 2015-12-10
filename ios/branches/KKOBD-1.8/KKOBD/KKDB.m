//
//  KKDB.m
//  KKShowBooks
//
//  Created by  on 12-10-19.
//  Copyright (c) 2012年 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "KKDB.h"
#import "KKHelper.h"
#import "KKPreference.h"

static KKDB *g_ssfdb = nil;

@implementation KKDB
@synthesize db = _db;

- (id)init
{
	self = [super init];
	if (nil == self)
		return self;
	
	NSString *dbName = KKDBName;
	BOOL success;
	NSString *dbPath = [KKHelper getPathWithinDocumentDir:dbName];
	NSFileManager *fileManager = [NSFileManager defaultManager];
	success = [fileManager fileExistsAtPath:dbPath];
    
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    NSString *dbVersion = [userDefault objectForKey:@"dbVersion"];
    if(!(dbVersion && [dbVersion isEqualToString:KKDBVersion]) && success)
    {
        if(![fileManager removeItemAtPath:dbPath error:nil])
        {
            NSLog(@"数据库更新失败！");
        }
        success = NO;
    }
    
	if (NO == success) {
		NSString *dbPathFrom = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:dbName];
		NSError *err = nil;
		BOOL suc = NO;
		suc = [fileManager copyItemAtPath:dbPathFrom toPath:dbPath error:&err];
		if (suc == NO)
        {
            [userDefault removeObjectForKey:@"dbVersion"];
			NSLog(@"db upgrade failed:%@", err.description);
        }
        else
        {
            [userDefault setObject:KKDBVersion forKey:@"dbVersion"];
        }
        [userDefault synchronize];
	}
	
	_db = [[FMDatabase alloc] init];
	[_db initWithPath:dbPath];
	if(![_db open])
	{
		NSLog(@"tonggou db open error");
		[_db release];
		_db = nil;
	}
	
	return self;
}


- (void)dealloc
{
	[_db close];
	[_db release];
	_db = nil;
	
    [super dealloc];
}

+ (KKDB *)sharedDB
{
	if (g_ssfdb == nil)
		g_ssfdb = [[KKDB alloc] init];
	return g_ssfdb;
}

@end