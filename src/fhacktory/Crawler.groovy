package fhacktory

import edu.uci.ics.crawler4j.crawler.Page
import edu.uci.ics.crawler4j.crawler.WebCrawler
import edu.uci.ics.crawler4j.parser.HtmlParseData
import edu.uci.ics.crawler4j.url.WebURL
import org.jsoup.*
import groovy.transform.CompileStatic
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.util.regex.Pattern

/**
 * @version $Id$
 */
@CompileStatic
class Crawler extends WebCrawler
{
    private static
    final Pattern FITLER = ~/.*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz))$/

    @Override
    public boolean shouldVisit(WebURL url)
    {
        String href = url.getURL().toLowerCase()
        return !FITLER.matcher(href).matches()
    }

    @Override
    public void visit(Page page)
    {
        String url = page.getWebURL().getURL()
        System.out.println("Visiting " + url)

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData()

            Document doc = Jsoup.parse(htmlParseData.getText());

            Elements elements = new Elements();

            elements.addAll(doc.getElementsMatchingText("PTDR"))
            elements.addAll(doc.getElementsByTag("cite"))
            
            if (elements.size() > 0) {
                System.out.println("Found citations on " + url)
                for (Element element : elements) {
                    System.out.println(" - " + element.text());
                }
            }
        }
    }
}
