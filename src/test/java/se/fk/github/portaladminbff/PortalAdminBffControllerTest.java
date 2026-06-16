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
   void updateUppgift_returnsMappedUppgiftOnSuccess()
   {
      OulManagementWireMock.server.stubFor(
            patch(urlPathEqualTo("/uppgifter/test-001"))
                  .willReturn(okJson("""
                        {
                          "uppgift_id": "test-001",
                          "handlaggning_id": "h-001",
                          "skapad": "2025-01-10",
                          "status": "Tilldelad",
                          "regel": "RTF_MANUELL",
                          "roll": "Handläggning",
                          "beskrivning": "Test",
                          "verksamhetslogik": "VAB",
                          "url": "",
                          "individer": [],
                          "handlaggar_id": {"typ_id": "kortnummer", "varde": "12345"},
                          "planerad_till": null,
                          "utford": null,
                          "erbjudande": {"id": "e1", "namn": "Erbjudande 1"}
                        }
                        """))
      );

      given()
            .contentType("application/json")
            .body("{\"handlaggarId\": {\"typId\": \"kortnummer\", \"varde\": \"12345\"}}")
            .when().patch("/admin/tasks/test-001")
            .then()
            .statusCode(200)
            .body("uppgiftId", equalTo("test-001"))
            .body("status", equalTo("Tilldelad"))
            .body("handlaggarId.typId", equalTo("kortnummer"))
            .body("handlaggarId.varde", equalTo("12345"));
   }

   @Test
   void updateUppgift_forwardsHandlaggarIdAsSnakeCase()
   {
      OulManagementWireMock.server.stubFor(
            patch(urlPathEqualTo("/uppgifter/test-002"))
                  .withRequestBody(matchingJsonPath("$.handlaggar_id.typ_id", WireMock.equalTo("kortnummer")))
                  .withRequestBody(matchingJsonPath("$.handlaggar_id.varde", WireMock.equalTo("99999")))
                  .willReturn(okJson("""
                        {
                          "uppgift_id": "test-002",
                          "handlaggning_id": "h-002",
                          "skapad": "2025-01-11",
                          "status": "Tilldelad",
                          "regel": "RTF_MANUELL",
                          "roll": "Handläggning",
                          "beskrivning": "Test",
                          "verksamhetslogik": "VAB",
                          "url": "",
                          "individer": [],
                          "handlaggar_id": {"typ_id": "kortnummer", "varde": "99999"},
                          "planerad_till": null,
                          "utford": null,
                          "erbjudande": {"id": "e1", "namn": "Erbjudande 1"}
                        }
                        """))
      );

      given()
            .contentType("application/json")
            .body("{\"handlaggarId\": {\"typId\": \"kortnummer\", \"varde\": \"99999\"}}")
            .when().patch("/admin/tasks/test-002")
            .then()
            .statusCode(200);
   }

   @Test
   void updateUppgift_returns404WhenNotFound()
   {
      OulManagementWireMock.server.stubFor(
            patch(urlPathEqualTo("/uppgifter/does-not-exist"))
                  .willReturn(aResponse().withStatus(404))
      );

      given()
            .contentType("application/json")
            .body("{\"handlaggarId\": null}")
            .when().patch("/admin/tasks/does-not-exist")
            .then()
            .statusCode(404);
   }

   @Test
   void createSorteringsordning_returnsCreatedWithBody()
   {
      OulManagementWireMock.server.stubFor(
            post(urlPathEqualTo("/sorteringsordning"))
                  .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                              {
                                "id": "f47ac10b-0001-0001-0001-000000000001",
                                "skapad": "2026-06-11T09:00:00Z",
                                "entries": [
                                  {
                                    "sort_by": {"field": "skapad", "direction": "asc"}
                                  }
                                ]
                              }
                              """))
      );

      given()
            .contentType("application/json")
            .body("""
                  {"entries": [{"sort_by": {"field": "skapad", "direction": "asc"}}]}
                  """)
            .when().post("/admin/sorteringsordning")
            .then()
            .statusCode(201)
            .body("id", equalTo("f47ac10b-0001-0001-0001-000000000001"))
            .body("entries[0].sort_by.field", equalTo("skapad"));
   }

   @Test
   void createSorteringsordning_propagates400FromOul()
   {
      OulManagementWireMock.server.stubFor(
            post(urlPathEqualTo("/sorteringsordning"))
                  .willReturn(aResponse().withStatus(400))
      );

      given()
            .contentType("application/json")
            .body("{\"entries\": []}")
            .when().post("/admin/sorteringsordning")
            .then()
            .statusCode(400);
   }

   @Test
   void getSorteringsordning_returnsItemById()
   {
      OulManagementWireMock.server.stubFor(
            get(urlPathEqualTo("/sorteringsordning/f47ac10b-0001-0001-0001-000000000001"))
                  .willReturn(okJson("""
                        {
                          "id": "f47ac10b-0001-0001-0001-000000000001",
                          "skapad": "2026-06-01T10:00:00Z",
                          "entries": [
                            {
                              "sort_by": {"field": "regel", "direction": "asc"}
                            }
                          ]
                        }
                        """))
      );

      given()
            .when().get("/admin/sorteringsordning/f47ac10b-0001-0001-0001-000000000001")
            .then()
            .statusCode(200)
            .body("id", equalTo("f47ac10b-0001-0001-0001-000000000001"))
            .body("entries[0].sort_by.field", equalTo("regel"));
   }

   @Test
   void getSorteringsordning_returns404WhenNotFound()
   {
      OulManagementWireMock.server.stubFor(
            get(urlPathEqualTo("/sorteringsordning/does-not-exist"))
                  .willReturn(aResponse().withStatus(404))
      );

      given()
            .when().get("/admin/sorteringsordning/does-not-exist")
            .then()
            .statusCode(404);
   }

   @Test
   void getDefaultSorteringsordning_returnsDefaultFromOul()
   {
      OulManagementWireMock.server.stubFor(
            get(urlPathEqualTo("/sorteringsordning/default"))
                  .willReturn(okJson("""
                        {
                          "id": "f47ac10b-0001-0001-0001-000000000001",
                          "skapad": "2026-06-01T10:00:00Z",
                          "entries": [
                            {
                              "constraints": [
                                {"field": "skapad", "operator": "between", "from": "2026-01-01", "to": "2026-06-30"}
                              ],
                              "sort_by": {"field": "skapad", "direction": "desc"}
                            }
                          ]
                        }
                        """))
      );

      given()
            .when().get("/admin/sorteringsordning/default")
            .then()
            .statusCode(200)
            .body("id", equalTo("f47ac10b-0001-0001-0001-000000000001"))
            .body("entries", hasSize(1))
            .body("entries[0].sort_by.field", equalTo("skapad"))
            .body("entries[0].sort_by.direction", equalTo("desc"))
            .body("entries[0].constraints[0].operator", equalTo("between"));
   }

   @Test
   void getDefaultSorteringsordning_returns404WhenNotConfigured()
   {
      OulManagementWireMock.server.stubFor(
            get(urlPathEqualTo("/sorteringsordning/default"))
                  .willReturn(aResponse().withStatus(404))
      );

      given()
            .when().get("/admin/sorteringsordning/default")
            .then()
            .statusCode(404);
   }

   @Test
   void setDefaultSorteringsordning_returns204OnSuccess()
   {
      OulManagementWireMock.server.stubFor(
            put(urlPathEqualTo("/sorteringsordning/f47ac10b-0001-0001-0001-000000000001/default"))
                  .willReturn(aResponse().withStatus(204))
      );

      given()
            .when().put("/admin/sorteringsordning/f47ac10b-0001-0001-0001-000000000001/default")
            .then()
            .statusCode(204);
   }

   @Test
   void setDefaultSorteringsordning_returns404WhenNotFound()
   {
      OulManagementWireMock.server.stubFor(
            put(urlPathEqualTo("/sorteringsordning/does-not-exist/default"))
                  .willReturn(aResponse().withStatus(404))
      );

      given()
            .when().put("/admin/sorteringsordning/does-not-exist/default")
            .then()
            .statusCode(404);
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
