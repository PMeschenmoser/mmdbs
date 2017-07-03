

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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
    private JLabel labelout;
    private JLabel labelin;
    private int imgwidth;
    private static GalleryRenderer renderer;
    private static File selectedin;
    private static File searchfolder;
    private final JFileChooser fileChooser;
    private static DefaultListModel<String> listmodel;
    private  UISettings settings;


    public UI() {
        settings = new UISettings();
        imgwidth = 250;
        listmodel = new DefaultListModel<>();
        fileChooser = new JFileChooser();
        FileFilter imageFilter = new FileNameExtensionFilter(
                "Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.setFileFilter(imageFilter);

        setImage(new File("gui/input.png"), true, false);
        setImage(new File("gui/output.png"), false, false );

        //drop listener:
        new  FileDrop( imgleft, files -> {
                String mimetype= new MimetypesFileTypeMap().getContentType(files[0]);
                String type = mimetype.split("/")[0];
                if(type.equals("image")){
                    selectedin = files[0];
                    setImage(selectedin, true, true );
                    calculateScore();
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
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setCellRenderer(renderer);
            list.addListSelectionListener(e -> {
                String p = renderer.getSelectedPath(list.getSelectedValue().toString());
                setImage(new File(p), false, true);
            });
            scroll.setPreferredSize(new Dimension(300, 100));
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
    }

    public static void main(String[] args) {
        frame = new JFrame("MMDBS");
        frame.setContentPane(new UI().mainpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(700,500);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private  void calculateScore(){
        File[] files = settings.getSearchFiles();
        int bincount =50;
        QFWrapper qf = new QFWrapper(bincount);
        ColorHistogram in = new ColorHistogram(selectedin, 1,bincount);
        ArrayList<ScoreItem> score = new ArrayList<>();

        for (int i= 0; i<files.length;i++){
            ColorHistogram c = new ColorHistogram(files[i], 1, bincount);
            score.add(new ScoreItem(files[i], Measures.euclid(in,c,0)));
            //System.out.println(Measures.quadraticform(in,c, qf));
            //System.out.println("-----");
            //c.getResults() returns a 2D dim. first dim -> different cells, 2nd dim -> color bins
        }
        Collections.sort(score, (o1, o2) -> o1.getScore().compareTo(o2.getScore()));
        ScoreItem[] scorearr = new ScoreItem[score.size()];
        scorearr = score.toArray(scorearr);
        File[] allfiles = new File[score.size()];
        listmodel.removeAllElements();
        for (int i= 0; i<scorearr.length; i++){
            allfiles[i] = scorearr[i].getFile();
            listmodel.addElement(allfiles[i].getName());
        }
        renderer.generateMap(allfiles);
    }


    private void setImage(File f, boolean left, boolean updateLabel){
        try {
            BufferedImage img = ImageIO.read(f);
            double ratio = img.getHeight()*1.0/img.getWidth()*1.0;
            ImageIcon icon = new ImageIcon(img.getScaledInstance(imgwidth, (int) (imgwidth*ratio),  Image.SCALE_SMOOTH));
            if (left){
                imgleft.setIcon(icon);
                if (updateLabel) labelin.setText("Query Image: " + f.getName());
            } else {
                imgright.setIcon(icon);
                if (updateLabel) labelout.setText("Selected: " + f.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void importByDialog(){
        int value = fileChooser.showOpenDialog(mainpanel);
        if (value == JFileChooser.APPROVE_OPTION) {
            selectedin = fileChooser.getSelectedFile();
            setImage(selectedin, true, true);
            calculateScore();
        }
    }
}
