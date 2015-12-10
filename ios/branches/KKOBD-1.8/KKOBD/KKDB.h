//
//  KKDB.h
//  KKShowBooks
//
//  Created by  on 12-10-19.
//  Copyright (c) 2012年 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "FMDatabase.h"

#define KKDBName		@"tonggou.sqlite"
#define KKDBVersion		@"1.7"

#define SQL_Field_nilOrString(text) \
(text && [[text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]] length] > 0)? ([NSString stringWithFormat:@"'%@'",[text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]]]):@"NULL"

@interface KKDB : NSObject {
	FMDatabase		*_db;				// 核准设备Database
}
@property (nonatomic, readonly) FMDatabase	*db;
//@property (nonatomic, copy)     NSString    *dbVersion;

// singleton
+ (KKDB *)sharedDB;

@end

