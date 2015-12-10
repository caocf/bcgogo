//
//  TGViolateTableCell.h
//  TGOBD
//
//  Created by Jiahai on 14-3-10.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
@class TGModelViolateDetailInfo;

@interface TGViolateTableCell : UITableViewCell
{
    UILabel         *_dateLabel;
    UILabel         *_areaLabel;
    UILabel         *_actLabel;
    UILabel         *_codeLabel;
    UILabel         *_fenLabel;
    UILabel         *_moneyLabel;
}
@property (nonatomic, strong) TGModelViolateDetailInfo *violateDetailInfo;
-(void) setUIFit:(TGModelViolateDetailInfo *)aViolateDetailInfo;
@end
