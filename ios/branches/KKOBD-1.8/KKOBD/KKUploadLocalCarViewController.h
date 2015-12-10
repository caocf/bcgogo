//
//  KKUploadLocalCarViewController.h
//  KKOBD
//
//  Created by Jiahai on 14-1-9.
//  Copyright (c) 2014年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKProtocolEngineDelegate.h"

@interface KKUploadLocalCarViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,KKProtocolEngineDelegate>
{
    NSMutableArray  *selectedVehicles;
    
    UITableView     *_tableView;
    
//    int             _uploadSuccessCount;     //上传成功的数量统计
//    int             _uploadIndex;
}

@property(nonatomic, retain) NSArray *localVehicleList;
@end
