//
//  KKSearchCarModelViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-28.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKProtocolEngineDelegate.h"

@protocol KKSearchCarModelDelegate;

@interface KKSearchCarModelViewController : UIViewController<UITextFieldDelegate,UITableViewDataSource,UITableViewDelegate,KKProtocolEngineDelegate>
{
    NSMutableArray      *_dataArray;
    UITableView         *_mainTableView;
    UITextField         *_textField;
    
}
@property (nonatomic ,assign)BOOL haveTabbar;
@property (nonatomic ,assign)BOOL isBrand;
@property (nonatomic ,copy)NSString *brandName;
@property (nonatomic ,copy)NSString *brandID;
@property (nonatomic ,assign)id<KKSearchCarModelDelegate> delegate;

@end

@protocol KKSearchCarModelDelegate
@optional
- (void)KKSearchCarModelViewDidSelected:(id)obj isBrand:(BOOL)isbrand;

@end