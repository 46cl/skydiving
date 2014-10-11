package fhacktory

import com.google.common.eventbus.EventBus
import edu.uci.ics.crawler4j.crawler.Page
import edu.uci.ics.crawler4j.crawler.WebCrawler
import edu.uci.ics.crawler4j.url.WebURL
import fhacktory.event.SkyblogFound
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.regex.Pattern

/**
 * @version $Id$
 */
@CompileStatic
class Crawler extends WebCrawler
{
    Logger logger = LoggerFactory.getLogger(Crawler.class)

    Pattern filter = ~/.*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz))$/

    // Cache skyblogs already found
    Set found = [] as Set

    @Override
    public boolean shouldVisit(WebURL url)
    {
        String href = url.getURL().toLowerCase()
        return !filter.matcher(href).matches()
    }

    @Override
    public void visit(Page page)
    {
        String url = page.getWebURL().getURL()

        logger.debug("Visiting {}", url)

        String domain = (page.webURL.subDomain.length() > 0 ? (page.webURL.subDomain + ".") : "") + page.webURL.domain
        boolean skyblog = domain.indexOf("skyrock.com") > 0 && !domain.startsWith("www") && !domain.startsWith("en")

        if (!skyblog || found.contains(skyblog)) {
            return
        }

        EventBus eventBus = Fhacktory.eventBus
        eventBus.post(new SkyblogFound([
                host: domain
        ]))

        found.add(skyblog)
    }
}
