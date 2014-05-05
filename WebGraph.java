import java.util.*;


public class WebGraph implements Iterable<WebPage> {

    //members
    private Map<String, WebPage> pages; //<URL , WebPage>
    private Set<WebGraphObserver> observers;

    //constructor
    public WebGraph() {
        pages = new HashMap<String, WebPage>();
        observers = new HashSet<WebGraphObserver>();
    }

    public void addPage(WebPage webPage) {
        //checks if page already exists in graph
        if (pages.containsKey(webPage.getUrl()))
            return;

        pages.put(webPage.getUrl(), webPage);
        for (WebGraphObserver o : observers) {
            o.webPageAdded(webPage);
        }
    }

    public int getSize() {
        return pages.size();
    }

    public void addObserver(WebGraphObserver observer) {
        observers.add(observer);
    }

    @Override
    public Iterator<WebPage> iterator() {
        return pages.values().iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Web Graph:");
        sb.append("\n");

        for (WebPage webPage : this) {
            sb.append(webPage);
            sb.append("\n");
        }

        return sb.toString();
    }


}
