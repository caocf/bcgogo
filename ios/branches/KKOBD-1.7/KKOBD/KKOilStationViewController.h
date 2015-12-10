//
//  KKOilStationViewController.h
//  KKOBD
//
//  Created by Jiahai on 13-12-5.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKProtocolEngineDelegate.h"
#import "KKServiceSegmentControl.h"
@class KKModelOilStationListRsp;

@interface KKOilStationViewController : UIViewController<KKProtocolEngineDelegate,KKServiceSegmentControlDelegate,UITableViewDataSource,UITableViewDelegate>
{
    UITableView *_tableView;
    KKServiceSegmentControl *_segmentControl;
    
    BOOL                    _isEnd;             //数据全部加载结束
    BOOL                    _isLoading;
}

@property (nonatomic,retain) KKModelOilStationListRsp *oilStationListRsp;
@end
