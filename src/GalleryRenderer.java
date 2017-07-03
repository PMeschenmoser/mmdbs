import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GalleryRenderer extends DefaultListCellRenderer {
    Font font;
    Map<String, ImageIcon> map;
    Map<String, String> pathlookup;
    public GalleryRenderer(){
        font = new Font("helvetica", Font.BOLD, 12);
    }

    public void generateMap(File[] files){
        map = new HashMap<>();
        pathlookup = new HashMap<>();
        int iconwidth = 50;
        double ratio;
        for (File img: files){
            try {
                BufferedImage right = ImageIO.read(img);
                ratio = right.getHeight()*1.0/right.getWidth()*1.0;
                map.put(img.getName(), new ImageIcon(right.getScaledInstance(iconwidth, (int) (iconwidth*ratio),  Image.SCALE_SMOOTH)));
                pathlookup.put(img.getName(), img.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public String getSelectedPath(String key){
        return pathlookup.get(key);
    }


    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        label.setIcon(map.get(value));
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setFont(font);


        return label;
    }
}