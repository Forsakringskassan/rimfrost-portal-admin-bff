package se.fk.github.portaladminbff.integration;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import se.fk.github.portaladminbff.model.MgmtUppgiftPage;

@RegisterRestClient(configKey = "oul-management")
@Path("/uppgifter")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface OulManagementClient
{
   @GET
   MgmtUppgiftPage getUppgifter(
         @QueryParam("limit") int limit,
         @QueryParam("offset") int offset);
}
