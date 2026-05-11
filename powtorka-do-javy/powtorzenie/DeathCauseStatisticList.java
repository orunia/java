// Importujemy klasy List oraz ArrayList z pakietu java.util, by móc tworzyć dynamiczne listy obiektów.
import java.util.List;
import java.util.ArrayList;

// Importujemy narzędzia do czytania z plików (Scanner, Paths, IOException).
import java.util.Scanner;
import java.nio.file.Paths;
import java.io.IOException;

// Importujemy narzędzie Collectors, ułatwiające operacje na tzw. strumieniach (potrzebne do sortowania).
import java.util.stream.Collectors;

// Definiujemy publiczną klasę.
public class DeathCauseStatisticList {

    // Tworzymy prywatne pole przechowujące listę (dynamiczną strukturę danych) obiektów DeathCauseStatistic.
    private List<DeathCauseStatistic> statistics;

    // Konstruktor dla nowej listy statystyk. Wykonuje się samoistnie przy stworzeniu DeathCauseStatisticList.
    public DeathCauseStatisticList() {
        // Inicjujemy (tworzymy pustą) listę ArrayList, gotową do przyjmowania danych.
        this.statistics = new ArrayList<>();
    }

    // Publiczna metoda ładująca dane z pliku. Przyjmuje ścieżkę do pliku tekstowego w formie String.
    public void repopulate(String path) {
        // Przed załadowaniem nowych danych czyścimy listę ze wszystkich poprzednich wpisów, używając clear().
        statistics.clear();
        
        // Używamy bloku try-catch, który chroni program przed awarią, jeśli plik np. nie istnieje (błąd IO).
        try {
            // Tworzymy Scanner – bardzo przydatne narzędzie do czytania plików linijka po linijce. Podajemy mu ścieżkę.
            Scanner scanner = new Scanner(Paths.get(path));
            
            // Pętla while działa dopóki w pliku są kolejne linie (scanner.hasNextLine() zwraca 'prawda').
            while (scanner.hasNextLine()) {
                // Pobieramy całą linię z pliku do zmiennej typu tekstowego.
                String line = scanner.nextLine();
                
                // Zgodnie z poleceniem sprawdzamy, czy kod choroby jest poprawny (zaczyna się od litery i 2 cyfr).
                // Wykorzystujemy regex (wyrażenie regularne): "^[A-Z]\\d{2}.*"
                // ^ - początek, [A-Z] - jedna wielka litera, \\d{2} - dwie cyfry, .* - cokolwiek po nich.
                if (line.matches("^[A-Z]\\d{2}.*")) {
                    // Jeśli linia jest poprawna (to dane, a nie np. nagłówek), wywołujemy metodę z zadania 1
                    // na tej linijce, a utworzony obiekt dodajemy do naszej listy.
                    statistics.add(DeathCauseStatistic.fromCsvLine(line));
                }
            }
            // Zamykamy czytnik plików, bo skończyliśmy pracę. To bardzo ważny nawyk programistyczny.
            scanner.close();
            
        // 'catch' łapie błędy plikowe, np. gdyby ścieżka była zła.
        } catch (IOException e) {
            // Wypisujemy informacje o błędzie na ekranie konsoli, żeby wiedzieć, co poszło nie tak.
            e.printStackTrace();
        }
    }

    // Metoda z zadania 3 zwracająca n najbardziej śmiercionośnych chorób dla danego wieku.
    // Zwraca Listę obiektów DeathCauseStatistic. Przyjmuje wiek oraz liczbę n.
    public List<DeathCauseStatistic> mostDeadlyDiseases(int age, int n) {
        // Konwertujemy naszą listę w tak zwany 'Strumień' (Stream) – to jak taśmociąg pozwalający na łatwe filtrowanie i sortowanie.
        return statistics.stream()
            // Zmieniamy kolejność elementów (sortujemy). Używamy lambdy '(a, b) ->' by porównać dwa obiekty.
            .sorted((a, b) -> 
                // Wyciągamy klasę wiekową dla obiektu 'b' (czyli drugiego) i dla 'a' (pierwszego).
                // Z klas wyciągamy pole 'deathCount' i nakazujemy Javie je porównać za pomocą Integer.compare().
                // Ponieważ chcemy malejąco (najwięcej zgonów na szczycie), porównujemy b do a (a nie a do b).
                Integer.compare(b.getAgeBracket(age).deathCount, a.getAgeBracket(age).deathCount)
            )
            // .limit(n) obcina nasz posegregowany "taśmociąg" tak, by pobrać tylko określoną w zmiennej 'n' liczbę wyników.
            .limit(n)
            // Zamienia przetworzony strumień danych z powrotem w normalną Listę i zwraca z metody.
            .collect(Collectors.toList());
    }
}