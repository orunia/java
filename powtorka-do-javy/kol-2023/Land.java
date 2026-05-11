// Importujemy obsługę dynamicznych tablic oraz strumieni.
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Krok 5. Tworzymy klasę Land dziedziczącą po Polygon (rozszerzającą jego możliwości).
public class Land extends Polygon {

    // Krok 7. Prywatna lista miast, na początku tworzymy nową (pustą) ArrayListę gotową na wpisy.
    private List<City> cities = new ArrayList<>();

    // Krok 5. Taki sam konstruktor jak w Polygon, przyjmujący listę punktów brzegu lądu.
    public Land(List<Point> points) {
        // Słówko 'super' wysyła przekazaną listę punktów do oryginalnego konstruktora z klasy Polygon (by zbudował podstawę).
        super(points);
    }

    // Krok 7. Publiczna metoda służąca do rejestrowania miast w granicach tego lądu.
    public void addCity(City city) {
        // Wywołujemy własną odziedziczoną z Polygon metodę 'inside' i podajemy w argument środek (center) dodawanego miasta.
        // Wykrzyknik (!) odwraca logikę. Jeśli miasto NIE JEST wewnątrz lądu:
        if (!this.inside(city.center)) {
            // Rzucamy wyjątkiem błędu wykonania (RuntimeException), przekazując w argument wiadomość (nazwę miasta).
            throw new RuntimeException(city.getName());
        }
        
        // Krok 9. Wywołujemy w mieście specjalną metodę każąc mu sprawdzić względem Tego lądu (this), czy wystaje na wodę.
        city.checkAndSetPort(this);
        
        // Jeśli miasto bez błędu zmieściło się środkiem w lądzie, dodajemy je do wewnętrznej listy lądu.
        cities.add(city);
    }

    // Krok 16. Nadpisujemy domyślną metodę zmieniania obiektu w tekst.
    @Override
    public String toString() {
        // Z listy miast tworzymy "Strumień" (urządzenie do masowej przeróbki danych).
        return cities.stream()
            // Z każdego miasta w strumieniu wydobywamy jego tekst (wywołując toString z City).
            .map(City::toString)
            // Zbieramy wszystkie kawałki tekstów i łączymy je w jeden, rozdzielając wstawionym przecinkiem i spacją.
            .collect(Collectors.joining(", "));
    }
}