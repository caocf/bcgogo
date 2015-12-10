//
//  TGHTTPRequestSerializer.m
//  TGOBD
//
//  Created by Jiahai on 14-3-19.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGHTTPRequestSerializer.h"

@implementation TGHTTPRequestSerializer

+ (instancetype)serializer {
    return [self serializerWithWritingOptions:0];
}

+ (instancetype)serializerWithWritingOptions:(NSJSONWritingOptions)writingOptions
{
    TGHTTPRequestSerializer *serializer = [[self alloc] init];
    serializer.writingOptions = writingOptions;
    
    return serializer;
}


#pragma mark - AFURLRequestSerialization

- (NSURLRequest *)requestBySerializingRequest:(NSURLRequest *)request
                               withParameters:(id)parameters
                                        error:(NSError *__autoreleasing *)error
{
    NSParameterAssert(request);
    
    NSMutableURLRequest *mutableRequest = [request mutableCopy];
    
    NSArray *cookies = [[NSHTTPCookieStorage sharedHTTPCookieStorage] cookies];
    NSEnumerator *enumerator = [cookies objectEnumerator];
    NSHTTPCookie *cookie;
    while (cookie = [enumerator nextObject])
    {
        if ([[cookie name] isEqualToString:@"JSESSIONID"])
        {
            NSMutableDictionary *propscook = [[NSMutableDictionary alloc] initWithDictionary: [cookie properties]];
            
            propscook[@"Path"] = @"/";
            
            [[NSHTTPCookieStorage sharedHTTPCookieStorage] deleteCookie:cookie];
            
            NSHTTPCookie *newcookie = [NSHTTPCookie cookieWithProperties:propscook];
            [[NSHTTPCookieStorage sharedHTTPCookieStorage] setCookie:newcookie];
            
        }
    }
    
    cookies = [[NSHTTPCookieStorage sharedHTTPCookieStorage] cookies];
    for(NSHTTPCookie *cookie in cookies)
    {
        if([cookie.name isEqualToString:@"JSESSIONID"])
        {
            NSDictionary *dict = [NSHTTPCookie requestHeaderFieldsWithCookies:@[cookie]];
            [mutableRequest setValue:[dict objectForKey:@"Cookie"] forHTTPHeaderField:@"Cookie"];
        }
    }
    
    if ([self.HTTPMethodsEncodingParametersInURI containsObject:[[request HTTPMethod] uppercaseString]] || [[request HTTPMethod] isEqualToString:@"POST"]) {
        return [super requestBySerializingRequest:mutableRequest withParameters:parameters error:error];
    }
    
    [self.HTTPRequestHeaders enumerateKeysAndObjectsUsingBlock:^(id field, id value, BOOL * __unused stop) {
        if (![request valueForHTTPHeaderField:field]) {
            [mutableRequest setValue:value forHTTPHeaderField:field];
        }
    }];
    
    if (!parameters) {
        return mutableRequest;
    }
    
    NSString *charset = (__bridge NSString *)CFStringConvertEncodingToIANACharSetName(CFStringConvertNSStringEncodingToEncoding(NSUTF8StringEncoding));
    
    [mutableRequest setValue:[NSString stringWithFormat:@"application/json; charset=%@", charset] forHTTPHeaderField:@"Content-Type"];
    [mutableRequest setHTTPBody:[NSJSONSerialization dataWithJSONObject:parameters options:self.writingOptions error:error]];
    
    return mutableRequest;
}

@end
