package Model;

import java.io.Serializable;
import java.net.URL;

/*
The UrlStruct is a structure for URL data.

Data that the department maintains:
- URL address.
- Depth of the recursion in which the page was discovered.
- The ratio of the page that the crawl has detected.
* We will not always use the last two data.

The class should have the ability to save bit files and therefore implements Serializable.
 */
public class UrlStruct implements Serializable {

    URL url;
    int deep;
    double ratio;

    public UrlStruct(URL url, int deep, double ratio) {
        this.url = url;
        this.deep = deep;
        this.ratio = ratio;
    }

    public UrlStruct(URL url, int deep) {
        this.url = url;
        this.deep = deep;
        ratio = -1;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public int getDeep() {
        return deep;
    }

    public void setDeep(int deep) {
        this.deep = deep;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

}
