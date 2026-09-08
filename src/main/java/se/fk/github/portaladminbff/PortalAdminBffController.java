package se.fk.github.portaladminbff;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fk.github.portaladminbff.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PortalAdminBffController
{

   private static final Logger LOGGER = LoggerFactory.getLogger(PortalAdminBffController.class);

   @Inject
   @RestClient
   se.fk.github.portaladminbff.integration.OulManagementClient oulManagementClient;

   @ConfigProperty(name = "portal.admin.tasks.limit", defaultValue = "500")
   int tasksLimit;

   @GET
   @Path("/admin/handlaggare")
   public Response getHandlaggare()
   {
      LOGGER.debug("GET /admin/handlaggare");

      // Hårdkodad testdata, samma identiteter som rimfrost-portal-bff:s motsvarande mock-lista
      // (PBFF-FR-02.3) -- typId matchar rimfrost-service-team/rimfrost-service-sid:s kända
      // testidentitet, så SID-behörighetskontroller ger meningsfulla utfall för dessa tre.
      String kandTypId = "116759e4-18fd-4209-849c-90abbd257d22";

      HandlaggarId id1 = new HandlaggarId();
      id1.typId = kandTypId;
      id1.varde = "111111111";

      HandlaggarId id2 = new HandlaggarId();
      id2.typId = kandTypId;
      id2.varde = "222222222";

      HandlaggarId id3 = new HandlaggarId();
      id3.typId = kandTypId;
      id3.varde = "333333333";

      Handlaggare h1 = new Handlaggare();
      h1.handlaggarId = id1;
      h1.fornamn = "Lisa";
      h1.efternamn = "Tass";

      Handlaggare h2 = new Handlaggare();
      h2.handlaggarId = id2;
      h2.fornamn = "Karl";
      h2.efternamn = "von Dobermann";

      Handlaggare h3 = new Handlaggare();
      h3.handlaggarId = id3;
      h3.fornamn = "Åsa";
      h3.efternamn = "Ormsäter";

      return Response.ok(Map.of("handlaggare", List.of(h1, h2, h3))).build();
   }

   @GET
   @Path("/admin/tasks")
   public Response getAllTasks()
   {
      LOGGER.debug("GET /admin/tasks");
      try
      {
         OulUppgiftPage page = oulManagementClient.getUppgifter(tasksLimit, 0);
         List<OperativUppgift> uppgifter = page.items.stream()
               .map(UppgiftMapper::transform)
               .collect(Collectors.toList());
         return Response.ok(Map.of("total", page.total, "operativa_uppgifter", uppgifter)).build();
      }
      catch (WebApplicationException e)
      {
         LOGGER.error("OUL returned error status={} fetching tasks", e.getResponse().getStatus(), e);
         return Response.status(e.getResponse().getStatus())
               .entity(Map.of("error", "Upstream error fetching tasks")).build();
      }
      catch (ProcessingException e)
      {
         LOGGER.error("OUL unreachable when fetching tasks", e);
         return Response.status(502)
               .entity(Map.of("error", "OUL unavailable")).build();
      }
      catch (Exception e)
      {
         LOGGER.error("Unexpected error on GET /admin/tasks", e);
         return Response.status(500)
               .entity(Map.of("error", "Internal server error")).build();
      }
   }

   @GET
   @Path("/admin/sorteringsordning")
   public Response getSorteringsordningar(
         @QueryParam("limit") @DefaultValue("100") int limit,
         @QueryParam("offset") @DefaultValue("0") int offset)
   {
      LOGGER.debug("GET /admin/sorteringsordning limit={} offset={}", limit, offset);
      return Response.ok(oulManagementClient.getSorteringsordningar(limit, offset)).build();
   }

   @POST
   @Path("/admin/tasks/{uppgiftId}/unassign")
   @Consumes(MediaType.WILDCARD)
   public Response unassignUppgift(@PathParam("uppgiftId") String uppgiftId)
   {
      LOGGER.debug("POST /admin/tasks/{}/unassign", uppgiftId);
      try
      {
         RawOperativUppgift raw = oulManagementClient.unassignUppgift(uppgiftId);
         return Response.ok(UppgiftMapper.transform(raw)).build();
      }
      catch (WebApplicationException e)
      {
         return Response.status(e.getResponse().getStatus()).build();
      }
   }

   @PATCH
   @Path("/admin/tasks/{uppgiftId}")
   public Response updateUppgift(@PathParam("uppgiftId") String uppgiftId, UpdateUppgiftRequest request)
   {
      LOGGER.debug("PATCH /admin/tasks/{}", uppgiftId);
      try
      {
         RawOperativUppgift raw = oulManagementClient.patchUppgift(uppgiftId, UppgiftMapper.toOulRequest(request));
         return Response.ok(UppgiftMapper.transform(raw)).build();
      }
      catch (WebApplicationException e)
      {
         return Response.status(e.getResponse().getStatus()).build();
      }
   }

   @POST
   @Path("/admin/sorteringsordning")
   public Response createSorteringsordning(OulSorteringsordningSpec spec)
   {
      LOGGER.debug("POST /admin/sorteringsordning");
      try
      {
         return Response.status(Response.Status.CREATED)
               .entity(oulManagementClient.createSorteringsordning(spec))
               .build();
      }
      catch (WebApplicationException e)
      {
         return Response.status(e.getResponse().getStatus()).build();
      }
   }

   @GET
   @Path("/admin/sorteringsordning/{id}")
   public Response getSorteringsordning(@PathParam("id") String id)
   {
      LOGGER.debug("GET /admin/sorteringsordning/{}", id);
      try
      {
         return Response.ok(oulManagementClient.getSorteringsordning(id)).build();
      }
      catch (WebApplicationException e)
      {
         return Response.status(e.getResponse().getStatus()).build();
      }
   }

   @POST
   @Path("/admin/sorteringsordning/preview")
   public Response previewSorteringsordning(
         @QueryParam("limit") int limit,
         @QueryParam("offset") @DefaultValue("0") int offset,
         OulSorteringsordningSpec spec)
   {
      LOGGER.debug("POST /admin/sorteringsordning/preview limit={} offset={}", limit, offset);
      try
      {
         OulUppgiftPage page = oulManagementClient.previewSorteringsordning(limit, offset, spec);
         List<OperativUppgift> uppgifter = page.items.stream()
               .map(UppgiftMapper::transform)
               .collect(Collectors.toList());
         return Response.ok(Map.of("total", page.total, "operativa_uppgifter", uppgifter)).build();
      }
      catch (WebApplicationException e)
      {
         return Response.status(e.getResponse().getStatus()).build();
      }
   }

   @DELETE
   @Path("/admin/sorteringsordning/{id}")
   public Response deleteSorteringsordning(@PathParam("id") String id)
   {
      LOGGER.debug("DELETE /admin/sorteringsordning/{}", id);
      try
      {
         oulManagementClient.deleteSorteringsordning(id);
         return Response.noContent().build();
      }
      catch (WebApplicationException e)
      {
         return Response.status(e.getResponse().getStatus()).build();
      }
   }

   @PUT
   @Path("/admin/sorteringsordning/{id}")
   public Response updateSorteringsordning(@PathParam("id") String id, OulSorteringsordningSpec spec)
   {
      LOGGER.debug("PUT /admin/sorteringsordning/{}", id);
      try
      {
         return Response.ok(oulManagementClient.updateSorteringsordning(id, spec)).build();
      }
      catch (WebApplicationException e)
      {
         return Response.status(e.getResponse().getStatus()).build();
      }
   }

   @PUT
   @Path("/admin/sorteringsordning/{id}/aktiv")
   public Response setAktivSorteringsordning(@PathParam("id") String id)
   {
      LOGGER.debug("PUT /admin/sorteringsordning/{}/aktiv", id);
      try
      {
         oulManagementClient.setAktivSorteringsordning(id);
         return Response.noContent().build();
      }
      catch (WebApplicationException e)
      {
         return Response.status(e.getResponse().getStatus()).build();
      }
   }

   @GET
   @Path("/admin/sorteringsordning/aktiv")
   public Response getAktivSorteringsordning()
   {
      LOGGER.debug("GET /admin/sorteringsordning/aktiv");
      try
      {
         return Response.ok(oulManagementClient.getAktivSorteringsordning()).build();
      }
      catch (WebApplicationException e)
      {
         return Response.status(e.getResponse().getStatus()).build();
      }
   }

}
