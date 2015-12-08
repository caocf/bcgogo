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
using Newtonsoft.Json;
using System.IO;
using System.Runtime.InteropServices;
using System.Security.Cryptography.X509Certificates;
 
namespace YiFaClient
{  
    
    public partial class LoginForm : Form
    {
        [DllImport("user32.dll")]
        public static extern bool SendMessage(IntPtr hwnd, int wMsg, int wParam, int lParam);      
        [DllImport("user32.dll")]
        public static extern bool ReleaseCapture();

        bool bBind = false;
        bool canAutoLogin = true;
        bool forceExit = false;
        bool bLogining = false;
        bool bAutoStart = true;
        List<UserInfo> users = null;
        Thread loginThread = null;
        List<LoginHandler> handlers = new List<LoginHandler>();
        static LoginForm instance = null;
        private LoginForm(bool auto)
        {
            InitializeComponent();
            canAutoLogin = auto;           
            init();
            username.Focus();
        }

        public List<UserInfo> getUsers()
        {
            return users;
        }

        public void ForceExit()
        {
            forceExit = true;
            this.Close();
        }

       
        public static LoginForm getInstance(bool auto)
        {
            if (instance == null)
            {
                instance = new LoginForm(true);
            }

            instance.canAutoLogin = auto;

            return instance;
        }

        
        //是否绑定帐户
        public bool BBind()
        {
            return true;
            //return bBind;
        }

        private void init()
        {           
            Control.CheckForIllegalCrossThreadCalls = false;
            string exePath = Application.ExecutablePath;

            versionlabel.Text += "(" + Constants.currentVersion + ")";
            
            string lastUserId ;
            int closeoption = 1;
            bool nevernotifyclose = false;
            string preversion;
            users = FileUtils.getUsers(out bAutoStart, out lastUserId, out closeoption, out nevernotifyclose, out preversion);
             
            Utils.showNotify(nevernotifyclose);
            Utils.setCloseMainDirec(closeoption == 2);

            checkBox1.Checked = true;
            checkBox2.Checked = true;

            checkBox3.Checked = true;
            if (users != null && users.Count > 0)
            {
                bBind = true;
                foreach(UserInfo u in users)
                {                  
                    username.Items.Add(u.userNo.Trim());
                }

                if (lastUserId.Length > 0)
                {
                    DataProcessUtil.setUserNo(lastUserId);
                    username.Text = lastUserId;
                }   
            }                      
        }
  

        private void loginBtn_Click(object sender, EventArgs e)
        {       
            if (username.Text.Trim().Length == 0 || username.Text == "用户名")
            {
                MessageBox.Show("请输入用户名。");
                username.Focus();
                return;
            }
            else if (password.Text.Trim().Length == 0 || password.Text == "密码")
            {
                MessageBox.Show("请输入密码。");
                password.Focus();
                return;
            }

            if (!bLogining)
            {
                loginThread = new Thread(new ThreadStart(Login));             
                loginThread.Start();
                showUI(true);
                bLogining = true;
            }
            else
            {
                if (handlers.Count > 0)
                {
                    handlers.ElementAt(handlers.Count-1).cancel();
                }
                showUI(false);
                loginThread.Abort();
                canAutoLogin = false;
                bLogining = false;
            }
        }

        private class LoginHandler : ReadDataComplectedEventHandler
        {
            LoginForm loginform = null;
            public LoginHandler(LoginForm form)
            {
                loginform = form;
            }
            bool bCancel = false;
            public void cancel()
            {
                bCancel = true;
            }
            public void Error(string errMsg)
            {
                loginform.TopMost = false;
                MessageBox.Show("登录失败:"+errMsg);
                loginform.cancelLogin();
            }

            public void Handler(bool bTest, String response)
            {
                if (bTest)
                {
                    loginform.processLoginSuccess();
                    return;
                }
                if (!bCancel)
                {
                    DataProcessUtil.parseLoginResponse(response);

                    if (DataProcessUtil.isLogin())
                    {
                        loginform.processLoginSuccess();
                    }
                    else
                    {
                        loginform.TopMost = false;
                        MessageBox.Show("登录失败!");
                      
                        loginform.cancelLogin();
                    }
                }              
            }
        }

        private void processLoginSuccess()
        {
            this.Invoke(new System.EventHandler(this.loginBtn_Click), new object[] { null, null }); 
            this.Hide();
            if (username.Text.Trim().Length != 0 && password.Text.Trim().Length != 0)
            {
                FileUtils.saveUsers(true/*checkBox3.Checked*/, Utils.DirectExit()?2:1, !Utils.getCloseWindownotifyflag(), username.Text.Trim(), password.Text.Trim(), checkBox1.Checked ? "true" : "false", checkBox2.Checked ? "true" : "false");
                bool autoStart;
                string lastUserId;
                int closeoption = 1;
                bool nevernotifyclose = false;
                string preversion;
                users = FileUtils.getUsers(out autoStart, out lastUserId, out closeoption,out nevernotifyclose, out preversion);
               // button1.Visible = true;
                if (checkBox1.Checked == false)
                {
                    password.PasswordChar = '\0';
                    password.Text = "密码";
                }
            }

            handlers.Clear();
           
            Utils.getMainInstance().switchUser();
           
            Utils.getMainInstance().WindowState = FormWindowState.Normal;      
            Utils.getMainInstance().TopMost = true;
            Utils.getMainInstance().Show();

            //Utils.FFUpdate();
            //URLNavigator.ffOpenURL(Constants.getYiFaHomepage(DataProcessUtil.getUserNo()));
        }

        private void showUI(bool bLogin)
        {
            if (bLogin)
            {
                waitingpanel.Visible = true;
                panel1.Visible = false;
                loginBtn.BackgroundImage = Properties.Resources.quxiao;
            }
            else
            {
                waitingpanel.Visible = false;
                panel1.Visible = true;
                label1.Visible = true;
                loginBtn.BackgroundImage = Properties.Resources.loginbtn;
            }
        }

        private void cancelLogin()
        {
            showUI(false);
            loginThread.Abort();
            canAutoLogin = false;
            bLogining = false;
            if (username.Text.Trim().Length != 0 && password.Text.Trim().Length != 0)
            {            
                FileUtils.saveUsers(true/*checkBox3.Checked*/, Utils.DirectExit() ? 2 : 1, !Utils.getCloseWindownotifyflag(), username.Text.Trim(), password.Text.Trim(), checkBox1.Checked ? "true" : "false", checkBox2.Checked ? "true" : "false");
                bool autoStart;
                string lastUserId;
                int closeoption = 1;
                bool nevernotifyclose = false;
                string preversion;
                users = FileUtils.getUsers(out autoStart, out lastUserId, out closeoption, out nevernotifyclose, out preversion);
                //button1.Visible = true;
                if (checkBox1.Checked == false)
                {
                    password.PasswordChar = '\0';
                    password.Text = "密码";
                }
            }
        }

        private void Login()
        {
            string userNo = username.Text;
            string pass = password.Text;
            LoginHandler handler = new LoginHandler(this);
            handlers.Add(handler);
            DataProcessUtil.ayncLogin(false,userNo, pass, handler);     
        }

        private void username_TextChanged(object sender, EventArgs e)
        {
            bool auto = false;
            bool find = false;
          
            //button1.Visible = false;
            checkBox1.Checked = true;
            checkBox2.Checked = true;
            if (users != null && username.Text.Trim().Length > 0)
            {
                //button1.Visible = true;
                foreach (UserInfo u in users)
                {
                    if (u.userNo.Trim() == username.Text.Trim())
                    {
                        checkBox2.Checked = u.autoLogin.Equals("true");
                        checkBox1.Checked = u.savePass.Equals("true");

                        auto = checkBox2.Checked;

                        if (u.savePass.Equals("true"))
                        {
                            password.Text = u.userPass;
                            password.PasswordChar = '*';
                        }
                        else
                        {
                            password.PasswordChar = '\0';
                            password.Text = "";
                        }

                        find = true;

                        break;
                    }
                }
            }
            else
            {
                password.Text = "密码";
                password.PasswordChar = '\0';
            }

            if (find)
            {
                //button1.Visible = true;
            }
            else
            {
               // button1.Visible = false;
            }

            if (auto && canAutoLogin && username.Text.Trim().Length > 0 && password.Text.Trim().Length > 0)
            {
                //loginBtn_Click(null, null);      
                Thread loginthread = new Thread(new ThreadStart(Autologin));
                loginthread.Start();
            }
        }

        private void Autologin()
        {                                
            while (true)
            {
                Thread.Sleep(200);
                try
                {
                    this.Invoke(new System.EventHandler(this.loginBtn_Click), new object[] { null, null });
                    break;
                }
                catch (System.InvalidOperationException ex)
                {
                }
            }
        }
   
        private void checkBox2_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox2.Checked)
            {
                checkBox1.Checked = true;
            }
        }

        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox1.Checked == false)
            {
                if (checkBox2.Checked)
                {
                    checkBox2.Checked = false;
                }
            }
        }

        private void contextMenuStrip1_ItemClicked(object sender, ToolStripItemClickedEventArgs e)
        {
             ToolStripItem item = e.ClickedItem;

             if (item.Text.Equals("删除"))
             {
                 string userno = username.Text.Trim();
                 if (users != null && users.Count > 0)
                 {
                     FileUtils.removeUser(userno);
                 }

                 username.Items.Remove(userno);
             }
        }

        private void checkBox3_CheckedChanged(object sender, EventArgs e)
        {
            bool bAutostart = true /*checkBox3.Checked*/;
            string startpath = Environment.GetFolderPath(Environment.SpecialFolder.Programs) + "\\一发软件\\YiFaClient.appref-ms";
           
            String pathOfYifaClient = startpath;
           
            bool bSetSuccess = AutoStartUtil.SetAutoStart(bAutostart, pathOfYifaClient);
            if (bSetSuccess)
            {
                FileUtils.setAutoStart(bAutostart, Utils.DirectExit() ? 2 : 1, !Utils.getCloseWindownotifyflag());
                bAutoStart = bAutostart;
            }
            else
            {
                checkBox3.Checked = true; /* bAutoStart*/;
            }
        }

        private void linkforgetpass_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            if (Constants.forgetpassword.Length > 0)
            {
                if (!URLNavigator.ffOpenURL(Constants.forgetpassword))
                {
                    URLNavigator.defaultOpenURL(Constants.forgetpassword);
                }
            }
        }

        private void button1_Click(object sender, EventArgs e)
        {
            if (username.Text.Trim().Length > 0)
            {
                string userno = username.Text.Trim();
                if (users != null && users.Count > 0)
                {
                    FileUtils.removeUser(userno);
                }

                //button1.Visible = false;
                username.Text = "";
                checkBox1.Checked = true;
                checkBox2.Checked = true;
                username.Items.Remove(userno);
                if (users != null)
                {
                    foreach (UserInfo u in users)
                    {
                        if (u.userNo.Trim().Equals(userno))
                        {
                            users.Remove(u);
                            break;
                        }
                    }
                }
            }
        }

        private void label1_MouseEnter(object sender, EventArgs e)
        {
            toolTip1.Active = true;
        }

        private void label1_MouseLeave(object sender, EventArgs e)
        {
            toolTip1.Active = false;
        }

        private void LoginForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (forceExit == false)
            {
                e.Cancel = true;
                this.Hide();
            }
        }

        private void username_KeyDown(object sender, KeyEventArgs e)
        {
            if (username.Text == "用户名")
            {
                username.Text = "";
            }
        }

        private void username_Leave(object sender, EventArgs e)
        {
            if (username.Text.Trim() == "")
            {
                username.Text = "用户名";
            }
        }

        private void username_MouseDown(object sender, MouseEventArgs e)
        {
            if (username.Text == "用户名")
            {
                username.Text = "";
            }
        }

        private void password_KeyDown(object sender, KeyEventArgs e)
        {
            password.PasswordChar = '*';
            if (password.Text == "密码")
            {
                
                password.Text = "";
            }
        }

        private void password_MouseDown(object sender, MouseEventArgs e)
        {
            if (password.Text == "密码")
            {
                password.PasswordChar = '*';
                password.Text = "";
            }
        }

        private void password_Leave(object sender, EventArgs e)
        {
            if (password.Text.Trim() == "")
            {
                
                password.Text = "密码";
                password.PasswordChar = '\0';
            }
        }
        /*
        private void button1_MouseEnter(object sender, EventArgs e)
        {
            button1.BackgroundImage = Properties.Resources.delete2;
        }

        private void button1_MouseLeave(object sender, EventArgs e)
        {
            button1.BackgroundImage = Properties.Resources.delete;
        }*/

        private void label1_Click(object sender, EventArgs e)
        {
            forgetPassNotify notify = new forgetPassNotify();
            notify.TopMost = true;
            notify.Show();
        }

        private void registeraccount_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            if (!URLNavigator.ffOpenURL(Constants.registerurl))//首先尝试firefox
            {
                URLNavigator.defaultOpenURL(Constants.registerurl);
            }
        }

        private void titlapanel_MouseDown(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left)
            {
                ReleaseCapture();
                SendMessage(this.Handle, 161, 2, 0);
            }
            base.OnMouseDown(e);
            
        }

        private void button1_Click_1(object sender, EventArgs e)
        {
            this.Hide();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            this.WindowState = FormWindowState.Minimized;
        }

        private void button2_MouseEnter(object sender, EventArgs e)
        {
            button2.BackgroundImage = Properties.Resources.鼠标经过背景;
            button2.Image = Properties.Resources.鼠标经过最小化;
        }

        private void button2_MouseLeave(object sender, EventArgs e)
        {
            button2.BackgroundImage = Properties.Resources.默认背景;
            button2.Image = Properties.Resources.默认最小化;
        }

        private void button1_MouseEnter(object sender, EventArgs e)
        {
            button1.BackgroundImage = Properties.Resources.鼠标经过背景;
            button1.Image = Properties.Resources.鼠标经过关闭;
        }

        private void button1_MouseLeave(object sender, EventArgs e)
        {
            button1.BackgroundImage = Properties.Resources.默认背景;
            button1.Image = Properties.Resources.默认关闭;
        }

        private void label2_MouseDown(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left)
            {
                ReleaseCapture();
                SendMessage(this.Handle, 161, 2, 0);
            }
            base.OnMouseDown(e);
        }

      

      
    }
}
