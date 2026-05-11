import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Krok 2. Definiujemy klasę dla produktów żywnościowych, oznajmiając jej pokrewieństwo poprzez dziedziczenie (extends) po Product.
public class FoodProduct extends Product {

    // W żywności układ cen zależy od regionu, stąd musimy użyć Słownika (Map).
    // Kluczem jest tekst (String) na nazwę województwa, a wartością - cała tablica cen z nim związana (Double[]).
    private Map<String, Double[]> pricesByProvince;

    // Prywatny konstruktor dla naszej fabryki plików, wymuszający super nazwę i wewnętrzny słownik mapy.
    private FoodProduct(String name, Map<String, Double[]> pricesByProvince) {
        // Super zrzuca odpowiedzialność zarejestrowania nazwy na wyższą klasę abstrakcyjną.
        super(name);
        // Podpięcie lokalnej tablicy Map utworzonej przez parser.
        this.pricesByProvince = pricesByProvince;
    }

    // Krok 2. Statyczna metoda analogiczna do zeszłej fabryki Csv dla niespożywczych.
    public static FoodProduct fromCsv(Path path) {
        try (Scanner scanner = new Scanner(path)) {
            // Jak nakazuje struktura pliku, zaczynamy od wczytania pierwszej od góry linii - nazwy produktu.
            String name = scanner.nextLine();
            // Ignorujemy kolejny wiersz będący nagłówkami lat.
            scanner.nextLine();

            // Tworzymy pustą instancję wirtualnego słownika, z którym połączymy wczytywane województwa.
            Map<String, Double[]> map = new HashMap<>();

            // Pętla odpalająca dopóki nasz wirtualny "palec" na kartce widzi niżej jakiś jakikolwiek nieprzeczytany wpis.
            while (scanner.hasNextLine()) {
                // Czytamy kolejny napotkany rządek.
                String line = scanner.nextLine();
                // Jeśli ten akurat wiersz jest po oczyszczeniu zaledwie "pusty", zignoruj i przejdź skrótem (continue) do następnego.
                if (line.trim().isEmpty()) continue;

                // Cięcie zapisu po średnikach.
                String[] parts = line.split(";");
                // W kolumnie pod indeksem 0 ukryto nazwę województwa - rejestrujemy je w osobnym tekstowym pliku.
                String prov = parts[0];

                // Identyczny proces konwersji ze starego parsera, pomijając jedynie że bierzemy komórki od pozycji 1 (za województwem).
                Double[] prices = Arrays.stream(parts, 1, parts.length)
                        .map(v -> v.replace(",", "."))
                        .map(Double::valueOf)
                        .toArray(Double[]::new);

                // Zakładamy teczkę do mapy - wpisując jej imię (województwo) po lewej stronie, oraz tablicę z obrobionymi już cenami po prawej.
                map.put(prov, prices);
            }
            // Zbudowanie gotowego kompletnego owocu z uzbrojonym zbiorem po mapowaniu wszystkich możliwych województw.
            return new FoodProduct(name, map);
        } catch (IOException e) {
            // Uziemienie kompilatora po wyskoczeniu nieoczekiwanej usterki odczytu.
            throw new RuntimeException(e);
        }
    }

    // Krok 2. Stworzenie nowej metody odczytującej - dopytująca o specyficzne województwo (stąd String na końcu).
    public double getPrice(int year, int month, String province) {
        // Kontrola bezpieczeństwa - jeżeli słownik (mapa) nigdy wcześniej nie widział na liście podanego argumentem "province"...
        if (!pricesByProvince.containsKey(province)) {
            // Ujawniamy usterkę przekroczenia.
            throw new IndexOutOfBoundsException();
        }
        
        // Zabezpieczamy kalendarz dat by nie wyciągać rekordów ponad podane ramy trwania 01.2010 - 03.2022.
        if (year < 2010 || year > 2022 || month < 1 || month > 12 || (year == 2022 && month > 3)) {
            throw new IndexOutOfBoundsException();
        }

        // Powtarzamy proces translacji daty na numer szuflady w podłużnej tablicy cen wczytanych z excela.
        int index = (year - 2010) * 12 + (month - 1);
        
        // Odblokowujemy dostęp do szuflad pobierając listę konkretnego zidentyfikowanego bezpiecznie obszaru (prowincji/woj).
        Double[] prices = pricesByProvince.get(province);
        
        // Finalna kontrola poprawności ułożenia dat do ramy uzyskanej z pliku długości rzędów.
        if (index < 0 || index >= prices.length) {
            throw new IndexOutOfBoundsException();
        }
        // Wypuszczenie znalezionej w danej kolumnie ceny na zewnątrz.
        return prices[index];
    }

    // Krok 2. Wdrażanie w życie nakazanej przez rodzica klasyki ogólnego doboru. Nadpisujemy bazę, która nie uwzględnia regionu.
    @Override
    public double getPrice(int year, int month) {
        // Kapsułka mająca sumować ceny znalezione ze wszystkich wygenerowanych obszarów administracyjnych w kraju.
        double sum = 0;
        // Wewnętrzny licznik zliczający ilość odwiedzonych jednostek (województw) by poprawnie określić w końcowym równaniu mianownik średniej.
        int count = 0;

        // Pętla załączająca (wyciągająca wszystkie odnalezione z lewej strony wpisy ze struktury słownika).
        for (String prov : pricesByProvince.keySet()) {
            // Prosimy program by odpalił powyżej napisaną precyzyjną instrukcję wskazując jej jako target kolejno pobrane z boku w pętli nazwy miast.
            sum += getPrice(year, month, prov);
            // Inkrementujemy (zwiększamy) ułamek mianownika po udanym zebraniu.
            count++;
        }
        
        // Zabezpieczenie przed błędem zera (jeśli program byłby pusty w pliku i nic by się nie dopięło).
        if (count == 0) return 0;
        
        // Według polecenia generujemy średnią arytmetyczną - odzyskujemy zebrany budżet podzielony przez sprawdzaną liczebność populacyjną próbki.
        return sum / count;
    }
}