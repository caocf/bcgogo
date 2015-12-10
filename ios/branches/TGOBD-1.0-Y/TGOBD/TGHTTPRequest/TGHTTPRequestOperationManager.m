//
//  TGHTTPRequestOperationManager.m
//  TGOBD
//
//  Created by Jiahai on 14-2-28.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGHTTPRequestOperationManager.h"
#import "TGHTTPRequestOperation.h"

@implementation TGHTTPRequestOperationManager

- (instancetype)initWithBaseURL:(NSURL *)url {
    self = [super initWithBaseURL:url];
    if (!self) {
        return nil;
    }
    
    self.securityPolicy.allowInvalidCertificates = YES;
    [self.requestSerializer setValue:@"gzip" forHTTPHeaderField:@"Content-Encoding"];
    
    return self;
}

- (TGHTTPRequestOperation *)HTTPRequestOperationWithRequest:(NSURLRequest *)request
                                                    success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))success
                                                    failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))failure
{
    TGHTTPRequestOperation *operation = [[TGHTTPRequestOperation alloc] initWithRequest:request];
    operation.responseSerializer = self.responseSerializer;
    operation.shouldUseCredentialStorage = self.shouldUseCredentialStorage;
    operation.credential = self.credential;
    operation.securityPolicy = self.securityPolicy;
    
    [operation setCompletionBlockWithSuccess:success failure:failure];
    
    return operation;
}

- (void)dealloc
{
    NSLog(@"TGHTTPRequestOperationManager dealloc!");
}

@end
