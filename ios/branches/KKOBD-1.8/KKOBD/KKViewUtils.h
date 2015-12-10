//
//  KKViewUtils.h
//  KaiKai
//
//  Created by mazhiwei on 11-9-7.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface KKViewUtils : NSObject
+ (UIBarButtonItem*)createNavigationBarButtonItem:(UIImage*)aImageName  bgImage:(UIImage*)bgImage
                                            target:(id)aTarget action:(SEL)aAction;
+ (UIBarButtonItem*)createNavigationBarButtonItemWithTitle:(NSString*)title bgImage:(UIImage*)bgImage
                                            target:(id)aTarget action:(SEL)aAction;
@end

@interface UIView  (findsubview)
- (UIView*)findSubviewByTag:(NSInteger)tag;
- (UIView*)findSubview:(id)target withSel:(SEL)filter withObj:(id)obj;
- (void) setAllBackgroundColor:(UIColor *)color;
- (void) drawBorder:(CGFloat)thickness color:(UIColor*)color;
@end

@interface UIBarButtonItem (KKAdditional)
- (id)initWithCustomView:(UIImage*)image title:(NSString*)title target:(id)target action:(SEL)action;
@end

@interface UINavigationBar (KKAdditional)
//- (id)initWithCoder:(NSCoder *)aDecoder;
- (void)addIconImageView;
- (void)addBgImageView;
- (void)sendBgImageViewToBack;
- (void)KKTitleStyleInit;
- (void)setIconHiddend;
@end

@interface UIAlertView (KKAdditional)
- (id)initWithTitle:(NSString *)title message:(NSString *)message userInfo:(id)userInfo delegate:(id /*<UIAlertViewDelegate>*/)delegate 
  cancelButtonTitle:(NSString *)cancelButtonTitle otherButtonTitles:(NSString *)otherButtonTitles, ...;

- (id)userInfo;

@end

@interface UIViewController (KKAdditional)

- (UINavigationBar *)createCustomNaviBar;
- (void)dismissModalViewControllerAndChild:(BOOL)child animated:(BOOL)animated;
@end


