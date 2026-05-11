import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Główna scena odpalenia Java
public class Main {
    public static void main(String[] args) {
        
        // Najpierw ręcznie spreparujemy stolicę jako zapchajdziurę, bo bez pliku system nie odpali obiektu (zabezpieczenie konstruktorów).
        City testCity = City.parseFile("strefy.csv").getOrDefault("Warszawa", null);

        // --- TEST KROK 2 --- (Przetestuj tabelę z różnicą godzin 12 a 24).
        System.out.println("--- TEST KROK 2 (Digital Clock 12 vs 24 format) ---");
        // Sprawdzamy konstrukcję na format ujednolicony.
        DigitalClock clock24 = new DigitalClock(testCity, DigitalClock.Format.H24);
        // Konstrukcja po formacie PM/AM.
        DigitalClock clock12 = new DigitalClock(testCity, DigitalClock.Format.H12);
        
        // Sztuczna tablica ze zleconymi czasami testowymi by zrobić pętle.
        int[][] times = {
            {0, 0, 0}, {0, 1, 0}, {1, 0, 0}, {11, 59, 0}, {12, 0, 0}, {13, 0, 0}, {23, 59, 0}
        };
        // Masowe sprawdzenie - czy wleciało dobrze.
        for(int[] t : times) {
            clock24.setTime(t[0], t[1], t[2]);
            clock12.setTime(t[0], t[1], t[2]);
            System.out.println("Podany: " + t[0] + ":00 -> 12h: " + clock12 + " | 24h: " + clock24);
        }

        // --- TEST KROK 3 --- (Czytanie wiersza CSV z miastami strefowymi z mapą po Nazwie).
        System.out.println("\n--- TEST KROK 3 ---");
        // Uruchomienie parsera.
        Map<String, City> citiesMap = City.parseFile("strefy.csv");
        System.out.println("Wczytano do wirtualnej pamieci obiekty miast w szt.: " + citiesMap.size());

        // --- TEST KROK 4 --- (Testowanie przeprowadzki - przesunięcia godzin!)
        System.out.println("\n--- TEST KROK 4 ---");
        // Skoro Warszawa ma bazowo ułamek 22.5 a Tokio 139 to różnica stref da nam przeskoki czasowe. 
        City warszawa = citiesMap.get("Warszawa");
        City tokio = citiesMap.get("Tokio"); // W CSV leży +9 godzin
        
        // Zegar stacjonuje w stolicy Polski
        clock24.setCity(warszawa);
        // Nadajemy godzinę
        clock24.setTime(10, 0, 0);
        System.out.println("Aktualny Czas u Nas (Warszawa): " + clock24);
        
        // Rezerwujemy bilet, lecimy do Tokio - odpalamy na zegarku "Znajduję się w Azji".
        clock24.setCity(tokio);
        System.out.println("Czas po przestawieniu zewn. mechanizmu strefy na Japonię: " + clock24);

        // --- TEST KROK 5 --- (Wyciąganie matematycznego, słonecznego, miejscowego offsetu ze Słońcem a długością grawimetryczną osi).
        System.out.println("\n--- TEST KROK 5 ---");
        // Przyjmijmy wspomniany Lublin.
        City lublin = citiesMap.get("Lublin");
        // Skoro polecenie wymaga podać do funkcji czas systemowy 12 z komputera, odpalę ją na "sztywno" by uzyskać zweryfikowane różnice ułamkowe.
        LocalTime wPoludnie = LocalTime.of(12, 0, 0);
        System.out.println("Dla miasta Lublin o systemowym " + wPoludnie + ", Słoneczny (Local Mean Time) to: " + lublin.localMeanTime(wPoludnie));

        // --- TEST KROK 6 --- (Zbuduj komparator gubienia osi z systemową administracją terytorialną)
        System.out.println("\n--- TEST KROK 6 ---");
        // Narzędzie list konwertuje zebrany Słownik w potężną kolekcje, nadającą się do wymuszonego sortowania.
        List<City> citiesList = new ArrayList<>(citiesMap.values());
        // Wykorzystujemy referencje na metodę (City::worstTimezoneFit) do oceny każdego z nich maszynowo.
        citiesList.sort(City::worstTimezoneFit);
        // Odczyt po przejrzeniu wyników i wydrukowaniu topowych (lub całych).
        System.out.println("Miasta ze złym przesunięciem ułamkowym w hierarchii malejąco:");
        for(City c : citiesList) {
            System.out.print(c.getName() + ", ");
        }
        System.out.println(); // dla nowej ładnej linii na końcu.

        // --- TEST KROK 12 --- (Graficzny wektor!)
        System.out.println("\n--- TEST KROK 12 ---");
        // Produkujemy nowy cyferblat dedykowany na test w Warszawie.
        AnalogClock aClock = new AnalogClock(warszawa);
        // Nadajemy ładny testowy wzór dla tarczy
        aClock.setTime(10, 10, 30);
        // Funkcja wywołuje sama na sobie masową produkcję kilkudziesięciu wyciągniętych plików z bazą graficzną!
        City.generateAnalogClocksSvg(citiesList, aClock);
        System.out.println("Sprawdz swoj dysk (folder z projektem), Java wyprodukowala dziesiątki plikow .svg z cyferblatami!");
    }
}