//
//  XSHTTPRequestParser.m
//  General
//
//  Created by Jiahai on 14-2-27.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGHTTPRequestParser.h"
#import "TGHTTPRequestEngine.h"
#import <objc/runtime.h>

@implementation TGHTTPRequestParserFactory
+ (TGHTTPRequestAbstractParser *)createParser:(NSInteger)aRequestApiID
{
    TGHTTPRequestAbstractParser *parser = nil;
    switch (aRequestApiID) {
        case apiID_oilStation_getList:
            parser = [[TGModelOilStationListRspParser alloc] init];
            break;
        case apiID_Shop_serviceCategoty:
            parser = [[TGModelServiceCategoryRspParser alloc] init];
            break;
            
        default:
            break;
    }
    return parser;
}
@end


@implementation TGHTTPRequestAbstractParser

- (id)parse:(id)jsonObject
{
    return nil;
}

- (id)parseObjectWithDict:(NSDictionary *)dict classname:(NSString *)classname
{
    Class cls = NSClassFromString(classname);
    id retObj = [[cls alloc] init];
    if (dict == nil || [dict count] == 0)
        return retObj;
    
   	unsigned int outCount;
	objc_property_t *properties = class_copyPropertyList(cls, &outCount);
    for (NSInteger i=0; i<outCount; i++) {
        objc_property_t property = properties[i];
        const char *cszName = property_getName(property);
        NSString *keyName = [NSString stringWithCString:cszName encoding:NSUTF8StringEncoding];
        NSString *reservedKeyName = keyName;
        NSString *extName = nil;
        // reserved key handle
        if ([keyName length] > 2) {
            NSRange range = [keyName rangeOfString:@"__"];
            if (range.length > 0) {
                extName = [keyName substringFromIndex:range.location+range.length];
                keyName = [keyName substringToIndex:range.location];
            }
        }
        NSObject *value = (NSObject *)[dict objectForKey:keyName];
        if (value == nil || [[value class] isSubclassOfClass:[NSNull class]])
            continue;
        
        char *typeEncoding = NULL;
        typeEncoding = property_copyAttributeValue(property, "T");
        
        if (typeEncoding == NULL)
            continue;
        
        switch (typeEncoding[0]) {
            case '@':
            {
                // Object
                Class class = nil;
                if (strlen(typeEncoding) >= 3) {
                    char *className = strndup(typeEncoding+2, strlen(typeEncoding)-3);
                    class = NSClassFromString([NSString stringWithUTF8String:className]);
                    free(className);
                }
                // Check for type mismatch, attempt to compensate
                if ([class isSubclassOfClass:[NSString class]] && [value isKindOfClass:[NSNumber class]]) {
                    NSNumber *num = (NSNumber *)value;
                    value = [num stringValue];
                }
                else if ([class isSubclassOfClass:[NSNumber class]] && [value isKindOfClass:[NSString class]]) {
                    // If the ivar is an NSNumber we really can't tell if it's intended as an integer, float, etc.
                    NSNumberFormatter *numberFormatter = [[NSNumberFormatter alloc] init];
                    [numberFormatter setNumberStyle:NSNumberFormatterDecimalStyle];
                    value = [numberFormatter numberFromString:(NSString *)value];
                    
                }
                // If inherit from KKModelObject(not a simple type), should recursive
                else if ([class isSubclassOfClass:[TGModelObject class]] && [[value class] isSubclassOfClass:[NSDictionary class]]) {
                    value = [self parseObjectWithDict:(NSDictionary*)value classname:NSStringFromClass(class)];
                }
                // If array
                else if ([class isSubclassOfClass:[NSMutableArray class]] && [[value class] isSubclassOfClass:[NSArray class]] && [extName length] > 0) {
                    NSArray *arr = (NSArray *)value;
                    NSMutableArray *fieldArr = [NSMutableArray arrayWithCapacity:10];
                    for (NSInteger i=0; i<[arr count]; i++) {
                        NSDictionary *itemDict = [arr objectAtIndex:i];
                        if (NO == [[itemDict class] isSubclassOfClass:[NSDictionary class]])
                            continue;
                        id fieldObj = [self parseObjectWithDict:itemDict classname:extName];
                        [fieldArr addObject:fieldObj];
                    }
                    value = fieldArr;
                }
                
                if ([class isSubclassOfClass:[TGModelObject class]] && NO == [value isKindOfClass:[TGModelObject class]])
                    [retObj setValue:nil forKey:reservedKeyName];
                else
                    [retObj setValue:value forKey:reservedKeyName];
            }
                break;
                
            case 'i': // int
            case 's': // short
            case 'l': // long
            case 'q': // long long
            case 'I': // unsigned int
            case 'S': // unsigned short
            case 'L': // unsigned long
            case 'Q': // unsigned long long
            case 'f': // float
            case 'd': // double
            case 'B': // BOOL
            {
                if ([value isKindOfClass:[NSString class]]) {
                    NSNumberFormatter *numberFormatter = [[NSNumberFormatter alloc] init];
                    [numberFormatter setNumberStyle:NSNumberFormatterDecimalStyle];
                    value = [numberFormatter numberFromString:(NSString *)value];
                    if (nil == value)
                        value = [NSNumber numberWithInt:0];
                    
                }
                [retObj setValue:value forKey:reservedKeyName];
                break;
            }
                
            case 'c': // char
            case 'C': // unsigned char
            {
                if ([value isKindOfClass:[NSString class]]) {
                    char firstCharacter = [(NSString*)value characterAtIndex:0];
                    value = [NSNumber numberWithChar:firstCharacter];
                }
                [retObj setValue:value forKey:reservedKeyName];
            }
                break;
            default:
                break;
        }
        
    }
    free(properties);
    return retObj;
}

@end

@implementation TGRspHeaderParser

- (id)parse:(id)jsonObject
{
    NSMutableDictionary *dict = (NSMutableDictionary *)jsonObject;
    TGRspHeader *modelHeader = [[TGRspHeader alloc] init];
    
    NSString *status = [dict objectForKey:@"status"];
    if (NSOrderedSame == [status compare:@"SUCCESS" options:NSCaseInsensitiveSearch])
        modelHeader.status = rspStatus_Succeed;
    else if (NSOrderedSame == [status compare:@"FAIL" options:NSCaseInsensitiveSearch])
        modelHeader.status = rspStatus_Failed;
    else
        modelHeader.status = rspStatus_Unknown;
    
    NSString *msgCode = [dict objectForKey:@"msgCode"];
    modelHeader.msgCode = [msgCode integerValue];
    
    NSString *desc = [dict objectForKey:@"message"];
    modelHeader.message = desc;
    
	return modelHeader;

}

@end

#pragma mark - Marco Parse TGRspHeader
#define TG_PARSE_RspHeader(jsonObject) \
    TGRspHeaderParser *headerParser = [[TGRspHeaderParser alloc] init];\
    NSObject *header = [headerParser parse:jsonObject];\
    __unused TGRspHeader *rspHeader = (TGRspHeader *)header;\
    NSMutableDictionary *dict = (NSMutableDictionary *)jsonObject;\


#pragma mark - 加油站

@implementation TGModelOilStationListRspParser

- (id)parse:(id)jsonObject
{
    TG_PARSE_RspHeader(jsonObject)
    TGModelOilStationListRsp *rsp = [self parseObjectWithDict:dict classname:NSStringFromClass([TGModelOilStationListRsp class])];
    return rsp;
}

@end

@implementation TGModelServiceCategoryRspParser

- (id)parse:(id)jsonObject
{
    TG_PARSE_RspHeader(jsonObject)
    TGModelServiceCategoryRsp *model = [self parseObjectWithDict:dict classname:NSStringFromClass([TGModelServiceCategoryRsp class])];
    model.header = rspHeader;
    
    return model;
}
@end