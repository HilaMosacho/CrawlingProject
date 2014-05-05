
import java.util.Map;
import java.util.TreeMap;


public class InvertedIndex implements WebGraphObserver {

    //members
    private Map<String, Map<String, Double>> wordsList; //<Word, <URL, Score>>

    //constructor
    public InvertedIndex() {
        wordsList = new TreeMap<String, Map<String, Double>>();
    }

    public Map<String, Double> getUrlAndScoreForWord(String word) {
        return wordsList.get(word);
    }

    //public methods
    /*
    gets a url as a string, a word and number of shows on url
   * updates the structure with the new word
    * */
    public void updateWordInUrl(String url, String word, int cnt, int totalCount) {
        Map<String, Double> currUrl;

        if (!wordsList.containsKey(word)) { //new word
            //create new Map
            currUrl = new TreeMap<String, Double>();
        } else { //already seen word
            //get url map
            currUrl = wordsList.get(word); //TreeMap<URL, Score>

            //url exists for current word
            assert currUrl != null; // error. we should scan each page once
        }
        currUrl.put(url, (double) cnt / totalCount);//new url for curr word
        wordsList.put(word, currUrl);
    }

    @Override
    public void webPageAdded(WebPage webPage) {
        String url = webPage.getUrl();
        int totalCount = webPage.getNumOfWords();
        Map<String, Integer> words = webPage.getWords();
        for (Map.Entry<String, Integer> entry : words.entrySet()) {
            updateWordInUrl(url, entry.getKey(), entry.getValue(), totalCount);
        }
    }
}

