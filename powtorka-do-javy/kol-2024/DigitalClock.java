// Importujemy odpowiedni formater tekstu.
import java.time.format.DateTimeFormatter;
// Importujemy Locale (region), aby zmusić program do drukowania "AM/PM" w stylu amerykańskim.
import java.util.Locale;

// Krok 2. Tworzymy klasę DigitalClock, która "dziedziczy" (rozszerza) główną bazę Clock.
public class DigitalClock extends Clock {

    // Krok 2. Tworzymy wyliczenie (enum), które jest jak przełącznik: daje tylko dwie opcje wyboru formatu.
    public enum Format { H12, H24 }
    
    // Prywatne pole przechowujące decyzję użytkownika, co do formatu w tym konkretnym zegarze.
    private Format format;

    // Konstruktor. Wymusza podanie miasta (dla klasy nadrzędnej) oraz formatu wyświetlania.
    public DigitalClock(City city, Format format) {
        // super wywołuje konstruktor starej klasy (Clock), która wie, jak zapisać miasto.
        super(city);
        // Zapisujemy wybrany format do wnętrza obiektu.
        this.format = format;
    }

    // Krok 2. Nadpisujemy (zmieniamy) zachowanie metody zamieniającej czas na tekst.
    @Override
    public String toString() {
        // Jeśli aktualny format to zwykłe europejskie 24 godziny...
        if (format == Format.H24) {
            // Wykorzystaj algorytm napisany wyżej w abstrakcyjnej klasie Clock (tam robi to HH:mm:ss).
            return super.toString();
        } else {
            // Jeśli wybrano format 12-godzinny:
            // Wzorzec "h:mm:ss a": małe 'h' to 1-12 (bez zer z przodu), a 'a' to miejsce na AM/PM. 
            // Locale.US gwarantuje, że nie wydrukuje polskiego "w południe" tylko sztywne "AM/PM".
            DateTimeFormatter f = DateTimeFormatter.ofPattern("h:mm:ss a", Locale.US);
            // Zwracamy skonwertowany przez nasz nowy formater czas.
            return time.format(f);
        }
    }
}