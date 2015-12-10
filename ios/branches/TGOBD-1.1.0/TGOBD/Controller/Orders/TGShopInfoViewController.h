//
//  TGShopInfoViewController.h
//  TGOBD
//
//  Created by James Yu on 14-3-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
#import "BMapKit.h"

@interface TGShopInfoViewController : TGBaseViewController <BMKMapViewDelegate>

@property (nonatomic, strong) UILabel *shopName;
@property (nonatomic, strong) UILabel *address;
@property (nonatomic, strong) UILabel *mobile;
@property (nonatomic, strong) BMKMapView *mapView;

@end
