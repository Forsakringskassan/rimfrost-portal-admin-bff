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
   @Path("/admin/sorteringsordning/{id}/default")
   public Response setDefaultSorteringsordning(@PathParam("id") String id)
   {
      LOGGER.debug("PUT /admin/sorteringsordning/{}/default", id);
      try
      {
         oulManagementClient.setDefaultSorteringsordning(id);
         return Response.noContent().build();
      }
      catch (WebApplicationException e)
      {
         return Response.status(e.getResponse().getStatus()).build();
      }
   }

   @GET
   @Path("/admin/sorteringsordning/default")
   public Response getDefaultSorteringsordning()
   {
      LOGGER.debug("GET /admin/sorteringsordning/default");
      try
      {
         return Response.ok(oulManagementClient.getDefaultSorteringsordning()).build();
      }
      catch (WebApplicationException e)
      {
         return Response.status(e.getResponse().getStatus()).build();
      }
   }

}
