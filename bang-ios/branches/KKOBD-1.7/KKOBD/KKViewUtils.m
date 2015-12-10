//
//  KKViewUtils.m
//  KaiKai
//
//  Created by mazhiwei on 11-9-7.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "KKViewUtils.h"
#import <objc/runtime.h>
#import "KKApplicationDefine.h"

#pragma mark -
#pragma mark KKViewUtils
@implementation KKViewUtils
+ (UIBarButtonItem*)createNavigationBarButtonItem:(UIImage*)aImage bgImage:(UIImage*)bgImage
                                           target:(id)aTarget action:(SEL)aAction
{    
    UIButton* button = [UIButton buttonWithType:UIButtonTypeCustom];
	UIImage *img = aImage?aImage:bgImage;
    CGSize size = img.size;
    if (currentSystemVersion >= 7.0)
        [button setFrame:CGRectMake(0, 0, size.width, size.height)];
    else
        [button setFrame:CGRectMake(0, 0, 44, 44)];
    [button setImage:img forState:UIControlStateNormal];
    [button addTarget:aTarget action:aAction forControlEvents:UIControlEventTouchUpInside];
    
    [button setExclusiveTouch:YES];
    
    UIBarButtonItem* item = [[UIBarButtonItem alloc] initWithCustomView:button];

    return [item autorelease];
}

+ (UIBarButtonItem*)createNavigationBarButtonItemWithTitle:(NSString*)title bgImage:(UIImage*)bgImage
                                           target:(id)aTarget action:(SEL)aAction
{    
    UIButton* button = [UIButton buttonWithType:UIButtonTypeCustom];
    button.tag = 2012;
    UIFont* font = [UIFont systemFontOfSize:13.0f];
    button.titleLabel.font = font;
    CGSize size = [title sizeWithFont:font];
    CGFloat buttonWidth = 0, buttonHeight = 30.f;
    if (size.width < 43)
    {
		if ([title length]==0 && bgImage.size.width > 0) {
			buttonHeight = bgImage.size.height;
			buttonWidth = bgImage.size.width;
		}
		else
			buttonWidth = 43;
    }
    else 
    {
        buttonWidth = size.width + 10;
    }
    button.frame = CGRectMake(0,0,buttonWidth,buttonHeight);
    
    [button setTitle:title forState:UIControlStateNormal];
    [button setBackgroundImage:bgImage forState:UIControlStateNormal];
    [button addTarget:aTarget action:aAction forControlEvents:UIControlEventTouchUpInside];
    
    [button setExclusiveTouch:YES];
    
    UIBarButtonItem* item = [[UIBarButtonItem alloc] initWithCustomView:button];
    item.target = aTarget;
    item.action = aAction;
    return [item autorelease];
}
@end

#pragma mark -
#pragma mark UIView
@implementation UIView  (findsubview)
- (UIView*)findSubviewByTag:(NSInteger)tag
{
    for (UIView* view in self.subviews)
    {
        if (view.tag == tag) 
        {
            return view;
        }
    }
    return nil;
}

- (UIView*)findSubview:(id)target withSel:(SEL)filter withObj:(id)obj
{
    if ([self.subviews count] > 0) 
    {
        for (id sub in self.subviews) 
        {
            UIView* subview = (UIView*)sub;
            if ([target respondsToSelector:filter] && [target performSelector:filter withObject:subview withObject:obj]) 
            {
                return subview;
            }
            else 
            {
                UIView* view = [subview findSubview:target withSel:filter withObj:obj];
                if (view) 
                {
                    return view;
                }
            }
        }
    }
    return nil;
}

- (void) setAllBackgroundColor:(UIColor *)color
{
    self.backgroundColor = color;
    NSArray* subArr = self.subviews;
    if ([subArr count] > 0) 
    {
        for (UIView* sub in subArr)
        {
            [sub setAllBackgroundColor:color];
        }
    }
}

- (void) drawBorder:(CGFloat)thickness color:(UIColor*)color
{
    CGContextRef ctx = UIGraphicsGetCurrentContext();
    
    //设置颜色，仅填充4条边
    CGContextSetStrokeColorWithColor(ctx, [color CGColor]);
    
    //设置线宽为
    CGContextSetLineWidth(ctx, thickness);
    
    //设置长方形4个顶点
    CGPoint origin = CGPointZero;
    CGSize  size = self.frame.size;
    CGPoint poins[] = {origin,CGPointMake(origin.x + size.width, origin.y),
        CGPointMake(origin.x + size.width, origin.y+size.height),CGPointMake(origin.x, origin.y+size.height)};
    CGContextAddLines(ctx,poins,4);
    CGContextClosePath(ctx);
    CGContextStrokePath(ctx);
}
@end



#pragma mark -
#pragma mark UIBarButtonItem (KKAdditional)
@implementation UIBarButtonItem (KKAdditional)

- (id)initWithCustomView:(UIImage*)image title:(NSString*)title target:(id)target action:(SEL)action
{
    UIButton* customView = [UIButton buttonWithType:UIButtonTypeCustom];
    customView.frame = CGRectMake(0,0,49,49);
    
    UIImageView* imageView = [[UIImageView alloc] initWithImage:image];
    imageView.center = CGPointMake(30, 20);
    [customView addSubview:imageView];
    [imageView release];
    
    UILabel* label = [[UILabel alloc] initWithFrame:CGRectMake(0, 30, 60, 20)];
    label.font = [UIFont systemFontOfSize:10.0f];
    label.backgroundColor = [UIColor clearColor];
    label.text = title;
    label.textColor = KKCOLOR_CCCCCC;
    label.textAlignment = UITextAlignmentCenter;
    [customView addSubview:label];
    [label release];
    
    [customView addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
    
    return [self initWithCustomView:customView];
}
@end

#pragma mark -
#pragma mark UINavigationBar
@implementation UINavigationBar (KKAdditional)
- (void)addBgImageView
{    
	if ([self respondsToSelector:@selector(setBackgroundImage: forBarMetrics:)]) {   // for ios5 and ios6
		[self setBackgroundImage:[KKImageByName(KKNavgationBarbgImage) stretchableImageWithLeftCapWidth:0 topCapHeight:0] forBarMetrics:UIBarMetricsDefault];
		return;
	}
	
        //添加背景
    CGSize navbarsize = self.frame.size;
    UIImageView* bgImgView = [[UIImageView alloc] initWithFrame:CGRectMake(0,0,navbarsize.width,navbarsize.height)];
    bgImgView.tag = 7086;
    bgImgView.image = [KKImageByName(KKNavgationBarbgImage) stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    [self addSubview:bgImgView];
    [bgImgView release];
    
    [self sendSubviewToBack:bgImgView];
    
}

- (void)addIconImageView
{
    for (UIView* subview in self.subviews)
    {
        if (subview.tag == 7085)
        {
            subview.hidden = NO;
            return;
        }
    }
    UIImageView *iconImageView = [[UIImageView alloc] initWithImage:KKImageByName(nil)];
    [iconImageView setFrame:CGRectMake(0, 0, 66, 22)];
    iconImageView.center = CGPointMake(160, 22);
    iconImageView.tag = 7085;
    [self addSubview:iconImageView];
    [iconImageView release];
}

- (void)sendBgImageViewToBack
{
    for (UIView* view in self.subviews)
    {
        if (view.tag == 7086)
        {
            [self sendSubviewToBack:view];
			NSLog(@"background send to back");
            break;
        }
    }
}

- (void)setIconHiddend
{
    for (UIView* subview in self.subviews)
    {
        if (subview.tag == 7085)
        {
            subview.hidden = YES;
            return;
        }
    }
}

- (void)KKTitleStyleInit
{
	UILabel *titleLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0, 0, 200, 30)] autorelease];
	titleLabel.center = CGPointMake(320/2, 44/2);
	titleLabel.textColor = [UIColor whiteColor];
	titleLabel.font = [UIFont boldSystemFontOfSize:20.0f];
	titleLabel.backgroundColor = [UIColor clearColor];
	titleLabel.textAlignment = UITextAlignmentCenter;
	self.topItem.titleView = titleLabel;
}
@end

#pragma mark -
#pragma mark UIAlertView
@implementation UIAlertView (KKAdditional)

- (id)initWithTitle:(NSString *)title message:(NSString *)message userInfo:(id)userInfo delegate:(id /*<UIAlertViewDelegate>*/)delegate 
  cancelButtonTitle:(NSString *)cancelButtonTitle otherButtonTitles:(NSString *)otherButtonTitles, ...
{
	self = [self initWithTitle:title message:message delegate:delegate cancelButtonTitle:cancelButtonTitle otherButtonTitles:otherButtonTitles, nil];
	if (nil == self)
		return self;
		
	objc_setAssociatedObject(self, @"userInfo", userInfo, OBJC_ASSOCIATION_COPY);
	return self;
}

- (id)userInfo
{
	return objc_getAssociatedObject(self, @"userInfo");
}

@end


#pragma mark -
#pragma mark UIViewController
@implementation UIViewController (KKAdditional)

- (UINavigationBar *)createCustomNaviBar
{
	UINavigationBar *navigationBar = [[UINavigationBar alloc] initWithFrame:CGRectMake(0, 0, 320, 44)];
	navigationBar.barStyle = UIBarStyleBlackOpaque;
	navigationBar.tintColor = [UIColor blackColor];
	[navigationBar addBgImageView];
	UINavigationItem *navigationItem = [[UINavigationItem alloc] initWithTitle:nil];
	[navigationBar setItems:[NSArray arrayWithObject:navigationItem]];
	[navigationItem release];
	[self.view addSubview:navigationBar];
	[navigationBar KKTitleStyleInit];
	return [navigationBar autorelease];
}

- (void)dismissModalViewControllerAndChild:(BOOL)child animated:(BOOL)animated
{
	if (child) {
		if (self.modalViewController)
			[self.modalViewController dismissModalViewControllerAndChild:child animated:animated];
	}
	[self dismissModalViewControllerAnimated:animated];
}
@end




