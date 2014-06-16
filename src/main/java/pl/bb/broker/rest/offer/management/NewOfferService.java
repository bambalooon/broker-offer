package pl.bb.broker.rest.offer.management;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;
import org.apache.commons.io.IOUtils;
import org.hibernate.HibernateException;
import pl.bb.broker.brokerdb.broker.entities.CompaniesEntity;
import pl.bb.broker.brokerdb.broker.entities.OfferDetailsEntity;
import pl.bb.broker.brokerdb.broker.entities.OffersEntity;
import pl.bb.broker.brokerdb.broker.xml.XmlCollectionWrapper;
import pl.bb.broker.brokerdb.util.BrokerDBOfferUtil;
import pl.bb.broker.security.settings.SecurityGroups;
import pl.bb.broker.security.settings.SecuritySettings;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BamBalooon
 * Date: 27.05.14
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */

@Path("/management/new")
public class NewOfferService {
    private static final String companyRole = "COMPANY";

    @RolesAllowed(value = NewOfferService.companyRole)
    @POST
    @Path("/site")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createNewOffer(                             //check file!
            @FormDataParam("image")final InputStream imageIS,
            @FormDataParam("image")final FormDataContentDisposition imageDetail,
            @FormDataParam("facility") final String facility,
            @FormDataParam("city") final String city,
            @FormDataParam("description") final String description,
            @FormDataParam("types") final List<FormDataBodyPart> types,
            @FormDataParam("rooms") final List<FormDataBodyPart> rooms,
            @FormDataParam("prices") final List<FormDataBodyPart> prices,
            @Context SecurityContext context //context to get username of logged company!
    ) {
        OffersEntity offer = new OffersEntity();
        offer.setFacility(facility);
        offer.setCity(city);
        offer.setDescription(description);
        String username = context.getUserPrincipal().getName();
        CompaniesEntity company;
        try {
            company = BrokerDBOfferUtil.FACTORY.getCompany(username);
            if(company==null) {
                return Response.status(401).build();
            }
        } catch (HibernateException e) {
            return Response.status(500).entity("No company").build();
        }
        offer.setCompany(company);
        offer.setPosted(new java.sql.Date(new Date().getTime()));
        byte[] imgBytes;
        try {
            imgBytes = IOUtils.toByteArray(imageIS);
        } catch (IOException e) {
            return Response.status(500).entity("Error transering to byte[]").build();
        }
        offer.setImage(imgBytes);
        List<OfferDetailsEntity> details = new ArrayList<>();
        Iterator<FormDataBodyPart> pricesIt = prices.iterator();
        Iterator<FormDataBodyPart> typesIt = types.iterator();
        for(FormDataBodyPart room : rooms) {
            OfferDetailsEntity detail = new OfferDetailsEntity();
            detail.setOffer(offer);
            detail.setRoom(room.getValueAs(String.class));
            if(pricesIt.hasNext()) {
                FormDataBodyPart price = pricesIt.next();
                try {
                    detail.setPrice(BigDecimal.valueOf(Double.parseDouble(price.getValueAs(String.class))));
                } catch (Exception e) {
                    return Response.status(400).entity("Wrong price: "+price.getValueAs(String.class)).build();
                }
            }
            if(typesIt.hasNext()) {
                FormDataBodyPart type = typesIt.next();
                detail.setRoomType(type.getValueAs(String.class));
            }
            details.add(detail);
        }
        offer.setDetails(details);
        try {
            BrokerDBOfferUtil.FACTORY.saveOffer(offer);
        } catch (HibernateException e) {
            return Response.status(500).entity("Hibernate error: "+e.getMessage()).build();
        }
        return Response.status(201).entity("Added!").build();
    }

    @RolesAllowed(value = NewOfferService.companyRole)
    @POST
    @Path("/app")
    @Consumes(MediaType.APPLICATION_XML)
    public Response createNewOffers(XmlCollectionWrapper<OffersEntity> xmlOffers,
                                    @Context SecurityContext context) {
        String username = context.getUserPrincipal().getName();
        CompaniesEntity company;
        try {
            company = BrokerDBOfferUtil.FACTORY.getCompany(username);
            if(company==null) {
                return Response.status(401).entity("No company").build();
            }
        } catch (HibernateException e) {
            return Response.status(500).entity("Company check - Hibernate Exc").build();
        }
        String response = "";
        for(OffersEntity offer : xmlOffers.getItems()) {
            offer.setCompany(company);
            for(OfferDetailsEntity detail: offer.getDetails()) {
                detail.setOffer(offer);
            }
            try {
                BrokerDBOfferUtil.FACTORY.saveOffer(offer);
            } catch (HibernateException e) {
                response += "Hibernate error: "+e.getMessage()+"\n";
            }
        }
        if(response.equals("")) {
            return Response.status(201).entity("Added!").build();
        }
        return Response.status(500).entity(response).build();
    }

}
