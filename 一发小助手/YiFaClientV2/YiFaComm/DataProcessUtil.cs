using System;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.IO;
using Newtonsoft.Json;
using System.Security.Cryptography.X509Certificates;
using System.Net.Security;
using System.Threading;
using System.Diagnostics;
using System.Windows.Forms;
using System.Net.NetworkInformation;

namespace YiFaComm
{
    public interface ReadDataComplectedEventHandler
    {
        void Handler(bool bTest, String msg);
        void Error(string errMsg);
    }

    public class DataProcessUtil
    {
        private static string shopId;
        private static string shopName;
        private static string userNo;
        private static string userName;
        private static string sessionId;
        private static string apiVersion="1.0";
        private static string appPath;
        private static string loginurl;

        private static bool bLogin = false;

        private static string[] certificatefiles;

        //private static string ServerIp = "phone.bcgogo.cn";
        private static string ServerIp = "shop.bcgogo.com";
        //private static string ServerIp = "mail.bcgogo.com";
        private static int ServerPort = 443;
      

        private static string backendAddress = "https://" + ServerIp ;
        //private static string backendAddress = "http://" + "localhost" +":"+ServerPort;
        //登陆
        private static string clientrequest = "/web/client";

        private static DataProcessUtil instance = null;

        public static DataProcessUtil getInstance()
        {
            if (instance == null)
            {
                instance = new DataProcessUtil();
             
            }

            return instance;
        }

        public static void setUserNo(string uno)
        {
            userNo = uno;
        }
   
        public static string getServerIp()
        {
            return ServerIp;
        }

        public static int getServerPort()
        {
            return ServerPort;
        }

        public static bool isLogin()
        {
            return bLogin;
        }

        public static string getShopId()
        {
            return shopId;
        }

        public static string getShopName()
        {
            return shopName;
        }

        public static string getUserNo()
        {
            return userNo;
        }

        public static string getUserName()
        {
            return userName;
        }

        public static void setAppPath(string path)
        {
            appPath = path;
            appPath = appPath.Substring(0, appPath.LastIndexOf('\\') + 1);
            certificatefiles = new string[3];

            certificatefiles[0] = appPath + "images\\bcgogo.crt";
            certificatefiles[1] = appPath + "images\\ca.crt";
            certificatefiles[2] = appPath + "images\\phone.crt";

            //certificatefile2 = appPath + "images\\ca.crt";
        }

        public static string getAppPath()
        {
            return appPath; ;
        }

        public static void logOut()
        {
            bLogin = false;
         
            userName = "";
            shopId = "";
            shopName = "";
        }

        public static  bool validateRemoteCertificate(object sender, X509Certificate certificate, X509Chain chain, SslPolicyErrors sslPolicyErrors)
        {
            //不需要对服务器端证书进行认证
            return true;
        }
        /*
         * request : 请求URL
         * method:get or post
         * 返回:json格式的返回
         */
        private static String getBackendData(String request, String method)
        {
            HttpWebRequest httpRequest = null;
            WebResponse rep = null;
            try
            {
                httpRequest = (HttpWebRequest)WebRequest.Create(request);
                httpRequest.Method = method;
                httpRequest.Timeout = 60000;

                ServicePointManager.ServerCertificateValidationCallback = new RemoteCertificateValidationCallback(validateRemoteCertificate);
                //string[] certificates = new string[1];
                //certificates[0] = certificatefile1;
                //certificates[1] = certificatefile2;
                if (certificatefiles != null && certificatefiles.Length > 0)
                {
                    foreach (string cer in certificatefiles)
                    {
                        X509Certificate x509 = X509Certificate.CreateFromCertFile(cer);
                        httpRequest.ClientCertificates.Add(x509);
                    }
                }                                
             
                rep = httpRequest.GetResponse();
                Stream stream = rep.GetResponseStream();
                Encoding myEncoding = Encoding.GetEncoding("utf-8");
                StreamReader sr = new StreamReader(stream, myEncoding);
                String response = sr.ReadToEnd();
                sr.Close();
                sr.Dispose();
                stream.Close();
                stream.Dispose();
                httpRequest = null;
                return response;
            }
            catch (Exception ex)
            {
                if (rep != null)
                {
                    rep.Close();
                    rep = null;
                }

                return "";
            }
            finally
            {
                httpRequest = null;
            }
        }  

        public static void ayncLogin(bool bTest, string username, string password, ReadDataComplectedEventHandler handler)
        {
            login(username.Trim(), password.Trim(), handler);        
        }     

        private class LoginThread 
        {
            string username;
            string password;
            ReadDataComplectedEventHandler handler;
            public LoginThread(string username, string password, ReadDataComplectedEventHandler handler)
            {
                this.username = username.Trim();
                this.password = password.Trim();
                this.handler = handler;
            }

            public void Start()
            {
                Thread workthread = new Thread(work);
                workthread.Start();
            }

            public void work()
            {
                try
                {
                    String request = backendAddress + clientrequest;

                    request += "?method=login";
                    request += "&userNo=";
                    request += username;
                    request += "&password=";
                    request += password;
                    request += "&apiVersion=";
                    request += apiVersion;

                    string mac = GetMacAddressByDos();
                    request += "&MAC=";
                    request += mac;
                    String response = getBackendData(request, "Get");
                    //Thread.Sleep(20000);
                    handler.Handler(false, response);

                }
                catch (Exception ex)
                {
                    string message = ex.Message;
                    handler.Error(ex.Message);
                }
            }

        }

        public static bool isConnect()
        {
            if (isLogin() == false) return true;

            return true;
        }

        /*
         * 登陆接口
         * username:用户名
         * password:口令
         * handler:返回处理器
         */
        public static void login(string username, string password, ReadDataComplectedEventHandler handler)
        {
            LoginThread loginprocessthread = new LoginThread(username, password, handler);
            loginprocessthread.Start();
        }
      
        /*
         * 处理登录的返回信息
         */
        public static bool parseLoginResponse(string response)
        {
            JsonTextReader jsonReader = new JsonTextReader(new StringReader(response));

            while (jsonReader.Read())
            {
                if (jsonReader.TokenType == JsonToken.PropertyName)
                {
                    if ((string)(jsonReader.Value) == "isSuccess" || (string)(jsonReader.Value) == "success")
                    {
                        if (jsonReader.Read())
                        {
                            object result = jsonReader.Value;
                            bLogin = (bool)result;
                            if (!bLogin)
                            {
                                break;
                            }
                        }
                    }
                    else if ((string)(jsonReader.Value) == "shopId")
                    {
                        if (jsonReader.Read())
                        {
                            object result = jsonReader.Value;
                            shopId = Convert.ToString(result);
                        }
                    }
                    else if ((string)(jsonReader.Value) == "shopName")
                    {
                        if (jsonReader.Read())
                        {
                            object result = jsonReader.Value;
                            shopName = Convert.ToString(result);
                        }
                    }
                    else if ((string)(jsonReader.Value) == "userNo")
                    {
                        if (jsonReader.Read())
                        {
                            object result = jsonReader.Value;
                            userNo = Convert.ToString(result);
                        }
                    }
                    else if ((string)(jsonReader.Value) == "userName")
                    {
                        if (jsonReader.Read())
                        {
                            object result = jsonReader.Value;
                            userName = Convert.ToString(result);
                        }
                    }
                    else if ((string)(jsonReader.Value) == "sessionId")
                    {
                        if (jsonReader.Read())
                        {
                            object result = jsonReader.Value;
                            sessionId = Convert.ToString(result);
                        }
                    }
                }
            }

            return bLogin;
        }

        /*
         * 检查是否需要进行firefox升级的函数
         * version:当前版本
         * result:机构化的是否升级返回，里面包括是否返回指示及升级URL
         */
        public static bool ffUpdateCheck(string version, out FFCheckResult result)
        {
            String request = backendAddress + clientrequest;

            request += "?method=checkFirefoxUpdate";
            request += "&sessionId=";
            request += sessionId;
            request += "&shopId=";
            request += shopId;
            request += "&userNo=";
            request += userNo;
            request += "&localVersion=";
            request += version;
            request += "&apiVersion=";
            request += apiVersion;

            String response = getBackendData(request, "Get");

            return parseFFCheckResult(response, out result);
        }

        /*
         * 火狐是否升级应答的解析函数
         */
        private static bool parseFFCheckResult(string response, out FFCheckResult rtnresult)
        {
            JsonTextReader jsonReader = new JsonTextReader(new StringReader(response));

            rtnresult = new FFCheckResult();

            bool bResult = false;
            while (jsonReader.Read())
            {
                if (jsonReader.TokenType == JsonToken.PropertyName)
                {
                    if ((string)(jsonReader.Value) == "shopId")
                    {
                        if (jsonReader.Read())
                        {
                            object result = jsonReader.Value;
                            rtnresult.shopId = Convert.ToString(result);
                        }
                    }

                    else if ((string)(jsonReader.Value) == "userNo")
                    {
                        if (jsonReader.Read())
                        {
                            object result = jsonReader.Value;
                            rtnresult.userNo = Convert.ToString(result);
                        }
                    }
                    else if ((string)(jsonReader.Value) == "localVersion")
                    {
                        if (jsonReader.Read())
                        {
                            object result = jsonReader.Value;
                            rtnresult.localversion = Convert.ToString(result);
                        }
                    }
                    else if ((string)(jsonReader.Value) == "recentVersion")
                    {
                        if (jsonReader.Read())
                        {
                            object result = jsonReader.Value;
                            rtnresult.recentVersion = Convert.ToString(result);
                        }
                    }
                    else if ((string)(jsonReader.Value) == "needUpdate")
                    {
                        if (jsonReader.Read())
                        {
                            object result = jsonReader.Value;
                            rtnresult.needUpdate = Convert.ToBoolean(result);
                        }
                    }
                    else if ((string)(jsonReader.Value) == "updateUrl")
                    {
                        if (jsonReader.Read())
                        {
                            object result = jsonReader.Value;
                            rtnresult.updateUrl = Convert.ToString(result);
                        }
                    }
                }
            }

            return bResult;
        }
        /*
         * 获取主窗口消息
         * bTest:为true的时候使用测试消息，否则从服务器获取消息
         */
        public static ClientMessage getMessages(bool bTest)
        {
            if (bTest)
            {
                List<ClientAssortedMessage> results = new List<ClientAssortedMessage>();
                for (int j = 0; j < 4; j++)
                {
                    ClientAssortedMessage msg = new ClientAssortedMessage();
                    if (j == 0)
                    {
                        msg.recommendScene = "关联请求";
                        msg.title = "关联请求";
                        msg.relatedTitle = "前往消息中心";
                        msg.relatedUrl = "www.baidu.com";
                        msg.msgNumber = "10";
                    }
                    else if (j == 1)
                    {
                        msg.recommendScene = "配件推送";
                        msg.title = "配件推送";
                        msg.relatedTitle = "前往配件报价";
                        msg.relatedUrl = "www.baidu.com";
                        msg.msgNumber = "3";
                    }
                    else if (j == 2)
                    {
                        msg.recommendScene = "订单消息";
                        msg.title = "订单消息";
                        msg.relatedTitle = "前往订单中心";
                        msg.relatedUrl = "www.baidu.com";
                        msg.msgNumber = "5";
                    }
                    else if (j == 3)
                    {
                        msg.recommendScene = "系统消息";
                        msg.title = "系统消息";
                        msg.relatedTitle = "前往系统公告";
                        msg.relatedUrl = "www.baidu.com";
                        msg.msgNumber = "7";
                    }
                    
                    
                    for (int i = 0; i < 3; i++)
                    {
                        ClientAssortedMessageItem item = new ClientAssortedMessageItem();
                        item.content = "childmessage" + (i + 1);
                        item.url = "www.sina.com.cn";
                        item.feedbackUrls.Add("url1");
                        item.feedbackUrls.Add("url2");
                        item.feedbackUrls.Add("url3");
                        msg.items.Add(item);
                    }

                    results.Add(msg);
                }

                ClientMessage result = new ClientMessage(30000, results);
                return result;
            }
            String request = backendAddress + clientrequest;

            request += "?method=getMessages";
            request += "&sessionId=";
            request += sessionId;
            request += "&shopId=";
            request += shopId;
            request += "&userNo=";
            request += userNo;
            request += "&apiVersion=";
            request += apiVersion;

            String response = getBackendData(request, "Get");
            return parseMessages(response);
        }
        /*
         * 解析主窗口消息的返回
         */
        private static ClientMessage parseMessages(string response)
        {
            List<ClientAssortedMessage> results = new List<ClientAssortedMessage>();
            ClientAssortedMessage obj = null;
            long nextrequesttime = 600000;
            JsonTextReader jsonReader = new JsonTextReader(new StringReader(response));
            while (jsonReader.Read())
            {
                if (jsonReader.TokenType == JsonToken.PropertyName)
                {
                    if ((string)(jsonReader.Value) == "nextRequestTimeInterval")
                    {
                        if (jsonReader.Read())
                        {
                            object result = jsonReader.Value;
                            nextrequesttime = Convert.ToInt64(result);
                        }
                    }
                }
                else if (jsonReader.TokenType == JsonToken.StartArray)
                {
                    while (jsonReader.Read())
                    {
                        if (jsonReader.TokenType == JsonToken.EndArray)
                        {
                            //end 
                            break;
                        }
                        else if (jsonReader.TokenType == JsonToken.StartObject)
                        {
                            obj = new ClientAssortedMessage();

                        }
                        else if (jsonReader.TokenType == JsonToken.EndObject)
                        {
                            results.Add(obj);
                        }
                        else if (jsonReader.TokenType == JsonToken.PropertyName)
                        {
                            if ((string)(jsonReader.Value) == "recommendScene")
                            {
                                if (jsonReader.Read())
                                {
                                    object result = jsonReader.Value;
                                    obj.recommendScene = Convert.ToString(result);
                                }
                            }
                            else if ((string)(jsonReader.Value) == "title")
                            {
                                if (jsonReader.Read())
                                {
                                    object result = jsonReader.Value;
                                    obj.title = Convert.ToString(result);
                                }
                            }
                            else if ((string)(jsonReader.Value) == "relatedTitle")
                            {
                                if (jsonReader.Read())
                                {
                                    object result = jsonReader.Value;
                                    obj.relatedTitle = Convert.ToString(result);
                                }
                            }
                            else if ((string)(jsonReader.Value) == "relatedUrl")
                            {
                                if (jsonReader.Read())
                                {
                                    object result = jsonReader.Value;
                                    obj.relatedUrl = Convert.ToString(result);
                                    if (!string.IsNullOrEmpty(obj.relatedUrl))
                                    {
                                        obj.relatedUrl += "&clientUserNo="+userNo;
                                    }
                                }
                            }
                            else if ((string)(jsonReader.Value) == "nextRequestTime")
                            {
                                if (jsonReader.Read())
                                {
                                    object result = jsonReader.Value;
                                    obj.nextRequestTime = Convert.ToString(result);
                                }
                            }
                            else if ((string)(jsonReader.Value) == "feekbackUrl")
                            {
                                if (jsonReader.Read())
                                {
                                    object result = jsonReader.Value;
                                    obj.feekbackUrl = Convert.ToString(result);
                                }
                            }
                            else if ((string)(jsonReader.Value) == "msgNumber")
                            {
                                if (jsonReader.Read())
                                {
                                    object result = jsonReader.Value;
                                    obj.msgNumber = Convert.ToString(result);
                                }
                            }
                            else if ((string)(jsonReader.Value) == "items")
                            {
                                ClientAssortedMessageItem item = null;
                                if (jsonReader.Read())
                                {
                                    if (jsonReader.TokenType == JsonToken.StartArray)
                                    {
                                        while (jsonReader.Read())
                                        {
                                            if (jsonReader.TokenType == JsonToken.EndArray)
                                            {
                                                //end 
                                                break;
                                            }
                                            else if (jsonReader.TokenType == JsonToken.StartObject)
                                            {
                                                item = new ClientAssortedMessageItem();

                                            }
                                            else if (jsonReader.TokenType == JsonToken.EndObject)
                                            {
                                                obj.items.Add(item);
                                            }
                                            else if (jsonReader.TokenType == JsonToken.PropertyName)
                                            {
                                                if ((string)jsonReader.Value == "content")
                                                {
                                                    if (jsonReader.Read())
                                                    {
                                                        object result = jsonReader.Value;
                                                        item.content = Convert.ToString(result);
                                                    }
                                                }
                                                else if ((string)jsonReader.Value == "url")
                                                {
                                                    if (jsonReader.Read())
                                                    {
                                                        object result = jsonReader.Value;
                                                        item.url = Convert.ToString(result);
                                                        if (!string.IsNullOrEmpty(item.url))
                                                        {
                                                            item.url += "&clientUserNo=" + userNo;
                                                        }
                                                    }
                                                }
                                                else if ((string)jsonReader.Value == "feedbackUrls")
                                                {
                                                    if (jsonReader.Read())
                                                    {
                                                        if (jsonReader.TokenType == JsonToken.StartArray)
                                                        {
                                                            while (jsonReader.Read())
                                                            {
                                                                if (jsonReader.TokenType == JsonToken.EndArray)
                                                                {
                                                                    //end 
                                                                    break;
                                                                }
                                                              
                                                                if (jsonReader.TokenType == JsonToken.String)
                                                                {
                                                                   
                                                                    {
                                                                        object result = jsonReader.Value;
                                                                        item.feedbackUrls.Add(Convert.ToString(result));
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (results.Count == 0)
            {
                return null;
            }

            ClientMessage returnresult = new ClientMessage(nextrequesttime, results);
            return returnresult; 
          
        }
        /*
         * 获取弹窗消息
         * bTest:为true的时候，返回自定义测试弹窗消息
         */
        public static ClientPrompt getPrompt(bool bTest)
        {
            if (bTest)
            {
                ClientPrompt cltprompt = new ClientPrompt();
                cltprompt.title = "你有5条新的关联请求!";
                cltprompt.content = "苏州新宇商贸，苏州大马汽修，苏州明明美容等请求与你关联!";
                cltprompt.url = "www.baidu.com";
                cltprompt.nextRequestTime = "60000";
                //cltprompt.feedbackUrls.Add("www.baidu.com");
                //cltprompt.feedbackUrls.Add("www.baidu.com");
                //cltprompt.feedbackUrls.Add("www.baidu.com");
                return cltprompt;
            }

            String request = backendAddress + clientrequest;

            request += "?method=getPrompt";
            request += "&sessionId=";
            request += sessionId;
            request += "&shopId=";
            request += shopId;
            request += "&userNo=";
            request += userNo;
            request += "&apiVersion=";
            request += apiVersion;

            String response = getBackendData(request, "Get");

            return parsePrompt(response);
        }
        /*
         * 解析弹窗消息的返回
         */
        private static ClientPrompt parsePrompt(string response)
        {
            ClientPrompt cltprompt = null;
            try
            {
                JsonTextReader jsonReader = new JsonTextReader(new StringReader(response));


                while (jsonReader.Read())
                {
                    if (jsonReader.TokenType == JsonToken.PropertyName)
                    {
                        if ((string)(jsonReader.Value) == "recommendScene")
                        {
                            
                            if (jsonReader.Read())
                            {
                                object result = jsonReader.Value;
                                if (cltprompt == null)
                                {
                                    cltprompt = new ClientPrompt();
                                }
                                cltprompt.recommendScene = Convert.ToString(result);
                            }
                        }

                        else if ((string)(jsonReader.Value) == "recommendId")
                        {
                            if (jsonReader.Read())
                            {
                                object result = jsonReader.Value;
                                if (cltprompt == null)
                                {
                                    cltprompt = new ClientPrompt();
                                }
                                cltprompt.recommendId = Convert.ToString(result);
                            }
                        }
                        else if ((string)(jsonReader.Value) == "title")
                        {
                            if (jsonReader.Read())
                            {
                                object result = jsonReader.Value;
                                if (cltprompt == null)
                                {
                                    cltprompt = new ClientPrompt();
                                }
                                cltprompt.title = Convert.ToString(result);
                            }
                        }
                        else if ((string)(jsonReader.Value) == "content")
                        {
                            if (jsonReader.Read())
                            {
                                object result = jsonReader.Value;
                                if (cltprompt == null)
                                {
                                    cltprompt = new ClientPrompt();
                                }
                                cltprompt.content = Convert.ToString(result);
                            }
                        }
                        else if ((string)(jsonReader.Value) == "url")
                        {
                            if (jsonReader.Read())
                            {
                                object result = jsonReader.Value;
                                if (cltprompt == null)
                                {
                                    cltprompt = new ClientPrompt();
                                }
                                cltprompt.url = Convert.ToString(result);
                                if (!string.IsNullOrEmpty(cltprompt.url))
                                {
                                    cltprompt.url += "&clientUserNo="+userNo;
                                }
                            }
                        }
                        else if ((string)(jsonReader.Value) == "feedbackUrl")
                        {
                            if (jsonReader.Read())
                            {
                                object result = jsonReader.Value;
                                if (cltprompt == null)
                                {
                                    cltprompt = new ClientPrompt();
                                }
                                cltprompt.feedbackUrl = Convert.ToString(result);
                            }
                        }
                        else if ((string)(jsonReader.Value) == "nextRequestTime")
                        {
                            if (jsonReader.Read())
                            {
                                object result = jsonReader.Value;
                                if (cltprompt == null)
                                {
                                    cltprompt = new ClientPrompt();
                                }
                                cltprompt.nextRequestTime = Convert.ToString(result);
                            }
                        }
                        else if ((string)(jsonReader.Value) == "feedbackUrls")
                        {
                            if (jsonReader.Read())
                            {
                                if (cltprompt == null)
                                {
                                    cltprompt = new ClientPrompt();
                                }
                                if (jsonReader.TokenType == JsonToken.StartArray)
                                {
                                    while (jsonReader.Read())
                                    {
                                        if (jsonReader.TokenType == JsonToken.EndArray)
                                        {
                                            //end 
                                            break;
                                        }
                                      
                                        if (jsonReader.TokenType == JsonToken.String)
                                        {
                                             
                                            {
                                                object result = jsonReader.Value;
                                                cltprompt.feedbackUrls.Add(Convert.ToString(result));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
            }

            return cltprompt;


        }
        //获取MAC地址
        private static string GetMacAddressByDos()
         {
            /*
             string macAddress = "";
              Process p = null;
              StreamReader reader = null;
              try
              {
                  ProcessStartInfo start = new ProcessStartInfo("cmd.exe"); 
  
                  start.FileName = "ipconfig";
                  start.Arguments = "/all"; 
  
                  start.CreateNoWindow = true; 
  
                  start.RedirectStandardOutput = true; 
  
                  start.RedirectStandardInput = true; 
  
                  start.UseShellExecute = false; 
  
                  p = Process.Start(start);
  
                  reader = p.StandardOutput; 
  
                  string line = reader.ReadLine(); 
  
                  while (!reader.EndOfStream)
                  {
                      if (line.ToLower().IndexOf("physical address") > 0 || line.ToLower().IndexOf("物理地址") > 0)
                      {
                          int index = line.IndexOf(":");
                          index += 2;
                          macAddress = line.Substring(index);
                          macAddress = macAddress.Replace('-', ':');
                          break;
                      }
                      line = reader.ReadLine();
                  }
              }
              catch
              {
  
              }
              finally
              {
                  if (p != null)
                  {
                      p.WaitForExit(); 
                      p.Close(); 
                  }
                  if (reader != null)
                  {
                      reader.Close(); 
                  }
              }
              return macAddress;
             * */

             NetworkInterface[] interfaces = NetworkInterface.GetAllNetworkInterfaces();
             foreach (NetworkInterface ni in interfaces)
             {
                 return ni.GetPhysicalAddress().ToString();
             }

             return "";
          }
     
        //发送url
        public static void sendDirect(string url)
        {
            String response = getBackendData(url, "Get");

            return;  
        }
    }
}


