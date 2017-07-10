package gui;

import javax.activation.MimetypesFileTypeMap;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Authors: P. Meschenmoser, C. Gutknecht
 */
public class Settings {

    /*
        Objects bound to the UI.
     */
    private static JFrame frame;
    private JPanel panel1;
    private  JTextField searchpathtext;
    private JComboBox descriptorcombo;
    private JLabel searchpathlabel;
    private JButton browsesearchpath;
    private JLabel descriptorlabel;
    private JSpinner cellcountspinner;
    private JLabel cellcountlabel;
    private JLabel bincountlabel;
    private JSpinner bincountspinner;
    private JLabel metriclabel;
    private JComboBox metriccombobox;
    private JButton updatecurrent;
    private JLabel eigenvaluelabel;
    private JSpinner eigenvaluespinner;
    private JLabel threadslabel;
    private JSpinner threadsspinner;
    private JLabel maxresultslabel;
    private JSpinner maxresultsspinner;
    private JFileChooser pathChooser;
    private File defaultpath;

    /**
     * Authors: P. Meschenmoser, C. Gutknecht
     */
    public Settings() {
        /*
            Settings panel, accessible via "Settings/All.."

         */
        /*
            set default search path (in the project directory's subfolder) and initiate the folder selector
         */
        defaultpath = new File("data/");
        searchpathtext.setText("data/");

        pathChooser = new JFileChooser();
        pathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        pathChooser.setCurrentDirectory(defaultpath);
        pathChooser.setAcceptAllFileFilterUsed(false);

        /*
            Boundaries/Logic for spinner fields:

         */
        cellcountspinner.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
        bincountspinner.setModel(new SpinnerNumberModel(50, 1, 256, 1));
        eigenvaluespinner.setModel(new SpinnerNumberModel(50, 1, 256, 1));
        maxresultsspinner.setModel(new SpinnerNumberModel(54, 1, 1000, 1));
        threadsspinner.setModel(new SpinnerNumberModel(5, 1, 10, 1));
        bincountspinner.addChangeListener(e -> {
            //synchronize max eigenvalue with bincounts (i.e. there cannot be more than eigenvalues than bincounts)
            int val = (int) bincountspinner.getValue();
            eigenvaluespinner.setModel(new SpinnerNumberModel(val, 1, val, 1));
        });
        browsesearchpath.addActionListener(e -> setPathByChooser());

        frame = new JFrame("All Settings");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setVisible(false);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);

    }

    public static void show(){
        frame.setVisible(true);
        frame.toFront();
    }

    private void setPathByChooser(){
        if (pathChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            //apply selected directory to the input field:
            searchpathtext.setText(pathChooser.getSelectedFile().getAbsolutePath());
        }
    }

    public File[] getSearchFiles(){
        //returns all files that are contained
        ArrayList<File> files = new ArrayList<>();
        File directory = new File(searchpathtext.getText());
        if (directory.isDirectory()){
            listfiles(directory, files);
        } else {
            System.out.println("get from default path");
            listfiles(defaultpath, files);
        }
        return files.toArray(new File[files.size()]);   //for invalid text input
    }


    private void listfiles(File directory, ArrayList<File> in){
        //recursively add all images files to 'in'
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                String mimetype= new MimetypesFileTypeMap().getContentType(file); //allow only image files
                if(mimetype.split("/")[0].equals("image")) in.add(file);
            } else if (file.isDirectory()) {
                listfiles(file, in);
            }
        }
    }

    /*
        Simple Getter Methods
     */
    public int getCellCount(){
        return  (int) cellcountspinner.getValue();
    }

    public int getBinCount(){
        return  (int) bincountspinner.getValue();
    }
    public int getMaxEigen(){
        return  (int) cellcountspinner.getValue();
    }

    public int getThreadCount(){
        return  (int) threadsspinner.getValue();
    }

    public int getMaxResults(){ return (int) maxresultsspinner.getValue();}

    public String getMetric(){
        return (String) metriccombobox.getSelectedItem();
    }


    public JButton getUpdater(){
        //this is needed, as we apply an event listener in UI.java, which runs the search afterwards.
        return updatecurrent;
    }
}
