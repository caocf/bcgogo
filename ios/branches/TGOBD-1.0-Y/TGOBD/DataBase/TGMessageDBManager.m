//
//  TGMessageDBManager.m
//  TGOBD
//
//  Created by James Yu on 14-3-5.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGMessageDBManager.h"
#import "TGDBHelper.h"

static TGMessageDBManager *_instance = nil;

@implementation TGMessageDBManager

+ (TGMessageDBManager *)sharedMessageDBManager
{
    @synchronized (self)
    {
        if (_instance == nil) {
            _instance = [[TGMessageDBManager alloc] init];
        }
        return _instance;
    }
}

- (NSMutableArray *)test
{
    TGDBHelper *helper = [TGDBHelper sharedDBHelper];
    
    NSMutableArray *rsArray = [[NSMutableArray alloc] init];
    
    NSString *sql = @"select * from test";
    
    @try {
        FMResultSet *rs = [[helper db] executeQuery:sql];
        
        while ([rs next])
        {
            NSString *tmp = [rs stringForColumn:@"test"];
            
            [rsArray addObject:tmp];
        }
    }
    @catch (NSException *exception) {
        NSLog(@"failed to test----%@", exception.description);
    }
    @finally {
        
    }
    NSLog(@"result----%@", rsArray);
    return rsArray;
}

@end
