package com.company.ui;

/**
 * Created by Intellij Idea 2018.5
 * Company :SEU
 * Author  :yonggandewo12
 * GitHub  :https://github.com/yonggandewo12
 */
import com.company.util.FontEnum;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * 在这里绘制中频信号的功率谱
 * Created by Administrator on 2018/1/27.
 */
public class PlotIntermediateFrequencyFFTPart implements Runnable{
    private static XYSeries xyseries_I = new XYSeries("FrequencyFFT_I");
    private static XYSeries xyseries_Q = new XYSeries("FrequencyFFT_Q");
    private static JFreeChart jfreechart = null;
    private LinkedBlockingQueue queue;
    private JPanel parent;
    private static int i=0;
    public PlotIntermediateFrequencyFFTPart(LinkedBlockingQueue queue,JPanel parent) {
        this.parent = parent;
        this.queue=queue;
        parent.add(PlotIntermediateFrequencyFFTPart.getJFreeChart());
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
        jfreechart.setTitle("中频信号功率谱图");
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        // Y轴
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setLowerBound(-80);
        numberaxis.setUpperBound(0);
        numberaxis.setTickUnit(new NumberTickUnit(10d));
        numberaxis.setLabel("Magnitude/db");
        numberaxis.setLabelFont(new Font("斜体", Font.BOLD, 15));
        // 只显示整数值
        //numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // numberaxis.setAutoRangeIncludesZero(true);
        numberaxis.setLowerMargin(0); // 数据轴下（左）边距 ­
        numberaxis.setMinorTickMarksVisible(false);// 标记线是否显示
        numberaxis.setTickMarkInsideLength(0);// 外刻度线向内长度
        numberaxis.setTickMarkOutsideLength(0);

        // X轴的设计
        NumberAxis x = (NumberAxis) xyplot.getDomainAxis();
        x.setAutoRange(false);// 自动设置数据轴数据范围
        // 自己设置横坐标的值
        x.setAutoTickUnitSelection(true);
        x.setTickUnit(new NumberTickUnit(10));
        // 设置最大的显示值和最小的显示值
        x.setLowerBound(10);
        x.setUpperBound(70);
        // 数据轴的数据标签：只显示整数标签
        //x.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        x.setAxisLineVisible(true);// X轴竖线是否显示
        x.setTickMarksVisible(false);// 标记线是否显示
        x.setLabel("Frequency/MHz");

        RectangleInsets offset = new RectangleInsets(0, 0, 0, 0);
        xyplot.setAxisOffset(offset);// 坐标轴到数据区的间距
        xyplot.setBackgroundAlpha(0.0f);// 去掉柱状图的背景色
        xyplot.setOutlinePaint(null);// 去掉边框
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
        xyseriescollection.addSeries(xyseries_I);
        xyseriescollection.addSeries(xyseries_Q);
        return xyseriescollection;
    }
    @Override
    public void run() {
        while (true) {
            o:while(true) {
                try {
                    List list = (List) queue.take();
                    double x1=0;
                    double x2 = 0;
                    if ((20.0-(double) list.get(0))>1e-6) {
                        x1=(double) list.get(0)+40.0;
                    }else{
                        x1=(double) list.get(0);
                    }
                    if ((20.0-(double) list.get(2))>1e-6) {
                        x2=(double) list.get(2)+40.0;
                    }else{
                        x2=(double) list.get(2);
                    }
                    //I路
                    xyseries_I.addOrUpdate(x1, (double) list.get(1));
                    //Q路
                    xyseries_Q.addOrUpdate(x2, (double) list.get(3));
                    i++;
                    Thread.sleep(0);
                    //test
                    //System.out.println("out_i=" + list.get(0) + "," + list.get(1) + ",out_q=" + list.get(2) + "," + list.get(3));
                    if (i == 4096) {
                        Thread.sleep(10000);
                        xyseries_Q.delete(0, 4095);
                        xyseries_I.delete(0, 4095);
                        i = 0;
                        continue;
                    }
                    break o;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
