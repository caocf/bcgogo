//
//  BGTask_DriveRecord.m
//  KKOBD
//
//  Created by Jiahai on 14-1-24.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "BGTask_DriveRecord.h"
#import "KKDriveRecordEngine.h"
#import "KKProtocolEngine.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKModelComplex.h"

@interface BGTask_DriveRecord()
@property (nonatomic, retain) NSArray *driveRecordArray;
@property (nonatomic, retain) NSArray *incompleteArray;
@end

@implementation BGTask_DriveRecord
{
    
}

-(id) init
{
    if(self = [super init])
    {
        _search = [[BMKSearch alloc] init];
        _search.delegate = self;
    }
    return self;
}

-(void) startTask
{
    [self stopTask];
    
    _taskRunning = YES;
    
    timer = [NSTimer scheduledTimerWithTimeInterval:TASKTIMEINTERVAL_DRIVERECORD target:self selector:@selector(checkDriveRecordAndUpload) userInfo:nil repeats:YES];
    [timer fire];
    [[NSRunLoop currentRunLoop] addTimer:timer forMode:NSDefaultRunLoopMode];
    [[NSRunLoop currentRunLoop] run];
}

-(void) stopTask
{
    _taskRunning = NO;
    currentIndex = 0;
    currentEndPlaceIndex = 0;
    
    self.driveRecordArray = nil;
    self.incompleteArray = nil;
    
    if(timer)
    {
        [timer invalidate],timer = nil;
    }
}

//补全行车日志的终点地址
-(void) completeDriveRecordEndPlace
{
    if(currentEndPlaceIndex < [self.incompleteArray count])
    {
        BGDriveRecordDetail *detail = [self.incompleteArray objectAtIndex:currentEndPlaceIndex];
        [_search reverseGeocode:CLLocationCoordinate2DMake([detail.endLat doubleValue], [detail.endLon doubleValue])];
    }
    else
    {
        currentEndPlaceIndex = 0;
        self.incompleteArray = nil;
    }
}

-(void) checkDriveRecordAndUpload
{
    currentIndex = 0;
    currentEndPlaceIndex = 0;
    
    self.driveRecordArray = [[KKDriveRecordEngine sharedInstance] queryDriveRecordWithState:DriveRecordState_UnUploaded];
    
    self.incompleteArray = [[KKDriveRecordEngine sharedInstance] queryDriveRecordWithState:DriveRecordState_Incomplete];
    
    [self uploadDriveRecord];
    
    [self completeDriveRecordEndPlace];
}

-(void) uploadDriveRecord
{
    if(currentIndex < [self.driveRecordArray count])
    {
        [[KKProtocolEngine sharedPtlEngine] driveRecord_Upload:[self.driveRecordArray objectAtIndex:currentIndex] delegate:self];
    }
    else
    {
        currentIndex = 0;
        self.driveRecordArray = nil;
    }
}

-(NSNumber *) driveRecord_Upload:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        //[self uploadDriveRecord];
		return KKNumberResultEnd;
	}
    BGDriveRecordUploadRsp *uploadRsp = (BGDriveRecordUploadRsp *)rsp;
    
    if(uploadRsp.header.code == eRsp_succeed)
    {
        uploadRsp.result.state = DriveRecordState_Uploaded;
        [[KKDriveRecordEngine sharedInstance] updateLocalDriveRecord:uploadRsp.result];
    }
    
    if(_taskRunning)
    {
        currentIndex++;
        [self uploadDriveRecord];
    }
    return KKNumberResultEnd;
}

#pragma mark -
#pragma mark BMKSearchDelegate
- (void)onGetAddrResult:(BMKAddrInfo*)result errorCode:(int)error
{
    if(_taskRunning && currentEndPlaceIndex < [self.incompleteArray count])
    {
        [[KKDriveRecordEngine sharedInstance] updateEndPlace:result.strAddr appDriveLogId:((BGDriveRecordDetail *)[self.incompleteArray objectAtIndex:currentEndPlaceIndex]).appDriveLogId];
        
        currentEndPlaceIndex++;
        [self completeDriveRecordEndPlace];
    }
}

-(void) dealloc
{
    self.driveRecordArray = nil;
    self.incompleteArray = nil;
    _search = nil;
    [super dealloc];
}
@end
