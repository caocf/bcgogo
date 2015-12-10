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
    NSMutableArray              *_dataArray1;
    NSMutableArray              *_dataArray2;
    KKServiceSegmentControl     *_segmentControl;
    
    NSInteger                   _page0;     //未完成
    NSInteger                   _page1;     //已完成
    NSInteger                   _size;
    
    NSInteger                   _selectedIndex; //0:1 //未完成：已完成
    
    BOOL                        _isLoading;
    BOOL                        _enableRefresh0;
    BOOL                        _enableRefresh1;
}
@end
