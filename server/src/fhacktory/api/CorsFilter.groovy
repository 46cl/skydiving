package fhacktory.api

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter

/**
 * @version $Id$
 */
class CorsFilter implements ContainerResponseFilter
{
    @Override
    void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
    {
        responseContext.headers.add('Access-Control-Allow-Origin', '*')
    }
}
