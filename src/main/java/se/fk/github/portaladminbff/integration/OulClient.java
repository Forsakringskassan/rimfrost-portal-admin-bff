package se.fk.github.portaladminbff.integration;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import se.fk.github.portaladminbff.model.RawTaskBackendResponse;

/**
 * REST client for the OUL service.
 *
 * NOTE: OUL currently only exposes per-handler endpoints. A dedicated admin endpoint
 * (e.g. GET /uppgifter/admin/alla) does not yet exist. Wire it up here when added.
 */
@RegisterRestClient(configKey = "oul")
@Path("/uppgifter")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface OulClient
{
   @GET
   @Path("/handlaggare/{typId}/{varde}")
   RawTaskBackendResponse getTasksForHandlaggare(
         @PathParam("typId") String typId,
         @PathParam("varde") String varde);
}
