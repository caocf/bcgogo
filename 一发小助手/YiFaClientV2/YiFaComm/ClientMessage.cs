using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace YiFaComm
{
    /*
     * 主窗口消息结构，包含所有的消息及下次更新时间
     * 
     */
    public class ClientMessage
    {
        public ClientMessage(long nexttime, List<ClientAssortedMessage> pmessages)
        {
            this.nextrequesttimeinterval = nexttime;
            this.messsages = new List<ClientAssortedMessage>();
            this.messsages.AddRange(pmessages);
        }
        long nextrequesttimeinterval;
        List<ClientAssortedMessage> messsages;
        

        public long getNextRequesttimeInterval()
        {
            return nextrequesttimeinterval;

        }

        public List<ClientAssortedMessage> getMessages()
        {
            return messsages;
        }
    }
}
