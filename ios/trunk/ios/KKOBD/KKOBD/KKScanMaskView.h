//
//  KKScanMaskView.h
//  KKOBD
//
//  Created by zhuyc on 13-8-16.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface KKScanMaskView : UIView
{
    UIImage     *_holeImage;
    UIColor     *_backColor;
    float        _alpha;
}
- (id)initWithFrame:(CGRect)frame WithHoleImage:(UIImage *)image;

@end
