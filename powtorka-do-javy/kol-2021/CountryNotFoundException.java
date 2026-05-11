// Importujemy podstawową klasę Exception z Javy, aby nasz własny wyjątek mógł po niej dziedziczyć.
import java.lang.Exception;

// Krok 3. Definiujemy klasę wyjątku CountryNotFoundException. Dziedziczymy po Exception (a nie RuntimeException), 
// co sprawia, że jest to tzw. wyjątek sprawdzany (checked) i kompilator wymusi na nas jego przechwycenie[cite: 31].
public class CountryNotFoundException extends Exception {

    // Definiujemy publiczny konstruktor naszego wyjątku, który przyjmuje nazwę państwa, którego nie udało się odnaleźć.
    public CountryNotFoundException(String countryName) {
        
        // Słówko super wywołuje konstruktor klasy nadrzędnej (czyli Exception).
        // Przekazujemy mu nazwę państwa, dzięki czemu późniejsze wywołanie wbudowanej w Exception metody getMessage() 
        // zwróci dokładnie ten przekazany tekst z nazwą państwa[cite: 32].
        super(countryName);
    }
}