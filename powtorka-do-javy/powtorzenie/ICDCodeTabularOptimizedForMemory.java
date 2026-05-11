// Znów sprowadzamy Scannera i klasę do odnajdowania ścieżek do pliku.
import java.util.Scanner;
import java.nio.file.Paths;
import java.io.IOException;

// Piszemy kolejną klasę, która także przysięga ('implements') zastosować wzór z ICDCodeTabular.
public class ICDCodeTabularOptimizedForMemory implements ICDCodeTabular {

    // Tutaj NIE tworzymy mapy/listy. Zapisujemy w klasie tylko i wyłącznie ścieżkę do pliku tekstowego (String).
    private String filePath;

    // Prosty konstruktor. Kiedy tworzymy obiekt tej klasy, podajemy ścieżkę, a on tylko przypisuje ją do powyższego pola.
    public ICDCodeTabularOptimizedForMemory(String path) {
        // Słówko 'this' zawsze używamy, by pokazać, że "ten filepath wewnątrz klasy równa się tamtemu z wejścia konstruktora".
        this.filePath = path;
    }

    // Ponownie uroczyście informujemy Javę, że metoda pochodzi z interfejsu i zostanie tutaj wcielona w życie.
    @Override
    public String getDescription(String code) throws IndexOutOfBoundsException {
        
        // Uruchamiamy blok try-catch żeby kontrolować odczyty z pliku na twardym dysku.
        try {
            // Tworzymy narzędzie Scanner i każemy mu otworzyć nasz zapisany wejściowy plik tekstowy.
            Scanner scanner = new Scanner(Paths.get(filePath));
            
            // Robimy dokładnie to samo co wcześniej – przeskakujemy 87 bezwartościowych początkowych linii (bo dane są od 88).
            for (int i = 0; i < 87; i++) {
                // Jeśli w pliku są w ogóle te linie.
                if (scanner.hasNextLine()) {
                    // Oczytaj linijkę i wrzuć ją od razu do wirtualnego śmietnika.
                    scanner.nextLine();
                }
            }
            
            // Zapuszczamy pętlę po pozostałych (właściwych) linijkach.
            while (scanner.hasNextLine()) {
                // Bierzemy na tapet kolejną linijkę.
                String line = scanner.nextLine().trim();
                
                // Upewniamy się, że struktura na początku linijki to faktycznie Kod ICD-10 wg wytycznych.
                if (line.matches("^[A-Z]\\d{2}.*")) {
                    // Kod musi zaczynać linię, po nim następuje spacja. Szukamy jej znów.
                    int spaceIndex = line.indexOf(" ");
                    
                    // Jeśli znaleźliśmy granicę między kodem a tekstem.
                    if (spaceIndex != -1) {
                        // Pobieramy kod z tej linijki, aby go skonfrontować z poszukiwanym.
                        String foundCode = line.substring(0, spaceIndex).trim();
                        
                        // Metoda 'equals' porównuje dwa teksty ze sobą. Sprawdzamy czy kod odczytany równa się poszukiwanemu ('code').
                        if (foundCode.equals(code)) {
                            // Jeśli obiekty się nakładają to mamy bingo!
                            // Wycinamy opis, wyrzucamy śmieci metodą trim() i zapisujemy.
                            String description = line.substring(spaceIndex).trim();
                            // Jako że znaleźliśmy co chcemy, kończymy pracę scannera zamykając go.
                            scanner.close();
                            // A następnie, za pomocą return, wychodzimy z metody natychmiast, zwracając światu odpowiedź.
                            return description;
                        }
                    }
                }
            }
            // Zabezpieczenie. O ile nie wyszliśmy poleceniem "return" z pętli, to w tym punkcie wciąż mamy włączony plik i otwarty scanner.
            // Skoro doszliśmy tak daleko (przeczytaliśmy plik do końca bez sukcesu), to znaczy że odpowiedź w nim nie istnieje.
            // Profilaktycznie gasimy światło - zamykamy scanner.
            scanner.close();
            
        } catch (IOException e) {
            // Jeśli wystąpił błąd odczytu pliku, też musimy to zameldować.
            e.printStackTrace();
        }

        // Pamiętasz obietnicę? Metoda musi zwrócić odpowiedź ALBO rzucić wyjątek. 
        // Skoro nie znaleźliśmy kodu (bądź wystąpił błąd wejścia/wyjścia), wypełniamy obietnicę i strzelamy wyjątkiem o przekroczonym zakresie.
        throw new IndexOutOfBoundsException();
    }
}