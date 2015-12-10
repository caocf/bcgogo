//
//  BGDriveRecordTableViewCell.h
//  KKOBD
//
//  Created by Jiahai on 14-1-20.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
@class BGDriveRecordDetail;

@interface BGDriveRecordTableViewCell : UITableViewCell
{
    UILabel             *_startTimeLabel1;
    UILabel             *_startTimeLabel2;
    UILabel             *_startAddressLabel;
    UILabel             *_endTimeLabel1;
    UILabel             *_endTimeLabel2;
    UILabel             *_endAddressLabel;
    UIImageView         *_lineImageView;
    UILabel             *_distanceLabel;
    UILabel             *_oilPriceLabel;
    
    UIButton            *_editBtn;
    
    NSDateFormatter     *_dateFormatter;
}

@property (nonatomic, retain) BGDriveRecordDetail *driveRecordDetail;

-(void) refreshUIWithDriveRecordDetail:(BGDriveRecordDetail *)aDetail selected:(BOOL)selected;
@end
