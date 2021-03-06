import eval.Evaluator;
import feature.*;
import gui.*;
import misc.Serializer;
import search.Calculator;
import search.ScoreItem;
import vis.HistogramPlot;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Authors: P. Meschenmoser, C. Gutknecht
 */
public class UI {
    /*
        Bound UI elements
     */
    private static JFrame frame;
    private JPanel mainpanel;
    private JLabel imgleft;
    private JLabel imgright;
    private JScrollPane scroll;
    private JList list;
    private JTabbedPane tabbedPane1;
    private JLabel labelout;
    private JLabel labelin;
    private JPanel tabOriginal;
    private JMenuItem showscoreplot;
    private JMenuItem showevaluation;

    // some UI settings and addons
    private int imgheight;
    private static GalleryRenderer renderer;
    private static File selectedin; //currently chosen input file
    private JFileChooser fileChooser;
    private static DefaultListModel<String> listmodel; //for the gallery

    private java.util.List<ScoreItem> score;
    private Settings settings; //wrapper for the UI Settings panel
    private ScoreView scoreview; //wrapper for bar chart view
    private EvalView evalview; //wrapper for pr & f-measure plots
    private Calculator calculator; //search engine

    private HistogramPlot[] plots;

    public UI() {
        settings = new gui.Settings();
        calculator = new Calculator(settings);
        scoreview = new ScoreView();
        evalview = new EvalView();

        imgheight = 230;
        listmodel = new DefaultListModel<>();
        // init file chooser and allow only image files
        fileChooser = new JFileChooser();
        FileFilter imageFilter = new FileNameExtensionFilter(
                "Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.setFileFilter(imageFilter);
       // fileChooser.setCurrentDirectory(new File("data/"));

        //set left and right default images
        setImageCanvas(new File("gui/input.png"), true, false);
        setImageCanvas(new File("gui/output.png"), false, false );

        //import via drop listener:
        new FileDrop( imgleft, files -> {
                String mimetype= new MimetypesFileTypeMap().getContentType(files[0]);
                String type = mimetype.split("/")[0];
                if(type.equals("image")){ //only allow images
                    selectedin = files[0];
                    setImageCanvas(selectedin, true, true );
                    query();
                }
            });
        //import via select dialog
        imgleft.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                importByDialog();
            }
        });

        //update last query on button click in settings panel:
        settings.getUpdater().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                query();
            }
        });
        //define gallery behavior:
            renderer = new GalleryRenderer();
            list.setModel(listmodel);
            list.setCellRenderer(renderer);
            list.addListSelectionListener(e -> {
                //show selected list item on the right canvas and update histograms
                if (list.getSelectedValue() != null) {
                    ScoreItem s = renderer.getScoreItem(list.getSelectedValue().toString());
                    setImageCanvas(s.getFile(), false, true);
                    if (s.getHistogram().getType() == 0) updatePlots((ColorHistogram)s.getHistogram(), false);
                }
            });
           scroll.setPreferredSize(new Dimension(500, 50));

        initMenus();
        initHistograms();
    }

    public static void main(String[] args) {
        frame = new JFrame("CBIR");
        frame.setContentPane(new UI().mainpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800,500);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private void initMenus(){
        JMenuBar menuBar = new JMenuBar();
        //INPUT MENU:
        JMenu inputmenu = new JMenu("Input");
        JMenuItem importmenu = new JMenuItem("Select Query Image.");
        importmenu.addActionListener(e -> importByDialog());
        inputmenu.add(importmenu);
        menuBar.add(inputmenu);

        //OUTPUT MENU
        JMenu outputmenu = new JMenu("Output");
        menuBar.add(outputmenu);
        showscoreplot = new JMenuItem("Show Score Plot.");
        showscoreplot.setEnabled(false);
        showscoreplot.addActionListener(e -> scoreview.show());
        outputmenu.add(showscoreplot);

        showevaluation = new JMenuItem("Show Evaluation.");
        showevaluation.setEnabled(false);
        showevaluation.addActionListener(e -> evalview.show());
        outputmenu.add(showevaluation);

        //SETTINGS MENU
        JMenu settingsmenu = new JMenu("Settings");
        JMenuItem allsettings = new JMenuItem("All...");
        allsettings.addActionListener(e -> settings.show());
        settingsmenu.add(allsettings);
        menuBar.add(settingsmenu);

        //add to GUI:
        frame.setJMenuBar(menuBar);
    }

    private void initHistograms(){
        //instantiate 3 HistogramPlots and add their interactive panels as new tab components
        plots = new HistogramPlot[3];
        plots[0] = new HistogramPlot(Color.RED);
        tabbedPane1.add(plots[0].getPanel(), "R");
        plots[1] = new HistogramPlot(Color.GREEN);
        tabbedPane1.add(plots[1].getPanel(), "G");
        plots[2] = new HistogramPlot(Color.BLUE);
        tabbedPane1.add(plots[2].getPanel(), "B");
        //before any query image selected, to avoid boundary errors with empty plots:
        for (int i=1; i<=plots.length; i++) tabbedPane1.setEnabledAt(i,false);
    }
    private  void query(){
        //most important method
        //1. gather candidates
        //2. generate/deserialize color histograms
        //3. search, sort and chop off result list
        File[] files = settings.getSearchFiles(); //from search path + subfolders
        int cellcount = settings.getCellCount(); // per axis
        int bincount = settings.getBinCount();
        if (settings.getDescriptorIndex() > 0) {
            bincount =4;
        }


        /*
            Feature extraction/loading for query image:
         */
        int type = settings.getDescriptorIndex(); // 0 for ColorHistogram, 1 for EdgeHistogram, 2 for TamuraLikeDescriptor
        FeatureHistogram in;
        String serQuery = Serializer.getPathSerialized(selectedin, type, cellcount, bincount);
        if (serQuery.length() > 0){ // there exists a serialized version of selectedin with cellcount and bincount
            in = Serializer.deserialize(serQuery);
        } else {
            if(type == 0) in = new ColorHistogram(selectedin, cellcount, bincount);
            else if(type == 1) in = new EdgeHistogram(selectedin, cellcount);
            else in = new TamuraLikeDescriptor(selectedin, cellcount);
            Serializer.serialize(in);
        }

        /*
            Feature extraction/loading for search images:
         */
        ArrayList<FeatureHistogram> toSerialize = new ArrayList<>();
        FeatureHistogram[] candidates = new FeatureHistogram[files.length];
        for (int i= 0; i<files.length;i++){
            FeatureHistogram c;

            String ser = Serializer.getPathSerialized(files[i], type, cellcount, bincount);
            if (ser.length() > 0){
                //this color histogram was already serialized.
                //System.out.println("found serialized version: " + ser);
                c = Serializer.deserialize(ser);
                if (c == null){
                    //deserializing failed, compute histogram again...
                    System.out.println("deserializing failed, compute histogram again...");
                    if(type == 0) c = new ColorHistogram(files[i], cellcount, bincount);
                    else if(type == 1) c = new EdgeHistogram(files[i], cellcount);
                    else c = new TamuraLikeDescriptor(files[i], cellcount);
                    toSerialize.add(c);
                }
            } else {
                if(type == 0) c = new ColorHistogram(files[i], cellcount, bincount);
                else if(type == 1) c = new EdgeHistogram(files[i], cellcount);
                else c = new TamuraLikeDescriptor(files[i], cellcount);
                toSerialize.add(c);
            }
            candidates[i] = c;
        }

        /*
            Calculate similarity
         */
        score = calculator.run(in, candidates);
        Collections.sort(score, (o1, o2) -> o1.getScore().compareTo(o2.getScore())); //sort by ascending distance
        score =  score.subList(0, Math.min(settings.getMaxResults(), score.size())); //apply max results

        /* update gallery*/
        listmodel.removeAllElements();
        for (ScoreItem s : score){
            String key = s.getFile().getParentFile().getName()+ "/" + s.getFile().getName();
            listmodel.addElement(key);
        }
        renderer.generateMap(score);

        //serialize new images:
        Iterator<FeatureHistogram> iter = toSerialize.listIterator();
        while (iter.hasNext()){
            Serializer.serialize(iter.next());
        }

        //update histograms, score and evaluation plots
        if(type == 0) updatePlots((ColorHistogram) in, true);
        scoreview.setScore(score);
        evalview.setEvaluator(new Evaluator(in, score));

        //after first search:
        for (int i=1; i<=plots.length; i++) tabbedPane1.setEnabledAt(i,true);
        showscoreplot.setEnabled(true);
        showevaluation.setEnabled(true);
    }

    private void updatePlots(ColorHistogram c, boolean isQueryImage){
        //double[3][256] histograms
        double[][] histograms = c.getMergedChannelHistograms();
        for (int channel= 0; channel < histograms.length; channel++){
            plots[channel].setHistogramData(histograms[channel],  c.getFile().getName(),isQueryImage);
            if (isQueryImage) plots[channel].clearOutputLine();
        }
        tabbedPane1.repaint();
    }

    private void setImageCanvas(File f, boolean left, boolean updateLabel){
        try {
            //applies selected image either to the left or right canvas
            //the image has a constant height
            //we deduce the width from the original's image ratio
            BufferedImage img = ImageIO.read(f);
            double ratio =img.getWidth() *1.0/img.getHeight()*1.0;
            ImageIcon icon = new ImageIcon(img.getScaledInstance((int) (imgheight*ratio), imgheight,  Image.SCALE_SMOOTH));
            if (left){
                imgleft.setIcon(icon);
                if (updateLabel) imgleft.setText("Query Image: " + f.getName());
            } else {
                imgright.setIcon(icon);
                if (updateLabel) imgright.setText("Selected: " + f.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void importByDialog(){
        int value = fileChooser.showOpenDialog(mainpanel);
        if (value == JFileChooser.APPROVE_OPTION) {
            selectedin = fileChooser.getSelectedFile();
            setImageCanvas(selectedin, true, true);
            query();
        }
    }


}
