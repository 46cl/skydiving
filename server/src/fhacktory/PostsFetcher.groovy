package fhacktory

import com.google.common.eventbus.EventBus
import fhacktory.data.Quote
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
                    String host = hosts.poll()

                    if (host) {
                        logger.info("http://" + host + "/rss.xml")
                        def http = new HTTPBuilder("http://" + host)
                        http.get(path: '/rss.xml', query: [mode: 'xml'], contentType: ContentType.XML) { resp, xml ->
                            def author = xml.channel.link.text().substring(7)
                            author = author.substring(0, author.size() - 13)
                            xml.item.each {
                                def content = Jsoup.parse(it.description.text()).text()
                                if (author && content) {
                                    Quote quote = new Quote([
                                            content: content,
                                            author : author
                                    ])
                                    eventBus.post(new NewQuoteEvent([quote: quote]))
                                    database.recordQuote(quote)
                                }
                            }
                        }
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
