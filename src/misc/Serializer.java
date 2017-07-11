package misc;

import feature.*;

import java.io.*;

/**
 * Authors: P. Meschenmoser, C. Gutknecht
 */

public class Serializer  {
    /*
        This class is used for saving/loading colorhistograms that were already seen.
        For this, we access the subfolder 'ser', and an image gets serialized
        into the folder "hashcode(absolutepath)+removespecialchars(absolutepath)"
        There, we include bin- and cellcount into the .ser's file name.
     */

    public static void serialize(FeatureHistogram c){
        //create folder
        String path = "ser/" + generateFolderName(c.getFile());
        if (!createFolder(path)){
            System.out.println("Could not create folder.");
            return;
        }
        //create .ser file

        path += "/" + c.getType() + "-" + c.getCellCount() + "-" + c.getBinCount()+"-";
        path += c.getFile().getName().replaceFirst("[.][^.]+$", "") + ".ser";
        try {
            //serialize ColorHistogram into the file
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            System.out.println(path);
            out.writeObject(c);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FeatureHistogram deserialize(String path){
        try {
            //deserialize a given .ser file and return the ColorHistogram
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream(path));
            return (FeatureHistogram) stream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean createFolder(String path){
        File f = new File(path);
        if (!f.exists()){
            return f.mkdir();
        }
        return true;
    }

    public static String generateFolderName(File f){
        //generate a subfolder, which is unique (by 99.99%)
        //i.e. concat hashcode with absolutepath without special chars.
        return f.getAbsolutePath().hashCode() + f.getAbsolutePath().replaceAll("[^A-Za-z]", "");
    }

    public static String getPathSerialized(File f, int type, int cellcount, int bincount) {
        //For a given image file, this method returns the path to the serialized file
        // empty string, if not existent. Then, we will serialize the histogram...
        String path = "ser/" + generateFolderName(f);
        path += "/" + type + "-" + cellcount + "-" + bincount +"-";
        path += f.getName().replaceFirst("[.][^.]+$", "") + ".ser";
        if (new File(path).exists()){

            return path;
        }
        return "";
    }
}
