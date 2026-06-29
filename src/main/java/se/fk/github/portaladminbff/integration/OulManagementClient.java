package se.fk.github.portaladminbff.integration;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import se.fk.github.portaladminbff.model.OulSorteringsordningPage;
import se.fk.github.portaladminbff.model.OulSorteringsordningResponse;
import se.fk.github.portaladminbff.model.OulSorteringsordningSpec;
import se.fk.github.portaladminbff.model.OulUpdateUppgiftRequest;
import se.fk.github.portaladminbff.model.OulUppgiftPage;
import se.fk.github.portaladminbff.model.RawOperativUppgift;

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
   OulSorteringsordningPage getSorteringsordningar(
         @QueryParam("limit") int limit,
         @QueryParam("offset") int offset);

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

   @PUT
   @Path("/sorteringsordning/{id}")
   OulSorteringsordningResponse updateSorteringsordning(
         @PathParam("id") String id,
         OulSorteringsordningSpec spec);

   @DELETE
   @Path("/sorteringsordning/{id}")
   void deleteSorteringsordning(@PathParam("id") String id);

   @POST
   @Path("/uppgifter/{id}/unassign")
   RawOperativUppgift unassignUppgift(@PathParam("id") String id);

   @POST
   @Path("/sorteringsordning/preview")
   OulUppgiftPage previewSorteringsordning(
         @QueryParam("limit") int limit,
         @QueryParam("offset") int offset,
         OulSorteringsordningSpec spec);
}
