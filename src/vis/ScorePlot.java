package vis;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.graphics.Location;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;
import search.ScoreItem;

import java.awt.*;

/**
 * Authors: P. Meschenmoser, C. Gutknecht
 */
public class ScorePlot {
    /*
        Interactive Bar Plot for showing the output's ranking.
     */

    private XYPlot plot;
    private DataTable d;
    private InteractivePanel panel;

    public ScorePlot() {
        //init bar plot, set autoscaling and axis labels
        plot = new BarPlot();
        plot.getAxis(BarPlot.AXIS_X).setAutoscaled(true);
       // plot.getAxis(BarPlot.AXIS_Y).setAutoscaled(true);
        plot.getAxisRenderer(BarPlot.AXIS_Y).setLabel(new Label("Distance"));
        plot.getAxisRenderer(BarPlot.AXIS_X).setLabel(new Label("File"));
        // add some margin
        plot.setInsets(new Insets2D.Double(40.0, 80.0, 40.0, 40.0));
        //interactive panel:
        panel =  new InteractivePanel(plot);
        panel.setPannable(true);
        panel.setZoomable(false);
    }

    public void setValues(java.util.List<ScoreItem> score ){
        if (d!= null) plot.remove(d); //remove old data

        //insert new data
        d = new DataTable(Integer.class, Double.class, String.class);
        //DataTable(xposition, distance, label)
        int x= 1;
        double max = 0;
        for (ScoreItem s: score){
            d.add(x, s.getScore(), s.getFile().getName());
            if (max < s.getScore()) max = s.getScore(); //needed as there are problems with autoscale
            x++;
        }
        plot.add(d);

        //specify renderers for the new data objects
        BarPlot.BarRenderer pointRenderer = (BarPlot.BarRenderer) plot.getPointRenderers(d).get(0);
        pointRenderer.setValueRotation(90);

        pointRenderer.setBorderStroke(new BasicStroke(3f));
        pointRenderer.setValueVisible(true);
        pointRenderer.setValueColumn(2);
        pointRenderer.setValueLocation(Location.NORTH); //labels above bars
        pointRenderer.setValueFont(Font.decode(null).deriveFont(Font.BOLD));

        /*
            Apply a color gradient to the bars.

         */
        Color color =  new Color( 55, 170, 200);
        pointRenderer.setColor(
                new LinearGradientPaint(0f,0f, 0f,1f,
                        new float[] { 0.0f, 1.0f },
                        new Color[] { color, GraphicsUtils.deriveBrighter(color) }
                )
        );
        pointRenderer.setBorderStroke(new BasicStroke(1f));
        pointRenderer.setBorderColor(
                new LinearGradientPaint(0f,0f, 0f,1f,
                        new float[] { 0.0f, 1.0f },
                        new Color[] { GraphicsUtils.deriveBrighter(color), color }
                )
        );

        //because autoscale seems to fail at large differences (c.f. euclid vs canberra)
        plot.getAxis(BarPlot.AXIS_Y).setMax(max);
        //render
        panel.repaint();
    }

    public InteractivePanel getPanel(){
        return panel;
    }
}
