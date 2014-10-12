package fhacktory.crawling

import com.google.common.eventbus.EventBus
import fhacktory.db.Database
import fhacktory.data.Quote
import fhacktory.event.CrawlingEvent
import fhacktory.event.NewQuoteEvent
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @version $Id$
 */
class PostsFetcher implements Runnable
{
    Queue<String> hosts = new LinkedList<>()

    Database database

    EventBus eventBus

    PostsFetcher(EventBus eventBus, Database database)
    {
        this.eventBus = eventBus
        this.database = database
    }

    @Override
    void run()
    {
        try {
            Logger logger = LoggerFactory.getLogger(PostsFetcher.class)
            hosts.addAll(database.hosts())
            logger.info("Post fetcher initialized with {} quotes", hosts.size())

            while (true) {
                try {
                    //String host = hosts.poll()
                    String host = hosts.remove((int) (Math.random() * hosts.size()))

                    eventBus.post(new CrawlingEvent([
                            url: host
                    ]))

                    if (host) {
                        logger.info("http://" + host + "/rss.xml")
                        def http = new HTTPBuilder("http://" + host)
                        http.get(path: '/rss.xml', contentType: ContentType.XML) { resp, xml ->
                            String author = xml.channel.link.text().substring(7)
                            author = author.substring(0, author.size() - 13)
                            xml.item.each {
                                def content = Jsoup.parse(it.description.text()).text()
                                if (author && content) {
                                    Quote quote = new Quote([
                                            content: content.trim(),
                                            author : author.trim()
                                    ])
                                    eventBus.post(new NewQuoteEvent([quote: quote]))
                                }
                            }
                        }
                    }

                    if (hosts.size() == 0) {
                        logger.info("Resetting post fetcher with host list", hosts.size())
                        hosts.addAll(database.hosts())
                    }
                } catch (Exception e) {
                    e.printStackTrace()
                }
                Thread.sleep(1000);
            }
        }
        catch (Exception e) {
            e.printStackTrace()
        }
    }
}
