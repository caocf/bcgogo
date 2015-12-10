//
//  KKAutoTableView.h
//  KKOBD
//
//  Created by zhuyc on 13-9-12.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
@protocol KKAutoTableViewDelegate;

@interface KKAutoTableView : UIView<UITableViewDataSource,UITableViewDelegate>
{
    UITableView *_tableView;
    UIControl   *_control;
}
@property (nonatomic ,assign)id<KKAutoTableViewDelegate> autoDelegate;

- (void)setClipsFrame:(CGRect)rect;
- (void)reloadAllData;

@end


@protocol KKAutoTableViewDelegate
@required
- (NSInteger)KKAutoTableViewnumberOfRows;
- (NSString *)KKAutoTableViewCellShowString:(NSInteger)index;

@optional
- (void)KKAutoTableViewCellClicked:(NSInteger)index;
- (void)KKAutoTableViewRemoveButtonClicked;

@end