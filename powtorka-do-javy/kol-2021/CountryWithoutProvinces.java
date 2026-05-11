// Importujemy podstawowe struktury daty.
import java.time.LocalDate;
// Narzędzia do budowania dynamicznych List oraz Map (słowników).
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Krok 1. Definiujemy klasę "Państwa bez prowincji" która podąża (dziedziczy - extends) za strukturą abstrakcyjnego państwa Country[cite: 16].
public class CountryWithoutProvinces extends Country {

    // Krok 4. Tworzymy samodzielnie mapę, przechowującą jako Klucz (identyfikator) wbudowany obiekt czasu (LocalDate),
    // a jako wpis przypisany do niego, tablicę dwóch liczb całkowitych Integer (jeden na zakażenia, drugi na zgony)[cite: 39].
    private final Map<LocalDate, int[]> dailyStats;

    // Krok 1. Wymuszony przez dziedziczenie konstruktor, wywoływany w momencie tworzenia obiektu, z argumentem nazwy państwa[cite: 18].
    public CountryWithoutProvinces(String name) {
        // Wywołujemy z samej góry hierarchii super konstruktor rodzica (Country) załączając przekazany ciąg znaków,
        // przez co z automatu zostanie wypełnione pole "name"[cite: 18].
        super(name);
        // Konstruujemy i przygotowujemy nowiutki "wirtualny słownik" oparty na Hashowaniu (HashMap).
        this.dailyStats = new HashMap<>();
    }

    // Krok 4. Napisana publiczna metoda implementująca rządek statystyk z konkretnego dnia. Przyjmuje poprawną datę, i dwie liczby[cite: 41].
    public void addDailyStatistic(LocalDate date, int confirmed, int deaths) {
        // Wypełniamy nową komórkę z kluczem-datą, świeżą tablicą typu int zawierającą wpis z zachorowaniami pod indeksem 0 a zgonami na 1[cite: 41].
        dailyStats.put(date, new int[]{confirmed, deaths});
    }

    // Krok 7. Deklaracja wysoce wymuszonej implementacji "wirtualnej metody" getConfirmedCases. Odpowiada za wysunięcie pojedynczej liczby o dacie[cite: 54, 56].
    @Override
    public int getConfirmedCases(LocalDate date) {
        // Prosimy słownik o element przydzielony dacie. Jeśli coś jest to wyciągnie tablicę, jak go nie ma odda słówko "null" (pustkę).
        int[] stat = dailyStats.get(date);
        // Oddajemy światu element spod indeksu "0" (ponieważ na zero kodowaliśmy chore osoby), albo jeśli go nie było podajemy czyste 0[cite: 57].
        return stat != null ? stat[0] : 0;
    }

    // Krok 7. Identyczna operacja tylko dedykowana do wyciągania liczby śmierci[cite: 54, 56].
    @Override
    public int getDeaths(LocalDate date) {
        // Wskazujemy słownikowi o jaki dzień pytamy.
        int[] stat = dailyStats.get(date);
        // Tym razem prosimy o zawartość tablicy z pozycji "1", lub wyrzucamy sztuczne zero jak nie było pomiaru[cite: 57].
        return stat != null ? stat[1] : 0;
    }

    // Metoda pomocnicza dla Kroku 9 nadpisująca (wypełniająca ciałem) wersję z klasy z rodzica.
    @Override
    public List<LocalDate> getAvailableDates() {
        // Wypompowuje z "słownika" zbiór wszystkich jego kluczy (keySet) - w naszym wypadku wszystkich zebranych historycznych dat.
        // Od razu wpakowuje to w wygodny twór klasycznej, sortowalnej ArrayListy i zwraca w odpowiedzi do wywołującego.
        return new ArrayList<>(dailyStats.keySet());
    }
}