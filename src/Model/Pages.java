package Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/*
Pages is responsible for scanning and storing the data (on disk)
of all scanned pages in the current computer.

- The department can receive a URL and extract the URLs (text/html mime type) from it.

- The department can save all the addresses discovered from a certain page
in order of "tree structure" on the disk.

- The department can receive a URL and check whether the page needs to be re-crawled
or that data already existing in the system from the last
crawl can be streamed - based on a recent revision date of the page.
 */
public class Pages {
    final String pagesPath = "file/pages/";
    UrlsInfo urlsInfo = UrlsInfo.getInstance();

    //c-tor
    private Pages(){}
    //singeltone
    private static Pages instance = null;
    public static Pages getInstance() {
        if(instance == null) {
            instance = new Pages();
        }
        return instance;
    }

    //get url and download the page to disk

//    public void downloadPage(URL url){
//        try {
//            //open buffer for read/write from web/file
//            BufferedReader readr =
//                    new BufferedReader(new InputStreamReader(url.openStream()));
//
//            //save the page in folder according the url
//            File file = new File(pagesPath + urlToFolderPath(url));
//            //create the path folder
//            file.getParentFile().mkdirs();
//
//            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//
//            // read each line from stream till end
//            String line;
//            while ((line = readr.readLine()) != null) {
//                writer.write(line);
//            }
//
//            readr.close();
//            writer.close();
//        }
//        catch (Exception e) {
//            System.out.println("Error with download page..");
//        }
//    }

    public void createUrlsFile(URL url){
        String folderPath = pagesPath + urlToFolderPath(url);
        //get list with all urls
        List<UrlStruct> urls = scanUrlFromWeb(url);

        //create stream and save urls file
        try {
            File file = new File(folderPath);
            //create the path folder
            file.getParentFile().mkdirs();

            OutputStream outputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(urls);
            objectOutputStream.close();

        } catch (Exception e) {
            System.out.println("Error create urls file: " + url.toString());
        }

        //save urls text file
        try {
            File file = new File(folderPath + ".txt");
            //create the path folder
            file.getParentFile().mkdirs();

            PrintWriter out = new PrintWriter(file);

            for (UrlStruct tempUrl:urls) {
                out.println((tempUrl.getUrl()).toString());
            }

            out.close();
        }
        catch (Exception e){
            System.out.println("Error create urls file: " + url.toString() + ".txt");
        }
    }

    //get url and return list with all urls which are contained inside (from web)
    public List<UrlStruct> scanUrlFromWeb(URL url){
        List<UrlStruct> urls = new ArrayList<UrlStruct>();

        try{
            //scan the page
            Document doc = Jsoup.connect(url.toString()).get();
            Elements links = doc.select("a[href]");

            //list for return
            List<Thread> threads = new ArrayList<Thread>();
            //foreach url in the page check if it text/html type (separately/thread)
            for (Element link : links){

                threads.add(new Thread(){

                    public void run(){
                        //check if the url is text/html mime type
                        try {
                            URL tempUrl = new URL(link.attr("abs:href"));
                            if(isHtml(tempUrl)) {
                                urls.add(new UrlStruct(tempUrl, -1, -1));
                                //TerminalWin.getInstance().print(tempUrl.toString());
                            }
                        }
                        catch (Exception e){}
                    }

                });

            }
            for (Thread tempThread:threads) {
                tempThread.start();
            }
            for (Thread tempThread:threads) {
                //wait maximum 20 sec for confirm url type
                tempThread.join(60000);
            }
        }
        catch (Exception e){
            System.out.println("Error scan url: " + url.toString() + ". " + e.getMessage());
        }

        return urls;
    }

    //get url and return list with all urls which are contained inside (from file)
    public List<UrlStruct> scanUrlFromFile(URL url){
        String folderPath = pagesPath + urlToFolderPath(url);
        List<UrlStruct> urls = new ArrayList<UrlStruct>();

        //check if the file exist
        if(Files.exists(Paths.get(folderPath))){
            try{
                FileInputStream inputStream = new FileInputStream(folderPath);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                urls = (List<UrlStruct>) objectInputStream.readObject();
                objectInputStream.close();
            }
            catch (Exception e){
                System.out.println("file " + folderPath + " not found...");
            }
        }

        return urls;
    }

    //get url and check if it text/html mime type
    public boolean isHtml(URL url){
        try{
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            //how mach time wait to connect
            connection.setConnectTimeout(7000);
            //get url type
            String type = connection.getHeaderField("Content-Type");

            connection.disconnect();
            //return true if url is html type
            return type.toLowerCase().contains("text/html".toLowerCase());

        }
        catch (Exception e){
            return false;
        }
    }

    //get url and return path to folder in the disk
    public String urlToFolderPath(URL url){
        try{
            String folderPath;
            //***.*****.****/urlPath
            String urlPath = url.getPath();
            //www.host.host/*******/******
            String host = url.getHost();

            //folderPath += domain name (withput 'www')
            folderPath = (host.startsWith("www.")) ? (host.substring(4)) : host;

            //while we have more sub folder in the urlPath
            //open more folder in folderPath
            while(urlPath.length() > 1 && urlPath.charAt(0) == '/'){
                //remove '/' from first string
                urlPath = urlPath.substring(1,urlPath.length());

                //check if exist more '/' in path
                if(urlPath.indexOf("/") >= 0){//exist '/' in next urlPath
                    String nextFolder = urlPath.substring(0,urlPath.indexOf("/"));

                    urlPath = urlPath.substring(urlPath.indexOf("/"));

                    folderPath += "/" + nextFolder;
                }
                else{//end of urlPath
                    folderPath += "/" + urlPath;

                    urlPath = "";
                }
            }

            return folderPath + "/urls";
        }
        catch (Exception e){
            System.out.println("Error with create path from url to folder..");
        }

        return null;
    }

    //returns a list of the current page URL contained
    public List<UrlStruct> getUrlsContained(URL url){
        //create connect to url
        HttpURLConnection httpConn= null;
        try {
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(7000);
        } catch (IOException e) {
            System.out.println("Error connect to:" + url.toString());
        }

        //page's last modified
        long webDate = httpConn.getLastModified();

        //check if the file urls need update
        if(urlsInfo.lastUpdate(url) < webDate || webDate == 0){
            //update urls File
            createUrlsFile(url);
            //update urlInfo
            urlsInfo.updateUrl(url,webDate);
        }

        return scanUrlFromFile(url);
    }

}
