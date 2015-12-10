//
//  KKGradeView.h
//  KKOBD
//
//  Created by zhuyc on 13-8-8.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface KKGradeView : UIView
{
    UILabel     *_valuateLabel;
    UIView      *_scoreView;
}
@property (nonatomic ,assign) NSInteger     rank;

@end
