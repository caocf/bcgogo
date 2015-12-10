//
//  TGDataSingleton.m
//  TGOBD
//
//  Created by Jiahai on 14-3-5.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGDataSingleton.h"

@implementation TGDataSingleton

static TGDataSingleton *_dataSingleton = nil;

+ (TGDataSingleton *)sharedInstance
{
    @synchronized(self)
    {
        if(_dataSingleton == nil)
            _dataSingleton = [[TGDataSingleton alloc] init];
    }
    return _dataSingleton;
}

- (id)init
{
    if(self = [super init])
    {
        
    }
    return self;
}

@end
