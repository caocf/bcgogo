//
//  KKAuthorization.m
//  KKOBD
//
//  Created by Jiahai on 13-12-27.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKAuthorization.h"

@implementation KKAccessAuthorization

+(KKAccessAuthorization *) createWithAuthorizationType:(AuthorizationType)aType
{
    KKAccessAuthorization *accessAuthor = [[KKAccessAuthorization alloc] init];
    switch (aType) {
        case Authorization_Visitor:
        {
            accessAuthor.localCarManager = YES;
            accessAuthor.shopQuery = YES;
            accessAuthor.shopQuery_MoreBtn = NO;
            accessAuthor.vehicleCondition = NO;
            accessAuthor.orderOnline = NO;
            accessAuthor.serviceSeeking = NO;
            accessAuthor.personalInfo = NO;
            accessAuthor.searchCar = NO;
            accessAuthor.scanShop = NO;
        }
            break;
        case Authorization_Register:
        {
            accessAuthor.localCarManager = NO;
            accessAuthor.shopQuery = YES;
            accessAuthor.shopQuery_MoreBtn = YES;
            accessAuthor.vehicleCondition = YES;
            accessAuthor.orderOnline = YES;
            accessAuthor.serviceSeeking = YES;
            accessAuthor.personalInfo = YES;
            accessAuthor.searchCar = YES;
            accessAuthor.scanShop = YES;
        }
            break;
    }
    return [accessAuthor autorelease];
}

-(void) dealloc
{
    [super dealloc];
}

@end

@implementation KKAuthorization

static KKAuthorization *_authorization = nil;

+(KKAuthorization *)sharedInstance
{
    @synchronized(self)
    {
        if (_authorization == nil)
            _authorization = [[KKAuthorization alloc] init];
    }
    return _authorization;

}

-(id) init
{
    if(self = [super init])
    {
        [self setAuthorizationType:Authorization_Visitor];
    }
    return self;
}

-(void) setAuthorizationType:(AuthorizationType)authorizationType
{
    _authorizationType = authorizationType;
    self.accessAuthorization = [KKAccessAuthorization createWithAuthorizationType:authorizationType];
}


-(void) dealloc
{
    self.accessAuthorization = nil;
    _authorization = nil;
    [super dealloc];
}
@end
