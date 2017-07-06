package Vis;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;

import java.awt.*;

/**
 * Created by Phil on 06.07.2017.
 */
public class LinePlot {
    private InteractivePanel panel;
    private XYPlot plot;
    private DataTable queryHistogram;
    private DataTable outputHistogram;
    private LineRenderer queryRenderer;
    private LineRenderer outputRenderer;

    public LinePlot(Color outputColor){
        queryHistogram = new DataTable(Integer.class, Double.class);
        outputHistogram = new DataTable(Integer.class, Double.class);
        plot = new XYPlot(queryHistogram, outputHistogram);

        Color black = new Color(0.0f, 0.0f, 0.0f);
        queryRenderer = new DefaultLineRenderer2D();
        queryRenderer.setColor(black);
        outputRenderer = new DefaultLineRenderer2D();
        outputRenderer.setColor(outputColor);
        panel = new InteractivePanel(plot);
    }

    public void setHistogramData(double[] c, boolean isQueryObject){
        DataTable tmp = new DataTable(Integer.class, Double.class);
        for (int i= 0; i<c.length; i++){
            tmp.add(i, c[i]);
            tmp.add(i+1, c[i]);
        }
        if (isQueryObject){
            plot.remove(queryHistogram);
            queryHistogram = tmp;
            plot.add(queryHistogram);
            plot.setLineRenderers(queryHistogram, queryRenderer);
            plot.getPointRenderers(queryHistogram).get(0).setShape(null);
        } else {
            plot.remove(outputHistogram);
            outputHistogram = tmp;
            plot.add(outputHistogram);
            plot.setLineRenderers(outputHistogram, outputRenderer);
            plot.getPointRenderers(outputHistogram).get(0).setShape(null);
        }
    }

    public InteractivePanel getPanel(){
        return panel;
    };
}
