package fhacktory.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response

/**
 * @version $Id$
 */
@Path("photo")
class ImageProxy
{

    @GET
    def getImage(@QueryParam("url") String url)
    {
        String decoded = URLDecoder.decode(url, "UTF-8")
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        out << new URL(decoded).openStream()

        return Response.ok(out.toByteArray()).build();
    }

}
