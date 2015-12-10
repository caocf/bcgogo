//
//  KKSecondViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-5.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKMessagePromptCell.h"
#import "KKProtocolEngineDelegate.h"

@interface KKSecondViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,KKMessagePromptCellDelegate,KKProtocolEngineDelegate>
{
    UITableView         *_mainTableView;
    NSMutableArray      *_dataArray;
    
    BOOL                _clearEnable;
}

@end
