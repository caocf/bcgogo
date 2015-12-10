//
//  TGDTCTableCell.h
//  TGOBD
//
//  Created by Jiahai on 14-3-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
@class TGModelDTCInfo;
@protocol TGDTCTableViewCellDelegate;

@interface TGDTCTableCell : UITableViewCell
{
    UIView              *_view;
    UILabel             *_titleLabel;
    UILabel             *_timeLabel;
    UILabel             *_descTagLabel;
    UILabel             *_descLabel;
    UILabel             *_categoryTagLabe;
    UILabel             *_categoryLabel;
    UIView              *_controlView;
    UIImageView         *_line1Imv;
    UIImageView         *_line2Imv;
    UIImageView         *_line3Imv;
    UIButton            *_operateBtn;
    
    UIButton            *_bgInfoBtn;
    
}
@property (nonatomic, assign) id<TGDTCTableViewCellDelegate> delegate;
@property(nonatomic, strong) TGModelDTCInfo *dtcInfo;

-(void) setDTCMessage:(TGModelDTCInfo *)aDTCInfo isHistory:(BOOL)isHistory;

+ (float)calculateVehicleConditionTableViewCellHeightWithContent:(TGModelDTCInfo *)aDTCInfo;
@end


@protocol TGDTCTableViewCellDelegate <NSObject>

-(void) controlBtnClicked:(NSInteger)btnType dtcInfo:(TGModelDTCInfo *)dtcInfo;

@end
