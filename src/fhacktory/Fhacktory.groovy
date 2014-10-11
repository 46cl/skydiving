package fhacktory

import edu.uci.ics.crawler4j.crawler.CrawlConfig
import edu.uci.ics.crawler4j.crawler.CrawlController
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class Fhacktory
{
    def static final Logger logger = LoggerFactory.getLogger(Fhacktory.class)

    public static void main(String[] args)
    {
        def app = new Fhacktory()
        app.run()
    }

    def run()
    {
        logger.info("""\n\n\tFhacktory app started\n""")

        String crawlStorageFolder = "/home/jerome/crawler"
        int numberOfCrawlers = 7

        CrawlConfig config = new CrawlConfig()
        config.setCrawlStorageFolder(crawlStorageFolder)
        config.setUserAgentString(
                "Mozilla/5.0 (X11 Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36")
        config.setPolitenessDelay(500)
        //config.setMaxDepthOfCrawling(-1)
        config.setResumableCrawling(true)

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config)
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig()
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher)

        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer)
        controller.addSeed("http://fr.skyrock.com/blog/top.php")
        //controller.addSeed("http://tumblr.com/tagged/quote")
        //controller.addSeed("http://www.tumblr.com/tagged/random")
        controller.start(fhacktory.Crawler.class, numberOfCrawlers)

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run()
            {
                logger.info("\n\n\tBye !")
            }
        }, "shutdownHook"))
    }
}
