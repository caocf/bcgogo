//
//  KKRootViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-5.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKCustomTabbarContentView.h"

@interface KKRootViewController : UITabBarController<KKTabBarItemDelegate>
{
    KKCustomTabbarContentView       *_tabbarContentView;
}
@property (nonatomic ,retain)KKCustomTabbarContentView *tabbarContentView;

- (void)popToRootViewWithIndex:(NSInteger)index;
@end
