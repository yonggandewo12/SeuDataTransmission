package com.company.ui;

/**
 * Created by Intellij Idea 2018.5
 * Company :SEU
 * Author  :yonggandewo12
 * GitHub  :https://github.com/yonggandewo12
 */
import com.company.rs232.DSerialPort;
import com.company.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;
/**
 * 参数设置部分，采用网格布局
 * 这里设计 5 * 2 的表格，实际使用前 6 个，其余用于后序扩展
 * Created by Administrator on 2018/1/26.
 */
public class CommunicationParameterSettingPart {
    /**
     * 主panel网格布局参数
     */
    private static final int DEFAULT_GRID_ROWS = 7;
    private static final int DEFAULT_GRID_COLUMN = 1;
    /**
     * 组件标题
     */
    private static final String BORDER_TITLE = "参数设置";
    /**
     * 主 panel 默认大小
     */
    private static final int DEFAULT_PANEL_WIDTH = 180;
    private static final int DEFAULT_PANEL_HEIGHT = 300;

    private static final String DEFAULT_MODE = "DQPSK";
    private static  String unit ;
    private static float value;
    private static volatile String UNIT="kbps";
    private static volatile float VALUE=100;
    private static String mode_select;
    private static DSerialPort portUsed;

    /**
     * 创建一个动态的参数配置表，具体内容跟随 mode 改变
     * 为了满足参数间的约束关系，这里设定参数配置过程是
     * 先选择单位，后选择数值
     * 或者 先选数值，后选单位
     * @param mode 工作模式，即DPQSK / DQPSK-DSSS等
     * @return
     */
    public static JPanel createCommunicationParameterSettingPanel(DSerialPort port, String mode, String propertyPath, UIParameterCollector collector) {
        // 存放参数选择的panel，网格布局
        portUsed = port;
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(DEFAULT_GRID_ROWS, DEFAULT_GRID_COLUMN));

        // TODO: 2018/1/28  增加一个复选框，实现参数设置模式的切换
        if (mode == null) {
            mode = DEFAULT_MODE;
        }
        setParameterByFistUnitThenValue(panel, propertyPath, mode, collector);
        //状态栏
        Border titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
                                            BORDER_TITLE, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                                            FontEnum.BORDER_TITLE_FONT.getFont());
        panel.setBorder(titledBorder);
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(DEFAULT_PANEL_WIDTH, DEFAULT_PANEL_HEIGHT));
        mode_select = mode;
        //创建一个Button
        for(int i=0;i<5;i++){
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(Color.WHITE);
            panel.add(emptyPanel);
        }
        JPanel btnPanel1 = new JPanel(new GridLayout(1,3));
        btnPanel1.setBackground(Color.WHITE);
        JButton btn1 = new JButton("确定");
        btnPanel1.add(new JLabel(""));
        btnPanel1.add(btn1);
        btnPanel1.add(new JLabel(""));
        panel.add(btnPanel1);

        btn1.addActionListener(new ConfirmActionListener(unit,value,mode_select));

        //System.out.println("mode_select ="+mode_select);
        /*panel.add(CommunicationUploading.createCommunicationUploadingPanel(portUsed));
        panel.add(BERTest.createBERTestPanel(portUsed));
        panel.add(RST.createRSTPanel(portUsed));*/
        return panel;
    }
    //给button按钮确认监听事件
    public static class ConfirmActionListener implements ActionListener{
        private static  String unit ;
        private static float value;
        private static String mode_select;
        public ConfirmActionListener(String unit,float value,String mode_select) {
            this.unit = unit;
            this.value = value;
            this.mode_select = mode_select;

        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("确定")) {
                //System.out.println("value = "+VALUE+",Unit="+UNIT+",mode="+mode_select);
                //System.out.println("您点击了按钮。。。");
                //portUsed.write("徐良");
                /*if (Math.exp(value-200.0)<0.001) {
                    System.out.println("value =100");
                }*/
                if (mode_select.equals("DQPSK")&&((int)VALUE==100)&&(UNIT.equals("kbps"))) {
                    //DQPSK   100kbps
                    portUsed.write(new byte[]{-21,-112,1,85,85,0,0});
                    //System.out.println("EB900155550000");
                } else if (mode_select.equals("DQPSK")&&((int)VALUE==200)&&(UNIT.equals("kbps"))) {
                    portUsed.write(new byte[]{-21,-112,1,85,85,1,1});
                    //System.out.println("EB900155550101");
                }else if (mode_select.equals("DQPSK")&&((int)VALUE==500)&&(UNIT.equals("kbps"))) {
                    portUsed.write(new byte[]{-21,-112,1,85,85,2,2});
                    //System.out.println("EB900155550202");
                }else if (mode_select.equals("DQPSK")&&((int)VALUE==1)&&(UNIT.equals("Mbps"))) {
                    portUsed.write(new byte[]{-21,-112,1,85,85,3,3});
                    //System.out.println("EB900155550303");
                }else if (mode_select.equals("DQPSK")&&((int)VALUE==2)&&(UNIT.equals("Mbps"))) {
                    portUsed.write(new byte[]{-21,-112,1,85,85,4,4});
                    //System.out.println("EB900155550404");
                }else if (mode_select.equals("DQPSK")&&((int)VALUE==10)&&(UNIT.equals("Mbps"))) {
                    portUsed.write(new byte[]{-21,-112,1,85,85,6,6});
                    //System.out.println("EB900155550606");
                }else if (mode_select.equals("DQPSK")&&((int)VALUE==5)&&(UNIT.equals("Mbps"))) {
                    portUsed.write(new byte[]{-21,-112,1,85,85,5,5});
                    //System.out.println("EB900155550505");
                } else if (mode_select.equals("DQPSK-DSSS")&&((int)VALUE==10)&&(UNIT.equals("kbps"))) {
                    portUsed.write(new byte[]{-21,-112,1,85,85,-128,-128});
                    //System.out.println("EB900155558080");
                }else if (mode_select.equals("DQPSK-DSSS")&&((int)VALUE==16)&&(UNIT.equals("kbps"))) {
                    portUsed.write(new byte[]{-21,-112,1,85,85,-127,-127});
                    //System.out.println("EB900155558181");
                }else if (mode_select.equals("DQPSK-DSSS")&&((int)VALUE==32)&&(UNIT.equals("kbps"))) {
                    portUsed.write(new byte[]{-21,-112,1,85,85,-126,-126});
                    //System.out.println("EB900155558282");
                }else if (mode_select.equals("DQPSK-DSSS")&&((int)VALUE==64)&&(UNIT.equals("kbps"))) {
                    portUsed.write(new byte[]{-21,-112,1,85,85,-125,-125});
                    //System.out.println("EB900155558383");
                }else if (mode_select.equals("DQPSK-DSSS")&&((int)VALUE==128)&&(UNIT.equals("kbps"))) {
                    portUsed.write(new byte[]{-21,-112,1,85,85,-124,-124});
                    //System.out.println("EB900155558484");
                }else if (mode_select.equals("DQPSK-DSSS")&&((int)VALUE==256)&&(UNIT.equals("kbps"))) {
                    portUsed.write(new byte[]{-21,-112,1,85,85,-123,-123});
                    //System.out.println("EB900155558585");
                }else if (mode_select.equals("FH-MSK")&&((int)VALUE==100)&&(UNIT.equals("kbps"))) {
                    //跳频控制信息
                    portUsed.write(new byte[]{-21,-112,1,85,85,-16,-16});
                    //System.out.println("EB90015555F0F0");
                }
            }
        }
    }

    /**
     * 获取配置参数，按自然顺序排序
     * @return
     */
    private static List<String> getParameterNames() {
        EnumSet<CommunicationParameterEnum> enumSet = EnumSet.allOf(CommunicationParameterEnum.class);
        Iterator<CommunicationParameterEnum> iterator = enumSet.iterator();
        List<String> parameterNames = new ArrayList<>();
        while (iterator.hasNext()) {
            CommunicationParameterEnum parameter = iterator.next();
            parameterNames.add(parameter.getCommunicationParameter());
        }
        Collections.sort(parameterNames);
        return parameterNames;
    }

    /**
     *
     * 这里的 CheckBox 用于值-单位的选择模式
     * 选中该 CheckBox 则先选值，后选单位
     * 否则，先选单位，后选值
     * 无效代码。。
     * @return
     */
    @Deprecated
    private static JCheckBox createSelectorModeCheckBox(JPanel parent) {
        JCheckBox checkBox = new JCheckBox();
        // 增加几个空的JLable，将 CheckBox 位置靠右侧
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new GridLayout(1,3));
        //checkBoxPanel.add(dumpLabel);
        //checkBoxPanel.add(dumpLabel2);
        checkBoxPanel.add(checkBox);
        parent.add(checkBoxPanel);
        return checkBox;
    }

    /**
     * 参数设置模式：先选单位，后选数值
     * @param parent 参数列表所在的panel
     * @param propertyPath 配置文件位置
     * @param mode 通信模式, 即DQPSK/DQPSK-DSSS等
     */
    private static void setParameterByFistUnitThenValue(JPanel parent, String propertyPath, String mode, UIParameterCollector collector) {
        // 获取mode对应的参数
        Map<String, String> map = ReadModeConfiguration.getCommunicationModeConfigureation(propertyPath, mode);
        // 按照参数的单位进行分组
        Map<String, Map<String, List<Float>>> parameterGroup = ParameterGroup.groupByUnit(map);
        // 参数的名字，类似所有名字设置为相同长度
        List<String> parameterNames = ExtendStringToSameLength.extendString(getParameterNames());
        for (String parameterName : parameterNames) {
            // 参数名对应的配置项的key
            String key = mode + "-" + parameterName.trim();
            //System.out.println(key);
            //值和单位
            Map<String, List<Float>> kvPairsMap = parameterGroup.get(key);
            if (kvPairsMap != null) {
                // 容纳标签和两个ComboBox的的Panel
                JPanel innerPanel = new JPanel();
                innerPanel.setBackground(Color.WHITE);
                innerPanel.setLayout(new GridLayout(1,3));
                // 参数名对应的label
                JLabel label = new JLabel(parameterName);
                //System.out.println(parameterName);
                label.setFont(FontEnum.LABEL_FONT.getFont());
                // 单位对应的Combo box
                JComboBox comboBoxUnit = new JComboBox();
                // 获取所有的单位
                Set<String> unitSet = kvPairsMap.keySet();
                for (String unit : unitSet) {
                    comboBoxUnit.addItem(unit);//test
                    //System.out.println(unit);
                    comboBoxUnit.setBackground(Color.WHITE);
                }
                // 专门用于参数传递
                comboBoxUnit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        unit = (String) comboBoxUnit.getItemAt(comboBoxUnit.getSelectedIndex());
                        /**
                         * 单位选择
                         */

                        //System.out.println(unit);
                        if (unit!=null) {
                            //非空判断
                            UNIT = unit;
                        }
                        String unitSetMethodName = UIParameterCollector.getMethodNameMappingUnitSetter().get(parameterName.trim());
                        // 这里就先反射调用吧~~~
                        try {
                            Class clazz = UIParameterCollector.class;
                            Method method = clazz.getMethod(unitSetMethodName, String.class);
                            method.invoke(collector, unit);
                        } catch (NoSuchMethodException e1) {
                            System.out.println(e1);
                        } catch (InvocationTargetException e1) {
                            System.out.println(e1);
                        } catch (IllegalAccessException e1) {
                            System.out.println(e1);
                        } finally {
                            // TODO: 2018/1/29 finally 处理 
                        }
                    }
                });
                // 默认选择第一个
                comboBoxUnit.setEditable(false);
                comboBoxUnit.setSelectedIndex(0);
                // 单位对应的值的combo box
                List<Float> values = kvPairsMap.get(unit);

                JComboBox comboBoxValue = new JComboBox();
                for (Float value : values) {
                    comboBoxValue.addItem(value);
                    //System.out.println(value);
                    comboBoxValue.setBackground(Color.WHITE);
                }
                // 专门用于参数传递
                // 默认选择第一个值
                comboBoxUnit.setEditable(false);
                comboBoxUnit.setSelectedIndex(0);
                comboBoxValue.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Float value = (Float) comboBoxValue.getItemAt(comboBoxValue.getSelectedIndex());
                        /**
                         * 对应的值
                         */
                        //System.out.println("value = "+value);
                        if (value!=null) {
                            //非空判断
                            VALUE = value;
                        }
                        String valueSetMethodName = UIParameterCollector.getMethodNameMappingValueSetter().get(parameterName.trim());
                        try {
                            Class clazz = UIParameterCollector.class;
                            Method method = clazz.getMethod(valueSetMethodName, Float.class);
                            method.invoke(collector, value);
                        } catch (NoSuchMethodException e1) {
                            TimedDialog.getDialog("错误",e1.getMessage(), JOptionPane.ERROR_MESSAGE, false,0);
                        } catch (InvocationTargetException e1) {
                            TimedDialog.getDialog("错误",e1.getMessage(), JOptionPane.ERROR_MESSAGE, false,0);
                        } catch (IllegalAccessException e1) {
                            TimedDialog.getDialog("错误",e1.getMessage(), JOptionPane.ERROR_MESSAGE, false,0);
                        } finally {
                            // TODO: 2018/1/29 finally处理
                        }
                    }
                });
                // 为 comboBoxUnit 增加一个新的事件，用于动态更新value部分
                comboBoxUnit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // step 1 获取本次被选中的元素——单位
                        unit = (String) comboBoxUnit.getItemAt(comboBoxUnit.getSelectedIndex());
                        // step 2 获取单位对应的value
                        List<Float> valueToUpdate = kvPairsMap.get(unit);
                        // step 3 清除combo box的内容
                        comboBoxValue.removeAllItems();
                        // step 4 填写新的Item
                        for (int i = 0; i < valueToUpdate.size(); i++) {
                            comboBoxValue.addItem(valueToUpdate.get(i));
                        }
                        // step 5 重新绘制，实现动态更新
                        parent.paint(parent.getGraphics());
                    }
                });
                comboBoxValue.setEditable(false);
                comboBoxValue.setSelectedIndex(0);
                innerPanel.add(label);
                innerPanel.add(comboBoxValue);
                innerPanel.add(comboBoxUnit);
                parent.add(innerPanel);
            }
        }
    }

    /**
     * 参数设置模式：先选数值，后选单位
     * 注意：该部分代码没有写参数传递功能，禁用！！
     * @param parent 参数配置表所在的panel
     * @param propertyPath 配置文件位置
     * @param mode 工作模式 即DQPSK/DQPSK-DSSS等
     */
    @Deprecated
    private static void setParameterByFirstValueThenValue(JPanel parent, String propertyPath, String mode) {
        // 获取mode对应的参数
        Map<String, String> map = ReadModeConfiguration.getCommunicationModeConfigureation(propertyPath, mode);
        // 按照参数的单位进行分组
        Map<String, Map<Float, List<String>>> parameterGroup = ParameterGroup.groupByNumber(map);
        // 参数的名字，类似所有名字设置为相同长度
        List<String> parameterNames = ExtendStringToSameLength.extendString(getParameterNames());
        for (String parameterName : parameterNames) {
            String key = mode + "-" + parameterName.trim();
            //System.out.println("num = "+parameterNames.size());
            Map<Float, List<String>> kvPairsMap = parameterGroup.get(key);
            if (kvPairsMap != null) {
                JPanel innerPanel = new JPanel();
                innerPanel.setLayout(new GridLayout(1, 3));
                JLabel label = new JLabel(parameterName);
                label.setFont(FontEnum.LABEL_FONT.getFont());
                // 值对应的Combo box
                JComboBox comboBoxValue = new JComboBox();
                // 获取所有的值
                Set<Float> valueSet = kvPairsMap.keySet();
                for (Float value : valueSet) {
                    comboBoxValue.addItem(value);
                }
                // 这里linstener专门用于处理选择事件
                comboBoxValue.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // TODO: 2018/1/28 增加值选择参数传递
                        value = (Float) comboBoxValue.getItemAt(comboBoxValue.getSelectedIndex());
                        //System.out.println(value);
                    }
                });
                // 默认选择第一个
                comboBoxValue.setEditable(false);
                comboBoxValue.setSelectedIndex(0);
                // 值对应的所有单位
                List<String> units = kvPairsMap.get(value);
                JComboBox comboBoxUnit = new JComboBox();
                for (String unitItem : units) {
                    comboBoxUnit.addItem(unitItem);
                }
                // 这个actionlistener专门处理选择事件
                comboBoxUnit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // TODO: 2018/1/28 增加单位选择参数传递
                        //System.out.println(comboBoxUnit.getItemAt(comboBoxUnit.getSelectedIndex()));
                    }
                });
                // 为 comboxValue 添加新的事件，用于动态更新
                comboBoxValue.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println(e.getActionCommand());
                        value = (Float) comboBoxValue.getItemAt(comboBoxValue.getSelectedIndex());
                        List<String> unitToUpdate = kvPairsMap.get(value);
                        comboBoxUnit.removeAllItems();
                        for (int i = 0; i < unitToUpdate.size(); i++) {
                            comboBoxUnit.addItem(unitToUpdate.get(i));
                        }
                        parent.paint(parent.getGraphics());
                    }
                });
                comboBoxUnit.setEditable(false);
                comboBoxUnit.setSelectedIndex(0);
                innerPanel.add(label);
                innerPanel.add(comboBoxValue);
                innerPanel.add(comboBoxUnit);
                parent.add(innerPanel);
            }
        }

    }

    public static String getUnit() {
        return unit;
    }

    public static Float getValue() {
        return value;
    }

    public static void main(String[] args) {
        UIParameterCollector collector = new UIParameterCollector();
        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.add(createCommunicationParameterSettingPanel("DQPSK-FH", "LANComm.properties", collector));
        frame.pack();
    }

}
