# Teknisk spec — Portal Admin BFF (PABF)

## Översikt

Synkron REST BFF utan egen datalagring och utan meddelandeintegration. Primärt uppströmsberoende
är OUL:s administrationstjänst (`oul-management`). Ett sekundärt REST-klientberoende mot en
annan OUL-tjänst finns registrerat men används idag endast av hälsokontrollen.

## Komponentstruktur

```text
src/main/java/se/fk/github/portaladminbff
├── PortalAdminBffController   # REST-ändpunkter mot admin-frontend
├── OulManagementClient        # REST-klient mot oul-management (primär integration)
├── OulClient                  # REST-klient mot oul (registrerad, ej kopplad in i kontrollern)
├── UppgiftMapper               # Snake_case (OUL) -> camelCase (frontend) + null-normalisering
├── OulHealthCheck              # Readiness-koll mot "oul"-tjänsten
└── model/                     # DTO:er (Raw*/Oul*-varianter = uppströms form)
```

## API-specifikationer

Ingen extern OpenAPI-specifikation — kontraktet definieras av kontrollerklassen i denna tjänst.

| Metod | Sökväg | Beskrivning |
|---|---|---|
| GET | `/admin/handlaggare` | Mockad handläggarlista (samma testidentiteter som rimfrost-portal-bff) |
| GET | `/admin/tasks` | Samtliga operativa uppgifter |
| POST | `/admin/tasks/{uppgiftId}/unassign` | Ta bort tilldelning |
| PATCH | `/admin/tasks/{uppgiftId}` | Uppdatera tilldelad handläggare |
| GET | `/admin/sorteringsordning` | Lista sorteringsordningar |
| POST | `/admin/sorteringsordning` | Skapa sorteringsordning |
| GET | `/admin/sorteringsordning/{id}` | Hämta sorteringsordning |
| PUT | `/admin/sorteringsordning/{id}` | Uppdatera sorteringsordning |
| DELETE | `/admin/sorteringsordning/{id}` | Ta bort sorteringsordning |
| PUT | `/admin/sorteringsordning/{id}/default` | Sätt som standard |
| GET | `/admin/sorteringsordning/default` | Hämta standardsorteringsordning |
| POST | `/admin/sorteringsordning/preview` | Förhandsgranska matchande uppgifter |

## Kafka-integration

Ingen. Tjänsten har ingen meddelandeintegration.

## Konfiguration

| Egenskap | Beskrivning | Standardvärde |
|---|---|---|
| `quarkus.rest-client.oul-management.url` (`BE_OUL_MANAGEMENT_URL`) | Bas-URL till oul-management | `http://localhost:8889` |
| `quarkus.rest-client.oul.url` (`BE_OUL_URL`) | Bas-URL till sekundär OUL-tjänst (endast hälsokontroll) | `http://localhost:8889` |
| `portal.admin.tasks.limit` | Maxantal uppgifter per anrop till `/admin/tasks` | `500` |

## Liveness

`/q/health`, `/q/health/ready` (inkl. "oul-backend"-koll), `/q/health/live`.

## Kända begränsningar och framtida arbete

| Begränsning | Föreslagen åtgärd |
|---|---|
| Felhantering är inkonsekvent mellan ändpunkter — endast uppgiftslistningen hanterar otillgänglig uppströmstjänst gracefult | Enhetliggör felhanteringen för samtliga ändpunkter |
| `GET /admin/sorteringsordning` saknar felhantering helt | Lägg till motsvarande felhantering som övriga ändpunkter |
| Ingen indatavalidering trots att valideringsbibliotek är tillgängligt | Lägg till validering på inkommande specifikationer |
| Sekundär OUL-klient är oanvänd i praktiken, väntar på en administrationsändpunkt för uppgifter i den tjänsten | Koppla in eller ta bort när beslut är taget |
| `/admin/tasks` saknar egentlig paginering mot frontend, begränsas av en hårdkodad maxgräns | Inför paginering om uppgiftsvolymen växer förbi gränsen |
