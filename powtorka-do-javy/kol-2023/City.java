// Importujemy metody do szybkiego tworzenia list oraz zbiory (Set).
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Krok 6. Klasa Miasta dziedzicząca po Wielokącie (Polygon).
public class City extends Polygon {
    // Krok 6. Publiczna, ostateczna zmienna punktu środkowego.
    public final Point center;
    // Krok 6. Prywatne pole tekstowe na nazwę miasta.
    private String name;
    // Dodatkowe, prywatne pole do zapisania wymiaru ścian, niezbędne później do weryfikacji dostępu do wody bez łamania prywatności punktów.
    private double wallLength;
    // Krok 9. Prywatne pole logiczne decydujące, czy miasto ma status portu.
    private boolean port;

    // Krok 11. Zbiór Set typu Resource.Type o tzw. dostępie pakietowym (czyli bez 'private' ani 'public').
    // Inicjujemy go pustym obiektem HashSet.
    Set<Resource.Type> resources = new HashSet<>();

    // Krok 6. Konstruktor budujący miasto. Przyjmuje środek, nazwę i rozpiętość kwadratowych murów.
    public City(Point center, String name, double wallLength) {
        // Aby klasa zbudowała poprawny nadrzędny Polygon z prywatnymi punktami (i ocaliła pełną punktację z polecenia),
        // musimy OD RAZU wyliczyć 4 rogi kwadratu i podać je wewnątrz polecenia 'super' do ojcowskiej klasy.
        super(Arrays.asList(
            // Lewy górny wierzchołek kwadratu. (X odjąć pół ściany, Y odjąć pół ściany).
            new Point(center.x - wallLength / 2, center.y - wallLength / 2),
            // Prawy górny wierzchołek.
            new Point(center.x + wallLength / 2, center.y - wallLength / 2),
            // Prawy dolny wierzchołek.
            new Point(center.x + wallLength / 2, center.y + wallLength / 2),
            // Lewy dolny wierzchołek.
            new Point(center.x - wallLength / 2, center.y + wallLength / 2)
        ));
        
        // Zapisujemy przypisane parametry do wnętrza nowo powstającego miasta.
        this.center = center;
        this.name = name;
        this.wallLength = wallLength;
    }

    // Publiczny akcesor (getter), żeby z zewnątrz można było odczytać przypisaną nazwę.
    public String getName() {
        return name;
    }

    // Krok 15. Potrzebuje tego metoda MapParser by później "dopisać" odnalezioną nazwę. Mutator (setter).
    public void setName(String name) {
        this.name = name;
    }

    // Krok 9 cd. Ta metoda znając siebie i dany ląd, sprawdza czy mury wystają za teren i na tej podstawie nadaje tytuł portu.
    public void checkAndSetPort(Land land) {
        // Połowa ściany to przydatny skrót.
        double half = wallLength / 2;
        // Rekonstruujemy w pamięci ulokowanie murów do sprawdzenia.
        Point p1 = new Point(center.x - half, center.y - half);
        Point p2 = new Point(center.x + half, center.y - half);
        Point p3 = new Point(center.x + half, center.y + half);
        Point p4 = new Point(center.x - half, center.y + half);
        
        // Jeśli CHOCIAŻ JEDEN z punktów ściany (krawędzi) NIE JEST w lądzie (leży na wodzie)...
        if (!land.inside(p1) || !land.inside(p2) || !land.inside(p3) || !land.inside(p4)) {
            // ...nadajemy wewnetrznej fladze portu status prawdy.
            this.port = true;
        } else {
            // W przeciwnym razie upewniamy się, że to zwykłe miasto.
            this.port = false;
        }
    }

    // Krok 11. Zasilanie magazynów miasta zasobami będącymi w rozsądnym zasięgu.
    public void addResourcesInRange(List<Resource> resourcesList, double range) {
        // Pętla pobierająca z Listy w argumencie każdy dostępny element 'r' typu Resource.
        for (Resource r : resourcesList) {
            // Obliczamy fizyczny dystans używając twierdzenia Pitagorasa (Math.hypot pobiera różnice obu osi i oddaje odległość w linii prostej).
            double distance = Math.hypot(r.point.x - center.x, r.point.y - center.y);
            
            // Jeżeli tak wyliczona odległość jest mniejsza bądź równa limitowi udźwigu (range).
            if (distance <= range) {
                // Jeśli zasób (r) jest typu Ryby, a do tego status miasta to (!port) "nie portowe"...
                if (r.type == Resource.Type.Fish && !this.port) {
                    // Instrukcja continue wyrywa nas z tego jednego etapu pętli, ignorując ryby i przechodząc szukać innej kopaliny.
                    continue;
                }
                // Jeśli nie wyrzuciło nas na rybach w górnym warunku, z czystym sumieniem dorzucamy do magazynu odczytany typ surowca.
                resources.add(r.type);
            }
        }
    }

    // Krok 16. Jak klasa ma być widoczna jako tekst.
    @Override
    public String toString() {
        // Zwracamy nazwę. Jeżeli port to 'true', dopisujemy po znaku zapytania ikonkę kotwicy. W przeciwnym razie po dwukropku dajemy puste "".
        return name + (port ? "⚓" : "");
    }
}