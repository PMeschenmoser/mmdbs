import java.awt.*;
import java.io.*;

/**
 * Created by Phil on 06.07.2017.
 */
public class Serializer  {

    public static void serialize(ColorHistogram c){
        String path = "ser/" + generateFolderName(c.getFile());
        if (!createFolder(path)){
            System.out.println("Could not create folder.");
            return;
        }
        path += "/" + c.getCellCount() + "-" + c.getBinCount()+"-";
        path += c.getFile().getName().replaceFirst("[.][^.]+$", "") + ".ser";
        try {
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(c);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ColorHistogram deserialize(String path){
        try {
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream(path));
            return (ColorHistogram) stream.readObject();
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
        return f.getAbsolutePath().hashCode() + f.getAbsolutePath().replaceAll("[^A-Za-z]", "");
    }

    public static String getPathSerialized(File f, int cellcount, int bincount) {
        String path = "ser/" + generateFolderName(f);
        path += "/" + cellcount + "-" + bincount +"-";
        path += f.getName().replaceFirst("[.][^.]+$", "") + ".ser";
        if (new File(path).exists())return path;
        return "";
    }
}
