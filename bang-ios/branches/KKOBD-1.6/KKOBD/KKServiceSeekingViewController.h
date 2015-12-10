//
//  KKServiceSeekingViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-20.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKServiceSegmentControl.h"
#import "KKProtocolEngineDelegate.h"

@interface KKServiceSeekingViewController : UIViewController<KKServiceSegmentControlDelegate,UITableViewDataSource,UITableViewDelegate,KKProtocolEngineDelegate,UIScrollViewDelegate>
{
    UITableView                 *_mainTableView;
    
    KKServiceSegmentControl     *_segmentControl;
    
    NSInteger                   _page;
    
    NSInteger                   _size;
    
    BOOL                        _isLoading;
    BOOL                        _enableRefresh;
}
@end
