// Importy błędów wejścia-wyjścia, ścieżek, narzędzi do tablic i czytania z dysku.
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;

// Krok 1. Dodajemy "extends Product", informując Javę, że towar niespożywczy to rodzaj Produktu (i będzie dziedziczył jego cechy).
public class NonFoodProduct extends Product {
    // Pozostało pole na ceny towaru dla kolejnych miesięcy, zaczynając od stycznia 2010.
    // (Pole name usunięto, bo znajduje się teraz w klasie nadrzędnej Product).
    Double[] prices;

    // Prywatny konstruktor.
    private NonFoodProduct(String name, Double[] prices) {
        // Słówkiem 'super' zmuszamy klasę nadrzędną (Product), by ustawiła nazwę za nas w swoim prywatnym polu.
        super(name);
        // Przypisujemy tablicę z cenami do tutejszego pola klasy.
        this.prices = prices;
    }

    // Pozostawiona z zadania publiczna, statyczna metoda wytwarzająca obiekty z pliku.
    public static NonFoodProduct fromCsv(Path path) {
        String name;
        Double[] prices;
        try {
            // Tworzymy narzędzie ułatwiające czytanie pliku tekstowego ścieżka po ścieżce.
            Scanner scanner = new Scanner(path);
            // Pierwsza linia zawiera nazwę, zapisujemy ją do zmiennej.
            name = scanner.nextLine(); 
            // Drugą linię pobieramy, ale nigdzie nie zapisujemy - ignorujemy ją, tak jak każe polecenie.
            scanner.nextLine(); 
            
            // Trzecią linię dzielimy podając jako rozdzielacz średnik (;)
            prices = Arrays.stream(scanner.nextLine().split(";")) 
                    // Pozbywamy się z podziałów polskich przecinków zamieniając je na kropki rozumiane przez Javę jako dziesiętne.
                    .map(value -> value.replace(",",".")) 
                    // Zamieniamy tekstowy zapis na typ wymierny ułamkowy Double.
                    .map(Double::valueOf) 
                    // Wyniki spakowujemy do nowej tablicy Double[].
                    .toArray(Double[]::new); 

            // Czystość pracy – gdy skończyliśmy czytać, wyłączamy Scanner.
            scanner.close();

            // Zwracamy nowo wytworzony model w oparciu o zebrane właśnie zmienne.
            return new NonFoodProduct(name, prices);

        } catch (IOException e) {
            // Jeśli wystąpi błąd z załączaniem pliku, zatrzymujemy działanie rzucając "awarię programu".
            throw new RuntimeException(e);
        }
    }

    // Krok 1. Wdrażamy (nadpisujemy) metodę, którą wymusiła na nas nadrzędna klasa abstrakcyjna Product.
    @Override
    public double getPrice(int year, int month) {
        // Sprawdzamy, czy podany termin mieści się w akceptowalnych w zadaniu ramach (2010-2022) lub (1-12 miesiąc).
        if (year < 2010 || year > 2022 || month < 1 || month > 12) {
            // Jeśli podano głupoty w czasie - rzucamy błąd w poszukiwaniu.
            throw new IndexOutOfBoundsException();
        }
        // Ubezpieczamy jeszcze miesiące, bo wedle polecenia, dane w plikach sięgają tylko "do marca 2022 roku".
        if (year == 2022 && month > 3) {
            // Skoro tak, kwiecień 2022 nie istnieje - znów błąd indeksu.
            throw new IndexOutOfBoundsException();
        }

        // Musimy wiedzieć, o którą komórkę tablicy odpytać. Styczeń 2010 to indeks 0.
        // Od podanego roku odejmujemy 2010 i mnożymy wynik przez 12 by dostać paczkę z lat. Do paczki dodajemy aktualny miesiąc (odjęty o 1 z powodu natury indeksu od zera).
        int index = (year - 2010) * 12 + (month - 1);

        // Bezpiecznik: jeśli wyliczony indeks przekracza rozmiary faktycznie pobranej z pliku tablicy cen, informujemy błedem.
        if (index < 0 || index >= prices.length) {
            throw new IndexOutOfBoundsException();
        }

        // Zwracamy z naszej tablicy cenę kryjącą się pod wyliczonym właśnie ułożeniem w czasie.
        return prices[index];
    }
}