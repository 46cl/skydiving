package fhacktory.api

import com.google.common.eventbus.Subscribe
import fhacktory.data.Quote
import fhacktory.event.PublishQuoteEvent
import groovy.transform.CompileStatic
import org.glassfish.jersey.media.sse.EventOutput
import org.glassfish.jersey.media.sse.OutboundEvent
import org.glassfish.jersey.media.sse.SseBroadcaster
import org.glassfish.jersey.server.ChunkedOutput
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import java.util.concurrent.atomic.AtomicLong

/**
 * @version $Id$
 */
@Path("/quotes")
@CompileStatic
class Api
{
    SseBroadcaster broadcaster = new SseBroadcaster() {
        @Override
        public void onException(ChunkedOutput<OutboundEvent> chunkedOutput, Exception exception)
        {
            logger.error("Error broadcasting message.", exception);
        }
    };

    AtomicLong nextMessageId = new AtomicLong(0);

    Logger logger = LoggerFactory.getLogger(Api.class);

    /**
     * When receiving a new data event, broadcasts it to the SSE open connections.
     *
     * @param dataEvent the data event received
     */
    @Subscribe
    public void onPublishQuoteEvent(PublishQuoteEvent event)
    {
        logger.info("--> Broadcasting quote {}", event.quote.content);
        Quote quote = event.quote
        OutboundEvent sseEvent = new OutboundEvent.Builder()
                .id(String.valueOf(nextMessageId.getAndIncrement()))
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(quote)
                .build();

        broadcaster.broadcast(sseEvent);
    }

    /**
     * @return new SSE message stream channel.
     */
    @GET
    @Produces("text/event-stream")
    public EventOutput getMessageStream()
    {
        logger.info("--> SSE connection received.");
        final EventOutput eventOutput = new EventOutput();
        broadcaster.add(eventOutput);
        return eventOutput;
    }
}
