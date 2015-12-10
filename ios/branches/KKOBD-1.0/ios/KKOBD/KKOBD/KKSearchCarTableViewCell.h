//
//  KKSearchCarTableViewCell.h
//  KKOBD
//
//  Created by zhuyc on 13-8-19.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface KKSearchCarTableViewCell : UITableViewCell
{
    UIImageView         *_activityImv;
    UIImageView         *_linkedImv;
    UILabel             *_deviceNameLb;
}
@property (nonatomic ,assign) BOOL isAnimating;
@property (nonatomic ,assign) BOOL isLinked;

- (void)setDeviceName:(NSString *)name;
- (void)startAnimating;
- (void)stopAnimation;
- (void)setDeviceLinked:(BOOL)link;

@end
