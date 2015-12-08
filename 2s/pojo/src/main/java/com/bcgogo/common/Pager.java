package com.bcgogo.common;

import com.bcgogo.exception.PageException;
import com.bcgogo.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页组件公用类
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-2-15
 * Time: 下午3:13
 * To change this template use File | Settings | File Templates.
 */
public class Pager {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private static final int DEFAULT_PAGE_INDEX = 1;

  private static final String INIT_PAGE_FAILED = "初始化分页组件失败！";

  private int currentPage = DEFAULT_PAGE_INDEX;

  private int totalPage;

  private int totalRows;

  private int pageSize = DEFAULT_PAGE_SIZE;

  private int PageRows;

  private int nextPage;

  private int lastPage;

  private int rowStart;

  private int rowEnd;

  private boolean isFirstPage;

  private boolean isLastPage;

  private boolean hasNextPage;

  private boolean hasPreviousPage;

  //营业统计打印专用
  private String startDateStr;

  private String endDateStr;

  public int getLastPage() {
    return lastPage;
  }

  public int getTotalPage() {
    return totalPage;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public int getTotalRows() {
    return totalRows;
  }

  public int getPageSize() {
    return pageSize;
  }

  public int getNextPage() {
    return nextPage;
  }

  public int getRowStart() {
    return rowStart;
  }

  /**
   * 当前页总记录数
   */
  public int getPageRows() {
    if (this.getIsLastPage()) {
      // 最后一页时
      return this.totalRows - (this.pageSize * (this.totalPage - 1));
    } else {
      return this.getPageSize();
    }
  }

  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
    if (currentPage >= this.DEFAULT_PAGE_INDEX && currentPage <= this.getTotalPage() && currentPage != this.currentPage) {
      this.currentPage = currentPage;
    }
  }

  public void setTotalPage(int totalPage) {
    this.totalPage = totalPage;
  }

  public void setLastPage(int lastPage) {
    this.lastPage = lastPage;
  }

  public void setTotalRows(int totalRows) {
    this.totalRows = totalRows;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public void setNextPage(int nextPage) {
    this.nextPage = nextPage;
  }

  public void setRowStart(int rowStart) {
    this.rowStart = rowStart;
  }

  public void setRowStart() {
    this.rowStart = (this.currentPage - 1) * this.pageSize;
  }

  public int getRowEnd() {
    return rowEnd;
  }

  public void setRowEnd(int rowEnd) {
    this.rowEnd = rowEnd;
  }

  public boolean getIsFirstPage() {
    return this.currentPage == this.DEFAULT_PAGE_INDEX;
  }

  public void setIsFirstPage(boolean isFirstPage) {
    this.isFirstPage = isFirstPage;
  }

  public boolean getIsLastPage() {
    return this.currentPage == this.totalPage;
  }


  public boolean getHasNextPage() {
    return this.hasNextPage();
  }

  public boolean hasNextPage() {
    return this.totalPage > this.currentPage;
  }

  public boolean getHasPreviousPage() {
    //当前页索引号大于默认的初始页号,这里为1
    return this.currentPage > this.DEFAULT_PAGE_INDEX;
  }

  public void gotoFirstPage() {
    this.gotoPage(this.DEFAULT_PAGE_INDEX);
  }

  public void gotoLastPage() {
    this.gotoPage(this.getTotalPage());

  }

  public void gotoPreviousPage() {
    this.gotoPage(this.getCurrentPage() - 1);

  }

  public void gotoNextPage() {
    this.gotoPage(this.getCurrentPage() + 1);
    changePage();
  }

  public void gotoPage(int newPageIndex) {
    if (newPageIndex >= this.DEFAULT_PAGE_INDEX && newPageIndex <= this.getTotalPage()) {
      this.setCurrentPage(newPageIndex);
    }
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
  }

  public Pager(){

  }

  public Pager(int currentPage) throws PageException {
    if (currentPage < 1) {
      throw new PageException(INIT_PAGE_FAILED);
    }
    this.currentPage = currentPage;
    this.pageSize = DEFAULT_PAGE_SIZE;
    this.rowStart = (this.currentPage - 1) * this.pageSize;
    if (totalPage > 0) {
    if (this.currentPage != this.totalPage) {
      this.rowEnd = this.currentPage * this.pageSize;
    } else {
      this.rowEnd = this.totalRows;
    }
  }
  }

  public Pager(int currentPage, int pageSize, boolean isPageSize) throws PageException {
    if (currentPage < 1 || pageSize < 1) {
      throw new PageException(INIT_PAGE_FAILED);
    }
    this.currentPage = currentPage;
    this.pageSize = pageSize;
    this.rowStart = (this.currentPage - 1) * this.pageSize;
    if (this.currentPage != this.totalPage) {
      this.rowEnd = this.currentPage * this.pageSize;

    } else {
      this.rowEnd = this.totalRows;
    }
  }

  /**
   * 根据总行数、当前页生成page对象，页大小使用默认值
   *
   * @param totalRows
   * @param currentPage
   * @throws PageException
   */
  public Pager(int totalRows, int currentPage) throws PageException {
    if (totalRows < 0 || currentPage <= 0) {
      throw new PageException(INIT_PAGE_FAILED);
    }
    this.currentPage = currentPage;
    this.totalRows = totalRows;
    this.pageSize = DEFAULT_PAGE_SIZE;
    this.totalPage = this.totalRows % this.pageSize == 0 ? (this.totalRows / this.pageSize) : (this.totalRows / this.pageSize + 1);
    changePage();
  }

  /**
   * 根据总行数、当前页、页大小生成page对象
   *
   * @param totalRows
   * @param currentPage
   * @param pageSize
   * @throws PageException
   */
  public Pager(int totalRows, int currentPage, int pageSize) throws PageException {
    if (totalRows < 0 || currentPage <= 0 || pageSize <= 0) {
      throw new PageException(INIT_PAGE_FAILED);
    }
    this.currentPage = currentPage;
    this.totalRows = totalRows;
    this.pageSize = pageSize;
    this.totalPage = this.totalRows % this.pageSize == 0 ? (this.totalRows / this.pageSize) : (this.totalRows / this.pageSize + 1);
    changePage();
  }



  private void changePage() {
    this.nextPage = this.currentPage < this.totalPage ? (this.currentPage + 1) : this.currentPage;
    this.lastPage = this.currentPage > 1 ? (this.currentPage - 1) : this.currentPage;
    this.rowStart = (this.currentPage - 1) * this.pageSize;
    if (this.currentPage != this.totalPage) {
      this.rowEnd = this.currentPage * this.pageSize;
    } else {
      this.rowEnd = this.totalRows;
    }
    this.hasNextPage = this.currentPage < this.totalPage;
    this.hasPreviousPage = this.currentPage > this.DEFAULT_PAGE_INDEX;
    this.isFirstPage = this.currentPage == this.DEFAULT_PAGE_INDEX;
    this.isLastPage = this.currentPage == this.totalPage;
    if(this.totalPage==0){
         this.isLastPage=true;
    }
  }

  public String toJson() {
    List<Pager> list = new ArrayList<Pager>();
    if (null != this) {
      list.add(this);
    }
    return JsonUtil.listToJson(list);
  }

  public void reSetPager(int totalRows) throws PageException {
    if (totalRows < 0) {
      throw new PageException(INIT_PAGE_FAILED);
    }
    this.totalRows = totalRows;
    this.totalPage = this.totalRows % this.pageSize == 0 ? (this.totalRows / this.pageSize) : (this.totalRows / this.pageSize + 1);
    changePage();
  }
}
