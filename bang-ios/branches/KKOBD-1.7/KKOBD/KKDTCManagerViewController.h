//
//  KKDTCManagerViewController.h
//  KKOBD
//
//  Created by Jiahai on 14-2-7.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKServiceSegmentControl.h"
#import "KKProtocolEngineDelegate.h"
#import "BGDTCTableViewCell.h"
#import "EGORefreshTableHeaderView.h"
@class KKCustomAlertView;

@interface KKDTCManagerViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,KKServiceSegmentControlDelegate,KKProtocolEngineDelegate,BGDTCTableViewCellDelegate,EGORefreshTableHeaderDelegate>
{
    NSMutableArray                  *_dataArray;
    
    UIButton                        *_createOrderBtn;
    
    KKServiceSegmentControl         *_segmentControl;
    UITableView                     *_mainTableView;
    
    EGORefreshTableHeaderView       *_refreshHeaderView;
    
    UIImageView                     *_noDataImageView;

    BOOL                            _isLoading;                 //是否正在请求历史故障
    BOOL                            _enableRefresh;             //判断是否是最后一页，是的话 ＝ NO
    
    BOOL                            _reGetFlag;                 //是否是重新获取故障码，是的话，收到数据后清空之前的
}

@end
