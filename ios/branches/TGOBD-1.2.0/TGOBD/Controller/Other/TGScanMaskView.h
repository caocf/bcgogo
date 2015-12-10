//
//  TGScanMaskView.h
//  TGOBD
//
//  Created by Jiahai on 14-3-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TGScanMaskView : UIView
{
    UIImage     *_holeImage;
    UIColor     *_backColor;
    float        _alpha;
}
- (id)initWithFrame:(CGRect)frame WithHoleImage:(UIImage *)image;
@end
