//
//  KKMsgPlaySound.h
//  KKOBD
//
//  Created by zhuyc on 13-9-26.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AudioToolbox/AudioToolbox.h> 

@interface KKMsgPlaySound : NSObject
{
    SystemSoundID soundID;
}


-(id)initForPlayingVibrate;


-(id)initForPlayingSystemSoundEffectWith:(NSString *)resourceName ofType:(NSString *)type;


-(id)initForPlayingSoundEffectWith:(NSString *)filename;


-(void)play;

@end
