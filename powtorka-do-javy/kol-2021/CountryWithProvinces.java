// Narzędzia do daty z Javy.
import java.time.LocalDate;
// Tworzymy pustą instancję by nie wyrzucać błędów przy zapytaniach pętli.
import java.util.ArrayList;
import java.util.List;

// Krok 1. Tworzymy drugą, tym razem państwową pochodną z prowincjami wyłaniającą się ze struktury nadrzędnej[cite: 16].
public class CountryWithProvinces extends Country {

    // Krok 1. Deklaracja prywatnej struktury "prowincji" zdefiniowanej jako stały (niedotykany po inicjacji) zbiór obiektów typu Country[cite: 19].
    private final Country[] provinces;

    // Krok 1. Publiczny dedykowany kreator klasy wywoływany z argumentem twardego pola nazwy kraju i już wypełnioną listą jego dzieci[cite: 20].
    public CountryWithProvinces(String name, Country[] provinces) {
        // Wykonanie przymusowej, odgórnej instrukcji klasyfikowania samego siebie po nazwie do nadrzędnych właściwości rodzica.
        super(name);
        // Twarde i bezpowrotne przypisanie dostarczonej mu kolekcji elementów jako pole wewnętrzne obiektu[cite: 20].
        this.provinces = provinces;
    }

    // Krok 7. Nadpisywana obligatoryjna implementacja operacji odczytu, o zapytaniu wymuszonym przez klucz (parametru argumentu) daty[cite: 54, 56].
    @Override
    public int getConfirmedCases(LocalDate date) {
        // Przygotowujemy sztuczny akumulator sumujący wszystkie wyciągnięte do tej pory pomniejsze wartości.
        int sum = 0;
        // Ustanawiamy zautomatyzowaną pętlę po tablicy iterującą nad całą wciągniętą kolekcją provinces.
        for (Country prov : provinces) {
            // Dodajemy do wewnętrznego licznika to co usłyszymy (wywołując się rekurencyjnie u dzieci) o zachorowaniach i połykając do sum[cite: 58].
            sum += prov.getConfirmedCases(date);
        }
        // Wynik ostatecznej całości zwracamy jako twardy i bezwzględny stan całego państwa.
        return sum;
    }

    // Krok 7. Wdrażanie po raz drugi wymuszonej reguły wirtualnej dotyczącej liczenia ilości zejść[cite: 54, 56].
    @Override
    public int getDeaths(LocalDate date) {
        // Wdrażamy akumulującą szufladkę.
        int sum = 0;
        // Zapętlamy operacje odczytu poszczególnych wskaźników dzieci na rzecz całości.
        for (Country prov : provinces) {
            // Analogicznie, agregujemy żądając w zapytaniu (rekurencyjnie w głąb obiektu) zliczenia wyników tego samego konkretnego parametru date[cite: 58].
            sum += prov.getDeaths(date);
        }
        // Wyrzucamy pełną akumulację na przód państwa.
        return sum;
    }

    // Ponowne zapętlenie wymuszonej procedury pobierania wskaźników czasu, przygotowujące do implementacji kroku 9.
    @Override
    public List<LocalDate> getAvailableDates() {
        // Sprawdzamy czy podłączone państwo posiada absolutnie jakąkolwiek wewnętrzną infrastrukturę od której da się ściągnąć dane o czasie.
        if (provinces.length > 0) {
            // Wykorzystujemy strukturę podłączeń wymuszając wyciągnięcie osi czasu operując w głąb abstrakcyjnej tablicy indeksu "0".
            // Skoro to państwo główne (i plik CSV idzie równo w rzędach), wystarczy pobrać oś czasu z jednej z prowincji by posiadać listę.
            return provinces[0].getAvailableDates();
        }
        // Jeżeli zawiódł główny system, wydajemy absolutnie pustą listę ubezpieczając główny algorytm pętli zapisu pliku przed awarią.
        return new ArrayList<>();
    }
}