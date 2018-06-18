package com.company.data;

import com.company.dsp.Complex;
import com.company.dsp.FFT;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Intellij Idea 2018.5
 * Company :SEU
 * Author  :yonggandewo12
 * GitHub  :https://github.com/yonggandewo12
 */
public class FFTCalculator implements Runnable{
    private LinkedBlockingQueue normal;
    private LinkedBlockingQueue complexquque;
    private static final double EPS = 1E-6;
    private static final double sampleRate=40;
    private static int FFT_LEN = 4096;
    public FFTCalculator(LinkedBlockingQueue normal,LinkedBlockingQueue complexquque) {
        this.normal = normal;
        this.complexquque = complexquque;
    }
    @Override
    public void run() {
        float[] I=new float[FFT_LEN];
        float[] Q=new float[FFT_LEN];
        Complex[] x = new Complex[FFT_LEN];
        Complex[] y = new Complex[FFT_LEN];
        while(true){
            for (int i = 0; i < FFT_LEN; i++) {
                I[i] = isTheRightData()[0];
                Q[i] = isTheRightData()[1];
                x[i] = new Complex(I[i],0);
                y[i] = new Complex(Q[i],0);
                //System.out.println(I[i]);
            }
            //System.out.println("outer");
            // 计算FFT，复数    用I路
            // 计算FFT，复数
            Complex[] fftRes_I = FFT.fft(x);
            // 计算纵坐标值，实数
            double[] yValue_I = getYAxisDataWithLog(fftRes_I);
            // 计算横轴坐标，实数
            double[] xValue_I = getXAxisDataWithMHzUnit(sampleRate, I.length);
            // 计算FFT，复数    用I路
            Complex[] fftRes_Q = FFT.fft(y);
            // 计算纵坐标值，实数
            double[] yValue_Q = getYAxisDataWithLog(fftRes_Q);
            // 计算横轴坐标，实数
            double[] xValue_Q = getXAxisDataWithMHzUnit(sampleRate, fftRes_Q.length);
            //数据装箱
            for (int i=0;i<fftRes_I.length;i++) {
                List d = new ArrayList(4);
                d.add(xValue_I[i]);
                d.add(yValue_I[i]);
                d.add(xValue_Q[i]);
                d.add(yValue_Q[i]);
                try {
                    complexquque.put(d);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private int[] isTheRightData() {
        int[] data = new int[2];
        try {
            byte frame_tail, frame_flag, data_flag;
            byte[] I = new byte[2];
            byte[] Q = new byte[2];

            if (normal!=null){
                List b = (List)normal.take();
                //帧头判断
                frame_tail = (byte)b.get(0);
                frame_flag = (byte)b.get(1);
                data_flag = (byte)b.get(2);
                I[0] = (byte)b.get(3);
                I[1] = (byte)b.get(4);
                Q[0] = (byte)b.get(5);
                Q[1] = (byte)b.get(6);
                //System.out.println("frame_tail="+frame_tail+",frame_flag="+frame_flag+",data_flag="+data_flag);
                if ((frame_tail == -21) && (frame_flag == -111) && (data_flag == 1)) {
                    //数据区解析
                    //I
                    //System.out.println("Enter");
                    data[0] = getIntData(I);
                    //System.out.println("a="+data[0]);
                    data[1] = getIntData(Q);
                    return data;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
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
     * 计算功率谱图像的纵轴值，20log10(abs(fft(data)))-maxValue;
     * @param fftRes
     * @return
     */
    private double[] getYAxisDataWithLog(Complex[] fftRes) {
        // step1 求模
        double[] absValue = getComplexValueLength(fftRes);
        /*for (int i=00;i<absValue.length;i++) {
            System.out.println(absValue[i]);
        }*/
        // step2 计算log
        double[] temp = new double[fftRes.length];
        for (int index = 0; index <= fftRes.length - 1; index++) {
            temp[index] = 20 * Math.log10(absValue[index]);
            if (temp[index] == Double.NEGATIVE_INFINITY) {
                // -100dB是一个很小的数字了
                temp[index] = -100;
            }
        }
        // step3 归一化
        double maxValue = getMaxValue(temp);
        for (int index = 0; index <= fftRes.length - 1; index++) {
            temp[index] = temp[index] - maxValue;
        }
        //return absValue;
        return temp;

    }
    /**
     * 计算复数数组中每个复数的模
     * @param array
     * @return
     */
    private double[] getComplexValueLength(Complex[] array) {
        int len = array.length;
        double[] res = new double[len];
        for (int index = 0; index <= len - 1; index++) {
            res[index] = array[index].abs();
        }
        return res;
    }
    /**
     * 获取数组中最大的数
     * @param array
     * @return
     */
    private double getMaxValue(double[] array) {
        double max = 0.0;
        for (int index = 0; index <= array.length - 1; index++) {
            if ((array[index] - max) >= EPS) {
                max = array[index];
            }
        }
        return max;
    }
    /**
     * 计算X轴的数据，单位 MHz
     * @param sampleRate
     * @param Nfft
     * @return
     */
    private double[] getXAxisDataWithMHzUnit(double sampleRate, int Nfft) {
        double[] res = new double[Nfft];
        // 横轴单位使用Hz
        double step = sampleRate / (Nfft);
        for (int index = 0; index < Nfft; index++) {
            res[index] = index * step;
        }
        return res;
    }

}
