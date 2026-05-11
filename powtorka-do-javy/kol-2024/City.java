// Importujemy masę narzędzi z Javy do plików (File, Scanner) i czasu (LocalTime).
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
// Map i HashMap do stworzenia "słownika" miast, oraz Scanner do czytania.
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.List;

// Krok 3. Budujemy klasę przechowującą jeden wiersz z tabelki CSV.
public class City {
    // Nazwa miasta (np. "Warszawa").
    private String name;
    // Strefa czasowa (np. 2).
    private int timezone;
    // Długość geograficzna pobrana z CSV (wartość Double na ułamkach).
    private double longitude;

    // Prywatny konstruktor – używany tu tylko przez nasze wewnętrzne metody parsera pliku.
    private City(String name, int timezone, double longitude) {
        this.name = name;
        this.timezone = timezone;
        this.longitude = longitude;
    }

    // Krok 3. Akcesory pozwalające wyciągnąć informacje z zewnątrz bez prawa do ich modyfikacji.
    public String getName() { return name; }
    public int getTimezone() { return timezone; }

    // Krok 3. Prywatna, statyczna metoda produkująca obiekt miasta z surowej linijki CSV.
    private static City parseLine(String line) {
        // Tniemy tekst używając przecinków jako punktów podziału.
        String[] parts = line.split(",");
        // Pierwsza kolumna to nazwa.
        String name = parts[0].trim();
        // Druga to strefa czasowa – zmieniamy tekst na liczbę całkowitą (int).
        int tz = Integer.parseInt(parts[1].trim());
        
        // Z długością geograficzną (czwarta kolumna, indeks 3) jest ciężej - ma literki W lub E na końcu.
        String lonStr = parts[3].trim();
        // Wycinamy samą liczbę (obcinamy 2 ostatnie znaki: spację i literkę), potem robimy z niej Double.
        double lon = Double.parseDouble(lonStr.substring(0, lonStr.length() - 2).trim());
        // Jeśli tekst kończy się na "W" (Zachód), to matematycznie jest to stopień ujemny.
        if (lonStr.endsWith("W")) {
            // Mnożymy przez -1, żeby z wartości dodatniej zrobić ujemną.
            lon = -lon;
        }
        
        // Zwracamy posklejane na nowo miasto do życia.
        return new City(name, tz, lon);
    }

    // Krok 3. Publiczna metoda czytająca cały dokument i robiąca z niego Mapę (Słownik).
    public static Map<String, City> parseFile(String path) {
        // Przygotowujemy pustą HashMapę. Kluczem będzie String (Nazwa), wynikiem City.
        Map<String, City> map = new HashMap<>();
        try {
            // Odpalamy Skaner nakierowany na wskazany ścieżką plik.
            Scanner scanner = new Scanner(new File(path));
            // Pomijamy pierwszą linijkę, bo zawiera tylko puste nazwy kolumn (nagłówki).
            if (scanner.hasNextLine()) scanner.nextLine();
            
            // Dopóki jest cokolwiek poniżej...
            while (scanner.hasNextLine()) {
                // ...wyciągamy i przetwarzamy linijkę przez napisaną wyżej metodę.
                City c = parseLine(scanner.nextLine());
                // Gotowy obiekt miasta ląduje w "Słowniku" pod kluczem jego nazwy.
                map.put(c.getName(), c);
            }
            // Sprzątamy wyłączając skaner.
            scanner.close();
        } catch (FileNotFoundException e) {
            // Jeśli pliku na dysku by nie było, ubezpieczamy program wypluwając usterkę w konsole.
            e.printStackTrace();
        }
        // Zwracamy zapełniony słownik.
        return map;
    }

    // Krok 5. Publiczna metoda obliczająca czas prawdziwy (słoneczny) po koordynatach.
    public LocalTime localMeanTime(LocalTime time) {
        // Wg zadania: "Przesunięcie czasu zmienia się proporcjonalnie do długości geograficznej". 
        // Skoro doba ma 24h a kula ziemska 360 stopni -> 1 godzina to 15 stopni.
        // Dzieląc nasze stopnie przez 15, uzyskujemy ilość godzin od południka zero.
        double geoHours = longitude / 15.0;
        
        // Czas podany w strefie obejmuje już przesunięcie sztuczne (timezone). Oddejmujemy je.
        // Potem dodajemy to nasze - czysto naturalne geograficzne (geoHours).
        double offsetHours = geoHours - timezone;
        
        // Przeliczamy to na ilość sekund dla precyzji działania zegarka, rzutując na (long).
        long offsetSeconds = (long)(offsetHours * 3600);
        
        // Do wskazanego czasu bazowego 'time' dorzucamy/odejmujemy te sekundy.
        return time.plusSeconds(offsetSeconds);
    }

    // Krok 6. Metoda oceniająca niedopasowanie dla komparatora układającego elementy (np. podczas sortowania).
    public static int worstTimezoneFit(City c1, City c2) {
        // Liczymy matematyczną różnicę między przesunięciem administracyjnym (timezone) a realnym (longitude/15) dla miasta 1.
        // Math.abs ucina minus (zmienia na wartość bezwzględną), bo interesuje nas wielkość błędu.
        double diff1 = Math.abs((c1.longitude / 15.0) - c1.timezone);
        // Analogicznie badamy miasto 2.
        double diff2 = Math.abs((c2.longitude / 15.0) - c2.timezone);
        
        // Double.compare standardowo sortuje rosnąco. Żeby ułożyć MALEJĄCO, musimy najpierw podać diff2, a potem diff1.
        return Double.compare(diff2, diff1);
    }

    // Krok 12. Metoda generująca folder z zegarami.
    public static void generateAnalogClocksSvg(List<City> cities, AnalogClock clock) {
        // Z "toString" zegara wyjdzie np. "15:30:00". Dwukropki są zabronione w nazwach folderów Windows! 
        // Używamy replace() aby zastąpić je myślnikami (-).
        String dirName = clock.toString().replace(":", "-");
        // Tworzymy uchwyt na folder o wyliczonej nazwie.
        File dir = new File(dirName);
        // .mkdirs() bezpiecznie i cicho tworzy folder z taką ścieżką w systemie na twardym dysku.
        dir.mkdirs();
        
        // Pętla przelatująca po kolei podane w argumencie miasta.
        for (City city : cities) {
            // Ustawiamy w zegarze nową tabliczkę z miastem, przez co wskazówki same (wyliczone z diff) przeskoczą w tle!
            clock.setCity(city);
            // Wywołujemy produkcję wektorowego obrazu podając ścieżkę (Folder/NazwaMiasta.svg).
            clock.toSvg(dirName + "/" + city.getName() + ".svg");
        }
    }
}