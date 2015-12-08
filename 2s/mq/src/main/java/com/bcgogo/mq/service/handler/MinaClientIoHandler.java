package com.bcgogo.mq.service.handler;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-6-3
 * Time: 16:33
 */

import com.bcgogo.utils.ByteUtil;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinaClientIoHandler extends IoHandlerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(MinaClientIoHandler.class);

    /**
     * 当客户端接受到消息时
     */
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        byte[] data = (byte[]) message;
        int len = ByteUtil.reverseByteToInt(ByteUtil.subBytes(data, 0, 4));
        byte[] bData = ByteUtil.subBytes(data, 5, len);
        LOG.info("client:receive data,type={}, bData={}",data[4],new String(bData));
    }

    /**
     * 当一个客户端被关闭时
     */
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        LOG.info("client disconnect");
    }

    /**
     * 当一个客户端连接进入时
     */
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        LOG.info("create connection to server :" + session.getRemoteAddress());
    }

}
