package se.fk.github.portaladminbff;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@QuarkusTestResource(OulManagementWireMock.class)
class PortalAdminBffControllerTest
{
   @BeforeEach
   void resetStubs()
   {
      OulManagementWireMock.server.resetAll();
   }

   @Test
   void getAllTasks_returnsItemsFromOulManagement()
   {
      OulManagementWireMock.server.stubFor(
            get(urlPathEqualTo("/uppgifter"))
                  .willReturn(okJson("""
                        {
                          "total": 2,
                          "items": [
                            {
                              "uppgift_id": "aaa-001",
                              "handlaggning_id": "h-001",
                              "skapad": "2025-01-10",
                              "status": "Ny",
                              "regel": "RTF_MANUELL",
                              "roll": "Handläggning",
                              "beskrivning": "Test 1",
                              "verksamhetslogik": "VAB",
                              "url": "",
                              "individer": [],
                              "handlaggar_id": null,
                              "planerad_till": null,
                              "utford": null,
                              "erbjudande": {"id": "e1", "namn": "Erbjudande 1"}
                            },
                            {
                              "uppgift_id": "bbb-002",
                              "handlaggning_id": "h-002",
                              "skapad": "2025-01-11",
                              "status": "Tilldelad",
                              "regel": "BEKRAFTA_BESLUT",
                              "roll": "Beslut",
                              "beskrivning": "Test 2",
                              "verksamhetslogik": "VAB",
                              "url": "",
                              "individer": [],
                              "handlaggar_id": {"typ_id": "abc", "varde": "19901010-1234"},
                              "planerad_till": "2025-02-01",
                              "utford": null,
                              "erbjudande": {"id": "e2", "namn": "Erbjudande 2"}
                            }
                          ]
                        }
                        """))
      );

      given()
            .when().get("/admin/tasks")
            .then()
            .statusCode(200)
            .body("operativa_uppgifter", hasSize(2))
            .body("operativa_uppgifter[0].uppgiftId", equalTo("aaa-001"))
            .body("operativa_uppgifter[0].status", equalTo("Ny"))
            .body("operativa_uppgifter[1].uppgiftId", equalTo("bbb-002"))
            .body("operativa_uppgifter[1].handlaggarId.typId", equalTo("abc"))
            .body("operativa_uppgifter[1].handlaggarId.varde", equalTo("19901010-1234"));
   }

   @Test
   void getAllTasks_mapsHandlaggarIdWithSnakeCaseTypId()
   {
      OulManagementWireMock.server.stubFor(
            get(urlPathEqualTo("/uppgifter"))
                  .willReturn(okJson("""
                        {
                          "total": 1,
                          "items": [{
                            "uppgift_id": "ccc-003",
                            "handlaggning_id": "h-003",
                            "skapad": "2025-01-12",
                            "status": "Ny",
                            "regel": "RTF_MANUELL",
                            "roll": "Handläggning",
                            "beskrivning": "Test",
                            "verksamhetslogik": "VAB",
                            "url": "",
                            "individer": [],
                            "handlaggar_id": {"typ_id": "kortnummer", "varde": "12345"},
                            "planerad_till": null,
                            "utford": null,
                            "erbjudande": {"id": "e3", "namn": "Erbjudande 3"}
                          }]
                        }
                        """))
      );

      given()
            .when().get("/admin/tasks")
            .then()
            .statusCode(200)
            .body("operativa_uppgifter[0].handlaggarId.typId", equalTo("kortnummer"))
            .body("operativa_uppgifter[0].handlaggarId.varde", equalTo("12345"));
   }

   @Test
   void getAllTasks_returnsEmptyListWhenOulReturnsNoItems()
   {
      OulManagementWireMock.server.stubFor(
            get(urlPathEqualTo("/uppgifter"))
                  .willReturn(okJson("""
                        {"total": 0, "items": []}
                        """))
      );

      given()
            .when().get("/admin/tasks")
            .then()
            .statusCode(200)
            .body("operativa_uppgifter", empty());
   }

   @Test
   void getSorteringsordningar_returnsListFromOul()
   {
      OulManagementWireMock.server.stubFor(
            get(urlPathEqualTo("/sorteringsordning"))
                  .willReturn(okJson("""
                        [
                          {
                            "id": "f47ac10b-0001-0001-0001-000000000001",
                            "skapad": "2026-06-01T10:00:00Z",
                            "entries": [
                              {
                                "constraints": [
                                  {"field": "status", "operator": "eq", "value": "Ny"}
                                ],
                                "sort_by": {"field": "skapad", "direction": "asc"}
                              }
                            ]
                          }
                        ]
                        """))
      );

      given()
            .when().get("/admin/sorteringsordning")
            .then()
            .statusCode(200)
            .body("$", hasSize(1))
            .body("[0].id", equalTo("f47ac10b-0001-0001-0001-000000000001"))
            .body("[0].entries", hasSize(1))
            .body("[0].entries[0].sort_by.field", equalTo("skapad"))
            .body("[0].entries[0].sort_by.direction", equalTo("asc"))
            .body("[0].entries[0].constraints[0].field", equalTo("status"));
   }

   @Test
   void getSorteringsordningar_returnsEmptyListWhenOulReturnsNone()
   {
      OulManagementWireMock.server.stubFor(
            get(urlPathEqualTo("/sorteringsordning"))
                  .willReturn(okJson("[]"))
      );

      given()
            .when().get("/admin/sorteringsordning")
            .then()
            .statusCode(200)
            .body("$", empty());
   }

   @Test
   void getAllTasks_callsOulWithLimitAndOffset()
   {
      OulManagementWireMock.server.stubFor(
            get(urlPathEqualTo("/uppgifter"))
                  .willReturn(okJson("{\"total\": 0, \"items\": []}"))
      );

      given()
            .when().get("/admin/tasks")
            .then()
            .statusCode(200);

      OulManagementWireMock.server.verify(
            getRequestedFor(urlPathEqualTo("/uppgifter"))
                  .withQueryParam("limit", WireMock.equalTo("500"))
                  .withQueryParam("offset", WireMock.equalTo("0"))
      );
   }
}
