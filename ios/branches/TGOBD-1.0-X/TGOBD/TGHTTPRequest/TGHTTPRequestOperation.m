//
//  TGHTTPRequestOperation.m
//  TGOBD
//
//  Created by Jiahai on 14-2-28.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGHTTPRequestOperation.h"
#import "TGHTTPRequestParser.h"
#import "TGHTTPRequestEngine.h"

@interface TGHTTPRequestOperation ()
@end

@implementation TGHTTPRequestOperation

- (instancetype)initWithRequest:(NSURLRequest *)urlRequest {
    self = [super initWithRequest:urlRequest];
    if (!self) {
        return nil;
    }
    
    self.userInfo = [[NSMutableDictionary alloc] init];
    return self;
}

- (id)responseObject
{
    id responseObj = [super responseObject];
    if(responseObj)
    {
        TGHTTPRequestAbstractParser *parser = [TGHTTPRequestParserFactory createParser:self.apiID];
        if(parser)
        {
            responseObj = [parser parse:responseObj];
        }
    }
    return responseObj;
}

@end
