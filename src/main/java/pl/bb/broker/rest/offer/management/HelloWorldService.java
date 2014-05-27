package pl.bb.broker.rest.offer.management;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA.
 * User: BamBalooon
 * Date: 26.05.14
 * Time: 21:41
 * To change this template use File | Settings | File Templates.
 */

@Path("/management/hello")
public class HelloWorldService {

    @GET
    @Path("/{param}")
    public Response getMessage(@PathParam("param") String param) {
        String output = "Jersey say: "+param;
        return Response.status(200).entity(output).build();
    }
}
