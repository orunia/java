// Importujemy klasy do obsługi wejścia/wyjścia (odczyt plików i łapanie błędów).
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
// Importujemy klasy do operowania na dacie i czasie.
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
// Importujemy narzędzia do list i dynamicznych tablic.
import java.util.ArrayList;
import java.util.List;

// Krok 1. Tworzymy abstrakcyjną klasę Country (oznacza to, że nie można stworzyć bezpośrednio jej obiektu,
// służy tylko jako baza dla klas po niej dziedziczących)[cite: 16].
public abstract class Country {

    // Krok 1. Definiujemy prywatne, ostateczne (final) pole name typu String, przechowujące nazwę państwa[cite: 17].
    private final String name;

    // Krok 2. Definiujemy statyczne (wspólne dla całej klasy), prywatne pole przechowujące ścieżkę do pliku z zakażeniami[cite: 22].
    private static String confirmedCasesFile;
    
    // Krok 2. Definiujemy statyczne, prywatne pole przechowujące ścieżkę do pliku ze zgonami[cite: 22].
    private static String deathsFile;

    // Krok 1. Publiczny konstruktor, który podczas tworzenia wywołuje się z argumentem (nazwą państwa)[cite: 17].
    public Country(String name) {
        // Przypisujemy argument name do pola name naszej klasy (używając słówka this dla odróżnienia).
        this.name = name;
    }

    // Krok 1. Piszemy akcesor (getter), czyli publiczną metodę pozwalającą odczytać prywatne pole name[cite: 17].
    public String getName() {
        // Zwracamy po prostu zawartość pola name.
        return name;
    }

    // Krok 2. Publiczna, statyczna metoda setFiles ustawiająca ścieżki plików z danymi[cite: 23].
    // Sygnalizujemy (throws), że może wyrzucić wyjątek, jeśli plik nie istnieje[cite: 26].
    public static void setFiles(String confirmedPath, String deathsPath) throws FileNotFoundException {
        // Tworzymy obiekt File reprezentujący plik z zakażeniami na dysku, używając podanej ścieżki.
        File f1 = new File(confirmedPath);
        // Sprawdzamy czy plik istnieje oraz czy mamy uprawnienia do jego odczytu[cite: 24].
        if (!f1.exists() || !f1.canRead()) {
            // Jeśli nie, rzucamy (throw) nowy wyjątek FileNotFoundException podając mu błędną ścieżkę[cite: 26].
            throw new FileNotFoundException(confirmedPath);
        }
        
        // Tworzymy obiekt File reprezentujący plik ze zgonami na dysku.
        File f2 = new File(deathsPath);
        // Analogicznie sprawdzamy jego istnienie i możliwość odczytu[cite: 24].
        if (!f2.exists() || !f2.canRead()) {
            // Jeśli jest problem, rzucamy błąd z podaniem ścieżki pliku ze zgonami[cite: 26].
            throw new FileNotFoundException(deathsPath);
        }
        
        // Jeśli oba pliki są w porządku, zapisujemy ich ścieżki w prywatnych statycznych polach tej klasy[cite: 23].
        confirmedCasesFile = confirmedPath;
        deathsFile = deathsPath;
    }

    // Krok 3. Wewnątrz klasy Country definiujemy prywatną, statyczną klasę reprezentującą położenie kolumn[cite: 33].
    private static class CountryColumns {
        // Publiczne, ostateczne całkowite pole na indeks początkowy kolumny[cite: 33].
        public final int firstColumnIndex;
        // Publiczne, ostateczne całkowite pole mówiące o ilości kolumn należących do danego państwa[cite: 33].
        public final int columnCount;

        // Konstruktor służący do ustawienia obu powyższych wartości przy tworzeniu obiektu tej wewnętrznej klasy[cite: 33].
        public CountryColumns(int firstColumnIndex, int columnCount) {
            // Przypisanie argumentu do pierwszego pola.
            this.firstColumnIndex = firstColumnIndex;
            // Przypisanie argumentu do drugiego pola.
            this.columnCount = columnCount;
        }
    }

    // Krok 3. Prywatna, statyczna metoda odnajdująca kolumny danego państwa w pierwszym wierszu CSV[cite: 34].
    private static CountryColumns getCountryColumns(String firstRow, String countryName) throws CountryNotFoundException {
        // Dzielimy pierwszy wiersz na tablicę napisów (kolumn) na podstawie średników[cite: 11].
        String[] columns = firstRow.split(";");
        
        // Przygotowujemy zmienną przechowującą indeks pierwszej kolumny (na start -1, co oznacza "nie znaleziono").
        int firstIndex = -1;
        // Przygotowujemy zmienną zliczającą kolumny państwa (startujemy od 0).
        int count = 0;

        // Pętla iterująca przez wszystkie odczytane nazwy kolumn (nagłówki państw).
        for (int i = 0; i < columns.length; i++) {
            // Sprawdzamy czy tekst w danej komórce równa się poszukiwanej nazwie państwa.
            if (columns[i].equals(countryName)) {
                // Jeżeli to pierwsze trafienie, firstIndex nadal wynosi -1.
                if (firstIndex == -1) {
                    // Zapisujemy pozycję jako początkową kolumnę.
                    firstIndex = i;
                }
                // Niezależnie czy to pierwsza czy kolejna kolumna tego samego państwa, zwiększamy licznik kolumn.
                count++;
            }
        }

        // Po przejrzeniu całego wiersza sprawdzamy, czy w ogóle znaleźliśmy państwo (czy indeks się zmienił).
        if (firstIndex == -1) {
            // Jeśli nie, wyrzucamy stworzony wcześniej własny wyjątek, podając nazwę nieistniejącego państwa[cite: 31, 37].
            throw new CountryNotFoundException(countryName);
        }

        // W przeciwnym razie zwracamy nowy obiekt z danymi o kolumnach (indeks startowy i ilość)[cite: 35].
        return new CountryColumns(firstIndex, count);
    }

    // Krok 2 i 5. Publiczna, statyczna metoda parsująca z podaną nazwą państwa, zadeklarowana ze zwracaniem abstrakcyjnego Country[cite: 28].
    public static Country fromCsv(String countryName) throws CountryNotFoundException {
        // Deklarujemy zmienną na polimorficzny obiekt Country, który ostatecznie zwrócimy z metody.
        Country result = null;

        // Zabezpieczamy blokiem try na wypadek błędów odczytu z wejścia/wyjścia. Try with resources automatycznie zamyka pliki[cite: 29].
        try (
            // Otwieramy plik CSV z zachorowaniami, wsadzamy czytnik FileReader do BufferedReadera (dla wydajności i metody readLine).
            BufferedReader confReader = new BufferedReader(new FileReader(confirmedCasesFile));
            // Analogicznie otwieramy i przygotowujemy do czytania plik ze zgonami.
            BufferedReader deathReader = new BufferedReader(new FileReader(deathsFile))
        ) {
            // Czytamy pierwszy wiersz (nazwy państw) z pliku zachorowań[cite: 5].
            String confHeader1 = confReader.readLine();
            // Czytamy (i ignorujemy/pomijamy z użycia, bo układ jest identyczny) pierwszy wiersz z pliku zgonów[cite: 4].
            deathReader.readLine();

            // Krok 3. Pobieramy informacje o kolumnach dla tego konkretnego państwa z pierwszego wiersza[cite: 37].
            CountryColumns cols = getCountryColumns(confHeader1, countryName);

            // Czytamy drugi wiersz (nazwy prowincji) z pliku zachorowań[cite: 6].
            String confHeader2 = confReader.readLine();
            // Czytamy drugi wiersz (nazwy prowincji) z pliku zgonów (znowu dla zrównania linii w obu plikach).
            deathReader.readLine();

            // Dzielimy ten drugi wiersz po średnikach, by uzyskać nazwy prowincji[cite: 11].
            String[] provNames = confHeader2.split(";");

            // Przygotowujemy pustą tablicę na obiekty prowincji, wykorzystywaną jeśli takie powstaną[cite: 19].
            Country[] provinces = null;

            // Krok 5. Sprawdzamy czy państwo posiada prowincje. Posiada jedną kolumnę a w drugim wierszu napis "nan" to brak prowincji[cite: 7, 36].
            if (cols.columnCount == 1 && provNames[cols.firstColumnIndex].equals("nan")) {
                // Jeśli nie posiada, inicjujemy wynik jako obiekt klasy państwa bez prowincji[cite: 44, 46].
                result = new CountryWithoutProvinces(countryName);
            } else {
                // Jeśli posiada, inicjujemy tablicę prowincji rozmiarem odczytanych powtórzeń kolumn[cite: 19].
                provinces = new Country[cols.columnCount];
                // Pętla tworząca obiekty poszczególnych prowincji.
                for (int i = 0; i < cols.columnCount; i++) {
                    // Do naszej tablicy wkładamy nowe państwo bez prowincji, gdzie nazwą jest odpowiedni element z drugiego wiersza[cite: 47].
                    provinces[i] = new CountryWithoutProvinces(provNames[cols.firstColumnIndex + i]);
                }
                // Gdy tablica jest gotowa, tworzymy nasz polimorficzny wynik jako państwo z prowincjami, przekazując tę tablicę[cite: 20, 44].
                result = new CountryWithProvinces(countryName, provinces);
            }

            // Deklarujemy zmienną przechowującą pojedynczą linijkę zachorowań w pętli.
            String confLine;
            // Deklarujemy zmienną przechowującą pojedynczą linijkę zgonów w pętli.
            String deathLine;
            
            // Definiujemy parser dat, zgodny z opisanym w poleceniu formacie amerykańskim M/d/yy[cite: 9].
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");

            // Rozpoczynamy odczytywanie pliku od trzeciego wiersza (ponieważ readLine użyliśmy już 2 razy na obu).
            // Pętla trwa dopóki nie skończą się linie w którymś z plików.
            while ((confLine = confReader.readLine()) != null && (deathLine = deathReader.readLine()) != null) {
                
                // Dzielimy aktualną linię zachorowań na komórki przy użyciu średnika[cite: 11].
                String[] confData = confLine.split(";");
                // Dzielimy aktualną linię zgonów na komórki analogicznym sposobem[cite: 11].
                String[] deathData = deathLine.split(";");
                
                // W pierwszej kolumnie (czyli na indeksie 0) leży data[cite: 9]. Przemieniamy napis na formalny obiekt LocalDate[cite: 42].
                LocalDate date = LocalDate.parse(confData[0], formatter);

                // Sprawdzamy, czy nasz nadrzędny obiekt to ten bez prowincji.
                if (result instanceof CountryWithoutProvinces) {
                    // Konwertujemy tekstową liczbę zachorowań ze wskazanej dla państwa kolumny na prawdziwy integer (int).
                    // Wartości ujemne parsują się automatycznie poprawnie, zgodnie z poleceniem[cite: 10].
                    int cases = Integer.parseInt(confData[cols.firstColumnIndex]);
                    // Konwertujemy tekstową liczbę zgonów w ten sam sposób.
                    int deaths = Integer.parseInt(deathData[cols.firstColumnIndex]);
                    
                    // Rzutujemy bezpiecznie nasz wynik na typ CountryWithoutProvinces i dodajemy wyciągniętą dzienną statystykę[cite: 45, 46].
                    ((CountryWithoutProvinces) result).addDailyStatistic(date, cases, deaths);
                } else {
                    // Jeżeli to państwo z prowincjami, to na każdej dacie iterujemy przez wszystkie przypisane mu kolumny[cite: 47].
                    for (int i = 0; i < cols.columnCount; i++) {
                        // Odczytujemy przypadek chorobowy dla danej prowincji.
                        int cases = Integer.parseInt(confData[cols.firstColumnIndex + i]);
                        // Odczytujemy zgony dla danej prowincji.
                        int deaths = Integer.parseInt(deathData[cols.firstColumnIndex + i]);
                        
                        // Wykorzystujemy lokalnie utworzoną przed chwilą tablicę provinces, 
                        // rzutujemy jej element na CountryWithoutProvinces i dodajemy statystykę wewnątrz[cite: 45, 47].
                        ((CountryWithoutProvinces) provinces[i]).addDailyStatistic(date, cases, deaths);
                    }
                }
            }
        } catch (IOException e) {
            // W razie jakiegokolwiek problemu np. przy zamykaniu pliku, wypluwamy błąd w oknie konsoli.
            e.printStackTrace();
        }

        // Oddajemy wygenerowany z uzupełnionymi polami obiekt światu (zwracamy go).
        return result;
    }

    // Krok 6. Pisana jest przeciążona metoda fromCsv, która zamiast pojedynczego tekstu, bierze tablicę nazw[cite: 49].
    public static Country[] fromCsv(String[] countryNames) {
        // Szykujemy dynamiczną listę (ponieważ przy przeskakiwaniu niespełnionych państw nie wiemy jaki duży ostatecznie będzie wynik).
        List<Country> list = new ArrayList<>();
        
        // Pętla typu "for-each" iterująca po każdej nazwie dostarczonej w tablicy.
        for (String name : countryNames) {
            // Próbujemy (try) uruchomić poprzednią metodę dla aktualnie przeglądanej nazwy.
            try {
                // Jeśli się powiedzie, wkładamy zwrócony obiekt państwa do listy.
                list.add(fromCsv(name));
            } catch (CountryNotFoundException e) {
                // Jeśli wyrzuci błąd braku państwa, łapiemy wyjątek i wyciągamy oraz wyświetlamy na wyjściu jego komunikat (czyli nazwę państwa)[cite: 52].
                System.out.println(e.getMessage());
                // Pomijamy element w liście i pętla automatycznie poleci do następnego.
            }
        }
        
        // Ponieważ metoda ma zwrócić sztywną tablicę typu Country[] a nie List, konwertujemy ją narzędziem .toArray[cite: 50].
        return list.toArray(new Country[0]);
    }

    // Krok 7. Deklarujemy publiczną, czysto wirtualną (czyli po polsku abstrakcyjną) metodę getConfirmedCases.
    // Nie ma ciała metody, zmusza klasy dziedziczące do jej napisania[cite: 54].
    public abstract int getConfirmedCases(LocalDate date);

    // Krok 7. Analogicznie robimy czysto wirtualną metodę pobierającą informację o zgonach. Zakładamy poprawność daty[cite: 54, 55].
    public abstract int getDeaths(LocalDate date);

    // Abstrakcyjna metoda pomocnicza (niewspomniana dosłownie, ale absolutnie konieczna do wykonania Kroku 8 i 9).
    // Pozwoli nam na wyciągnięcie pełnej listy przetworzonych dat by móc tworzyć plik i pętle.
    public abstract List<LocalDate> getAvailableDates();

    // Krok 8. Statyczna metoda sortująca malejąco listę państw na podstawie zgonów w podanym zakresie czasowym[cite: 60].
    public static void sortByDeaths(List<Country> countries, LocalDate start, LocalDate end) {
        // Używamy metody sort wbudowanej w List, która jako argument bierze komparator (wyrażenie logiczne oceniające kolejność).
        countries.sort((c1, c2) -> {
            // Zmienna przechowująca łączną ilość śmierci w pierwszej porównywanej strukturze państwa.
            int sum1 = 0;
            // Zmienna na łączną ilość śmierci w drugiej.
            int sum2 = 0;
            
            // Ustawiamy wskaźnik daty na początek zakresu.
            LocalDate current = start;
            
            // Pętla hula dopóty, dopóki current nie "przeskoczy" daty end (czyli zliczy datę end włącznie z nią)[cite: 61, 62].
            while (!current.isAfter(end)) {
                // Dodajemy dzisiejsze zgony pierwszego kraju do jego sumy.
                sum1 += c1.getDeaths(current);
                // Dodajemy dzisiejsze zgony drugiego kraju do jego sumy.
                sum2 += c2.getDeaths(current);
                // "Przesuwamy" datę o jeden dzień do przodu.
                current = current.plusDays(1);
            }
            
            // Ponieważ sortowanie ma być malejące, odwracamy kolejność i w obiekcie Integer nakazujemy by porównał wynik 2 do wyniku 1[cite: 61].
            return Integer.compare(sum2, sum1);
        });
    }

    // Krok 9. Metoda generująca i formatująca gotowy plik zestawieniowy[cite: 64].
    public void saveToDataFile(String path) {
        // Łapiemy błędy operacji na plikach.
        try (
            // Korzystamy z wbudowanego narzędzia PrintWriter pozwalającego wygodnie zapisywać tekst z polskiego "println" do pliku[cite: 65].
            PrintWriter writer = new PrintWriter(new File(path))
        ) {
            // Tworzymy parser generujący tekst zgodny z narzuconym w dokumencie wzorcem: d.MM.yy[cite: 66].
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yy");
            
            // Wyciągamy z klas dziedziczących pulę wszystkich dat jakie przetworzył nasz program w danym kraju.
            List<LocalDate> dates = getAvailableDates();
            
            // Przelatujemy pojedynczo każdą z wyciągniętych dat.
            for (LocalDate date : dates) {
                // Zamieniamy obiekt w czysty tekst używając wcześniej zdefiniowanego wzorca formatter.
                String dateStr = date.format(formatter);
                // Prosimy nasz kraj (zależnie czy z prowincjami czy bez, zachowa się to różnie za sprawą polimorfizmu) o podanie na ten dzień przypadków.
                int cases = getConfirmedCases(date);
                // To samo dla zgonów na ten sam dzień.
                int deaths = getDeaths(date);
                
                // Plujemy gotową, surową złączoną linijką do pliku (zgodnie ze sztuką rozdzielając używając "\t" czyli tabulatora)[cite: 65, 66].
                writer.println(dateStr + "\t" + cases + "\t" + deaths);
            }
        } catch (FileNotFoundException e) {
            // Jeśli nie uda nam się utworzyć docelowego pliku, wyrzucamy ślad w terminalu.
            e.printStackTrace();
        }
    }
}