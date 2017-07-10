package gui;

import eval.Evaluator;
import vis.FMeasurePlot;
import vis.PRPlot;

import javax.swing.*;

/**
 * Authors: P. Meschenmoser, C. Gutknecht
 */
public class EvalView {
    //Swing Wrapper for Evaluation Dialog. It contains the tabbed component and is accessible via "Output/Show Evaluation.."
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
        tabbedeval.add("Precision/Recall" , prplot.getPanel()); //add pr plot as a new tab

        fplot = new FMeasurePlot();
        tabbedeval.add("F-Measure" , fplot.getPanel()); //add fmeasure plot as a new tab
    }

    public void show(){
        frame.setVisible(true);
        frame.toFront();
        frame.repaint();
    }
    public void setEvaluator(Evaluator evaluator){
        prplot.setData(evaluator.getPR()); //update plots
        fplot.setData(evaluator.getFMeasure());
    }
}
