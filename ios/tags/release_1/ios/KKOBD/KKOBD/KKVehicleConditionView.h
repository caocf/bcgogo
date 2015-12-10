//
//  KKVehicleConditionView.h
//  KKOBD
//
//  Created by zhuyc on 13-8-22.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKBLEEngine.h"

@interface KKVehicleConditionItem : UIView
@property (nonatomic ,retain) UILabel   *detailLabel;
@property (nonatomic ,retain) UILabel   *titleLabel;

@end

@interface KKVehicleConditionView : UIView
{
    KKVehicleConditionItem  *_item1;
    KKVehicleConditionItem  *_item2;
    KKVehicleConditionItem  *_item3;
    KKVehicleConditionItem  *_item4;
}

- (void)setContent:(KKModelVehicleRealtimeData *)content;

@end
