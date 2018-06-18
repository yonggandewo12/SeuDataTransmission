package com.company.ui;

/**
 * Created by Intellij Idea 2018.5
 * Company :SEU
 * Author  :yonggandewo12
 * GitHub  :https://github.com/yonggandewo12
 */
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

/**
 * 上传数据控制页面
 */
public class CommunicationUploading{
    /**
     * 主panel网格布局参数
     */
    private static final int DEFAULT_GRID_ROWS = 7;
    private static final int DEFAULT_GRID_COLUMN = 1;
    /**
     * 主 panel 默认大小
     */
    private static final int DEFAULT_PANEL_WIDTH = 180;
    private static final int DEFAULT_PANEL_HEIGHT = 300;
    /**
     * 组件标题
     */
    private static final String BORDER_TITLE = "上传数据选择";
    private static MyItemListener itemListener = new MyItemListener();
    private static JRadioButton samplingData,Constellation,Hopping;

    //串口
    private static DSerialPort port;

    //命令
    private static String dataCommand;
    //JPanel
    JPanel parent;

    public CommunicationUploading(DSerialPort port,JPanel parent) {
        this.port=port;
        this.parent=parent;
        parent.add(CommunicationUploading.createCommunicationUploadingPanel());
    }
    public static JPanel createCommunicationUploadingPanel() {
        /**
         * 相关参数初始化
         */
        dataCommand ="采样数据";
        JPanel panel = new JPanel(new GridLayout(DEFAULT_GRID_ROWS,DEFAULT_GRID_COLUMN));
        //设置颜色
        panel.setBackground(Color.WHITE);
        ButtonGroup buttonGroup = new ButtonGroup();
        //采样点
        samplingData = new JRadioButton("采样点数据");
        samplingData.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        samplingData.setBackground(Color.WHITE);
        samplingData.setSelected(true);
        buttonGroup.add(samplingData);
        panel.add(samplingData);
        samplingData.addItemListener(itemListener);
        //星座图
        Constellation = new JRadioButton("星座图数据");
        Constellation.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        Constellation.setBackground(Color.WHITE);
        buttonGroup.add(Constellation);
        panel.add(Constellation);
        Constellation.addItemListener(itemListener);
        //跳频图
        Hopping = new JRadioButton("跳频数据");
        Hopping.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        Hopping.setBackground(Color.WHITE);
        buttonGroup.add(Hopping);
        panel.add(Hopping);
        Hopping.addItemListener(itemListener);

        //empty
        for(int i=0;i<3;i++){
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(Color.WHITE);
            panel.add(emptyPanel);
        }
        //确认button
        JPanel btnPanel = new JPanel(new GridLayout(1,3));
        btnPanel.setBackground(Color.WHITE);
        JLabel label1 = new JLabel("");
        label1.setBackground(Color.WHITE);
        btnPanel.add(label1);

        JButton btn = new JButton("确定");
        btn.setBackground(Color.WHITE);
        btnPanel.add(btn);
        btn.addActionListener(new btnActionListener());

        JLabel label2 = new JLabel("");
        label2.setBackground(Color.WHITE);
        btnPanel.add(label2);
        panel.add(btnPanel);
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
     * btn监听
     */
    public static class btnActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if (dataCommand!=null) {
                if (dataCommand.equals("采样数据")) {
                    port.write(new byte[]{-21,-112,6,85,85,85,1});
                    //System.out.println("EB900655555501");
                } else if (dataCommand.equals("星座数据")) {
                    port.write(new byte[]{-21,-112,6,85,85,85,2});
                    //System.out.println("EB900655555502");
                } else if (dataCommand.equals("跳频数据")) {
                    port.write(new byte[]{-21,-112,6,85,85,85,3});
                    //System.out.println("EB900655555503");
                }
            }
        }
    }
    /**
     *ButtonGroup事件监听
     */
    public static class MyItemListener implements ItemListener{
        /**
         * 监听事件
         * @param e
         */
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == e.SELECTED) {
                if (e.getSource()==samplingData) {
                    dataCommand = "采样数据";
                    /*portUsed.write("sampling。。。");
                    System.out.println("请上传采样点数据！！");*/
                } else if (e.getSource()==Constellation) {
                    dataCommand = "星座数据";
                    /*portUsed.write("constelling。。。");
                    System.out.println("请上传星座图数据！！");*/
                } else if (e.getSource()==Hopping) {
                    dataCommand = "跳频数据";
                    /*portUsed.write("decoding。。。");
                    System.out.println("请上传解调数据！！");*/
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
        //frame.add(CommunicationUploading.createCommunicationUploadingPanel(portUsed));
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
