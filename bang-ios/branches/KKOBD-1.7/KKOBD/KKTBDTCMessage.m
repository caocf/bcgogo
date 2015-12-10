//
//  KKTBDTCMessage.m
//  KKOBD
//
//  Created by zhuyc on 13-10-9.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKTBDTCMessage.h"
#import "KKDB.h"

#define KKDefaultDTCMessageTbName       @"tb_dtcMessage"

@implementation KKModelDTCMessage

- (void)dealloc
{
    self.status = nil;
    self.faultCode = nil;
    self.userNo = nil;
    self.timeStamp = nil;
    self.vehicleModelId = nil;
    self.desArray = nil;
    [super dealloc];
}

@end

@implementation KKTBDTCMessage

-(BOOL)creatTable
{
    BOOL ret = NO;
    
    if (_db == nil)
        return ret;
    
    NSString *sqlFmt =  @"CREATE TABLE IF NOT EXISTS %@("
    @"[faultCode] CHAR(20),"
    @"[userNo] VARCHAR(20),"
    @"[timestamp] VARCHAR(20),"
    @"[vehicleModelId] VARCHAR(20),"
    @"[warnTimeStamp] VARCHAR(20)"
    @")";
    NSString *sql = [NSString stringWithFormat:sqlFmt, KKDefaultDTCMessageTbName];
    
    @try {
        ret = [_db executeUpdate:sql];
    }
    @catch (NSException *exception) {
        NSLog(@"create table tb_dtcMessage exception:%@", exception.description);
        ret = NO;
    }
    
    return ret;
}

- (NSMutableArray *)getMessagesWithSQL:(NSString *)aSQL
{
    NSMutableArray *resultArr = [[NSMutableArray alloc] init];
    if ([aSQL length] == 0)
        return [resultArr autorelease];
    
    @try {
		FMResultSet *rs = [_db executeQuery:aSQL];
		while ([rs next])
		{
			KKModelDTCMessage *message = [[KKModelDTCMessage alloc] init];
            message.faultCode = [rs stringForColumn:@"faultCode"];
            message.userNo = [rs stringForColumn:@"userNo"];
            message.timeStamp = [rs stringForColumn:@"timeStamp"];
            message.vehicleModelId = [rs stringForColumn:@"vehicleModelId"];
            message.warnTimeStamp = [rs stringForColumn:@"warnTimeStamp"];            
            [resultArr addObject:message];
            [message release];
        }
    }
    @catch (NSException *exception) {
        NSLog(@"get table tb_dtcMessage messages exception:%@", exception.description);
    }
    
    return [resultArr autorelease];
}

-(NSArray *)getDTCMessageByUserNo:(NSString *)userNo vehicleModelId:(NSString *)aVehicleModelId
{
    NSString *sql = [NSString stringWithFormat:@"SELECT * FROM tb_dtcMessage WHERE userNo ='%@' and vehicleModelId = '%@' order by timestamp desc LIMIT 100",userNo,aVehicleModelId];
    if ([aVehicleModelId length] == 0)
        sql = [NSString stringWithFormat:@"SELECT * FROM tb_dtcMessage WHERE userNo ='%@' order by timestamp desc LIMIT 100",userNo];
    
    return [self getMessagesWithSQL:sql];
}


-(NSArray *)queryDTCMessageByUserNo:(NSString *)userNo vehicleModelId:(NSString *)aVehicleModelId faultCode:(NSString *)aFaultCode
{
    NSString *sql = [NSString stringWithFormat:@"select * from tb_dtcMessage where userNo = '%@' and vehicleModelId = '%@' and faultCode = '%@' order by timestamp",userNo,aVehicleModelId,aFaultCode];
    return [self getMessagesWithSQL:sql];
}

-(void)insertDTCMessage:(KKModelDTCMessage *)message
{
    [self creatTable];
    
    NSArray *resultArr = [self queryDTCMessageByUserNo:message.userNo vehicleModelId:message.vehicleModelId faultCode:message.faultCode];
    
    NSString *sql = [NSString stringWithFormat:@"update tb_dtcMessage set warnTimeStamp = '%@' where userNo = '%@' and vehicleModelId = '%@' and faultCode = '%@'",message.warnTimeStamp,message.userNo,message.vehicleModelId,message.faultCode];
    
    if ([resultArr count] == 0)
        sql = [NSString stringWithFormat:@"insert into tb_dtcMessage values('%@','%@','%@','%@','%@')",message.faultCode,message.userNo,message.timeStamp,message.vehicleModelId,message.warnTimeStamp];
        
    @try {
       [_db executeUpdate:sql];
    }
    @catch (NSException *exception) {
        NSLog(@"insert or update DTC (%@) to messageList error %@", sql, [exception description]);
    }
    
}

-(void)deleteDTCMessagesWithUserNo:(NSString *)userNo 
{
    NSString *sql = [NSString stringWithFormat:@"DELETE FROM tb_dtcMessage Where userNo ='%@' ",userNo];
    @try {
        [_db executeUpdate:sql];
    }
    @catch (NSException *exception) {
        NSLog(@"delete DTC (%@) to messageList error %@", sql, [exception description]);
    }
}

-(void)deleteDTCMessage:(NSString *)msg WithUserNo:(NSString *)userNo
{
    NSString *sql = [NSString stringWithFormat:@"DELETE FROM tb_dtcMessage Where faultCode = '%@' and userNo ='%@' ",msg,userNo];
    @try {
        [_db executeUpdate:sql];
    }
    @catch (NSException *exception) {
        NSLog(@"delete DTC (%@) to messageList error %@", sql, [exception description]);
    }
}

- (void)limt100DTCMsgesForUserNo:(NSString *)userNo
{
    NSString *sql = [NSString stringWithFormat:@"delete from tb_dtcMessage where cast(timestamp as int)< (select min(iid) from (select cast(timestamp as int) iid from tb_dtcMessage where userNo = '%@'order by iid desc limit 100)) and userNo = '%@'",userNo,userNo];
    @try {
        [_db executeQuery:sql];
    }
    @catch (NSException *exception) {
        NSLog(@"limt 100 DTC (%@) to messageList error %@", sql, [exception description]);
    }
    
}

@end
