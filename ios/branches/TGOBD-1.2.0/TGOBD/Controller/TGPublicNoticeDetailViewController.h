//
//  TGPublicNoticeDetailViewController.h
//  TGOBD
//
//  Created by James Yu on 14-4-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"

@interface TGPublicNoticeDetailViewController : TGBaseViewController <UIWebViewDelegate>

@property (nonatomic, strong) UIWebView *webView;
@property (nonatomic, assign) long long advertId;

@end
