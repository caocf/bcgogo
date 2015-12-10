//
//  TGMaintenanceTableViewCell.h
//  TGYF
//
//  Created by James Yu on 14-5-19.
//  Copyright (c) 2014年 james. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TGMaintenanceTableViewCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIView *bgView;
@property (weak, nonatomic) IBOutlet UILabel *vehicleOwner;
@property (weak, nonatomic) IBOutlet UILabel *vehicleNo;
@property (weak, nonatomic) IBOutlet UILabel *time;
@property (weak, nonatomic) IBOutlet UIButton *sendMsg;
@property (weak, nonatomic) IBOutlet UIButton *phoneCall;
@property (weak, nonatomic) IBOutlet UILabel *horizonalLine;
@property (weak, nonatomic) IBOutlet UILabel *veticleLine;
@property (weak, nonatomic) IBOutlet UIView *titleBgView;
@property (weak, nonatomic) IBOutlet UILabel *mileage;

@end
