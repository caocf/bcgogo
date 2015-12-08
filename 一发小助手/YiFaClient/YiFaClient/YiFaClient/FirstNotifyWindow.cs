using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using YiFaComm;
using Microsoft.Win32;
using System.Runtime.InteropServices;
namespace YiFaClient
{
    public partial class FirstNotifyWindow : Form
    {
        [DllImport("user32.dll")]
        public static extern bool SendMessage(IntPtr hwnd, int wMsg, int wParam, int lParam);
        [DllImport("user32.dll")]
        public static extern bool ReleaseCapture();
        LoginForm loginwindow = null;
    
       
        public FirstNotifyWindow()
        {
            InitializeComponent();
        }    

        public void setLoginWindow(LoginForm loginwnd)
        {
            this.loginwindow = loginwnd;
        }

        private void linkLabel2_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            this.Close();
            loginwindow.WindowState = FormWindowState.Normal;
            loginwindow.Show();
        }

        private void linkLabel1_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            if (!URLNavigator.ffOpenURL(Constants.registerurl))//首先尝试firefox
            {
                URLNavigator.defaultOpenURL(Constants.registerurl);
            }
        }

        private void button3_Click(object sender, EventArgs e)
        {
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

        private void label4_MouseDown(object sender, MouseEventArgs e)
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
