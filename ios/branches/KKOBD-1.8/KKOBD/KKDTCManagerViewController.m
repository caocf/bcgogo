//
//  KKDTCManagerViewController.m
//  KKOBD
//
//  Created by Jiahai on 14-2-7.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "KKDTCManagerViewController.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKTBDictFault.h"
#import "KKDB.h"
#import "KKTBDTCMessage.h"
#import "KKProtocolEngine.h"
#import "KKAppDelegate.h"
#import "KKApplicationDefine.h"
#import "KKModelComplex.h"
#import "MBProgressHUD.h"
#import "KKError.h"
#import "KKCustomAlertView.h"
#import "KKGlobal.h"
#import "UATitledModalPanel.h"
#import "KKShopQueryViewController.h"

@interface KKDTCManagerViewController ()
@property (nonatomic, retain) NSIndexPath           *currentIndexPath;
@property (nonatomic, retain) NSMutableArray        *historyDTCArray;       //历史故障
@property (nonatomic, retain) KKModelPagerInfo      *pager;                 //分页信息
@end

#define FaultCodeStatus_Fixed       @"FIXED"
#define FaultCodeListPageSize       10

@implementation KKDTCManagerViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    [self setVcEdgesForExtendedLayout];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateDtcArray) name:@"updateDtcArrayNotication" object:nil];
    
    [self initComponents];
    
    _isLoading = NO;
    _enableRefresh = YES;
    self.historyDTCArray = [[[NSMutableArray alloc] init] autorelease];
    
    _dataArray = [[NSMutableArray alloc] init];
    [self getDtcMessages];
}

-(void) initComponents
{
    [self initTitleView];
    
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"故障查询";
    
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
    
    _createOrderBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [_createOrderBtn setFrame:CGRectMake(0, 0, 46, 32)];
    _createOrderBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    [_createOrderBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [_createOrderBtn setTitleColor:[UIColor blackColor] forState:UIControlStateHighlighted];
    [_createOrderBtn setTitle:@"预约" forState:UIControlStateNormal];
    [_createOrderBtn setBackgroundImage:[UIImage imageNamed:@"icon_fgpwBtn.png"] forState:UIControlStateNormal];
    [_createOrderBtn addTarget:self action:@selector(createOrderOnline) forControlEvents:UIControlEventTouchUpInside];
    
    self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc] initWithCustomView:_createOrderBtn] autorelease];
    
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:self.view.bounds];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor clearColor];
    bgImv.image = [UIImage imageNamed:@"bg_serviceSeeking.png"];
    [self.view addSubview:bgImv];
    [bgImv release];
    
    _segmentControl = [[KKServiceSegmentControl alloc] initWithFrame:CGRectMake(0, 0, 320, 35)];
    _segmentControl.delegate = self;
    _segmentControl.type = KKServiceSegmentControlType_DTCManager;
    [_segmentControl updateInfo];
    [self.view addSubview:_segmentControl];
    [_segmentControl release];
    
    _mainTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 35, 320, currentScreenHeight - 44 - 49 - 35 - [self getOrignY]) style:UITableViewStylePlain];
    _mainTableView.delegate = self;
    _mainTableView.dataSource = self;
    _mainTableView.backgroundColor = [UIColor clearColor];
    _mainTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_mainTableView];
    [_mainTableView release];
    
    _refreshHeaderView = [[EGORefreshTableHeaderView alloc] initWithFrame:CGRectMake(0, -_mainTableView.bounds.size.height, _mainTableView.bounds.size.width, _mainTableView.bounds.size.height)];
    _refreshHeaderView.delegate = self;
    [_mainTableView addSubview:_refreshHeaderView];
    [_refreshHeaderView release];
    
//    [_refreshHeaderView refreshLastUpdatedDate];
}

- (void)getDtcMessages
{
    [_dataArray removeAllObjects];
    
    KKTBDTCMessage *dtcTb = [[KKTBDTCMessage alloc] initWithDB:[KKDB sharedDB]];
    [_dataArray addObjectsFromArray:[dtcTb getDTCMessageByUserNo:[KKProtocolEngine sharedPtlEngine].userName vehicleModelId:KKAppDelegateSingleton.currentVehicle.vehicleModelId]];
    [dtcTb release];
    
    KKTBDictFault *faultDict = [[KKTBDictFault alloc] initWithDB:[KKDB sharedDB]];
    for (KKModelDTCMessage *message in _dataArray)
    {
        NSArray *arr = [faultDict getFaultInfoWithCode:message.faultCode vehicleModelId:message.vehicleModelId];
        if ([arr count] == 0)
        {
            KKModelFaultCodeInfo *info = [[KKModelFaultCodeInfo alloc] init];
            info.description = @"未知故障";
            arr = [NSArray arrayWithObjects:info, nil];
            [info release];
        }
        message.desArray = arr;
    }
    [faultDict release];
    
    [self refreshView];
}

-(void) getHistoryFixedDTCList:(NSInteger)aPageNo
{
    _isLoading = YES;
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    [[KKProtocolEngine sharedPtlEngine] vehicleFaultCodeList:FaultCodeStatus_Fixed pageNo:aPageNo pageSize:FaultCodeListPageSize delegate:self];
}

-(void) reGetDTCList
{
    self.currentIndexPath = nil;
    
    [self getDtcMessages];
}

-(void) reGetHistoryFixedDTCList
{
    _reGetFlag = YES;
    
    [self getHistoryFixedDTCList:1];
}

-(void) refreshView
{
    [_mainTableView reloadData];
    
    NSArray *array = [self getCurrentDataArray];
    if([array count] == 0)
    {
        if(_noDataImageView == nil)
        {
            _noDataImageView = [[UIImageView alloc] initWithFrame:CGRectMake(100, 60, 120, 90)];
            _noDataImageView.image = [UIImage imageNamed:@"icon_dtcManager_nodata.png"];
            [_mainTableView addSubview:_noDataImageView];
            [_noDataImageView release];
        }
    }
    else
    {
        if(_noDataImageView)
        {
            [_noDataImageView removeFromSuperview];
            _noDataImageView = nil;
        }
    }
    
    if(_refreshHeaderView)
        [_refreshHeaderView egoRefreshScrollViewDataSourceDidFinishedLoading:_mainTableView];
}

#pragma mark - Event
-(void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

-(void) updateDtcArray
{
    [self getDtcMessages];
}

-(void)createOrderOnline
{
    KKShopQueryViewController *Vc = [[KKShopQueryViewController alloc] initWithNibName:@"KKShopQueryViewController" bundle:nil];
    Vc.serviceTypeKey = @"服务范围";
    //NSMutableString *str = [[NSMutableString alloc] init];
    NSMutableArray *array = [[NSMutableArray alloc] init];
    for(KKModelDTCMessage *dtc in _dataArray)
    {
        NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
        [dict setObject:dtc.faultCode forKey:@"faultCode"];
        [dict setObject:KKAppDelegateSingleton.currentVehicle.vehicleId forKey:@"appVehicleId"];
        [dict setObject:((KKModelFaultCodeInfo *)[dtc.desArray objectAtIndex:0]).description forKey:@"description"];
        
        [array addObject:dict];
        [dict release];
        //[str appendFormat:@"{\"faultCode\":\"%@\",\"appVehicleId\":%@,\"description\":\"%@\"},",dtc.faultCode,KKAppDelegateSingleton.currentVehicle.vehicleId,((KKModelFaultCodeInfo *)[dtc.desArray objectAtIndex:0]).description];
    }
    //[str deleteCharactersInRange:NSMakeRange(str.length-1,1)];
    Vc.dtcMsgArray = array;
    [array release];
    //[str release];
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];

}


//获取当前Segment下的DTC数据
-(NSMutableArray *)getCurrentDataArray
{
    NSMutableArray *array = nil;
    switch (_segmentControl.selectedIndex) {
        case 0:
        {
            array = _dataArray;
        }
            break;
        case 1:
        {
            array = self.historyDTCArray;
        }
            break;
        default:
            break;
    }
    return array;
}

#pragma mark -
#pragma mark KKServiceSegmentControlDelegate
-(void) KKServiceSegmentControlSegmentChanged:(NSInteger)index
{
    self.currentIndexPath = nil;
    
    switch (index) {
        case 0:
        {
            _createOrderBtn.hidden = NO;
        }
            break;
        case 1:
        {
            _createOrderBtn.hidden = YES;
            if(self.pager == nil)
            {
                //第一次进入历史故障，获取历史故障列表
                [self getHistoryFixedDTCList:1];
            }
        }
            break;
        default:
            break;
    }
    
    [self refreshView];
}
#pragma mark -
#pragma mark UITableViewDelegate
-(CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    CGFloat height = 0;
    
    switch (_segmentControl.selectedIndex) {
        case 0:
        {
            height = [BGDTCTableViewCell calculateVehicleConditionTableViewCellHeightWithContent:[_dataArray objectAtIndex:indexPath.row] selected:(self.currentIndexPath && self.currentIndexPath.row == indexPath.row)];
        }
            break;
        case 1:
        {
            height = [BGDTCTableViewCell calculateVehicleConditionTableViewCellHeightWithContent:[self.historyDTCArray objectAtIndex:indexPath.row] selected:(self.currentIndexPath && self.currentIndexPath.row == indexPath.row)];
        }
            break;
        default:
            break;
    }
    
    return height;
}

-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    NSInteger n = 0;
    switch (_segmentControl.selectedIndex) {
        case 0:
        {
            n = [_dataArray count];
        }
            break;
        case 1:
        {
            n = [self.historyDTCArray count];
        }
            break;
        default:
            break;
    }
    return n;
}

-(BGDTCTableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identifier = @"dtcTableCell";
    BGDTCTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if(cell == nil)
    {
        cell = [[[BGDTCTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier] autorelease];
        cell.delegate = self;
    }
    
    KKModelDTCMessage *dtcMsg = nil;
    
    BOOL    isHistory = NO;
    switch (_segmentControl.selectedIndex) {
        case 0:
        {
            dtcMsg = [_dataArray objectAtIndex:indexPath.row];
        }
            break;
        case 1:
        {
            isHistory = YES;
            dtcMsg = [self.historyDTCArray objectAtIndex:indexPath.row];
        }
            break;
        default:
            break;
    }
    NSLog(@"isHistory:%@",isHistory ? @"YES":@"NO");
    [cell setDTCMessage:dtcMsg selected:(self.currentIndexPath && indexPath.row == self.currentIndexPath.row) isHistory:isHistory];
    
    return cell;
}

-(void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSMutableArray *indexPaths = nil;
    if(self.currentIndexPath)
    {
        if(self.currentIndexPath.row != indexPath.row)
        {
            indexPaths = [NSMutableArray arrayWithObjects:self.currentIndexPath,indexPath,nil];
            self.currentIndexPath = indexPath;
        }
        else
        {
            indexPaths = [NSMutableArray arrayWithObject:indexPath];
            self.currentIndexPath = nil;
        }
    }
    else
    {
        indexPaths = [NSMutableArray arrayWithObject:indexPath];
        self.currentIndexPath = indexPath;
    }
    
    //BGDTCTableViewCell *cell = (BGDTCTableViewCell *)[tableView cellForRowAtIndexPath:indexPath];
    
    [tableView reloadRowsAtIndexPaths:indexPaths withRowAnimation:UITableViewRowAnimationAutomatic];
}

#pragma mark -
#pragma mark BGDTCTableViewCellDelegate
-(void) controlBtnClicked:(NSInteger)btnType
{
    KKModelDTCMessage *dtcMsg = nil;
    
    NSMutableArray *currentDataBuf = [self getCurrentDataArray];
    
    if(self.currentIndexPath && currentDataBuf && self.currentIndexPath.row < [currentDataBuf count])
    {
        dtcMsg = [currentDataBuf objectAtIndex:self.currentIndexPath.row];
    }
    
    switch (btnType) {
        case 1:
        {
            //修复按钮
            [MBProgressHUD showHUDAddedTo:self.view animated:YES];
            [[KKProtocolEngine sharedPtlEngine] vehicleFaultCodeOperate:dtcMsg.id errorCode:dtcMsg.faultCode oldStatus:@"UNTREATED" newStatus:@"FIXED" vehicleId:KKAppDelegateSingleton.currentVehicle.vehicleId delegate:self];
        }
            break;
        case 2:
        {
            //背景知识
            if(dtcMsg.desArray && [dtcMsg.desArray count] > 0)
            {
                NSString *str = @"\t";// = ((KKModelFaultCodeInfo *)[dtcMsg.desArray objectAtIndex:0]).backgroundInfo;
                for(KKModelFaultCodeInfo *des in dtcMsg.desArray)
                {
                    if(des.backgroundInfo)
                        str = [NSString stringWithFormat:@"%@%@\r\n",str,des.backgroundInfo];
                }
                
                UATitledModalPanel *panel = [[UATitledModalPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.bounds.size.width, currentScreenHeight - 49 - 44 - [self getOrignY])];
                panel.headerLabel.text = @"背景知识";
                CGRect rect = [panel contentViewFrame];
                rect.origin.x = rect.origin.y = 0;
                UITextView *textView = [[UITextView alloc] initWithFrame:rect];
                textView.backgroundColor = [UIColor clearColor];
                textView.textColor = [UIColor blackColor];
                textView.font = [UIFont systemFontOfSize:16];
                textView.editable = NO;
                textView.text = (str && ![str isEqualToString:@"\t"]) ? str : @"暂无背景知识！";
                [panel.contentView addSubview:textView];
                [textView release];
                
                [self.view addSubview:panel];
                [panel show];
                [panel release];
            }
        }
            break;
        case 3:
        {
            //删除按钮
            [MBProgressHUD showHUDAddedTo:self.view animated:YES];
            [[KKProtocolEngine sharedPtlEngine] vehicleFaultCodeOperate:dtcMsg.id errorCode:dtcMsg.faultCode oldStatus:dtcMsg.status newStatus:@"DELETED" vehicleId:KKAppDelegateSingleton.currentVehicle.vehicleId delegate:self];
        }
            break;
    }
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate

-(NSNumber *) vehicleFaultCodeListResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    _isLoading = NO;
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
        _reGetFlag = NO;
        [self refreshView];
		return KKNumberResultEnd;
	}
    
    KKFaultCodeListRsp *fcListRsp = (KKFaultCodeListRsp *)rsp;
    
    if(_reGetFlag)
    {
        _reGetFlag = NO;
        self.currentIndexPath = nil;
        [self.historyDTCArray removeAllObjects];
    }
    self.pager = fcListRsp.pager;
    
    KKTBDictFault *faultDict = [[KKTBDictFault alloc] initWithDB:[KKDB sharedDB]];
    for(KKModelFaultCodeGetInfo *info in fcListRsp.result__KKModelFaultCodeGetInfo)
    {
        KKModelDTCMessage *dtcMsg = [[KKModelDTCMessage alloc] init];
        dtcMsg.id = info.id;
        dtcMsg.status = info.status;
        dtcMsg.faultCode = info.errorCode;
        dtcMsg.userNo = nil;
        dtcMsg.warnTimeStamp = info.lastOperateTime;
        dtcMsg.vehicleModelId = KKAppDelegateSingleton.currentVehicle.vehicleModelId;
        
        NSArray *arr = [faultDict getFaultInfoWithCode:dtcMsg.faultCode vehicleModelId:dtcMsg.vehicleModelId];
        if ([arr count] == 0)
        {
            KKModelFaultCodeInfo *info = [[KKModelFaultCodeInfo alloc] init];
            info.description = @"未知故障";
            info.category=@"未知";
            info.backgroundInfo = @"暂无背景知识";
            arr = [NSArray arrayWithObjects:info, nil];
            [info release];
        }
        dtcMsg.desArray = arr;
        [self.historyDTCArray addObject:dtcMsg];
    }
    [faultDict release];
    
    if(self.pager.isLastPage)
    {
        _enableRefresh = NO;
    }
    
    if(_segmentControl.selectedIndex == 1)
    {
        [self refreshView];
    }
    
    return KKNumberResultEnd;
}

-(NSNumber *) vehicleFaultCodeOperateResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    
    KKModelProtocolRsp *aRsp = (KKModelProtocolRsp *)rsp;
    if (aRsp.header.code == eRsp_succeed) {
        
        switch (_segmentControl.selectedIndex) {
            case 0:
            {
                NSMutableArray *array = [self getCurrentDataArray];
                
                KKModelDTCMessage *message = [array objectAtIndex:self.currentIndexPath.row];
                KKTBDTCMessage *dtcTb = [[KKTBDTCMessage alloc] initWithDB:[KKDB sharedDB]];
                [dtcTb deleteDTCMessage:message.faultCode WithUserNo:message.userNo];
                [dtcTb release];
                
                [array removeObjectAtIndex:self.currentIndexPath.row];
                
                if([array count] == 0)
                {
                    KKAppDelegateSingleton.dtcWarnning = NO;
                    if(KKAppDelegateSingleton.isConnect)
                    {
                        [KKAppDelegateSingleton updateVehicleCondition:e_CarWell];
                    }
                    else
                    {
                        [KKAppDelegateSingleton updateVehicleCondition:e_CarNotOnLine];
                    }
                }
                else
                {
                    KKAppDelegateSingleton.dtcWarnning = YES;
                }
                self.currentIndexPath = nil;
                [self refreshView];
            }
                break;
            case 1:
            {
                [self reGetHistoryFixedDTCList];
            }
                break;
            default:
                break;
        }
        self.currentIndexPath = nil;
    }
    return KKNumberResultEnd;
}

#pragma mark - UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{

    if(_segmentControl.selectedIndex == 1)
    {
        CGPoint offset = scrollView.contentOffset;
        CGSize size = scrollView.frame.size;
        CGSize contentSize = scrollView.contentSize;
        float yMargin = offset.y + size.height - contentSize.height;
        if (_enableRefresh && !_isLoading && yMargin > -60 && contentSize.height > scrollView.bounds.size.height)
        {
            if(!self.pager.isLastPage)
            {
                [self getHistoryFixedDTCList:self.pager.nextPage];
            }
        }
    }
        
    [_refreshHeaderView egoRefreshScrollViewDidScroll:scrollView];
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    [_refreshHeaderView egoRefreshScrollViewDidEndDragging:scrollView];
}

#pragma mark - EGORefreshTableHeaderDelegate

- (void)egoRefreshTableHeaderDidTriggerRefresh:(EGORefreshTableHeaderView*)view
{
    switch (_segmentControl.selectedIndex) {
        case 0:
        {
            [self reGetDTCList];
            //刷新车况
            dispatch_time_t ckTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC));
            dispatch_after(ckTime, dispatch_get_main_queue(), ^(void){
                [self refreshView];
            });
        }
            break;
        case 1:
        {
            [self reGetHistoryFixedDTCList];
        }
            break;
    }

}
- (BOOL)egoRefreshTableHeaderDataSourceIsLoading:(EGORefreshTableHeaderView*)view
{
    switch (_segmentControl.selectedIndex) {
        case 0:
        {
            return NO;
        }
            break;
        case 1:
        {
            return _isLoading;
        }
            break;
        default:
            return NO;
            break;
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) dealloc
{
    [_dataArray release],_dataArray=nil;
    self.currentIndexPath = nil;
    self.historyDTCArray = nil;
    self.pager = nil;
    [super dealloc];
}

@end
