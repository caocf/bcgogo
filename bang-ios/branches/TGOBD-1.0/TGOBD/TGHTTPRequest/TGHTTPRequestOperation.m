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
    [((NSMutableURLRequest *)self.request) setTimeoutInterval:TIME_HTTP_TIMEOUT];
    return self;
}

- (id)responseObject
{
    TGLog(@"%@",self.responseString);
    
    id responseObj = [super responseObject];
    if(responseObj)
    {
        responseObj = [TGHTTPRequestParser parse:responseObj apiID:self.apiID];
    }
    TGComplexObject *retObj = (TGComplexObject *)responseObj;
    if(retObj.header.msgCode == -202)
    {
        //登录过期
        dispatch_async(dispatch_get_main_queue(), ^{
            [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_LoginOut object:nil];
            [TGAlertView showAlertViewWithTitle:nil message:retObj.header.message];
        });
        
        return nil;
    }
    return responseObj;
}

@end
