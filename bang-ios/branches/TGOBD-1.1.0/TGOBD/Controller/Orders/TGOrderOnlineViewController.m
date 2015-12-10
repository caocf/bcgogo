//
//  TGOrderOnlineViewController.m
//  TGOBD
//
//  Created by James Yu on 14-3-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGOrderOnlineViewController.h"
#import "TGCustomDropDownListView.h"
#import "TGDataSingleton.h"
#import "TGHTTPRequestEngine.h"
#import "NSDate+millisecond.h"
#import "TGShopInfoViewController.h"
#import "TGAppDelegate.h"

@interface TGOrderOnlineViewController ()

@property (nonatomic, assign) float offset;

@end

@implementation TGOrderOnlineViewController

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
	
    [self initComopents];
    [self setnavigationBar];
    [self initVariable];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Custom Methods

- (void)initComopents
{
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar];
    
    _scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, originY, 320, [self getViewHeightWithNavigationBar])];
    _scrollView.scrollEnabled = YES;
    _scrollView.bounces = NO;
    
    originY = 10;
    
    _shopName = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"服务店面:" placeholder:@"" rightTitle:@"" rightImage:nil];
    _shopName.textField.enabled = NO;
    _shopName.textField.textColor = [UIColor blueColor];
    
    UIButton *shopInfoBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 320, 50)];
    shopInfoBtn.backgroundColor = [UIColor clearColor];
    [shopInfoBtn addTarget:self action:@selector(getShopInfo) forControlEvents:UIControlEventTouchUpInside];
    [_shopName addSubview:shopInfoBtn];
    
    originY += 50;
    
    _vehicleNo = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"车牌号码:" placeholder:@"" rightTitle:@"" rightImage:nil];
    _vehicleNo.textField.enabled = NO;
    
    originY += 50;
    
    _serviceType = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"服务类型:" placeholder:@"请选择服务类型" rightTitle:@"" rightImage:nil];
    _serviceType.textField.enabled = NO;
    _serviceType.textField.text = @"保养";
    UIButton *serviceBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 320, 50)];
    serviceBtn.backgroundColor = [UIColor clearColor];
    [serviceBtn addTarget:self action:@selector(selectServiceTye) forControlEvents:UIControlEventTouchUpInside];
    [_serviceType addSubview:serviceBtn];
    //按钮上的下拉小图标
    UIImageView *arrowDown = [[UIImageView alloc] initWithFrame:CGRectMake(284, 22, 13, 7)];
    arrowDown.image = [UIImage imageNamed:@"icon_arrow_down.png"];
    [serviceBtn addSubview:arrowDown];
    
    originY += 50;
    
    _appointTime = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"预约时间:" placeholder:@"请选择预约时间" rightTitle:@"" rightImage:[UIImage imageNamed:@"icon_calendar.png"]];
    _appointTime.textField.enabled = NO;
    UIButton *appointBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 320, 50)];
    appointBtn.backgroundColor = [UIColor clearColor];
    [appointBtn addTarget:self action:@selector(selectAppointTime) forControlEvents:UIControlEventTouchUpInside];
    [_appointTime addSubview:appointBtn];
    
    originY += 50;
    
    _contact = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"联系人:" placeholder:@"请输入联系人" rightTitle:@"" rightImage:nil];
    _contact.textField.delegate = self;
    
    originY += 50;
    
    _mobile = [[TGCustomTextFieldView alloc] initWithFrame:CGRectMake(0, originY, 320, 50) leftTitle:@"联系方式:" placeholder:@"请输入联系方式" rightTitle:@"" rightImage:nil];
    _mobile.textField.delegate = self;
    
    originY += 50;
    
    UIImageView *imageView = [self createTextViewWithFrame:CGRectMake(0, originY, 320, 110)];
    
    originY += 120;
    
    [_scrollView addSubview:_shopName];
    [_scrollView addSubview:_vehicleNo];
    [_scrollView addSubview:_serviceType];
    [_scrollView addSubview:_appointTime];
    [_scrollView addSubview:_contact];
    [_scrollView addSubview:_mobile];
    [_scrollView addSubview:imageView];
    
    [_scrollView setContentSize:CGSizeMake(320, originY > [self getViewHeightWithNavigationBar] ? originY : [self getViewHeightWithNavigationBar])];
    
    [self.view addSubview:_scrollView];

}

- (UIImageView *)createTextViewWithFrame:(CGRect)frame
{
    UIImageView *imageView = [[UIImageView alloc] initWithFrame:frame];
    imageView.image = [[UIImage imageNamed:@"bg_textField.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:20];
    imageView.userInteractionEnabled = YES;
    
    UILabel *lbl = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 105, 50)];
    lbl.text = @"备注:";
    lbl.font = [UIFont systemFontOfSize:15];
    lbl.backgroundColor = [UIColor clearColor];
    lbl.textAlignment = NSTextAlignmentRight;
    lbl.textColor = [UIColor grayColor];
    lbl.numberOfLines = 2;
    lbl.lineBreakMode = NSLineBreakByWordWrapping;
    lbl.minimumFontSize = 7;
    
    _remark = [[UITextView alloc] initWithFrame:CGRectMake(113, 10, 170, 90)];
    _remark.delegate = self;
    _remark.font = [UIFont systemFontOfSize:17];
    _remark.layer.borderColor = [UIColor colorWithRed:222/255.0 green:222/255.0 blue:222/255.0 alpha:1].CGColor;
    _remark.layer.borderWidth = 1;
    _remark.layer.cornerRadius = 4.0;
    
    [imageView addSubview:lbl];
    [imageView addSubview:_remark];
    
    return imageView;
}

- (void)setnavigationBar
{
    [self setNavigationTitle:@"在线预约"];
    self.navigationItem.rightBarButtonItem = [TGViewUtils createNavigationBarButtonItemWithTitle:@"预约" bgImage:nil target:self action:@selector(commitOrder)];
}

- (void)getShopInfo
{
    [TGAppDelegateSingleton.rootViewController pushViewController:[[TGShopInfoViewController alloc] init] animated:YES];
}

- (void)selectAppointTime
{
    TGCustomDataPickerView *picker = [[TGCustomDataPickerView alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    picker.delegate = self;
    [picker show];
    [self hideKeyboard];
}

- (void)selectServiceTye
{
    CGPoint point = _serviceType.textField.frame.origin;
    
    point = [_serviceType convertPoint:point toView:_scrollView];
    
    TGCustomDropDownListView *listView = [[TGCustomDropDownListView alloc] initWithFrame:CGRectMake(point.x, point.y + 24, 153, 140)];
    listView.delegate = self;
    
    [_scrollView addSubview:listView];
    [self hideKeyboard];
}

- (void)initVariable
{
    TGDataSingleton *singleton = [TGDataSingleton sharedInstance];
    
    _shopName.textField.text = singleton.shopInfo.name;
    _vehicleNo.textField.text = singleton.vehicleInfo.vehicleNo;
    _mobile.textField.text = singleton.userInfo.mobile;
}

- (void)commitOrder
{
    if (_serviceType.textField.text.length == 0) {
        [TGAlertView showAlertViewWithTitle:nil message:@"请选择服务类型"];
        return;
    }
    
    if (_appointTime.textField.text.length == 0) {
        [TGAlertView showAlertViewWithTitle:nil message:@"请选择预约时间"];
        return;
    }
    
    if (_contact.textField.text.length == 0) {
        [TGAlertView showAlertViewWithTitle:nil message:@"请输入联系人"];
        return;
    }
    
    if (_mobile.textField.text == nil) {
        [TGAlertView showAlertViewWithTitle:nil message:@"请输入联系方式"];
        return;
    }
    
    if (![TGHelper isValidateMobile:_mobile.textField.text]) {
        [TGAlertView showAlertViewWithTitle:nil message:@"请输入正确的手机号码"];
        return;
    }
    
    NSDictionary *serviceType = @{@"维修": @"10000010001000001",
                                  @"保养": @"10000010001000002",
                                  @"美容": @"10000010002000001",
                                  };
    TGDataSingleton *singleton = [TGDataSingleton sharedInstance];
    
    [TGProgressHUD show];
    
    [[TGHTTPRequestEngine sharedInstance] orderOnline:singleton.userInfo.userNo serviceCategoryId:[[serviceType objectForKey:_serviceType.textField.text] longLongValue] appointTime:[NSDate timeIntervalSince1970WithMillisecondFromString:_appointTime.textField.text formatter:nil] mobile:_mobile.textField.text contact:_contact.textField.text shopId:singleton.shopInfo.id vehicleNo:singleton.vehicleInfo.vehicleNo remark:_remark.text faultInfoItems:_faultInfoItems viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if ([self httpResponseCorrect:responseObject]) {
            [self.navigationController popViewControllerAnimated:YES];
            TGModelOrderLineRsp *rsp = (TGModelOrderLineRsp *)responseObject;
            [TGAlertView showAlertViewWithTitle:nil message:rsp.header.message];
        }
    } failure:self.faultBlock];
}

- (void)hideKeyboard
{
    [self.view endEditing:YES];
}

#pragma mark - CustomDataPicker delegate

- (void)TGDataPickerSelected:(NSDate *)timeDate
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd HH:mm"];
    NSString *time = [formatter stringFromDate:timeDate];
    
    _appointTime.textField.text = time;
}

#pragma mark - CustomDropDownList delegate

- (void)TGCustomDropDownListSelected:(NSString *)selectedValue
{
    _serviceType.textField.text = selectedValue;
}

#pragma mark - UITextField delegate

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    CGPoint point = [textField convertPoint:textField.frame.origin toView:[[UIApplication sharedApplication] keyWindow]];
    
    float height = [[UIScreen mainScreen] bounds].size.height;
    
    _offset = (height - point.y - 50) > 216 ? 0 : 216 - (height - point.y - 50);
    
    if (_offset > 0) {
        [_scrollView setContentOffset:CGPointMake(_scrollView.contentOffset.x, _scrollView.contentOffset.y + _offset) animated:NO];
        [_scrollView setContentSize:CGSizeMake(_scrollView.contentSize.width, _scrollView.contentSize.height + _offset + 40)];
    }
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    if (_offset > 0) {
        [_scrollView setContentOffset:CGPointMake(_scrollView.contentOffset.x, _scrollView.contentOffset.y - _offset) animated:NO];
        [_scrollView setContentSize:CGSizeMake(_scrollView.contentSize.width, _scrollView.contentSize.height - _offset - 40)];
    }
    
    if (textField == _mobile.textField) {
        textField.text = [TGHelper stringWithNoSpaceAndNewLine:textField.text];
    }
}

#pragma mark - UITextView delegate

- (void)textViewDidBeginEditing:(UITextView *)textView
{
    CGPoint point = [textView convertPoint:textView.frame.origin toView:[[UIApplication sharedApplication] keyWindow]];
    
    float height = [[UIScreen mainScreen] bounds].size.height;
    
    _offset = (height - point.y - 50) > 216 ? 0 : 216 - (height - point.y - 50);
    
    if (_offset > 0) {
        [_scrollView setContentOffset:CGPointMake(_scrollView.contentOffset.x, _scrollView.contentOffset.y + _offset) animated:NO];
        [_scrollView setContentSize:CGSizeMake(_scrollView.contentSize.width, _scrollView.contentSize.height + _offset + 60)];
    }
}

- (void)textViewDidEndEditing:(UITextView *)textView
{
    if (_offset > 0) {
        [_scrollView setContentOffset:CGPointMake(_scrollView.contentOffset.x, _scrollView.contentOffset.y - _offset) animated:NO];
        [_scrollView setContentSize:CGSizeMake(_scrollView.contentSize.width, _scrollView.contentSize.height - _offset - 60)];
    }
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text
{
    if ([text isEqualToString:@"\n"]) {
        [textView resignFirstResponder];
    }
    return YES;
}

@end
