// Importujemy HashMape oraz Map, które pozwalają na bardzo szybkie znajdowanie danych za pomocą tzw. kluczy.
import java.util.HashMap;
import java.util.Map;
// Importujemy Scanner do czytania i Paths/IOException do plików.
import java.util.Scanner;
import java.nio.file.Paths;
import java.io.IOException;

// Definiujemy klasę, która w swojej deklaracji używa słówka 'implements ICDCodeTabular',
// co oznacza, że uroczyście obiecuje posiadać wszystkie metody z tego interfejsu.
public class ICDCodeTabularOptimizedForTime implements ICDCodeTabular {

    // Tworzymy prywatną mapę 'descriptions'. To jak wirtualny słownik, gdzie słowem (kluczem - String) 
    // jest kod, a definicją (wartością - String) jego opis.
    private Map<String, String> descriptions;

    // Konstruktor, przyjmujący ścieżkę do pliku z opisami kodów (String 'path').
    public ICDCodeTabularOptimizedForTime(String path) {
        // Tworzymy pustą instancję HashMapy. Gotowa do zapełniania.
        descriptions = new HashMap<>();
        
        // Zabezpieczamy blok na wypadek błędu ładowania pliku (try-catch).
        try {
            // Analogicznie do poprzedniej klasy tworzymy narzędzie do czytania (Scanner) ze ścieżki pliku.
            Scanner scanner = new Scanner(Paths.get(path));
            
            // W treści polecenia zapisano, że "Właściwe dane znajdują się od linii 88".
            // Czyli pierwsze 87 linii to śmieci. Tworzymy pętlę dla 87 przebiegów.
            for (int i = 0; i < 87; i++) {
                // Warunek sprawdzający czy faktycznie jest następna linia.
                if (scanner.hasNextLine()) {
                    // Odczytujemy linię, ale NIC z nią nie robimy. W ten sposób omijamy 87 niepotrzebnych linijek.
                    scanner.nextLine();
                }
            }
            
            // Mając za sobą nagłówki, tworzymy pętlę trwającą do wyczerpania reszty pliku.
            while (scanner.hasNextLine()) {
                // Zapisujemy pojedynczą linijkę.
                String line = scanner.nextLine();
                // Oczyszczamy ją z białych znaków (np. spacji) na początku i na końcu.
                line = line.trim();
                
                // Polecenie brzmi: "Kody ICD-10 zawsze rozpoczynają się od pojedynczej litery oraz dwóch cyfr".
                // Pomijamy kontynuacje i linie śmieciowe. Znowu używamy Wyrażenia Regularnego (regex).
                // Sprawdzamy czy linia zaczyna się od dużej litery i dwóch cyfr, a po nich spacja i jakikolwiek tekst.
                if (line.matches("^[A-Z]\\d{2}.*")) {
                    // Skoro tak, szukamy pierwszego "odstępu" (spacji), który rozdziela kod od opisu.
                    // Metoda indexOf zwraca pozycję pierwszej znalezionej spacji.
                    int spaceIndex = line.indexOf(" ");
                    
                    // Upewniamy się, że w ogóle jest spacja (indexOf zwraca -1 jak czegoś nie znajdzie).
                    if (spaceIndex != -1) {
                        // Wycinamy kod z tekstu, ucinając wszystko od początku do indeksu spacji (za pomocą substring).
                        String code = line.substring(0, spaceIndex).trim();
                        // Wycinamy opis, biorąc wszystko od spacji do samego końca (drugi wariant substring).
                        String description = line.substring(spaceIndex).trim();
                        // Wrzucamy nasz kod i opis do wirtualnego "słownika" (Mapy).
                        descriptions.put(code, description);
                    }
                }
            }
            // Zamykamy skaner.
            scanner.close();
            
        } catch (IOException e) {
            // Rysujemy komunikat w wypadku błędu IO, co zawsze jest bezpieczne i w dobrym guście.
            e.printStackTrace();
        }
    }

    // Aby zachować wierność względem interfejsu, MUSIMY dodać tu słówko '@Override', pokazując,
    // że nadpisujemy (wdrażamy) metodę pochodzącą z interfejsu.
    @Override
    public String getDescription(String code) throws IndexOutOfBoundsException {
        // Szukamy opisu w naszym słowniku (HashMapie) prosząc ją o oddanie wartości przypisanej pod klucz (code).
        String desc = descriptions.get(code);
        
        // Metoda 'get' oddaje 'null' (czyli nic, dosłowną próżnię), jeśli nie znajdzie kodu w słowniku.
        if (desc == null) {
            // Zgodnie z poleceniem, jeśli kodu tam nie ma, WYRzucamy (throw) nowy (new) wyjątek.
            throw new IndexOutOfBoundsException();
        }
        
        // Jeśli jednak opis nie był próżnią (kod znaleziony), zwracamy go w odpowiedzi.
        return desc;
    }
}