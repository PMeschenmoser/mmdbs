package vis;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;

import java.awt.*;
/**
 * Created by Phil on 06.07.2017.
 */
public class HistogramPlot {
    private InteractivePanel panel;
    private XYPlot plot;
    private DataSeries queryHistogram;
    private DataSeries outputHistogram;
    private LineRenderer queryRenderer;
    private LineRenderer outputRenderer;

    public HistogramPlot(Color outputColor){
        plot = new XYPlot();
        plot.setLegendVisible(true);
        plot.getAxisRenderer(XYPlot.AXIS_X).setLabel(new Label("Bins"));
        plot.getAxis(XYPlot.AXIS_X).setAutoscaled(true);
        plot.getAxis(XYPlot.AXIS_Y).setAutoscaled(true);
        plot.setInsets(new Insets2D.Double(10.0, 10.0, 10.0, 10.0));
        plot.getAxisRenderer(XYPlot.AXIS_Y).setLabel(new Label("Abs. Freq."));
        queryRenderer = new DefaultLineRenderer2D();
        queryRenderer.setColor(new Color(0.0f, 0.0f, 0.0f));
        outputRenderer = new DefaultLineRenderer2D();
        outputRenderer.setColor(outputColor);

        panel = new InteractivePanel(plot);
    }

    public void setHistogramData(double[] c,  String label, boolean isQueryObject){
        DataTable table = new DataTable(Integer.class, Double.class);
        for (int i= 0; i<c.length; i++){
            table.add(i, c[i]);
            table.add(i+1, c[i]);
        }
        DataSeries series = new DataSeries(label, table);
        if (isQueryObject){
            clearQueryLine();
            queryHistogram = series;
            plot.add(queryHistogram);
            plot.setLineRenderers(queryHistogram, queryRenderer);
            plot.getPointRenderers(queryHistogram).get(0).setShape(null);
        } else {
            clearOutputLine();
            outputHistogram = series;
            plot.add(outputHistogram);
            plot.setLineRenderers(outputHistogram, outputRenderer);
            plot.getPointRenderers(outputHistogram).get(0).setShape(null);
        }
    }
    public void clearQueryLine(){
        if (queryHistogram != null) plot.remove(queryHistogram);
        queryHistogram = null;
    }

    public void clearOutputLine(){
        if (outputHistogram != null) plot.remove(outputHistogram);
        outputHistogram = null;
    }
    public InteractivePanel getPanel(){
        return panel;
    };
}
