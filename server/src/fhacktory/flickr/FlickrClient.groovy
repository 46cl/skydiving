package fhacktory.flickr

import fhacktory.data.Quote
import fhacktory.event.NewQuoteEvent
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.RESTClient
import org.jsoup.Jsoup

/**
 * @version $Id$
 */
class FlickrClient
{
    def client

    def init()
    {
        client = new HTTPBuilder('https://api.flickr.com/services/rest/')
    }

    def findAPhoto(List<String> keywords)
    {
        client.get(path: '', contentType: ContentType.TEXT, query: [
                method: 'flickr.photos.search',
                api_key: '05685c48c998a2f691691b6cd59f284f',
                text: keywords.get(0),
                //content_type: 1,
                //sort: "relevance"
        ]) { resp, reader ->
            def xml = new XmlSlurper().parseText(reader.text)
            def id = xml.photos.photo[0].@id.text()

            if (!id) {
                return null
            }

            def secret = xml.photos.photo[0].@secret.text()
            def server = xml.photos.photo[0].@server.text()
            def farm = xml.photos.photo[0].@farm.text()
            def url = "https://farm${farm}.staticflickr.com/${server}/${id}_${secret}.jpg"

            return url
        }
    }
}
