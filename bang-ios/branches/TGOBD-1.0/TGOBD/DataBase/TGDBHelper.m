//
//  TGDBHelper.m
//  TGOBD
//
//  Created by James Yu on 14-3-5.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGDBHelper.h"
#import "TGHelper.h"

static TGDBHelper *_instace = nil;

@implementation TGDBHelper

+ (TGDBHelper *)sharedDBHelper
{
    if (_instace == nil) {
        _instace = [[TGDBHelper alloc] init];
    }
    return _instace;
}

- (id)init
{
    self = [super init];
    if (nil == self) {
        return self;
    }
    
    NSString *dbPath = [TGHelper getPathWithinDocumentDir:TGDBName];
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    
    if (![fileManager fileExistsAtPath:dbPath])
    {
        NSString *dbPathFrom = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:TGDBName];
        NSError *err = nil;
        
        if (![fileManager copyItemAtPath:dbPathFrom toPath:dbPath error:&err]) {
            NSLog(@"failed to copy dbfile to document:%@", err.description);
        }
        else
        {
            [userDefault setObject:TGDBVersion forKey:@"currentDBVersion"];
        }
    }
    
    _db = [[FMDatabase alloc] initWithPath:dbPath];
    if (![_db open]) {
        NSLog(@"failed to open db!");
        _db = nil;
    }
    
    NSString *currentDBVersion = [userDefault objectForKey:@"currentDBVersion"];
    
    if (![currentDBVersion isEqualToString:TGDBVersion]) {
        //进行数据库升级处理
        if ([self updateDBWithCurrentVersion:currentDBVersion]) {
            [userDefault setObject:TGDBVersion forKey:@"currentDBVersion"];
        }
        else
        {
            //待定，
        }
        
    }
    
    return self;
}

- (BOOL)updateDBWithCurrentVersion:(NSString *)currentVersion
{
    //
    return true;
}

@end
