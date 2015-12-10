//
//  KKReservationServiceViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-7.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKReservationCellView.h"

@interface KKReservationServiceViewController : UIViewController<UITableViewDataSource, UITableViewDelegate
,KKReservationCellViewDelegate>
{
    UITableView         *_mainTableView;
    NSMutableArray      *_cNameArr;
    NSMutableArray      *_eNameArr;
}

@end
