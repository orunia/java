// Narzędzie słownika użyte aby spinać razem produkt i jego zakupioną liczbę sztuk.
import java.util.HashMap;
import java.util.Map;

// Krok 5. Inicjujemy Koszyk do zadań domowych zdefiniowany w zadaniu Krok 5.
public class Cart {
    
    // Prywatna struktura na wzór portfela, z lewej kładziemy nasz wytarty z fabryk obiekt Produktu, a z prawej jego całkowitą zamówioną liczbę (Integer).
    private Map<Product, Integer> items = new HashMap<>();

    // Definicja wdrażająca metodę dodającą Produkt do zdefiniowanej wewnątrz torby (items).
    public void addProduct(Product product, int amount) {
        // "put" umieszcza element. Jeśli element był tam wrzucony prędzej, narzędzie "getOrDefault" wyciągnie poprzednią ilość i dołączy nową liczbę.
        items.put(product, items.getOrDefault(product, 0) + amount);
    }

    // Metoda wymuszająca sprawdzenie zsumowanej wartości po cennikach ze wskazanej daty.
    public double getPrice(int year, int month) {
        // Startujemy z portfelem pustym po brzegi (zmienna na 0).
        double total = 0;
        
        // Bierzemy pętlą wszystkie zawinięte obiekty razem ze spinaczem ilości (Map.Entry ułatwia iterowanie w całości par).
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            // Ładujemy w total (dodając sukcesywnie) przemnożoną cenę jednostkową z danej daty wyciągniętą od produktu (Key) * zakupiona ilość sztuk na rachunku (Value).
            total += entry.getKey().getPrice(year, month) * entry.getValue();
        }
        
        // Zwracamy podliczony stan rynkowy owego miesiąca.
        return total;
    }

    // Metoda przeliczająca rosnącą stopę zjawiska utraty wartości bazująca na zadanym z polecenia wzorze równania inflacyjnego w narzuconym wymiarze.
    public double getInflation(int year1, int month1, int year2, int month2) {
        // Wywołujemy lokalnie zapisaną uprzednio operację ściągając globalną taryfę we wskazanym starym punkcie na taśmie z lat i dni...
        double price1 = getPrice(year1, month1);
        // Po czym wyliczamy portfel zakupiony podczas nowszych, przyszłych realiów by ujrzeć dysproporcje obciążenia kwotowego.
        double price2 = getPrice(year2, month2);
        
        // Obliczamy ile odległości czasowej oddziela obie wyżej wymierzone próby by użyć jej w dolnym zapisie.
        int months = (year2 - year1) * 12 + (month2 - month1);
        
        // Literalnie narzucony w formacie formuły algorytm z Kroku 5 liczący stosunek różnicy do mianownika odzyskanej pierwotnej puli w kalendarzu.
        return (price2 - price1) / price1 * 100 / months * 12;
    }
}