import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
    private GalleryRenderer renderer;
    public UI() {
        imgwidth = 200;


        try {
            BufferedImage left = ImageIO.read(new File("data/hedgehog/image_0001.jpg"));
            double ratio = left.getHeight()*1.0/left.getWidth()*1.0;
            ImageIcon iconleft = new ImageIcon(left.getScaledInstance(imgwidth, (int) (imgwidth*ratio),  Image.SCALE_SMOOTH));
            imgleft.setIcon(iconleft);

            BufferedImage right = ImageIO.read(new File("data/hedgehog/image_0002.jpg"));
            ratio = right.getHeight()*1.0/right.getWidth()*1.0;
            ImageIcon iconright = new ImageIcon(right.getScaledInstance(imgwidth, (int) (imgwidth*ratio),  Image.SCALE_SMOOTH));
            imgright.setIcon(iconright);


            File[] files = new File("data/hedgehog").listFiles();
            renderer = new GalleryRenderer(files);
            String[] paths = new String[files.length];
            DefaultListModel<String> model = new DefaultListModel<>();
            list.setModel(model);
            for (int i=0; i< paths.length; i++){
                model.addElement(files[i].getName());
            }
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
            scroll.setPreferredSize(new Dimension(300, 200));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MMDBS");
        frame.setContentPane(new UI().mainpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(700,500);
        frame.setVisible(true);

        File[] files = new File("data/hedgehog").listFiles();
        int bincount =50;
        for (int i= 0; i<files.length-1;i++){
            ColorHistogram c1 = new ColorHistogram(files[i], 1,bincount);
            ColorHistogram c2 = new ColorHistogram(files[i+1], 1, bincount);
           System.out.println(Comparator.euclid(c1,c2,0));
            System.out.println(Comparator.quadraticform(c1,c2, Util.createHumanPerceptionSimilarityMatrix(bincount)));
            System.out.println("-----");
            //c.getResults() returns a 2D dim. first dim -> different cells, 2nd dim -> color bins
        }
    }
}
