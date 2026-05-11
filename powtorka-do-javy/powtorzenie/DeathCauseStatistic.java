// Importujemy narzędzia z biblioteki Javy. W tym przypadku nic nie importujemy na górze, 
// ponieważ korzystamy z podstawowych typów danych i funkcji wbudowanych.

// Definiujemy główną klasę publiczną (widoczną dla innych) o nazwie podanej w zadaniu.
public class DeathCauseStatistic {

    // Tworzymy prywatne pole (dostępne tylko w tej klasie) typu String (tekst) na kod ICD-10.
    private String icd10Code;
    
    // Tworzymy prywatne pole typu int[] (tablica liczb całkowitych) na zgony w grupach wiekowych.
    private int[] deathsByAge;

    // Tworzymy tzw. konstruktor prywatny. To narzędzie służące do tworzenia obiektu tej klasy,
    // ale dzięki słówku 'private' możemy go użyć tylko z wnętrza tej klasy (np. w metodzie fromCsvLine).
    private DeathCauseStatistic(String icd10Code, int[] deathsByAge) {
        // Przypisujemy kod przekazany do konstruktora do pola klasy za pomocą słówka 'this'.
        this.icd10Code = icd10Code;
        // Przypisujemy tablicę zgonów do odpowiedniego pola w tej klasie.
        this.deathsByAge = deathsByAge;
    }

    // Publiczna (widoczna wszędzie), statyczna (można jej użyć bez tworzenia obiektu klasy) metoda
    // zwracająca obiekt DeathCauseStatistic, która przyjmuje tekst (String) o nazwie 'line' (linijka z CSV).
    public static DeathCauseStatistic fromCsvLine(String line) {
        // Rozdzielamy przekazany tekst (całą linijkę CSV) w miejscach, gdzie jest przecinek (",").
        // Zapisujemy wynik jako tablicę tekstów (String[]).
        String[] parts = line.split(",");
        
        // Pobieramy pierwszy element (indeks 0 to pierwsza kolumna z CSV), czyli kod choroby.
        String code = parts[0];
        
        // Zgodnie z poleceniem, usuwamy z kodu tabulator, zamieniając "\t" (znak tabulatora) na "" (pusty tekst).
        code = code.replace("\t", "");
        
        // Dodatkowo obcinamy ewentualne białe znaki (np. spacje) z początku i końca za pomocą metody trim().
        code = code.trim();

        // Tworzymy nową tablicę liczb całkowitych (int) o rozmiarze 20, bo tyle jest przedziałów wiekowych (od 0-4 do 95+).
        int[] deaths = new int[20];
        
        // Tworzymy pętlę, która wykona się 20 razy (dla każdej grupy wiekowej). Zmienna 'i' to licznik (od 0 do 19).
        for (int i = 0; i < 20; i++) {
            // Pobieramy konkretną kolumnę. Dodajemy 2 do 'i', ponieważ indeks 0 to kod, a indeks 1 to "Razem" (które pomijamy).
            String value = parts[i + 2];
            
            // W pliku CSV brak zgonów oznaczony jest myślnikiem "-". Sprawdzamy, czy pobrany tekst to myślnik.
            if (value.equals("-")) {
                // Jeśli to myślnik, wpisujemy do naszej tablicy wartość 0.
                deaths[i] = 0;
            } else {
                // W przeciwnym razie tekst to liczba. Metoda Integer.parseInt zamienia tekst na liczbę i zapisuje w tablicy.
                deaths[i] = Integer.parseInt(value);
            }
        }
        
        // Kiedy wszystko jest gotowe, tworzymy nowy obiekt naszej klasy za pomocą konstruktora i go zwracamy.
        return new DeathCauseStatistic(code, deaths);
    }

    // To jest tzw. akcesor (getter) - publiczna metoda, która po prostu zwraca wartość prywatnego pola icd10Code.
    public String getIcd10Code() {
        // Zwracamy kod choroby.
        return icd10Code;
    }

    // Zadanie 2 - Metoda przyjmująca wiek (int) i zwracająca obiekt AgeBracketDeaths.
    public AgeBracketDeaths getAgeBracket(int age) {
        // Dzielimy wiek przez 5, ponieważ grupy wiekowe rosną co 5 lat (0-4, 5-9 itd.).
        // To da nam indeks naszej tablicy. Np. dla 6 lat wynik to 1 (bo 6/5=1 w liczbach całkowitych).
        int index = age / 5;
        
        // Przedziały kończą się na indeksie 19 (dla wieku 95+). Jeśli wyliczony indeks jest większy, ucinamy go do 19.
        if (index > 19) {
            // Jeśli wiek to 100, indeks to 20. Zmieniamy go na 19, by nie wyjść poza tablicę.
            index = 19;
        }

        // Granica dolna grupy (young) to po prostu indeks pomnożony przez 5 (np. indeks 1 * 5 = 5).
        int young = index * 5;
        
        // Granica górna to zazwyczaj dolna + 4 (np. 5 + 4 = 9). Wyjątkiem jest ostatnia grupa (indeks 19).
        // Używamy zapisu warunkowego: jeśli indeks to 19, dajemy maksymalną wartość liczby całkowitej (brak górnej granicy).
        int old = (index == 19) ? Integer.MAX_VALUE : young + 4;
        
        // Pobieramy liczbę zgonów z naszej tablicy dla wyliczonego indeksu.
        int deathCount = deathsByAge[index];

        // Tworzymy i zwracamy nowy obiekt grupy wiekowej z wyliczonymi danymi.
        return new AgeBracketDeaths(young, old, deathCount);
    }

    // Wewnętrzna, publiczna klasa (zdefiniowana wewnątrz DeathCauseStatistic) opisana w Zadaniu 2.
    public class AgeBracketDeaths {
        // Publiczne (dostępne) i ostateczne (final - nie można zmienić ich wartości po utworzeniu) pole dla dolnej granicy.
        public final int young;
        
        // Publiczne, ostateczne pole dla górnej granicy wieku.
        public final int old;
        
        // Publiczne, ostateczne pole na liczbę zgonów w tym przedziale.
        public final int deathCount;

        // Konstruktor tej klasy wewnętrznej, potrzebny do wpisania danych do powyższych pól przy tworzeniu obiektu.
        public AgeBracketDeaths(int young, int old, int deathCount) {
            // Przypisujemy przekazaną dolną granicę do pola 'young'.
            this.young = young;
            // Przypisujemy przekazaną górną granicę do pola 'old'.
            this.old = old;
            // Przypisujemy przekazaną liczbę zgonów do pola 'deathCount'.
            this.deathCount = deathCount;
        }
    }
}