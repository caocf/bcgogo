using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Runtime.InteropServices;
using YiFaComm;
using System.Threading;

namespace YiFaClient
{
    public partial class Popup : Form
    {
        [DllImport("user32")]
        private static extern bool AnimateWindow(IntPtr hwnd, int dwTime, int dwFlags);
        private const int AW_HOR_POSITIVE = 0x0001;//自左向右显示窗口,该标记可以在迁移转变动画和滑动动画中应用。应用AW_CENTER标记时忽视该标记
        private const int AW_HOR_NEGATIVE = 0x0002;//自右向左显示窗口,该标记可以在迁移转变动画和滑动动画中应用。应用AW_CENTER标记时忽视该标记
        private const int AW_VER_POSITIVE = 0x0004;//自顶向下显示窗口,该标记可以在迁移转变动画和滑动动画中应用。应用AW_CENTER标记时忽视该标记
        private const int AW_VER_NEGATIVE = 0x0008;//自下向上显示窗口,该标记可以在迁移转变动画和滑动动画中应用。应用AW_CENTER标记时忽视该标记该标记
        private const int AW_CENTER = 0x0010;//若应用了AW_HIDE标记,则使窗口向内重叠;不然向外扩大
        private const int AW_HIDE = 0x10000;//隐蔽窗口
        private const int AW_ACTIVE = 0x20000;//激活窗口,在应用了AW_HIDE标记后不要应用这个标记
        private const int AW_SLIDE = 0x40000;//应用滑动类型动画结果,默认为迁移转变动画类型,当应用AW_CENTER标记时,这个标记就被忽视
        private const int AW_BLEND = 0x80000;//应用淡入淡出结果
        [DllImport("user32.dll")]
        public static extern bool SendMessage(IntPtr hwnd, int wMsg, int wParam, int lParam);
        [DllImport("user32.dll")]
        public static extern bool ReleaseCapture();

        private long showtime = 0;
        ClientPrompt cltPrompt;
        Thread timedetectThread = null;
        bool bExist = false;
        int closeType = 0;
        bool fromCloseExit = false;
        bool timeout = false;

       
        public Popup(ClientPrompt prompt)
        {
            InitializeComponent();
            this.cltPrompt = prompt;
            title.Text = cltPrompt.title;
            content.Text = cltPrompt.content;
            closeType = 3;
            if (cltPrompt.url == null || cltPrompt.url.Trim().Length == 0)
            {
                linkLabel1.Visible = false;
            }
        }

        public void ExitWindows()
        {
            bExist = true;
        }

    
        public void startDetectThread()
        {
            timedetectThread = new Thread(new ThreadStart(MsgDetectMain));
            timedetectThread.Start();
        }



        private void MsgDetectMain()
        {
            try
            {
                while (!bExist)
                {                                                     
                    Thread.Sleep(200);

                    if (nowTime() > 30)
                    {
                      
                        break;
                    }
                }
                timeout = true;
                if (!fromCloseExit)
                {
                    this.Close();
                }
            }
            catch (Exception ex)
            {

            }
        }

      
        private void Popup_Load(object sender, EventArgs e)
        {
            int x = Screen.PrimaryScreen.WorkingArea.Right - this.Width;
            int y = Screen.PrimaryScreen.WorkingArea.Bottom - this.Height;
            this.Location = new Point(x, y);//设置窗体在屏幕右下角显示
            AnimateWindow(this.Handle, 1000, AW_SLIDE | AW_ACTIVE | AW_VER_NEGATIVE);

            showtime = DateTime.Now.Ticks;
            startDetectThread();
        }
        //距离窗口打开的时间
        public long nowTime()
        {
            long nowticks = DateTime.Now.Ticks;
            long delttime = nowticks - showtime;
            long nSec = delttime / 10000000;
            return nSec;
        }


        private void Popup_FormClosed(object sender, FormClosedEventArgs e)
        {
            Utils.getMainInstance().removePopup(this);
            //用户点击了该消息，反馈
            if (cltPrompt.feedbackUrls.Count > 0)
            {
                if (closeType == 1) //点击了超连接
                {
                    DataProcessUtil.sendDirect(cltPrompt.feedbackUrls[0]);
                }
                else if (closeType == 3)//30秒时间到,自动关闭
                {
                    if (cltPrompt.feedbackUrls.Count >= 3)
                    {
                        DataProcessUtil.sendDirect(cltPrompt.feedbackUrls[2]);
                    }
                }
                else
                {
                    if (cltPrompt.feedbackUrls.Count >= 2) //点击了关闭按钮
                    {
                        DataProcessUtil.sendDirect(cltPrompt.feedbackUrls[1]);
                    }
                }
            }

            if (!timeout)
            {
                fromCloseExit = true;
                bExist = true;
                timedetectThread.Join();
            }

           

        }

        private void linkLabel1_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            if (cltPrompt != null && cltPrompt.url != null && cltPrompt.url.Trim().Length > 0)
            {
                if (!URLNavigator.ffOpenURL(cltPrompt.url))
                {
                    Utils.FFUpdate();
                }

                //用户点击了该消息，反馈
                closeType = 1;
                bExist = true;
                //timedetectThread.Join();
            }
        }

        private void button3_Click(object sender, EventArgs e)
        {
            closeType = 2;
            this.Close();
        }

        private void panel1_MouseDown(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left)
            {
                ReleaseCapture();
                SendMessage(this.Handle, 161, 2, 0);
            }
            base.OnMouseDown(e);
        }

        private void button3_MouseEnter(object sender, EventArgs e)
        {
            button3.BackgroundImage = Properties.Resources.鼠标经过背景;
            button3.Image = Properties.Resources.鼠标经过关闭;
        }

        private void button3_MouseLeave(object sender, EventArgs e)
        {
            button3.BackgroundImage = Properties.Resources.默认背景;
            button3.Image = Properties.Resources.默认关闭;
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

        private void content_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            if (cltPrompt != null && cltPrompt.url!=null && cltPrompt.url.Trim().Length > 0)
            {
                if (!URLNavigator.ffOpenURL(cltPrompt.url))
                {
                    Utils.FFUpdate();
                }

                //用户点击了该消息，反馈
                closeType = 1;
                bExist = true;
                //timedetectThread.Join();
            }
        }
    }
}
