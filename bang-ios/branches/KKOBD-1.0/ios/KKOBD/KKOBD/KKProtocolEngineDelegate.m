/*
 *  KKProtocolEngineDelegate.cpp
 *  Better
 *
 *  Created by apple on 10-4-8.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */


#import "KKProtocolEngineDelegate.h"
#import "KKProtocolEngine.h"

@implementation KKProtocolEngineSelector

+ (SEL) getResponseSEL:(NSInteger)aPtlApiId
{
	SEL sel = nil;
	switch (aPtlApiId) {
        case ePtlApi_user_register:
            sel = @selector(userRegisterResponse:withObject:);
            break;
        case ePtlApi_user_login:
            sel = @selector(userLoginResponse:withObject:);
            break;
        case ePtlApi_user_password:
            sel = @selector(userPasswordResponse:withObject:);
            break;
        case ePtlApi_obd_bind:
            sel = @selector(obdBindResponse:withObject:);
            break;
        case ePtlApi_new_version:
            sel = @selector(newVersionResponse:withObject:);
            break;
            
        case ePtlApi_vehicle_list:
            sel = @selector(vehicleListResponse:withObject:);
            break;
        case ePtlApi_vehicle_saveInfo:
            sel = @selector(vehicleSaveInfoResponse:withObject:);
            break;
        case ePtlApi_vehicle_getInfo:
            sel = @selector(vehicleGetInfoResponse:withObject:);
            break;
        case ePtlApi_vehicle_delete:
            sel = @selector(vehicleDeleteResponse:withObject:);
            break;
        case ePtlApi_vehicle_getModelByKey:
            sel = @selector(vehicleGetBrandModelResponse:withObject:);
            break;
        case ePtlApi_vehicle_fault:
            sel = @selector(vehicleFaultResponse:withObject:);
            break;
        case ePtlApi_vehicle_faultDic:
            sel = @selector(vehicleFaultDictResponse:withObject:);
            break;
        case ePtlApi_vehicle_condition:
            sel = @selector(vehicleConditionResponse:withObject:);
            break;
            
            
 
        case ePtlApi_area_list:
            sel = @selector(areaListResponse:withObject:);
            break;
        case ePtlApi_shop_searchList:
            sel = @selector(shopSearchListResponse:withObject:);
            break;
        case ePtlApi_shop_suggestionsByKey:
            sel = @selector(shopSuggestionsResponse:withObject:);
            break;
        case ePtlApi_shop_detail:
            sel = @selector(shopDetailResponse:withObject:);
            break;
            
        case ePtlApi_message_polling:
            sel = @selector(messagePollingResponse:withObject:);
            break;
        case ePtlApi_service_historyList:
            sel = @selector(serviceHistoryListResponse:withObject:);
            break;
        case ePtlApi_service_appointment:
            sel = @selector(serviceAppointResponse:withObject:);
            break;
        case ePtlApi_service_historyDetail:
            sel = @selector(serviceHistoryDetailResponse:withObject:);
            break;
        case ePtlApi_service_delete:
            sel = @selector(serviceDeleteResponse:withObject:);
            break;
            
            
        case ePtlApi_shop_score:
            sel = @selector(shopScoreResponse:withObject:);
            break;
        case ePtlApi_user_information:
            sel = @selector(userInformationResponse:withObject:);
            break;
        case ePtlApi_user_passwordModify:
            sel = @selector(userPasswordModifyResponse:withObject:);
            break;
        case ePtlApi_user_informationModify:
            sel = @selector(userInformationModifyResponse:withObject:);
            break;
        case ePtlApi_vehicle_maintainModify:
            sel = @selector(vehicleMaintainModifyResponse:withObject:);
            break;
        case ePtlApi_user_logout:
            sel = @selector(userLogoutResponse:withObject:);
            break;
        case ePtlApi_user_feedback:
            sel = @selector(userFeedbackResponse:withObject:);
            break;
        case ePtlApi_serviceCategory_list:
            sel = @selector(serviceCategoryListResponse:withObject:);
            break;

        case ePtlApi_vehicle_updateDefault:
            sel = @selector(updateDefaultVehicle:withObject:);
            break;
            
        case ePtlApi_vehicle_singleVehicle_vehicleVin:
            sel = @selector(getVehicleInfoByVehicleVin:withObject:);
            break;
        case ePtlApi_Register_SuggestVehicle:
            sel = @selector(getSuggestVehicleWithMobileResponse:withObject:);
            break;
            
        case ePtlApi_oil_stationList:
            sel = @selector(oilStationListResponse:withObject:);
            break;
        case ePtlApi_Register_shopBinding:
            sel = @selector(registerShopBindResponse:withObject:);
            break;
            
        case ePtlApi_violate_juheAreaList:
            sel = @selector(getViolateAreaListResponse:withObject:);
            break;
        case ePtlApi_violate_query:
            sel = @selector(getViolateJuheQueryResponse:withObject:);
		default:
			break;
	}

	return sel;
}

@end

