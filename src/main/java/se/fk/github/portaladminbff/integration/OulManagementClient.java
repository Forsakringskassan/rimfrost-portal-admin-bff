package se.fk.github.portaladminbff.integration;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import se.fk.github.portaladminbff.model.OulSorteringsordningResponse;
import se.fk.github.portaladminbff.model.OulSorteringsordningSpec;
import se.fk.github.portaladminbff.model.OulUpdateUppgiftRequest;
import se.fk.github.portaladminbff.model.OulUppgiftPage;
import se.fk.github.portaladminbff.model.RawOperativUppgift;

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

   @POST
   @Path("/sorteringsordning")
   OulSorteringsordningResponse createSorteringsordning(OulSorteringsordningSpec spec);

   @GET
   @Path("/sorteringsordning/default")
   OulSorteringsordningResponse getDefaultSorteringsordning();

   @GET
   @Path("/sorteringsordning/{id}")
   OulSorteringsordningResponse getSorteringsordning(@PathParam("id") String id);

   @PATCH
   @Path("/uppgifter/{id}")
   RawOperativUppgift patchUppgift(
         @PathParam("id") String id,
         OulUpdateUppgiftRequest request);

   @PUT
   @Path("/sorteringsordning/{id}/default")
   void setDefaultSorteringsordning(@PathParam("id") String id);
}
