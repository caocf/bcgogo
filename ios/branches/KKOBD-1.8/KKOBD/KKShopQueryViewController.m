//
//  KKShopQueryViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-22.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKShopQueryViewController.h"
#import "KKApplicationDefine.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKShopFilterSegmentControl.h"
#import "KKShopFilterPopView.h"
#import "KKSmallRatingView.h"
#import <QuartzCore/QuartzCore.h>
#import "KKShopQueryTableViewCell.h"
#import "KKShopMapListViewController.h"
#import "KKShopDetailViewController.h"
#import "GGFullscreenImageViewController.h"
#import "AKLocationManager.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKPreference.h"
#import "KKAppDelegate.h"
#import "UIImageView+WebCache.h"
#import "KKPhotoBrowserVc.h"

#define  KKPopViewTag			10001

static const NSString *KKSortCategoryStrings[] = {@"距离排序",@"评分排序"};
static const NSString *KKSortCategoryShowStrings[] = {@"按距离",@"按评分"};
static const NSString *KKSortCategoryParamStrings[] = {@"DISTANCE",@"EVALUATION"};

@interface KKCustomButton : UIButton
@property (nonatomic ,assign) NSInteger index;
@end

@implementation KKCustomButton
@synthesize index;
@end


@interface KKShopQueryViewController ()

@end

@implementation KKShopQueryViewController
@synthesize currentCity = _currentCity;

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    _moreBtnView = [[UIView alloc] init];
    _moreBtnView.frame = CGRectMake(236, 3, 42, 36);
    _moreBtnView.backgroundColor = [UIColor clearColor];
    UIButton *moreBtn = [[UIButton alloc] initWithFrame:_moreBtnView.bounds];
    [moreBtn setBackgroundColor:[UIColor clearColor]];
    moreBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    [moreBtn setTitle:@"更多" forState:UIControlStateNormal];
    [moreBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [moreBtn setTitleColor:[UIColor grayColor] forState:UIControlStateHighlighted];
    [moreBtn addTarget:self action:@selector(moreShopButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [_moreBtnView addSubview:moreBtn];
    [moreBtn release];
    
    [self setVcEdgesForExtendedLayout];
    [self setMapView];
    [self initVariables];
    [self initComponents];
    
    if (self.VcType == eShopQueryVcType_home)
        [self getServiceScope];
    else
        [self searchShopInfo];
    
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    if([KKAuthorization sharedInstance].accessAuthorization.shopQuery_MoreBtn)
    {
        [self.navigationController.navigationBar addSubview:_moreBtnView];
    }
    [_mapView viewWillAppear];
    _mapView.delegate = self;

}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [_moreBtnView removeFromSuperview];
    [_mapView viewWillDisappear];
    _mapView.delegate = nil;
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self resignKeyboardNotification];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [self removeKeyboardNotification];
}

#pragma mark -
#pragma mark Custom methods

- (void)setMapView
{
    if (self.VcType == eShopQueryVcType_home)
    {
        _mapView = [[BMKMapView alloc] initWithFrame:self.view.frame];
        _mapView.delegate = self;
        [self.view addSubview:_mapView];
        [_mapView release];
        
        if ([KKAppDelegateSingleton.currentCity.replaceCityName length] > 0)
        {
            BMKOfflineMap *offLineMap = [[BMKOfflineMap alloc] init];
            NSArray *cityArr = [offLineMap searchCity:KKAppDelegateSingleton.currentCity.replaceCityName];
            if ([cityArr count] > 0)
            {
                NSLog(@"current city is %@",KKAppDelegateSingleton.currentCity.replaceCityName);
                
                BMKOLSearchRecord *record = [cityArr objectAtIndex:0];
                
                KKCity *gloalCity = [[KKCity alloc] init];
                gloalCity.provinceName = KKAppDelegateSingleton.currentCity.replaceProvinceName;
                gloalCity.cityName = KKAppDelegateSingleton.currentCity.replaceCityName;
                gloalCity.cityCode = [NSString stringWithFormat:@"%d",record.cityID];
                KKAppDelegateSingleton.currentCity = gloalCity;
                [gloalCity release];
            }
            [offLineMap release];
        }
        
        KKCity *cCity = [[KKCity alloc] init];
        cCity.provinceName = KKAppDelegateSingleton.currentCity.provinceName;
        cCity.cityName = KKAppDelegateSingleton.currentCity.cityName;
        cCity.cityCode = KKAppDelegateSingleton.currentCity.cityCode;
        self.currentCity = cCity;
        [cCity release];
        
        _currentSelectedProvinceName = self.currentCity.provinceName;
    }
}

- (void) initVariables
{
    _enableRefresh = YES;
    _isLoading = NO;
    _page = 1;
    _size = 10;
    _is4sShop = NO;
    
    self.sortType = eSortType_distance;
    self.shopCategoryType = eShopCategory_4S;
    
    _dataArray = [[NSMutableArray alloc] init];
    _provinceArray = [[NSMutableArray alloc] init];
    _cityArray = [[NSMutableArray alloc] init];
    _cityDict = [[NSMutableDictionary alloc] init];
    _promptDataArray = [[NSMutableArray alloc] init];
    if (self.serviceTypeArray == nil)
    {
        NSMutableArray *arr = [[NSMutableArray alloc] init];
        self.serviceTypeArray = arr;
        [arr release];
    }
}

- (void) initComponents
{
    [self setNavgationBar];
    [self setBackGroundView];
    
    float height = 0;
    
    if (self.VcType == eShopQueryVcType_home)
    {
        [self createFilterControl];
        height += 33;
    }
    [self createSearchBarWithFrame:CGRectMake(0, height, 320, 44)];
    height += 44;
    
    [_textField addTarget:self action:@selector(textFieldValueChanged:) forControlEvents:UIControlEventEditingChanged];
    
    _mainTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, height, 320, currentScreenHeight - height - [self getOrignY] - 44 - 49) style:UITableViewStylePlain];
    _mainTableView.delegate = self;
    _mainTableView.dataSource = self;
    _mainTableView.backgroundColor = [UIColor clearColor];
    _mainTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_mainTableView];
    [_mainTableView release];
    
}


- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"店面查询";
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];

    self.navigationItem.rightBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_shopq_map.png"] bgImage:nil target:self action:@selector(mapButtonClicked)];
}

- (void)setBackGroundView
{
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:self.view.bounds];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor clearColor];
    bgImv.image = [UIImage imageNamed:@"bg_serviceSeeking.png"];
    [self.view addSubview:bgImv];
    [bgImv release];
}

- (void)createFilterControl
{
    _filterControl = [[KKShopFilterSegmentControl alloc] initWithFrame:CGRectMake(0, 0, 320, 33)];
    _filterControl.delegate = self;
    [_filterControl setShopFilterItemTitle:(NSString *)KKSortCategoryShowStrings[self.sortType] WithIndex:1000];
    [_filterControl setShopFilterItemTitle:self.serviceTypeKey WithIndex:1001];
    [_filterControl setShopFilterItemTitle:[_currentCity.cityName length] > 0 ?_currentCity.cityName : @"城市" WithIndex:1002];
    [self.view addSubview:_filterControl];
    [_filterControl release];
}

- (void)createSearchBarWithFrame:(CGRect)rect
{
    UIImage *image = [UIImage imageNamed:@"bg_shopq_searchBar.png"];
    
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:rect];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor redColor];
    bgImv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    
    image = [UIImage imageNamed:@"bg_shopq_searchBar_field.png"];
    CGSize size = image.size;
    
    UIImageView *bgInputImv = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), 0.5*(44 - image.size.height), 240, image.size.height)];
    bgInputImv.userInteractionEnabled = YES;
    bgInputImv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    
    image = [UIImage imageNamed:@"icon_shopq_searchBar_magnifier.png"];
    UIImageView *marImv = [[UIImageView alloc] initWithFrame:CGRectMake(10, 0.5*(size.height - image.size.height), image.size.width, image.size.height)];
    marImv.image = image;
    [bgInputImv addSubview:marImv];
    [marImv release];
    
    _textField = [[UITextField alloc] initWithFrame:CGRectMake(30, 0, 210, size.height)];
    _textField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    _textField.delegate = self;
    _textField.clearButtonMode = UITextFieldViewModeWhileEditing;
    _textField.font = [UIFont systemFontOfSize:15.f];
    _textField.backgroundColor = [UIColor clearColor];
    _textField.textColor = [UIColor blackColor];
    _textField.placeholder = @"店铺名称/地址";
    _textField.text = self.searchStr;
    [bgInputImv addSubview:_textField];
    [_textField release];
    
    image = [UIImage imageNamed:@"icon_shopq_searchBar_btn.png"];
    UIButton *cancelBtn = [[UIButton alloc] initWithFrame:CGRectMake(255, 0.5*(44 - image.size.height), image.size.width, image.size.height)];
    [cancelBtn setBackgroundImage:image forState:UIControlStateNormal];
    [cancelBtn.titleLabel setFont:[UIFont systemFontOfSize:15.f]];
    [cancelBtn.titleLabel setTextColor:[UIColor whiteColor]];
    [cancelBtn setTitle:@"确定" forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [bgImv addSubview:cancelBtn];
    [cancelBtn release];
    
    [bgImv addSubview:bgInputImv];
    [bgInputImv release];
    
    [self.view addSubview:bgImv];
    [bgImv release];
}


- (KKShopFilterPopView*) popMenuView
{
    KKShopFilterPopView *view = nil;
	for (UIView *subView in [self.view subviews]) {
		if (subView.tag == KKPopViewTag && [subView isKindOfClass:[KKShopFilterPopView class]])
			view = (KKShopFilterPopView*)subView;
	}
    
    if(view == nil)
    {
        KKShopFilterPopView *FilterPopView = [[KKShopFilterPopView alloc] initWithFrame:CGRectMake(0, 33, 320, currentScreenHeight - 33- 44 - 49 - [self getOrignY]) WithArrowOrignX:200 WithRowHeight:33];
        FilterPopView.popViewDelegate = self;
        FilterPopView.tag = KKPopViewTag;
        FilterPopView.popId = 1002;
        FilterPopView.isTwoStep = YES;
        FilterPopView.isInit = YES;
        
        [self.view addSubview:FilterPopView];
        [FilterPopView release];
    }
	return view;
}

- (UITableViewCell *)creatRecommedShopTableViewCell:(UITableView *)tableView WithIndexPath:(NSIndexPath *)indexPath
{
    NSString *CellIdentifier = @"shopCell_recommend";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil)
    {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
        
        UIImage *image = [UIImage imageNamed:@"bg_shopq_headView.png"];
        UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), 0, image.size.width, image.size.height)];
        bgImv.image = image;
        [cell.contentView addSubview:bgImv];
        [bgImv release];
        
        UIImageView *iconImv = [[UIImageView alloc] initWithFrame:CGRectMake(16, 8, 110, 110)];
        iconImv.tag = 100;
        iconImv.layer.cornerRadius = 5;
        iconImv.layer.masksToBounds = YES;
        iconImv.userInteractionEnabled = YES;
        [cell.contentView addSubview:iconImv];
        [iconImv release];
        
        KKCustomButton *button = [[KKCustomButton alloc] initWithFrame:iconImv.frame];
        button.backgroundColor = [UIColor clearColor];
        button.tag = 106;
        [button addTarget:self action:@selector(iconButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
        [cell.contentView addSubview:button];
        [button release];
        
        float height = 14;
        
        UILabel *shopNameLb = [[UILabel alloc] initWithFrame:CGRectMake(133, height , 165, 15)];
        shopNameLb.backgroundColor = [UIColor clearColor];
        shopNameLb.textColor = KKCOLOR_3359ac;
        shopNameLb.textAlignment = UITextAlignmentLeft;
        shopNameLb.font = [UIFont boldSystemFontOfSize:15.f];
        shopNameLb.tag = 101;
        [cell.contentView addSubview:shopNameLb];
        [shopNameLb release];
        
        height += 18;
        
        KKSmallRatingView *rateView = [[KKSmallRatingView alloc] initWithRank:0];
        [rateView setFrame:CGRectMake(133, height, 100, 15)];
        rateView.tag = 102;
        [cell.contentView addSubview:rateView];
        [rateView release];
        
        height += 25;
        
        image = [UIImage imageNamed:@"icon_shopq_cell_add.png"];
        UIImageView *mark1Imv = [[UIImageView alloc] initWithFrame:CGRectMake(133, height, image.size.width, image.size.height)];
        mark1Imv.backgroundColor = [UIColor clearColor];
        mark1Imv.userInteractionEnabled = YES;
        mark1Imv.image = image;
        [cell.contentView addSubview:mark1Imv];
        [mark1Imv release];
        
        UILabel *mark1Label = [[UILabel alloc] initWithFrame:CGRectMake(153, height, 145, 12)];
        mark1Label.backgroundColor = [UIColor clearColor];
        mark1Label.font = [UIFont systemFontOfSize:11.f];
        mark1Label.textAlignment = UITextAlignmentLeft;
        mark1Label.textColor = KKCOLOR_717171;
        mark1Label.tag = 103;
        [cell.contentView addSubview:mark1Label];
        [mark1Label release];
        
        height += 18;
        
        image = [UIImage imageNamed:@"icon_shopq_cell_map.png"];
        UIImageView *mark2Imv = [[UIImageView alloc] initWithFrame:CGRectMake(133, height, image.size.width, image.size.height)];
        mark2Imv.backgroundColor = [UIColor clearColor];
        mark2Imv.userInteractionEnabled = YES;
        mark2Imv.image = image;
        [cell.contentView addSubview:mark2Imv];
        [mark2Imv release];
        
        UILabel *mark2Label = [[UILabel alloc] initWithFrame:CGRectMake(153, height, 145, 12)];
        mark2Label.backgroundColor = [UIColor clearColor];
        mark2Label.font = [UIFont systemFontOfSize:11.f];
        mark2Label.textAlignment = UITextAlignmentLeft;
        mark2Label.textColor = KKCOLOR_717171;
        mark2Label.tag = 104;
        [cell.contentView addSubview:mark2Label];
        [mark2Label release];
        
        height += 18;
        
        image = [UIImage imageNamed:@"icon_shopq_cell_rep.png"];
        UIImageView *mark3Imv = [[UIImageView alloc] initWithFrame:CGRectMake(133, height, image.size.width, image.size.height)];
        mark3Imv.backgroundColor = [UIColor clearColor];
        mark3Imv.userInteractionEnabled = YES;
        mark3Imv.image = image;
        [cell.contentView addSubview:mark3Imv];
        [mark3Imv release];
        
        UILabel *mark3Label = [[UILabel alloc] initWithFrame:CGRectMake(153, height, 145, 12)];
        mark3Label.backgroundColor = [UIColor clearColor];
        mark3Label.font = [UIFont systemFontOfSize:11.f];
        mark3Label.textAlignment = UITextAlignmentLeft;
        mark3Label.tag = 105;
        mark3Label.textColor = KKCOLOR_717171;
        [cell.contentView addSubview:mark3Label];
        [mark3Label release];
    }
    UIImageView *iconImv = (UIImageView *)[cell.contentView viewWithTag:100];
    UILabel *shopNameLb = (UILabel *)[cell.contentView viewWithTag:101];
    KKSmallRatingView *rateView = (KKSmallRatingView *)[cell.contentView viewWithTag:102];
    UILabel *distanceLb = (UILabel *)[cell.contentView viewWithTag:103];
    UILabel *addressLb = (UILabel *)[cell.contentView viewWithTag:104];
    UILabel *serviceRangeLb = (UILabel *)[cell.contentView viewWithTag:105];
    KKCustomButton *button = (KKCustomButton *)[cell.contentView viewWithTag:106];
    button.index = indexPath.row;
    
    KKModelShopInfo *shopInfo = (KKModelShopInfo *)[_dataArray objectAtIndex:indexPath.row];

    [iconImv setImageWithURL:[NSURL URLWithString:shopInfo.smallImageUrl]];
    shopNameLb.text = shopInfo.name;
    [rateView setSmallRankViewWithRank:shopInfo.totalScore];
    distanceLb.text = [NSString stringWithFormat:@"%.2fkm",shopInfo.distance];
    addressLb.text = shopInfo.address;
    serviceRangeLb.text = shopInfo.serviceScope;
    
    return cell;
}

- (UITableViewCell *)creatNomalShopTableViewCell:(UITableView *)tableView WithIndexPath:(NSIndexPath *)indexPath
{
    NSString *CellIdentifier = @"shopCell_nomal";
    
    KKShopQueryTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil)
    {
        cell = [[[KKShopQueryTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
        
        KKCustomButton *button = [[KKCustomButton alloc] initWithFrame:CGRectZero];
        button.backgroundColor = [UIColor clearColor];
        button.tag = 100;
        [button addTarget:self action:@selector(iconButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
        [cell.contentView addSubview:button];
        [button release];
    }
    KKCustomButton *button = (KKCustomButton *)[cell.contentView viewWithTag:100];
    [cell bringSubviewToFront:button];
    [button setFrame:CGRectMake(15, 10, cell.iconImv.frame.size.width, cell.iconImv.frame.size.height)];
    button.index = indexPath.row;
    
    KKModelShopInfo *shopInfo = (KKModelShopInfo *)[_dataArray objectAtIndex:indexPath.row];
    [cell.iconImv setImageWithURL:[NSURL URLWithString:shopInfo.smallImageUrl]];
    [cell setContentWith:shopInfo];
    
    return cell;
}

#pragma mark -
#pragma mark Events

- (void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

-(void) moreShopButtonClicked
{
    [self reGetShopInfo];
}

- (void)mapButtonClicked
{
    KKModelServiceCategory *service = nil;
    if([self.serviceTypeArray count] > self.serviceIndex)
        service = [self.serviceTypeArray objectAtIndex:self.serviceIndex];
    
    KKShopMapListViewController *Vc = [[KKShopMapListViewController alloc] initWithNibName:@"KKShopMapListViewController" bundle:nil];
    Vc.shopArray = [NSMutableArray arrayWithArray:_dataArray];
    Vc.currentCity = self.currentCity;
    Vc.serviceName = service.name;
    Vc.serviceIds = service.id;
    Vc.remarkString = self.remarkString;
    Vc.dtcMsgArray = self.dtcMsgArray;
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
}

- (void)headViewClicked
{
    [self shopViewClicked:-1];
}

- (void)shopViewClicked:(NSInteger)index
{
    KKModelServiceCategory *service = [self.serviceTypeArray objectAtIndex:self.serviceIndex];
    
    KKModelShopInfo *shopInfo = [_dataArray objectAtIndex:index];
    KKShopDetailViewController *Vc= [[KKShopDetailViewController alloc] initWithNibName:@"KKShopDetailViewController" bundle:nil];
    Vc.shopId = shopInfo.id;
    Vc.serviceName = service.name;
    Vc.remarkString = self.remarkString;
    Vc.dtcMsgArray = self.dtcMsgArray;
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
}

- (void)iconButtonClicked:(id)sender
{
    KKCustomButton *button = (KKCustomButton *)sender;
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:button.index inSection:0];
    KKModelShopInfo *shopInfo = (KKModelShopInfo *)[_dataArray objectAtIndex:button.index];
    KKPhotoBrowserVc *photoBrowserVc = [[KKPhotoBrowserVc alloc] initWithSmallUrl:shopInfo.smallImageUrl andBigUrl:shopInfo.bigImageUrl];
    [self.navigationController presentModalViewController:photoBrowserVc animated:NO];
    [photoBrowserVc release];
    
    return;
    
    GGFullscreenImageViewController *vc = [[GGFullscreenImageViewController alloc] init];
    UITableViewCell *cell = [_mainTableView cellForRowAtIndexPath:indexPath];
    
    if ([cell isKindOfClass:[KKShopQueryTableViewCell class]])
    {
        KKShopQueryTableViewCell *queryCell = (KKShopQueryTableViewCell *)[_mainTableView cellForRowAtIndexPath:indexPath];
        vc.liftedImageView = queryCell.iconImv;
    }
    else
        vc.liftedImageView = (UIImageView *)[cell.contentView viewWithTag:100];
    
    [self presentViewController:vc animated:YES completion:nil];
    [vc release];
}

- (void)cityChanged
{
    
}

- (void)getShopInfo
{
    _isLoading = YES;
    KKModelServiceCategory *service = nil;
    if([self.serviceTypeArray count] > self.serviceIndex)
        service = [self.serviceTypeArray objectAtIndex:self.serviceIndex];
    
    [[KKProtocolEngine sharedPtlEngine] shopSearchList:[KKHelper getShopListCoordinate2DString:KKAppDelegateSingleton.currentCoordinate2D]
                                       serviceScopeIds:service.id
                                        coordinateType:KKAppDelegateSingleton.coordinateType
                                              sortType:(NSString *)KKSortCategoryParamStrings[self.sortType]
                                                areaId:_currentCity.cityID
                                              cityCode:_currentCity.cityCode
                                              shopType:_is4sShop ? @"SHOP_4S":@"ALL"
                                              keywords:(self.VcType == eShopQueryVcType_home)? nil : self.searchStr
                                                isMore:_isMore
                                                pageNo:_page pageSize:_size delegate:self];
}

- (void)searchShopInfo
{
    _isLoading = YES;
    
    [[KKProtocolEngine sharedPtlEngine] shopSearchList:[KKHelper getShopListCoordinate2DString:KKAppDelegateSingleton.currentCoordinate2D]
                                       serviceScopeIds:self.serviceIds
                                        coordinateType:KKAppDelegateSingleton.coordinateType
                                              sortType:(NSString *)KKSortCategoryParamStrings[self.sortType]
                                                areaId:_currentCity.cityID
                                              cityCode:_currentCity.cityCode
                                              shopType:@"ALL"
                                              keywords:self.searchStr
                                                isMore:_isMore
                                                pageNo:_page pageSize:_size delegate:self];
}

- (void)reGetShopInfo
{
    _page = 1;
    _enableRefresh = YES;
    [self getShopInfo];
}

- (void)getSuggestInfo
{
    KKModelServiceCategory *service = nil;
    if([self.serviceTypeArray count] > self.serviceIndex)
        service = [self.serviceTypeArray objectAtIndex:self.serviceIndex];
    
    [[KKProtocolEngine sharedPtlEngine] shopSuggestionsByKey:self.searchStr
                                                    cityCode:self.currentCity.cityCode
                                                      areaId:self.currentCity.cityID
                                             serviceScopeIds:service.id
                                                    delegate:self];
}

- (void)getCityInfoWithProvinceId:(NSString *)provinceId
{
    if (_cityType == eShopCity_province)
        [[KKProtocolEngine sharedPtlEngine] areaList:@"PROVINCE" provinceId:nil delegate:self];
    else
        [[KKProtocolEngine sharedPtlEngine] areaList:@"CITY" provinceId:provinceId delegate:self];
}

- (void)getServiceScope
{
    [[KKProtocolEngine sharedPtlEngine] getServiceCategoryList:[KKServiceScopeFirstCategoryDict objectForKey:self.serviceTypeKey] delegate:self];
}

- (void)setCityDataAndShow
{
    KKShopFilterPopView *popView = [self popMenuView];
    NSInteger pid = 1;
    
    NSMutableArray *leftArr = [[NSMutableArray alloc] init];
    NSMutableArray *rightArr = [[NSMutableArray alloc] init];
    
    NSInteger topSelected = 0;			// first(top) menu selected index
    NSInteger secSelected = 0;
    
    for (int i = 0 ;i < [_provinceArray count] ; i++)
    {
        KKModelAreaInfo *areaInfo = [_provinceArray objectAtIndex:i];
        if ([areaInfo.name isEqualToString:_currentSelectedProvinceName])
            topSelected = i;
        
        KKPopMenuItem *leftItem = [[KKPopMenuItem alloc] initWithId: i + 1 parentId:-1 title:areaInfo.name others:areaInfo];
        [leftArr addObject:leftItem];
        [leftItem release];
        
        NSArray *childArr = [_cityDict objectForKey:areaInfo.name];
        for (int j = 0; j < [childArr count]; j ++) {
            KKModelAreaInfo *childAreaInfo = [childArr objectAtIndex:j];
            if ([childAreaInfo.name isEqualToString:_currentCity.cityName])
                secSelected = j;
            
            KKPopMenuItem *rightItem = [[KKPopMenuItem alloc] initWithId:j + 1 parentId:pid title:childAreaInfo.name others:childAreaInfo];
            [rightArr addObject:rightItem];
            [rightItem release];
        }
        pid ++;
    }

    if (popView)
    {
        if (popView.popId == 1002)
        {
            popView.selectedInFirstList  = topSelected;
            popView.selectedInSecondList = secSelected;
            [popView setLeftDataArray:leftArr RightDataArray:rightArr];
        }
    }
    else
    {
        KKShopFilterPopView *FilterPopView = [[KKShopFilterPopView alloc] initWithFrame:CGRectMake(0, 33, 320, currentScreenHeight - 33- 44 - 49 - [self getOrignY]) WithArrowOrignX:200 WithRowHeight:33];
        FilterPopView.popViewDelegate = self;
        FilterPopView.tag = KKPopViewTag;
        FilterPopView.popId = 1002;
        FilterPopView.isTwoStep = YES;
        FilterPopView.isInit = YES;
        
        FilterPopView.selectedInFirstList  = topSelected;
        FilterPopView.selectedInSecondList = secSelected;
        [FilterPopView setLeftDataArray:leftArr RightDataArray:rightArr];
        [self.view addSubview:FilterPopView];
        [FilterPopView release];
    }
    
    [leftArr release];
    [rightArr release];
}

- (void)setServiceRangeText
{
    
}

- (void)shopCategoryButtonClicked
{
    _is4sShop = !_is4sShop;
    
    if (_is4sShop)
        self.shopCategoryType = eShopCategory_4S;
    else
        self.shopCategoryType = eShopCategory_all;
    
    [_filterControl setShopFilterItemBackGroundImage:(_is4sShop ? [UIImage imageNamed:@"icon_btn_4s_selected.png"] : nil) WithIndex:1003];
}

- (void)cancelButtonClicked
{
     [_textField resignFirstResponder];
    
    if ([_textField.text length] > 0)
    {
//        if (self.VcType == eShopQueryVcType_home)
//        {
//            KKModelServiceCategory *service = [self.serviceTypeArray objectAtIndex:self.serviceIndex];
//            KKShopQueryViewController *queryVc = [[KKShopQueryViewController alloc] initWithNibName:@"KKShopQueryViewController" bundle:nil];
//            queryVc.VcType = eShopQueryVcType_search;
//            queryVc.searchStr = nilOrString(_textField.text);
//            queryVc.currentCity = _currentCity;
//            queryVc.serviceIds = service.id;
//            queryVc.serviceIndex = self.serviceIndex;
//            queryVc.serviceTypeArray = self.serviceTypeArray;
//            queryVc.remarkString = self.remarkString;
//            [self.navigationController pushViewController:queryVc animated:YES];
//            [queryVc release];
//        }
//        else
//        {
            _page = 1;
            self.searchStr = nilOrString(_textField.text);
            
            [self searchShopInfo];
//        }
    }
    
}

- (void) didKeyboardNotification:(NSNotification*)notification
{
    NSString* nName = notification.name;
    NSDictionary* nUserInfo = notification.userInfo;
    if ([nName isEqualToString:UIKeyboardDidShowNotification])
    {
        NSString* sysStr = [[UIDevice currentDevice] systemVersion];
        sysStr = [sysStr substringToIndex:1];
        NSInteger ver = [sysStr intValue];
        if (ver >= 5)
        {
            NSValue* value = [nUserInfo objectForKey:UIKeyboardFrameEndUserInfoKey];
            CGRect rect = CGRectZero;
            [value getValue:&rect];
            float keyboardHeight = rect.size.height;
            
            float height = 44;
            if (self.VcType == eShopQueryVcType_home)
                height += 33;
            
            if (_promptTableView != nil)
            {
                float h = currentScreenHeight - [self getOrignY] - height - 44 - keyboardHeight;
                [_promptTableView setFrame:CGRectMake(0, height, 320, h)];
                [_promptTableView setClipsFrame:CGRectMake(0, 0, 320, h)];
            }
        }
    }
    if ([nName isEqualToString:UIKeyboardWillHideNotification])
    {
        if (_promptTableView != nil)
        {
            [_promptTableView removeFromSuperview];
            _promptTableView = nil;
        }
    }
}

#pragma mark -
#pragma mark UITableViewDataSource,UITableViewDelegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_dataArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = nil;
    
    if (indexPath.row == 0 && self.VcType != eShopQueryVcType_search)
        cell = [self creatRecommedShopTableViewCell:tableView WithIndexPath:indexPath];
    else
        cell = [self creatNomalShopTableViewCell:tableView WithIndexPath:indexPath];
    
    cell.backgroundColor = [UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0 && self.VcType != eShopQueryVcType_search)
        return 144;
    else
        return 87;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self shopViewClicked:indexPath.row];
}

#pragma mark -
#pragma mark - UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    [_textField resignFirstResponder];
    
    CGPoint offset = scrollView.contentOffset;
    CGSize size = scrollView.frame.size;
    CGSize contentSize = scrollView.contentSize;
    float yMargin = offset.y + size.height - contentSize.height;
    if (_enableRefresh && !_isLoading && yMargin > -1 && contentSize.height > scrollView.bounds.size.height)
    {
        if (self.VcType == eShopQueryVcType_home)
            [self getShopInfo];
        else
            [self searchShopInfo];
    }
    
}


#pragma mark -
#pragma mark KKShopFilterSegmentControlDelegate
- (void)shopFilterSegmentControlClicked:(id)sender
{
    KKShopFilterItem *item = (KKShopFilterItem *)sender;
    KKShopFilterPopView *popView = [self popMenuView];
    
    if (item.itemId == 1003)
    {
        if (popView)
            [popView removeFromSuperview];
        
        [self shopCategoryButtonClicked];
        [self reGetShopInfo];
        return;
    }
    
    BOOL popDismissOnly = NO;
	if (popView) {
		if (popView.popId == 1000 && item.itemId==1000)
			popDismissOnly = YES;
		if (popView.popId == 1001 && item.itemId==1001)
			popDismissOnly = YES;
		if (popView.popId == 1002 && item.itemId==1002)
			popDismissOnly = YES;
	}
	if (popView)
		[popView removeFromSuperview];
	if (popDismissOnly)
		return;
    
    if (item.itemId == 1001)
    {
        if ([self.serviceTypeArray count] == 0)
        {
            [self getServiceScope];
            return;
        }
    }
    
    KKShopFilterPopView *FilterPopView = [[KKShopFilterPopView alloc] initWithFrame:CGRectMake(0, 33, 320, currentScreenHeight - 33- 44 - 49 - [self getOrignY]) WithArrowOrignX:200 WithRowHeight:33];
    FilterPopView.popViewDelegate = self;
    FilterPopView.tag = KKPopViewTag;
    
    if (item.itemId - 1000 == 0)
    {

        FilterPopView.popId = 1000;
        FilterPopView.arrowOrignX = 30;
        
        NSMutableArray *parr = [NSMutableArray arrayWithCapacity:10];
		NSInteger pid = 1;
		
		for (NSInteger i=0; i<sizeof(KKSortCategoryStrings)/sizeof(NSString*); i++) {
			KKPopMenuItem *item = [[KKPopMenuItem alloc] initWithId:pid+i parentId:-1 title:(NSString*)KKSortCategoryStrings[i] others:nil];
			[parr addObject:item];
			[item release];
		}
        FilterPopView.selectedInFirstList = (NSInteger)self.sortType;
        [FilterPopView setLeftDataArray:parr RightDataArray:nil];
        
    }

    if (item.itemId - 1000 == 1)
    {
        FilterPopView.popId = 1001;
        FilterPopView.arrowOrignX = 100;
        
        NSMutableArray *parr = [NSMutableArray arrayWithCapacity:10];
		NSInteger pid = 1;
		
		for (NSInteger i = 0; i < [self.serviceTypeArray count]; i++) {
            KKModelServiceCategory *service = (KKModelServiceCategory *)[self.serviceTypeArray objectAtIndex:i];
			KKPopMenuItem *item = [[KKPopMenuItem alloc] initWithId:pid+i parentId:-1 title:service.name others:nil];
			[parr addObject:item];
			[item release];
		}
        FilterPopView.selectedInFirstList = (NSInteger)self.serviceIndex;
        [FilterPopView setLeftDataArray:parr RightDataArray:nil];
       
    }
    if (item.itemId - 1000 == 2)
    {
        if ([_provinceArray count] == 0)
        {
            _cityType = eShopCity_province;
            [self getCityInfoWithProvinceId:nil];
        }
        else
            [self setCityDataAndShow];
        
        return;
    }

    [self.view addSubview:FilterPopView];
    [FilterPopView release];
}

#pragma mark -
#pragma mark KKShopFilterPopViewDelegate

- (void)KKShopFilterPopView:(KKShopFilterPopView *)popView WithItem:(KKPopMenuItem *)item AndParentItem:(KKPopMenuItem *)pItem
{
    NSInteger index = popView.popId - 1000;
    switch (index) {
        case 0:
        {
            self.sortType = popView.selectedInFirstList;
            [_filterControl setShopFilterItemTitle:(NSString *)KKSortCategoryShowStrings[self.sortType] WithIndex:popView.popId];
            break;
        }
        case 1:
        {
            //服务选择
            self.serviceIndex = popView.selectedInFirstList;
            KKModelServiceCategory *service = nil;
            if([self.serviceTypeArray count] > self.serviceIndex)
                service = [self.serviceTypeArray objectAtIndex:self.serviceIndex];
            [_filterControl setShopFilterItemTitle:service.name WithIndex:popView.popId];
            self.serviceIds = service.id;
            break;
        }
        case 2:
        {
            KKModelAreaInfo *pInfo = (KKModelAreaInfo *)pItem.additional;
            KKModelAreaInfo *cInfo = (KKModelAreaInfo *)item.additional;
            _currentCity.provinceName = pInfo.name;
            _currentCity.cityName = cInfo.name;
            _currentCity.cityID = cInfo.id;
            _currentCity.cityCode = cInfo.cityCode;

            [_filterControl setShopFilterItemTitle:_currentCity.cityName WithIndex:popView.popId];
            
            break;
        }
        default:
            break;
    }
    
    [self reGetShopInfo];
        
}

- (void)KKShopFilterPopViewLeftCellClickedWithItem:(KKPopMenuItem *)item
{
    KKModelAreaInfo *info = (KKModelAreaInfo *)item.additional;
    _currentSelectedProvinceName = info.name;
    _cityType = eShopCity_city;
    [self getCityInfoWithProvinceId:info.id];
}

#pragma mark -
#pragma mark UITextFieldDelegate

- (void)textFieldValueChanged:(UITextField *)textField
{
    self.searchStr = textField.text;
    if ([self.searchStr length] > 0 && _promptTableView)
        [self getSuggestInfo];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    
    return YES;
}

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    if (_promptTableView != nil)
    {
        [_promptTableView removeFromSuperview];
        _promptTableView = nil;
    }
    
    float height = 44;
    if (self.VcType == eShopQueryVcType_home)
        height += 33;
    
    _promptTableView = [[KKAutoTableView alloc] initWithFrame:CGRectMake(0, height, 320, currentScreenHeight - height - [self getOrignY] - 44 - 49)];
    _promptTableView.autoDelegate = self;
    [self.view addSubview:_promptTableView];
    [_promptTableView release];
    
}

#pragma mark -
#pragma mark KKAutoTableViewDelegate

- (NSInteger)KKAutoTableViewnumberOfRows
{
    return [_promptDataArray count];
}

- (NSString *)KKAutoTableViewCellShowString:(NSInteger)index
{
    KKModelShopInfo *shopInfo = [_promptDataArray objectAtIndex:index];
    return shopInfo.name;
}

- (void)KKAutoTableViewCellClicked:(NSInteger)index
{
    KKModelShopInfo *shopInfo = [_promptDataArray objectAtIndex:index];
    _textField.text  = shopInfo.name;
    
    [self cancelButtonClicked];
}

- (void)KKAutoTableViewRemoveButtonClicked
{
    [_promptTableView removeFromSuperview];
    _promptTableView = nil;
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate

-(NSNumber *) localCacheGetSuccess:(NSNumber *)aReqId withObject:(id)cacheObj
{
    switch ([aReqId integerValue]) {
        case ePtlApi_area_list:
        case ePtlApi_serviceCategory_list:
        {
            if([cacheObj isEqual:[NSNull null]])
            {
                [MBProgressHUD showHUDAddedTo:self.view animated:YES];
            }
        }
            break;
        case ePtlApi_shop_searchList:
        {
            //_page默认为1
            if(_page > 1)
            {
                [MBProgressHUD showHUDAddedTo:self.view animated:YES];
                return [NSNumber numberWithBool:NO];
            }
            else
            {
                if([_dataArray count] != 0)
                {
                    return [NSNumber numberWithBool:NO];
                }
            }
        }
            break;
        default:
            break;
    }
    return [NSNumber numberWithBool:YES];
}

- (NSNumber *)areaListResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    if([aReqId integerValue] != ePtlApi_LoadCache)
        [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        if(_cityType == eShopCity_province)
        {
            if([_provinceArray count] == 0)
            {
                [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
            }
        }
        else
        {
            if([_cityDict objectForKey:_currentSelectedProvinceName] == nil)
            {
                [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
            }
        }
		return KKNumberResultEnd;
	}
    KKModelAreaListRsp *areaListRsp = (KKModelAreaListRsp *)rsp;
    if (_cityType == eShopCity_province)
    {
        [_provinceArray removeAllObjects];
        
        KKModelAreaInfo *temArea = nil;
        for (KKModelAreaInfo *areaInfo in areaListRsp.KKArrayFieldName(areaList, KKModelAreaInfo))
        {
            if ([areaInfo.name isEqualToString:@"江苏省"])
            {
                temArea = [areaInfo retain];
                [areaListRsp.KKArrayFieldName(areaList, KKModelAreaInfo) removeObject:areaInfo];
                break;
            }
        }
        if (temArea)
        {
            [areaListRsp.KKArrayFieldName(areaList, KKModelAreaInfo) insertObject:temArea atIndex:0];
            [temArea release];
        }
        
        [_provinceArray addObjectsFromArray:areaListRsp.KKArrayFieldName(areaList, KKModelAreaInfo)];
        
        [self setCityDataAndShow];
    }
    else
    {
        if ([_cityDict objectForKey:_currentSelectedProvinceName] == nil)
        {
            [_cityDict setObject:areaListRsp.KKArrayFieldName(areaList, KKModelAreaInfo) forKey:_currentSelectedProvinceName];
            [self setCityDataAndShow];
        }
    }
    return KKNumberResultEnd;
}

- (NSNumber *)shopSearchListResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    
    NSObject *rsp = (NSObject*)aRspObj;    
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:^{
            if([aReqId integerValue] != ePtlApi_LoadCache)
                _isLoading = NO;
        }];
		return KKNumberResultEnd;
	}
    
    if([aReqId integerValue] != ePtlApi_LoadCache)
        _isLoading = NO;
    
    _isMore = YES;
    KKModelShopSearchListRsp *listRsp = (KKModelShopSearchListRsp *)rsp;
    if (listRsp.pager.currentPage == 1)
        [_dataArray removeAllObjects];
    
    if ([listRsp.KKArrayFieldName(shopList, KKModelShopInfo) count] == 0 && _page == 1)
    {
        if (_is4sShop)
            [KKCustomAlertView showAlertViewWithMessage:@"该地区暂无4S店铺信息！"];
        else
            [KKCustomAlertView showAlertViewWithMessage:@"该地区暂无相关店铺信息！"];
    }
    
    if ([listRsp.KKArrayFieldName(shopList, KKModelShopInfo) count] < _size)
        _enableRefresh = NO;
    else
        _enableRefresh = YES;
    
    [_dataArray addObjectsFromArray:listRsp.KKArrayFieldName(shopList, KKModelShopInfo)];
    
    if (listRsp.pager.currentPage == 1 && [_dataArray count] > 0 )
        [_mainTableView setContentOffset:CGPointZero animated:NO];
    [_mainTableView reloadData];
    
    if([aReqId integerValue] != ePtlApi_LoadCache)
    {
        _page = listRsp.pager.nextPage;
    }
    return KKNumberResultEnd;
}

- (NSNumber *)shopSuggestionsResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        NSLog(@"suggest error is %@",error.description);
//        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    KKModelShopSuggestionsRsp *suggestRsp = (KKModelShopSuggestionsRsp *)rsp;
    [_promptDataArray removeAllObjects];
    [_promptDataArray addObjectsFromArray:suggestRsp.KKArrayFieldName(shopSuggestionList, KKModelShopInfo)];
    [_promptTableView reloadAllData];
    
    return KKNumberResultEnd;
}

- (NSNumber *)serviceCategoryListResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideAllHUDsForView:self.view animated:NO];
    
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        if([self.serviceTypeArray count] == 0)
        {
            KKError * error = (KKError *)rsp;
            [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
        }
		return KKNumberResultEnd;
	}
    KKModelServiceCategoryListRsp *listRsp = (KKModelServiceCategoryListRsp *)rsp;
    [self.serviceTypeArray removeAllObjects];
    
    if (![self.serviceTypeKey isEqualToString:@"洗车服务"])
    {
//        if ([self.serviceTypeKey isEqualToString:@"服务范围"])
//        {
            KKModelServiceCategory *service = [[KKModelServiceCategory alloc] init];
            service.name = @"所有";
            service.id = nil;
            [self.serviceTypeArray addObject:service];
            [service release];
//        }
    }
    else
    {
        if([listRsp.serviceCategoryDTOList__KKModelServiceCategory count] > 0)
            self.serviceIds = ((KKModelServiceCategory *)[listRsp.serviceCategoryDTOList__KKModelServiceCategory objectAtIndex:0]).id;
    }
    
    [self.serviceTypeArray addObjectsFromArray:listRsp.KKArrayFieldName(serviceCategoryDTOList,KKModelServiceCategory)];
    
    if ([listRsp.KKArrayFieldName(serviceCategoryDTOList,KKModelServiceCategory) count] > 0)
    {
        KKModelServiceCategory *service =  [self.serviceTypeArray objectAtIndex:0];
        [_filterControl setShopFilterItemTitle:service.name WithIndex:1001];
    }
    
    [self getShopInfo];
    
    return KKNumberResultEnd;
}

#pragma mark -
#pragma mark Handle memory

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    _mainTableView = nil;
    _textField = nil;
    _filterControl = nil;
    _mapView = nil;
}

- (void)dealloc
{
    [_dataArray release];
    _dataArray = nil;
    _mainTableView = nil;
    _textField = nil;
    _filterControl = nil;
    _mapView = nil;
    [_moreBtnView release];
    self.currentCity = nil;
    self.serviceTypeArray = nil;
    self.dtcMsgArray = nil;
    self.remarkString = nil;
    self.searchStr = nil;
    self.serviceTypeKey = nil;
    self.serviceIds = nil;
    
    [super dealloc];
}
@end
