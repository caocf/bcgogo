//
//  KKTBBase.h
//  KKShowBooks
//
//  Created by  on 12-10-19.
//  Copyright (c) 2012年 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KKDB.h"

@interface KKTBBase : NSObject
{
	FMDatabase			*_db;
	
}

- (id)initWithDB:(KKDB*)db;
+ (NSString*)dateTime2String:(NSDate*)aDateTime;
+ (NSDate*)string2DateTime:(NSString*)aString;

@end
