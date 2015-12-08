namespace YiFaClient
{
    partial class LoginForm
    {
        /// <summary>
        /// 必需的设计器变量。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// 清理所有正在使用的资源。
        /// </summary>
        /// <param name="disposing">如果应释放托管资源，为 true；否则为 false。</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows 窗体设计器生成的代码

        /// <summary>
        /// 设计器支持所需的方法 - 不要
        /// 使用代码编辑器修改此方法的内容。
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(LoginForm));
            this.toolTip1 = new System.Windows.Forms.ToolTip(this.components);
            this.label1 = new System.Windows.Forms.Label();
            this.username = new System.Windows.Forms.ComboBox();
            this.password = new System.Windows.Forms.TextBox();
            this.panel3 = new System.Windows.Forms.Panel();
            this.waitingpanel = new System.Windows.Forms.Panel();
            this.panel1 = new System.Windows.Forms.Panel();
            this.registeraccount = new System.Windows.Forms.LinkLabel();
            this.checkBox2 = new System.Windows.Forms.CheckBox();
            this.checkBox1 = new System.Windows.Forms.CheckBox();
            this.loginBtn = new System.Windows.Forms.Button();
            this.titlapanel = new System.Windows.Forms.Panel();
            this.button2 = new System.Windows.Forms.Button();
            this.versionlabel = new System.Windows.Forms.Label();
            this.button1 = new System.Windows.Forms.Button();
            this.panel2 = new System.Windows.Forms.Panel();
            this.checkBox3 = new System.Windows.Forms.CheckBox();
            this.panel3.SuspendLayout();
            this.panel1.SuspendLayout();
            this.titlapanel.SuspendLayout();
            this.panel2.SuspendLayout();
            this.SuspendLayout();
            // 
            // toolTip1
            // 
            this.toolTip1.IsBalloon = true;
            this.toolTip1.ShowAlways = true;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Pixel);
            this.label1.ForeColor = System.Drawing.Color.DodgerBlue;
            this.label1.Location = new System.Drawing.Point(197, 66);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(59, 12);
            this.label1.TabIndex = 8;
            this.label1.Text = "忘记密码?";
            this.toolTip1.SetToolTip(this.label1, "请联系客服找回密码,客服电话:0512-85187956/400-6388528  客服QQ:800060787");
            this.label1.Click += new System.EventHandler(this.label1_Click);
            this.label1.MouseEnter += new System.EventHandler(this.label1_MouseEnter);
            this.label1.MouseLeave += new System.EventHandler(this.label1_MouseLeave);
            // 
            // username
            // 
            this.username.Font = new System.Drawing.Font("宋体", 14F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Pixel);
            this.username.ForeColor = System.Drawing.SystemColors.ButtonShadow;
            this.username.FormattingEnabled = true;
            this.username.Location = new System.Drawing.Point(20, 18);
            this.username.Name = "username";
            this.username.Size = new System.Drawing.Size(173, 22);
            this.username.TabIndex = 0;
            this.username.Text = "用户名";
            this.toolTip1.SetToolTip(this.username, "用户名");
            this.username.TextChanged += new System.EventHandler(this.username_TextChanged);
            this.username.KeyDown += new System.Windows.Forms.KeyEventHandler(this.username_KeyDown);
            this.username.Leave += new System.EventHandler(this.username_Leave);
            this.username.MouseDown += new System.Windows.Forms.MouseEventHandler(this.username_MouseDown);
            // 
            // password
            // 
            this.password.Font = new System.Drawing.Font("宋体", 14F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Pixel);
            this.password.ForeColor = System.Drawing.SystemColors.ButtonShadow;
            this.password.Location = new System.Drawing.Point(19, 56);
            this.password.MaxLength = 20;
            this.password.Name = "password";
            this.password.Size = new System.Drawing.Size(173, 23);
            this.password.TabIndex = 1;
            this.password.Text = "密码";
            this.toolTip1.SetToolTip(this.password, "密码");
            this.password.KeyDown += new System.Windows.Forms.KeyEventHandler(this.password_KeyDown);
            this.password.MouseDown += new System.Windows.Forms.MouseEventHandler(this.password_MouseDown);
            this.password.MouseLeave += new System.EventHandler(this.password_Leave);
            // 
            // panel3
            // 
            this.panel3.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.panel3.Controls.Add(this.waitingpanel);
            this.panel3.Controls.Add(this.panel1);
            this.panel3.Controls.Add(this.loginBtn);
            this.panel3.Dock = System.Windows.Forms.DockStyle.Fill;
            this.panel3.Location = new System.Drawing.Point(0, 24);
            this.panel3.Name = "panel3";
            this.panel3.Size = new System.Drawing.Size(280, 182);
            this.panel3.TabIndex = 14;
            // 
            // waitingpanel
            // 
            this.waitingpanel.BackgroundImage = global::YiFaClient.Properties.Resources.loginprogress;
            this.waitingpanel.Location = new System.Drawing.Point(21, 79);
            this.waitingpanel.Name = "waitingpanel";
            this.waitingpanel.Size = new System.Drawing.Size(151, 17);
            this.waitingpanel.TabIndex = 13;
            this.waitingpanel.Visible = false;
            // 
            // panel1
            // 
            this.panel1.BackColor = System.Drawing.SystemColors.Control;
            this.panel1.Controls.Add(this.registeraccount);
            this.panel1.Controls.Add(this.label1);
            this.panel1.Controls.Add(this.username);
            this.panel1.Controls.Add(this.password);
            this.panel1.Controls.Add(this.checkBox2);
            this.panel1.Controls.Add(this.checkBox1);
            this.panel1.Location = new System.Drawing.Point(1, 3);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(275, 129);
            this.panel1.TabIndex = 10;
            // 
            // registeraccount
            // 
            this.registeraccount.AutoSize = true;
            this.registeraccount.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Pixel);
            this.registeraccount.LinkBehavior = System.Windows.Forms.LinkBehavior.NeverUnderline;
            this.registeraccount.LinkColor = System.Drawing.Color.DodgerBlue;
            this.registeraccount.Location = new System.Drawing.Point(197, 24);
            this.registeraccount.Name = "registeraccount";
            this.registeraccount.Size = new System.Drawing.Size(53, 12);
            this.registeraccount.TabIndex = 9;
            this.registeraccount.TabStop = true;
            this.registeraccount.Text = "注册账号";
            this.registeraccount.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.registeraccount_LinkClicked);
            // 
            // checkBox2
            // 
            this.checkBox2.AutoSize = true;
            this.checkBox2.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Pixel);
            this.checkBox2.Location = new System.Drawing.Point(115, 97);
            this.checkBox2.Name = "checkBox2";
            this.checkBox2.Size = new System.Drawing.Size(72, 16);
            this.checkBox2.TabIndex = 3;
            this.checkBox2.Text = "自动登录";
            this.checkBox2.UseVisualStyleBackColor = true;
            this.checkBox2.CheckedChanged += new System.EventHandler(this.checkBox2_CheckedChanged);
            // 
            // checkBox1
            // 
            this.checkBox1.AutoSize = true;
            this.checkBox1.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Pixel);
            this.checkBox1.Location = new System.Drawing.Point(20, 97);
            this.checkBox1.Name = "checkBox1";
            this.checkBox1.Size = new System.Drawing.Size(72, 16);
            this.checkBox1.TabIndex = 2;
            this.checkBox1.Text = "记住密码";
            this.checkBox1.UseVisualStyleBackColor = true;
            this.checkBox1.CheckedChanged += new System.EventHandler(this.checkBox1_CheckedChanged);
            // 
            // loginBtn
            // 
            this.loginBtn.BackgroundImage = global::YiFaClient.Properties.Resources.loginbtn;
            this.loginBtn.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Stretch;
            this.loginBtn.FlatAppearance.BorderSize = 0;
            this.loginBtn.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
            this.loginBtn.ForeColor = System.Drawing.Color.AliceBlue;
            this.loginBtn.Location = new System.Drawing.Point(20, 141);
            this.loginBtn.Name = "loginBtn";
            this.loginBtn.Size = new System.Drawing.Size(176, 31);
            this.loginBtn.TabIndex = 4;
            this.loginBtn.UseVisualStyleBackColor = true;
            this.loginBtn.Click += new System.EventHandler(this.loginBtn_Click);
            // 
            // titlapanel
            // 
            this.titlapanel.BackColor = System.Drawing.Color.Transparent;
            this.titlapanel.BackgroundImage = global::YiFaClient.Properties.Resources.titleback;
            this.titlapanel.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Stretch;
            this.titlapanel.Controls.Add(this.button2);
            this.titlapanel.Controls.Add(this.versionlabel);
            this.titlapanel.Controls.Add(this.button1);
            this.titlapanel.Dock = System.Windows.Forms.DockStyle.Top;
            this.titlapanel.Location = new System.Drawing.Point(0, 0);
            this.titlapanel.Name = "titlapanel";
            this.titlapanel.Size = new System.Drawing.Size(280, 24);
            this.titlapanel.TabIndex = 13;
            this.titlapanel.MouseDown += new System.Windows.Forms.MouseEventHandler(this.titlapanel_MouseDown);
            // 
            // button2
            // 
            this.button2.BackgroundImage = global::YiFaClient.Properties.Resources.默认背景;
            this.button2.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Center;
            this.button2.Dock = System.Windows.Forms.DockStyle.Right;
            this.button2.FlatAppearance.BorderSize = 0;
            this.button2.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.button2.Image = global::YiFaClient.Properties.Resources.默认最小化;
            this.button2.Location = new System.Drawing.Point(234, 0);
            this.button2.Name = "button2";
            this.button2.Size = new System.Drawing.Size(23, 24);
            this.button2.TabIndex = 2;
            this.button2.UseVisualStyleBackColor = false;
            this.button2.Click += new System.EventHandler(this.button2_Click);
            this.button2.MouseEnter += new System.EventHandler(this.button2_MouseEnter);
            this.button2.MouseLeave += new System.EventHandler(this.button2_MouseLeave);
            // 
            // versionlabel
            // 
            this.versionlabel.AutoSize = true;
            this.versionlabel.Font = new System.Drawing.Font("宋体", 14F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Pixel);
            this.versionlabel.ForeColor = System.Drawing.Color.White;
            this.versionlabel.Location = new System.Drawing.Point(4, 6);
            this.versionlabel.Name = "versionlabel";
            this.versionlabel.Size = new System.Drawing.Size(77, 14);
            this.versionlabel.TabIndex = 1;
            this.versionlabel.Text = "一发小助手";
            this.versionlabel.MouseDown += new System.Windows.Forms.MouseEventHandler(this.label2_MouseDown);
            // 
            // button1
            // 
            this.button1.BackgroundImage = global::YiFaClient.Properties.Resources.默认背景;
            this.button1.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Center;
            this.button1.Dock = System.Windows.Forms.DockStyle.Right;
            this.button1.FlatAppearance.BorderSize = 0;
            this.button1.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.button1.Image = global::YiFaClient.Properties.Resources.默认关闭;
            this.button1.Location = new System.Drawing.Point(257, 0);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(23, 24);
            this.button1.TabIndex = 0;
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click_1);
            this.button1.MouseEnter += new System.EventHandler(this.button1_MouseEnter);
            this.button1.MouseLeave += new System.EventHandler(this.button1_MouseLeave);
            // 
            // panel2
            // 
            this.panel2.BackColor = System.Drawing.SystemColors.ButtonFace;
            this.panel2.BackgroundImage = global::YiFaClient.Properties.Resources.loginbottom;
            this.panel2.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Stretch;
            this.panel2.Controls.Add(this.checkBox3);
            this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.panel2.Location = new System.Drawing.Point(0, 206);
            this.panel2.Name = "panel2";
            this.panel2.Size = new System.Drawing.Size(280, 24);
            this.panel2.TabIndex = 11;
            // 
            // checkBox3
            // 
            this.checkBox3.AutoSize = true;
            this.checkBox3.BackColor = System.Drawing.Color.Transparent;
            this.checkBox3.Location = new System.Drawing.Point(199, 6);
            this.checkBox3.Name = "checkBox3";
            this.checkBox3.Size = new System.Drawing.Size(72, 16);
            this.checkBox3.TabIndex = 5;
            this.checkBox3.Text = "开机启动";
            this.checkBox3.UseVisualStyleBackColor = false;
            this.checkBox3.Visible = false;
            this.checkBox3.CheckedChanged += new System.EventHandler(this.checkBox3_CheckedChanged);
            // 
            // LoginForm
            // 
            this.AcceptButton = this.loginBtn;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackgroundImageLayout = System.Windows.Forms.ImageLayout.None;
            this.ClientSize = new System.Drawing.Size(280, 230);
            this.Controls.Add(this.panel3);
            this.Controls.Add(this.titlapanel);
            this.Controls.Add(this.panel2);
            this.DoubleBuffered = true;
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MaximizeBox = false;
            this.Name = "LoginForm";
            this.ShowIcon = false;
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "一发软件";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.LoginForm_FormClosing);
            this.panel3.ResumeLayout(false);
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            this.titlapanel.ResumeLayout(false);
            this.titlapanel.PerformLayout();
            this.panel2.ResumeLayout(false);
            this.panel2.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button loginBtn;
        private System.Windows.Forms.CheckBox checkBox3;
        private System.Windows.Forms.ToolTip toolTip1;
        private System.Windows.Forms.Panel panel2;
        private System.Windows.Forms.Panel titlapanel;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.Label versionlabel;
        private System.Windows.Forms.Panel panel3;
        private System.Windows.Forms.Panel waitingpanel;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.LinkLabel registeraccount;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.ComboBox username;
        private System.Windows.Forms.TextBox password;
        private System.Windows.Forms.CheckBox checkBox2;
        private System.Windows.Forms.CheckBox checkBox1;
        private System.Windows.Forms.Button button2;
    }
}

