//
//  TGOilStationViewController.h
//  TGOBD
//
//  Created by Jiahai on 14-3-5.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
@class TGModelOilStationListRsp;

@interface TGOilStationViewController : TGBaseViewController<UITableViewDataSource,UITableViewDelegate>
{
    UITableView     *_tableView;
}

@property (nonatomic, strong)   TGModelOilStationListRsp    *oilStationListRsp;
@end
