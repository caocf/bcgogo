//
//  TGButton.h
//  TGOBD
//
//  Created by James Yu on 14-3-22.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TGButton : UIButton

{
    UILabel *_titleTip;
    UIImageView *_titleImageView;
}

//通知需要自己进行管理，特别是重用的时候
- (id)initWithFrame:(CGRect)frame tipsFrame:(CGRect)tipsFrame notificationName:(NSString *)notificationName;

@end
