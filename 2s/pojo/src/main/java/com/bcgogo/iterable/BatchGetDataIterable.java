package com.bcgogo.iterable;

import org.apache.commons.collections.CollectionUtils;

import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-22
 * Time: 下午5:11
 * To change this template use File | Settings | File Templates.
 */
public abstract class BatchGetDataIterable<T> implements Iterable<T> {
  private int batchSize = 2000;

  protected abstract List<T> getBatch(int start, int rows);

  class BatchGetSourceIterator implements Iterator<T> {
    private int currBatchSeq = 0;
    private int cursor = 0;
    private volatile List<T> currentBatch;

    List<T> findBatch(int cursor) {
      int batchSeq = cursor / batchSize;
      if (batchSeq != currBatchSeq || CollectionUtils.isEmpty(currentBatch)) {
        currentBatch = getBatch(batchSeq * batchSize, batchSize);
        currBatchSeq = batchSeq;
      }
      return currentBatch;
    }

    public boolean hasNext() {
      List<T> batch = findBatch(cursor);
      return indexOfTheBatch(cursor) < batch.size();
    }

    private int indexOfTheBatch(int pos) {
      return pos % batchSize;
    }

    public T next() {
      if (!hasNext()) {
        throw new IndexOutOfBoundsException();
      }
      int pos = cursor++;
      return findBatch(pos).get(indexOfTheBatch(pos));
    }
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  public Iterator<T> iterator() {
    return new BatchGetSourceIterator();
  }
  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }
}
