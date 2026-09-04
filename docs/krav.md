# Krav — Portal Admin BFF (PABF)

## Bakgrund och syfte

Portal Admin BFF är backend-för-frontend för administrationsgränssnittet mot Operativt
Uppgiftslager (OUL). Den ger administrationsgränssnittet ett stabilt, frontend-vänligt
kontrakt för att observera operativa uppgifter och hantera sorteringsordningar, och döljer
OUL-administrationstjänstens interna datamodell och namngivningskonvention.

---

## Intressenter och aktörer

| Aktör | Roll |
|---|---|
| Portal Admin Frontend | Anropar BFF:n för att lista uppgifter och hantera sorteringsordningar |
| OUL:s administrationstjänst | Uppströmstjänst som äger uppgifts- och sorteringsordningsdata |

---

## Funktionella krav

### PABF-FR-01 — Uppgiftsöversikt

- **PABF-FR-01.1** BFF:n ska kunna hämta samtliga operativa uppgifter från OUL:s
  administrationstjänst, med ett konfigurerbart maxantal per anrop.
- **PABF-FR-01.2** Uppgiftsdata ska normaliseras till ett konsekvent, frontend-vänligt format
  innan det returneras, inklusive att alltid returnera tomsträng istället för utelämnat värde
  för planerings- och utförandetidpunkter.
- **PABF-FR-01.3** BFF:n ska kunna ta bort tilldelningen av en angiven uppgift.
- **PABF-FR-01.4** BFF:n ska kunna uppdatera en uppgifts tilldelade handläggare.

### PABF-FR-02 — Sorteringsordningar

- **PABF-FR-02.1** BFF:n ska kunna lista konfigurerade sorteringsordningar, paginerat.
- **PABF-FR-02.2** BFF:n ska kunna hämta en enskild sorteringsordning via dess identifierare.
- **PABF-FR-02.3** BFF:n ska kunna hämta den sorteringsordning som är satt som standard.
- **PABF-FR-02.4** BFF:n ska kunna skapa en ny sorteringsordning från en angiven specifikation.
- **PABF-FR-02.5** BFF:n ska kunna uppdatera en befintlig sorteringsordning.
- **PABF-FR-02.6** BFF:n ska kunna ta bort en sorteringsordning.
- **PABF-FR-02.7** BFF:n ska kunna sätta en angiven sorteringsordning som standard.
- **PABF-FR-02.8** BFF:n ska kunna förhandsgranska vilka uppgifter en (eventuellt osparad)
  sorteringsordningsspecifikation matchar och i vilken ordning, med paginering.

### PABF-FR-03 — Felhantering vid integration mot OUL

- **PABF-FR-03.1** Vid hämtning av uppgifter ska BFF:n särskilja fel som svarats av OUL,
  otillgänglighet hos OUL, och oväntade interna fel, och returnera lämplig HTTP-statuskod för
  varje fall.

---

## Icke-funktionella krav

### PABF-NFR-01 — Observerbarhet

- **PABF-NFR-01.1** BFF:n ska exponera en hälsokontroll som speglar tillgängligheten hos
  bakomliggande uppgiftstjänst.

---

## API-gränssnitt (översikt)

| API | Målgrupp | Specifikationsartefakt |
|---|---|---|
| Portal Admin BFF REST-API | Portal Admin Frontend | Definieras i denna tjänst (ingen extern OpenAPI-specifikation ännu) |

---

## Integration med OUL

Portal Admin BFF integrerar mot OUL:s administrationstjänst för både uppgiftsdata och
sorteringsordningar. Den lagrar inget tillstånd själv — all data ägs och persisteras
bakomliggande. En sekundär, ej färdig integrationsväg mot en annan OUL-tjänst finns förberedd
i väntan på en framtida administrationsändpunkt för uppgifter där.
