import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

// Krok 12. Test zasobów.
public class CityTest {

    // Przygotowujemy sztuczną wyspę dla testów, żeby móc zadecydować czy miasto ma dostęp do wody.
    static Land testLand = new Land(Arrays.asList(
        new Point(0, 0), new Point(10, 0), new Point(10, 10), new Point(0, 10)
    ));

    // Tworzymy miasto bezpiecznie ukryte we wnętrzu lądu (środek 5,5, mur to 2 - czyli daleko mu do ścian lądu przy krawędziach X/Y rzędu 0 czy 10).
    static City inlandCity = new City(new Point(5, 5), "Ladowo", 2.0);
    // Tworzymy miasto brzegowe (środek 1,5, mur to 4 - czyli jego lewa krawędź znajdzie się na X=-1, wykraczając w wodę).
    static City portCity = new City(new Point(1, 5), "Portowo", 4.0);

    // Krok wymusza by metoda przygotowywała obiekty - inicjalizujemy w nich sprawdzanie portów na bazie powyższego lądu.
    static {
        inlandCity.checkAndSetPort(testLand);
        portCity.checkAndSetPort(testLand);
    }

    // Ta funkcja statyczna dostarcza (produkuje) zbiory argumentów (Arguments) dla wielokrotnego (Sparametryzowanego) testu.
    static Stream<Arguments> provideResourcesForTest() {
        return Stream.of(
            // Scenariusz 1: Miasto Lądowe próbuje zebrać węgiel obok siebie (5,6). Pownien się udać (true).
            Arguments.of(inlandCity, new Resource(new Point(5, 6), Resource.Type.Coal), true),
            
            // Scenariusz 2: Miasto Lądowe chce zebrać drewno obok (20,20) mając zasięg 10. Zbyt daleko. Spodziewamy się (false).
            Arguments.of(inlandCity, new Resource(new Point(20, 20), Resource.Type.Wood), false),
            
            // Scenariusz 3: Miasto Portowe próbuje łowić ryby z płytkiej wody (0,5). Spodziewamy się wielkiego sukcesu (true).
            Arguments.of(portCity, new Resource(new Point(0, 5), Resource.Type.Fish), true),
            
            // Scenariusz 4: Miasto Śródlądowe ma wodę za daleko i łowi w piasku. Operacja ma się zaciąć, więc spodziewamy się (false).
            Arguments.of(inlandCity, new Resource(new Point(5, 6), Resource.Type.Fish), false)
        );
    }

    // Zamiast @Test, piszemy że to test karmiący się metodą dostarczającą ('MethodSource'). Wskazujemy jako zasilanie nazwę naszej funkcji wyżej.
    @ParameterizedTest
    @MethodSource("provideResourcesForTest")
    public void testAddResourcesInRange(City city, Resource resource, boolean expectedToBeAdded) {
        // Czyścimy magazyny sprawdzanego miasta przed każdą nową iteracją by testy na siebie nie nakładały danych z tyłu głowy.
        city.resources.clear();
        
        // Zmuszamy miasto do wysłania karawany po listę dóbr (w której znajduje się testowy klocek) o zasięgu zbrojnym 10 jednostek drogi.
        city.addResourcesInRange(Arrays.asList(resource), 10.0);
        
        // Oceniamy, czy uzyskana w magazynach odpowiedź (obecność w secie zasobu, czyli true lub false) równa się naszym założeniom.
        assertEquals(expectedToBeAdded, city.resources.contains(resource.type));
    }
}