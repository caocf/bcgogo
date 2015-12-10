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

#pragma mark - Marco Parse TGRspHeader

#define TG_PARSE_RspHeader(jsonObject) \
    TGRspHeaderParser *headerParser = [[TGRspHeaderParser alloc] init];\
    NSObject *header = [headerParser parse:jsonObject];\
    __unused TGRspHeader *rspHeader = (TGRspHeader *)header;\
    NSMutableDictionary *dict = (NSMutableDictionary *)jsonObject;\

@implementation TGHTTPRequestParser

static NSDictionary *classNameDic = nil;

+ (id)parse:(id)jsonObject apiID:(NSInteger)apiID
{

    TG_PARSE_RspHeader(jsonObject)
    
    if(classNameDic == nil)
    {
        /**
         *  需要解析的类配置在这里，无特殊信息返回字段的无需配置。
         */
        classNameDic = @{
                        // @(apiID_oilStation_getList)    :   NSStringFromClass([TGModelOilStationListRsp class]),
                         @(apiID_violate_getCityList)   :   NSStringFromClass([TGModelViolateCityInfoListRsp class]),
                         @(apiID_violate_query)         :   NSStringFromClass([TGModelViolationInfoListRsp class]),
                         @(apiID_driveRecord_downloadList): NSStringFromClass([TGModelDriveRecordListRsp class]),
                         @(apiID_dtc_getList)           :   NSStringFromClass([TGModelDTCListRsp class]),
                         @(apiID_user_login)            :   NSStringFromClass([TGModelLoginRsp class]),
                         @(apiID_user_validateIMEI)     :   NSStringFromClass([TGModelValidateIMEIRsp class]),
                         @(apiID_user_register)         :   NSStringFromClass([TGModelRegisterRsp class]),
                         @(apiID_get_newMessage)        :   NSStringFromClass([TGModelGetNewMessageRsp class]),
                         @(apiID_get_orderList)         :   NSStringFromClass([TGModelOrderListRsp class]),
                         @(apiID_get_orderDetail)       :   NSStringFromClass([TGModelOrderDetailRsp class]),
                         @(apiID_check_newVersion)      :   NSStringFromClass([TGModelCheckNewVersionRsp class]),
                         @(apiID_driveRecord_downloadDetail) : NSStringFromClass([TGModelDriveRecordDetailRsp class]),
                         @(apiID_driveRecord_getVehicle):   NSStringFromClass([TGModelDriveRecordGetVehicleRsp class]),
                         @(apiID_get_advertList)        :   NSStringFromClass([TGMOdelPublicNoticeListRsp class]),
                         @(apiID_get_advert_detail)     :   NSStringFromClass([TGModelPublicNoticeDetailRsp class]),
                         @(apiID_get_driveStat)         :   NSStringFromClass([TGModelDriveStatisticRsp class]),
                         };
    }
    
    NSString *classString = [classNameDic objectForKey:@(apiID)];
    if(!classString)
    {
        classString = NSStringFromClass([TGComplexObject class]);
    }
    
    TGComplexObject *retObj = [TGHTTPRequestParser parseObjectWithDict:dict classname:classString];
    retObj.header = rspHeader;
    
    return retObj;
}

+ (id)parseObjectWithDict:(NSDictionary *)dict classname:(NSString *)classname
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
