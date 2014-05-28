package pl.bb.broker.rest.offer.management;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;
import org.apache.commons.io.IOUtils;
import org.hibernate.HibernateException;
import pl.bb.broker.brokerdb.broker.entities.CompaniesEntity;
import pl.bb.broker.brokerdb.broker.entities.OfferDetailsEntity;
import pl.bb.broker.brokerdb.broker.entities.OffersEntity;
import pl.bb.broker.brokerdb.util.BrokerDBUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    @POST
    @Path("/site")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createNewOffer(                             //check file!
            @FormDataParam("image")final InputStream imageIS,
            @FormDataParam("image")final FormDataContentDisposition imageDetail,
            @FormDataParam("description") String description,
            @FormDataParam("rooms") List<FormDataBodyPart> rooms,
            @FormDataParam("prices") List<FormDataBodyPart> prices,
            @Context SecurityContext context //context to get username of logged company!
    ) {
        OffersEntity offer = new OffersEntity();
        offer.setDescription(description);
        String username = context.getUserPrincipal().getName();
        CompaniesEntity company;
        try {
            company = BrokerDBUtil.INSTANCE.getCompany(username);
            if(company==null) {
                return Response.status(401).build();
            }
        } catch (HibernateException e) {
            return Response.status(500).entity("No company").build();
        }
        offer.setCompany(company);
        byte[] imgBytes;
        try {
            imgBytes = IOUtils.toByteArray(imageIS);
        } catch (IOException e) {
            return Response.status(500).entity("Error transering to byte[]").build();
        }
        offer.setImage(imgBytes);
        List<OfferDetailsEntity> details = new ArrayList<>();
        Iterator<FormDataBodyPart> pricesIt = prices.iterator();
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
            details.add(detail);
        }
        offer.setDetails(details);
        try {
            BrokerDBUtil.INSTANCE.saveOffer(offer);
        } catch (HibernateException e) {
            return Response.status(500).entity("Hibernate error: "+e.getMessage()).build();
        }
        return Response.status(201).entity("Added!").build();
    }

}
