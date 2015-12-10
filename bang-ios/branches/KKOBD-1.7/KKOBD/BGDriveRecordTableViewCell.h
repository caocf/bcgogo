//
//  BGDriveRecordTableViewCell.h
//  KKOBD
//
//  Created by Jiahai on 14-1-20.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
@class BGDriveRecordDetail;
@protocol BGDriveRecordTableViewCellDelegate;

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
@property (nonatomic, assign) id<BGDriveRecordTableViewCellDelegate> delegate;
@property (nonatomic, retain) BGDriveRecordDetail *driveRecordDetail;
@property (nonatomic, retain) NSIndexPath       *indexPath;

-(void) refreshUIWithDriveRecordDetail:(BGDriveRecordDetail *)aDetail selected:(BOOL)selected indexPath:(NSIndexPath *)aIndexPath;
@end

@protocol BGDriveRecordTableViewCellDelegate <NSObject>

-(void) driveRecordEditBtnClicked:(NSIndexPath *)aIndexPath;

@end
