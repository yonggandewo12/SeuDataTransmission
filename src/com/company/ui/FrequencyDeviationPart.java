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

/**
 * 设置频偏的模块
 */
public class FrequencyDeviationPart {
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
    private static final String BORDER_TITLE = "频偏设置";
    private static JComboBox frequency;
    //串口
    private static DSerialPort port;
    //JPanel
    JPanel parent;
    //频偏
    private static final String[] FREQUENCY_DEVIATION = {"0k","10k","20k","30k","40k","50k","60k","70k","80k","90k","100k","110k","-10k","-20k","-30k","-40k","-50k","-60k","-70k","-80k","-90k","-100k","-110k"};
    //发送的前缀
    private static byte[] PRE = new byte[]{-21,-112,2,85,85,85,0};
    private static int f;
    public FrequencyDeviationPart(DSerialPort port,JPanel parent) {
        this.port=port;
        this.parent=parent;
        parent.add(createFrequencyDeviationPanel());
    }
    public static JPanel createFrequencyDeviationPanel() {
        /**
         * 相关参数初始化
         */
        f = 0;
        JPanel panel = new JPanel(new GridLayout(DEFAULT_GRID_ROWS,DEFAULT_GRID_COLUMN));
        //设置颜色
        panel.setBackground(Color.WHITE);

        //empty
        for(int i=0;i<2;i++){
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(Color.WHITE);
            panel.add(emptyPanel);
        }

        //下拉框设置频偏
        //发送频率
        JPanel frequencyDeviation = new JPanel(new GridLayout(1,3));
        frequencyDeviation.setBackground(Color.WHITE);
        JLabel freqquencyLabel = new JLabel("频偏：");
        freqquencyLabel.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        freqquencyLabel.setBackground(Color.WHITE);
        frequencyDeviation.add(freqquencyLabel);
        frequency = new JComboBox<>();
        for (int i=0;i<FREQUENCY_DEVIATION.length;i++) {
            frequency.addItem(FREQUENCY_DEVIATION[i]);
        }
        frequency.addActionListener(new comboboxActionListener());
        frequencyDeviation.add(frequency);
        //empty
        for(int i=0;i<1;i++){
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(Color.WHITE);
            frequencyDeviation.add(emptyPanel);
        }
        panel.add(frequencyDeviation);

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
     * Comboboox监听
     */
    public static class comboboxActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            for (int i=0;i<FREQUENCY_DEVIATION.length;i++) {
                if (FREQUENCY_DEVIATION[i].equals((String) frequency.getSelectedItem())){
                    if (i<=11) {
                        f = i;
                    }else{
                        f = 8 *16+(i-11);
                    }
                }
            }
        }
    }
    /**
     * btn监听
     */
    public static class btnActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            PRE[6] = (byte) (f & 0xff);
            port.write(PRE);
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
