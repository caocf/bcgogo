//
//  global.m
//  Better
//
//  Created by apple on 10-3-23.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#ifndef _KK_GLOBAL_
#define _KK_GLOBAL_
#import "KKGlobal.h"
#undef _KK_GLOBAL_
#endif

NSString *KKPlatformName[PLATFORM_COUNT] = {
	@"WIN",		// Windows
	@"S60",		// Symbian Series60
	@"UIQ",		// Symbian UIQ
	@"PPC",		// PocketPC
	@"SPN",		// Smartphone
	@"BRW",		// BREW
	@"PLM",		// PALM OS
	@"J2M",		// J2ME
	@"IFN",		// IPhone
	@"AND",		// Android
};

// global init, should be called in AppDelegate init
void KKGlobalInit(void)
{
	if (KKNumberResultEnd) [KKNumberResultEnd release];
	KKNumberResultEnd =  [[NSNumber alloc]initWithInteger:0];		// eKKResultEnd
	
	if (KKNumberResultGoOn) [KKNumberResultGoOn release];
	KKNumberResultGoOn = [[NSNumber alloc]initWithInteger:1];		// eKKResultGoOn
	
}


