//
//  TGHTTPRequestSerializer.h
//  TGOBD
//
//  Created by Jiahai on 14-3-19.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "AFURLRequestSerialization.h"

@interface TGHTTPRequestSerializer : AFHTTPRequestSerializer
/**
 The property list format. Possible values are described in "NSPropertyListFormat".
 */
@property (nonatomic, assign) NSPropertyListFormat format;

/**
 Options for writing the request JSON data from Foundation objects. For possible values, see the `NSJSONSerialization` documentation section "NSJSONWritingOptions". `0` by default.
 */
@property (nonatomic, assign) NSJSONWritingOptions writingOptions;

/**
 Creates and returns a JSON serializer with specified reading and writing options.
 
 @param writingOptions The specified JSON writing options.
 */
+ (instancetype)serializerWithWritingOptions:(NSJSONWritingOptions)writingOptions;
@end
