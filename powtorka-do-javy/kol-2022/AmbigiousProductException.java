// Import potrzebny do odebrania w argumencie listy.
import java.util.List;

// Krok 4. Deklarujemy naszą osobistą klasę zgłaszającą błąd wieloznaczności (dziedziczy standardowe zachowanie z ogólnej klasy Exception).
public class AmbigiousProductException extends Exception {

    // Konstruktor nakazujący wymagać na starcie by podawać obiektom przekazany stos w formie Listy ze stringami.
    public AmbigiousProductException(List<String> productNames) {
        // Wedle zalecenia informującego program by pokazywał usterki formatując dowolnie w stosie błędów używamy Super-Więzi dziedzicznej, 
        // wysyłając scalony (z przedziałkiem przecinkowym przy pomocy String.join) rządek jako twardy komunikat wyrzuconej na pulpit usterki.
        super(String.join(", ", productNames));
    }
}