//
//  KKPhotoBrowserVc.h
//  KKOBD
//
//  Created by zhuyc on 13-9-23.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SDWebImageManager.h"
@interface KKPhotoBrowserVc : UIViewController<UIGestureRecognizerDelegate,UIScrollViewDelegate,SDWebImageDownloaderDelegate,SDWebImageManagerDelegate>
{
    UIScrollView        *_scrollView;
    UIImageView         *_imageView;
    BOOL                 _hiddenStatusBar;
}
- (id)initWithSmallUrl:(NSString *)sUrl andBigUrl:(NSString *)bUrl;

@end
