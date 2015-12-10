//
//  KKCacheDBManager.h
//  KKOBD
//
//  Created by Jiahai on 14-2-19.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "FMDatabase.h"

#define KKCacheDBName		@"tonggou_cache.sqlite"
#define KKCacheDBVersion    @"1.0"

#define SQL_Field_nilOrString(text) \
(text && [[text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]] length] > 0)? ([NSString stringWithFormat:@"'%@'",[text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]]]):@"NULL"

@interface KKCacheDBManager : NSObject
{
	FMDatabase		*_db;				// 核准设备Database
}

+(KKCacheDBManager *)sharedInstance;

-(NSString *) getCacheJSONDataWithUserNo:(NSString *)aUserNo URL:(NSString *)aUrl;
-(void) insertJSONDataWithUserNo:(NSString *)aUserNo URL:(NSString *)aUrl jsonData:(NSString *)aJsonData;

@end
