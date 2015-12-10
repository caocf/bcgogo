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
    TGLog(@"=====%@=====",self.responseString);
    
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
    //取cookies
    
    NSDictionary *fields = [self.response allHeaderFields];
    
    NSArray *cookiesArray = [NSHTTPCookie cookiesWithResponseHeaderFields:fields forURL:[[self request] URL]];

    for (NSHTTPCookie *cookie in cookiesArray) {
        if ([cookie.name isEqualToString:@"JSESSIONID"]) {
//            NSString *cook = [NSString stringWithFormat:@"JSESSIONID=%@",cookie.value];
//            TGLog(@"---url-----%@--------请求获取的COOKIE----%@", self.request, cook);
            [[NSUserDefaults standardUserDefaults] setObject:[NSString stringWithFormat:@"JSESSIONID=%@",cookie.value] forKey:TGCOOKIE];
            [[NSUserDefaults standardUserDefaults] synchronize];
            
        }
    }
    
    
    return responseObj;
}

@end
