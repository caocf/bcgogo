package com.bcgogo.config.service.excelexport;

import com.bcgogo.common.Pager;
import com.bcgogo.exception.PageException;

import java.util.Iterator;
import java.util.List;

/**
 * 数据迭代器抽象类，所有导出的数据迭代器都集成该类
 * Created with IntelliJ IDEA.
 * User: zoujianhong
 * Date: 13-6-5
 * Time: 上午10:34
 * To change this template use File | Settings | File Templates.
 */
public abstract class BcgogoExportDataIterator implements Iterator {

    /**
     * 默认页大小
     */
    private static final int DEFAULT_PAGE_SIZE = 100;

    /**
     * 分页信息
     */
    private DataPage page;

  /**
   * 每个EXCEL的最大数据量
   */
  private int totalRowsPerExcel;

  public int getTotalRowsPerExcel() {
    return totalRowsPerExcel;
  }


  public DataPage getPage() {
        return page;
    }

    protected BcgogoExportDataIterator() throws PageException {
        this.page = new DataPage(getTotalRows(), DEFAULT_PAGE_SIZE);
    }

    protected BcgogoExportDataIterator(int pageSize) throws PageException {
        this.page = new DataPage(getTotalRows(), pageSize);
    }

  protected BcgogoExportDataIterator(int pageSize, int totalRowsPerExcel) throws PageException {
    this.page = new DataPage(getTotalRows(), pageSize);
    this.totalRowsPerExcel = totalRowsPerExcel;
  }

  protected BcgogoExportDataIterator(int totalRows, int pageSize, int totalRowsPerExcel) throws PageException {
    this.page = new DataPage(totalRows, pageSize);
    this.totalRowsPerExcel = totalRowsPerExcel;
  }


    /**
     * 判断迭代器是否可以继续下移
     * @return
     */
    @Override
    public boolean hasNext() {
        return this.page.hasNextPage();
    }

    @Override
    public void remove() {
        //TODO 暂不实现
    }

    /**
     * 获取需要导出的数据所有行数
     * 由具体业务场景子类负责实现
     * @return
     */
    abstract protected int getTotalRows();

    /**
     * 获取excel表头
     * @return
     */
    abstract protected List<String> getHead();

  /**
   * 需要导出的excel文件数量
   * @return
   */
  public int getNumOfExportExcel() {
    return getTotalRows() % totalRowsPerExcel == 0 ? getTotalRows() / totalRowsPerExcel : getTotalRows() / totalRowsPerExcel + 1;
  }

  /**
   * 在excel头最上面显示的信息，比如一些统计信息
   * @return
   */
  abstract protected List<String> getHeadShowInfo();

  /**
   * 在excel最尾部显示的信息，比如一些统计信息
   */
  abstract protected List<String> getTailShowInfo();

}
