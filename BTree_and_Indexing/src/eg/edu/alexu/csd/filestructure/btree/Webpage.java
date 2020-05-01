package eg.edu.alexu.csd.filestructure.btree;

import java.net.URL;

public class Webpage {
    private String docTitle;
    private String url;
    private String id;
    private String value;
    public Webpage(String docTitle, String url, String id, String value){
        this.docTitle = docTitle;
        this.url = url;
        this.id = id;
        this.value=value;
    }
    public String getValue(){return this.value;}

    public String getIndex() {return this.id;}
}
