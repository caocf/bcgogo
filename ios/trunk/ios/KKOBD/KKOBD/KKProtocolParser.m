//
//  KKProtocolParser.m
//  KKShowBooks
//
//  Created by shugq on 13-08-11.
//  Copyright (c) 2012年 kk. All rights reserved.
//

#import <objc/runtime.h>
#import "KKError.h"
#import "KKProtocolEngine.h"
#import "KKProtocolParser.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"

// ================================================================================================
// 解析器类厂
// ================================================================================================
#pragma mark -
#pragma mark ParserFactory
@implementation KKProtocolParserFactory

+ (KKProtocolAbstractParser*) createParser:(NSInteger)aProtocolApiID
{
	KKProtocolAbstractParser *parser = nil;
	switch (aProtocolApiID) {
        case ePtlApi_user_register:
            parser = [[[KKUserRegisterRspParser alloc] init] autorelease];
        case ePtlApi_user_login:
            parser = [[[KKUserLoginRspParser alloc] init] autorelease];
            break;
        case ePtlApi_obd_bind:
            parser = [[[KKObdBindRspParser alloc] init] autorelease];
            break;
        case ePtlApi_user_password:
            parser = [[[KKUserPasswordRspParser alloc] init] autorelease];
            break;
        case ePtlApi_new_version:
            parser = [[[KKNewVersionRspParser alloc] init] autorelease];
            break;
            
        case ePtlApi_vehicle_list:
            parser = [[[KKVehicleListRspParser alloc] init] autorelease];
            break;
        case ePtlApi_vehicle_saveInfo:
            parser = [[[KKVehicleSaveInfoRspParser alloc] init] autorelease];
            break;
        case ePtlApi_vehicle_getInfo:
            parser = [[[KKVehicleGetInfoRspParser alloc] init] autorelease];
            break;
        case ePtlApi_vehicle_delete:
            parser = [[[KKVehicleDeleteRspParser alloc] init] autorelease];
            break;
        case ePtlApi_vehicle_getModelByKey:
            parser = [[[KKVehicleGetBrandModelRspParser alloc] init] autorelease];
            break;
        case ePtlApi_vehicle_fault:
            parser = [[[KKVehicleFaultRspParser alloc] init] autorelease];
            break;
        case ePtlApi_vehicle_faultDic:
            parser = [[[KKVehicleFaultDictRspParser alloc] init] autorelease];
            break;
        case ePtlApi_vehicle_condition:
            parser = [[[KKVehicleConditionRspParser alloc] init] autorelease];
            break;
            
            
        case ePtlApi_shop_searchList:
            parser = [[[KKShopSearchListRspParser alloc] init] autorelease];
            break;
        case ePtlApi_area_list:
            parser = [[[KKAreaListRspParser alloc] init] autorelease];
            break;
        case ePtlApi_shop_suggestionsByKey:
            parser = [[[KKShopSuggestionsRspParser alloc] init] autorelease];
            break;
        case ePtlApi_shop_detail:
            parser = [[[KKShopDetailRspParser alloc] init] autorelease];
            break;
            
        case ePtlApi_message_polling:
            parser = [[[KKMessagePollingRspParser alloc] init] autorelease];
            break;
        case ePtlApi_service_appointment:
            parser = [[[KKServiceAppointmentRspParser alloc] init] autorelease];
            break;
        case ePtlApi_service_historyList:
            parser = [[[KKServiceHistoryListRspParser alloc] init] autorelease];
            break;
        case ePtlApi_service_historyDetail:
            parser = [[[KKServiceHistoryDetailRspParser alloc] init] autorelease];
            break;
        case ePtlApi_service_delete:
            parser = [[[KKServiceDeleteRspParser alloc] init] autorelease];
            break;
            
        case ePtlApi_shop_score:
            parser = [[[KKShopScoreRspParser alloc] init] autorelease];
            break;
        case ePtlApi_user_information:
            parser = [[[KKUserInformationRspParser alloc] init] autorelease];
            break;
        case ePtlApi_user_passwordModify:
            parser = [[[KKUserPasswordModifyRspParser alloc] init] autorelease];
            break;
        case ePtlApi_user_informationModify:
            parser = [[[KKUserInformationModifyRspParser alloc] init] autorelease];
            break;
        case ePtlApi_vehicle_maintainModify:
            parser = [[[KKVehicleMaintainModifyRspParser alloc] init] autorelease];
            break;
        case ePtlApi_user_logout:
            parser = [[[KKUserLogoutRspParser alloc] init] autorelease];
            break;
        case ePtlApi_user_feedback:
            parser = [[[KKUserFeedbackRspParser alloc] init] autorelease];
            break;
        case ePtlApi_serviceCategory_list:
            parser = [[[KKServiceCategoryListRspParser alloc] init] autorelease];
            break;
        case ePtlApi_vehicle_updateDefault:
            parser = [[[KKVehicleUpdateDefaultRspParser alloc] init] autorelease];
            break;
        case ePtlApi_vehicle_singleVehicle_vehicleVin:
            parser = [[[KKVehicleGetInfoRspParser alloc] init] autorelease];
            break;
            
        default:
            break;
	}
	
	return parser;
}

@end

// ================================================================================================
// 解析器基类（抽象类）
// ================================================================================================
#pragma mark -
#pragma mark abstractParser
@implementation KKProtocolAbstractParser

- (id) parse:(id)jsonObject
{
	NSAssert(NO, @"KKProtocolAbstractParser is an abstract class without implementing");
	return nil;
}

-(id) parseObjectWithDict:(NSDictionary *)dict classname:(NSString *)classname
{
    Class cls = NSClassFromString(classname);
    id retObj = [[[cls alloc] init] autorelease];
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
        
        if ([keyName isEqualToString:@"memberInfo"])
        {
            NSInteger j=0;
            j++;
        }
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
                    [numberFormatter release];
                }
                // If inherit from KKModelObject(not a simple type), should recursive
                else if ([class isSubclassOfClass:[KKModelObject class]] && [[value class] isSubclassOfClass:[NSDictionary class]]) {
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
                
                if ([class isSubclassOfClass:[KKModelObject class]] && NO == [value isKindOfClass:[KKModelObject class]])
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
                    [numberFormatter release];
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


#pragma mark -
// ================================================================================================
// KKRspHeaderParser
// ================================================================================================
@implementation KKRspHeaderParser

- (id) parse:(id)jsonObject
{
	KKError *err = nil;
    NSObject *obj = (NSObject *)jsonObject;
    if (NO == [[obj class] isSubclassOfClass:[NSMutableDictionary class]]) {
        err = [KKError KKErrorWithCode:eErrorJsonParser withSubcode:error_parse_not_json withDesc:@"网络连接异常" withDetailErr:nil];
		return err;
    }
	
    NSMutableDictionary *dict = (NSMutableDictionary *)obj;
    KKModelRspHeader *modelHeader = [[KKModelRspHeader alloc] init];
    
    NSString *status = [dict objectForKey:@"status"];
    if (NSOrderedSame == [status compare:@"SUCCESS" options:NSCaseInsensitiveSearch])
        modelHeader.code = eRsp_succeed;
    else if (NSOrderedSame == [status compare:@"FAIL" options:NSCaseInsensitiveSearch])
        modelHeader.code = eRsp_failed;
    else
        modelHeader.code = eRsp_unknown;
    
    NSString *msgCode = [dict objectForKey:@"msgCode"];
    modelHeader.msgCode = [msgCode integerValue];
    
    NSString *desc = [dict objectForKey:@"message"];
    modelHeader.desc = desc;
 
	return [modelHeader autorelease];
}

@end

// ================================================================================================
// 基本元素解析类
// ================================================================================================
#pragma mark -
#pragma mark 基本元素

// ================================================================================================
// 业务相关解析类
// ================================================================================================
#pragma mark -
#pragma mark complex common marco

#define KK_PARSE_PREPARE(jsonObject, aDesc) \
    KKRspHeaderParser *headerParser = [[KKRspHeaderParser alloc] init]; \
    NSObject *header = [headerParser parse:jsonObject]; \
    [headerParser release]; \
    if ([[header class] isSubclassOfClass:[KKError class]]) { \
        KKError *err = (KKError*)header;    \
        err.description = [NSString stringWithFormat:@"%@",err.description];    \
        return err; \
    } \
    KKModelRspHeader *rspHeader = (KKModelRspHeader*)header; \
    if (rspHeader.code != eRsp_succeed) { \
        KKError *err = [KKError KKErrorWithCode:eErrorProtocol withSubcode:rspHeader.msgCode withDesc:aDesc withDetailErr:nil];\
        if ([rspHeader.desc length] > 0) \
            err.description = rspHeader.desc; \
		return err; \
    } \
    NSMutableDictionary *dict = nil; \
    dict = (NSMutableDictionary *)jsonObject;



// ================================================================================================
// 业务相关解析类
// ================================================================================================
#pragma mark -
#pragma mark 业务相关解析类

#pragma mark -
#pragma mark system and setting
#pragma mark -
#pragma mark KKUserRegisterRspParser
@implementation KKUserRegisterRspParser

-(id) parse:(id)jsonObject
{
//    KKRspHeaderParser *headerParser = [[KKRspHeaderParser alloc] init]; 
//	NSObject *header = [headerParser parse:jsonObject];
//	[headerParser release];
//    
//	if ([[header class] isSubclassOfClass:[KKError class]]) { 
//		KKError *err = (KKError*)header; 
//		err.description = [NSString stringWithFormat:@"%@",err.description];
//		return err; 
//	} 
//	KKModelRspHeader *rspHeader = (KKModelRspHeader*)header; 
//	if (rspHeader.code != eRsp_succeed) { 
//        KKError *err = [KKError KKErrorWithCode:eErrorProtocol withSubcode:rspHeader.msgCode withDesc:@"操作失败" withDetailErr:nil]; 
//        if ([rspHeader.desc length] > 0) 
//            err.description = rspHeader.desc; 
//		return err; 
//	}
    
    KK_PARSE_PREPARE(jsonObject, @"user register parse error")
    
    KKModelProtocolRsp *rsp = [[KKModelProtocolRsp alloc] init];
    rsp.header = rspHeader;
    
    return [rsp autorelease];
}

@end


@implementation helloclass
@synthesize propName = varName;
+ (id)class { return self; }
+ (void)testMethod
{
	unsigned int outCount, i;
	objc_property_t *properties = class_copyPropertyList([helloclass class], &outCount);
	assert(outCount == 1);
	objc_property_t property = properties[0];
	assert(strcmp(property_getName(property), "propName") == 0);
	assert(strcmp(property_getAttributes(property), "Ti,VvarName") == 0);
	free(properties);
	Method* methods = class_copyMethodList([helloclass class], &outCount);
	assert(outCount == 2);
	free(methods);
    
	objc_property_attribute_t a = { "V", "varName2" };
	assert(class_addProperty([helloclass class], "propName2", &a, 1));
	properties = class_copyPropertyList([helloclass class], &outCount);
	assert(outCount == 2);
	int found = 0;
	for (int i=0 ; i<2 ; i++)
	{
		property = properties[i];
		fprintf(stderr, "Name: %s\n", property_getName(property));
		fprintf(stderr, "Attrs: %s\n", property_getAttributes(property));
		if (strcmp(property_getName(property), "propName2") == 0)
		{
			assert(strcmp(property_getAttributes(property), "VvarName2") == 0);
			found++;
		}
	}
    helloclass *he = [[[helloclass class] alloc] init];
    he.propName = 23;
    
    Ivar iv = class_getInstanceVariable([helloclass class], "varName");
    id d = object_getIvar(he, iv);
    fprintf(stderr, "propName2=%d", d);
    [he release];
    
 	assert(found == 1);
    
}
@end

#pragma mark -
#pragma mark KKUserLoginRspParser
@implementation KKUserLoginRspParser
-(id) parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"user login parse error")
    
    // traditional method
//    KKModelLoginRsp *rsp = [[KKModelLoginRsp alloc] init];
//    rsp.header = rspHeader;
//
//    rsp.obdList__KKModelObdInfo = [[[NSMutableArray alloc] initWithCapacity:10] autorelease];
//    NSMutableArray *obdList = (NSMutableArray *)[dict objectForKey:@"obdList"];
//        
//    for (NSInteger i=0; i<[obdList count]; i++) {
//        NSMutableDictionary *item = (NSMutableDictionary *)[obdList objectAtIndex:i];
//        KKModelObdInfo *info = [self parseObjectWithDict:item classname:@"KKModelObdInfo"];
//        [rsp.obdList__KKModelObdInfo addObject:info];
//    }
//    
//    NSMutableDictionary *appConfig = [dict objectForKey:@"appConfig"];
//    rsp.appConfig = [self parseObjectWithDict:appConfig classname:@"KKModelAppConfig"];
    
    // smart method
    KKModelLoginRsp *rsp = [self parseObjectWithDict:dict classname:@"KKModelLoginRsp"];
    rsp.header = rspHeader;
    
    return rsp;
}

@end

#pragma mark -
#pragma mark KKObdBindRspParser
@implementation KKObdBindRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"obd binding parse error")
    
    KKModelProtocolRsp *rsp = [[KKModelProtocolRsp alloc] init];
    rsp.header = rspHeader;
    
    return [rsp autorelease];
}

@end


#pragma mark -
#pragma mark KKObdBindRspParser
@implementation KKUserPasswordRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"user password parse error")
    
    KKModelProtocolRsp *rsp = [[KKModelProtocolRsp alloc] init];
    rsp.header = rspHeader;
    
    return [rsp autorelease];
}

@end

#pragma mark -
#pragma mark KKNewVersionRspParser
@implementation KKNewVersionRspParser 
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"new version parse error")
    
    KKModelNewVersionRsp *rsp = [self parseObjectWithDict:dict classname:@"KKModelNewVersionRsp"];;
    rsp.header = rspHeader;
    
    return rsp;
}
@end


#pragma mark -
#pragma mark vehicle relative
#pragma mark -
#pragma mark KKVehicleListRspParser
@implementation KKVehicleListRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"vehicle list parse error")
    
    KKVehicleListRsp *listRsp = [self parseObjectWithDict:dict classname:@"KKVehicleListRsp"];
    listRsp.header = rspHeader;
    return listRsp;
}

@end

#pragma mark -
#pragma mark KKVehicleSaveInfoRspParser
@implementation KKVehicleSaveInfoRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"vehicle save info parse error")
    
    KKModelProtocolRsp *rsp = [[KKModelProtocolRsp alloc] init];
    rsp.header = rspHeader;
    
    return [rsp autorelease];
}
@end

#pragma mark -
#pragma mark KKVehicleGetInfoRspParser
@implementation KKVehicleGetInfoRspParser 
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"vehicle get info parse error")
    
    KKModelVehicleGetInfoRsp *rsp = [self parseObjectWithDict:dict classname:@"KKModelVehicleGetInfoRsp"];;
    rsp.header = rspHeader;
    
    return rsp;
}
@end

#pragma mark -
#pragma mark KKVehicleDeleteRspParser
@implementation KKVehicleDeleteRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"vehicle delete parse error")
    
    KKModelProtocolRsp *rsp = [[KKModelProtocolRsp alloc] init];
    rsp.header = rspHeader;
    
    return [rsp autorelease];
}
@end

#pragma mark -
#pragma mark KKVehicleFaultRspParser
@implementation KKVehicleFaultRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"vehicle fault parse error")
    
    KKModelProtocolRsp *rsp = [[KKModelProtocolRsp alloc] init];
    rsp.header = rspHeader;
    
    return [rsp autorelease];
}
@end

#pragma mark -
#pragma mark KKVehicleFaultDictRspParser
@implementation KKVehicleFaultDictRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"vehicle fault dict parse error")
    
    KKModelVehicleFaultDictRsp *rsp = [self parseObjectWithDict:dict classname:@"KKModelVehicleFaultDictRsp"];
    rsp.header = rspHeader;
    
    return rsp;
}
@end

#pragma mark -
#pragma mark KKVehicleGetBrandModelRspParser
@implementation KKVehicleGetBrandModelRspParser 
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"vehicle get brandmodel parse error")
    
    KKModelGetBrandModelRsp *rsp = [self parseObjectWithDict:dict classname:@"KKModelGetBrandModelRsp"];
    rsp.header = rspHeader;
    
    return rsp;
}
@end

#pragma mark -
#pragma mark KKVehicleConditionRspParser
@implementation KKVehicleConditionRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"vehicle condition parse error")
    
    KKModelProtocolRsp *rsp = [[KKModelProtocolRsp alloc] init];
    rsp.header = rspHeader;
    
    return [rsp autorelease];
}
@end


#pragma mark -
#pragma mark shop relative
#pragma mark -
#pragma mark KKModelAreaListRsp
@implementation KKAreaListRspParser 
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"area list parse error")
    
    KKModelAreaListRsp *rsp = [self parseObjectWithDict:dict classname:@"KKModelAreaListRsp"];
    rsp.header = rspHeader;
    
    return rsp;
}
@end

#pragma mark -
#pragma mark KKShopSearchListRspParser
@implementation KKShopSearchListRspParser 
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"shop searchList parse error")
    
    KKModelShopSearchListRsp *rsp = [self parseObjectWithDict:dict classname:@"KKModelShopSearchListRsp"];
    rsp.header = rspHeader;
    
    return rsp;
}
@end


#pragma mark -
#pragma mark KKShopSuggestionsRspParser
@implementation KKShopSuggestionsRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"shop suggestions parse error")
    
    KKModelShopSuggestionsRsp *rsp = [self parseObjectWithDict:dict classname:@"KKModelShopSuggestionsRsp"];
    rsp.header = rspHeader;
    
    return rsp;
}
@end

#pragma mark -
#pragma mark KKShopDetailRspParser
@implementation KKShopDetailRspParser 
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"shop detail parse error")
    
    KKModelShopDetailRsp *rsp = [self parseObjectWithDict:dict classname:@"KKModelShopDetailRsp"];
    rsp.header = rspHeader;
    
    return rsp;
}
@end

#pragma mark -
#pragma mark service relative
#pragma mark -
#pragma mark KKMessagePollingRspParser
@implementation KKMessagePollingRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"message polling parse error")
    
    KKModelMessagePollingRsp *rsp = [self parseObjectWithDict:dict classname:@"KKModelMessagePollingRsp"];
    rsp.header = rspHeader;
    
    return rsp;
}
@end

#pragma mark -
#pragma mark KKServiceCategoryListRspParser
@implementation KKServiceCategoryListRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"service Category list Parse error")
    
    KKModelServiceCategoryListRsp *rsp = [self parseObjectWithDict:dict classname:@"KKModelServiceCategoryListRsp"];
    rsp.header = rspHeader;
    
    return rsp;
}

@end

#pragma mark -
#pragma mark KKServiceAppointmentRspParser
@implementation KKServiceAppointmentRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"service appointment parse error")
    
    KKModelProtocolRsp *rsp = [[KKModelProtocolRsp alloc] init];
    rsp.header = rspHeader;
    
    return [rsp autorelease];
}
@end

#pragma mark -
#pragma mark KKServiceHistoryListRspParser
@implementation KKServiceHistoryListRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"service historyList parse error")
    
    KKModelServiceHistoryListRsp *rsp = [self parseObjectWithDict:dict classname:@"KKModelServiceHistoryListRsp"];
    rsp.header = rspHeader;
    
    return rsp;
}
@end

#pragma mark -
#pragma mark KKServiceHistoryDetailRspParser
@implementation KKServiceHistoryDetailRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"service detail parse error")
    
    KKModelServiceDetailRsp *rsp = [self parseObjectWithDict:dict classname:@"KKModelServiceDetailRsp"];
    rsp.header = rspHeader;
    
    return rsp;
}

@end

#pragma mark -
#pragma mark KKServiceDeleteRspParser
@implementation KKServiceDeleteRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"service delete parse error")
    
    KKModelProtocolRsp *rsp = [[KKModelProtocolRsp alloc] init];
    rsp.header = rspHeader;
    
    return [rsp autorelease];
}
@end

#pragma mark - 
#pragma mark setting relative
#pragma mark 
@implementation KKShopScoreRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"shop score parse error")
    
    KKModelProtocolRsp *rsp = [[KKModelProtocolRsp alloc] init];
    rsp.header = rspHeader;
    
    return [rsp autorelease];
}
@end

@implementation KKUserPasswordModifyRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"user modify password parse error")
    
    KKModelProtocolRsp *rsp = [[KKModelProtocolRsp alloc] init];
    rsp.header = rspHeader;
    
    return [rsp autorelease];
}
@end

@implementation KKUserInformationModifyRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"user info modify parse error")
    
    KKModelProtocolRsp *rsp = [[KKModelProtocolRsp alloc] init];
    rsp.header = rspHeader;
    
    return [rsp autorelease];
}
@end

@implementation KKVehicleMaintainModifyRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"vehicle maintain modify parse error")
    
    KKModelProtocolRsp *rsp = [[KKModelProtocolRsp alloc] init];
    rsp.header = rspHeader;
    
    return [rsp autorelease];
}
@end

@implementation KKUserLogoutRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"user logout parse error")
    
    KKModelProtocolRsp *rsp = [[KKModelProtocolRsp alloc] init];
    rsp.header = rspHeader;
    
    return [rsp autorelease];
}
@end

@implementation KKUserFeedbackRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"user feedback parse error")
    
    KKModelProtocolRsp *rsp = [[KKModelProtocolRsp alloc] init];
    rsp.header = rspHeader;
    
    return [rsp autorelease];
}
@end

@implementation KKUserInformationRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"user information parse error")
    
    KKModelUserInfomationRsp *rsp = [self parseObjectWithDict:dict classname:@"KKModelUserInfomationRsp"];
    rsp.header = rspHeader;
    
    return rsp;
}
@end


@implementation KKVehicleUpdateDefaultRspParser
- (id)parse:(id)jsonObject
{
    KK_PARSE_PREPARE(jsonObject, @"update default vehicle parse error")
    KKModelProtocolRsp *rsp = [[KKModelProtocolRsp alloc] init];
    rsp.header = rspHeader;
    return [rsp autorelease];
}

@end






