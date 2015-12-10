//
//  KKTBMessage.m
//  KKOBD
//
//  Created by zhuyc on 13-9-22.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKTBMessage.h"
#import "KKDB.h"
#import "KKProtocolEngine.h"

@implementation KKPollMessage

- (void)dealloc
{
    self.userNo = nil;
    [super dealloc];
}
@end

@interface KKTBMessage(_private)
- (NSMutableArray *)getMessagesWithSQL:(NSString *)aSQL;
@end

@implementation KKTBMessage

- (NSMutableArray *)getMessagesWithSQL:(NSString *)aSQL
{
	if ([aSQL length] == 0)
		return nil;
	
	NSMutableArray *result = [[NSMutableArray alloc] initWithCapacity:100];
	
	@try {
		FMResultSet *rs = [_db executeQuery:aSQL];
		while ([rs next])
		{
			KKPollMessage *message = [[KKPollMessage alloc] init];
            message.userNo = [rs stringForColumn:@"userNo"];
            message.id = [rs stringForColumn:@"messageId"];
            message.type = [rs stringForColumn:@"type"];
            message.content = [rs stringForColumn:@"content"];
            message.actionType = [rs stringForColumn:@"actionType"];
            message.params = [rs stringForColumn:@"params"];
            message.title = [rs stringForColumn:@"title"];
			[result addObject:message];
			[message release];
		}
		[rs close];
	}
	@catch(NSException *e) {
		NSLog(@"get message(%@) from messageList error %@", aSQL, [e description]);
	}
	@finally {
		
	}
	return [result autorelease];
}

- (NSMutableArray *)getPollMessagesWithUserNo:(NSString *)userNo
{
    NSString *sql = [NSString stringWithFormat:@"SELECT * FROM messageList WHERE userNo ='%@' order by messageId desc LIMIT 100",userNo];
    return [self getMessagesWithSQL:sql];
}

- (NSInteger)numOfMessagesWithUserNo:(NSString *)userNo
{
    NSInteger count = 0;
    NSString *sql = [NSString stringWithFormat:@"SELECT count(*) FROM messageList WHERE userNo ='%@'",userNo];
    @try {
        FMResultSet *rs = [_db executeQuery:sql];
        while ([rs next])
        {
            count = [rs intForColumnIndex:0];
        }
        [rs close];
    }
    @catch (NSException *exception) {
        NSLog(@"get message(%@) total num from messageList error %@", sql, [exception description]);
    }
    @finally {
        
    }
    return count;
}

- (BOOL)isAleradyHaveMessage:(NSString *)msgId andUserNo:(NSString *)userNo
{
    NSInteger count = 0;
    NSString *sql = [NSString stringWithFormat:@"SELECT count(*) FROM messageList WHERE userNo ='%@' and messageId = '%@'",userNo,msgId];
    @try {
        FMResultSet *rs = [_db executeQuery:sql];
        while ([rs next])
        {
            count = [rs intForColumnIndex:0];
        }
        [rs close];
    }
    @catch (NSException *exception) {
        NSLog(@"get message(%@) total num from messageList error %@", sql, [exception description]);
    }
    @finally {
        
    }
    return (count > 0) ? YES : NO;
}

- (void)insertNewMessages:(KKModelMessage *)message
{
    NSString *aSQL = [NSString stringWithFormat:@"insert into messageList values('%@','%@','%@','%@','%@','%@','%@','%@')",[KKProtocolEngine sharedPtlEngine].userName,message.id,message.type,message.content,message.actionType,message.params,message.title,[NSString stringWithFormat:@"%.f",(NSTimeInterval)[[NSDate date] timeIntervalSince1970]]];
    
    @try {
        [_db executeUpdate:aSQL];
    }
    @catch (NSException *exception) {
        NSLog(@"insert message(%@) to messageList error %@", aSQL, [exception description]);
    }
    @finally {
        
    };
    
}

- (void)deleteOneMessages:(NSString *)msgId andUserNo:(NSString *)userNo;
{
    NSString *aSQL = [NSString stringWithFormat:@"DELETE FROM messageList Where messageId = '%@' and userNo ='%@' ",msgId,userNo];
    @try {
        [_db executeUpdate:aSQL];
    }
    @catch (NSException *exception) {
        NSLog(@"delete  one message(%@) from messageList error %@", aSQL, [exception description]);
    }
    @finally {
        
    };
}

- (void)limt100MsgesForUserNo:(NSString *)userNo
{
    NSInteger count = [self numOfMessagesWithUserNo:userNo];
    if (count > 100)
    {
        NSString *sql = [NSString stringWithFormat:@"delete from messageList where cast(messageId as int)< (select min(iid) from (select cast(messageId as int) iid from messageList where userNo = '%@'order by iid desc limit 100)) and userNo = '%@'",userNo,userNo];
        [_db executeQuery:sql];
    }
}

- (void)setMessageActionTypeToNull:(NSString *)messageId
{
    if ([messageId length] > 0)
    {
        NSString *sql = [NSString stringWithFormat:@"update messageList set actionType = '' where messageId = '%@'",messageId];
        [_db executeUpdate:sql];
    }
}

- (void)dealloc
{
    
    [super dealloc];
}
@end
