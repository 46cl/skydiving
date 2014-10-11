package fhacktory

import fhacktory.data.Quote
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

    PostsFetcher(Database database)
    {
        this.database = database
    }

    @Override
    void run()
    {
        try {
            Logger logger = LoggerFactory.getLogger(PostsFetcher.class)
            hosts.addAll(database.hosts())
            logger.info("Post fetcher initialized with {} hosts", hosts.size())

            while (true) {

                try {
                    String host = hosts.poll()

                    logger.info("http://" + host + "/rss.xml")

                    def http = new HTTPBuilder("http://" + host)
                    http.get(path: '/rss.xml', query: [mode: 'xml'], contentType: ContentType.XML) { resp, xml ->
                        def author = xml.channel.title.text()
                        xml.item.each {
                            def content = Jsoup.parse(it.description.text()).text()
                            if (author && content) {
                                database.recordQuote(new Quote([
                                        content: content,
                                        author : author
                                ]))
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
