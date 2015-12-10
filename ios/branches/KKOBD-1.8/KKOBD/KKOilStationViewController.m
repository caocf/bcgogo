//
//  KKOilStationViewController.m
//  KKOBD
//
//  Created by Jiahai on 13-12-5.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKOilStationViewController.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKProtocolEngine.h"
#import "KKGlobal.h"
#import "KKError.h"
#import "KKCustomAlertView.h"
#import "KKOilStationTableViewCell.h"
#import "KKApplicationDefine.h"
#import "KKModelComplex.h"
#import "MBProgressHUD.h"
#import "KKAppDelegate.h"
#import "KKOilStationMapViewController.h"


@interface KKOilStationViewController ()
@end

@implementation KKOilStationViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

-(void) viewWillDisappear:(BOOL)animated
{
    [MBProgressHUD hideHUDForView:self.view animated:NO];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [self setVcEdgesForExtendedLayout];
    [self initComponents];
    
    [self sortStations:0 OilStationListRsp:self.oilStationListRsp];
    
//    [self loadDataBegin];
}

- (void) initComponents
{
    [self setNavgationBar];
    [self setBackGroundView];
    
//    _segmentControl = [[KKServiceSegmentControl alloc] initWithFrame:CGRectMake(0, 0, 320, 35)];
//    _segmentControl.delegate = self;
//    _segmentControl.type = KKServiceSegmentControlType_OilStation;
//    [_segmentControl updateInfo];
//    [self.view addSubview:_segmentControl];
//    [_segmentControl release];
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, 320, currentScreenHeight - 44 - 49 - [self getOrignY]) style:UITableViewStylePlain];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:_tableView.bounds];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor clearColor];
    bgImv.image = [UIImage imageNamed:@"bg_serviceSeeking.png"];
    _tableView.backgroundView = bgImv;
    [bgImv release];
    
    [self.view addSubview:_tableView];
    [_tableView release];

    
}
- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"加油站列表";
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
    
//    self.navigationItem.rightBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_shopq_map.png"] bgImage:nil target:self action:@selector(mapButtonClicked)];
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

-(void) backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

-(void)mapButtonClicked
{
    KKOilStationMapViewController *mapVc = [[KKOilStationMapViewController alloc] init];
    mapVc.oilStationListRsp = self.oilStationListRsp;
    [self.navigationController pushViewController:mapVc animated:YES];
    [mapVc release];
}

-(void) loadDataBegin
{
    if(self.oilStationListRsp && ![self.oilStationListRsp.resultcode isEqualToString:@"200"])
    {
        _isEnd = YES;
        
        if([_tableView numberOfRowsInSection:0] < [self.oilStationListRsp.result.data__KKModelOilStation count])
        {
            [_tableView reloadData];
            return;
        }
    }
    
    if(!_isEnd && !_isLoading)
    {
        _isLoading = YES;
        [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        if(self.oilStationListRsp == nil)
        {
            [[KKProtocolEngine sharedPtlEngine] getOilStationList:KKAppDelegateSingleton.currentCoordinate2D_Gcj02 Radius:10000 Page:[self.oilStationListRsp.result.pageinfo.current intValue]+1 delegate:self];
        }
        else
        {
            [[KKProtocolEngine sharedPtlEngine] getOilStationList:KKAppDelegateSingleton.currentCoordinate2D_Gcj02 Radius:10000 Page:[self.oilStationListRsp.result.pageinfo.current intValue]+1 delegate:self];
        }
    }
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate
-(NSNumber *) oilStationListResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    _isLoading = NO;
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    
    if([((KKModelOilStationListRsp *)rsp).resultcode isEqualToString:@"200"])
    {
        //局部排序
        [self sortStations:_segmentControl.selectedIndex OilStationListRsp:(KKModelOilStationListRsp *)rsp];
        
        if(self.oilStationListRsp)
        {
            [self.oilStationListRsp.result.KKArrayFieldName(data, KKModelOilStation) addObjectsFromArray:((KKModelOilStationListRsp *)rsp).result.KKArrayFieldName(data, KKModelOilStation)];
            self.oilStationListRsp.result.pageinfo =((KKModelOilStationListRsp *)rsp).result.pageinfo;
        }
        else
        {
            self.oilStationListRsp = (KKModelOilStationListRsp *)rsp;
        }
        
        if([((KKModelOilStationListRsp *)rsp).result.KKArrayFieldName(data, KKModelOilStation) count] < ((KKModelOilStationListRsp *)rsp).result.pageinfo.pnums)
        {
            _isEnd = YES;
            self.oilStationListRsp.resultcode = @"205"; //请求完成
        }
        [_tableView reloadData];
    }
    else
    {
        self.oilStationListRsp.resultcode = ((KKModelOilStationListRsp *)rsp).resultcode;
        _isEnd = YES;
    }
    return KKNumberResultEnd;
}

-(void) sortStations:(NSInteger) index OilStationListRsp:(KKModelOilStationListRsp *)rsp
{
    if(rsp)
    {
        switch (index) {
            case 0:
            {
                //按距离排序
                [rsp.result.data__KKModelOilStation sortUsingComparator:^NSComparisonResult(id obj1, id obj2) {
                    KKModelOilStation *station1 = (KKModelOilStation *)obj1;
                    KKModelOilStation *station2 = (KKModelOilStation *)obj2;
                    NSInteger distance1 = station1.distance;
                    NSInteger distance2 = station2.distance;
                    if(distance1 < distance2)
                    {
                        return NSOrderedAscending;
                    }
                    else if(distance1 > distance2)
                    {
                        return NSOrderedDescending;
                    }
                    else
                    {
                        return NSOrderedSame;
                    }
                }];
            }
                break;
            case 1:
            {
                //按价格排序
                [rsp.result.data__KKModelOilStation sortUsingComparator:^NSComparisonResult(id obj1, id obj2) {
                    KKModelOilStation *station1 = (KKModelOilStation *)obj1;
                    KKModelOilStation *station2 = (KKModelOilStation *)obj2;
                    float price1 = [station1.price.E93 floatValue];
                    float price2 = [station2.price.E93 floatValue];;
                    if(price1 < price2)
                    {
                        return NSOrderedAscending;
                    }
                    else if(price1 > price2)
                    {
                        return NSOrderedDescending;
                    }
                    else
                    {
                        return NSOrderedSame;
                    }
                }];
            }
                break;
            default:
                break;
        }
    }
}

#pragma mark -
#pragma mark KKServiceSegmentControlDelegate
-(void) KKServiceSegmentControlSegmentChanged:(NSInteger)index
{
    [self sortStations:index OilStationListRsp:self.oilStationListRsp];
    
    [_tableView reloadData];
    [_tableView scrollRectToVisible:CGRectMake(0, 0, 1, 1) animated:NO];
}

#pragma mark -
#pragma mark UITableViewDelegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 96;
}
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.oilStationListRsp.result.KKArrayFieldName(data, KKModelOilStation) count];
}

-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identifier = @"oilStationCell";
    KKOilStationTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if(cell == nil)
    {
        cell = [[[KKOilStationTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier] autorelease];
    }
    [cell setDataAndRefresh:[self.oilStationListRsp.result.KKArrayFieldName(data, KKModelOilStation) objectAtIndex:indexPath.row]];
    
    return cell;
}

-(void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [[NSNotificationCenter defaultCenter] postNotificationName:@"OilStationListClicked" object:nil userInfo:[NSDictionary dictionaryWithObjectsAndKeys:((KKModelOilStation *)[self.oilStationListRsp.result.data__KKModelOilStation objectAtIndex:indexPath.row]).id,@"oilStationID", nil]];
    
    [self backButtonClicked];
//    KKOilStationMapViewController *mapVc = [[KKOilStationMapViewController alloc] init];
//    mapVc.oilStationListRsp = self.oilStationListRsp;
//    mapVc.currentStation = (KKModelOilStation *)[mapVc.oilStationListRsp.result.data__KKModelOilStation objectAtIndex:indexPath.row];
//    [self.navigationController pushViewController:mapVc animated:YES];
//    [mapVc release];
}

#pragma mark -
#pragma mark - UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    
    CGPoint offset = scrollView.contentOffset;
    CGSize size = scrollView.frame.size;
    CGSize contentSize = scrollView.contentSize;
    float yMargin = offset.y + size.height - contentSize.height;
    if (!_isLoading && yMargin > -60 && contentSize.height > scrollView.bounds.size.height)
    {
        [self loadDataBegin];
    }
    
}
//-(void) scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
//{
//    if(scrollView.contentOffset.y > scrollView.contentSize.height-scrollView.frame.size.height)
//    {
//        [self loadDataBegin];
//    }
//    else{
//        [_tableView reloadData];
//    }
//}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) dealloc
{
    self.oilStationListRsp = nil;
    [super dealloc];
}
@end
