package fhacktory

import com.google.common.eventbus.EventBus
import edu.uci.ics.crawler4j.crawler.CrawlConfig
import edu.uci.ics.crawler4j.crawler.CrawlController
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer
import fhacktory.api.Api
import fhacktory.api.CorsFilter
import fhacktory.api.ImageProxy
import fhacktory.api.Log
import fhacktory.crawling.Crawler
import fhacktory.crawling.PostsFetcher
import fhacktory.db.Database
import fhacktory.flickr.FlickrClient
import fhacktory.nlp.NounsExtractor
import fhacktory.stream.Stream
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.media.sse.SseFeature
import org.glassfish.jersey.server.ResourceConfig

import javax.ws.rs.core.UriBuilder
import java.util.concurrent.Executors

@CompileStatic
class Fhacktory
{
    def static final Logger logger = LoggerFactory.getLogger(Fhacktory.class)

    // Yeah, I know, right
    static EventBus eventBus = new EventBus()

    public static void main(String[] args)
    {
        String folder = args.length > 0 ? args[0] : "/home/jerome/crawler"
        def app = new Fhacktory()
        app.run(folder)
    }

    def run(String folder)
    {
        Database database = new Database();
        FlickrClient flickrClient = new FlickrClient()
        flickrClient.init()
        NounsExtractor nounsExtractor = new NounsExtractor()
        nounsExtractor.init()
        Stream stream = new Stream(eventBus, flickrClient, nounsExtractor)
        Api api = new Api()
        Log log = new Log()
        PostsFetcher postsFetcher = new PostsFetcher(eventBus, database)

        eventBus.register(database)
        eventBus.register(api)
        eventBus.register(stream)
        eventBus.register(log)

        HttpServer server = createHttpServer(api, log, new SseFeature(), new CorsFilter(), new ImageProxy())

        // Start crawler
        startCrawler(folder)

        // Start post fetcher
        Executors.newSingleThreadExecutor().submit(postsFetcher);

        // Start stream
        Executors.newSingleThreadExecutor().submit(stream);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run()
            {
                logger.info("\n\n\tBye !")
            }
        }, "shutdownHook"))

        // run
        try {
            server.start();
            logger.info("""\n\n\tFhacktory app started\n""")
            logger.info("CTRL^C to exit..");
        } catch (Exception e) {
            logger.error("There was an error while starting the server.", e);
        }
    }

    void startCrawler(String folder)
    {
        String crawlStorageFolder = folder
        int numberOfCrawlers = 7

        CrawlConfig config = new CrawlConfig()
        config.setCrawlStorageFolder(crawlStorageFolder)
        config.setUserAgentString(
                "Mozilla/5.0 (X11 Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36")
        config.setPolitenessDelay(250)
        config.setResumableCrawling(true)
        config.setFollowRedirects(true)

        PageFetcher pageFetcher = new PageFetcher(config)
        RobotstxtConfig robotsTxtConfig = new RobotstxtConfig()
        RobotstxtServer robotsTxtServer = new RobotstxtServer(robotsTxtConfig, pageFetcher)

        CrawlController controller = new CrawlController(config, pageFetcher, robotsTxtServer)
        controller.addSeed("http://fr.skyrock.com/blog/top.php")
        controller.addSeed("http://redgirl51.bestmonde.com/bestway/show_bestway.asp?page=le-guide-de-skyblog-et-des-meilleurs-skyblogs&i_id=7133&s=")
        controller.addSeed("http://clashrap.forumactif.com/t15373-les-meilleurs-skyblog")
        controller.addSeed("http://lepiredeskyblog.wordpress.com/")
        controller.startNonBlocking(Crawler.class, numberOfCrawlers)
    }

    HttpServer createHttpServer(Object... instances)
    {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8144).build();
        ResourceConfig config = new ResourceConfig();
        config.registerInstances(instances);
        GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
    }
}
