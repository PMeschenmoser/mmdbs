package vis;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;

import java.awt.*;

/**
 * Authors: P. Meschenmoser, C. Gutknecht
 */
public class PRPlot {

    private XYPlot plot;
    private DataTable d;
    private InteractivePanel panel;
    private LineRenderer lines;

    public PRPlot() {
        /*
            Instantiate XYPlot with connected Points
            Set Axis Labels and set labels to the inside (-> always visible in the canvas)
         */
        plot = new XYPlot();
        plot.getAxisRenderer(XYPlot.AXIS_X).setLabel(new Label("Recall"));
        plot.getAxisRenderer(XYPlot.AXIS_Y).setLabel(new Label("Precision"));
        plot.getAxisRenderer(XYPlot.AXIS_Y).setTickLabelsOutside(false);
        plot.getAxisRenderer(XYPlot.AXIS_X).setTickLabelsOutside(false);

        //line renderer
        lines = new DefaultLineRenderer2D();
        lines.setColor(new Color(0.0f, 0.0f, 0.0f));

        //interactive panel
        panel =  new InteractivePanel(plot);
        panel.setPannable(true);
        panel.setZoomable(true);
    }

    public void setData(double[][] pr){
        if (d!= null) plot.remove(d); //remove old data
        //insert new data
        d = new DataTable(Double.class, Double.class); //(x,y)
        for (int i=0; i<pr.length; i++){
            d.add(pr[i][1], pr[i][0]);
        }
        plot.add(d);
        plot.setLineRenderers(d, lines);

        //PR is only in the domain of [0,1]
        //but we extend it a little bit for a better view...
        plot.getAxis(XYPlot.AXIS_X).setRange(-0.3, 1.3);
        plot.getAxis(XYPlot.AXIS_Y).setRange(-0.3, 1.3);

        //render
        panel.repaint();
    }
    public InteractivePanel getPanel(){
        return panel;
    }
}
