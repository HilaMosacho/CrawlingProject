Code Overview:
The code performs crawling on wikipedia web pages, it uses a WebGraph that holds the links between all scanned web pages.
each page is scanned and has a words index.
each word has a rank on each page.
Web pages also has ranks according to the hub and authority values they got (HITS algorithm).
All of the ranks then computed to a one score with the TA method.
The interface accepts keywords from the user and returns the top 5 web pages with their ranks (from the range [0,1]). 

Main Classes :

WebGraph Class
Holds the "Links Graph" data structure between different web pages.

InvertedIndex Class
Manages a lexicographic sorted words list.
each word has a sorted list of urls, each url has a score: shows of the word divided by the length of the web page
a word is defined as a characters sequence seperated by whitespaces.

Search Class
defines the methods:

crawl: 
The method performs web page crawling on Wikipedia pages. 
Starting from  - http://simple.wikipedia.org/wiki/Albert_einstein 
and follow links of the form "href =" / wiki / xxx only. 
The function fills the data structures InvertedIndex, WebGraph with information of 100 different pages.(may configure differently)

HITS:
Implements the HITS algorithm for ranking web pages. 
Input: An object of type WebGraph. 
Output: a sorted list of pairs (ID, rank), 
The rank is the authority score of the page. 
The desired accuracy used as an halting condition for the function. 
The condition is explained as a comment within the code.

TA:
Implements the threshold algorithm, for computing the top-k elements.
Input: a list containing sorted lists of pairs (ID, rank). 
The method performs MAX aggregation (hard-coded) to rate scores according to individual words ranks + Rating of the WebGraph. 
The selection of the aggregation function is also explained as a comment within the code.