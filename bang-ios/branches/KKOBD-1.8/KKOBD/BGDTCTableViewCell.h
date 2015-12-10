//
//  BGDTCTableViewCell.h
//  KKOBD
//
//  Created by Jiahai on 14-2-7.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
@class KKModelDTCMessage;
@protocol BGDTCTableViewCellDelegate;

@interface BGDTCTableViewCell : UITableViewCell
{
    UILabel             *_titleLabel;
    UILabel             *_timeLabel;
    UILabel             *_descTagLabel;
    UILabel             *_descLabel;
    UILabel             *_categoryTagLabe;
    UILabel             *_categoryLabel;
    UIView              *_controlView;
    UIImageView         *_arrowImv;
    UIImageView         *_lineImv;
    UIButton            *_operateBtn;
    
    NSDateFormatter     *dateFormatter;
}
@property (nonatomic, assign) id<BGDTCTableViewCellDelegate> delegate;

-(void) setDTCMessage:(KKModelDTCMessage *)aDTCMessage selected:(BOOL) selected isHistory:(BOOL)isHistory;

+ (float)calculateVehicleConditionTableViewCellHeightWithContent:(KKModelDTCMessage *)content selected:(BOOL)selected;
@end


@protocol BGDTCTableViewCellDelegate <NSObject>

-(void) controlBtnClicked:(NSInteger)btnType;

@end