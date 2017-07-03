import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by user on 03.07.2017.
 */
public class UISettings {
    private static JFrame frame;
    private JPanel panel1;
    private  JTextField searchpathtext;
    private JLabel searchpathlabel;
    private JButton browsesearchpath;
    private JFileChooser pathChooser;
    private File defaultpath;

    public UISettings() {
        defaultpath = new File("data/hedgehog/");

        pathChooser = new JFileChooser();
        pathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        pathChooser.setAcceptAllFileFilterUsed(false);

        frame = new JFrame("All Settings");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setVisible(false);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        browsesearchpath.addActionListener(e -> setPathByChooser());
    }

    public static void toggleVisibility(){
        frame.setVisible(!frame.isVisible());
    }

    private void setPathByChooser(){
        if (pathChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            searchpathtext.setText(pathChooser.getSelectedFile().getAbsolutePath());
        }
    }

    public File[] getSearchFiles(){
        ArrayList<File> files = new ArrayList<>();
        File directory = new File(searchpathtext.getText());
        if (directory.isDirectory()){
            listfiles(directory, files);
        } else {
            listfiles(defaultpath, files);
        }
        return files.toArray(new File[files.size()]);   //for invalid text input
    }


    private void listfiles(File directory, ArrayList<File> in){
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                in.add(file);
            } else if (file.isDirectory()) {
                listfiles(file, in);
            }
        }
    }
}
