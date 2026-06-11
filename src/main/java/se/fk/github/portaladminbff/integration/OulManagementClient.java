package se.fk.github.portaladminbff.integration;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import se.fk.github.portaladminbff.model.OulSorteringsordningResponse;
import se.fk.github.portaladminbff.model.OulUppgiftPage;

import java.util.List;

@RegisterRestClient(configKey = "oul-management")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface OulManagementClient
{
   @GET
   @Path("/uppgifter")
   OulUppgiftPage getUppgifter(
         @QueryParam("limit") int limit,
         @QueryParam("offset") int offset);

   @GET
   @Path("/sorteringsordning")
   List<OulSorteringsordningResponse> getSorteringsordningar();
}
