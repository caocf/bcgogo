package com.bcgogo.api;

/**
 * Created by Luffy.Liu on 2015/9/23.
 */
public class FileCollisionVideo {
    private long total;//总大小
    private long offset;//数据偏移量
    private long block;//当前块大小
    private long crc;//当前块校验值


    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getBlock() {
        return block;
    }

    public void setBlock(long block) {
        this.block = block;
    }

    public long getCrc() {
        return crc;
    }

    public void setCrc(long crc) {
        this.crc = crc;
    }
}
