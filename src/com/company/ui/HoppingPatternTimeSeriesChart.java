package com.company.ui;

/**
 * Created by Intellij Idea 2018.5
 * Company :SEU
 * Author  :yonggandewo12
 * GitHub  :https://github.com/yonggandewo12
 */
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 绘制跳频图案
 * Created by Administrator on 2018/3/16.
 * @author WYCPhoenix
 */
public class HoppingPatternTimeSeriesChart implements Runnable{
    private static XYSeries xyseries = new XYSeries("跳频图");
    private static JFreeChart jfreechart = null;
    public boolean isRunning=true;
    private LinkedBlockingQueue queue;
    private JPanel parent;
    private static int message;
    public HoppingPatternTimeSeriesChart(LinkedBlockingQueue queue,JPanel parent) {
        this.parent = parent;
        this.queue=queue;
        parent.add(HoppingPatternTimeSeriesChart.getJFreeChart());
    }
    public static JPanel getJFreeChart(){
        jfreechart = ChartFactory.createXYLineChart(
                null, null, null, createDataset(),
                PlotOrientation.VERTICAL, false, true, false);
        StandardChartTheme mChartTheme = new StandardChartTheme("CN");
        mChartTheme.setLargeFont(new Font("黑体", Font.BOLD, 20));
        mChartTheme.setExtraLargeFont(new Font("宋体", Font.PLAIN, 15));
        mChartTheme.setRegularFont(new Font("宋体", Font.PLAIN, 15));
        ChartFactory.setChartTheme(mChartTheme);
        jfreechart.setBorderPaint(new Color(0,204,205));
        jfreechart.setBorderVisible(false);
        jfreechart.setTitle("跳频图");
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        // Y轴
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setLowerBound(0);
        numberaxis.setUpperBound(10);
        numberaxis.setTickUnit(new NumberTickUnit(2d));
        numberaxis.setLabel("Frequency/MHz");
        numberaxis.setLabelFont(new Font("斜体", Font.BOLD, 15));
        // 只显示整数值
        //numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        numberaxis.setAutoRangeIncludesZero(true);
        numberaxis.setLowerMargin(0); // 数据轴下（左）边距 ­
        numberaxis.setMinorTickMarksVisible(false);// 标记线是否显示
        numberaxis.setTickMarkInsideLength(0);// 外刻度线向内长度
        numberaxis.setTickMarkOutsideLength(0);

        // X轴的设计
        NumberAxis x = (NumberAxis) xyplot.getDomainAxis();
        x.setAutoRange(true);// 自动设置数据轴数据范围
        // 自己设置横坐标的值
        x.setAutoTickUnitSelection(false);
        x.setTickUnit(new NumberTickUnit(1d));
        // 设置最大的显示值和最小的显示值
        x.setLowerBound(0);
        x.setUpperBound(10);
        // 数据轴的数据标签：只显示整数标签
        //x.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        x.setAxisLineVisible(true);// X轴竖线是否显示
        x.setTickMarksVisible(false);// 标记线是否显示
        x.setLabel("time");

        RectangleInsets offset = new RectangleInsets(0, 0, 0, 0);
        xyplot.setAxisOffset(offset);// 坐标轴到数据区的间距
        xyplot.setBackgroundAlpha(0.0f);// 去掉柱状图的背景色
        xyplot.setOutlinePaint(null);// 去掉边框
        //画点图
        XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer();
        xylineandshaperenderer.setSeriesLinesVisible(0, false);
        xylineandshaperenderer.setSeriesShapesVisible(1, false);
        xylineandshaperenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        xyplot.setRenderer(xylineandshaperenderer);
        // ChartPanel chartPanel = new ChartPanel(jfreechart);
        // chartPanel.restoreAutoDomainBounds();//重置X轴
        ChartPanel chartPanel = new ChartPanel(jfreechart, true);
        return chartPanel;
    }

    /**
     * 该方法是数据的设计
     *
     * @return
     */
    public static XYDataset createDataset() {
        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }

    /**
     * 数据解析
     * @return
     */
    private int isTheRightData() {
        byte frame_tail,frame_flag,data_flag;
        byte[] Q=new byte[2];
        int data=0;
        try {
            List b = (List) queue.take();
            if ((b != null) && (b.size() == 7)) {
                //帧头判断
                frame_tail = (byte)b.get(0);
                frame_flag = (byte)b.get(1);
                data_flag = (byte)b.get(2);
                Q[0] = (byte)b.get(5);
                Q[1] = (byte)b.get(6);
                if ((frame_tail == -21) && (frame_flag == -111) && (data_flag == 3)) {
                    isRunning = true;
                } else {
                    isRunning = false;
                }
                //数据判断
                if (Q[0]==1&&(Q[1]==38)) {
                    data = 6;
                } else if (Q[0]==1&&(Q[1]==-116)) {
                    data = 8;
                } else if (Q[0]==0&&(Q[1]==-13)) {
                    data = 5;
                } else if (Q[0]==0&&(Q[1]==-64)) {
                    data = 4;
                } else if (Q[0]==1&&(Q[1]==89)) {
                    data = 7;
                } else if (Q[0]==1&&(Q[1]==-64)) {
                    data = 9;
                } else if (Q[0] == 1 && (Q[1] == -13)) {
                    data = 10;
                } else {
                    data = 0;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }
    @Override
    public void run() {
        int i = 0;
        while (true) {
            try {
                message = isTheRightData();
                //System.out.println("flag = " + isRunning);
                while (isRunning) {
                    xyseries.addOrUpdate(i, message);
                    //System.out.println("out="+(message[1]));
                    i++;
                    Thread.sleep(50);
                    if (i == 10) {
                        xyseries.delete(0, 9);
                        i = 0;
                        continue;
                    }
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
            }
        }
    }
   /* public static void main(String[] args) {
        PlotIntermediateFrequencyPart jz = new PlotIntermediateFrequencyPart();
        JFrame frame = new JFrame();
        frame.setSize(700, 500);
        frame.getContentPane().add(jz.getCPUJFreeChart(), BorderLayout.CENTER);

        frame.setVisible(true);
        frame.setLocationRelativeTo(null); // 窗口居于屏幕正中央
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        new Thread(jz).start();
    }*/
}
