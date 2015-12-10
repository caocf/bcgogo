//
//  global.h
//  Better
//
//  Created by apple on 10-3-23.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


// ================================================================================================
//  type define
// ================================================================================================
typedef enum {
	WIN = 0,	// Windows
	S60,		// Symbian Series60
	UIQ,		// Symbian UIQ
	PPC,		// PocketPC
	SPN,		// Smartphone
	BRW,		// BREW
	PLM,		// PALM OS
	J2M,		// J2ME
	IFN,		// IPhone
	AND,		// Android
	
	PLATFORM_UNKNOWN, // unknown
	PLATFORM_COUNT = PLATFORM_UNKNOWN
} _KKPlatform;
typedef NSUInteger KKPlatform;
	
//#define KK_TelAESPrivateKey			@"2012isnotthelast"
//#define KK_DBName					@"dbKaiKai.sqlite"

// belows should be modified before release to distribution
#define KK_DataKind                 @"OFFICIAL"         //TEST,OFFICIAL（表示正式 测试 ）（guest 必须）

#define KK_AppName					@"OBD"
#define KK_Version                  [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"]
#define KK_Build					[[[NSBundle mainBundle] infoDictionary] objectForKey:(NSString *)kCFBundleVersionKey]
#define KK_Model					@"3G"

// ================================================================================================
//  global vars define
// ================================================================================================

#ifndef _KK_GLOBAL_
// For Protocol api handle response, refer to KKProtocolEngineDelegateResult
extern NSNumber *KKNumberResultEnd;
extern NSNumber *KKNumberResultGoOn;

extern NSString* KKPlatformName[PLATFORM_COUNT];

#else

NSNumber *KKNumberResultEnd = nil;
NSNumber *KKNumberResultGoOn = nil;

NSString *KKPlatformName[PLATFORM_COUNT];

#endif


// ================================================================================================
//  global methods define
// ================================================================================================
// global init, should be called in AppDelegate init
#ifndef _KK_GLOBAL_
void KKGlobalInit(void);

#else
extern void KKGlobalInit(void);

#endif


#define KKLOG_FUN_ENTER NSLog(@"%@ %@ %@",[[NSString stringWithUTF8String:__FILE__] lastPathComponent], [NSString stringWithUTF8String:__FUNCTION__],@"Enter")
#define KKLOG_FUN_LEAVE NSLog(@"%@ %@ %@",[[NSString stringWithUTF8String:__FILE__] lastPathComponent], [NSString stringWithUTF8String:__FUNCTION__],@"Leave")




