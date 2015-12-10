//
//  KKViolateTableViewCell.h
//  KKOBD
//
//  Created by Jiahai on 13-12-16.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
@class KKViolateDetailInfo;
@interface KKViolateTableViewCell : UITableViewCell
{
    UILabel         *_dateLabel;
    UILabel         *_areaLabel;
    UILabel         *_actLabel;
    UILabel         *_codeLabel;
    UILabel         *_fenLabel;
    UILabel         *_moneyLabel;
}
@property (nonatomic, retain) KKViolateDetailInfo *violateDetailInfo;
-(void) setUIFit:(KKViolateDetailInfo *)aViolateDetailInfo;
@end
