// Import bibliotek na temat obróbki ścieżki i lokalizacji plikowej.
import java.nio.file.Paths;

// Tworzymy finalną instancję wykonawczą.
public class Main {
    // Standardowa reguła uruchomieniowa dla środowisk Java nakazana przez polecenie wejściowe.
    public static void main(String[] args) {
        try {
            // --- TEST KROK 1 --- (Sprawdzenie wyciągniętej metody z NonFood na obiekcie)
            System.out.println("--- TEST KROK 1 ---");
            // Ponieważ test Kroku 1 zakłada operacje na pliku zanim wykonamy Krok 3, musimy wgrać testowy plik ręcznie.
            // Zakładamy testowo z reguł zadania że pliki siedzą w pakiecie z "data" (stąd np. benzyna).
            NonFoodProduct testNonFood = NonFoodProduct.fromCsv(Paths.get("data/nonfood/benzyna.csv"));
            // Wywołujemy napisaną odświeżoną funkcję dla rocznika z zakresu (np 2011 luty).
            System.out.println("Cena za benzyne z 02.2011 to: " + testNonFood.getPrice(2011, 2));

            // --- TEST KROK 2 --- (Sprawdzenie nowo utworzonego podziału na prowincje na podklasie i średniej)
            System.out.println("\n--- TEST KROK 2 ---");
            // Budujemy referencyjny twór opierając na metodach dostępu analogicznie jak to było prędzej.
            FoodProduct testFood = FoodProduct.fromCsv(Paths.get("data/food/buraki.csv"));
            // Odpytujemy trójargumentową odnogę getPrice dedykowaną tylko i wyłącznie rejonizacyjnemu trybowi.
            System.out.println("Cena burakow w PODKARPACKIE 03.2010 to: " + testFood.getPrice(2010, 3, "PODKARPACKIE"));
            // Sprawdzamy dwuargumentowe przełamanie sprawdzające algorytm zliczania i poszukiwania wspólnego dzielnika prób.
            System.out.println("Cena z uzyciem sredniej arytmetycznej z 03.2010: " + testFood.getPrice(2010, 3));

            // --- TEST KROK 3 --- (Dodawanie produktów do globalnej abstrakcyjnej pamięci)
            System.out.println("\n--- TEST KROK 3 ---");
            // Na wejście obiektu ładujemy zapisaną w metodzie statyczną delegację "zrób ten odczyt sam" wraz z posadzką startową.
            Product.addProducts(NonFoodProduct::fromCsv, Paths.get("data/nonfood"));
            // Rozszerzamy pakiet pobierając dedykowany zestaw z odmienną obróbką mapowania prowincjonalnego.
            Product.addProducts(FoodProduct::fromCsv, Paths.get("data/food"));
            System.out.println("Produkty zaladowane do glownej listy poprawnie.");

            // --- TEST KROK 4 --- (Testowanie wyjątków zapytań o przedmioty po frazach startowych)
            System.out.println("\n--- TEST KROK 4 ---");
            // Przypadek 0 produktów:
            try {
                // Skoro nic nie jest wpisane, ma się posypać po brzegach OutOfBoundsException.
                Product.getProducts("NieIstniejacyWymysl");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Zlapano test 0 (IndexOutOfBoundsException) - nie ma takiego produktu.");
            }
            
            // Przypadek 1 produktu (szukamy buraków z załadowanej wcześniej paczki danych):
            try {
                // Po udanej inicjalizacji, wypisujemy go do kontrolnego wglądu.
                Product jednoznaczny = Product.getProducts("Buraki");
                System.out.println("Zlapano test 1 (Sukces) - Odnaleziono konkretnie 1 produkt: " + jednoznaczny.getName());
            } catch (Exception e) {}

            // Przypadek powyżej 1 produktu (zakładamy że na liście mamy dużo obiektów na B, np benzyna i buraki):
            try {
                // Strzał do puli spowoduje zaciągnięcie kliku zajawek po przedrostku "B".
                Product.getProducts("B");
            } catch (AmbigiousProductException e) {
                // Narzędzie drukowania wyjątków (stos) objawi listę ukrytą pod Super.
                System.out.println("Zlapano test >1 (AmbigiousProductException). Druk stosu błędów:");
                e.printStackTrace(System.out); // Strumien wyrzuca wydruk bledu w standardowe okno konsoli
            }

            // --- TEST KROK 5 --- (Wykreowanie na sucho koszyka zakupowego i analiza podwyżek cen)
            System.out.println("\n--- TEST KROK 5 ---");
            // Deklarujemy nasz własny pusty wózek.
            Cart wozek = new Cart();
            // Przechwytujemy wybrane, upewnione obiekty do zmiennych z puli list używając napisanych metod.
            Product benzyna = Product.getProducts("Benzyna"); // Zalozone istnienie na bazie skryptu "benzyna"
            Product buraki = Product.getProducts("Buraki");
            
            // Wkładamy w bagażnik 50 litrów paliwa i dorzucamy 10 paczek urobku buraczanego.
            wozek.addProduct(benzyna, 50);
            wozek.addProduct(buraki, 10);
            
            // Podliczamy zapłatę dla starych czasów używając getPrice.
            System.out.println("Wartosc wozka zakupionego 01.2011 to kwota w zl: " + wozek.getPrice(2011, 1));
            // Pobieramy analityczną wartość matematyczną określającą wzrost i utratę z wypracowanej na gotowo formuły.
            System.out.println("Poziom mierzalnej inflacji tego koszyka w latach miedzy 01.2011 a 12.2021 (%): " + 
                               wozek.getInflation(2011, 1, 2021, 12));

        } catch (Exception e) {
            // Tarcza awaryjna zabezpieczająca aplikację główną (Main) przed zgaśnięciem jeśli testowanie napotka np. braki z samym wejściem na dysk w ogóle.
            System.out.println("Brak dostepu do systemu plikow na sciezce wejsciowej. " + e.getMessage());
        }
    }
}