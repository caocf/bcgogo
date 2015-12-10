/*
 * This file is part of the SDWebImage package.
 * (c) Olivier Poitrey <rs@dailymotion.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

#import "UIImageView+WebCache.h"
#import <objc/runtime.h>

static const NSString *webImageDownloadKey = @"webImageDelegate";

@implementation UIImageView (WebCache)

- (void)setImageWithURL:(NSURL *)url
{
    [self setImageWithURL:url placeholderImage:nil];
}

- (void)setImageWithURL:(NSURL *)url placeholderImage:(UIImage *)placeholder
{
    [self setImageWithURL:url placeholderImage:placeholder options:0];
}

- (void)setImageWithURL:(NSURL *)url placeholderImage:(UIImage *)placeholder options:(SDWebImageOptions)options
{
    SDWebImageManager *manager = [SDWebImageManager sharedManager];

    // Remove in progress downloader from queue
    [manager cancelForDelegate:self];

    self.image = placeholder;

    if (url)
    {
        UIActivityIndicatorView *actv = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhite];
        actv.hidesWhenStopped = YES;
        actv.tag = 10013;
        CGRect rt = self.frame;
        actv.center = CGPointMake(0.5*rt.size.width, 0.5*rt.size.height);
        [self addSubview:actv];
        [actv release];
        [actv startAnimating];
        
        [manager downloadWithURL:url delegate:self options:options];
    }
}

- (void)cancelCurrentImageLoad
{
    for (UIView *sub  in [self subviews]) {
        if (sub.tag == 10013) {
            UIActivityIndicatorView *actv = (UIActivityIndicatorView*)sub;
            [actv stopAnimating];
        }
    }
    
    [[SDWebImageManager sharedManager] cancelForDelegate:self];
}

- (void)webImageManager:(SDWebImageManager *)imageManager didFinishWithImage:(UIImage *)image
{
    self.image = image;
    for (UIView *sub  in [self subviews]) {
        if (sub.tag == 10013) {
            UIActivityIndicatorView *actv = (UIActivityIndicatorView*)sub;
            [actv stopAnimating];
        }
        if ([sub isKindOfClass:[UILabel class]])
             [sub removeFromSuperview];
    }
    NSObject *obj = (NSObject*)objc_getAssociatedObject(self, webImageDownloadKey);
    if ([obj respondsToSelector:@selector(SDWebimageDidFinished:)])
        [obj performSelector:@selector(SDWebimageDidFinished:) withObject:self];
}

- (void)webImageManager:(SDWebImageManager *)imageManager didFailWithError:(NSError *)error
{
    for (UIView *sub  in [self subviews]) {
        if (sub.tag == 10013) {
            UIActivityIndicatorView *actv = (UIActivityIndicatorView*)sub;
            [actv stopAnimating];
        }
    }
//    if (error.code != 250) { // cancel
//        if (self.image == nil)
//        {
//            CGRect rt = self.bounds;
//            UILabel *label = [[UILabel alloc] initWithFrame:rt];
//        
//            label.text = @"sorry, couldn't find";
//            label.textColor = [UIColor blueColor];
//            label.backgroundColor = [UIColor clearColor];
//            label.textAlignment = UITextAlignmentCenter;
//            label.font = [UIFont boldSystemFontOfSize:14.0f];
//            [self addSubview:label];
//            [label release];
//        }
//    }
    
    NSObject *obj = (NSObject*)objc_getAssociatedObject(self, webImageDownloadKey);
    if ([obj respondsToSelector:@selector(SDWebimageDidError:)])
        [obj performSelector:@selector(SDWebimageDidError:) withObject:self];
}

- (void)setImageDownloadDelegate:(id)delegate
{
    objc_setAssociatedObject(self, webImageDownloadKey, delegate, OBJC_ASSOCIATION_RETAIN);
}

@end
