//
//  TGPublicNoticeTableViewCell.h
//  TGOBD
//
//  Created by James Yu on 14-4-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TGBasicModel.h"

@interface TGPublicNoticeTableViewCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel *time;
@property (weak, nonatomic) IBOutlet UILabel *title;
@property (weak, nonatomic) IBOutlet UIImageView *image;
@property (weak, nonatomic) IBOutlet UIView *bgView;

- (void)setCellContent:(TGModelPublicNoticeInfo *)notice;

@end
