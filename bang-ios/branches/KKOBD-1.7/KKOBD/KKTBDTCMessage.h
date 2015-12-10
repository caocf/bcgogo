//
//  KKTBDTCMessage.h
//  KKOBD
//
//  Created by zhuyc on 13-10-9.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KKTBBase.h"
#import "KKTBDictFault.h"

@interface KKModelDTCMessage : NSObject
@property (nonatomic, assign) long long id;
@property (nonatomic, copy) NSString *status;

@property (nonatomic ,copy)NSString *faultCode;
@property (nonatomic ,copy)NSString *userNo;
@property (nonatomic ,copy)NSString *timeStamp;
@property (nonatomic ,copy)NSString *warnTimeStamp;
@property (nonatomic ,copy)NSString *vehicleModelId;
@property (nonatomic ,retain)NSArray *desArray;  //故障号描述语句
@end


@interface KKTBDTCMessage : KKTBBase

-(BOOL)creatTable;
//inside is KKModelDTCMessage
-(NSArray *)getDTCMessageByUserNo:(NSString *)userNo vehicleModelId:(NSString *)aVehicleModelId;
-(NSArray *)queryDTCMessageByUserNo:(NSString *)userNo vehicleModelId:(NSString *)aVehicleModelId faultCode:(NSString *)aFaultCode;


-(void)insertDTCMessage:(KKModelDTCMessage *)message;
-(void)deleteDTCMessagesWithUserNo:(NSString *)userNo;
-(void)deleteDTCMessage:(NSString *)msg WithUserNo:(NSString *)userNo;
-(void)limt100DTCMsgesForUserNo:(NSString *)userNo;

@end
