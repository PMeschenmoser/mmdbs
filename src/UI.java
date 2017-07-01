

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
import java.util.Comparator;

/**
 * Created by HP on 30.06.2017.
 */
public class UI {
    private JPanel mainpanel;
    private JLabel imgleft;
    private JLabel imgright;
    private JScrollPane scroll;
    private JList list;
    private int imgwidth;
    private static GalleryRenderer renderer;
    private static File input;
    private static File searchfolder;
    private final JFileChooser fileChooser;
    private static DefaultListModel<String> listmodel;

    public UI() {
        imgwidth = 250;
        searchfolder = new File("data/hedgehog/");
        listmodel = new DefaultListModel<>();
        fileChooser = new JFileChooser();
        FileFilter imageFilter = new FileNameExtensionFilter(
                "Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.setFileFilter(imageFilter);

        setImage(new File("gui/input.png"), true);
        setImage(new File("gui/output.png"), false);

        //drop listener:
        new  FileDrop( imgleft, files -> {
                String mimetype= new MimetypesFileTypeMap().getContentType(files[0]);
                String type = mimetype.split("/")[0];
                if(type.equals("image")){
                    input = files[0];
                    setImage(input, true);
                    calculateScore();
                }
            });
        //import via select dialog
        imgleft.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int value = fileChooser.showOpenDialog(mainpanel);
                if (value == JFileChooser.APPROVE_OPTION) {
                    input = fileChooser.getSelectedFile();
                    setImage(input, true);
                    calculateScore();
                }
            }
        });

            File[] files = new File("data/hedgehog").listFiles();
            renderer = new GalleryRenderer(files);
            String[] paths = new String[files.length];
            list.setModel(listmodel);
            list.setCellRenderer(renderer);
            list.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    String p = renderer.getSelectedPath(list.getSelectedValue().toString());
                    try {
                        BufferedImage  right = ImageIO.read(new File(p));
                        double ratio = right.getHeight()*1.0/right.getWidth()*1.0;
                        ImageIcon iconright = new ImageIcon(right.getScaledInstance(imgwidth, (int) (imgwidth*ratio),  Image.SCALE_SMOOTH));
                        imgright.setIcon(iconright);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            scroll.setPreferredSize(new Dimension(300, 100));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MMDBS");
        frame.setContentPane(new UI().mainpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(700,500);
        frame.setVisible(true);
    }

    private static void calculateScore(){
        File[] files = searchfolder.listFiles();
        int bincount =50;
        QFWrapper qf = new QFWrapper(bincount);
        ColorHistogram in = new ColorHistogram(input, 1,bincount);
        ArrayList<ScoreItem> score = new ArrayList<>();

        for (int i= 0; i<files.length;i++){
            ColorHistogram c = new ColorHistogram(files[i], 1, bincount);
            score.add(new ScoreItem(files[i], Measures.euclid(in,c,0)));
            System.out.println(Measures.euclid(in,c,0));
            //System.out.println(Measures.quadraticform(in,c, qf));
            //System.out.println("-----");
            //c.getResults() returns a 2D dim. first dim -> different cells, 2nd dim -> color bins
        }
        Collections.sort(score, (o1, o2) -> o2.getScore().compareTo(o1.getScore()));
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
    private void setImage(File f, boolean left){

        try {
            BufferedImage img = ImageIO.read(f);
            double ratio = img.getHeight()*1.0/img.getWidth()*1.0;
            ImageIcon icon = new ImageIcon(img.getScaledInstance(imgwidth, (int) (imgwidth*ratio),  Image.SCALE_SMOOTH));
            if (left){
                imgleft.setIcon(icon);
            } else {
                imgright.setIcon(icon);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
