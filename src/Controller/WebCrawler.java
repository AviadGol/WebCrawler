package Controller;

import Model.Output;
import Model.UrlStruct;
import Model.UrlsInfo;
import Model.WaitingQ;
import View.TerminalWin;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;



/*
WebCrawler class is responsible for managing the network scan.

Receives a root address and sends it to a scan.
every address added to the waitingQ - the WebCrawler is responsible
for opening its own thread and sending it to the scan.

The WebCrawler has the option to perform a new scan/continuous scan
according to the type of constructor that the class was created for.

The WebCrawler is responsible for cleaning up all the data files
on which data from the previous scans are stored.
*/
public class WebCrawler extends Thread{
    //received from user
    URL urlRoot;
    static final String scanDataPath = "file/temp/dataScan";
    //index for scanData array
    final int deepRec = 0;
    final int numOfThread = 1;
    int[] scanData = new int[2];

    WaitingQ waitingQ = WaitingQ.getInstance();

    //c-tor for new scan
    public WebCrawler(URL urlRoot, int deepRec, int numOfThread) {
        this.urlRoot = urlRoot;
        scanData[this.deepRec] = deepRec;
        scanData[this.numOfThread] = numOfThread;
        //save data for continue the scan
        saveScanData();
        //clear all file
        clearAll();
        //add the path url to waitingQ
        waitingQ.addUrl(new UrlStruct(urlRoot,1));

        //start Web Crawler
        start();
    }
    //c-tor for continue scan
    public WebCrawler(){
        ////check if not exist page details (first running)
        if(Files.exists(Paths.get(scanDataPath))) {
            try {
                FileInputStream inputStream = new FileInputStream(scanDataPath);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                //try and catch if the file is empty
                try {
                    scanData = (int[]) objectInputStream.readObject();
                } catch (Exception e) {
                }

                objectInputStream.close();

                //start Web Crawler
                start();
            } catch (Exception e) {
                System.out.println("file " + scanDataPath + " not found...");
            }
        }
        else{
            System.out.println("file " + scanDataPath + " not found...");
        }
    }

    //main function
    public void run(){
        try {
            //make array of thread (according user chose)
            Process[] processes = new Process[scanData[numOfThread]];

            //As exists URL in the waitingQ
            //or there is a process that still works
            while (!waitingQ.isEmpty() || !allProcessesFinish(processes)){
                //scan "free" thread
                for(int i = 0; i < scanData[numOfThread]; i++)
                    if(processes[i] == null || !processes[i].isAlive()) {
                        //get next url from waitingQ
                        UrlStruct nextProcess = waitingQ.getNext();
                        if(nextProcess != null){
                            TerminalWin.getInstance().printGreen("Process-" + i + "-" + nextProcess.getUrl().toString());
                            //start scan url
                            processes[i] = new Process(nextProcess,scanData[deepRec]);
                        }

                    }
            }

            //Finish!!!
            TerminalWin.getInstance().printGreen("Finish!!!!");
        }
        catch (Exception e){
            System.out.println("Error in WebCrawler");
        }
    }

    //clear files for new scan
    public void clearAll(){
        WaitingQ.getInstance().clear();
        Output.getInstance().clear();
    }

    //check whether it's possible to continue scanning
    public static boolean isContinual(){
        //check if exist files: waitingQ, output, scanData
        if(Files.exists(Paths.get(scanDataPath))){
            return true;
        }

        return false;
    }

    //save data for continue the scan
    public void saveScanData(){
        try {
            //object for file work (save)
            FileOutputStream outputStream = new FileOutputStream(scanDataPath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(scanData);
            objectOutputStream.close();

        } catch (Exception ex) {
            System.out.println("Error save data scan...");
        }
    }

    //return true if all processes finish
    public boolean allProcessesFinish(Process[] processes){
        for (Process tempProcess:processes) {
            if(tempProcess != null && tempProcess.isAlive())
                return false;
        }

        return true;
    }
}
