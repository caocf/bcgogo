//
//  TGNavigationBar.h
//  TGYF
//
//  Created by James Yu on 14-5-19.
//  Copyright (c) 2014年 james. All rights reserved.
//

#import <UIKit/UIKit.h>
/**
 *  自定义导航栏，以后根据实际需要继续添加方法，在baseViewController 里面进行调用，包括左右按钮
 */
@interface TGNavigationBar : UIView

@property (nonatomic, strong) UIView *bagView;
@property (nonatomic, strong) UILabel *titleLbl;

- (void)setNavigationBarBagColor:(UIColor *)color;

- (void)setNavigationBarTitle:(NSString *)title;

- (void)addNavigationBarLeftButton:(UIButton *)button;

- (void)addNavigationBarRightButton:(UIButton *)button;

@end
