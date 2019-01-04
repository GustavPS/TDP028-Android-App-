Appbeskrivning
--------------
Recipyfinder fungerar som en sök applikation där man kan söka efter recept i en stor databas
som innehåller mer eller mindre alla recept som finns. Med appen så kan man söka efter recept
och när man hittat något man vill ha så kan man gå till det receptet och hitta
detaljerade instruktioner samt alla ingredienser som behövs.

För att göra det enkelt att komma ihåg alla recept så finns ett bokmärkssystem implementerat
så användaren slipper komma ihåg namnet till alla sina favoritrecept.

Inbyggt finns även ett vänn-system så man kan se sina vänners favoriträtter ifall man själv
inte vet vad man vill äta.

Specifikationer
---------------
* Appen sparar inte några av recepten själv, utan appen söker igenom en stor databas.
* Varje recept har en bild, information om ingredienser och en länk till instruktionerna.
* Alla användare kan söka efter recept, men kan inte lägga till egna. Detta kan vara 
värt att bygga ut ifall appen skulle bli större.

Tekniska krav
---------------
* Modulär kod
* Användning av fragments, dvs. bra avvägning mellan användning av activities och fragments, enligt best practices
* Hantering av användarinput på rätt nivå i koden, dvs. korrekt avvägning mellan view/fragment vs. activity
* Snygg användning av callbacks, dvs. anonyma listeners, men samtidigt underlättat för testning.  ( Används huvudsakligen i RecipyHandler)
* Användning av interfaces vid inkoppling av fragment till aktivitet ( Använder interface för kommunikation mellan activity och fragments )
* Hantering av bakåtknapp, så att man aldrig hamnar konstigt vid tryck på bakåtknappen
* Åtminstone en egenskriven adapter

API-krav
---------------
* Använder Firebase som databas
* Enkel inloggning mot Firebase
* Koll på inloggningsstatus för att kunna använda vissa features, t.ex. att inloggning krävs för att få se viss information i appen
* Kontohantering, t.ex. lagra poäng, vänlista, eller dylikt som ska sparas över tid för olika användare
* Tredjepartsinloggning med Google
* Tredjepartsinloggning med Facebook


Screencasts
---------------
Användardemo: https://www.youtube.com/watch?v=C--XZQ5C0tE