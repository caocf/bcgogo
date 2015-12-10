//
//  TGJsonNetworkEngine.h
//  TGYIFA
//
//  Created by James Yu on 14-5-14.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MKNetworkEngine.h"

typedef void (^success)(id rspObject);
typedef void (^error)(NSError *error);

@interface TGJsonNetworkEngine : MKNetworkEngine


@end
