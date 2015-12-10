//
//  KKPhotoBrowserVc.m
//  KKOBD
//
//  Created by zhuyc on 13-9-23.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKPhotoBrowserVc.h"
#import "ProgressIndicator.h"
#import "UIImageView+WebCache.h"
#import "KKAppDelegate.h"
#import "KKApplicationDefine.h"
#import "KKRootViewController.h"

#define WIDTH 310

@interface KKPhotoBrowserVc ()

@end

@implementation KKPhotoBrowserVc

#pragma mark -
#pragma mark Init Methods

- (id)initWithSmallUrl:(NSString *)sUrl andBigUrl:(NSString *)bUrl
{
    self = [super init];
    if (self) {
        
//        bUrl = @"http://c.hiphotos.baidu.com/album/w%3D2048/sign=532e753bb151f819f125044aee8c4bed/908fa0ec08fa513d4cab9fce3c6d55fbb2fbd999.jpg";
        
        UIImage *image = [[SDWebImageManager sharedManager] imageWithURL:[NSURL URLWithString:sUrl]];
        if (image == nil)
            image = [UIImage imageNamed:@"defaultPic.png"];
        
        UIImage *bigImage = [[SDWebImageManager sharedManager] imageWithURL:[NSURL URLWithString:bUrl]];
        _imageView=[[UIImageView alloc]init];
        if (bigImage == nil)
        {
            [_imageView setFrame:CGRectMake(0, 0, image.size.width, image.size.width)];
            [_imageView setImageDownloadDelegate:self];
            [_imageView setImageWithURL:[NSURL URLWithString:bUrl] placeholderImage:image];
        }
        else
        {
            bigImage = [self imageFitScreen:bigImage];
            [_imageView setFrame:CGRectMake(0, 0, bigImage.size.width, bigImage.size.height)];
            _imageView.image = bigImage;
        }
    }
    return self;
}

#pragma mark -
#pragma mark Public Method

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    self.view.backgroundColor = [UIColor blackColor];
    
    _scrollView = [[UIScrollView alloc] initWithFrame:[UIApplication sharedApplication].keyWindow.bounds];
    [_scrollView setContentSize:_imageView.frame.size];
    [_scrollView setPagingEnabled:NO]; 
    [_scrollView setShowsVerticalScrollIndicator:NO];
    [_scrollView setShowsHorizontalScrollIndicator:NO];
    _scrollView.maximumZoomScale=2.0;
    _scrollView.minimumZoomScale=1.0;
    [_scrollView setDelegate:self];
    [_scrollView addSubview:_imageView];
    _imageView.center = CGPointMake(0.5*_scrollView.frame.size.width, 0.5*_scrollView.frame.size.height);
    [_imageView release];
    [self.view addSubview:_scrollView];
    [_scrollView release];
    
    //增加手势识别 单击屏幕
    UITapGestureRecognizer *singleFingerOne = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleSingleFingerEvent:)];
    singleFingerOne.numberOfTouchesRequired = 1;
    singleFingerOne.numberOfTapsRequired = 1;
    [singleFingerOne setDelegate:self];
    [_scrollView addGestureRecognizer:singleFingerOne];
    [singleFingerOne release];
    
    UITapGestureRecognizer *doubleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleDoubleFingerEvent:)];
    doubleTap.numberOfTouchesRequired = 1; //手指数
    doubleTap.numberOfTapsRequired = 2; //tap次数
    [doubleTap setDelegate:self];
    [_scrollView addGestureRecognizer:doubleTap];     //imageView 增加触摸事件
    [doubleTap release];
}

- (void)viewWillAppear:(BOOL)animated
{
    [self hiddenStatusBar:YES];
}

//- (void)viewDidDisappear:(BOOL)animated
//{
//    [super viewDidDisappear:animated];
//    [self hiddenStatusBar:NO];
//    
//}

- (void)hiddenStatusBar:(BOOL)hidden
{
    if ([self respondsToSelector:@selector(setNeedsStatusBarAppearanceUpdate)]) {
        // iOS 7
        _hiddenStatusBar = hidden;
        [self performSelector:@selector(setNeedsStatusBarAppearanceUpdate)];
    } else {
        // iOS 6
        [[UIApplication sharedApplication] setStatusBarHidden:hidden withAnimation:UIStatusBarAnimationSlide];
    }
}

- (BOOL)prefersStatusBarHidden
{
    return _hiddenStatusBar;
}


-(UIImage *)imageFitScreen:(UIImage *)image
{
    UIImage *resultsImg;
    
    CGSize origImgSize = [image size];
    
    CGRect newRect;
    newRect.origin = CGPointZero;
    newRect.size = [[self view] bounds].size;
    CGRect r=newRect;
    r.size.width=WIDTH;
    newRect=r;
    //确定缩放倍数
    float ratio = MIN(newRect.size.width / origImgSize.width, newRect.size.height / origImgSize.height);
    
    //    UIGraphicsBeginImageContext(newRect.size);
    UIGraphicsBeginImageContextWithOptions(newRect.size, YES, 1.0);
    
    CGRect rect;
    rect.size.width = ratio * origImgSize.width;
    rect.size.height = ratio * origImgSize.height;
    rect.origin.x = (newRect.size.width - rect.size.width) / 2.0;
    rect.origin.y = (newRect.size.height - rect.size.height) / 2.0;
    
    [image drawInRect:rect];
    
    resultsImg = UIGraphicsGetImageFromCurrentImageContext();
    
    UIGraphicsEndImageContext();
    
    return resultsImg;
    
}

-(void)handleSingleFingerEvent:(UIGestureRecognizer *)gesture
{
    [self hiddenStatusBar:NO];
    [self dismissModalViewControllerAnimated:YES];
}

-(void)handleDoubleFingerEvent:(UIGestureRecognizer *)gesture{
    float newScale=0 ;
    if (_scrollView.zoomScale<=1.0) {
        newScale=_scrollView.zoomScale * 2.0;
        CGRect zoomRect = [self zoomRectForScale:newScale withCenter:[gesture locationInView:gesture.view]];
        [_scrollView zoomToRect:zoomRect animated:YES];
    }
    else
    {
        [_scrollView setZoomScale:1.0 animated:YES];
    }
}

- (CGRect)zoomRectForScale:(float)scale withCenter:(CGPoint)center
{
    CGRect zoomRect;
    zoomRect.size.height = self.view.frame.size.height / scale;
    zoomRect.size.width  = self.view.frame.size.width  / scale;
    zoomRect.origin.x = center.x - (zoomRect.size.width  / 2.0);
    zoomRect.origin.y = center.y - (zoomRect.size.height / 2.0);
    return zoomRect;
}


#pragma mark - UIScrollViewDelegate

- (UIView *)viewForZoomingInScrollView:(UIScrollView *)scrollView
{
    return _imageView;
}


- (void)scrollViewDidEndZooming:(UIScrollView *)scrollView withView:(UIView *)view atScale:(float)scale
{
    [scrollView setZoomScale:scale animated:YES];
}

#pragma mark -
#pragma mark
- (void)SDWebimageDidFinished:(UIImageView*)imageView
{
    UIImage *image = [self imageFitScreen:imageView.image];
    CGSize size = image.size;
    [_imageView setFrame:CGRectMake(0, 0, size.width, size.height)];
    _scrollView.contentSize = size;
    _imageView.image = image;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    _scrollView = nil;
    _imageView = nil;
}

- (void)dealloc
{
    _scrollView = nil;
    _imageView = nil;
    [super dealloc];
}
@end
