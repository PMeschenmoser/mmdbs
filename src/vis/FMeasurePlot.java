package vis;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;

import java.awt.*;

/**
 * Created by Phil on 08.07.2017.
 */
public class FMeasurePlot {

    private XYPlot plot;
    private DataTable d;
    private InteractivePanel panel;
    private LineRenderer lines;

    public FMeasurePlot() {
        plot = new XYPlot();
        plot.getAxisRenderer(XYPlot.AXIS_X).setLabel(new Label("#Results"));
        plot.getAxisRenderer(XYPlot.AXIS_Y).setLabel(new Label("F1-Score"));
        plot.getAxisRenderer(XYPlot.AXIS_Y).setTickLabelsOutside(false);
        plot.getAxisRenderer(XYPlot.AXIS_X).setTickLabelsOutside(false);

        lines = new DefaultLineRenderer2D();
        lines.setColor(new Color(0.0f, 0.0f, 0.0f));
        panel =  new InteractivePanel(plot);
        panel.setPannable(true);
        panel.setZoomable(true);
    }

    public void setData(double[] f){
        if (d!= null) plot.remove(d);
        d = new DataTable(Integer.class, Double.class);
        for (int i=0; i<f.length; i++){
            d.add(i, f[i]);
        }
        plot.add(d);
        plot.setLineRenderers(d, lines);
        plot.getAxis(XYPlot.AXIS_X).setRange(-0.3, 1.3);
        plot.getAxis(XYPlot.AXIS_Y).setRange(-0.3, 1.3);
        panel.repaint();
    }
    public InteractivePanel getPanel(){
        return panel;
    }
}
