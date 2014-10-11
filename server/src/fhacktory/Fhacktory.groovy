package fhacktory

import com.google.common.eventbus.EventBus
import edu.uci.ics.crawler4j.crawler.CrawlConfig
import edu.uci.ics.crawler4j.crawler.CrawlController
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.Executors

@CompileStatic
class Fhacktory
{
    def static final Logger logger = LoggerFactory.getLogger(Fhacktory.class)

    // Yeah, I know, right
    static EventBus eventBus = new EventBus()

    private Database database = new Database();

    public static void main(String[] args)
    {
        def app = new Fhacktory()
        app.run()
    }

    def run()
    {
        eventBus.register(database)

        logger.info("""\n\n\tFhacktory app started\n""")

        String crawlStorageFolder = "/home/jerome/crawler"
        int numberOfCrawlers = 7

        CrawlConfig config = new CrawlConfig()
        config.setCrawlStorageFolder(crawlStorageFolder)
        config.setUserAgentString(
                "Mozilla/5.0 (X11 Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36")
        config.setPolitenessDelay(500)
        config.setResumableCrawling(true)
        config.setFollowRedirects(true)

        PageFetcher pageFetcher = new PageFetcher(config)
        RobotstxtConfig robotsTxtConfig = new RobotstxtConfig()
        RobotstxtServer robotsTxtServer = new RobotstxtServer(robotsTxtConfig, pageFetcher)

        CrawlController controller = new CrawlController(config, pageFetcher, robotsTxtServer)
        controller.addSeed("http://fr.skyrock.com/blog/top.php")
        controller.startNonBlocking(fhacktory.Crawler.class, numberOfCrawlers)

        Executors.newSingleThreadExecutor().submit(new PostsFetcher(database));

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run()
            {
                logger.info("\n\n\tBye !")
            }
        }, "shutdownHook"))
    }
}
