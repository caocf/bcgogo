//
//  TGHTTPRequestOperationManager.m
//  TGOBD
//
//  Created by Jiahai on 14-2-28.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGHTTPRequestOperationManager.h"
#import "TGHTTPRequestOperation.h"
#import "TGHTTPRequestSerializer.h"

@implementation TGHTTPRequestOperationManager

- (instancetype)initWithBaseURL:(NSURL *)url {
    self = [super initWithBaseURL:url];
    if (!self) {
        return nil;
    }
    
    self.securityPolicy.allowInvalidCertificates = YES;
    self.requestSerializer = [TGHTTPRequestSerializer serializer];
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

- (AFHTTPRequestOperation *)GET:(NSString *)URLString
                     parameters:(NSDictionary *)parameters
                        success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))success
                        failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))failure
{
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:URLString]];
    [request setHTTPMethod:@"GET"];
    
    request = [[self.requestSerializer requestBySerializingRequest:request withParameters:parameters error:nil] mutableCopy];
    
    AFHTTPRequestOperation *operation = [self HTTPRequestOperationWithRequest:request success:success failure:failure];
    [self.operationQueue addOperation:operation];
    
    return operation;
}

- (void)dealloc
{
    NSLog(@"TGHTTPRequestOperationManager dealloc!");
}

@end
