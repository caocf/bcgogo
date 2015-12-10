//
//  TGVehicleNoEditViewController.m
//  TGOBD
//
//  Created by James Yu on 14-5-4.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGVehicleNoEditViewController.h"

@interface TGVehicleNoEditViewController ()

@property (nonatomic, strong) UILabel *location;
@property (nonatomic, strong) UITextField *vehicleNo;
@property (nonatomic, strong) NSArray *dataSource;

@end

@implementation TGVehicleNoEditViewController

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
    
    [self setNavigationTitle:@"修改车牌号"];
    
    self.navigationItem.rightBarButtonItem = [TGViewUtils createNavigationBarButtonItemWithTitle:@"确定" bgImage:nil target:self action:@selector(commit)];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Custom Method

- (void)initComponents
{
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar] + 10;
    
    _location = [[UILabel alloc] initWithFrame:CGRectMake(20, originY, 80, 40)];
    _location.textAlignment = NSTextAlignmentCenter;
    _location.textColor = [UIColor whiteColor];
    _location.font = [UIFont systemFontOfSize:22];
    _location.backgroundColor = TGRGBA(176, 226, 240, 0.8);
    
    _vehicleNo = [[UITextField alloc] initWithFrame:CGRectMake(100, originY, 200, 40)];
    _vehicleNo.delegate = self;
    _vehicleNo.font = [UIFont systemFontOfSize:22];
    _vehicleNo.layer.borderWidth = 2;
    _vehicleNo.layer.borderColor = TGRGBA(176, 226, 240, 0.8).CGColor;
    
    originY += 60;
    
    _dataSource = @[@"沪",@"苏", @"浙", @"赣", @"鄂", @"桂", @"甘", @"晋", @"蒙", @"陕", @"吉", @"闽",@"贵",@"粤",@"川",@"青", @"藏", @"琼", @"宁", @"渝", @"京", @"津", @"冀", @"豫", @"云", @"辽", @"黑", @"湘", @"皖", @"鲁", @"新"];
    
    CGFloat marginScreen = 20;
    CGFloat width = 35;
    CGFloat height = 35;
    CGFloat originX = marginScreen;
    //一排放8个按钮
    NSInteger btnNum = 7;
    CGFloat margin = (screenWidth - btnNum * width - marginScreen * 2)/(btnNum - 1);
    
    for (int i = 1; i <= [_dataSource count]; i ++ ) {
        UIButton *btn = [[UIButton alloc] initWithFrame:CGRectMake(originX, originY, width, height)];
        [btn setTitle:[_dataSource objectAtIndex:i-1] forState:UIControlStateNormal];
        UIImage *img = [UIImage imageNamed:@"icon_btn_bg.png"];

        btn.tag = i - 1;
        [btn setExclusiveTouch:YES];
        [btn setBackgroundImage:img forState:UIControlStateNormal];
        [btn setBackgroundImage:[UIImage imageNamed:@"icon_nav_rightBtn_active.png"] forState:UIControlStateSelected];
        [btn addTarget:self action:@selector(btnClicked:) forControlEvents:UIControlEventTouchUpInside];
        
        if (i == 2) {
            [self btnClicked:btn];
        }
        
        [self.view addSubview:btn];
        
        if ((i % btnNum) == 0 && i != 0) {
            originY += height + 10;
            originX = marginScreen;
        } else
        {
            originX += margin + width;
        }
    }
    
    [self.view addSubview:_location];
    [self.view addSubview:_vehicleNo];
}

- (void)btnClicked:(id)sender
{
    UIButton *btn = (UIButton *)sender;
    
    static UIButton *tmpBtn;
    
    if (tmpBtn == nil) {
        tmpBtn = btn;
        tmpBtn.selected = YES;
    }
    else
    {
        tmpBtn.selected = NO;
        tmpBtn = btn;
        tmpBtn.selected = YES;
    }
    _location.text = [_dataSource objectAtIndex:tmpBtn.tag];
}

- (void)commit
{
    NSString *vehicleNo = [NSString stringWithFormat:@"%@%@", _location.text, _vehicleNo.text];
    
    if (![TGHelper isValidateVehicleNo:vehicleNo]) {
        [TGAlertView showAlertViewWithTitle:nil message:@"车牌号格式不正确！"];
        return;
    }
    
    if (_delegate && [_delegate respondsToSelector:@selector(passEditVehicleN0:)]) {
        [_delegate passEditVehicleN0:vehicleNo];
    }
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark - UITextField delegate

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField
{
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    [self commit];
    return YES;
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    textField.text = [[TGHelper stringWithNoSpaceAndNewLine:textField.text] uppercaseString];
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    textField.text = [[TGHelper stringWithNoSpaceAndNewLine:textField.text] uppercaseString];
    
    return range.location > 5 ? NO : YES;
}

@end
