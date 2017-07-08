package gui;

import search.ScoreItem;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GalleryRenderer extends DefaultListCellRenderer {
    Font font;
    Map<String, ImageIcon> map;
    Map<String, ScoreItem> pathlookup;

    public GalleryRenderer(){
        font = new Font("helvetica", Font.BOLD, 12);
    }

    public void generateMap(java.util.List<ScoreItem> items){
        map = new HashMap<>();
        pathlookup = new HashMap<>();
        int iconwidth = 50;
        double ratio;
        for (ScoreItem item: items){
            File img = item.getFile();
            try {
                BufferedImage right = ImageIO.read(img);
                ratio = right.getHeight()*1.0/right.getWidth()*1.0;
                String key = img.getParentFile().getName()+ "/" + img.getName();
                map.put(key, new ImageIcon(right.getScaledInstance(iconwidth, (int) (iconwidth*ratio),  Image.SCALE_SMOOTH)));
                pathlookup.put(key, item);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public ScoreItem getScoreItem(String key){
        return  pathlookup.get(key);
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