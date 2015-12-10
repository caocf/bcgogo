//
//  KKCacheDBManager.m
//  KKOBD
//
//  Created by Jiahai on 14-2-19.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "KKCacheDBManager.h"
#import "KKHelper.h"

#define CacheDataTableName          @"cache_data"

#define sqlField_userNo(text) \
(text) ? ([NSString stringWithFormat:@" userNo = '%@' ",text]) : @" userNo is NULL "

static KKCacheDBManager *_cacheDBManager = nil;

@implementation KKCacheDBManager

+(KKCacheDBManager *) sharedInstance
{
    @synchronized(self)
    {
        if(_cacheDBManager == nil)
        {
            _cacheDBManager = [[KKCacheDBManager alloc] init];
        }
    }
    return _cacheDBManager;
}

-(id) init
{
    if(self = [super init])
    {
        NSString *dbName = KKCacheDBName;
        BOOL success;
        NSString *dbPath = [KKHelper getPathWithinDocumentDir:dbName];
        NSFileManager *fileManager = [NSFileManager defaultManager];
        success = [fileManager fileExistsAtPath:dbPath];
        
        NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
        NSString *dbVersion = [userDefault objectForKey:@"cacheDBVersion"];
        if(!(dbVersion && [dbVersion isEqualToString:KKCacheDBVersion]) && success)
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
                [userDefault removeObjectForKey:@"cacheDBVersion"];
                NSLog(@"db upgrade failed:%@", err.description);
            }
            else
            {
                [userDefault setObject:KKCacheDBVersion forKey:@"cacheDBVersion"];
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
    }
    return self;
}

-(NSString *) getCacheJSONDataWithUserNo:(NSString *)aUserNo URL:(NSString *)aUrl
{
    NSString *jsonStr = nil;
    NSString *aSQL = [NSString stringWithFormat:@"select * from %@ where %@ and URL = '%@'",CacheDataTableName,sqlField_userNo(aUserNo),aUrl];
    
    @try {
		FMResultSet *rs = [_db executeQuery:aSQL];
		while ([rs next])
		{
            jsonStr = [[rs stringForColumn:@"jsonData"] copy];
        }
        [rs close];
    }
    @catch (NSException *exception) {
        NSLog(@"SQL getCacheJSONDataWithUserNo exception:%@",exception.description);
    }
    return jsonStr;
}

-(void) insertJSONDataWithUserNo:(NSString *)aUserNo URL:(NSString *)aUrl jsonData:(NSString *)aJsonData
{
    NSString *aSQL = nil;
    if([self getCacheJSONDataWithUserNo:aUserNo URL:aUrl])
    {
        aSQL = [NSString stringWithFormat:@"update %@ set jsonData = '%@' where %@ and URL = '%@'",CacheDataTableName,aJsonData,sqlField_userNo(aUserNo),aUrl];
    }
    else
    {
        aSQL = [NSString stringWithFormat:@"insert into %@ values (%@,'GET','%@',NULL,'%@')",CacheDataTableName,SQL_Field_nilOrString(aUserNo),aUrl,aJsonData];
    }
    @try {
        [_db executeUpdate:aSQL];
    }
    @catch (NSException *exception) {
        NSLog(@"insert cache_data (%@) to VehicleList error %@", aSQL, [exception description]);
    }
}

@end
