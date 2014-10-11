package fhacktory

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import fhacktory.data.Quote
import fhacktory.event.NewQuoteEvent
import fhacktory.event.PublishQuoteEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @version $Id$
 */
class Stream implements Runnable
{
    EventBus eventBus

    Queue<Quote> quotes = new LinkedList<>()

    Logger logger = LoggerFactory.getLogger(Stream.class)

    Stream(EventBus eventBus)
    {
        this.eventBus = eventBus
    }

    @Override
    void run()
    {
        while (true) {
            try {
                if (!quotes.isEmpty()) {
                    Quote quote = quotes.remove()
                    logger.info("Posting quote {}", quote.content)
                    eventBus.post(new PublishQuoteEvent([quote: quote]))
                }
            } catch (Exception e) {
                e.printStackTrace()
            }
            Thread.sleep(1000)
        }
    }

    @Subscribe
    public void onNewQuote(NewQuoteEvent event)
    {
        //logger.debug("Adding new quote {}", event.quote.content)

        // Filter quotes

        Quote quote = event.quote

        // Filter out quotes too long
        if (quote.content.endsWith("...")) {
            return
        }

        // Filter out quotes with links
        if (quote.content.indexOf("http") >= 0) {
            return
        }

        // Filter out bait
        for (String clickBait in ["clic", "clique", "découvrez", "tweet", "inscrit", "kiffe"]) {
            if (quote.content.toLowerCase().indexOf(clickBait) >= 0) {
                return
            }
        }

        quotes.add(event.quote)
    }
}