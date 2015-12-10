//
//  TGDTCManagerViewController.h
//  TGOBD
//
//  Created by Jiahai on 14-3-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
#import "EGORefreshTableHeaderView.h"
#import "TGDTCTableCell.h"
#import "TGCustomSegmentView.h"

@interface TGDTCManagerViewController : TGBaseViewController <UITableViewDataSource,UITableViewDelegate,EGORefreshTableHeaderDelegate,TGDTCTableViewCellDelegate,TGCustomSegmentViewDelegate>
{
    UIButton                        *_createOrderBtn;
    UITableView                     *_mainTableView;
    UITableView                     *_historyTableView;
    EGORefreshTableHeaderView       *_refreshHeaderView;
    EGORefreshTableHeaderView       *_historyRefreshHeaderView;
    
    UIImageView                     *_noDataImageView;
    UIImageView                     *_noDataHistoryImageView;
    
    BOOL                            _isLoading;
    BOOL                            _enableRefresh;
    BOOL                            _enableRefreshHistory;
    BOOL                            _reGetFlag;
    BOOL                            _reGetFlagHistory;
    NSInteger                       _currentSegmentIndex;
}
@end
