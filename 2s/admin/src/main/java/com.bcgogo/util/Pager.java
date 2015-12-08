package com.bcgogo.util;

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

    private static final String INIT_PAGE_FAILED = "初始化分页组件失败！";

    private int currentPage;

    private int totalPage;

    private int totalRows;

    private int pageSize;

    private int nextPage;

    private int lastPage;

    private int rowStart;

    private int rowEnd;

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

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
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

    public int getRowEnd() {
        return rowEnd;
    }

    public void setRowEnd(int rowEnd) {
        this.rowEnd = rowEnd;
    }

    public boolean hasNextPage(){
        return this.totalPage > this.currentPage;
    }

    public Pager(int currentPage) throws PageException {
        if(currentPage < 1){
            throw new PageException(INIT_PAGE_FAILED);
        }
        this.currentPage = currentPage;
        this.pageSize = DEFAULT_PAGE_SIZE;
        this.rowStart = (this.currentPage - 1) * this.pageSize;
        this.rowEnd = this.currentPage * this.pageSize;
    }

    public Pager(int currentPage, int pageSize, boolean isPageSize) throws PageException {
        if(currentPage < 1 || pageSize < 1){
            throw new PageException(INIT_PAGE_FAILED);
        }
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.rowStart = (this.currentPage - 1) * this.pageSize;
        this.rowEnd = this.currentPage * this.pageSize;
    }

    /**
     * 根据总行数、当前页生成page对象，页大小使用默认值
     * @param totalRows
     * @param currentPage
     * @throws PageException
     */
    public Pager(int totalRows, int currentPage) throws PageException {
        if(totalRows < 0 || currentPage <= 0){
            throw new PageException(INIT_PAGE_FAILED);
        }
        this.currentPage = currentPage;
        this.totalRows = totalRows;
        this.pageSize = DEFAULT_PAGE_SIZE;
        this.totalPage = this.totalRows%this.pageSize==0?(this.totalRows/this.pageSize):(this.totalRows/this.pageSize + 1);
        this.nextPage = this.currentPage<this.totalPage?(this.currentPage + 1):this.currentPage;
        this.lastPage = this.currentPage>1?(this.currentPage - 1):this.currentPage;
        this.rowStart = (this.currentPage - 1) * this.pageSize;
        this.rowEnd = this.currentPage * this.pageSize;
    }

    /**
     * 根据总行数、当前页、页大小生成page对象
     * @param totalRows
     * @param currentPage
     * @param pageSize
     * @throws PageException
     */
    public Pager(int totalRows, int currentPage, int pageSize) throws PageException {
        if(totalRows < 0 || currentPage <= 0 || pageSize <= 0){
            throw new PageException(INIT_PAGE_FAILED);
        }
        this.currentPage = currentPage;
        this.totalRows = totalRows;
        this.pageSize = pageSize;
        this.totalPage = this.totalRows%this.pageSize==0?(this.totalRows/this.pageSize):(this.totalRows/this.pageSize + 1);
        this.nextPage = this.currentPage<this.totalPage?(this.currentPage + 1):this.currentPage;
        this.lastPage = this.currentPage>1?(this.currentPage - 1):this.currentPage;
        this.rowStart = (this.currentPage - 1) * this.pageSize;
        this.rowEnd = this.currentPage * this.pageSize;
    }

}
