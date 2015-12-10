//
//  TGDriveRecordTableCell.h
//  TGOBD
//
//  Created by Jiahai on 14-3-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
@class TGModelDriveRecordDetail;

@interface TGDriveRecordTableCell : UITableViewCell
{
    UILabel             *_startTimeLabel1;
    UILabel             *_startTimeLabel2;
    UILabel             *_startAddressLabel;
    UILabel             *_startAdressCityLabel;
    UILabel             *_endTimeLabel1;
    UILabel             *_endTimeLabel2;
    UILabel             *_endAddressLabel;
    UILabel             *_endAddressCityLabel;
    UIImageView         *_lineImageView;
    
    UIImageView         *countBgView;
    
    UILabel             *_distanceLabel;
    UILabel             *_travelTimeLabel;
    UILabel             *_averageOilWearLabel;
    UILabel             *_oilWearLabel;
    
    UIImageView         *_distanceImgView;
    UIImageView         *_travelTimeImgView;
    UIImageView         *_averageOilWearImgView;
    UIImageView         *_oilWearImgView;
    
    NSDateFormatter     *_dateFormatter;
}

//@property (nonatomic, assign) NSInteger     indexRow;

-(void) refreshUIWithDriveRecordDetail:(TGModelDriveRecordDetail *)aDetail;
@end
