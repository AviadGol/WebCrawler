package Model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/*
The WaitingQ is responsible for managing the waiting queue for the URLs.
The WaitingQ allows saving and deleting URLs from the queue.

There is another queue ("runingQ") that holds the list of urls that are scanning now,
so that we don't scan the same URL twice.

The class saves the waiting queue in bits file on disk.

The class is defined as a critical section and therefore is synchronized.
 */
public class WaitingQ {
    //path for waiting queue
    public final String waitingQPath = "file/temp/waitingQ";
    private List<UrlStruct> waitingQ = new ArrayList<UrlStruct>();
    private List<UrlStruct> runingQ = new ArrayList<UrlStruct>();

    //singeltone
    private static WaitingQ instance = null;
    public static WaitingQ getInstance() {
        if(instance == null) {
            instance = new WaitingQ();
        }
        return instance;
    }
    private WaitingQ() {
        ////check if not exist page details (first running)
        if(Files.exists(Paths.get(waitingQPath))){
            try{
                FileInputStream inputStream = new FileInputStream(waitingQPath);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                //try and catch if the file is empty
                try {
                    waitingQ = (List<UrlStruct>) objectInputStream.readObject();
                }
                catch (Exception e){}

                objectInputStream.close();
            }
            catch (Exception e){
                System.out.println("page waitingQ not found...");
            }

        }
        else{

            try {

                //create stream and save
                File file = new File(waitingQPath);
                //create the path folder
                file.getParentFile().mkdirs();

                //object for file work (save)
                FileOutputStream outputStream = new FileOutputStream(file);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(waitingQ);
                objectOutputStream.close();
            }
            catch (Exception e){}

        }

    }

    private synchronized void save(){
        try {
            //object for file work (save)
            FileOutputStream outputStream = new FileOutputStream(waitingQPath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(waitingQ);
            objectOutputStream.close();

        } catch (Exception ex) {
            System.out.println("Error save WaitingQ...");
        }
    }

    public synchronized void addUrl(UrlStruct url){
        waitingQ.add(url);
        save();
    }

    public synchronized void addUrls(List<UrlStruct> urls){
        for (UrlStruct tempUrl:urls) {
            waitingQ.add(tempUrl);
        }
        save();
    }

    public synchronized boolean isExist(UrlStruct url){
        for (UrlStruct tempUrl:waitingQ) {
            if(tempUrl.getUrl().toString().equals(url.getUrl().toString()))
                return true;
        }

        return false;
    }

    //return next url for scan
    public synchronized UrlStruct getNext(){
        //check of waitingQ not empty
        if(!isEmpty()){
            //foreach url in waiting Q check if it not on running Q
            for (UrlStruct tempUrl:waitingQ) {
                if(!runingQ.contains(tempUrl)){
                    //add tempUrl to running Q and return it
                    runingQ.add(tempUrl);
                    return tempUrl;
                }
            }
        }

        return null;
    }

    public synchronized void remUrl(UrlStruct url){
        //remove from waiting Q
        if(waitingQ.contains(url))
            waitingQ.remove(waitingQ.indexOf(url));
        //remove from running Q
        if(runingQ.contains(url))
            runingQ.remove(runingQ.indexOf(url));
    }

    public synchronized boolean isEmpty(){
        return waitingQ.isEmpty();
    }

    public synchronized void clear() {
        waitingQ.clear();
        save();
    }

}
