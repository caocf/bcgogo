//
//  TGDBHelper.h
//  TGOBD
//
//  Created by James Yu on 14-3-5.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "FMDatabase.h"

#define TGDBName        @"tonggou.sqlite"
#define TGDBVersion     @"1.0"

@interface TGDBHelper : NSObject

@property (nonatomic, readonly) FMDatabase *db;

+ (TGDBHelper *)sharedDBHelper;

@end
