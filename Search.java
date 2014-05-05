import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class Search {

    //search crawling settings:
    public final static int NUM_OF_WEB_PAGES_TO_CRAWL = 300;
    public final static String START_URL = "http://simple.wikipedia.org/wiki/Albert_einstein";

    public final static double threshold = 1 / Math.pow(2, 100);
    private static Map<String, HitsData> ranks = new HashMap<String, HitsData>(); // <URL, HitsData> for isConverge method


    //for numResultsAboveThreshold use:
    private static int index;
    private static int aboveTH;
    //

    public static void crawl(WebGraph webGraph, String url, int graphSizeLimit) {
        WebPage webPage = new WebPage(url);
        webGraph.addPage(webPage);

        Queue<WebPage> queue = new ArrayDeque<WebPage>();
        queue.add(webPage);

        while (webGraph.getSize() <= graphSizeLimit) {
            WebPage first = queue.poll();
            if (first == null)
                break;

            for (String link : first.getLinks()) {
                if (webGraph.getSize() > graphSizeLimit)
                    break;

                WebPage newPage = new WebPage(link);
                webGraph.addPage(newPage);

                newPage.insertInNeighbor(first);
                first.insertOutNeighbor(newPage);

                queue.add(newPage);
            }
        }
    }

    public static List<ResHitsData> hits(WebGraph webGraph) {

        double norm;

        while (!isConverges(webGraph)) {
            norm = 0.0;

            // update all authority values first
            for (WebPage currWebPage : webGraph) {
                currWebPage.getHitsData().setAuthority(0.0);

                double sumOfHubs = 0;
                for (WebPage inWebPage : currWebPage.getInNeighbors()) {
                    sumOfHubs += inWebPage.getHitsData().getHubness();
                }
                double prevAuth = currWebPage.getHitsData().getAuthority();
                double newAuth = prevAuth + sumOfHubs;
                currWebPage.getHitsData().setAuthority(newAuth);

                // calculate the sum of the squared auth values to normalise
                norm += Math.pow(currWebPage.getHitsData().getAuthority(), 2);
            }

            norm = Math.sqrt(norm);

            // update the auth scores
            for (WebPage currWebPage : webGraph) {
                double authority = currWebPage.getHitsData().getAuthority() / norm;
                currWebPage.getHitsData().setAuthority(authority);
            }
            norm = 0.0;


            // update all hubness values
            for (WebPage currWebPage : webGraph) {
                currWebPage.getHitsData().setHubness(0.0);

                double sumOfAuth = 0;
                for (WebPage outWebPage : currWebPage.getOutNeighbors()) {
                    sumOfAuth += outWebPage.getHitsData().getAuthority();
                }
                double prevHub = currWebPage.getHitsData().getHubness();
                double newHub = prevHub + sumOfAuth;
                currWebPage.getHitsData().setHubness(newHub);

                // calculate the sum of the squared hub values to normalise
                norm += Math.pow(currWebPage.getHitsData().getHubness(), 2);
            }

            norm = Math.sqrt(norm);

            // update the auth scores
            for (WebPage currWebPage : webGraph) {
                double hubness = currWebPage.getHitsData().getHubness() / norm;
                currWebPage.getHitsData().setHubness(hubness);
            }
        }

       return results(webGraph);

    }


    private static List<ResHitsData> results(WebGraph webGraph) {
        List<ResHitsData> results = new LinkedList<ResHitsData>();
        for (WebPage currWebPage : webGraph) {
            ResHitsData data = new ResHitsData(currWebPage.getUrl(), currWebPage.getHitsData().getAuthority());
            results.add(data);
        }

        // Sort results in decreasing authority score
        Collections.sort(results);

        // Return sorted results
        return results;
    }

    /**
     * In order to decide when the algorithm converges,
     * we measured the change of hubness and authority values between every two successive iterations.
     * To decide if the change is significant enough, we determined the threshold.
     * When the change is smaller than the threshold for all webPages tested,
     * we stopped the loop and got the final result.
     * Therefor, the threshold effects the number of iterations.
     * After a few trials, we decided of the value 1/(2^100) that enables 37 iterations.
     * We picked it in a way that more iteration wouldn't change the rank result and would not make it more precise.
     */
    public static boolean isConverges(WebGraph webGraph) {
        return numChanges(webGraph) == 0;

    }


    private static int numChanges(WebGraph webGraph) {
        int counter = 0;

        for (WebPage currWebPage : webGraph) {

            HitsData data = ranks.get(currWebPage.getUrl());
            if (data == null) { // Rank doesn't exist, add to map
                data = new HitsData(currWebPage.getUrl(),
                        currWebPage.getHitsData().getHubness(),
                        currWebPage.getHitsData().getAuthority());
                ranks.put(currWebPage.getUrl(), data);
                counter++; // Increase counter - new rank
            } else // Rank exists, compare and update
            {
                double d1 = Math.abs(data.getHubness() - currWebPage.getHitsData().getHubness());
                double d2 = Math.abs(data.getAuthority() - currWebPage.getHitsData().getAuthority());
                if (d1 > threshold || d2 > threshold)
                    counter++; // Increase counter - above threshold

                data.setHubness(currWebPage.getHitsData().getHubness());
                data.setAuthority(currWebPage.getHitsData().getAuthority());
            }
        }
        return counter;
    }


    public static List<ItemData> TA(List<CategoryData> categories, int k) {

        if (categories.isEmpty())
            return null;
        topItems.clear();

        // Create results list
        Map<String, ItemData> results = new HashMap<String, ItemData>();
        //convert to a List
        List<ItemData> res;
        res = new LinkedList<ItemData>(results.values());
        //init variables for numResultsAboveThreshold calc
        index = aboveTH = 0;
        // Do sorted access on all categories
        for (int i = 0; numResultsAboveThreshold(res) < k; i++) {

            if (i == categories.size()) {
                // Update threshold after each full sorted access iteration
                updateThreshold();

                // Reset i to 0
                i = 0;
            }

            // Get next category
            CategoryData category = categories.get(i);

            // Get top item in this category
            ItemData topItem = category.next();
            if (topItem == null)
                continue; // Continue if category has no more items

            // Add item to top items list
            topItems.add(topItem);

            if (results.containsKey(topItem.getName()))
                continue; // Continue if item already in results

            // Create scores list
            List<Double> scores = new LinkedList<Double>();

            // Do random access on all categories to find this item's score
            for (int j = 0; j < categories.size(); j++) {
                ItemData temp = categories.get(j).find(topItem.getName());
                if (temp == null)
                    continue;

                scores.add(temp.getScore());
            }

            // Calculate score and add item to results list
            double score = maxAggregate(scores);
            ItemData newItem = new ItemData(topItem.getName(), score);
            results.put(newItem.getName(), newItem);
            res = new LinkedList<ItemData>(results.values());
        }

        return results(results.values());


    }

    private static void updateThreshold() {
        // Add top items scores to scores list
        List<Double> scores = new LinkedList<Double>();
        for (ItemData item : topItems) {
            scores.add(item.getScore());
        }

        // Calculate new threshold
        TA_threshold = maxAggregate(scores);

        // Clear top items list
        topItems.clear();

        //init variables for numResultsAboveThreshold calc
        index = aboveTH = 0;

    }

    /*calculate and returns the number of results currently above TH value*/
    private static int numResultsAboveThreshold(List<ItemData> results) {
        int i;
        ItemData currItem;
        for (i = index; i < results.size(); i++) { //elements we still haven't seen
            currItem = results.get(i);
            if (currItem.getScore() >= TA_threshold)
                aboveTH++;
        }
        index = i;
        return aboveTH;
    }

    /**
     *
     * We chose to use Maximum aggregation,
     * that is because we willing to give the most accurate
     * and relevant results to the keywords that were entered.
     * The bigger the rank of the page in each category, the bigger it's score on the top-k list,
     * therefor it is more relevant to the search.
     *
     * @param scores input collection of scores
     * @return aggregative score
     */
    private static double maxAggregate(List<Double> scores) {

        if (scores.isEmpty())
            return 0.0;

        double max = 0.0;
        for (double d : scores) {
            if (max < d)
                max = d;
        }

        return max;
    }

    /**
     * @param items item collection
     * @return list of items
     */
    private static List<ItemData> results(Collection<ItemData> items) {
        // Copy items to linked list
        List<ItemData> list = new LinkedList<ItemData>();
        for (ItemData item : items) {
            list.add(item);
        }
        // Sort list in decreasing score order
        Collections.sort(list);

        // Return sorted list
        return list;
    }

    private static double TA_threshold;
    private static List<ItemData> topItems = new LinkedList<ItemData>();



    //main function

    public static void main(String[] args) throws IOException {
        WebGraph webGraph = new WebGraph();
        InvertedIndex invertedIndex = new InvertedIndex();
        webGraph.addObserver(invertedIndex);

        Search.crawl(webGraph, START_URL, NUM_OF_WEB_PAGES_TO_CRAWL);

        // write to file "urls.text" the web pages in the WebGraph
        File file = new File("urls.txt");
        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(webGraph.toString());
        bw.close();

        // run HITS algorithm and write to file "rank.txt" the best 5 pages
        file = new File("rank.txt");
        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }
        fw = new FileWriter(file.getAbsoluteFile());
        bw = new BufferedWriter(fw);

        List<ResHitsData> list;
        list = Search.hits(webGraph);
        for (int i = 0; i < 5; i++) {
            bw.write(list.get(i).toString());
        }
        bw.close();

        // receive keywords from user
        String input;
        String[] inputArray = null;
        Scanner in = new Scanner(System.in);
        System.out.println("Enter Keywords");
        while (!(input = in.nextLine()).equals("exit")) {
            inputArray = input.split(" ");


            List<CategoryData> categories = new LinkedList<CategoryData>();

            Map<String, Double> wordScore = null;
            for (int j = 0; j < inputArray.length; j++) {
                wordScore = invertedIndex.getUrlAndScoreForWord(inputArray[j]);
                if (wordScore == null) {
                    continue;
                }

                List<ItemData> items = new LinkedList<ItemData>();

                for (Map.Entry<String, Double> entry : wordScore.entrySet()) {
                    ItemData itemData = new ItemData(entry.getKey(), entry.getValue());
                    items.add(itemData);
                }
                CategoryData categoryData = new CategoryData(inputArray[j], items);
                categories.add(categoryData);
            }

            if ((wordScore == null) && (categories.size() == 0)) { //no keyword has result
                System.out.print("No Results, Please try again.\n");
                System.out.println("\nEnter Keywords");
                continue;
            }


            // create category for HITS results
            List<ItemData> items = new LinkedList<ItemData>();
            for (ResHitsData resHitsData : list) {
                ItemData itemData = new ItemData(resHitsData.getID(), resHitsData.getRank());
                items.add(itemData);
            }
            CategoryData categoryData = new CategoryData("HITS", items);
            categories.add(categoryData);

            //TA
            List<ItemData> result = Search.TA(categories, 5);
            if (result.size() > 0) {
                System.out.print("Search Results: \n");

                for (int i = 0; (i < 5) && (i < result.size()); i++) {
                    System.out.print(result.get(i).toString() + "\n");
                }
            } else {
                System.out.print("No Results, Please try again.\n");
            }

            System.out.println("\nEnter Keywords");


        }

        return;
    }

}
