package com.company.rs232;

/**
 * Created by Intellij Idea 2018.5
 * Company :SEU
 * Author  :yonggandewo12
 * GitHub  :https://github.com/yonggandewo12
 */
import gnu.io.CommPortIdentifier;

import java.util.Enumeration;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 串口初始化入口
 * Edit by XuLiang
 */
public class InitCommPort {
    /**
     * 串口号初始化
     */
    private static DSerialPort d;
    public static DSerialPort InitCommPort(LinkedBlockingQueue queue) {
        d = new DSerialPort(queue);
        //DSerialPort d2 = new DSerialPort();
        //d1.listPort();
        d.selectPort(findSerialAvaiable());
        new Thread(d).start();
        //d1.close();
        //d2.selectPort("COM2");
        return d;
    }
    public static String findSerialAvaiable() {
        Enumeration<?> en = CommPortIdentifier.getPortIdentifiers();
        CommPortIdentifier portId;
        while (en.hasMoreElements()) {
            portId = (CommPortIdentifier) en.nextElement();
            // 如果端口类型是串口，则打印出其端口信息
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                return portId.getName();
                //System.out.println(portId.getName());
            }
        }
        return null;
    }

    /**
     * 关闭串口
     */
    public void close() {
        d.close();
    }

}
