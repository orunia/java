// Importujemy klasę LocalTime służącą do wygodnego przechowywania samego czasu (bez daty).
import java.time.LocalTime;
// Importujemy formater, aby móc ładnie wyświetlać czas w postaci tekstu.
import java.time.format.DateTimeFormatter;

// Krok 1. Tworzymy abstrakcyjną klasę zegara (nie można stworzyć "samego" zegara, musi być np. cyfrowy).
public abstract class Clock {

    // Tutaj przechowujemy wewnętrzny stan zegara. Wykorzystuję gotową klasę LocalTime z Javy.
    // Zmienna ma dostęp 'protected', żeby klasy dziedziczące mogły z niej korzystać.
    protected LocalTime time;
    
    // Krok 4. Dodajemy prywatne pole przetrzymujące referencję do obiektu miasta.
    private City city;

    // Krok 4. Modyfikujemy konstruktor, by na starcie wymagał określenia miasta.
    public Clock(City city) {
        // Przypisujemy miasto.
        this.city = city;
        // Na starcie ustawiamy czas na ten moment (inicjacja zapobiegająca błędom).
        this.time = LocalTime.now();
    }

    // Krok 1. Metoda ustawiająca czas na bieżącą godzinę z systemu komputera.
    public void setCurrentTime() {
        // LocalTime.now() pobiera dokładnie to, co pokazuje zegar Twojego Windowsa/Maca.
        this.time = LocalTime.now();
    }

    // Krok 1. Metoda do ręcznego ustawiania czasu, biorąca godzinę, minutę i sekundę.
    public void setTime(int h, int m, int s) {
        // Weryfikujemy poprawność podanych danych: godziny (0-23), minuty (0-59), sekundy (0-59).
        if (h < 0 || h > 23 || m < 0 || m > 59 || s < 0 || s > 59) {
            // Jeśli któraś zmienna jest zła, rzucamy błąd i (zgodnie z zadaniem) opisujemy dlaczego w argumencie.
            throw new IllegalArgumentException("Nieprawidlowy czas: h(0-23)=" + h + ", m(0-59)=" + m + ", s(0-59)=" + s);
        }
        // Jeśli wszystko jest OK, wgrywamy nowy czas używając metody 'of' z LocalTime.
        this.time = LocalTime.of(h, m, s);
    }

    // Krok 1. Metoda zwracająca tekstową reprezentację czasu.
    @Override
    public String toString() {
        // Tworzymy wzorzec (formater) oczekiwany w zadaniu: hh:mm:ss. (Wielkie HH oznacza tryb 24h z zerem z przodu).
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        // Przerabiamy nasz zapamiętany czas na tekst używając tego formatera.
        return time.format(formatter);
    }

    // Krok 4. Publiczna metoda służąca do przeprowadzki zegara do innego miasta.
    public void setCity(City newCity) {
        // Sprawdzamy czy zegar miał już jakieś stare miasto i czy nowe faktycznie istnieje.
        if (this.city != null && newCity != null) {
            // Obliczamy różnicę w strefach czasowych (nowa strefa minus stara strefa).
            int diff = newCity.getTimezone() - this.city.getTimezone();
            // Przesuwamy wirtualne wskazówki o wyliczoną różnicę godzin. 
            // LocalTime samo zadba o to, by 23:00 + 2h stało się 01:00 (funkcja plusHours).
            this.time = this.time.plusHours(diff);
        }
        // Podmieniamy naklejkę z tyłu zegara - od teraz należy do nowego miasta.
        this.city = newCity;
    }

    // Prosty akcesor (getter) przydatny dla klas rysujących, by mogły pobrać aktualny czas.
    public LocalTime getTime() {
        return time;
    }
}