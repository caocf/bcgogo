//
//  TGJsonRspParser.h
//  TGYIFA
//
//  Created by James Yu on 14-5-14.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
@class TGComplexModel;

@interface TGJsonRspParser : NSObject

+ (id)parserWithJsonString:(NSString *)jsonStr apiId:(NSInteger)apiId;

@end

@interface TGJsonParserModelFactory : NSObject

+ (NSString *)createParseClass:(NSInteger)apiId;

@end