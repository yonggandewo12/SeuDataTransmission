package com.company.rs232;
/**
 * Created by Intellij Idea 2018.5
 * Company :SEU
 * Author  :yonggandewo12
 * GitHub  :https://github.com/yonggandewo12
 */
import com.company.ui.TimedDialog;
import gnu.io.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * @项目名称 :illegalsms
 * @文件名称 :SerialPort.java
 * @所在包 :org.serial
 * @功能描述 : 串口类
 * @修改记录 :
 */
public class DSerialPort implements Runnable{

    private String appName = "串口通讯测试";
    private int timeout = 2000;// open 端口时的等待时间
    private int threadTime = 0;
    // 输入输出流
    private InputStream inputStream;
    private OutputStream outputStream;
    private LinkedBlockingQueue queue;
    private CommPortIdentifier commPort;
    private SerialPort serialPort;
    private String portName;
    private static String message;
    private static byte[] shift = new byte[7];
    private static int i=0;
    /**
     *
     */
    public DSerialPort() {

    }
    /**
     * 构造函数，用于同步数据
      * @param queue
     */
    public DSerialPort(LinkedBlockingQueue queue){
        this.queue = queue;
    }
    /**
     * @方法名称 :listPort
     * @功能描述 :列出所有可用的串口
     * @返回值类型 :void
     */
    @SuppressWarnings("rawtypes")
    public void listPort() {
        CommPortIdentifier cpid;
        Enumeration en = CommPortIdentifier.getPortIdentifiers();

        System.out.println("now to list all Port of this PC：" + en);

        while (en.hasMoreElements()) {
            cpid = (CommPortIdentifier) en.nextElement();
            if (cpid.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                System.out.println(cpid.getName() + ", " + cpid.getCurrentOwner());
            }
        }
    }

    /**
     * @param portName
     * @方法名称 :selectPort
     * @功能描述 :选择一个端口，比如：COM1
     * @返回值类型 :void
     */
    @SuppressWarnings("rawtypes")
    public void selectPort(String portName) {

        this.portName = portName;
        this.commPort = null;
        CommPortIdentifier cpid;
        Enumeration en = CommPortIdentifier.getPortIdentifiers();

        while (en.hasMoreElements()) {
            cpid = (CommPortIdentifier) en.nextElement();
            //System.out.println("......" + cpid);
            if (cpid.getPortType() == CommPortIdentifier.PORT_SERIAL && cpid.getName().equals(portName)) {
                this.commPort = cpid;
                //System.out.println(cpid);
                break;
            }
        }

        openPort();
    }

    /**
     * @方法名称 :openPort
     * @功能描述 :打开SerialPort
     * @返回值类型 :void
     */
    private void openPort() {
        try {
            if (commPort == null) {
                System.out.println("......" + "error!!" + commPort.getName());
                TimedDialog.getDialog("错误", "无法找到对应串口！！", 0, true, 3000);
                log(String.format("无法找到名字为'%1$s'的串口！", commPort.getName()));
            } else {
                log("端口选择成功，当前端口：" + commPort.getName() + ",现在实例化 SerialPort:");
                try {
                    serialPort = (SerialPort) commPort.open(appName, timeout);
                    //设置串口监听
                    //serialPort.addEventListener(this);
                    // 设置串口数据时间有效(可监听)
                    serialPort.notifyOnDataAvailable(true);
                    // 设置串口通讯参数
                    // 波特率，数据位，停止位和校验方式
                    //波特率：115200，数据位8，停止位1，无校验
                    serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8,//
                            SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    log("实例 SerialPort 成功！");
                } catch (Exception e) {
                    TimedDialog.getDialog("错误", "串口正在使用，请重试！！", 0, true, 3000);
                    throw new RuntimeException(String.format("端口'%1$s'正在使用中！", commPort.getName()));
                }
            }
        } catch (NullPointerException e) {
            TimedDialog.getDialog("错误", "无法找到对应串口！！", 0, true, 300000);
        }

    }
    /**
     * @方法名称 :checkPort
     * @功能描述 :检查端口是否正确连接
     * @返回值类型 :void
     */
    private void checkPort() {
        if (commPort == null)
            throw new RuntimeException("没有选择端口，请使用 " + "selectPort(String portName) 方法选择端口");

        if (serialPort == null) {
            throw new RuntimeException("SerialPort 对象无效！");
        }
    }

    /**
     * @param message
     * @方法名称 :write
     * @功能描述 :向端口发送数据，请在调用此方法前 先选择端口，并确定SerialPort正常打开！
     * @返回值类型 :void
     */
    public void write(byte[] message) {
        checkPort();

        try {
            outputStream = new BufferedOutputStream(serialPort.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("获取端口的OutputStream出错：" + e.getMessage());
        }

        try {
            //outputStream.write(message.getBytes("GBK"), 0, message.getBytes("GBK").length);
            outputStream.write(message, 0, message.length);
            //outputStream.write(message.getBytes());
            //log("信息发送成功！" + new String(message));
        } catch (IOException e) {
            throw new RuntimeException("向端口发送信息时出错：" + e.getMessage());
        } finally {
            try {
                outputStream.close();
            } catch (Exception e) {
            }
        }
    }
    // 读取串口返回信息
    public void readComm(){
        byte[] readBuffer = new byte[1];
        StringBuffer sb = new StringBuffer("");
        try {
            inputStream = serialPort.getInputStream();
            //inputStream = serialPort.getInputStream();
            // 从线路上读取数据流
            int len = 0;
            while ((len = inputStream.read(readBuffer)) != -1) {
                //组帧     已接收到对应数据
                shift[0] = shift[1];
                shift[1] = shift[2];
                shift[2] = shift[3];
                shift[3] = shift[4];
                shift[4] = shift[5];
                shift[5] = shift[6];
                shift[6] = readBuffer[0];
                if ((shift[0] == -21) && (shift[1] == -111)) {
                    List list = new ArrayList(7);
                    for (int i = 0; i < shift.length; i++) {
                        list.add(shift[i]);
                        sb.append(shift[i] + " ");
                    }
                    //System.out.println(sb.toString());
                    //System.out.println("int=" + getIntData(new byte[]{shift[3], shift[4]}));
                    try {
                        queue.put(list);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        //return message;
    }
    /**
     * @方法名称 :close
     * @功能描述 :关闭 SerialPort
     * @返回值类型 :void
     */
    public void close() {
        serialPort.close();
        serialPort = null;
        commPort = null;
        try {
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(this+"端口关闭成功！！");
    }

    public void log(String msg) {
        System.out.println(appName + " --> " + msg);
    }

    /**
     * 数据接收的监听处理函数
     */
    /*@Override
    public void serialEvent(SerialPortEvent arg0) {
        switch (arg0.getEventType()) {
            case SerialPortEvent.BI:*//* Break interrupt,通讯中断 *//*
            case SerialPortEvent.OE:*//* Overrun error，溢位错误 *//*
            case SerialPortEvent.FE:*//* Framing error，传帧错误 *//*
            case SerialPortEvent.PE:*//* Parity error，校验错误 *//*
            case SerialPortEvent.CD:*//* Carrier detect，载波检测 *//*
            case SerialPortEvent.CTS:*//* Clear to send，清除发送 *//*
            case SerialPortEvent.DSR:*//* Data set ready，数据设备就绪 *//*
            case SerialPortEvent.RI:*//* Ring indicator，响铃指示 *//*
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:*//*
             * Output buffer is
             * empty，输出缓冲区清空
             *//*
                break;
            case SerialPortEvent.DATA_AVAILABLE:*//*
             * Data available at the serial
             * port，端口有可用数据。读到缓冲数组，输出到终端
             *//*
                readComm();
                break;
            default:
                break;
        }
    }*/

    @Override
    public void run() {
        while (true) {
            readComm();
        }
    }

    @Override
    public String toString() {
        return this.portName;
    }
}