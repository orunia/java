import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

// Krok 8. Sprawdzanie poprawności wiadomości w wyjątku RuntimeException.
public class LandTest {

    @Test
    public void testCityOutsideLandThrowsException() {
        // Tworzymy kawałek wyspy pomiędzy (0,0) a (10,10).
        Land land = new Land(Arrays.asList(
            new Point(0, 0), new Point(10, 0), new Point(10, 10), new Point(0, 10)
        ));
        
        // Próbujemy wybudować miasto o środku (20,20) - czyli grubo w oceanie. Nazywamy je "Atlantyda".
        City city = new City(new Point(20, 20), "Atlantyda", 2.0);
        
        // To specjalna metoda testowa. Oczekuje (assertThrows), że podczas egzekucji podanego w ()-> kodu, wystrzeli RuntimeException.
        // Jeśli wystrzeli, złapie ten wyjątek i umieści w zmiennej 'exception'.
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            // Kod który jest wykonywany i musi zepsuć się na żądanie.
            land.addCity(city);
        });

        // Weryfikujemy ostatnią wytyczną. Czy w wyciągniętej z wyjątku wiadomości kryje się nazwa miasta?
        assertEquals("Atlantyda", exception.getMessage());
    }
}