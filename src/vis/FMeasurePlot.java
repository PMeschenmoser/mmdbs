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
public class FMeasurePlot {

    private XYPlot plot;
    private DataTable d;
    private InteractivePanel panel;
    private LineRenderer lines;

    public FMeasurePlot() {
        plot = new XYPlot();
        //axis labels:
        plot.getAxisRenderer(XYPlot.AXIS_X).setLabel(new Label("#Results"));
        plot.getAxisRenderer(XYPlot.AXIS_Y).setLabel(new Label("F1-Score"));
        //make ticks always visible:
        plot.getAxisRenderer(XYPlot.AXIS_Y).setTickLabelsOutside(false);
        plot.getAxisRenderer(XYPlot.AXIS_X).setTickLabelsOutside(false);

        //line renderer
        lines = new DefaultLineRenderer2D();
        lines.setColor(new Color(0.0f, 0.0f, 0.0f));

        //pannable + zoomable panel:
        panel =  new InteractivePanel(plot);
        panel.setPannable(true);
        panel.setZoomable(true);
    }

    public void setData(double[] f){
        if (d!= null) plot.remove(d);//remove old data
        //fill data by #results vs f-score
        d = new DataTable(Integer.class, Double.class);
        for (int i=0; i<f.length; i++){
            d.add(i+1, f[i]);
        }
        plot.add(d);
        plot.setLineRenderers(d, lines);

        //c.f. PRPlot
        plot.getAxis(XYPlot.AXIS_X).setRange(-0.3, 1.3);
        plot.getAxis(XYPlot.AXIS_Y).setRange(-0.3, 1.3);

        //render
        panel.repaint();
    }
    public InteractivePanel getPanel(){
        return panel;
    }
}
