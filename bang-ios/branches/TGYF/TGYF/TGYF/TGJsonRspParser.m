//
//  TGJsonRspParser.m
//  TGYIFA
//
//  Created by James Yu on 14-5-14.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGJsonRspParser.h"
#import "TGHttpManager.h"
#import "TGComplexModel.h"

@implementation TGJsonRspParser

+ (id)parserWithJsonString:(NSString *)jsonStr apiId:(NSInteger)apiId
{
    Class cls = NSClassFromString([TGJsonParserModelFactory createParseClass:apiID_user_login]);
    
    NSError *err = nil;
    
    TGComplexModel *retObj = [[cls alloc] initWithString:jsonStr error:&err];
    
    if (err) {
        NSLog(@"=====fail to parser json ===%@=====", err.description);
    }
    
    NSDictionary *jsonObj = [NSJSONSerialization JSONObjectWithData:[jsonStr dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingMutableLeaves error:&err];
    
    retObj.header = [TGJsonRspParser parseModelHeader:jsonObj];
    
    return retObj;

}

+ (id)parseModelHeader:(id)jsonObject
{
    NSMutableDictionary *dict = (NSMutableDictionary *)jsonObject;
    TGModelRspHeader *header = [[TGModelRspHeader alloc] init];
    
    header.status = [dict objectForKey:@"status"];
    header.message = [dict objectForKey:@"message"];
    header.msgCode = [[dict objectForKey:@"msgCode"] intValue];
    
    return header;
}

@end

@implementation TGJsonParserModelFactory

+ (NSString *)createParseClass:(NSInteger)apiId
{
    NSString *classStr = nil;
    switch (apiId) {
        case apiID_user_login:
            classStr = NSStringFromClass([TGModelLoginRsp class]);
            break;
        case apiID_get_SMS_content:
            classStr = NSStringFromClass([TGModelGetSMSRsp class]);
            break;
        case apiID_send_SMS:
            classStr = NSStringFromClass([TGModelSendMsgRsp class]);
            break;
        case apiID_remindHandle:
            classStr = NSStringFromClass([TGModelRemindHandleRsp class]);
            break;
        case apiID_accept_appoint:
            classStr = NSStringFromClass([TGModelAcceptAppointRsp class]);
            break;
        case apiID_change_appoint_time:
            classStr = NSStringFromClass([TGModelChangeAppointTimeRsp class]);
            break;
        case apiID_get_faultInfo_list:
            classStr = NSStringFromClass([TGModelGetVehicleFaultInfoList class]);
            break;
        case apiID_get_remind_list:
            classStr = NSStringFromClass([TGModelGetCustomerRemindList class]);
            break;
        default:
            break;
    }
    return classStr;
}

@end