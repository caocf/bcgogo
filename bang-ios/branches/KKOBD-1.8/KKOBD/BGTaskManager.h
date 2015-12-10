//
//  BGTaskManager.h
//  KKOBD
//
//  Created by Jiahai on 14-1-24.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Reachability.h"

@interface BGTaskManager : NSObject

//开启/关闭任务
+ (void) startTask;
+ (void) stopTask;

//开启/关闭WIFI任务
+(void) startWIFITask;
+(void) stopWIFITask;

//开启/关闭有网络时的任务
+ (void)startNetworkTask;
+ (void)stopNetworkTask;
@end
