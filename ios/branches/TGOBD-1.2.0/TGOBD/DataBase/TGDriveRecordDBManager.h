//
//  TGDriveRecordDBManager.h
//  TGOBD
//
//  Created by Jiahai on 14-3-17.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "FMDatabase.h"
@class TGModelDriveRecordDetail;

@interface TGDriveRecordDBManager : NSObject

@property (nonatomic, strong) FMDatabase *db;

+ (TGDriveRecordDBManager *)sharedInstance;

- (NSArray *)getDriveRecordList:(NSString *)userNo startTime:(long long)startTime endTime:(long long)endTime;

- (NSArray *)getPlaceNotesArray:(long long)aID;

- (BOOL)updateDriveRecord:(TGModelDriveRecordDetail *)detail;
- (BOOL)updateDriveRecordWithArray:(NSMutableArray *)detailArray;
- (BOOL)updateDriveRecordPlaceNotes:(NSString *)placeNotes id:(long long)aID;
@end
