package com.company.ui;

/**
 * Created by Intellij Idea 2018.5
 * Company :SEU
 * Author  :yonggandewo12
 * GitHub  :https://github.com/yonggandewo12
 */
import com.company.rs232.DSerialPort;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.LinkedBlockingQueue;
public class PlotPanel {
    /**
     * 设置最优尺寸
     */
    private static final int DEFAULT_WIDTH = 1000;
    private static final int DEFAULT_HEIGHT = 500;
    //rs232
    private static DSerialPort portUsed;
    /**
     *
     * @param port
     * @return
     */

    public static JPanel createPlotPanel(DSerialPort port,LinkedBlockingQueue queue_sampling,LinkedBlockingQueue queue_constellation,LinkedBlockingQueue complexquque,LinkedBlockingQueue queue_hop) {
        // 绘图面板
        portUsed=port;
        JPanel plotPanel = new JPanel(new GridLayout(1,4));
        plotPanel.setBackground(Color.WHITE);
        plotPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        PlotIntermediateFrequencyPart plotIntermediateFrequencyPart = new PlotIntermediateFrequencyPart(queue_sampling,plotPanel);
        new Thread(plotIntermediateFrequencyPart).start();
        PlotIntermediateFrequencyFFTPart plotIntermediateFrequencyFFTPart = new PlotIntermediateFrequencyFFTPart(complexquque,plotPanel);
        if (queue_sampling!=null) {
            new Thread(plotIntermediateFrequencyFFTPart).start();
        }
        ConstellationDiagramChart constellationDiagramChart = new ConstellationDiagramChart(queue_constellation,plotPanel);
        if (queue_constellation!=null) {
            new Thread(constellationDiagramChart).start();
        }
        HoppingPatternTimeSeriesChart hoppingPatternTimeSeriesChart = new HoppingPatternTimeSeriesChart(queue_hop,plotPanel);
        if (queue_hop!=null) {
            new Thread(hoppingPatternTimeSeriesChart).start();
        }
        return plotPanel;
    }
}
