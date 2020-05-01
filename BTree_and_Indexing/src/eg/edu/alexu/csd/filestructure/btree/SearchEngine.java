package eg.edu.alexu.csd.filestructure.btree;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.management.RuntimeErrorException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SearchEngine implements ISearchEngine {
    private List <HashMap>files;
    private List <String>paths;

    public SearchEngine(int n){
        this.files = new ArrayList<>();
        this.paths = new ArrayList<>();
    }


    List readFile(String filePath) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(filePath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        List<Webpage> webpages = new ArrayList<Webpage>();
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String docTitle = node.getAttributes().getNamedItem("title").getNodeValue();
                String url = node.getAttributes().getNamedItem("url").getNodeValue();
                String ID = node.getAttributes().getNamedItem("id").getNodeValue();
                String value = node.getTextContent();

                webpages.add(new Webpage(docTitle, url, ID, value));
            }
        }
        return webpages;
    }

    @Override
    public void indexWebPage(String filePath) {
        if(isNullOrEmpty(filePath)){
            throw new RuntimeErrorException(new Error());
        }

        File file =  new File(filePath);
        if(!file.exists()){
            throw new RuntimeErrorException(new Error());
        }

        List<Webpage> webpages = new ArrayList<Webpage>();

        try {
            webpages = readFile(filePath);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        HashMap<String, BTree> indexes = new HashMap();
        for(int i=0; i<webpages.size(); i++){
            String[] words =  webpages.get(i).getValue().replaceAll("\n"," ").toLowerCase().split(" ");
            BTree WordTree = new BTree(128);
            for(int j = 0; j<words.length; j++){
                if(words[j].equals("")){
                    continue;
                }
                if(WordTree.search(words[j])==null){
                    WordTree.insert(words[j], 1);
                }else{
                    int rank = ((int)WordTree.search(words[j]))+1;
                    WordTree.delete(words[j]);
                    WordTree.insert(words[j],rank);
                }
            }
            indexes.put(webpages.get(i).getIndex(), WordTree);
        }
        paths.add(file.getPath());
        files.add(indexes);
    }

    @Override
    public void indexDirectory(String directoryPath) {
        if(isNullOrEmpty(directoryPath)){
            throw new RuntimeErrorException(new Error());
        }
        File directory =  new File(directoryPath);
        if (!directory.exists()){
            throw new RuntimeErrorException(new Error());
        }
        for(int i =0; i<directory.listFiles().length; i++){
            if(directory.listFiles()[i].isDirectory()){
                indexDirectory(directory.listFiles()[i].getPath());
            }else{
                indexWebPage(directory.listFiles()[i].getPath());
            }
        }
    }

    @Override
    public void deleteWebPage(String filePath) {
        if(isNullOrEmpty(filePath)){
            throw new RuntimeErrorException(new Error());
        }
        File file =  new File(filePath);
        if(!file.exists()){
            throw new RuntimeErrorException(new Error());
        }
        for(int i = 0 ; i<paths.size(); i++){
            if(paths.get(i).equals(filePath)){
                files.remove(i);
                paths.remove(i);
            }
        }
    }

    @Override
    public List<ISearchResult> searchByWordWithRanking(String word) {
        List<ISearchResult> searched = new ArrayList<>();
        if(word!=(null)){
            if(word.equals("")){
                return searched;
            }
            String toBeFound = word.toLowerCase();
            for(int i = 0; i<files.size(); i++){
                HashMap temp = files.get(i);
                Iterator iterator = temp.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry entry = (Map.Entry) iterator.next();
                    BTree tree = (BTree) entry.getValue();
                    if(tree.search(toBeFound)!= null){
                        searched.add(new SearchResult((String) entry.getKey(),(int)tree.search(toBeFound)));
                    }
                }
            }
            return searched;
        }else{
            throw new RuntimeErrorException(new Error());
        }
    }

    @Override
    public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
        List<ISearchResult> searched = new ArrayList<>();
        if(sentence!=(null)){
            if(sentence.equals("")){
                return searched;
            }
            String[] words = sentence.replaceAll("\n"," ").toLowerCase().split(" ");
            for(int i= 0; i<words.length;i++){
                List<ISearchResult> temp = searchByWordWithRanking(words[i]);
                for(int j = 0; j<temp.size(); j++){
                    searched.add(temp.get(j));
                }
            }
            return searched;
        } else{
            throw new RuntimeErrorException(new Error());
        }
    }

    private boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty() && !str.trim().isEmpty()){
            return false;
        }
        return true;
    }
}
