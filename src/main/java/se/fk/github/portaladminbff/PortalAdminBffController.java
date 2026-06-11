package se.fk.github.portaladminbff;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
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

   @ConfigProperty(name = "portal.admin.mock.uppgifter", defaultValue = "true")
   boolean mockUppgifter;

   @ConfigProperty(name = "portal.admin.tasks.limit", defaultValue = "500")
   int tasksLimit;

   /**
    * GET /admin/tasks
    *
    * Returns all operativa uppgifter for the admin view.
    *
    * When mockUppgifter=true (default for local dev): returns static sample data.
    * When mockUppgifter=false: fetches from OUL management via GET /uppgifter.
    */
   @GET
   @Path("/admin/tasks")
   public Response getAllTasks()
   {
      LOGGER.debug("GET /admin/tasks mock={}", mockUppgifter);

      if (mockUppgifter)
      {
         return Response.ok(Map.of("operativa_uppgifter", buildMockUppgifter())).build();
      }

      OulUppgiftPage page = oulManagementClient.getUppgifter(tasksLimit, 0);
      List<OperativUppgift> uppgifter = page.items.stream()
            .map(UppgiftMapper::transform)
            .collect(Collectors.toList());

      return Response.ok(Map.of("operativa_uppgifter", uppgifter)).build();
   }

   @GET
   @Path("/admin/sorteringsordning")
   public Response getSorteringsordningar()
   {
      LOGGER.debug("GET /admin/sorteringsordning");
      return Response.ok(oulManagementClient.getSorteringsordningar()).build();
   }

   private List<OperativUppgift> buildMockUppgifter()
   {
      return List.of(
            mockUppgift(
                  "a1b2c3d4-0001-0001-0001-000000000001",
                  "h1b2c3d4-0001-0001-0001-000000000001",
                  "Ny",
                  "RTF_MANUELL",
                  "Handläggning",
                  "2025-01-10T08:00:00Z",
                  "116759e4-18fd-4209-849c-90abbd257d22",
                  "469ddd20-6796-4e05-9e18-6a95953f6cb3"
            ),
            mockUppgift(
                  "a1b2c3d4-0002-0002-0002-000000000002",
                  "h1b2c3d4-0002-0002-0002-000000000002",
                  "Tilldelad",
                  "BEKRAFTA_BESLUT",
                  "Handläggning",
                  "2025-01-11T09:15:00Z",
                  "550e8400-e29b-41d4-a716-446655440001",
                  "19850601-5678"
            ),
            mockUppgift(
                  "a1b2c3d4-0003-0003-0003-000000000003",
                  "h1b2c3d4-0003-0003-0003-000000000003",
                  "Ny",
                  "RTF_MASKINELL",
                  "Handläggning",
                  "2025-01-12T10:30:00Z",
                  null,
                  null
            ),
            mockUppgift(
                  "a1b2c3d4-0004-0004-0004-000000000004",
                  "h1b2c3d4-0004-0004-0004-000000000004",
                  "Avslutad",
                  "BEKRAFTA_BESLUT",
                  "Beslut",
                  "2025-01-08T11:00:00Z",
                  "550e8400-e29b-41d4-a716-446655440002",
                  "19721115-9011"
            ),
            mockUppgift(
                  "a1b2c3d4-0005-0005-0005-000000000005",
                  "h1b2c3d4-0005-0005-0005-000000000005",
                  "Avbruten",
                  "RTF_MANUELL",
                  "Handläggning",
                  "2025-01-09T14:45:00Z",
                  "116759e4-18fd-4209-849c-90abbd257d22",
                  "469ddd20-6796-4e05-9e18-6a95953f6cb3"
            )
      );
   }

   private OperativUppgift mockUppgift(
         String uppgiftId,
         String handlaggningId,
         String status,
         String regel,
         String roll,
         String skapad,
         String handlaggarTypId,
         String handlaggarVarde)
   {
      OperativUppgift u = new OperativUppgift();
      u.uppgiftId = uppgiftId;
      u.handlaggningId = handlaggningId;
      u.status = status;
      u.regel = regel;
      u.roll = roll;
      u.skapad = skapad;
      u.planeradTill = "";
      u.utford = "";
      u.beskrivning = regel + " — mockbeskrivning";
      u.verksamhetslogik = "VAB";
      u.url = "";
      u.individer = List.of();

      if (handlaggarTypId != null)
      {
         HandlaggarId hid = new HandlaggarId();
         hid.typId = handlaggarTypId;
         hid.varde = handlaggarVarde;
         u.handlaggarId = hid;
      }

      return u;
   }
}
