package gui;

import search.ScoreItem;
import vis.ScorePlot;

import javax.swing.*;
import java.util.List;

/**
 * Authors: P. Meschenmoser, C. Gutknecht
 */
public class ScoreView {
    //Swing Wrapper for Bar Chart...
    private JFrame frame;
    private JPanel panel;
    private ScorePlot plot;

    public ScoreView() {
        frame = new JFrame("Score View");
        frame.setContentPane(panel);

        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(false);
        frame.setSize(700, 500);

        //add score plot to the jframe...
        plot = new ScorePlot();
        frame.getContentPane().add(plot.getPanel());
    }

    public void setScore(List<ScoreItem> score ){
        plot.setValues(score); //update vis
        frame.repaint(); //needed!
    }

    public void show(){
        frame.setVisible(true);
        frame.toFront();
        frame.repaint();
    }
}
