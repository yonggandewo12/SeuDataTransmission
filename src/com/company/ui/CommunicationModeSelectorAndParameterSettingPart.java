package com.company.ui;

/**
 * Created by Intellij Idea 2018.5
 * Company :SEU
 * Author  :yonggandewo12
 * GitHub  :https://github.com/yonggandewo12
 */
import com.company.rs232.DSerialPort;
import com.company.util.CommunicationModeEnum;
import com.company.util.ExtendStringToSameLength;
import com.company.util.FontEnum;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;


/**
 * 这里绘制工作模式部分
 * Created by Administrator on 2018/1/26.
 * @author WYCPhoenix
 * @date 2018-1-27:41
 *
 */
public class CommunicationModeSelectorAndParameterSettingPart {
    private static String selectedMode;//模式定义
    private static DSerialPort portUsed;
    /**
     * 主panel网格布局参数
     */
    private static final int DEFAULT_GRID_ROWS = 6;
    private static final int DEFAULT_GRID_COLUMN = 1;
    /**
     * 主 panel 默认大小
     */
    private static final int DEFAULT_PANEL_WIDTH = 180;
    private static final int DEFAULT_PANEL_HEIGHT = 300;

    public static JPanel createCommunicationModeSelectorAndParameterSettingPanel(DSerialPort port, LinkedBlockingQueue queue_sampling_data,LinkedBlockingQueue queue_constellation_data,LinkedBlockingQueue queue_test,LinkedBlockingQueue queue_err,LinkedBlockingQueue queue_hop, LinkedBlockingQueue queue_constellation,String propertyPath, UIParameterCollector collector) {
        portUsed = port;
        // 模式选择 + 参数设置的panel
        JPanel combinedPanel = new JPanel();
        combinedPanel.setBackground(Color.WHITE);
        combinedPanel.setLayout(new FlowLayout());

        /**
         * 通信模式选择UI部分
         */
        //1.1 模式选择的panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(DEFAULT_GRID_ROWS, DEFAULT_GRID_COLUMN));
        panel.setBackground(Color.WHITE);
        // 单选按钮
        ButtonGroup buttonGroup = new ButtonGroup();
        // 为了便于布局，将所有字符串变为相同长度
        List<String> supportedModeName =ExtendStringToSameLength.extendString(getSupportedMode());
        for (int i = 0; i < supportedModeName.size(); i++) {
            // 默认选中第一个
            if (i == 0) {
                JRadioButton radioButton = new JRadioButton(supportedModeName.get(i), true);
                radioButton.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
                radioButton.setBackground(Color.WHITE);
                radioButton.setActionCommand(supportedModeName.get(i));
                selectedMode = radioButton.getActionCommand().trim();
                collector.setMode(selectedMode);
                // 专门用于参数传输
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //为按钮添加事件  收集选择的模式信息
                        selectedMode = radioButton.getActionCommand().trim();
                        collector.setMode(selectedMode);
                        //System.out.println("模式选择为："+selectedMode);
                    }
                });
                // 实现参数设置面板更新
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        selectedMode = radioButton.getActionCommand().trim();
                        collector.setMode(selectedMode);
                        combinedPanel.remove(1);
                        JPanel panelToUpdate = CommunicationParameterSettingPart.createCommunicationParameterSettingPanel(port,
                                selectedMode, propertyPath, collector);
                        combinedPanel.add(panelToUpdate);
                        combinedPanel.paintAll(combinedPanel.getGraphics());

                    }
                });
                buttonGroup.add(radioButton);
                panel.add(radioButton);
            } else {
                JRadioButton radioButton = new JRadioButton(supportedModeName.get(i), false);
                radioButton.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
                radioButton.setBackground(Color.WHITE);
                radioButton.setActionCommand(supportedModeName.get(i));
                // 专门用于参数传输
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        selectedMode = radioButton.getActionCommand().trim();
                        collector.setMode(selectedMode);
                        //System.out.println("模式选择为："+selectedMode);
                    }
                });
                // 用于参数设置panel的更新
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        selectedMode = radioButton.getActionCommand().trim();
                        collector.setMode(selectedMode);
                        combinedPanel.remove(1);
                        JPanel panelToUpdate = CommunicationParameterSettingPart.createCommunicationParameterSettingPanel(port,
                                selectedMode, propertyPath, collector);
                        combinedPanel.add(panelToUpdate);
                        combinedPanel.paintAll(combinedPanel.getGraphics());
                    }
                });
                buttonGroup.add(radioButton);
                panel.add(radioButton);
            }
        }
        //1.2 标题栏
        Border titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
                "模式选择", TitledBorder.DEFAULT_POSITION, TitledBorder.DEFAULT_JUSTIFICATION,
                FontEnum.BORDER_TITLE_FONT.getFont());
        panel.setBorder(titledBorder);
        panel.setPreferredSize(new Dimension(DEFAULT_PANEL_WIDTH, DEFAULT_PANEL_HEIGHT));

        /**
         * 总的panel
         */
        JPanel ControlPanel = new JPanel();
        ControlPanel.setBackground(Color.WHITE);
        /**
         * 参数设置的panel
          */
        JPanel parameterSettingPanel = CommunicationParameterSettingPart.createCommunicationParameterSettingPanel(port,selectedMode, propertyPath, collector);
        combinedPanel.add(panel);
        combinedPanel.add(parameterSettingPanel);
        ControlPanel.add(combinedPanel);
        new CommunicationUploading(portUsed,ControlPanel);
        ControlPanel.add(BERTest.createBERTestPanel(port));
        new RST(port,ControlPanel,queue_hop,queue_test,queue_err,queue_constellation);
        DataSavingPart dataSavingPart = new DataSavingPart(portUsed,queue_test,queue_err,queue_sampling_data,queue_constellation_data,ControlPanel);
        new Thread(dataSavingPart).start();
        FrequencyDeviationPart frequencyDeviationPart = new FrequencyDeviationPart(portUsed,ControlPanel);
        /*int num=0;
        while((num++)<10000){
            try {
                StringBuffer sb = new StringBuffer("");
                List list = (List)queue_err.take();
                for (int i=0;i<list.size();i++) {
                    sb.append(list.get(i)+" ");
                }
                System.out.println("out = "+sb.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        return ControlPanel;
    }
    /**
     * 从CommunicationModeEnum中获取所有支持的模式
     * 模式顺序为：模式名的自然顺序
     * @return
     */
    private static List<String> getSupportedMode() {
        Set<CommunicationModeEnum> communicationModeEnums = EnumSet.allOf(CommunicationModeEnum.class);
        // 按字符自然顺序排序
        List<String> supportedModeName = new ArrayList<>();
        Iterator<CommunicationModeEnum> iterator = communicationModeEnums.iterator();
        while (iterator.hasNext()) {
            CommunicationModeEnum now = iterator.next();
            supportedModeName.add(now.getCommunicationMode());
        }
        Collections.sort(supportedModeName);
        return supportedModeName;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        BlockingQueue<Integer> queue = new SynchronousQueue<>();
        //获得串口
        //port =InitCommPort.InitCommPort("COM1",queue);
        JFrame frame = new JFrame();

        UIParameterCollector collector = new UIParameterCollector();
        //frame.add(createCommunicationModeSelectorAndParameterSettingPanel(port,"LANComm.properties", collector));
        //frame.add(CommunicationUploading.createCommunicationUploadingPanel());
        frame.setVisible(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
