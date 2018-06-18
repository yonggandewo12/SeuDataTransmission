package com.company.util;

/**
 * Created by Intellij Idea 2018.5
 * Company :SEU
 * Author  :yonggandewo12
 * GitHub  :https://github.com/yonggandewo12
 */

import javax.swing.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 误码率显示模块
 */
public class BitErrorRatePart implements Runnable{
    private LinkedBlockingQueue queue_test,queue_err;
    private JTextField BER,err;
    public BitErrorRatePart(LinkedBlockingQueue queue_test,LinkedBlockingQueue queue_err, JTextField BER,JTextField err) {
        this.queue_test= queue_test;
        this.queue_err = queue_err;
        this.BER = BER;
        this.err =err;
    }
    @Override
    public void run() {
        while (true) {
            try {
                X:
                if (queue_test != null) {
                    BER.setText("" + DoubleToString(getBERData()));
                    for (int i = 0; i < 50; i++) {
                        queue_test.take();
                    }
                    break X;
                }
                Y:
                if (queue_err != null) {
                    err.setText("" + DoubleToString(getERRData()));
                    for (int i = 0; i < 50; i++) {
                        queue_err.take();
                    }
                    break Y;
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 将科学计数法表示的double类型数据转化为String
     * @param dou
     * @return
     */
    private String DoubleToString(double dou){
        Double dou_obj = new Double(dou);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        String dou_str = nf.format(dou_obj);
        return dou_str;
    }
    /**
     * 获得解析后的接收bit正确数据
     * @return
     */
    private float getBERData() {
        try {
            if ((queue_test != null)&&(queue_test.size()!=0)) {
                List list = (List)queue_test.take();
                byte[] b= new byte[4];
                b[0] = (byte)list.get(3);
                b[1] = (byte)list.get(4);
                b[2] = (byte)list.get(5);
                b[3] = (byte)list.get(6);
                float data = get4IntData(b);
                if (data<0) {
                    data = (float)Math.pow(2,32)+data;
                }
                return data;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 获得解析后的错误bit正确数据
     * @return
     */
    private float getERRData() {
        ///System.out.println("enter");
        try {
            if ((queue_err != null)&&(queue_err.size()!=0)) {
                List list = (List)queue_err.take();
                byte[] b= new byte[4];
                b[0] = (byte)list.get(3);
                b[1] = (byte)list.get(4);
                b[2] = (byte)list.get(5);
                b[3] = (byte)list.get(6);
                float data = get4IntData(b);
                if (data<0) {
                    data = (float)Math.pow(2,32)+data;
                }
                return data;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 返回int类型数据  40位
     * @param b
     * @return
     */
    private float get4IntData(byte[] b){
        return (float)(((b[0] & 0x00FF) << 24) | ((b[1] &0x00FF) << 16)
                | ((b[2] & 0x00FF) << 8)
                | ((b[3] & 0x00FF)));
    }
}
