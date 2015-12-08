package com.bcgogo.config.service.excelexport;

import com.bcgogo.common.Pager;
import com.bcgogo.exception.PageException;
import com.bcgogo.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 公用Pager类不适合导出使用，这里单独实现一个数据分页类，两者大同小异，并提供转换方法
 * Created with IntelliJ IDEA.
 * User: zoujianhong
 * Date: 13-6-17
 * Time: 上午9:42
 * To change this template use File | Settings | File Templates.
 */
public class DataPage {

    private static final int DEFAULT_PAGE_SIZE = 200;

    private static final int DEFAULT_PAGE_INDEX = 0;

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

    public DataPage(int totalRows) {
        this.totalRows = totalRows;
        this.totalPage = this.totalRows % this.pageSize == 0 ? (this.totalRows / this.pageSize) : (this.totalRows / this.pageSize + 1);
        changePage();
    }

    public DataPage(int totalRows, int pageSize) {
        this.pageSize = pageSize;
        this.totalRows = totalRows;
        this.totalPage = this.totalRows % this.pageSize == 0 ? (this.totalRows / this.pageSize) : (this.totalRows / this.pageSize + 1);
        changePage();
    }

    public Pager toCommonPager() throws PageException {
        return new Pager(this.totalRows, this.currentPage, this.pageSize);
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


    public boolean getIsFirstPage() {
        return this.currentPage == this.DEFAULT_PAGE_INDEX;
    }

    public void setIsFirstPage(boolean isFirstPage) {
        this.isFirstPage = isFirstPage;
    }

    public boolean getIsLastPage() {
        return this.currentPage == this.totalPage;
    }

    public boolean hasNextPage() {
        return this.totalPage > this.currentPage;
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


}
