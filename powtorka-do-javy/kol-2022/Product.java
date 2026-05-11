// Importujemy narzędzia do pracy ze ścieżkami plików.
import java.nio.file.Files;
import java.nio.file.Path;
// Importujemy ArrayList i List do tworzenia dynamicznej tablicy wszystkich produktów.
import java.util.ArrayList;
import java.util.List;
// Importujemy Function – "obiekt funkcyjny", pozwalający przekazać w argumencie np. odwołanie do innej metody.
import java.util.function.Function;
// Importujemy strumienie, ułatwiające filtrowanie list i przeszukiwanie katalogów.
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Krok 1. Definiujemy klasę abstrakcyjną (nie można stworzyć jej bezpośredniego obiektu, służy za szablon).
public abstract class Product {

    // Krok 1. Tworzymy prywatne pole przechowujące nazwę produktu (przeniesione z NonFoodProduct).
    private String name;

    // Krok 3. Tworzymy prywatną, statyczną (czyli jedną wspólną dla całej klasy) listę przechowującą obiekty Product.
    private static List<Product> products = new ArrayList<>();

    // Konstruktor ustawiający nazwę dla klas dziedziczących, które będą z niego korzystać.
    public Product(String name) {
        // Przypisanie argumentu do pola klasy.
        this.name = name;
    }

    // Krok 1. Przeniesiony akcesor (getter), który zwraca publicznie ukrytą nazwę produktu.
    public String getName() {
        // Zwrócenie nazwy.
        return name;
    }

    // Krok 1. Publiczna, abstrakcyjna metoda. Nie ma ciała (wnętrza), zmusza klasy dziedziczące do jej napisania.
    // Oczekuje na przekazanie w argumencie roku i miesiąca, a w zamian ma oddać cenę (typ double z ułamkiem).
    public abstract double getPrice(int year, int month);

    // Krok 3. Statyczna metoda czyszcząca globalną listę produktów.
    public static void clearProducts() {
        // Używamy metody clear() na naszej statycznej liście "products", usuwając wszystkie jej elementy.
        products.clear();
    }

    // Krok 3. Metoda przyjmująca "obiekt funkcyjny" (twórcę produktu) oraz ścieżkę do katalogu.
    // Interfejs Function przyjmuje wejście typu Path i oddaje gotowy obiekt typu Product.
    public static void addProducts(Function<Path, Product> creator, Path dir) {
        // Try-with-resources pozwala otworzyć strumień plików z katalogu "dir" i zamknie go automatycznie na koniec.
        try (Stream<Path> paths = Files.list(dir)) {
            // Filtrujemy, żeby przetwarzać tylko zwykłe pliki (omijamy foldery), a dla każdego znalezionego pliku robimy to:
            paths.filter(Files::isRegularFile).forEach(path -> {
                // Wywołujemy naszą przekazaną funkcję (creator.apply) dając jej plik. Zwrócony produkt dodajemy do listy.
                products.add(creator.apply(path));
            });
        // Przechwytujemy i drukujemy ewentualne błędy przy pracy z plikami z dysku.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Krok 4. Metoda szukająca produktu po początku jego nazwy (prefiksu). Może zwrócić nasz własny wyjątek, stąd "throws".
    public static Product getProducts(String prefix) throws AmbigiousProductException {
        // Otwieramy strumień na głównej liście wszystkich produktów...
        List<Product> matched = products.stream()
            // Zostawiamy w strumieniu tylko te produkty, których nazwa (getName()) zaczyna się (startsWith) od podanego prefiksu.
            .filter(p -> p.getName().startsWith(prefix))
            // Przerabiamy strumień z powrotem na klasyczną Listę i zapisujemy do zmiennej "matched".
            .collect(Collectors.toList());

        // Krok 4: Jeśli nie znaleźliśmy ani jednego produktu pasującego do nazwy (lista jest pusta).
        if (matched.isEmpty()) {
            // Zgodnie z poleceniem rzucamy klasyczny wyjątek IndexOutOfBoundsException.
            throw new IndexOutOfBoundsException();
        }

        // Krok 4: Jeśli na liście znajduje się dokładnie jeden, jedyny produkt.
        if (matched.size() == 1) {
            // Zwracamy ten obiekt wydobywając go z zerowego indeksu naszej przefiltrowanej listy.
            return matched.get(0);
        }

        // Krok 4: Jeśli znaleziono więcej niż 1 produkt, tworzymy listę tekstową z samymi nazwami znalezionych konfliktów.
        List<String> names = matched.stream()
            // map() bierze każdy obiekt produktu i wyciąga z niego sam tekst nazwy (używając getName).
            .map(Product::getName)
            // Zapisujemy te nazwy do nowej Listy z tekstami.
            .collect(Collectors.toList());

        // Rzucamy nasz autorski wyjątek, podając mu w argumencie przygotowaną listę nakładających się na siebie nazw.
        throw new AmbigiousProductException(names);
    }
}