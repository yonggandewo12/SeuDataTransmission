package com.company.ui;

/**
 * Created by Intellij Idea 2018.5
 * Company :SEU
 * Author  :yonggandewo12
 * GitHub  :https://github.com/yonggandewo12
 */
/**
 * 主函数入口
 */

import com.company.data.DataDispacher;
import com.company.data.FFTCalculator;
import com.company.rs232.DSerialPort;
import com.company.rs232.InitCommPort;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * 主显示面板
 */
public class MainFrame {
    private static DSerialPort port;
    private static LinkedBlockingQueue<List> queue_all,queue_hop,queue_sampling,queue_samplingFFT,queue_constellation,queue_sampling_data,queue_constellation_data,queue_test,queue_err;
    private static LinkedBlockingQueue<List> complexquque;
    private static File folder;
    //时间
    private static DateFormat formate = new SimpleDateFormat("yy_MM_dd_HH_mm_ss");
    //文件路径常数    系统路径
    private static String PATH = System.getProperty("user.dir").replace('\\', '/');
    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        queue_all = new LinkedBlockingQueue();
        queue_sampling = new LinkedBlockingQueue();
        queue_samplingFFT = new LinkedBlockingQueue();
        queue_constellation = new LinkedBlockingQueue();
        complexquque = new LinkedBlockingQueue();
        queue_hop = new LinkedBlockingQueue();
        queue_sampling_data = new LinkedBlockingQueue();
        queue_constellation_data = new LinkedBlockingQueue();
        queue_test = new LinkedBlockingQueue();
        queue_err = new LinkedBlockingQueue();
        //创建文件夹
        folder = new File(PATH+"/datas");
        if (!folder.exists()) {
            folder.mkdir();
        }
        //获得串口
        port =InitCommPort.InitCommPort(queue_all);
        DataDispacher dataDispacher = new DataDispacher(queue_all,queue_sampling,queue_samplingFFT,queue_constellation,queue_hop,queue_sampling_data,queue_constellation_data,queue_test,queue_err);
        new Thread(dataDispacher).start();
        FFTCalculator fft = new FFTCalculator(queue_samplingFFT,complexquque);
        new Thread(fft).start();
        JFrame frame = new JFrame();
        UIParameterCollector collector = new UIParameterCollector();
        JPanel all = new JPanel(new GridLayout(2,1));
        all.setBackground(Color.WHITE);
        all.add(CommunicationModeSelectorAndParameterSettingPart.createCommunicationModeSelectorAndParameterSettingPanel(port,queue_sampling_data,queue_constellation_data,queue_test,queue_err,queue_hop,queue_constellation,"LANComm.properties", collector));
        all.add(PlotPanel.createPlotPanel(port,queue_sampling,queue_constellation,complexquque,queue_hop));
        frame.add(all);
        //frame.setBounds(0,0,1000,800);
        frame.setVisible(true);
        frame.pack();
        frame.setTitle("数据采集器");
        // ICON
        String path = System.getProperty("user.dir").replace('\\', '/') + "/config/";
        Image mainFrameIcon = new ImageIcon(path + "MainFrame.png").getImage();
        frame.setIconImage(mainFrameIcon);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
