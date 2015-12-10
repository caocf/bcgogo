//
//  KKBindShopViewController.h
//  KKOBD
//
//  Created by Jiahai on 13-12-19.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKCustomTextField.h"
#import "KKShopFilterPopView.h"
#import "KKProtocolEngine.h"
#import "KKProtocolEngineDelegate.h"

@interface KKBindShopViewController : UIViewController<KKShopFilterPopViewDelegate,KKProtocolEngineDelegate>
{
    KKCustomTextField *_shopText;
    KKCustomTextField *_vehicleNoText;
}
@property (nonatomic, copy) NSString *shopName;
@property (nonatomic, copy) NSString *shopId;
@end
