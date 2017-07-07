package vis;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Location;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;
import search.ScoreItem;
import java.awt.*;

/**
 * Created by Phil on 07.07.2017.
 */
public class ScorePlot {
    private XYPlot plot;
    private DataTable d;
    private InteractivePanel panel;

    public ScorePlot() {
        plot = new BarPlot();
        d = new DataTable(Integer.class, Double.class);
        panel =  new InteractivePanel(plot);
    }

    public void setValues(java.util.List<ScoreItem> score ){
        if (d!= null) plot.remove(d);
        d = new DataTable(Integer.class, Double.class, String.class);
        int x= 1;
        for (ScoreItem s: score){
            d.add(x, s.getScore(), s.getFile().getName());
            x++;
        }
        plot.setInsets(new Insets2D.Double(40.0, 40.0, 40.0, 40.0));

        plot.add(d);

        BarPlot.BarRenderer pointRenderer = (BarPlot.BarRenderer) plot.getPointRenderers(d).get(0);
    pointRenderer.setValueRotation(90);

        pointRenderer.setBorderStroke(new BasicStroke(3f));
        pointRenderer.setValueVisible(true);
        pointRenderer.setValueColumn(2);
        pointRenderer.setValueLocation(Location.NORTH);
        pointRenderer.setValueFont(Font.decode(null).deriveFont(Font.BOLD));
        Color COLOR1 =  new Color( 55, 170, 200);
        pointRenderer.setColor(
                new LinearGradientPaint(0f,0f, 0f,1f,
                        new float[] { 0.0f, 1.0f },
                        new Color[] { COLOR1, GraphicsUtils.deriveBrighter(COLOR1) }
                )
        );
        pointRenderer.setBorderStroke(new BasicStroke(1f));
        pointRenderer.setBorderColor(
                new LinearGradientPaint(0f,0f, 0f,1f,
                        new float[] { 0.0f, 1.0f },
                        new Color[] { GraphicsUtils.deriveBrighter(COLOR1), COLOR1 }
                )
        );
    }

    public InteractivePanel getPanel(){
        return panel;
    }
}
