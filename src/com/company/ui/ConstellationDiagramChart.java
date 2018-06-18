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
 * 星座图绘图
 * Created by Administrator on 2018/3/16.
 * @author
 */
public class ConstellationDiagramChart implements Runnable {
    private static XYSeries xyseries = new XYSeries("星座图");
    private static JFreeChart jfreechart = null;
    public boolean isRunning=true;
    private LinkedBlockingQueue queue;
    private JPanel parent;
    private static int[] message;
    public ConstellationDiagramChart(LinkedBlockingQueue queue,JPanel parent) {
        this.parent = parent;
        this.queue=queue;
        parent.add(ConstellationDiagramChart.getJFreeChart());
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
        jfreechart.setTitle("星座图");
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        // Y轴
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setLowerBound(-2048);
        numberaxis.setUpperBound(2048);
        numberaxis.setTickUnit(new NumberTickUnit(800d));
        numberaxis.setLabel("Q");
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
        x.setAutoRange(false);// 自动设置数据轴数据范围
        // 自己设置横坐标的值
        x.setAutoTickUnitSelection(false);
        x.setTickUnit(new NumberTickUnit(800d));
        // 设置最大的显示值和最小的显示值
        x.setLowerBound(-2048);
        x.setUpperBound(2048);
        // 数据轴的数据标签：只显示整数标签
        //x.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        x.setAxisLineVisible(true);// X轴竖线是否显示
        x.setTickMarksVisible(false);// 标记线是否显示
        x.setLabel("I");

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
    private int[] isTheRightData() {
        byte frame_tail,frame_flag,data_flag;
        byte[] I=new byte[2];
        byte[] Q=new byte[2];
        int[] data=new int[2];
        try {
            List b = (List) queue.take();
            if ((b != null) && (b.size() == 7)) {
                //帧头判断
                frame_tail = (byte)b.get(0);
                frame_flag = (byte)b.get(1);
                data_flag = (byte)b.get(2);
                I[0] = (byte)b.get(3);
                I[1] = (byte)b.get(4);
                Q[0] = (byte)b.get(5);
                Q[1] = (byte)b.get(6);
                if ((frame_tail == -21) && (frame_flag == -111) && (data_flag == 2)) {
                    data[0] = getIntData(I);
                    data[1] = getIntData(Q);
                    isRunning = true;
                    return data;
                } else {
                    isRunning = false;
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
                //更新的数据
                message = isTheRightData();
                while (isRunning) {
                    xyseries.addOrUpdate(message[0], message[1]);
                    //System.out.println("out="+message[1]+","+message[0]);
                    i++;
                    Thread.sleep(0);
                    if (i == 4096) {
                        xyseries.delete(0, 4095);
                        i = 0;
                        continue;
                    }
                    //System.out.println("i="+i);
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
