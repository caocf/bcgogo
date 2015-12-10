//
//  KKAuthorization.m
//  KKOBD
//
//  Created by Jiahai on 13-12-27.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKAuthorization.h"

@implementation KKAuthorization

static KKAuthorization *_authorization = nil;

+(KKAuthorization *)sharedInstance
{
    @synchronized(self)
    {
        if (_authorization == nil)
            _authorization = [[[KKAuthorization alloc] init] autorelease];
    }
    return _authorization;

}

-(id) init
{
    if(self = [super init])
    {
        
    }
    return self;
}

@end
