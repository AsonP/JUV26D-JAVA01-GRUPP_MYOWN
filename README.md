# Fångdjupet

![Tests](https://github.com/AsonP/JUV26D-JAVA01-GRUPP_MYOWN/actions/workflows/tests.yml/badge.svg)

Ett textbaserat rollspel i konsolen där en hjälte slåss sig igenom en serie fiender i en fängelsehåla.
Gruppuppgift i kursen JAVA01 (JUV26D). Fokus ligger på att arbeta som ett litet utvecklingsteam:
feature branches, pull requests, kodgranskning och enkla designmönster.

## Krav

- JDK 27
- Maven behöver inte vara installerat. Projektet använder Maven Wrapper, som laddar ner rätt version automatiskt.

## Köra spelet

I IntelliJ: öppna projektet och kör `fangdjupet.Main`.

Från terminalen (Windows):

```
.\mvnw compile exec:java -Dexec.mainClass=fangdjupet.Main
```

På macOS och Linux används `./mvnw` i stället för `.\mvnw`.

## Köra testerna

```
.\mvnw test
```

Testerna körs också automatiskt med GitHub Actions vid varje push till `main` och vid varje pull request.
Statusbrickan överst visar resultatet av den senaste körningen.

## Projektstruktur och ansvar

| Paket | Innehåll | Ansvarig |
|---|---|---|
| `fangdjupet.contract` | Gemensamma interfaces, records och enums som modulerna delar | Hela gruppen |
| `fangdjupet.combat` | Hjälte, fiender, anfallsstrategier och turordning i strid | |
| `fangdjupet.item` | Föremål, inventarium och fabriker för föremål och fiender | |
| `fangdjupet.game` | Meny, spelmotor, händelselogg och sparfunktion | |

Modulerna känner bara till varandra genom kontraktet i `fangdjupet.contract`. Därför kan de utvecklas parallellt.

## Designmönster

### Strategy
`AttackStrategy` beskriver hur skada beräknas, och `MeleeAttack` är en implementation av den.
En karaktär kan byta anfallssätt utan att karaktärsklassen ändras, och nya anfallstyper som
`MagicAttack` eller `RangedAttack` kan läggas till utan att befintlig kod ändras.
Skadeberäkningen är dessutom fristående och därför lätt att testa.

### Observer
`Game` skickar händelser (`GameEvent`) till alla registrerade `GameEventListener`, till exempel `EventLog`
som skriver ut dem i konsolen. Spellogiken behöver inte veta vem som lyssnar. I testerna används en vanlig
lista som lyssnare (`events::add`) för att kontrollera vilka händelser som skickades.

### Factory (planerad)
En `EnemyFactory` och en `ItemFactory` ska skapa fiender och föremål utifrån typ och nivå.
Tills vidare skapar `AdventureSetup` hjälten och fienderna.

## Arbetssätt med Git (förslag)

**Branches**
- `main` ska alltid gå att bygga och köra.
- Allt nytt arbete görs i en feature branch från en uppdaterad `main`:
  ```
  git checkout main
  git pull
  git checkout -b feature/<kort-beskrivning>
  ```
- Namnge branches efter vad de gör, till exempel `feature/inventory` eller `feature/save-game`.

**Commits**
- Små commits med beskrivande meddelanden på engelska, till exempel "Add Inventory with filtering by type".

**Pull requests**
- En pull request per funktion. Hellre flera små än en stor.
- Minst en annan gruppmedlem granskar och kommenterar innan merge.
- Testerna i GitHub Actions ska vara gröna innan merge.
- Den som skapade pull requesten mergar efter godkänd granskning.
- Branchen tas bort efter merge.

**Konflikter**
- Den som får en konflikt hämtar senaste `main` till sin branch, löser konflikten och pushar igen.
- Konflikter i någon annans modul löses tillsammans med den personen.

## Gruppreflektion kring Git-samarbetet

(Vad fungerade bra, vad var svårt, och hur löstes konflikter.)
