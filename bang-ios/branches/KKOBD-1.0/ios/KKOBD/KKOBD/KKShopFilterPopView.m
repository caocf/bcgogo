//
//  KKShopFilterPopView.m
//  KKOBD
//
//  Created by zhuyc on 13-8-22.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKShopFilterPopView.h"

// =======================================================================================
@implementation KKPopMenuItem
@synthesize parentItemId = _parentItemId;
@synthesize itemId = _itemId;
@synthesize title = _title;
@synthesize additional = _additional;

- (id)initWithId:(NSInteger)aItemId parentId:(NSInteger)aParentId title:(NSString*)aTitle others:(id)aOthers;
{
	if (nil == (self = [super init]))
		return self;
	
	_parentItemId = aParentId;
	_itemId = aItemId;
	self.title = aTitle;
	self.additional = aOthers;
	
	return self;
}

- (void)dealloc
{
	self.title = nil;
	self.additional = nil;
    
	[super dealloc];
}

@end

// =======================================================================================

@implementation KKShopFilterPopView
@synthesize popId;
@synthesize popViewDelegate;
@synthesize arrowOrignX = _arrowOrignX;
@synthesize isTwoStep = _isTwoStep;

- (id)initWithFrame:(CGRect)frame WithArrowOrignX:(float)orignX  WithRowHeight:(float)rowHeight
{
    self = [super initWithFrame:frame];
    if (self) {
        
        _leftItemWidth = 140;
        _rightItemWidth = 140;
        
        // Initialization code
        _leftDataArray = [[NSMutableArray alloc] initWithCapacity:20];
        _rightDataArray = [[NSMutableArray alloc] initWithCapacity:20];
        _rowHeight = rowHeight;
        _arrowOrignX = orignX;
        
        _upArrowImv = [[UIImageView alloc] initWithFrame:CGRectZero];
        [self addSubview:_upArrowImv];
        [_upArrowImv release];
        
        _bgImv = [[UIImageView alloc] initWithFrame:CGRectZero];
        _bgImv.userInteractionEnabled = YES;
        
        _leftTableView = [[UITableView alloc] initWithFrame:CGRectZero];
		_leftTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        _leftTableView.backgroundColor = [UIColor clearColor];
		_leftTableView.dataSource = self;
		_leftTableView.delegate = self;
		_leftTableView.showsVerticalScrollIndicator = NO;
        
        [_bgImv addSubview:_leftTableView];
		[_leftTableView release];
        
        _rightTableView = [[UITableView alloc] initWithFrame:CGRectZero];
		_rightTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        _rightTableView.showsVerticalScrollIndicator = NO;
        _rightTableView.backgroundColor = [UIColor clearColor];
		_rightTableView.dataSource = self;
		_rightTableView.delegate = self;
		[_bgImv addSubview:_rightTableView];
		[_rightTableView release];
        
        _lineImv = [[UIImageView alloc] initWithFrame:CGRectZero];
        _lineImv.userInteractionEnabled = YES;
        [_bgImv addSubview:_lineImv];
        [_lineImv release];
        
        [self addSubview:_bgImv];
        [_bgImv release];
        
    }
    self.backgroundColor = [UIColor clearColor];
    [self addTarget:self action:@selector(marginViewClicked) forControlEvents:UIControlEventTouchUpInside];
    return self;
}

- (void)dealloc
{
    self.selectedItemsId = nil;
    [super dealloc];
}

#pragma mark -
#pragma mark Custom methods

- (void)setLeftDataArray:(NSMutableArray *)lArr RightDataArray:(NSMutableArray *)rArr
{
    [_leftDataArray removeAllObjects];
	[_leftDataArray addObjectsFromArray:lArr];
	
	[_rightDataArray removeAllObjects];
	if ([rArr count] > 0)
		[_rightDataArray addObjectsFromArray:rArr];
    
    [self layoutComponents];
    
    if (self.isInit)
    {
        KKPopMenuItem *pitem = (KKPopMenuItem*)[_leftDataArray objectAtIndex:_selectedInFirstList];
        NSMutableArray *childs = [self childMenuItems:pitem.itemId];
        if ([childs count] == 0)
        {
            if (self.popViewDelegate && [(NSObject *)self.popViewDelegate respondsToSelector:@selector(KKShopFilterPopViewLeftCellClickedWithItem:)])
            {
                [self.popViewDelegate KKShopFilterPopViewLeftCellClickedWithItem:pitem];
            }
        }
        self.isInit = NO;
    }
}

- (void)layoutComponents
{
    CGFloat marginTop = 3.5f;
    CGPoint startPoint = CGPointMake(_arrowOrignX, marginTop);
    
    UIImage *image = [UIImage imageNamed:@"icon_shopq_popView_upArrow.png"];
    
    if (!self.arrowHidden)
    {
        
        [_upArrowImv setFrame:CGRectMake(_arrowOrignX, - (image.size.height - marginTop), image.size.width, image.size.height)];
        _upArrowImv.image = image;
    }
    
    if (!_isTwoStep)
    {
        startPoint.x += (0.5*image.size.width - 0.5*_leftItemWidth - 10);
        if (startPoint.x <= 0)
            startPoint.x = marginTop;
        else if ((startPoint.x + _leftItemWidth + 10) >= self.frame.size.width)
            startPoint.x = self.frame.size.width - _leftItemWidth - marginTop;
        
        float h0 = _rowHeight * [_leftDataArray count] + marginTop + 20;
        if (h0 > self.frame.size.height)
            h0 = self.frame.size.height - marginTop;
        
        [_leftTableView setFrame:CGRectMake(10, 10, _leftItemWidth, h0 -20)];
        [_bgImv setFrame:CGRectMake(startPoint.x, startPoint.y, _leftItemWidth + 20, h0)];
        _bgImv.image = [UIImage imageNamed:@"bg_shopq_popView.png"];
        
        if (self.centerView)
            _bgImv.center = self.center;
    }
    else
    {
        float h0 = _rowHeight * [self maxChildItemCount] + marginTop + 20;
//        if (h0 > self.frame.size.height)
            h0 = self.frame.size.height - marginTop;
        
        UIImage *image = [UIImage imageNamed:@"bg_shopq_popView_line.png"];
        float lineWidth = 0.5*(self.frame.size.width - 2*marginTop - _leftItemWidth - _rightItemWidth - 20 - image.size.width);
        
        
        [_leftTableView setFrame:CGRectMake(10, 10, _leftItemWidth, h0 -20)];
        _lineImv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
        [_lineImv setFrame:CGRectMake(10+_leftItemWidth + lineWidth +0.5*image.size.width, 10, image.size.width, h0)];
        [_rightTableView setFrame:CGRectMake(10+_leftItemWidth + lineWidth +0.5*image.size.width + lineWidth, 10, _rightItemWidth, h0 - 20)];
        
        [_bgImv setFrame:CGRectMake(marginTop, marginTop,self.frame.size.width - 2*marginTop, h0)];
        _bgImv.image = [UIImage imageNamed:@"bg_shopq_popView.png"];
    }
    [_leftTableView reloadData];
    [_rightTableView reloadData];
    
    if (self.isTwoStep)
    {
        NSIndexPath *indexPath = [NSIndexPath indexPathForRow:_selectedInFirstList inSection:0];
        UITableViewCell *cell = [_leftTableView cellForRowAtIndexPath:indexPath];
        if (cell)
        {
            NSArray *visibleCells = [_leftTableView visibleCells];
            if (![visibleCells containsObject:cell])
            {
                [_leftTableView scrollRectToVisible:cell.frame animated:YES];
            }
        }
        else
        {
            [_leftTableView scrollRectToVisible:CGRectMake(0, _selectedInFirstList *_rowHeight, _leftItemWidth, _rowHeight) animated:YES];
        }

    }
}

- (NSInteger)maxChildItemCount
{
	NSInteger max = 0;
	for (NSInteger p=0; p<[_leftDataArray count]; p++) {
		KKPopMenuItem *pitem = (KKPopMenuItem*)[_leftDataArray objectAtIndex:p];
		NSInteger count = 0;
		for (NSInteger c=0; c<[_rightDataArray count]; c++) {
			KKPopMenuItem *item = (KKPopMenuItem*)[_rightDataArray objectAtIndex:c];
			if (pitem.itemId == item.parentItemId)
				count++;
		}
		if (max < count)
			max = count;
	}
	return max;
}

- (UITableViewCell *)creatTableViewCellWithTableView:(UITableView *)tableview WithCellWidth:(float)aWidth WithIdentifier:(NSString *)identifier
{
    UITableViewCell *cell = [tableview dequeueReusableCellWithIdentifier:identifier];
    if (cell == nil)
    {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier] autorelease];
        UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, aWidth, _rowHeight)];
        bgImv.userInteractionEnabled = YES;
        bgImv.tag = 100;
        
        UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, aWidth, _rowHeight)];
        label.backgroundColor = [UIColor clearColor];
        label.textColor = [UIColor whiteColor];
        label.font = [UIFont systemFontOfSize:15.f];
        label.textAlignment = UITextAlignmentCenter;
        label.tag = 101;
        [bgImv addSubview:label];
        [label release];
        
        [cell.contentView addSubview:bgImv];
        [bgImv release];
    }
    return cell;
}


- (NSMutableArray *)childMenuItems:(NSInteger)aParentId
{
	NSMutableArray *arr = [[NSMutableArray alloc] initWithCapacity:20];
	for (NSInteger i=0; i<[_rightDataArray count]; i++) {
		KKPopMenuItem *item = (KKPopMenuItem*)[_rightDataArray objectAtIndex:i];
		if (item.parentItemId == aParentId)
			[arr addObject:item];
	}
    return [arr autorelease];
}

- (void)setLeftSelectedIndex:(NSInteger)aLeftSelectedIndex
{
    self.selectedInFirstList = aLeftSelectedIndex;
    self.selectedInSecondList = 0;
    [self layoutComponents];
}

#pragma mark -
#pragma mark Events

- (void)marginViewClicked
{
    [self removeFromSuperview];
}

#pragma mark -
#pragma mark UITableViewDataSource,UITableViewDelegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    NSInteger count = 0;
    
    if (tableView == _leftTableView)
        count =  [_leftDataArray count];
    
    if (tableView == _rightTableView)
    {
        KKPopMenuItem *pitem = (KKPopMenuItem*)[_leftDataArray objectAtIndex:_selectedInFirstList];
        count = [[self childMenuItems:pitem.itemId] count];
    }
    
    return count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = nil;
    if (tableView == _leftTableView)
    {
        cell = [self creatTableViewCellWithTableView:tableView WithCellWidth:_leftItemWidth WithIdentifier:@"popView_leftTableViewCell"];
        UIImageView *imv = (UIImageView *)[cell.contentView viewWithTag:100];
        if (indexPath.row == _selectedInFirstList)
            imv.image = [[UIImage imageNamed:@"bg_shopq_popView_cell.png"] stretchableImageWithLeftCapWidth:0 topCapHeight:0];
        else
            imv.image = nil;
        
        UILabel *label = (UILabel *)[cell.contentView viewWithTag:101];
        KKPopMenuItem *item = (KKPopMenuItem*)[_leftDataArray objectAtIndex:indexPath.row];
		label.text = item.title;
    }
    else
    {
        cell = [self creatTableViewCellWithTableView:tableView WithCellWidth:_rightItemWidth WithIdentifier:@"popView_rightTableViewCell"];
        UIImageView *imv = (UIImageView *)[cell.contentView viewWithTag:100];
        UILabel *label = (UILabel *)[cell.contentView viewWithTag:101];
        
        KKPopMenuItem *pitem = (KKPopMenuItem*)[_leftDataArray objectAtIndex:_selectedInFirstList];
		NSMutableArray *childs = [self childMenuItems:pitem.itemId];
		if (indexPath.row < [childs count]) {
			KKPopMenuItem *item = (KKPopMenuItem*)[childs objectAtIndex:indexPath.row];
			label.text = item.title;
            
            if(self.selectedItemsId)
            {
                if([self.selectedItemsId containsObject:[NSString stringWithFormat:@"%d",item.itemId]])
                    imv.image = [[UIImage imageNamed:@"bg_shopq_popView_cell.png"] stretchableImageWithLeftCapWidth:0 topCapHeight:0];
                else
                    imv.image = nil;
            }
            else
            {
                if (indexPath.row == _selectedInSecondList)
                    imv.image = [[UIImage imageNamed:@"bg_shopq_popView_cell.png"] stretchableImageWithLeftCapWidth:0 topCapHeight:0];
                else
                    imv.image = nil;
            }
        }
    }
    

    cell.backgroundColor  =[UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return _rowHeight;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    KKPopMenuItem *item = nil;
    if (!_isTwoStep)
    {
        item = [_leftDataArray objectAtIndex:indexPath.row];
        [self setLeftSelectedIndex:indexPath.row];
        if (self.popViewDelegate && [(NSObject *)self.popViewDelegate respondsToSelector:@selector(KKShopFilterPopView:WithItem:AndParentItem:)])
            [self.popViewDelegate KKShopFilterPopView:self WithItem:item AndParentItem:nil];
        [self removeFromSuperview];
    }
    else
    {
        if (tableView == _leftTableView)
        {
            [self setLeftSelectedIndex:indexPath.row];
            
            KKPopMenuItem *pitem = (KKPopMenuItem*)[_leftDataArray objectAtIndex:_selectedInFirstList];
            NSMutableArray *childs = [self childMenuItems:pitem.itemId];
            if ([childs count] == 0)
            {
                if (self.popViewDelegate && [(NSObject *)self.popViewDelegate respondsToSelector:@selector(KKShopFilterPopViewLeftCellClickedWithItem:)])
                {
                    [self.popViewDelegate KKShopFilterPopViewLeftCellClickedWithItem:pitem];
                }
            }
            
        }
        
        if (tableView == _rightTableView)
        {
            KKPopMenuItem *pitem = (KKPopMenuItem*)[_leftDataArray objectAtIndex:_selectedInFirstList];
            NSMutableArray *childs = [self childMenuItems:pitem.itemId];
            item = [childs objectAtIndex:indexPath.row];
            if (self.popViewDelegate && [(NSObject *)self.popViewDelegate respondsToSelector:@selector(KKShopFilterPopView:WithItem: AndParentItem:)])
                [self.popViewDelegate KKShopFilterPopView:self WithItem:item AndParentItem:pitem];
            [self removeFromSuperview];
        }
        
    }
}

@end
