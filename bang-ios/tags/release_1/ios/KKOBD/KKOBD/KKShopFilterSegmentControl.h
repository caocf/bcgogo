//
//  KKShopFilterSegmentControl.h
//  KKOBD
//
//  Created by zhuyc on 13-8-22.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol KKShopFilterSegmentControlDelegate;

@interface KKShopFilterItem : UIButton
{
    UIImageView     *_arrowImv;
}
@property (nonatomic ,assign)NSInteger  itemId;
@property (nonatomic ,assign)NSInteger  selectedIndex;
@property (nonatomic ,retain)UILabel    *textLabel;

- (id)initWithFrame:(CGRect)frame hideArrowImageView:(BOOL)hidden;

@end



@interface KKShopFilterSegmentControl : UIView
@property (nonatomic ,assign)id<KKShopFilterSegmentControlDelegate> delegate;

- (void)setShopFilterItemTitle:(NSString *)title WithIndex:(NSInteger)index;
- (void)setShopFilterItemBackGroundImage:(UIImage *)image WithIndex:(NSInteger)index;

@end


@protocol KKShopFilterSegmentControlDelegate
@optional
- (void)shopFilterSegmentControlClicked:(id)sender;

@end