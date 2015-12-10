//
//  KKOrderOnlineViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-27.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKCustomTextField.h"
#import "KKCustomDataPicker.h"
#import "KKShopFilterPopView.h"
#import "KKProtocolEngineDelegate.h"
#import "KKShopFilterPopView.h"
#import "KKSearchCarModelViewController.h"
@class KKModelShopInfo;
@class KKModelShopDetail;

@interface KKOrderOnlineViewController : UIViewController<KKCustomTextFieldDelegate,KKCustomDataPickerDelegate,KKProtocolEngineDelegate,KKShopFilterPopViewDelegate,KKSearchCarModelDelegate>
{
    UIScrollView        *_mainScrollView;
    UILabel             *_nameLabel;
    KKCustomTextField   *_textField1;       //服务类型
    KKCustomTextField   *_textField2;       //预约时间
    KKCustomTextField   *_textField3;       //备注
    KKCustomTextField   *_textField4;       //车牌号
    KKCustomTextField   *_textField5_1;     //品牌车型
    KKCustomTextField   *_textField5_2;
    KKCustomTextField   *_textField6;       //联系人
    KKCustomTextField   *_textField7;       //联系方式
        
    UIView              *_carBrandMarkView; //品牌水印
    UIView              *_carModelMarkView; //车型水印
    BOOL                 _selectedBrand;
    
}
@property (nonatomic ,retain)KKModelShopDetail  *detailShopInfo;
@property (nonatomic ,assign)NSInteger          selectedIndex;
@property (nonatomic ,retain)NSString           *serviceName;
@property (nonatomic ,retain)NSString           *remarkString;

@end
