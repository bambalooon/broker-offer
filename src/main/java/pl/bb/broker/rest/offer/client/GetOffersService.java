package pl.bb.broker.rest.offer.client;

import org.hibernate.HibernateException;
import pl.bb.broker.brokerdb.broker.entities.OfferDetailsEntity;
import pl.bb.broker.brokerdb.broker.entities.OffersEntity;
import pl.bb.broker.brokerdb.broker.xml.XmlCollectionWrapper;
import pl.bb.broker.brokerdb.broker.xml.XmlList;
import pl.bb.broker.brokerdb.util.BrokerDBOfferUtil;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BamBalooon
 * Date: 29.05.14
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */

@PermitAll
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
        OffersEntity offer=null;
        try {
            offer = BrokerDBOfferUtil.FACTORY.getOffer(id);
        } catch(HibernateException e) {
            return Response.status(500).entity(e).build();
        }
        if(offer==null) {
            return Response.status(204).build();
        }
        return Response.ok(offer).build();
    }

    @GET
    @Path("/get/company/{username}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getCompanyOffers(@PathParam("username") String username) {
        List<OffersEntity> offers = null;
        try {
            offers = BrokerDBOfferUtil.FACTORY.getCompanyOffers(username);
        } catch(HibernateException e) {
            return Response.status(500).entity(e).build();
        }
        if(offers==null) {
            return Response.status(204).build();
        }
        XmlCollectionWrapper<OffersEntity> xmlOffers = new XmlCollectionWrapper<>();
        xmlOffers.setItems(offers);
        return Response.ok(xmlOffers).build();
    }

    @GET
    @Path("/get/city/{city}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getOffersFromCity(@PathParam("city") String city) {
        List<OffersEntity> offers = null;
        try {
            offers = BrokerDBOfferUtil.FACTORY.getOffers(city);
        } catch(HibernateException e) {
            return Response.status(500).entity(e).build();
        }
        if(offers==null) {
            return Response.status(204).build();
        }
        XmlCollectionWrapper<OffersEntity> xmlOffers = new XmlCollectionWrapper<>();
        xmlOffers.setItems(offers);
        return Response.ok(xmlOffers).build();
    }

    @GET
    @Path("/get/cities")
    @Produces(MediaType.APPLICATION_XML)
    public Response getCities() throws Exception {
        List<String> cities = null;
        try {
            cities = BrokerDBOfferUtil.FACTORY.getCities();
        } catch(HibernateException e) {
            return Response.status(500).entity(e).build();
        }
        if(cities==null) {
            return Response.status(204).build();
        }
        final XmlList<String> list = new XmlList<>(cities);
        return Response.ok(list).build();
    }
}
