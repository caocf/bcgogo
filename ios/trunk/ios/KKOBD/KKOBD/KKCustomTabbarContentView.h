//
//  KKCustomTabbarContentView.h
//  KaiKai
//
//  Created by mazhiwei on 11-9-27.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@class KKCustomTabbarItem;

@protocol KKTabBarItemDelegate <NSObject>
@required
- (void)didSelectedItem:(KKCustomTabbarItem*)item;

@end

@interface KKCustomTabbarItem : UIView {
    UIImageView     *_itemImageView;
    UILabel         *_titleLabel;
    
	UIImageView     *_badgeImageView;
	UILabel         *_badgeValueLabel;
	NSInteger        _badgeValue;
    
	id <KKTabBarItemDelegate> _delegate;
}
@property (nonatomic, retain) NSString  *title;
@property (nonatomic, retain) UIColor   *selectedColor;
@property (nonatomic, retain) UIImage   *selectedImage;
@property (nonatomic, retain) UIColor   *noselectedColor;
@property (nonatomic, retain) UIImage   *noselectedImage;
@property (nonatomic, retain) UIImage   *selectedBgImage;
@property (nonatomic, assign) id<KKTabBarItemDelegate> delegate;

- (void)setContentViews;
- (void)setSelected:(BOOL)selected;
- (void)setBadgeValue:(NSInteger)value;

@end

// ============================================================================
@interface KKCustomTabbarContentView : UIView
{
    NSMutableArray      *items;
    NSInteger            selectedItemIndex;
    
    UIImageView         *_bgImageView;
    UIImageView         *_selectedBgImageView;
}

- (void)addItem:(KKCustomTabbarItem*)item;
- (void)setItemSelected:(NSInteger)index;
- (NSArray*)getItems;
- (void)setBadgeValue:(NSInteger)count andIndex:(NSInteger)index;
- (void)setbgImageView:(UIImage *)image;
- (void)setSelectedBgImageViewWithImage:(UIImage *)image andWidth:(float)width;

@end

