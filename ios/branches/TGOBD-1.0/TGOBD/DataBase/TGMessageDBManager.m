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

- (FMDatabase *)db
{
    if (_db == nil) {
        _db = [[TGDBHelper sharedDBHelper] db];
    }
    return _db;
}

- (BOOL)insertNewMessageWithUserNo:(NSString *)userNo message:(TGModelMessage *)message
{
    BOOL ifInsert = NO;
    
    if (![self isAleradyHaveMessage:message.id userNo:userNo]) {
        NSString *sql = @"insert into tg_message values(?,?,?,?,?,?,?,0,datetime('now','localtime'))";
        
        @try {
            ifInsert = [self.db executeUpdate:sql, [NSNumber numberWithLongLong:message.id], userNo, message.type,message.title, message.content,message.actionType,message.params];
        }
        @catch (NSException *exception) {
            NSLog(@"falied to query insertNewMessageWithUserNo--%@", exception.description);
        }
        @finally {
            
        }
    }
    
    return ifInsert;
}

- (BOOL)isAleradyHaveMessage:(long long)msgId userNo:(NSString *)userNo
{
    
    NSInteger count = 0;
    
    NSString *sql = @"select count(*) from tg_message where user_no = ? and msg_id = ?";
    
    @try {
        FMResultSet *rs = [self.db executeQuery:sql, userNo, [NSNumber numberWithLongLong:msgId]];
        
        while ([rs next]) {
            count = [rs intForColumnIndex:0];
        }
        [rs close];
    }
    @catch (NSException *exception) {
        NSLog(@"falied to query isAleradyHaveMessage--%@", exception.description);
    }
    
    return count > 0 ? YES : NO;
}

- (void)deleteMessageWithId:(long long)msgId userNo:(NSString *)userNo
{
    
    NSString *sql = @"delete from tg_message where msg_id=? and user_no=?";
    
    @try {
        [self.db executeUpdate:sql, [NSNumber numberWithLongLong:msgId], userNo];
    }
    @catch (NSException *exception) {
        NSLog(@"falied to query deleteMessageWithId--%@", exception.description);
    }
    @finally {
        
    }
}

- (NSMutableArray *)getMessageWithUserNo:(NSString *)userNo start:(NSInteger)start num:(NSInteger)num
{
    
    NSMutableArray *rsArray = [[NSMutableArray alloc] init];
    
    NSString *sql = @"select * from tg_message where user_no = ? order by msg_time desc limit ?,? ";
    
    @try {
        FMResultSet *rs = [self.db executeQuery:sql, userNo, [NSNumber numberWithInteger:start], [NSNumber numberWithInteger:num]];
        
        while ([rs next]) {
            TGModelMessage *message = [[TGModelMessage alloc] init];
            message.id = [rs longLongIntForColumn:@"msg_id"];
            message.type = [rs stringForColumn:@"msg_type"];
            message.title = [rs stringForColumn:@"msg_title"];
            message.content = [rs stringForColumn:@"msg_content"];
            message.actionType = [rs stringForColumn:@"msg_action_type"];
            message.params = [rs stringForColumn:@"msg_params"];
            message.time = [rs stringForColumn:@"msg_time"];
            
            [rsArray addObject:message];
        }
        [rs close];
    }
    @catch (NSException *exception) {
        NSLog(@"falied to query deleteMessageWithId--%@", exception.description);
    }
    @finally {
        
    }
    
    return rsArray;

}

- (void)setMessageRead:(NSString *)userNo
{
    
    NSString *sql = @"update tg_message set read = 1 where user_no = ?";
    
    @try {
        [self.db executeUpdate:sql, userNo];
    }
    @catch (NSException *exception) {
        NSLog(@"falied to setMessageRead--%@", exception.description);
    }
    @finally {
        
    }
}

- (void)setMessageRead:(NSString *)userNo messageType:(NSString *)messageType
{
    NSString *sql = @"update tg_message set read = 1 where user_no = ? and msg_type = ?";
    
    @try {
        [self.db executeUpdate:sql, userNo, messageType];
    }
    @catch (NSException *exception) {
        NSLog(@"falied to setMessageRead--%@", exception.description);
    }
    @finally {
        
    }
}

- (NSInteger)getUnreadMessageNumberWithUserNo:(NSString *)userNo
{
    
    NSInteger unReadNum = 0;
    
    NSString *sql = @"select count(*) from tg_message where read=0 and user_no = ?";
    
    @try {
        FMResultSet *rs = [self.db executeQuery:sql, userNo];
        
        while ([rs next]) {
            unReadNum = [rs intForColumnIndex:0];
        }
    }
    @catch (NSException *exception) {
         NSLog(@"falied to query getUnreadMessageNumberWithUserNo--%@", exception.description);
    }
    @finally {
        
    }
    return unReadNum;
}

@end
