package gui;

import search.ScoreItem;
import vis.ScorePlot;

import javax.swing.*;
import java.util.List;

/**
 * Created by Phil on 07.07.2017.
 */
public class ScoreView {
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
        plot = new ScorePlot();
        frame.getContentPane().add(plot.getPanel());
    }

    public void setScore(List<ScoreItem> score ){
        plot.setValues(score);
    }

    public void show(){
        frame.setVisible(true);
    }
}
