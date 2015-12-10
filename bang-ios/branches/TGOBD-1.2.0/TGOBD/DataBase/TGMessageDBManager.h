//
//  TGMessageDBManager.h
//  TGOBD
//
//  Created by James Yu on 14-3-5.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TGBasicModel.h"
#import "TGDBHelper.h"

@interface TGMessageDBManager : NSObject

@property (nonatomic, strong) FMDatabase *db;

+ (TGMessageDBManager *)sharedMessageDBManager;

- (BOOL)insertNewMessageWithUserNo:(NSString *)userNo message:(TGModelMessage *)message;

- (BOOL)isAleradyHaveMessage:(long long)msgId userNo:(NSString *)userNo;

- (void)deleteMessageWithId:(long long)msgId userNo:(NSString *)userNo;

- (NSMutableArray *)getMessageWithUserNo:(NSString *)userNo start:(NSInteger)start num:(NSInteger)num;

- (void)setMessageRead:(NSString *)userNo;

- (void)setMessageRead:(NSString *)userNo messageType:(NSString *)messageType;

- (NSInteger)getUnreadMessageNumberWithUserNo:(NSString *)userNo;

@end
