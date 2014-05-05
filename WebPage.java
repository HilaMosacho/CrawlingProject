import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WebPage {


    //members
    private String url; //page address
    private Set<String> links; //adjacency list
    private int numOfWords; //total number of words
    private Map<String, Integer> words; //words on page and counter for each word <word, counter>
    private String urlContent;
    private Set<WebPage> inNeighbors;
    private Set<WebPage> outNeighbors;
    private HitsData hitsData;


    //constructor
    public WebPage(String url) {
        this.url = url;
        links = new HashSet<String>();
        words = new TreeMap<String, Integer>();
        inNeighbors = new HashSet<WebPage>();
        outNeighbors = new HashSet<WebPage>();
        urlContent = getURLContent(url);
        scanAndUpdate();
        parseLinks();
        hitsData = new HitsData(url);
    }


    private void parseLinks() {
        final String prefix = "http://simple.wikipedia.org";

        Pattern pattern = Pattern.compile("href=\"/wiki/[^\"]*\"");
        Matcher matcher = pattern.matcher(urlContent);

        while (matcher.find()) {
            String item = matcher.group();
            String postfix = item.substring(6, item.length() - 1);
            links.add(prefix + postfix);
        }
    }

    //public methods

    public String getUrl() {
        return url;
    }

    public Set<String> getLinks() {
        return links;
    }

    public int getNumOfWords() {
        return numOfWords;
    }

    public Map<String, Integer> getWords() {
        return words;
    }

    public HitsData getHitsData() {
        return hitsData;
    }

    public Set<WebPage> getInNeighbors() {
        return inNeighbors;
    }

    public Set<WebPage> getOutNeighbors() {
        return outNeighbors;
    }

    @Override
    public String toString() {
        return "url = " + url;
    }

    /**
     * Receives a url of a page and returns the page content as a string
     */
    private String getURLContent(String url) {

        String text;
        StringBuilder stringbuilder = new StringBuilder();

        try {
            URL my_url = new URL(url);
            BufferedReader br = new BufferedReader(new InputStreamReader(my_url.openStream()));

            while ((text = br.readLine()) != null) {
                stringbuilder.append(text);
                stringbuilder.append(System.getProperty("line.separator"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringbuilder.toString();
    }


    /**
     * Receives a url of a web page and updates the  fields:
     * "numOfWords" = number of words in that web page
     * "words": TreeMap, key is a word that appear in that web page and the
     * value is the number of assurances of that word in that web page
     */
    private void scanAndUpdate() {
        String[] wordsArray = urlContent.split(" +");
        numOfWords = wordsArray.length;
        for (int i = 0; i < numOfWords; i++) {

            if (words.containsKey(wordsArray[i])) {
                int value = words.get(wordsArray[i]);
                value++;
                words.put(wordsArray[i], value);
            } else {
                words.put(wordsArray[i], 1);
            }
        }
    }

    public void insertInNeighbor(WebPage webPage) {
        inNeighbors.add(webPage);
    }

    public void insertOutNeighbor(WebPage webPage) {
        outNeighbors.add(webPage);

    }
}
