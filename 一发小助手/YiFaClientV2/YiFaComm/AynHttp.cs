using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Net;
using System.Security.Cryptography.X509Certificates;
using System.Net.Security;

namespace YiFaComm
{
   
    internal class WebReqState
    {
        public byte[] Buffer;
        public MemoryStream ms;
        public const int BufferSize = 1024;
        public Stream OrginalStream;
        public HttpWebResponse WebResponse;

        public WebReqState()
        {
            Buffer = new byte[1024];
            ms = new MemoryStream();
        }
    }

    public interface ReadDataComplectedEventHandler
    {
        void Handler(bool bTest, String msg);
        void Error(string errMsg);
    }


    /// <summary>
    /// 异步http请求类
    /// </summary>
    public class AsynchronousWebRequest
    {
        //定义回传时响应的事件
        public ReadDataComplectedEventHandler OnReadDataComplected;

        //远端回传数据时调用的读取返回值数据的方法

        protected void readDataCallback(IAsyncResult ar)
        {
            WebReqState rs = ar.AsyncState as WebReqState;
            int read = rs.OrginalStream.EndRead(ar);
            if (read > 0)
            {
                rs.ms.Write(rs.Buffer, 0, read);
                rs.OrginalStream.BeginRead(rs.Buffer, 0,
                    WebReqState.BufferSize, new AsyncCallback(readDataCallback), rs);
            }
            else
            {
                rs.OrginalStream.Close();
                rs.WebResponse.Close();
                String response = rs.ms.ToString();
                OnReadDataComplected.Handler(false, response);
                
            }

        }

        //远端response时调用的方法

        protected void responseCallback(IAsyncResult ar)
        {
            HttpWebRequest req = ar.AsyncState as HttpWebRequest;
            if (req == null)
            {
                OnReadDataComplected.Error("HttpWebRequest为空");
                return;
            }
            try
            {
                HttpWebResponse response = req.EndGetResponse(ar) as HttpWebResponse;
                if (response.StatusCode != HttpStatusCode.OK)
                {
                    response.Close();
                    return;
                }
                WebReqState st = new WebReqState();
                st.WebResponse = response;
                Stream repsonseStream = response.GetResponseStream();
                st.OrginalStream = repsonseStream;
                repsonseStream.BeginRead(st.Buffer, 0,
                    WebReqState.BufferSize, new AsyncCallback(readDataCallback), st);
            }
            catch (Exception ex)
            {
                OnReadDataComplected.Error(ex.Message);
                return;
            }

        }

        public static bool validateRemoteCertificate(object sender, X509Certificate certificate, X509Chain chain, SslPolicyErrors sslPolicyErrors)
        {
            //不需要对服务器端证书进行认证
            return true;
        }

        //异步请求方法

        public bool BeginRequest(string url, string[] certificates,  String method, string content, out string errorMsg)
        {
            try
            {
                HttpWebRequest req = HttpWebRequest.Create(url) as HttpWebRequest;

                ServicePointManager.ServerCertificateValidationCallback = new RemoteCertificateValidationCallback(validateRemoteCertificate);
                if (certificates != null && certificates.Length > 0)
                {
                    foreach(string cer in certificates)
                    {
                        X509Certificate x509 = X509Certificate.CreateFromCertFile(cer);
                        req.ClientCertificates.Add(x509);
                    }
                }
                errorMsg = "";

                req.Method = method.ToString();

                req.Timeout = 1000 * 10;//超时间10秒；
                //req.KeepAlive = true;

                if (content != null && content.Length > 0)
                {
                    byte[] contentBytes = Encoding.GetEncoding("UTF-8").GetBytes(content);

                    req.ContentLength = contentBytes.Length;

                    Stream reqStream = req.GetRequestStream();
                    reqStream.Write(contentBytes, 0, contentBytes.Length);
                    reqStream.Close();
                }
                
                req.BeginGetResponse(new AsyncCallback(responseCallback), req);

                return true;
            }
            catch (Exception ex)
            {
                errorMsg = ex.Message;
                return false;
            }

        }

    }


}
