package Model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/*
Output is responsible for exporting data to the output file for the user.

Output writes to 2 files - a text file and a serialize file.

The class is defined as a critical section and therefore is synchronized.
 */
public class Output {
    //set name of output path for serialize file
    public final String outputPath = "file/temp/output";
    private List<UrlStruct> output = new ArrayList<UrlStruct>();

    //singeltone
    private static Output instance = null;
    public static Output getInstance() {
        if(instance == null) {
            instance = new Output();
        }
        return instance;
    }
    private Output() {
        ////check if not exist page details (first running)
        if(Files.exists(Paths.get(outputPath))){
            try{
                FileInputStream inputStream = new FileInputStream(outputPath);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                //try and catch if the file is empty
                try {
                    output = (List<UrlStruct>) objectInputStream.readObject();
                }
                catch (Exception e){}

                objectInputStream.close();
            }
            catch (Exception e){
                System.out.println("file " + outputPath + " not found...");
            }
        }
        else{
            try {

                //create stream and save
                File file = new File(outputPath);
                //create the path folder
                file.getParentFile().mkdirs();

                //object for file work (save)
                FileOutputStream outputStream = new FileOutputStream(file);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(output);
                objectOutputStream.close();
            }
            catch (Exception e){}

        }

    }

    //save data to serialize file
    private synchronized void save(){
        try {
            //object for file work (save)
            FileOutputStream outputStream = new FileOutputStream(outputPath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(output);
            objectOutputStream.close();

        } catch (Exception ex) {
            System.out.println("Error save WaitingQ...");
        }
    }

    public synchronized void addUrl(UrlStruct url){
        output.add(url);
        save();
        print(url);
    }

    //return true if url exist in output
    public synchronized boolean isExist(UrlStruct url){
        //return true if url exist in output
        for (UrlStruct tempUrl:output) {
            if(tempUrl.getUrl().toString().equals(url.getUrl().toString()))
                return true;
        }

        return false;
    }

    //clear data from serialize file
    public synchronized void clear() {
        output.clear();
        save();
        printAll();
    }

    //append url info to exist user file
    public synchronized void print(UrlStruct url){
        //save output text file
        try {
            File file = new File("output.txt");
            FileWriter out = new FileWriter(file,true);

            out.append(("Deep: " + url.getDeep()) + "  ||  Ratio: " + url.getRatio() + "\n");
            out.append((url.getUrl()).toString() + "\n");

            out.close();
        }
        catch (Exception e){
            System.out.println("Error create file: output.txt");
        }
    }

    //print all info of urls t (from start of scan) to user file
    public synchronized void printAll(){
        //save output text file
        try {
            File file = new File("output.txt");
            PrintWriter out = new PrintWriter(file);

            //for each file print
            for (UrlStruct tempUrl:output) {
                out.println(("Deep: " + tempUrl.getDeep()) + "  ||  Ratio: " + tempUrl.getRatio());
                out.println((tempUrl.getUrl()).toString());
            }

            out.close();
        }
        catch (Exception e){
            System.out.println("Error create file: output.txt");
        }
    }
}
