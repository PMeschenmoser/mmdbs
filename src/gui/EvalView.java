package gui;

import eval.Evaluator;
import vis.FMeasurePlot;
import vis.PRPlot;

import javax.swing.*;

/**
 * Created by Phil on 08.07.2017.
 */
public class EvalView {
    private JFrame frame;
    private JPanel panel1;
    private JTabbedPane tabbedeval;
    private PRPlot prplot;
    private FMeasurePlot fplot;


    public EvalView() {
        frame = new JFrame("Evaluation");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setVisible(false);
        frame.setSize(500, 500);

        prplot = new PRPlot();
        tabbedeval.add("Precision/Recall" , prplot.getPanel());

        fplot = new FMeasurePlot();
        tabbedeval.add("F-Measure" , fplot.getPanel());
    }

    public void show(){
        frame.setVisible(true);
        frame.toFront();
        frame.repaint();
    }
    public void setEvaluator(Evaluator evaluator){
        prplot.setData(evaluator.getPR());
        fplot.setData(evaluator.getFMeasure());
    }
}
