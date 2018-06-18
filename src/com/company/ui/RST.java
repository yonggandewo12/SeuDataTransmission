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
import java.util.concurrent.LinkedBlockingQueue;

public class RST {
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
    private static final String BORDER_TITLE = "复位";
    private static Checkbox RxRst, TxRst;
    private volatile static boolean sendFlag = false;
    private volatile static boolean receiveFlag = false;
    private static final String RECEIVE_INIT = new String("ReceiveInit");
    private static final String Send_INIT = new String("SendInit");
    //串口
    private JPanel parent;
    private static DSerialPort portUsed;
    private static LinkedBlockingQueue queue_hop,queue_test,queue_err,queue_constellation;

    /**
     * Constructor
     *
     * @param
     * @return
     */
    public RST(DSerialPort port, JPanel parent,LinkedBlockingQueue queue_hop,LinkedBlockingQueue queue_test,LinkedBlockingQueue queue_err,LinkedBlockingQueue queue_constellation) {
        portUsed = port;
        this.queue_err = queue_err;
        this.queue_hop = queue_hop;
        this.queue_test = queue_test;
        this.queue_constellation = queue_constellation;
        this.parent = parent;
        parent.add(createRSTPanel());
    }

    public static JPanel createRSTPanel() {
        /**
         * 相关参数初始化
         */
        receiveFlag = false;
        sendFlag = false;
        JPanel panel = new JPanel(new GridLayout(DEFAULT_GRID_ROWS, DEFAULT_GRID_COLUMN));
        //设置颜色
        panel.setBackground(Color.WHITE);
        //动态生成CheckBox
        //接收
        RxRst = RST.createCheckbox("");
        RxRst.setLabel(RECEIVE_INIT);
        panel.add(RxRst);
        //发送
        TxRst = RST.createCheckbox("");
        TxRst.setLabel(Send_INIT);
        panel.add(TxRst);

        //empty
        for (int i = 0; i < 4; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(Color.WHITE);
            panel.add(emptyPanel);
        }
        //确认button
        JPanel btnPanel = new JPanel(new GridLayout(1, 3));
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
     * 动态生成CheckBox，并注册监听事件
     *
     * @param label
     * @return
     */
    private static Checkbox createCheckbox(String label) {
        Checkbox cb = new Checkbox(label);
        cb.setFont(FontEnum.CHECKBOX_TEXT_FONT.getFont());
        cb.setBackground(Color.WHITE);
        //给Checkbox对象注册事件监听
        cb.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Checkbox cb = (Checkbox) e.getSource();
                if (cb.getLabel().equals(RECEIVE_INIT)) {
                    receiveFlag = cb.getState();
                }
                if (cb.getLabel().equals(Send_INIT)) {
                    sendFlag = cb.getState();
                }
                //System.out.println("Checkbox "+cb.getLabel()+"的选择状态："+cb.getState());
            }
        });
        return cb;
    }

    /**
     * btn监听
     */
    public static class btnActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (sendFlag && receiveFlag) {
                queue_err.clear();
                queue_hop.clear();
                queue_test.clear();
                queue_constellation.clear();
                //System.out.println("size = "+queue_err.size());
                portUsed.write(new byte[]{-21, -112, 0, 85, 85, 1, 1});
                //System.out.println("EB900055550101");
            }
            if (sendFlag && (!receiveFlag)) {
                queue_err.clear();
                queue_hop.clear();
                queue_test.clear();
                queue_constellation.clear();
                //System.out.println("size = "+queue_err.size());
                portUsed.write(new byte[]{-21, -112, 0, 85, 85, 1, 0});
                //System.out.println("EB900055550100");
            }
            if ((!sendFlag) && receiveFlag) {
                queue_err.clear();
                queue_hop.clear();
                queue_test.clear();
                queue_constellation.clear();
                //System.out.println("size = "+queue_err.size());
                portUsed.write(new byte[]{-21, -112, 0, 85, 85, 0, 1});
                //System.out.println("EB900055550001");
            }
            if ((!sendFlag) && (!receiveFlag)) {
                queue_err.clear();
                queue_hop.clear();
                queue_test.clear();
                queue_constellation.clear();
                //System.out.println("size = "+queue_err.size());
                portUsed.write(new byte[]{-21, -112, 0, 85, 85, 0, 0});
                //System.out.println("EB900055550000");
            }

        }
    }
}

