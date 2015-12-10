//
//  KKTBVehicle.h
//  KKOBD
//
//  Created by Jiahai on 14-1-3.
//  Copyright (c) 2014年 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KKTBBase.h"

@class KKModelVehicleDetailInfo;

@interface KKTBVehicle : KKTBBase

-(BOOL) createTable;

-(NSArray *) getVehicleWithUserNo:(NSString *)aUserNo;
-(NSArray *) getVehicleWithUserNo:(NSString *)aUserNo vehicleNo:(NSString *)aVehicleNo;
-(BOOL) insertVehicle:(KKModelVehicleDetailInfo *)aVehicle;
-(BOOL) updateVehicle:(KKModelVehicleDetailInfo *)aVehicle;
-(BOOL) deleteVehicle:(KKModelVehicleDetailInfo *)aVehicle;
-(BOOL) existVehicle:(KKModelVehicleDetailInfo *)aVehicle;
-(BOOL) setDefaultVehicleWithUserNo:(NSString *)aUserNo vehicleNo:(NSString *)aVehicleNo;
-(BOOL) existVehicleVin:(KKModelVehicleDetailInfo *)aVehicle;
@end
