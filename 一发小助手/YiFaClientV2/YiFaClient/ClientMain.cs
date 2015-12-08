using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using YiFaComm;
using System.Threading;
using System.Runtime.InteropServices;
using YiFaClient.Properties;
namespace YiFaClient
{
    public partial class ClientMain : Form
    {
        [DllImport("user32.dll")]
        public static extern bool SendMessage(IntPtr hwnd, int wMsg, int wParam, int lParam);
        [DllImport("user32.dll")]
        public static extern bool ReleaseCapture();

        Thread msgDetectThread = null;     
        bool exit = true;   //是否退出主消息蚌
        bool forceExit = false;//是否强制退出
        List<ClientAssortedMessage> messages = null;//主窗口消息
        bool msgChanged = true;//主窗口消息是否改变
        static Object synobj = new Object();//同步对象
        static Object popupsynobj = new Object();//同步对象
        bool bSwitching = false;//是否在切换帐户
        List<Popup> prompwindows = new List<Popup>();//弹出窗口集合
        bool bManThreadRunning = false;//主消息蚌是否在运行
        bool bFlashThreadRunning = false;//闪烁线程是否在运行
        Thread flashThread = null;
        bool exitFlash = true;
        bool flashdisplay = false;
        bool bNormalICON = true;
        public ClientMain()
        {
            InitializeComponent();
            init();
            this.Hide();
        }

        public void removePopup(Popup p)
        {
            lock (popupsynobj)
            {
                prompwindows.Remove(p);
            }
        }

        public void clearPopup()
        {
            lock (popupsynobj)
            {
                if (prompwindows != null)
                {
                    foreach (Popup p in prompwindows)
                    {
                        try
                        {
                            p.ExitWindows();
                        }
                        catch (Exception ex)
                        {
                        }
                    }
                }
            }
        }

        private void init()
        {          
            this.WindowState = FormWindowState.Minimized;
            Control.CheckForIllegalCrossThreadCalls = false;
            DataProcessUtil.setAppPath(Application.ExecutablePath);

            this.AutoScaleDimensions = new System.Drawing.SizeF(96F, 96F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Dpi;

            bool autoStart;
            string lastUserId;
            int closeoption = 1;
            bool nevernotifyclose = false;
            string preversion;
            List<UserInfo> users = FileUtils.getUsers(out autoStart, out lastUserId, out closeoption, out nevernotifyclose, out preversion);

            bool autologin = false;
            foreach (UserInfo u in users)
            {
                if (u.userNo.Trim() == lastUserId)
                {
                    autologin = u.autoLogin.Equals("true");
                    break;
                }
            }

            if (!preversion.Equals(Constants.currentVersion))
            {
                MessageBox.Show("成功升级到版本:" + Constants.currentVersion);
                FileUtils.setAutoStart(autoStart, closeoption, nevernotifyclose);
            }

            versionlabel.Text += "(" + Constants.currentVersion + ")";

            LoginForm loginform = LoginForm.getInstance(true);

            if (loginform.BBind())
            {
                loginform.Show();

            }
            else
            {
                FirstNotifyWindow firstNotify = new FirstNotifyWindow();
                firstNotify.setLoginWindow(loginform);
                firstNotify.Show();
            }

            showNotifyIcon();

            if (!autologin)
            {
                startDetectThread();
            }

            //startFlashThread();

            this.panel2.HorizontalScroll.Visible = false;
        }

        public void startDetectThread()
        {
            msgDetectThread = new Thread(new ThreadStart(MsgDetectMain));
            msgDetectThread.Start();
            bManThreadRunning = true;
        }

        private void startFlashThread()
        {
            if (bFlashThreadRunning) return;

            flashThread = new Thread(new ThreadStart(flashMain));
            flashThread.Start();
            bFlashThreadRunning = true;
        }

        private void flashMain()
        {
            try
            {
                exitFlash = false;
                bool showIcon = false;
                flashdisplay = true;
                while (!exitFlash)
                {
                    if (flashdisplay)
                    {
                        showIcon2(showIcon);
                        if (showIcon)
                        {
                            // notifyIcon1.Visible = true;
                            showIcon = false;
                        }
                        else
                        {
                            //notifyIcon1.Visible = false;
                            showIcon = true;
                        }
                    }
                    else
                    {
                        if (!bNormalICON)
                        {
                            showIcon2(true);
                        }
                        ShowIcon = false;
                    }
                    Thread.Sleep(400);
                }

               
                showIcon2(true);
                
                bFlashThreadRunning = false;
            }
            catch (Exception ex)
            {

            }
        }

        public void switchUser()
        {
            if (!exit && bManThreadRunning)
            {
                exit = true;
                msgDetectThread.Join();
            }
            //重新启动工作线程       
            startDetectThread();
        }

        private void MsgDetectMain()
        {
            try
            {
                exit = false;
                bool firsttime = true;
                long lastdetectmessages = DateTime.Now.Ticks;
                long lastdetectpropmts = DateTime.Now.Ticks;
                long msginterval = 10 * 60;
                long promptinterval = 10 * 60;
                while (!exit)
                {
                    Thread.Sleep(200);

                    if (bSwitching)
                    {
                        msginterval = 10 * 60;
                        promptinterval = 10 * 60;
                        firsttime = true;
                        bSwitching = false;
                        clearPopup();
                    }
                    //检测消息
                    long nowt = DateTime.Now.Ticks;
                    long deltdetectmessages = (nowt - lastdetectmessages) / 10000000;


                    if (firsttime || deltdetectmessages >= msginterval)//10分钟
                    {
                        lastdetectmessages = nowt;//更新检测时间
                        try
                        {
                            ClientMessage cltMessages = DataProcessUtil.getMessages(false);

                            if (cltMessages != null)
                            {
                                processUnClickedMsgs(false);
                                messages = cltMessages.getMessages();
                                msginterval = cltMessages.getNextRequesttimeInterval() / 1000;
                            }
                            else
                            {
                                msginterval = 2; //查不到数据的时候，2秒钟时间间隔
                            }
                        }
                        catch (Exception ex)
                        {
                            string msg = ex.Message;
                            msginterval = 2; //查不到数据的时候，2秒钟时间间隔
                        }
                        msgChanged = true;
                    }

                    long detectPrompmessages = (nowt - lastdetectpropmts) / 10000000;
                    if (firsttime || detectPrompmessages >= promptinterval)//10分钟
                    {
                        lastdetectpropmts = DateTime.Now.Ticks;
                        //弹出消息
                        try
                        {
                            ClientPrompt cltPrompt = DataProcessUtil.getPrompt(false);

                            if (cltPrompt != null)
                            {                           
                                if (!string.IsNullOrEmpty(cltPrompt.nextRequestTime))
                                {
                                    if (Convert.ToInt64(cltPrompt.nextRequestTime) < Int32.MaxValue)
                                    {
                                        promptinterval = Convert.ToInt64(cltPrompt.nextRequestTime) / 1000;
                                    }
                                    else
                                    {
                                        promptinterval = 2;
                                    }
                                }
                                else
                                {
                                    promptinterval = 2;
                                }
                                //显示弹出窗口
                                Popup pop = new Popup(cltPrompt);

                                this.Invoke(new System.EventHandler(this.showForm), new object[] { pop, null });
                            }
                            else
                            {
                                promptinterval = 2;
                            }
                        }
                        catch (Exception ex)
                        {
                        }
                    }

                    if (msgChanged)
                    {
                        this.Invoke(new System.EventHandler(this.DisplayMessage), new object[] { null, null });
                    }

                    firsttime = false;
                }

                processUnClickedMsgs(false);
                bManThreadRunning = false;
            }
            catch (Exception ex)
            {
            }
        }

        private void processUnClickedMsgs(bool bClose)
        {
            if (messages != null)
            {
                foreach (ClientAssortedMessage existmsg in messages)
                {
                    foreach (ClientAssortedMessageItem msgitem in existmsg.items)
                    {
                        if (msgitem.clickTimes() == 0)
                        {
                            if (bClose)
                            {
                                if (msgitem.feedbackUrls.Count >= 2)
                                {
                                    DataProcessUtil.sendDirect(msgitem.feedbackUrls[1]);
                                }
                            }
                            else
                            {
                                if (msgitem.feedbackUrls.Count >= 3)
                                {
                                    DataProcessUtil.sendDirect(msgitem.feedbackUrls[2]);
                                }
                            }
                        }
                    }
                }
            }
        }

        [DllImport("user32.dll")]
        public static extern IntPtr GetActiveWindow();//获得当前活动窗体
        [DllImport("user32.dll")]
        public static extern IntPtr SetActiveWindow(IntPtr hwnd);//设置活动窗体


        public void showForm(object sender, EventArgs e)
        {          
            string wavfile = Application.ExecutablePath;
            wavfile = wavfile.Substring(0, wavfile.LastIndexOf('\\') + 1);
            wavfile += "images\\msg.wav";

            PlayWav.PlaySound(wavfile, IntPtr.Zero, 0x00020000 | 0x0001);
             
            
            Popup f1 = sender as Popup;
            lock (popupsynobj)
            {
                prompwindows.Add(f1);
            }

            IntPtr activeWnd = GetActiveWindow();

            f1.TopMost = true;
            f1.Show();
            f1.BringToFront();

            SetActiveWindow(activeWnd);

            startFlashThread();
        }

        private void contextMenuStrip1_ItemClicked(object sender, ToolStripItemClickedEventArgs e)
        {
            ToolStripItem item = e.ClickedItem;
            if (item == null) return;

            if (item.Text.Equals("显示主窗口"))
            {
                LoginForm loginform = LoginForm.getInstance(false);
                loginform.Hide();
                List<UserInfo> users = loginform.getUsers();
                if (users != null && users.Count > 0)
                {
                    /*
                    if (this.WindowState == FormWindowState.Normal)
                    {
                        this.BringToFront();
                        this.Show();
                        msgChanged = true;
                        this.Focus();
                    }
                    else
                    {
                        this.TopMost = true;
                        this.Show();
                        msgChanged = true;
                        this.WindowState = FormWindowState.Normal;
                        this.Focus();
                    }
                     * */
                    this.Hide();
                    this.WindowState = FormWindowState.Minimized;
                    this.TopMost = true;                 
                    msgChanged = true;
                    this.Show();
                    this.WindowState = FormWindowState.Normal;
                    this.Focus();
                    
                }
                else
                {
                    loginform.Hide();

                    FirstNotifyWindow firstNotify = new FirstNotifyWindow();
                    firstNotify.setLoginWindow(loginform);
                    firstNotify.TopMost = true;
                    firstNotify.Show();
                }

            }              
            else if (item.Text.Equals("退出"))
            {
                clearPopup();
                forceExit = true;

                if (!exit)
                {
                    exit = true;
                    msgDetectThread.Join();
                }

                if (bFlashThreadRunning)
                {
                    if (!exitFlash)
                    {
                        exitFlash = true;
                        flashThread.Join();
                    }
                }

                Thread.Sleep(100);
                LoginForm.getInstance(false).ForceExit();
                Application.Exit();
            }
            else if (item.Text.Equals("进入一发系统"))
            {
                if (DataProcessUtil.isLogin())
                {
                    if (!URLNavigator.ffOpenURL(Constants.getYiFaHomepage(DataProcessUtil.getUserNo())))
                    {
                        Utils.FFUpdate();
                    }
                }
                else//还没登陆，显示登陆页
                {
                    LoginForm loginform = LoginForm.getInstance(true);
                    loginform.TopMost = true;
                    loginform.Show();
                }
            }
            else if (item.Text.Equals("切换帐号"))
            {
                LoginForm loginform = LoginForm.getInstance(false);
                /*
                if (loginform.WindowState == FormWindowState.Minimized)
                {
                    this.TopMost = true;
                    this.Show();
                    this.WindowState = FormWindowState.Normal;
                    this.Hide();
                    DataProcessUtil.logOut();
                    bSwitching = true;
                    loginform.BringToFront();
                    loginform.WindowState = FormWindowState.Normal;
                    loginform.Show();
                }
                else
                {
                    this.TopMost = true;
                    this.Show();
                    this.WindowState = FormWindowState.Normal;
                    this.Hide();
                    DataProcessUtil.logOut();
                    bSwitching = true;
                    loginform.TopMost = true;
                    if (loginform.WindowState == FormWindowState.Minimized)
                    {
                        loginform.Show();
                    }
                    else
                    {
                        loginform.BringToFront();
                        loginform.Show();
                    }
                }
                */

                loginform.Close();
                this.Hide();
                DataProcessUtil.logOut();
                bSwitching = true;
                loginform.TopMost = true;
                loginform.Show();
                loginform.WindowState = FormWindowState.Normal;
            }
        }


        public void showNotifyIcon()
        {
            notifyIcon1.Visible = true;
        }

        private void ClientMain_Deactivate(object sender, EventArgs e)
        {
            if (this.WindowState == FormWindowState.Minimized)
            {
                this.Hide();
            }
        }

        public void HideMain()
        {
            this.Hide();
        }


        public void CloseMain()
        {
            clearPopup();

            if (!exit && bManThreadRunning)
            {
                exit = true;
                msgDetectThread.Join();
            }
        }

        private void ClientMain_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (!forceExit)
            {
                /*
                if (Utils.getCloseWindownotifyflag())//是否需要显示提示窗口
                {
                    CloseNotifyWindow window = new CloseNotifyWindow();
                    window.TopMost = true;
                    window.ShowDialog();
                    if (window.DialogResult == DialogResult.Yes)
                    {
                        CloseMain();
                    }
                    else
                    {
                        e.Cancel = true;
                        HideMain();
                    }
                }
                else // 不需要显示提示窗口，根据之前设置的值进行判断
                {
                    bool bExitDirect = Utils.DirectExit();
                    if (bExitDirect)
                    {
                        CloseMain();
                    }
                    else
                    {
                        e.Cancel = true;
                        HideMain();
                    }
                }*/

                e.Cancel = true;
                HideMain();
            }

        }

        public void DisplayMessage(object sender, EventArgs e)
        {
            lock (synobj)
            {
                showMessage();
                msgChanged = false;
            }
        }

        private void showMessage()
        {
            label1.Text = "欢迎您," + DataProcessUtil.getUserName();
            panel2.Controls.Clear();
            panel2.HorizontalScroll.Enabled = false;
            int width = panel2.Width - 20;
            this.panel2.VerticalScroll.Value = 0;
            if (messages == null || messages.Count == 0)
            {
                Label nodatalabel = new Label();
                nodatalabel.Size = new Size(98, 21);
                nodatalabel.Parent = panel2;
                nodatalabel.Text = "暂无数据";
                nodatalabel.Location = new Point(82, 33);
                nodatalabel.ForeColor = Color.Red;
                nodatalabel.Font = new Font("宋体", 15.75f, FontStyle.Bold);
            }
            else
            {
                int startlocation = 0;
                int titlePanelIndex = 0;
                foreach (ClientAssortedMessage msg in messages)
                {
                    if (msg.items.Count == 0) continue;//没有子消息就不显示了

                    string scenario = msg.recommendScene;
                    string title = msg.title;
                    if (!string.IsNullOrEmpty(msg.msgNumber))
                    {
                        title += "(" + msg.msgNumber + ")";
                    }

                    string relatedtitle = msg.relatedTitle;
                    string relatedurl = msg.relatedUrl;
                    //graypanel
                    Panel graypanel = new Panel();
                    if (startlocation == 0)
                    {
                        graypanel.Size = new Size(width, 16);
                        graypanel.BackColor = Color.FromArgb(0xcc, 0xcc, 0xcc);
                        graypanel.BackgroundImageLayout = ImageLayout.Stretch;
                    }
                    else
                    {
                        graypanel.Size = new Size(width, 23);
                        graypanel.BackgroundImage = Properties.Resources.grayback;
                        graypanel.BackgroundImageLayout = ImageLayout.Stretch;
                    }
                    //graypanel.BackgroundImage = Properties.Resources.grayback;
                    graypanel.Parent = panel2;
                    graypanel.Location = new Point(0, startlocation);
                    graypanel.Click += new System.EventHandler(this.panel2_Click);
                    startlocation += graypanel.Height;
                    //title panel
                    Panel titlepanel = new Panel();
                    titlepanel.Size = new Size(width, 30);
                    Label newlabel = new Label();
                    newlabel.Size = new Size(120,30);
                    newlabel.Text = title;
                    newlabel.Location = new Point(10, 8);
                    newlabel.Font = new Font("宋体", 10f);
                    newlabel.ForeColor = Color.White;
                    newlabel.BackColor = Color.Transparent;
                    newlabel.Parent = titlepanel;
                    //link
                    LinkLabel linklabel = new LinkLabel();
                    linklabel.Text = relatedtitle + ">>";


                    linklabel.Location = new Point(width - linklabel.Width - 20, 9);
                    linklabel.Size = new Size(130, 20);
                    linklabel.LinkColor = Color.White;
                    linklabel.LinkBehavior = LinkBehavior.NeverUnderline;
                    linklabel.Tag = msg;
                    linklabel.BackColor = Color.Transparent;
                    linklabel.MouseEnter += new System.EventHandler(this.linkLabel_MouseEnter);
                    linklabel.MouseLeave += new System.EventHandler(this.panellinkLabel_MouseLeave);
                    linklabel.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.linkLabel_LinkClicked);
                    linklabel.Parent = titlepanel;

                    switch (titlePanelIndex % 4)
                    {
                        case 0:
                            {
                                titlepanel.BackgroundImage = Properties.Resources.background1;
                                break;
                            }

                        case 1:
                            {
                                titlepanel.BackgroundImage = Properties.Resources.background2;
                                break;
                            }

                        case 2:
                            {
                                titlepanel.BackgroundImage = Properties.Resources.background3;
                                break;
                            }

                        case 3:
                            {
                                titlepanel.BackgroundImage = Properties.Resources.background4;
                                break;
                            }

                        default:
                            break;
                    }

                    titlepanel.BackgroundImageLayout = ImageLayout.Stretch;
                    titlePanelIndex++;

                    //titlepanel.BackgroundImage = Properties.Resources.grayback;
                    titlepanel.Click += new System.EventHandler(this.panel2_Click);
                    titlepanel.Parent = panel2;
                    titlepanel.Location = new Point(0, startlocation);
                    startlocation += titlepanel.Height;
                    //contentpanel
                    Panel contentpanel = new Panel();
                    int contentheight = msg.items.Count * 22 + 14 + 14;
                    contentpanel.Size = new Size(width, contentheight);
                    for (int i = 0; i < msg.items.Count && i < 3; i++)
                    {
                        ClientAssortedMessageItem item = msg.items[i];
                        LinkLabel link = new LinkLabel();
                        link.Text = item.content == null ? "" : item.content.Length > 18 ? item.content.Substring(0, 18) + "..." : item.content;

                        int ypos = 14;
                        if (i == 1)
                        {
                            ypos = 38;
                        }
                        else if (i == 2)
                        {
                            ypos = 60;
                        }
                        link.Location = new Point(10, ypos);
                        link.Size = new Size(width, 22);
                        link.Tag = item;
                        link.Font = new Font("宋体", 9f);
                        link.LinkColor = Color.FromArgb(0x42, 0x42, 0x42);// Color.Black;
                        link.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.msgitem_LinkClicked);
                        link.LinkBehavior = LinkBehavior.NeverUnderline;
                        link.MouseEnter += new System.EventHandler(this.linkLabel_MouseEnter);
                        link.MouseLeave += new System.EventHandler(this.linkLabel_MouseLeave);

                        link.Parent = contentpanel;
                    }

                    contentpanel.AutoScroll = false;
                    contentpanel.Click += new System.EventHandler(this.panel2_Click);
                    contentpanel.Parent = panel2;
                    contentpanel.Location = new Point(0, startlocation);
                    startlocation += contentpanel.Height;
                }

                panel2.AutoScrollMinSize = new Size(0, panel2.Height + 20);
                panel2.HorizontalScroll.Visible = false;
                panel2.HorizontalScroll.Enabled = false;

            }

            panel2.Focus();
        }
        //图标双击，显示主窗口
        private void notifyIcon1_MouseDoubleClick(object sender, MouseEventArgs e)
        {          
            LoginForm loginform = LoginForm.getInstance(false);
            loginform.Hide();
            List<UserInfo> users = loginform.getUsers();
            if ((users != null && users.Count > 0) || true)
            {           
                this.TopMost = true;
                msgChanged = true;
                this.Hide();
                this.WindowState = FormWindowState.Minimized;
                this.Show();               
                this.WindowState = FormWindowState.Normal;
               
            }
        
            if (bFlashThreadRunning)
            {
                if (!exitFlash)
                {
                    exitFlash = true;
                    flashThread.Join();
                }
            }
            
        }
        //主界面进入一发系统
        private void linkLabel2_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            Utils.FFUpdate();
            URLNavigator.ffOpenURL(Constants.getYiFaHomepage(DataProcessUtil.getUserNo()));
        }
        //主界面切换帐号
        private void linkLabel3_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            LoginForm loginform = LoginForm.getInstance(false);
            /*
            if (loginform.WindowState == FormWindowState.Minimized)
            {
                this.Hide();
                DataProcessUtil.logOut();
                bSwitching = true;

                loginform.BringToFront();
                loginform.WindowState = FormWindowState.Normal;
                loginform.Show();
            }
            else
            {
                this.Hide();
                DataProcessUtil.logOut();
                bSwitching = true;

                loginform.TopMost = true;
                loginform.BringToFront();
                loginform.Show();
            }*/
            loginform.Close();
            this.Hide();
            DataProcessUtil.logOut();
            bSwitching = true;
            loginform.TopMost = true;
            loginform.Show();
            loginform.WindowState = FormWindowState.Normal;
        }
        //点击主窗口标题拦链接处理函数
        private void linkLabel_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            Utils.FFUpdate();
            bool bOpen = false;
            lock (synobj)
            {
                LinkLabel link = (LinkLabel)sender;
                ClientAssortedMessage msg = (ClientAssortedMessage)link.Tag;
                string url = msg.relatedUrl;
                bOpen = URLNavigator.ffOpenURL(url);
            }
        }
        //点击主窗口的消息链接处理函数
        private void msgitem_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            Utils.FFUpdate();
            bool bOpen = false;
            lock (synobj)
            {
                LinkLabel link = (LinkLabel)sender;
                ClientAssortedMessageItem msg = (ClientAssortedMessageItem)link.Tag;
                if (msg == null) return;

                msg.click();

                string url = msg.url;
                bOpen = URLNavigator.ffOpenURL(url);

                if (msg.clickTimes() > 2) return;
                if (msg.feedbackUrls.Count > 0)
                {
                    DataProcessUtil.sendDirect(msg.feedbackUrls[0]);
                }
            }
        }

        private void panel3_Click(object sender, EventArgs e)
        {
            panel2.Focus();
        }

        private void panel2_Click(object sender, EventArgs e)
        {
            panel2.Focus();
        }

        private void linkLabel_MouseEnter(object sender, EventArgs e)
        {
            LinkLabel lnklabel = (LinkLabel)sender;
            lnklabel.LinkColor = Color.Red;

            Object o = lnklabel.Tag;
            if (o != null)
            {
                try
                {
                    ClientAssortedMessageItem msgitem = (ClientAssortedMessageItem)o;

                    System.Windows.Forms.ToolTip toolTip2 = new ToolTip();
                    toolTip2.Active = true;
                    toolTip2.ShowAlways = true;
                    toolTip2.SetToolTip(lnklabel, msgitem.content);
                }
                catch (Exception ex)
                {
                }
            }
        }

        private void panellinkLabel_MouseLeave(object sender, EventArgs e)
        {
            LinkLabel lnklabel = (LinkLabel)sender;
            lnklabel.LinkColor = Color.White;

            // toolTip1.Hide(lnklabel);
            // toolTip1.Active = false;
        }

        private void linkLabel_MouseLeave(object sender, EventArgs e)
        {
            LinkLabel lnklabel = (LinkLabel)sender;
            lnklabel.LinkColor = Color.FromArgb(0x42, 0x42, 0x42);
        }



        private void button3_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void panel3_MouseDown(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left)
            {
                ReleaseCapture();
                SendMessage(this.Handle, 161, 2, 0);
            }
            base.OnMouseDown(e);
        }

        private void btnMin_Click(object sender, EventArgs e)
        {
            this.WindowState = FormWindowState.Minimized;
        }


        private void btnMin_MouseEnter(object sender, EventArgs e)
        {
            btnMin.BackgroundImage = Properties.Resources.鼠标经过背景;
            btnMin.Image = Properties.Resources.鼠标经过最小化;
        }

        private void btnMin_MouseLeave(object sender, EventArgs e)
        {
            btnMin.BackgroundImage = Properties.Resources.默认背景;
            btnMin.Image = Properties.Resources.默认最小化;
        }

        private void closeBtn_MouseEnter(object sender, EventArgs e)
        {
            closeBtn.BackgroundImage = Properties.Resources.鼠标经过背景;
            closeBtn.Image = Properties.Resources.鼠标经过关闭;
        }

        private void closeBtn_MouseLeave(object sender, EventArgs e)
        {
            closeBtn.BackgroundImage = Properties.Resources.默认背景;
            closeBtn.Image = Properties.Resources.默认关闭;
        }

        private void label1_MouseDown(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left)
            {
                ReleaseCapture();
                SendMessage(this.Handle, 161, 2, 0);
            }
            base.OnMouseDown(e);
        }

        private void ClientMain_Leave(object sender, EventArgs e)
        {
            this.Hide();
        }
        
        public void showIcon2(bool bShow)
        {
            if (!flashdisplay)
            {
                if (bNormalICON) return;

                Icon icon = new Icon("images\\bcgg.ico");
                notifyIcon1.Icon = icon;
                bNormalICON = true;
                return;
            }

            if (bShow)
            {
                Icon icon = new Icon("images\\bcgg.ico");
                notifyIcon1.Icon = icon;
                bNormalICON = true;
            }
            else
            {
                Icon icon = new Icon("images\\null.ico");
                notifyIcon1.Icon = icon;
                bNormalICON = false;
            }
        }

        public void stopFlash()
        {
            if (bFlashThreadRunning)
            {
                if (!exitFlash)
                {
                    exitFlash = true;
                    flashThread.Join();
                }
            }
        }

        private void notifyIcon1_MouseClick(object sender, MouseEventArgs e)
        {
            if (bFlashThreadRunning)
            {
                if (bFlashThreadRunning)
                {
                    if (!exitFlash)
                    {
                        exitFlash = true;
                        flashThread.Join();
                    }
                }


                LoginForm loginform = LoginForm.getInstance(false);
                loginform.Hide();
                List<UserInfo> users = loginform.getUsers();
                if ((users != null && users.Count > 0) || true)
                {
                    this.TopMost = true;
                    msgChanged = true;
                    this.Hide();
                    this.WindowState = FormWindowState.Minimized;
                    this.Show();
                    this.WindowState = FormWindowState.Normal;
                }
            }
            else if (e.Button == System.Windows.Forms.MouseButtons.Right)
            {
                contextMenuStrip1.Show();
            }

        }      
    }
}
