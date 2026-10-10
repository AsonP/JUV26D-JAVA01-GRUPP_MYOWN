# Fångdjupet

![Tests](https://github.com/AsonP/JUV26D-JAVA01-GRUPP_MYOWN/actions/workflows/tests.yml/badge.svg)

Ett textbaserat rollspel i konsolen där en hjälte slåss sig igenom en serie fiender i en fängelsehåla.
Hjälten väljer anfallsstil, hittar vapen, rustningar och läkedrycker efter varje seger, och äventyret
sparas automatiskt så att det kan fortsättas senare.

Gruppuppgift i kursen JAVA01 (JUV26D). Fokus ligger på att arbeta som ett litet utvecklingsteam:
feature branches, pull requests, kodgranskning och enkla designmönster.

## Krav

- JDK 27
- Maven behöver inte vara installerat. Projektet använder Maven Wrapper, som laddar ner rätt version automatiskt.

## Köra spelet

I IntelliJ: öppna projektet och kör `fangdjupet.Main` via den gröna pilen i `Main.java`.

Från terminalen (Windows):

```
.\mvnw compile exec:java -Dexec.mainClass=fangdjupet.Main
```

På macOS och Linux används `./mvnw` i stället för `.\mvnw`.

## Köra testerna

```
.\mvnw test
```

Om bygget beter sig konstigt efter större ändringar, bygg om allt från början med `.\mvnw clean test`.

Testerna körs också automatiskt med GitHub Actions vid varje push till `main` och vid varje pull request.
Statusbrickan överst visar resultatet av den senaste körningen.

## Spelet

```
=== Fångdjupet ===
1. Starta nytt äventyr
2. Fortsätt sparat äventyr
3. Visa hjältens utrustning
4. Strid mot nästa fiende
5. Drick läkedryck
0. Avsluta
```

- **Nytt äventyr:** spelaren namnger hjälten och väljer anfallsstil: närstrid, avståndsanfall eller magi.
- **Strid:** hjälten slår först och fienden slår tillbaka om den överlever. Varje besegrad fiende lämnar
  ett föremål på samma nivå som fienden.
- **Utrustning:** vapen och rustningar tas på automatiskt om de är bättre än det hjälten bär.
  Övriga föremål hamnar i ryggsäcken.
- **Sparande:** äventyret sparas automatiskt efter varje händelse och när spelaren avslutar.
  När äventyret är vunnet eller förlorat tas sparfilen bort.

## Projektstruktur och ansvar

| Paket | Innehåll | Ansvarig |
|---|---|---|
| `fangdjupet.contract` | Gemensamma interfaces, records och enums som modulerna delar | Hela gruppen |
| `fangdjupet.combat` | Hjälte, fiender, anfallsstrategier, turordning i strid och fiendefabrik | |
| `fangdjupet.item` | Vapen, rustningar, drycker, generiskt inventarium och föremålsfabrik | |
| `fangdjupet.game` | Meny, spelmotor, händelselogg och sparfunktion | |

Modulerna känner till varandra genom kontraktet i `fangdjupet.contract`. Spelmotorn i `fangdjupet.game`
är den enda modul som använder de andra modulernas klasser direkt, eftersom det är den som sätter ihop spelet.

## Designmönster

### Strategy
`AttackStrategy` beskriver hur skada beräknas. Det finns tre implementationer:
`MeleeAttack` (anfall minus försvar), `RangedAttack` (anfall minus halva försvaret) och
`MagicAttack` (tre fjärdedelar av anfallet, oavsett försvar). Spelaren väljer anfallsstil
när äventyret startar, och menyn bygger listan direkt från strategierna, så en ny strategi
dyker upp utan att menyn behöver ändras. Den gemensamma regeln om minsta skada ligger i
`DamageRules` för att undvika kodduplicering. Varje strategi är fristående och därför lätt att testa.

### Observer
`Game` skickar händelser (`GameEvent`) till alla registrerade `GameEventListener`, till exempel `EventLog`
som skriver ut dem i konsolen. Spellogiken behöver inte veta vem som lyssnar. `Battle` får bara en
`GameEventListener` och vet inget om `Game` eller `EventLog`. I testerna används en vanlig lista som
lyssnare (`events::add`) för att kontrollera vilka händelser som skickades.

### Factory
`ItemFactory` skapar vapen, rustningar och drycker utifrån typ och nivå, och `EnemyFactory` skapar fiender
utifrån `EnemyType` och nivå. Den som behöver ett föremål eller en fiende behöver inte veta vilka klasser
som finns eller hur värdena räknas ut. Fabrikerna används både när fiender lämnar byte och när ett sparat
äventyr läses in. `EnemyFactory` ligger i `combat` och inte i `item`, så att föremålsmodulen inte blir
beroende av stridsmodulen.

## Sparfunktionen

`SaveManager` sparar äventyret som en textfil, `fangdjupet.save`, med en rad per sak:

```
HERO;Peter;40;8;3;33;Magi
EQUIP;WEAPON;2
ITEM;POTION;1
ENEMY;Goblinen;2;18;6;2;18
```

Ett eget textformat valdes framför Javas inbyggda serialisering, eftersom filen går att läsa och felsöka
och inte går sönder när en klass ändras. Föremål sparas som typ och nivå och återskapas med `ItemFactory`.
Alla fel, som en fil som saknas eller en trasig rad, samlas i undantaget `SaveGameException`,
så att menyn kan visa ett begripligt meddelande i stället för att programmet kraschar.

## Tester

JUnit 5-tester finns för den centrala logiken i varje modul:

| Testklass | Testar |
|---|---|
| `MeleeAttackTest`, `RangedAttackTest`, `MagicAttackTest` | Skadeberäkning för varje anfallsstil |
| `BattleTest` | Turordning och händelser i strid |
| `EnemyFactoryTest` | Fiendernas värden per nivå |
| `InventoryTest` | Lägga till, ta bort och filtrera föremål |
| `ItemFactoryTest` | Föremål och bonusar per typ och nivå |
| `GameTest` | Spelmotorn, byte efter strid, utrustning och drycker |
| `SaveManagerTest` | Spara, läsa in och felhantering, med en tillfällig katalog (`@TempDir`) |

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
