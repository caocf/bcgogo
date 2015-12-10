//
//  TGCustomDropDownListView.h
//  TGOBD
//
//  Created by James Yu on 14-3-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol TGCustomDropDownListViewDelegate;

@interface TGCustomDropDownListView : UIView <UITableViewDataSource, UITableViewDelegate>

@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) UIView *view;
@property (nonatomic, strong) NSArray *dataSource;
@property (nonatomic, assign) id <TGCustomDropDownListViewDelegate> delegate;

@end

@protocol TGCustomDropDownListViewDelegate <NSObject>

- (void)TGCustomDropDownListSelected:(NSString *)selectedValue;

@end