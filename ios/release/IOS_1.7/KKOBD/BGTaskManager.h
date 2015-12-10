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

+(void) startTask;
+(void) stopTask;
@end
