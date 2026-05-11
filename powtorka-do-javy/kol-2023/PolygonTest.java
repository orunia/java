// Importujemy odpowiednie narzędzia z biblioteki JUnit 5 do testowania.
import org.junit.jupiter.api.Test;
// Importujemy funkcje ułatwiające porównywanie wyników (np. assertTrue).
import static org.junit.jupiter.api.Assertions.*;
// Importujemy pomocnika do prostego tworzenia list (Arrays.asList).
import java.util.Arrays;

// Klasa grupująca testy algorytmu wielokąta.
public class PolygonTest {

    // Krok 4. Metoda pomocnicza tworząca zawsze ten sam testowy kwadrat (od (0,0) do (10,10)).
    private Polygon createTestPolygon() {
        // Zwracamy nowy obiekt wielokąta wstawiając w argument szybką listę 4 punktów-rogów.
        return new Polygon(Arrays.asList(
            new Point(0, 0), new Point(10, 0), new Point(10, 10), new Point(0, 10)
        ));
    }

    // Znacznik @Test informuje środowisko, że poniższa metoda to test, który należy uruchomić.
    @Test
    public void testPointInside() {
        // Budujemy wielokąt testowy.
        Polygon poly = createTestPolygon();
        // Punkt w samym środku kwadratu (5, 5).
        Point p = new Point(5, 5);
        // Oczekujemy że metoda zwróci prawdę (True). Test przejdzie, jeśli tak będzie.
        assertTrue(poly.inside(p));
    }

    // Kolejny test.
    @Test
    public void testPointBelow() {
        // Budujemy wielokąt.
        Polygon poly = createTestPolygon();
        // Punkt (5, 15) znajduje się poniżej kwadratu (bo Y kwadratu kończy się na 10).
        Point p = new Point(5, 15);
        // Oczekujemy że metoda inside zwróci fałsz (False). Jeśli odda fałsz, test uznaje się za zdany.
        assertFalse(poly.inside(p));
    }

    // Trzeci test z polecenia.
    @Test
    public void testPointRight() {
        // Budujemy wielokąt.
        Polygon poly = createTestPolygon();
        // Punkt (15, 5) znajduje się całkowicie po prawej (X większe od 10).
        Point p = new Point(15, 5);
        // Spodziewamy się odpowiedzi negatywnej.
        assertFalse(poly.inside(p));
    }
}