package Controller;

import Model.*;

import java.util.ArrayList;
import java.util.List;

/*
The Process is responsible for scanning a specific page.
The class receives a Web page URL and does the following:
- Get all HTML addresses that exist on thi current page.
* Entering the addresses into the waitingQ. (if they have not been scanned in the past)
* Calculates the address ratio that exists on a page with the same domain.
* Add the information received to the current address to the output file. (for user)
* At the end of the scan - Delete the current address from the queue
 */
public class Process extends Thread {
    UrlStruct url;
    Output output = Output.getInstance();
    Pages pages = Pages.getInstance();
    WaitingQ waitingQ = WaitingQ.getInstance();
    int deepRec;

    public Process(UrlStruct url, int deepRec) {
        this.url = url;
        this.deepRec = deepRec;

        //start scan
        start();
    }

    public void run(){
        try {
            //get all urls contained in current url
            List<UrlStruct> urls = pages.getUrlsContained(url.getUrl());
            //list of url to waitingQ
            List<UrlStruct> urlsToWaitingQ = new ArrayList<UrlStruct>();
            double ratio = 0;

            for(UrlStruct tempUrl:urls){
                //calculate the ratio
                String tempHost = tempUrl.getUrl().getHost();
                String host = url.getUrl().getHost();
                if(tempHost.equals(host))
                    ratio += 1.0/urls.size();

                //check if url not already exist in output/watingQ/urlsToWaitingQ
                //and if deep not over deep recursion
                if(url.getDeep() < deepRec &&
                    !waitingQ.isExist(tempUrl) &&
                    !output.isExist(tempUrl) &&
                    !isExist(urlsToWaitingQ,tempUrl)) {
                    //add temp url to waiting Q
                    tempUrl.setDeep(url.getDeep() + 1);
                    urlsToWaitingQ.add(tempUrl);

                    //TerminalWin.getInstance().print("addWaitingQ: " + tempUrl.getUrl().toString());
                }
            }

            //send urlsToWaitingQ  to waitingQ
            waitingQ.addUrls(urlsToWaitingQ);

            //update ratio
            url.setRatio(ratio);
            //add the url to output
            output.addUrl(url);
            //remove this url from waitnigQ
            waitingQ.remUrl(url);
        }
        catch (Exception e){
            System.out.println("Error in process");
        }
    }

    //get list and url and return true if list contain the url
    boolean isExist(List<UrlStruct> list1, UrlStruct url){
        for (UrlStruct tempUrl:list1) {
            if(tempUrl.getUrl().toString().equals(url.getUrl().toString()))
                return true;
        }

        return false;
    }

}
