package com.company.ui;

/**
 * Created by Intellij Idea 2018.5
 * Company :SEU
 * Author  :yonggandewo12
 * GitHub  :https://github.com/yonggandewo12
 */
import com.company.rs232.DSerialPort;
import com.company.util.BitErrorRatePart;
import com.company.util.FontEnum;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 保存数据功能
 */
public class DataSavingPart implements Runnable{
    /**
     * 主panel网格布局参数
     */
    private static final int DEFAULT_GRID_ROWS = 7;
    private static final int DEFAULT_GRID_COLUMN = 1;
    /**
     * 主 panel 默认大小
     */
    private static final int DEFAULT_PANEL_WIDTH = 200;
    private static final int DEFAULT_PANEL_HEIGHT = 300;
    /**
     * 组件标题
     */
    private static final String BORDER_TITLE = "数据保存/测试";
    private static LinkedBlockingQueue sampling,constellation,queue_test,queue_err;
    //JPanel
    private JPanel parent;
    private static DSerialPort portUsed;
    //保存数据标志
    private static boolean isRunning = false;

    //文件路径常数    系统路径
    private static String PATH = System.getProperty("user.dir").replace('\\', '/');
    //输出流
    private static FileOutputStream out_sampling,out_constellation;
    //时间
    private static DateFormat formate = new SimpleDateFormat("yy_MM_dd_HH_mm_ss");
    //误码率显示框
    private static JTextField BER,err;
    //文件
    private static File file_sampling;
    private static File file_constellation;
    public DataSavingPart(DSerialPort port,LinkedBlockingQueue queue_test,LinkedBlockingQueue queue_err,LinkedBlockingQueue sampling, LinkedBlockingQueue constellation, JPanel parent) {
        portUsed = port;
        this.parent=parent;
        this.sampling = sampling;
        this.queue_test = queue_test;
        this.queue_err = queue_err;
        this.constellation = constellation;
        parent.add(DataSavingPart.createDataSavingPanel());
        BitErrorRatePart bitErrorRatePart = new BitErrorRatePart(queue_test,queue_err,BER,err);
        new Thread(bitErrorRatePart).start();
    }
    public static JPanel createDataSavingPanel(){
        /**
         * 相关参数初始化
         */
        JPanel panel = new JPanel(new GridLayout(DEFAULT_GRID_ROWS,DEFAULT_GRID_COLUMN));
        //设置颜色
        panel.setBackground(Color.WHITE);

        //empty
        for(int i=0;i<1;i++){
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(Color.WHITE);
            panel.add(emptyPanel);
        }
        JPanel btnAll = new JPanel(new GridLayout(1,4));
        btnAll.setBackground(Color.WHITE);

        JLabel label1 = new JLabel("");
        label1.setBackground(Color.WHITE);
        btnAll.add(label1);
        //确认button
        JPanel btnPanel = new JPanel(new GridLayout(1,3));
        btnPanel.setBackground(Color.WHITE);
        /*JLabel label1 = new JLabel("");
        label1.setBackground(Color.WHITE);
        btnPanel.add(label1);*/

        JButton btn = new JButton("存");
        btn.setBackground(Color.WHITE);
        btnPanel.add(btn);
        btn.addActionListener(new btnActionListener());


        btnAll.add(btnPanel);
        panel.add(btnAll);

        //取消button
        JPanel deletePanel = new JPanel(new GridLayout(1,3));
        deletePanel.setBackground(Color.WHITE);

        JButton delete = new JButton("停");
        delete.setBackground(Color.WHITE);
        deletePanel.add(delete);
        delete.addActionListener(new deleteActionListener());
        btnAll.add(deletePanel);

        JLabel label2 = new JLabel("");
        label2.setBackground(Color.WHITE);
        btnAll.add(label2);

        panel.add(btnAll);


        //empty
        for(int i=0;i<2;i++){
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(Color.WHITE);
            panel.add(emptyPanel);
        }

        //BERTest
        //显示框
        JPanel showPanel = new JPanel(new GridLayout(1,2));
        showPanel.setBackground(Color.WHITE);

        /*JLabel label3 = new JLabel("");
        label3.setBackground(Color.WHITE);
        showPanel.add(label3);*/

        JLabel lb = new JLabel("收bit");
        lb.setBackground(Color.WHITE);
        lb.setFont(FontEnum.LABEL_FONT.getFont());
        showPanel.add(lb);

        BER = new JTextField();
        BER.setBackground(Color.WHITE);
        BER.setText(""+0);
        showPanel.add(BER);

        /*JLabel label4 = new JLabel("");
        label4.setBackground(Color.WHITE);
        showPanel.add(label4);*/

        panel.add(showPanel);

        //错误比特
        JPanel errPanel = new JPanel(new GridLayout(1,2));
        errPanel.setBackground(Color.WHITE);

        /*JLabel label5 = new JLabel("");
        label5.setBackground(Color.WHITE);
        errPanel.add(label5);*/

        JLabel lbr = new JLabel("错bit");
        lbr.setBackground(Color.WHITE);
        lbr.setFont(FontEnum.LABEL_FONT.getFont());
        errPanel.add(lbr);

        err = new JTextField();
        err.setBackground(Color.WHITE);
        err.setText(""+0);
        errPanel.add(err);

        /*JLabel label6 = new JLabel("");
        label6.setBackground(Color.WHITE);
        errPanel.add(label6);*/

        panel.add(errPanel);

        //测试按钮
        JPanel btnTestPanel = new JPanel(new GridLayout(1,4));
        btnTestPanel.setBackground(Color.WHITE);

        JLabel label7 = new JLabel("");
        label7.setBackground(Color.WHITE);
        btnTestPanel.add(label7);

        //testBtn
        JButton testBtnOpen = new JButton("开");
        testBtnOpen.setBackground(Color.WHITE);
        testBtnOpen.setFont(FontEnum.BUTTON_FONT.getFont());
        testBtnOpen.addActionListener(new btnOpenListener());
        btnTestPanel.add(testBtnOpen);

        JButton testBtnClosed = new JButton("关");
        testBtnClosed.setBackground(Color.WHITE);
        testBtnClosed.setFont(FontEnum.BUTTON_FONT.getFont());
        testBtnClosed.addActionListener(new btnClosedListener());
        btnTestPanel.add(testBtnClosed);

        JLabel label8 = new JLabel("");
        label8.setBackground(Color.WHITE);
        btnTestPanel.add(label8);

        panel.add(btnTestPanel);


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
     * 测试开button监听
     */
    public static class btnOpenListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            portUsed.write(new byte[]{-21,-112,7,85,85,85,1});
            queue_err.clear();
            queue_test.clear();
            //System.out.println("EB900855555501");
        }
    }
    /**
     * 测试关button监听
     */
    public static class btnClosedListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            portUsed.write(new byte[]{-21,-112,7,85,85,85,0});
            err.setText(""+0);
            queue_err.clear();
            queue_test.clear();
            //System.out.println("EB900855555500");
        }
    }
    /**
     * 开始保存文件button监听
     */
    public static class btnActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            file_sampling = new File(PATH + "/datas/" +formate.format(new Date())+"采样点.txt");
            file_constellation = new File(PATH + "/datas/" + formate.format(new Date())+"星座图.txt");
            try {
                //创建
                file_constellation.createNewFile();
                file_sampling.createNewFile();
                //绑定输出流
                out_sampling = new FileOutputStream(file_sampling, true);
                out_constellation = new FileOutputStream(file_constellation, true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            isRunning = true;
            TimedDialog.getDialog("提示","开始保存",1,true,3000);
        }
    }

    /**
     * 停止button监听
     */
    public static class deleteActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            isRunning = false;
            try {
                out_constellation.close();
                out_sampling.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            TimedDialog.getDialog("提示","保存成功",1,true,3000);
        }
    }
    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("");
                M:while (isRunning) {
                    I:if ((sampling != null) && (sampling.size() != 0)) {
                        int[] message = isTheSamplingData();
                        //System.out.println("sampling = " + message[0] + "," + message[1]);
                        out_sampling.write((" " + message[0]).getBytes());
                        out_sampling.write((" " + message[1]).getBytes());
                        break I;
                    }
                    Q:if ((constellation != null) && (constellation.size() != 0)) {
                        int[] message = isTheConstellationData();
                        //System.out.println("constellation = " + message[0] + "," + message[1]);
                        out_constellation.write((" " + message[0]).getBytes());
                        out_constellation.write((" " + message[1]).getBytes());
                        break Q;
                    }
                    break M;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        } catch (NullPointerException e){
        }
    }
    /**
     * 返回int类型数据
     * @param b
     * @return
     */
    private int getIntData(byte[] b){
        byte high,low;
        high=b[0];
        low=b[1];
        return (short)(((high & 0x00FF) << 8) | (0x00FF & low));
    }
    /**
     * 得到数据帧中的数据
     * @param
     * @return
     */
    private int[] isTheConstellationData() {
        int[] data=new int[2];
        byte[] I = new byte[2];
        byte[] Q = new byte[2];
        try {
            StringBuffer sb = new StringBuffer("");
            List b = (List) constellation.take();
            for (int i = 0; i < b.size(); i++) {
                sb.append(b.get(i) + " ");
            }
            I[0] = (byte)b.get(3);
            I[1] = (byte)b.get(4);
            Q[0] = (byte)b.get(5);
            Q[1] = (byte)b.get(6);
            data[0] = getIntData(I);
            data[1] = getIntData(Q);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }
    /**
     * 得到数据帧中的数据
     * @param
     * @return
     */
    private int[] isTheSamplingData() {
        int[] data=new int[2];
        byte[] I = new byte[2];
        byte[] Q = new byte[2];
        try {
            StringBuffer sb = new StringBuffer("");
            List b = (List) sampling.take();
            for (int i = 0; i < b.size(); i++) {
                sb.append(b.get(i) + " ");
            }
            I[0] = (byte)b.get(3);
            I[1] = (byte)b.get(4);
            Q[0] = (byte)b.get(5);
            Q[1] = (byte)b.get(6);
            data[0] = getIntData(I);
            data[1] = getIntData(Q);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }
}
