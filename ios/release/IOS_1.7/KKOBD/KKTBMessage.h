//
//  KKTBMessage.h
//  KKOBD
//
//  Created by zhuyc on 13-9-22.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KKTBBase.h"
#import "KKModelComplex.h"
@class KKDB;

@interface KKPollMessage : KKModelMessage
@property (nonatomic ,copy)NSString *userNo;

@end

@interface KKTBMessage : KKTBBase
- (NSMutableArray *)getPollMessagesWithUserNo:(NSString *)userNo;
- (NSInteger)numOfMessagesWithUserNo:(NSString *)userNo;
- (BOOL)isAleradyHaveMessage:(NSString *)msgId andUserNo:(NSString *)userNo;
- (void)insertNewMessages:(KKModelMessage *)msg;
- (void)deleteOneMessages:(NSString *)msgId andUserNo:(NSString *)userNo;
- (void)limt100MsgesForUserNo:(NSString *)userNo;
- (void)setMessageActionTypeToNull:(NSString *)messageId;
@end
