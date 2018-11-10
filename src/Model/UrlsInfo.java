package Model;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;

/*
The UrlsInfo is responsible for saving the last modified date of URL.
The class is defined as a critical section and therefore is synchronized.
 */
public class UrlsInfo {
    //path for hash table with all page details
    final String urlsInfoPath = "file/temp/page info";
    private Hashtable<URL,Long> urlInfo = new Hashtable<URL,Long>();

    //singeltone
    private static UrlsInfo instance = null;
    public static UrlsInfo getInstance() {
        if(instance == null) {
            instance = new UrlsInfo();
        }
        return instance;
    }
    private UrlsInfo() {
        //check if not exist page details (first running)
        if(Files.exists(Paths.get(urlsInfoPath))){
            try {
                //read xml file to urlInfo
                FileInputStream inputStream = new FileInputStream(urlsInfoPath);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                //try and catch if the file is empty
                try {
                    urlInfo = (Hashtable<URL,Long>) objectInputStream.readObject();
                }
                catch (Exception e){
                    System.out.println("Error open Urls info " + e.getMessage());
                }

                objectInputStream.close();
            }
            catch (Exception e){
                System.out.println("page " + urlsInfoPath + " not found...");
            }
        }
        else{
            try {
                //create stream and save
                File file = new File(urlsInfoPath);
                //create the path folder
                file.getParentFile().mkdirs();

                //object for file work (save)
                FileOutputStream outputStream = new FileOutputStream(file);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(urlInfo);
                objectOutputStream.close();

            } catch (Exception ex) {}
        }

    }

    private synchronized void save(){
        try {
            //object for file work (save)
            FileOutputStream outputStream = new FileOutputStream(urlsInfoPath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(urlInfo);
            objectOutputStream.close();

        } catch (Exception ex) {
            System.out.println("Error save Urls Info...");
        }
    }

    //get URL and return the last scan Url From Web
    public synchronized Long lastUpdate(URL url){
        if(urlExists(url))
            return urlInfo.get(url);

        return new Long(-1);
    }

    public synchronized void addUrl(URL url, Long date){
        urlInfo.put(url,date);
        save();
    }

    public synchronized void remUrl(URL url, Long date){
        try{
            urlInfo.remove(url,date);
            save();
        }
        catch (Exception e){
            System.out.println("remove: not exist page " + url);
        }

    }

    public synchronized void updateUrl(URL url, Long date){
        if(urlExists(url)){
            urlInfo.replace(url,date);
        }
        else{
            addUrl(url,date);
        }
    }

    public synchronized void clear(){
        urlInfo.clear();
        save();
    }

    //get url and check if it exist on the disk
    public synchronized boolean urlExists(URL url){
        return urlInfo.containsKey(url);
    }

    //get url and check if it update on the disk
    public synchronized boolean isUpdate(URL url, Long date){
        if(urlExists(url))
            if(urlInfo.get(url).compareTo(date) == 0)
                return true;

        return false;
    }

}
