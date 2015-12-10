//
//  KKReviewViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-16.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKRatingView.h"
#import "GCPlaceholderTextView.h"
#import "KKProtocolEngineDelegate.h"

@interface KKReviewViewController : UIViewController<UITextViewDelegate,KKProtocolEngineDelegate>
{
    KKRatingView                *_rankView;
    GCPlaceholderTextView       *_textView;
}
@property (nonatomic ,retain)NSString   *messageId;
@property (nonatomic ,retain)NSString   *orderId;
@end
