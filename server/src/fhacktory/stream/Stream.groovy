package fhacktory.stream

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import fhacktory.data.Quote
import fhacktory.event.NewQuoteEvent
import fhacktory.event.PublishQuoteEvent
import fhacktory.flickr.FlickrClient
import fhacktory.nlp.NounsExtractor
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @version $Id$
 */
@CompileStatic
class Stream implements Runnable
{
    EventBus eventBus

    FlickrClient flickrClient

    NounsExtractor nounsExtractor

    List<Quote> quotes = new LinkedList<>()

    Logger logger = LoggerFactory.getLogger(Stream.class)

    Stream(EventBus eventBus, FlickrClient flickrClient, NounsExtractor nounsExtractor)
    {
        this.eventBus = eventBus
        this.flickrClient = flickrClient
        this.nounsExtractor = nounsExtractor
    }

    @Override
    void run()
    {
        while (true) {
            try {
                if (!quotes.isEmpty()) {
                    Quote quote = quotes.remove((int) (Math.random() * quotes.size()))
                    logger.info("Posting quote {}", quote.content)
                    eventBus.post(new PublishQuoteEvent([quote: quote]))
                }
            } catch (Exception e) {
                e.printStackTrace()
            }
            Thread.sleep(5000)
        }
    }

    @Subscribe
    public void onNewQuote(NewQuoteEvent event)
    {
        try {
            Quote quote = event.quote
            if (quoteIsValid(quote)) {

                // Get a picture based on nouns in que quote
                List<String> nouns = nounsExtractor.extractNouns(quote.content)
                if (nouns.size() == 0) {
                    return
                }
                def photoUrl = flickrClient.findAPhoto(nouns)
                if (!photoUrl) {
                    return
                }

                //quote.picture = "http://localhost:8144/photo?url=" + URLEncoder.encode(photoUrl as String, "UTF-8")
                quote.picture = photoUrl
                quotes.add(event.quote)
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    Boolean quoteIsValid(Quote quote)
    {
        // Filter out quotes too long
        if (quote.content.endsWith("...")) {
            return false
        }

        // ...and quotes too short
        if (quote.content.size() < 15) {
            return false
        }

        // Filter out quotes with links
        if (quote.content.indexOf("http") >= 0) {
            return false
        }

        // Filter out bait
        for (String clickBait in ["clic", "clique", "découvrez", "tweet", "inscrit", "offre"]) {
            if (quote.content.toLowerCase().indexOf(clickBait) >= 0) {
                return false
            }
        }

        return true
    }
}
