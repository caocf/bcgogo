//
//  KKShopFilterPopView.h
//  KKOBD
//
//  Created by zhuyc on 13-8-22.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>

// =======================================================================================
@interface KKPopMenuItem : NSObject {
	NSInteger		_parentItemId;				// -1 means there's no parent menu
	NSInteger		_itemId;
	NSString		*_title;
	id				_additional;
}
@property (nonatomic, assign) NSInteger parentItemId;
@property (nonatomic, assign) NSInteger	itemId;
@property (nonatomic, copy) NSString	*title;
@property (nonatomic, assign) id		additional;

- (id)initWithId:(NSInteger)aItemId parentId:(NSInteger)aParentId title:(NSString*)aTitle others:(id)aOthers;

@end

// =======================================================================================

@protocol KKShopFilterPopViewDelegate;

@interface KKShopFilterPopView : UIControl<UITableViewDataSource,UITableViewDelegate>
{
    NSMutableArray      *_leftDataArray;
    NSMutableArray      *_rightDataArray;
    
    UITableView         *_leftTableView;
    UITableView         *_rightTableView;
    UIImageView         *_upArrowImv;
    UIImageView         *_bgImv;
    UIImageView         *_lineImv;
    
    BOOL                _isTwoStep;
    float               _rowHeight;
    float               _arrowOrignX;
    float               _leftItemWidth;
    float               _rightItemWidth;
    
}
@property (nonatomic ,assign)NSInteger  popId;
@property (nonatomic ,assign)float arrowOrignX;
@property (nonatomic ,assign)BOOL   isTwoStep;
@property (nonatomic ,assign)BOOL   isInit;
@property (nonatomic ,assign)BOOL   arrowHidden;
@property (nonatomic ,assign)BOOL   centerView;
@property (nonatomic, assign) NSInteger selectedInFirstList;
@property (nonatomic, assign) NSInteger selectedInSecondList;
@property (nonatomic ,assign)id<KKShopFilterPopViewDelegate> popViewDelegate;

- (id)initWithFrame:(CGRect)frame WithArrowOrignX:(float)orignX  WithRowHeight:(float)rowHeight;
- (void)setLeftDataArray:(NSMutableArray *)lArr RightDataArray:(NSMutableArray *)rArr;

@end


@protocol KKShopFilterPopViewDelegate
@optional
- (void)KKShopFilterPopView:(KKShopFilterPopView *)popView WithItem:(KKPopMenuItem *)item AndParentItem:(KKPopMenuItem *)pItem;
- (void)KKShopFilterPopViewLeftCellClickedWithItem:(KKPopMenuItem *)item;

@end

