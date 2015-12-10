//
//  TGDataStatisticViewController.m
//  TGOBD
//
//  Created by James Yu on 14-4-28.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGDataStatisticViewController.h"
#import "TGDataListView.h"
#import "TGLineChartView.h"
#import "SMPageControl.h"

#define TAB_BUTTON_COLOR TGRGBA(3, 138, 176, 1)

typedef enum {
    totalMileage = 1000,
    totalOil,
    avgOil,
    totalCost
}tabBtnTag;

@interface TGDataStatisticViewController ()

@property (nonatomic, strong) TGDataListView *dataListView;
@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) SMPageControl *pageControl;
@property (nonatomic, strong) NSMutableArray *titleArray;
@property (nonatomic, strong) NSMutableArray *oilArray;
@property (nonatomic, strong) NSMutableArray *mileageArray;
@property (nonatomic, strong) NSMutableArray *avgOilArray;
@property (nonatomic, strong) NSMutableArray *costArray;
@property (nonatomic, strong) UIImageView *noDataImgView1;
@property (nonatomic, strong) UIImageView *noDataImgView2;
@property (nonatomic, strong) UIImageView *tabLine;

@end

@implementation TGDataStatisticViewController

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
    
    [self initComponents];
    [self initVariable];
    [self httpRequest];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Custom Method

- (void)initVariable
{
    _oilArray = [[NSMutableArray alloc] init];
    _mileageArray = [[NSMutableArray alloc] init];
    _avgOilArray = [[NSMutableArray alloc] init];
    _costArray = [[NSMutableArray alloc] init];
    _titleArray = [[NSMutableArray alloc] init];
}

- (void)initComponents
{
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar];
    CGFloat height = [self getViewHeightWithNavigationBar];
    //第一页内容
    _segmentView = [[TGCustomSegmentView alloc] initWithFrame:CGRectMake(0, originY, screenWidth, height) segmentTitles:@[@"统计",@"图表"]];
    _segmentView.delegate = self;
    _segmentView.scrollView.backgroundColor = [UIColor whiteColor];
    
    _dataListView = [[TGDataListView alloc] initWithFrame:CGRectMake(0, 0, screenWidth, _segmentView.scrollView.frame.size.height)];
    
    //添加无数据图片
    UIImage *img = [UIImage imageNamed:@"no_data.png"];
    _noDataImgView1 = [[UIImageView alloc] initWithImage:img];
    _noDataImgView1.frame = CGRectMake((320-img.size.width)*0.5, 60, img.size.width, img.size.height);
    
    _noDataImgView2 = [[UIImageView alloc] initWithImage:img];
    _noDataImgView2.frame = CGRectMake((320-img.size.width)*0.5 + screenWidth, 60, img.size.width, img.size.height);
    
    [_segmentView.scrollView addSubview:_dataListView];
    [_segmentView.scrollView addSubview:_noDataImgView1];
    [_segmentView.scrollView addSubview:_noDataImgView2];
    [self.view addSubview:_segmentView];
}

- (UILabel *)createTagLabelWithTitle:(NSString *)title center:(CGPoint)center
{
    UILabel *lbl = [[UILabel alloc] initWithFrame:CGRectMake(1, 1, 60, 20)];
    lbl.text = title;
    lbl.backgroundColor = [UIColor redColor];
    lbl.font = [UIFont systemFontOfSize:14];
    lbl.center = center;
    lbl.textAlignment = NSTextAlignmentCenter;
    return lbl;
}

- (void)pageValueChanged
{
    NSInteger n = _pageControl.currentPage;
    _scrollView.contentOffset = CGPointMake(n * screenWidth, 0);
}

- (void)httpRequest
{
    [TGProgressHUD show];
    
    __weak TGDataStatisticViewController *weakSelf = self;
    
    [[TGHTTPRequestEngine sharedInstance] getDriveStatistic:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if ([weakSelf httpResponseCorrect:responseObject]) {
            //设置tableview 数据
            TGModelDriveStatisticRsp *rsp = (TGModelDriveStatisticRsp *)responseObject;
            
            if ([rsp.monthStats__TGModelDriveStatisticInfo count]) {
                
                [weakSelf.noDataImgView1 removeFromSuperview];
                [weakSelf.noDataImgView2 removeFromSuperview];
                
                //拆分各个数据源
                for (TGModelDriveStatisticInfo *info in rsp.monthStats__TGModelDriveStatisticInfo) {
                    [weakSelf.titleArray addObject:[NSString stringWithFormat:@"%d月", info.statMonth]];
                    [weakSelf.oilArray addObject:[NSNumber numberWithDouble:info.oilCost]];
                    [weakSelf.mileageArray addObject:[NSNumber numberWithDouble:info.distance]];
                    [weakSelf.avgOilArray addObject:[NSNumber numberWithDouble:info.oilWear]];
                    [weakSelf.costArray addObject:[NSNumber numberWithDouble:info.oilMoney]];
                    
                    [weakSelf.dataListView.dataSource insertObject:info atIndex:0];
                }
                //加入一年的统计
                [weakSelf.dataListView.dataSource insertObject:rsp.yearStat atIndex:0];
                
                [weakSelf.dataListView.tableView reloadData];
                
                [weakSelf createLineChart];
            }
        }
    } failure:weakSelf.faultBlock];
}

- (void)createLineChart
{
    //第二页内容
    CGFloat originY = 0;
    
    CGFloat width = screenWidth/4;
    
    UIButton *totalMileageBtn = [[UIButton alloc] initWithFrame:CGRectMake(0 + screenWidth, originY, width, 40)];
    [totalMileageBtn setBackgroundColor:[UIColor clearColor]];
    [totalMileageBtn setTitleColor:COLOR_TEXTLEFT_6C6C6C forState:UIControlStateNormal];
    [totalMileageBtn setTitle:@"总里程" forState:UIControlStateNormal];
    [totalMileageBtn addTarget:self action:@selector(tabButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    totalMileageBtn.tag = totalMileage;
    [self tabButtonClicked:totalMileageBtn];
    
    UIButton *totalOilBtn = [[UIButton alloc] initWithFrame:CGRectMake(width + screenWidth, originY, width, 40)];
    [totalOilBtn setBackgroundColor:[UIColor clearColor]];
    [totalOilBtn setTitleColor:COLOR_TEXTLEFT_6C6C6C forState:UIControlStateNormal];
    [totalOilBtn setTitle:@"总油耗" forState:UIControlStateNormal];
    [totalOilBtn addTarget:self action:@selector(tabButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    totalOilBtn.tag = totalOil;
    
    UIButton *avgOilBtn = [[UIButton alloc] initWithFrame:CGRectMake(width * 2 + screenWidth, originY, width, 40)];
    [avgOilBtn setBackgroundColor:[UIColor clearColor]];
    [avgOilBtn setTitleColor:COLOR_TEXTLEFT_6C6C6C forState:UIControlStateNormal];
    [avgOilBtn setTitle:@"平均油耗" forState:UIControlStateNormal];
    [avgOilBtn addTarget:self action:@selector(tabButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    avgOilBtn.tag = avgOil;
    
    UIButton *totalCostBtn = [[UIButton alloc] initWithFrame:CGRectMake(width * 3 + screenWidth, originY, width, 40)];
    [totalCostBtn setBackgroundColor:[UIColor clearColor]];
    [totalCostBtn setTitleColor:COLOR_TEXTLEFT_6C6C6C forState:UIControlStateNormal];
    [totalCostBtn setTitle:@"总花费" forState:UIControlStateNormal];
    [totalCostBtn addTarget:self action:@selector(tabButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    totalCostBtn.tag = totalCost;
    
    [_segmentView.scrollView addSubview:totalMileageBtn];
    [_segmentView.scrollView addSubview:totalOilBtn];
    [_segmentView.scrollView addSubview:avgOilBtn];
    [_segmentView.scrollView addSubview:totalCostBtn];
    
    //添加下面的line
    
    UILabel *lineLbl = [[UILabel alloc] initWithFrame:CGRectMake(0 + screenWidth, originY + 40 + 2, screenWidth, 1)];
    lineLbl.backgroundColor = COLOR_LAYER_BORDER;
    
    _tabLine = [[UIImageView alloc] initWithFrame:CGRectMake(0 + screenWidth, originY + 40, width, 3)];
    _tabLine.image = [UIImage imageNamed:@"icon_tab_line.png"];
    [_segmentView.scrollView addSubview:lineLbl];
    [_segmentView.scrollView addSubview:_tabLine];
    
    
    originY += 50;
    
    _scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(screenWidth, originY, screenWidth, _segmentView.scrollView.frame.size.height - 60)];
    _scrollView.scrollEnabled = NO;
    _scrollView.delegate = self;
    _scrollView.bounces = NO;
    [_scrollView setContentSize:CGSizeMake(screenWidth * 4, _scrollView.frame.size.height)];
    
    TGLineChartView *mileageLine = [[TGLineChartView alloc] initWithFrame:CGRectMake(4, 0, screenWidth-8, _scrollView.frame.size.height) dataArray:_mileageArray titleArray:_titleArray Ytitle:@"KM"];
    [_scrollView addSubview:mileageLine];

    TGLineChartView *oilLine = [[TGLineChartView alloc] initWithFrame:CGRectMake(screenWidth + 4, 0, screenWidth-8, _scrollView.frame.size.height) dataArray:_oilArray titleArray:_titleArray Ytitle:@"L"];
    [_scrollView addSubview:oilLine];
    
    TGLineChartView *avgOilLine = [[TGLineChartView alloc] initWithFrame:CGRectMake(screenWidth * 2 +4, 0, screenWidth-8, _scrollView.frame.size.height) dataArray:_avgOilArray titleArray:_titleArray Ytitle:@"L/100KM"];
    [_scrollView addSubview:avgOilLine];
    
    TGLineChartView *moneyLine = [[TGLineChartView alloc] initWithFrame:CGRectMake(screenWidth *3 +4, 0, screenWidth-8, _scrollView.frame.size.height) dataArray:_costArray titleArray:_titleArray Ytitle:@"元"];
    [_scrollView addSubview:moneyLine];
    
    [_segmentView.scrollView addSubview:_scrollView];

}

- (void)tabButtonClicked:(id)sender
{
    UIButton *btn = (UIButton *)sender;
    
    static UIButton *tmpBtn;
    
    if (tmpBtn == nil) {
        tmpBtn = btn;
        [btn setTitleColor:TAB_BUTTON_COLOR forState:UIControlStateNormal];
    }
    else
    {
        [btn setTitleColor:TAB_BUTTON_COLOR forState:UIControlStateNormal];
        [tmpBtn setTitleColor:COLOR_TEXTLEFT_6C6C6C forState:UIControlStateNormal];
        tmpBtn = btn;
    }
    
    NSInteger tag = btn.tag;
    
    NSInteger offset = 0;
    CGFloat originX = 0 + screenWidth;
    
    switch (tag) {
        case totalMileage:
            originX += 0;
            offset = 0;
            break;
        case totalOil:
            originX += screenWidth/4 * 1;
            offset = screenWidth * 1;
            break;
        case avgOil:
            originX += screenWidth/4 * 2;
            offset = screenWidth * 2;
            break;
        case totalCost:
            originX += screenWidth/4 * 3;
            offset = screenWidth * 3;
            break;
            
        default:
            break;
    }
    
    [UIView beginAnimations:@"xx" context:nil];
    [UIView setAnimationDuration:0.3f];
    [_tabLine setFrame:CGRectMake(originX, _tabLine.frame.origin.y, _tabLine.frame.size.width, _tabLine.frame.size.height)];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseIn];
    [UIView commitAnimations];
    
    _scrollView.contentOffset = CGPointMake(offset, 0);
}

#pragma mark - TGCustomSegmentView delegate

- (void)TGCustomSegementViewDidChange:(NSInteger)currentPage
{
    
}

#pragma mark - UIScrollView delegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{

}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    NSInteger n = _scrollView.contentOffset.x / (screenWidth);
    _pageControl.currentPage = n;
}

@end
