//
//  KKPreference.m
//  KKOBD
//
//  Created by zhuyc on 13-8-9.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKPreference.h"

// ========================================================================================================
static KKPreference *g_preference = nil;

@implementation KKPreference

+ (KKPreference *)sharedPreference
{
    @synchronized(self)
    {
        if (g_preference == nil)
            g_preference = [[KKPreference alloc] init];
    }
    return g_preference;
}

- (void)dealloc
{
    g_preference = nil;
    
    [super dealloc];
}

- (NSObject *)getPreference:(KKPreferenceEnum)aPreference
{
    if (aPreference == eUserInfo)
        return self.userInfo;
    if (aPreference == eAppConfig)
        return self.appConfig;
    if (aPreference == eVoicePrompt)
        return self.voiceSwitch;
    if (aPreference == ePeripheral)
        return self.peripheral;
    if (aPreference == eGlobalValue)
        return self.globalValues;
    if (aPreference == eCityInfo)
        return self.cityInfo;
    
    return nil;
}

//-------------------------------------userInfo------------------------------------------------
- (void)setUserInfo:(KKModelPreferenceUserInfo *)aUserInfo
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    if (nil == aUserInfo)
    {
//        [userDefault removeObjectForKey:@"userInfo"];
//        [userDefault synchronize];
        _getUserInfo_Nil = YES;
		return;
    }
    
    _getUserInfo_Nil = NO;
    NSMutableArray *array = [self userInfoArray];
    if(array != nil)
    {
        for(KKModelPreferenceUserInfo *info in array)
        {
            if([info.userNo isEqualToString:aUserInfo.userNo])
            {
                [array removeObject:info];
                break;
            }
        }
    }
    [array insertObject:aUserInfo atIndex:0];
    
    [userDefault setObject:[NSKeyedArchiver archivedDataWithRootObject:array] forKey:@"userInfo"];
	[userDefault synchronize];
}

- (KKModelPreferenceUserInfo *)userInfo
{
    if(_getUserInfo_Nil)
        return [[[KKModelPreferenceUserInfo alloc] init] autorelease];
    
    NSArray *array = [self userInfoArray];
    KKModelPreferenceUserInfo *aUserInfo;
    if(array != nil && [array count]>0)
    {
        aUserInfo = [array objectAtIndex:0];
    }
    else
    {
        aUserInfo = [[[KKModelPreferenceUserInfo alloc] init] autorelease];
    }
    return aUserInfo;
}

-(NSMutableArray *)userInfoArray
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    NSMutableArray *array;
	id userinfo = [userDefault objectForKey:@"userInfo"];
    if([[userinfo class] isSubclassOfClass:[NSDictionary class]])
    {
        [userDefault removeObjectForKey:@"userInfo"];
        [userDefault synchronize];
        
        //兼容单帐号存储
        KKModelPreferenceUserInfo *aUserInfo = [[KKModelPreferenceUserInfo alloc] init];
        aUserInfo.userNo = [userinfo objectForKey:@"userNo"];
        aUserInfo.password = [userinfo objectForKey:@"password"];
        aUserInfo.username = [userinfo objectForKey:@"userName"];
        aUserInfo.mobile = [userinfo objectForKey:@"mobile"];
        
        array = [[[NSMutableArray alloc] init] autorelease];
        [array addObject:aUserInfo];
        [aUserInfo release];
        
        [userDefault setObject:[NSKeyedArchiver archivedDataWithRootObject:array] forKey:@"userInfo"];
        [userDefault synchronize];
    }
    else if([[userinfo class] isSubclassOfClass:[NSData class]])
    {
        array = [NSKeyedUnarchiver unarchiveObjectWithData:userinfo];
    }
    else
    {
        array = [[[NSMutableArray alloc] init] autorelease];
    }
    
    return array;
}

-(void) removeSavedUserInfo:(KKModelPreferenceUserInfo *)uInfo
{
    NSMutableArray *array = [self userInfoArray];
    BOOL over = NO;
    do {
        for(int i=0;i<[array count];i++)
        {
            KKModelPreferenceUserInfo *info = (KKModelPreferenceUserInfo *)[array objectAtIndex:i];
            if([info.userNo isEqualToString:uInfo.userNo])
            {
                [array removeObject:info];
                over = YES;
                break;
            }
            if(i == [array count] - 1)
                over = YES;
        }
    } while (!over);
    
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    [userDefault setObject:[NSKeyedArchiver archivedDataWithRootObject:array] forKey:@"userInfo"];
    [userDefault synchronize];
}

//-------------------------------------appConfig------------------------------------------------

- (void)setAppConfig:(KKModelPreferenceAppConfig *)appConfig
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    if (nil == appConfig)
    {
        [userDefault removeObjectForKey:@"appConfig"];
        [userDefault synchronize];
        
		return;
    }
    
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] initWithCapacity:6];
    
    if (appConfig.obdReadInterval > 0)
        [dict setObject:[NSString stringWithFormat:@"%d",appConfig.obdReadInterval] forKey:@"obdReadInterval"];
    if (appConfig.serverReadInterval > 0)
        [dict setObject:[NSString stringWithFormat:@"%d",appConfig.serverReadInterval] forKey:@"serverReadInterval"];
    if (appConfig.mileageInformInterval > 0)
        [dict setObject:[NSString stringWithFormat:@"%d",appConfig.mileageInformInterval] forKey:@"mileageInformInterval"];
    if ([appConfig.customerServicePhone length] > 0)
        [dict setObject:[NSString stringWithFormat:@"%@",appConfig.customerServicePhone] forKey:@"customerServicePhone"];
    if (appConfig.appVehicleErrorCodeWarnIntervals > 0)
        [dict setObject:[NSString stringWithFormat:@"%d",appConfig.appVehicleErrorCodeWarnIntervals] forKey:@"appVehicleErrorCodeWarnIntervals"];
    if ([appConfig.remainOilMassWarn length] > 0)
        [dict setObject:appConfig.remainOilMassWarn forKey:@"remainOilMassWarn"];
    if ([appConfig.imageVersion length] > 0)
        [dict setObject:appConfig.imageVersion forKey:@"imageVersion"];
    
    [userDefault setObject:dict forKey:@"appConfig"];
    [userDefault synchronize];
	[dict release];
}

- (KKModelPreferenceAppConfig *)appConfig
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
	NSDictionary *dict = [userDefault objectForKey:@"appConfig"];
    
    KKModelPreferenceAppConfig *aAppConfig = [[KKModelPreferenceAppConfig alloc] init];
    aAppConfig.obdReadInterval = ([[dict objectForKey:@"obdReadInterval"] intValue] != 0) ? [[dict objectForKey:@"obdReadInterval"] intValue] : 60000;
    aAppConfig.serverReadInterval = ([[dict objectForKey:@"serverReadInterval"] intValue] != 0) ? [[dict objectForKey:@"serverReadInterval"] intValue] : 60000;
    aAppConfig.mileageInformInterval = ([[dict objectForKey:@"mileageInformInterval"] intValue] != 0) ? [[dict objectForKey:@"mileageInformInterval"] intValue] : 100;
    aAppConfig.customerServicePhone = [dict objectForKey:@"customerServicePhone"];
    aAppConfig.appVehicleErrorCodeWarnIntervals = ([[dict objectForKey:@"appVehicleErrorCodeWarnIntervals"] intValue] != 0) ? [[dict objectForKey:@"appVehicleErrorCodeWarnIntervals"] intValue] : 24;
    aAppConfig.remainOilMassWarn = ([[dict objectForKey:@"remainOilMassWarn"] length] > 0) ? [dict objectForKey:@"remainOilMassWarn"] : @"15_25";
    
    aAppConfig.imageVersion = [dict objectForKey:@"imageVersion"];
    
    return [aAppConfig autorelease];
}

- (void)setAppUserConfig:(KKModelAppUserConfig *)appUserConfig
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    if (nil == appUserConfig)
    {
        [userDefault removeObjectForKey:[NSString stringWithFormat:@"%@_appUserConfig",self.userInfo.userNo]];
        [userDefault synchronize];
        
		return;
    }
    
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] initWithCapacity:6];
    
    if(appUserConfig.first_drive_log_time)
        [dict setObject:appUserConfig.first_drive_log_time forKey:@"first_drive_log_time"];
    
    KKModelAppUserConfig *getConfig = self.appUserConfig;
    
    if(appUserConfig.oil_price)
    {
        if(getConfig.oil_price == nil || (getConfig.oil_price && [getConfig.oil_price isEqualToString:@"0"]))
            [dict setObject:appUserConfig.oil_price forKey:@"oil_price"];
        else
        {
            [dict setObject:getConfig.oil_price forKey:@"oil_price"];
        }
    }
    else
    {
        if(!getConfig.oil_price && ![getConfig.oil_price isEqualToString:@"0"])
            [dict setObject:@"0" forKey:@"oil_price"];
        else
            [dict setObject:getConfig.oil_price forKey:@"oil_price"];
    }
    
    if(appUserConfig.oil_kind)
        [dict setObject:appUserConfig.oil_kind forKey:@"oil_kind"];
    else
    {
        [dict setObject:@"" forKey:@"oil_kind"];
    }
    
    [userDefault setObject:dict forKey:[NSString stringWithFormat:@"%@_appUserConfig",self.userInfo.userNo]];
    [userDefault synchronize];
	[dict release];
}

- (KKModelAppUserConfig *)appUserConfig
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
	NSDictionary *dict = [userDefault objectForKey:[NSString stringWithFormat:@"%@_appUserConfig",self.userInfo.userNo]];
    NSLog(@"%@",self.userInfo.userNo);
    KKModelAppUserConfig *aAppUserConfig = [[KKModelAppUserConfig alloc] init];
    aAppUserConfig.first_drive_log_time = [dict objectForKey:@"first_drive_log_time"];
    aAppUserConfig.oil_price = [dict objectForKey:@"oil_price"];
    aAppUserConfig.oil_kind = [dict objectForKey:@"oil_kind"];
    
    if(aAppUserConfig.oil_price == nil || (aAppUserConfig.oil_price && aAppUserConfig.oil_price.length == 0))
        aAppUserConfig.oil_price = @"0";
    
    return [aAppUserConfig autorelease];
}


//---------------------------voicePrompt----------------------------------------------------------------

- (void)setVoiceSwitch:(KKModelPreferencePromptVoiceSwitch *)voiceSwitch
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    if (nil == voiceSwitch)
    {
        [userDefault removeObjectForKey:@"voicePrompt"];
        [userDefault synchronize];
        
		return;
    }
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] initWithCapacity:6];
    [dict setObject:[NSNumber numberWithBool:voiceSwitch.isOn] forKey:@"isOn"];
    [dict setObject:[NSNumber numberWithBool:voiceSwitch.highLevelWarn] forKey:@"highLevelWarn"];
    [dict setObject:[NSNumber numberWithBool:voiceSwitch.lowLevelWarn] forKey:@"lowLevelWarn"];
    
    [userDefault setObject:dict forKey:@"voicePrompt"];
    [userDefault synchronize];
	[dict release];
    
}

- (KKModelPreferencePromptVoiceSwitch *)voiceSwitch
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
	NSDictionary *dict = [userDefault objectForKey:@"voicePrompt"];
    
    KKModelPreferencePromptVoiceSwitch *vcSwitch = [[KKModelPreferencePromptVoiceSwitch alloc] init];
    if (dict == nil)
    {
        vcSwitch.isOn = YES;
    }
    else
    {
        vcSwitch.isOn = [[dict objectForKey:@"isOn"] boolValue];
        vcSwitch.highLevelWarn = [[dict objectForKey:@"highLevelWarn"] boolValue];
        vcSwitch.lowLevelWarn = [[dict objectForKey:@"lowLevelWarn"] boolValue];
    }
    
    return [vcSwitch autorelease];
}


//--------------------------------------------------peripheral----------------------------------------------------------------

- (void)setPeripheral:(KKModelPreferenceDefaultPeripheral *)peripheral
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    if (nil == peripheral)
    {
        [userDefault removeObjectForKey:@"peripheral"];
        [userDefault synchronize];
        
		return;
    }
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] initWithCapacity:6];
    [dict setObject:peripheral.peripheralName forKey:@"peripheralName"];
    [dict setObject:peripheral.peripheralUUID forKey:@"peripheralUUID"];
    [userDefault setObject:dict forKey:@"peripheral"];
    [userDefault synchronize];
	[dict release];
}

- (KKModelPreferenceDefaultPeripheral *)peripheral
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
	NSDictionary *dict = [userDefault objectForKey:@"peripheral"];
    
    KKModelPreferenceDefaultPeripheral *per = [[KKModelPreferenceDefaultPeripheral alloc] init];
    per.peripheralName = [dict objectForKey:@"peripheralName"];
    per.peripheralUUID = [dict objectForKey:@"peripheralUUID"];
    
    return [per autorelease];
}


//--------------------------------------------------GlobalValues-----------------------------------------------------
- (void)setGlobalValues:(KKModelPreferenceGlobalValue *)globalValues
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    if (nil == globalValues)
    {
        [userDefault removeObjectForKey:@"globalValues"];
        [userDefault synchronize];
        
		return;
    }
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] initWithCapacity:6];
    if ([globalValues.currentVehicleMile length] > 0)
        [dict setObject:globalValues.currentVehicleMile forKey:@"currentVehicleMile"];
    [dict setObject:[NSNumber numberWithBool:globalValues.isNotAutoLogin] forKey:@"isNotAutoLogin"];
    
    [userDefault setObject:dict forKey:@"globalValues"];
    [userDefault synchronize];
	[dict release];
}

- (KKModelPreferenceGlobalValue *)globalValues
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
	NSDictionary *dict = [userDefault objectForKey:@"globalValues"];
    
    KKModelPreferenceGlobalValue *values = [[KKModelPreferenceGlobalValue alloc] init];
    if ([[dict objectForKey:@"currentVehicleMile"] length] > 0)
        values.currentVehicleMile = [dict objectForKey:@"currentVehicleMile"];
    else
        values.currentVehicleMile = @"-1";
    
    values.isNotAutoLogin = [[dict objectForKey:@"isNotAutoLogin"] boolValue];
    
    return [values autorelease];
}


//-------------------------------------cityInfo-----------------------------------------------------------------

- (void)setCityInfo:(KKModelPreferenceCityInfo *)cityInfo
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    if (nil == cityInfo)
    {
        [userDefault removeObjectForKey:@"cityInfo"];
        [userDefault synchronize];
        
		return;
    }
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] initWithCapacity:6];
    if ([cityInfo.provinceName length] > 0)
        [dict setObject:cityInfo.provinceName forKey:@"provinceName"];
    if ([cityInfo.cityName length] > 0)
        [dict setObject:cityInfo.cityName forKey:@"cityName"];
    if ([cityInfo.cityCode length] > 0)
        [dict setObject:cityInfo.cityCode forKey:@"cityCode"];
    if ([cityInfo.latitude length] > 0)
        [dict setObject:cityInfo.latitude forKey:@"latitude"];
    if ([cityInfo.longitude length] > 0)
        [dict setObject:cityInfo.longitude forKey:@"longitude"];
    
    [userDefault setObject:dict forKey:@"cityInfo"];
    
    [userDefault synchronize];
	[dict release];
}

- (KKModelPreferenceCityInfo *)cityInfo
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
	NSDictionary *dict = [userDefault objectForKey:@"cityInfo"];
    
    KKModelPreferenceCityInfo *cityInfo = [[KKModelPreferenceCityInfo alloc] init];
    cityInfo.provinceName = [dict objectForKey:@"provinceName"];
    cityInfo.cityName = [dict objectForKey:@"cityName"];
    cityInfo.cityCode = [dict objectForKey:@"cityCode"];
    cityInfo.latitude = [dict objectForKey:@"latitude"];
    cityInfo.longitude = [dict objectForKey:@"longitude"];
    
    return [cityInfo autorelease];
}
@end




