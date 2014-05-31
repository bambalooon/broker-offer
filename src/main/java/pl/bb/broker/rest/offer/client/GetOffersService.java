package pl.bb.broker.rest.offer.client;

import org.hibernate.HibernateException;
import pl.bb.broker.brokerdb.broker.entities.OfferDetailsEntity;
import pl.bb.broker.brokerdb.broker.entities.OffersEntity;
import pl.bb.broker.brokerdb.broker.xml.XmlCollectionWrapper;
import pl.bb.broker.brokerdb.util.BrokerDBOfferUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BamBalooon
 * Date: 29.05.14
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */

@Path("/client")
public class GetOffersService {

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_XML)
    public Response getOffers() {
        List<OffersEntity> offers = null;
        try {
            offers = BrokerDBOfferUtil.FACTORY.getOffers();
        } catch (HibernateException e) {
            return Response.status(500).entity(e).build();
        }
        XmlCollectionWrapper<OffersEntity> xmlOffers = new XmlCollectionWrapper<>();
        xmlOffers.setItems(offers);
        return Response.ok(xmlOffers).build();
    }

    @GET
    @Path("/get/{id}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getOffer(@PathParam("id") int id) {
        try {
            OffersEntity offer = BrokerDBOfferUtil.FACTORY.getOffer(id);
            return Response.ok(offer).build();
        } catch(HibernateException e) {
            return Response.status(500).entity(e).build();
        }
    }
}
