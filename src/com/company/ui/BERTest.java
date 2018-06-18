package com.company.ui;

import com.company.rs232.DSerialPort;
import com.company.util.FontEnum;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class BERTest {
    /**
     * 主panel网格布局参数
     */
    private static final int DEFAULT_GRID_ROWS = 7;
    private static final int DEFAULT_GRID_COLUMN = 1;
    /**
     * 主 panel 默认大小
     */
    private static final int DEFAULT_PANEL_WIDTH = 220;
    private static final int DEFAULT_PANEL_HEIGHT = 300;
    /**
     * 组件标题
     */
    private static final String BORDER_TITLE = "AD9361设置";
    private static MyItemListener itemListener2 = new MyItemListener();
    private static MyItemListener itemListener3 = new MyItemListener();
    private static JRadioButton receive,send,singleTon,modulation;
    private static JTextField magnitude,decrease;
    //模式   1->单音  0->调制
    private static int mode;
    //收发    0->发  1->收
    private static int rxtx;
    //下拉框
    private static JComboBox<String> frequency,receiveFrequency;
    //串口
    private static DSerialPort portUsed;
    //发送的前缀
    private static byte[] PRE = new byte[]{-21,-112,8,85,85,0,0};
    private static int f,fr;
    //频点
    private static final String[] FREQUENCY = {"400M","500M","800M","1.2G","1.5G",
            "1.6G","2G","2.2G","2.4G","2.5G","2.8G","3G","3.2G","3.5G","3.6G","4G"};
    public static JPanel createBERTestPanel(DSerialPort port) {
        /**
         * 相关参数初始化
         */
        portUsed = port;
        mode = 1;
        rxtx = 1;
        JPanel panel = new JPanel(new GridLayout(DEFAULT_GRID_ROWS,DEFAULT_GRID_COLUMN));
        //设置颜色
        panel.setBackground(Color.WHITE);

        //收发设置   1->收  0->发
        JPanel RxtxPanel = new JPanel(new GridLayout(1,3));
        RxtxPanel.setBackground(Color.WHITE);
        JLabel Rxtxlabel = new JLabel("收|发：");
        Rxtxlabel.setBackground(Color.WHITE);
        Rxtxlabel.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        RxtxPanel.add(Rxtxlabel);
        ButtonGroup buttonGroup2 = new ButtonGroup();
        //收
        receive = new JRadioButton("收");
        receive.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        receive.setBackground(Color.WHITE);
        receive.setSelected(true);
        buttonGroup2.add(receive);
        RxtxPanel.add(receive);
        receive.addItemListener(itemListener2);
        //发
        send = new JRadioButton("发");
        send.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        send.setBackground(Color.WHITE);
        buttonGroup2.add(send);
        RxtxPanel.add(send);
        send.addItemListener(itemListener2);

        //模式选择 0->单音信号  1->调制信号
        JPanel modePanel = new JPanel(new GridLayout(1,3));
        modePanel.setBackground(Color.WHITE);
        JLabel modelabel = new JLabel("模式：");
        modelabel.setBackground(Color.WHITE);
        modelabel.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        modePanel.add(modelabel);
        ButtonGroup buttonGroup3 = new ButtonGroup();
        //单音信号
        singleTon = new JRadioButton("单音");
        singleTon.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        singleTon.setBackground(Color.WHITE);
        singleTon.setSelected(true);
        buttonGroup3.add(singleTon);
        modePanel.add(singleTon);
        singleTon.addItemListener(itemListener3);
        //调制信号
        modulation = new JRadioButton("调制");
        modulation.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        modulation.setBackground(Color.WHITE);
        buttonGroup3.add(modulation);
        modePanel.add(modulation);
        modulation.addItemListener(itemListener3);
        //组合panel
        panel.add(RxtxPanel);
        panel.add(modePanel);
        //发送频率
        JPanel frequencyPanel = new JPanel(new GridLayout(1,2));
        frequencyPanel.setBackground(Color.WHITE);
        JLabel freqquencyLabel = new JLabel("发送频率：");
        freqquencyLabel.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        freqquencyLabel.setBackground(Color.WHITE);
        frequencyPanel.add(freqquencyLabel);
        frequency = new JComboBox<>();
        for (int i=0;i<FREQUENCY.length;i++) {
            frequency.addItem(FREQUENCY[i]);
        }
        frequency.addActionListener(new comboboxActionListener());
        frequencyPanel.add(frequency);
        panel.add(frequencyPanel);

        //接收频率
        JPanel receivePanel = new JPanel(new GridLayout(1,2));
        receivePanel.setBackground(Color.WHITE);
        JLabel receiveLabel = new JLabel("接收频率：");
        receiveLabel.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        receivePanel.setBackground(Color.WHITE);
        receivePanel.add(receiveLabel);
        receiveFrequency = new JComboBox<>();
        for (int i=0;i<FREQUENCY.length;i++) {
            receiveFrequency.addItem(FREQUENCY[i]);
        }
        receiveFrequency.addActionListener(new comboboxActionListener());
        receivePanel.add(receiveFrequency);
        panel.add(receivePanel);

        //接收增益
        JPanel magnitudePanel = new JPanel(new GridLayout(1,3));
        magnitudePanel.setBackground(Color.WHITE);
        JLabel magnitudeLabel = new JLabel("接收增益：");
        magnitudeLabel.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        magnitudeLabel.setBackground(Color.WHITE);
        magnitudePanel.add(magnitudeLabel);
        magnitude = new JTextField();
        magnitude.setText(""+0);
        magnitudePanel.add(magnitude);
        panel.add(magnitudePanel);

        //发送衰减
        JPanel decreasePanel = new JPanel(new GridLayout(1,3));
        decreasePanel.setBackground(Color.WHITE);
        JLabel decreaseLabel = new JLabel("发送衰减：");
        decreaseLabel.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        decreaseLabel.setBackground(Color.WHITE);
        decreasePanel.add(decreaseLabel);
        decrease = new JTextField();
        decrease.setText(""+0);
        decreasePanel.add(decrease);
        panel.add(decreasePanel);



        //确认button
        JPanel btnPanel2 = new JPanel(new GridLayout(1,3));
        btnPanel2.setBackground(Color.WHITE);
        JLabel label1 = new JLabel("");
        label1.setBackground(Color.WHITE);
        btnPanel2.add(label1);

        JButton btn = new JButton("确定");
        btn.setBackground(Color.WHITE);
        btnPanel2.add(btn);
        btn.addActionListener(new btnActionListener());

        JLabel label2 = new JLabel("");
        label2.setBackground(Color.WHITE);
        btnPanel2.add(label2);
        panel.add(btnPanel2);
        //状态栏
        Border titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
                BORDER_TITLE, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                FontEnum.BORDER_TITLE_FONT.getFont());
        panel.setBorder(titledBorder);
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(DEFAULT_PANEL_WIDTH, DEFAULT_PANEL_HEIGHT));
        return panel;

    }
    /**
     * Comboboox监听
     */
    public static class comboboxActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            for (int i=0;i<FREQUENCY.length;i++) {
                I:if (FREQUENCY[i].equals((String) frequency.getSelectedItem())){
                    f=i;
                    break I;
                }
                Q:if (FREQUENCY[i].equals((String) receiveFrequency.getSelectedItem())) {
                    fr=i;
                    break Q;
                }
            }
            //System.out.println((String) frequency.getSelectedItem());
        }
    }
    /**
     * btn监听
     */
    public static class btnActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                //非空判断
                if (magnitude.getText() != null) {
                    int m = Integer.parseInt(magnitude.getText());
                    int d = Integer.parseInt(decrease.getText());
                    //System.out.println("m="+m+"d="+d);
                    if ((f >= 0 && f <= 15) && (m >= 0 && m <= 63)&&(d>=0&&d<=255)) {
                        byte b1, b2,b3,b4;
                        b4 = (byte) (d & 0xff);
                        b3 = (byte) (m & 0xff);
                        PRE[6] = b4;
                        PRE[5] = b3;
                        b2 = (byte) ((fr*16+f) & 0xff);
                        PRE[4] = b2;
                        b1 = (byte) ((mode+rxtx*16) & 0xff);
                        PRE[3] = b1;
                        portUsed.write(PRE);
                    } else {
                        TimedDialog.getDialog("提示","接收增益指定范围为0~63或发送衰减指定范围为0~255",2,true,3000);
                    }
                }
            } catch (NumberFormatException ex) {
                TimedDialog.getDialog("错误","幅度未指定，请输入！！",0,true,3000);
            }
        }
    }
    /**
     *ButtonGroup事件监听
     */
    public static class MyItemListener implements ItemListener {
        /**
         * 监听事件
         * @param e
         */
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == e.SELECTED) {
                if (e.getSource()==receive) {
                    rxtx = 1;
                    //System.out.println("i am receive....");
                } else if (e.getSource()==send) {
                    rxtx = 0;
                    //System.out.println("i am send....");
                } else if (e.getSource()==singleTon) {
                    mode = 1;
                    //System.out.println("i am singleTon....");
                } else if (e.getSource()==modulation) {
                    mode = 0;
                    //System.out.println("i am modulation....");
                }
            }

        }
    }


    /**
     * 创建一个主frame
     *
     * @param
     */
    public void createFrame() {
        JFrame frame = new JFrame();
        frame.add(BERTest.createBERTestPanel(portUsed));
        frame.setBackground(Color.WHITE);
        frame.setVisible(true);
        frame.setSize(DEFAULT_PANEL_WIDTH,DEFAULT_PANEL_HEIGHT);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void main(String[] args){
        //CommunicationUploading test = new CommunicationUploading();
        //test.createFrame();
    }
}
