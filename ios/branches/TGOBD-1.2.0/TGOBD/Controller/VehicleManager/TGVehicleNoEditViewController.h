//
//  TGVehicleNoEditViewController.h
//  TGOBD
//
//  Created by James Yu on 14-5-4.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"

@protocol TGPassEditVehicleNoProtocol;

@interface TGVehicleNoEditViewController : TGBaseViewController <UITextFieldDelegate>

@property (nonatomic, assign) id <TGPassEditVehicleNoProtocol> delegate;

@end

@protocol TGPassEditVehicleNoProtocol <NSObject>

- (void)passEditVehicleN0:(NSString *)vehicleNo;

@end