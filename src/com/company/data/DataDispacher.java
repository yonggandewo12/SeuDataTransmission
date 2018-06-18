package com.company.data;
/**
 * Created by Intellij Idea 2018.5
 * Company :SEU
 * Author  :yonggandewo12
 * GitHub  :https://github.com/yonggandewo12
 */
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * 数据分发器：
 * 完成将串口读入的数据再次派发功能
 */
public class DataDispacher implements Runnable{
    private LinkedBlockingQueue queue_hop,queue_all,queue_sampling,queue_samplingFFT,queue_constellation,queue_sampling_data,queue_constellation_data,queue_test,queue_err;
    private static List b;
    public DataDispacher(LinkedBlockingQueue queue_all,LinkedBlockingQueue queue_sampling,LinkedBlockingQueue queue_samplingFFT,LinkedBlockingQueue queue_constellation,LinkedBlockingQueue queue_hop,LinkedBlockingQueue queue_sampling_data,LinkedBlockingQueue queue_constellation_data,LinkedBlockingQueue queue_test,LinkedBlockingQueue queue_err) {
        this.queue_all=queue_all;
        this.queue_constellation=queue_constellation;
        this.queue_sampling=queue_sampling;
        this.queue_samplingFFT=queue_samplingFFT;
        this.queue_hop = queue_hop;
        this.queue_constellation_data = queue_constellation_data;
        this.queue_sampling_data = queue_sampling_data;
        this.queue_test = queue_test;
        this.queue_err = queue_err;
    }
    @Override
    public void run() {
        byte frame_tail1,frame_tail2,data_control;
        while (true) {
            try {
                if (queue_all != null) {
                    b = (List)queue_all.take();
                    frame_tail1 = (byte)b.get(0);
                    frame_tail2 = (byte)b.get(1);
                    data_control = (byte)b.get(2);
                    if ((frame_tail1 == -21) && (frame_tail2 == -111)) {
                        if (data_control == 1) {
                            queue_sampling.put(b);
                            queue_samplingFFT.put(b);
                            queue_sampling_data.put(b);
                        } else if (data_control == 2) {
                            queue_constellation.put(b);
                            queue_constellation_data.put(b);
                        }else if (data_control == 3) {
                            //跳频
                            queue_hop.put(b);
                        }else if (data_control == 4) {
                            //接收比特
                            queue_test.put(b);
                        }else if (data_control == 5) {
                            //错误比特
                            queue_err.put(b);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
