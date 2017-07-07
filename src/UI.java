

import Vis.LinePlot;


import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import java.io.IOException;
import java.util.*;

/**
 * Created by HP on 30.06.2017.
 */
public class UI {
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

    private int imgheight;
    private static GalleryRenderer renderer;
    private static File selectedin;
    private final JFileChooser fileChooser;
    private static DefaultListModel<String> listmodel;
    private  UISettings settings;
    private Calculator calculator;

    private LinePlot[] plots;

    public UI() {
        settings = new UISettings();
        calculator = new Calculator(settings);
        imgheight = 230;
        listmodel = new DefaultListModel<>();
        fileChooser = new JFileChooser();
        FileFilter imageFilter = new FileNameExtensionFilter(
                "Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.setFileFilter(imageFilter);

        setImageCanvas(new File("gui/input.png"), true, false);
        setImageCanvas(new File("gui/output.png"), false, false );

        //drop listener:
        new  FileDrop( imgleft, files -> {
                String mimetype= new MimetypesFileTypeMap().getContentType(files[0]);
                String type = mimetype.split("/")[0];
                if(type.equals("image")){
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

            renderer = new GalleryRenderer();
            list.setModel(listmodel);
           // list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setCellRenderer(renderer);
            list.addListSelectionListener(e -> {
                if (list.getSelectedValue() != null) {
                    ScoreItem s = renderer.getScoreItem(list.getSelectedValue().toString());
                    setImageCanvas(s.getFile(), false, true);
                    updatePlots(s.getHistogram(), false);
                }
            });
           scroll.setPreferredSize(new Dimension(500, 50));
        JMenuBar menuBar = new JMenuBar();
        JMenu inputmenu = new JMenu("Input");
        JMenuItem importmenu = new JMenuItem("Select Query Image.");
        importmenu.addActionListener(e -> importByDialog());
        inputmenu.add(importmenu);
        menuBar.add(inputmenu);

        JMenu outputmenu = new JMenu("Output");
        menuBar.add(outputmenu);
        JMenu settingsmenu = new JMenu("Settings");

        JMenuItem allsettings = new JMenuItem("All...");
        allsettings.addActionListener(e -> settings.toggleVisibility());
        settingsmenu.add(allsettings);
        menuBar.add(settingsmenu);

        frame.setJMenuBar(menuBar);
        plots = new LinePlot[3];
        plots[0] = new LinePlot(Color.RED);
        tabbedPane1.add(plots[0].getPanel(), "R");
        plots[1] = new LinePlot(Color.GREEN);
        tabbedPane1.add(plots[1].getPanel(), "G");
        plots[2] = new LinePlot(Color.BLUE);
        tabbedPane1.add(plots[2].getPanel(), "B");
    }

    public static void main(String[] args) {
        frame = new JFrame("MMDBS");
        frame.setContentPane(new UI().mainpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800,500);
        frame.setResizable(false);
        frame.setVisible(true);

    }

    private  void query(){
        File[] files = settings.getSearchFiles();
        int bincount = 50;
        int cellcount = 1;
        QFWrapper qf = new QFWrapper(settings.getBinCount());

        /*
            Feature extraction/loading for query image:
         */
        ColorHistogram in;
        String serQuery = Serializer.getPathSerialized(selectedin, cellcount, bincount);
        if (serQuery.length() > 0){
            in = Serializer.deserialize(serQuery);
        } else {
            in = new ColorHistogram(selectedin, settings.getCellCount(),settings.getBinCount());
            Serializer.serialize(in);
        }

        /*
            Feature extraction/loading for search images:
         */
        ArrayList<ColorHistogram> toSerialize = new ArrayList<>();
        ColorHistogram[] candidates = new ColorHistogram[files.length];
        for (int i= 0; i<files.length;i++){
            ColorHistogram c;
            String ser = Serializer.getPathSerialized(files[i], cellcount, bincount);
            if (ser.length() > 0){
                //this color histogram was already serialized.
                c = Serializer.deserialize(ser);
                if (c == null){
                    //deserializing failed, compute histogram again...
                    c = new ColorHistogram(files[i], cellcount, bincount);
                    toSerialize.add(c);
                }
            } else {
                c = new ColorHistogram(files[i], cellcount, bincount);
                toSerialize.add(c);
            }
            candidates[i] = c;
        }

        /*
            Calculate sim
         */
        ArrayList<ScoreItem> score = calculator.run(in, candidates);
       Collections.sort(score, (o1, o2) -> o1.getScore().compareTo(o2.getScore()));

        listmodel.removeAllElements();
        for (ScoreItem s : score){
            listmodel.addElement(s.getFile().getName());
        }
        renderer.generateMap(score);

        //serialize new images:
        Iterator<ColorHistogram> iter = toSerialize.listIterator();
        while (iter.hasNext()){
            Serializer.serialize(iter.next());
        }

        //update plots:
        double[][] channeled = in.getMergedChannelHistograms();
        for (int p =0; p< plots.length; p++){
            plots[p].setHistogramData(channeled[p],true);
        }

    }

    private void updatePlots(ColorHistogram c, boolean isQueryImage){
        double[][] histograms = c.getMergedChannelHistograms();
        for (int channel= 0; channel < histograms.length; channel++){
            plots[channel].setHistogramData(histograms[channel], isQueryImage);
        }
    }

    private void setImageCanvas(File f, boolean left, boolean updateLabel){
        try {
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
