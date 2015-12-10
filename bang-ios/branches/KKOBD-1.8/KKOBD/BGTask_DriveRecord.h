//
//  BGTask_DriveRecord.h
//  KKOBD
//
//  Created by Jiahai on 14-1-24.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KKProtocolEngineDelegate.h"
#import "BMKSearch.h"

@interface BGTask_DriveRecord : NSObject<KKProtocolEngineDelegate,BMKSearchDelegate>
{
    NSTimer         *timer;
    NSInteger       currentIndex;
    NSInteger       currentEndPlaceIndex;
    
    BMKSearch       *_search;
    
    BOOL            _taskRunning;
}
-(void) startTask;
-(void) stopTask;
@end
