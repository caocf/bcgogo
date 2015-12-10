//
//  BGTaskManager.m
//  KKOBD
//
//  Created by Jiahai on 14-1-24.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "BGTaskManager.h"
#import "BGTask_DriveRecord.h"
#import "KKGlobal.h"
#import "BGTask_DTCUpload.h"


#define SubThreadLabel_Task  [@"com.bcgogo.task" UTF8String]

@implementation BGTaskManager
static bool                 _taskRunning = NO;
static bool                 registWIFINotification = NO;
static BGTask_DriveRecord   *_driveRecordTask = nil;
static dispatch_queue_t task_DispatchQueue = nil;

+ (void)startTask
{
    if(task_DispatchQueue == nil)
    {
        task_DispatchQueue = dispatch_queue_create(SubThreadLabel_Task, DISPATCH_QUEUE_CONCURRENT);
    }
    
    if(!registWIFINotification)
    {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reachabilityChanged) name:kReachabilityChangedNotification object:nil];
        
        [[Reachability reachabilityForLocalWiFi] startNotifier];
        
        registWIFINotification = YES;
    }
    
    [self startWIFITask];
    [self startNetworkTask];
}

+ (void)stopTask
{
    [BGTaskManager stopWIFITask];
    [BGTaskManager stopNetworkTask];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    registWIFINotification = NO;
}

+(void) startWIFITask
{
    if(!_taskRunning)
    {
        _taskRunning = YES;
        
        dispatch_async(task_DispatchQueue, ^{
        
            if(_driveRecordTask == nil)
            {
                _driveRecordTask = [[BGTask_DriveRecord alloc] init];
            }
            
            if([BGTaskManager IsEnableWIFI])
            {
                [_driveRecordTask startTask];
            }
        });
    }
    
    //dispatch_resume(task_DispatchQueue);

}

+(void) stopWIFITask
{
    if(_taskRunning)
    {
        _taskRunning = NO;
        
        if(task_DispatchQueue)
        {
            dispatch_async(task_DispatchQueue, ^{
                
                [_driveRecordTask stopTask];
            });
        }
    }
}

+ (void)startNetworkTask
{
    if([BGTaskManager IsNetworkReachable])
    {
        dispatch_async(task_DispatchQueue, ^{
            BGTask_DTCUpload *dtcUpload = [[BGTask_DTCUpload alloc] init];
            [dtcUpload performTask];
            [dtcUpload release];
        });
    }
}

+ (void)stopNetworkTask
{
    
}

+ (BOOL) IsEnableWIFI
{
    return ([[Reachability reachabilityForLocalWiFi] currentReachabilityStatus] == ReachableViaWiFi);
}

+ (BOOL) IsNetworkReachable
{
    return ([[Reachability reachabilityForLocalWiFi] currentReachabilityStatus] != NotReachable);
}

+ (NetworkStatus)currentNetworkStatus
{
    return [[Reachability reachabilityForLocalWiFi] currentReachabilityStatus];
}

+(void)reachabilityChanged
{
    NetworkStatus status = [BGTaskManager currentNetworkStatus];
    if(status == NotReachable)
    {
        [BGTaskManager stopNetworkTask];
        [BGTaskManager stopWIFITask];
    }
    else
    {
        //有网络执行
        [BGTaskManager startNetworkTask];
        
        //WIFI执行
        if(status == ReachableViaWiFi)
        {
            [BGTaskManager startWIFITask];
        }
        else
        {
            [BGTaskManager stopWIFITask];
        }
    }
}

@end
