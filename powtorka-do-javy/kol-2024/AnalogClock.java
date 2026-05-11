import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

// Krok 7. Zegar tarczowy. Oczywiście musi dziedziczyć z uniwersalnej platformy.
public class AnalogClock extends Clock {

    // Krok 11. Tworzymy polimorficzną, prywatną i finalną Listę (zawsze tą samą kolekcję) z typami ramy 'ClockHand'.
    private final List<ClockHand> hands = new ArrayList<>();

    // Konstruktor do budowy. Znowu pamiętamy o mieście!
    public AnalogClock(City city) {
        // Wbicie ustawień bazowych starej klasy w tle.
        super(city);
        
        // Dodajemy poszczególne wyprodukowane elementy mechaniczne na stos zegarka.
        hands.add(new HourHand());
        hands.add(new MinuteHand());
        hands.add(new SecondHand());
    }

    // Krok 7 i 11. Funkcja malująca grafikę SVG do pliku tekstowego na twardy dysk.
    public void toSvg(String path) {
        
        // KROK 11. ROZWIĄZANIE. Polecenie nakazało ożywić te wskazówki BEZ nadpisywania metod typu 'setCurrentTime'.
        // Skoro nie możemy przechwycić akcji wkładania czasu, rozwiązujemy to inaczej: uaktualniamy parametry kątów 
        // tuż przed narysowaniem! Pętla pyta każdą z rączek by użyła 'getTime' wbudowanego wyżej, po czym natychmiast uaktualnia swój promień kręcenia (angle).
        for (ClockHand hand : hands) {
            hand.setTime(this.getTime());
        }

        // Przygotowujemy sprawny notatnik do szybkiego budowania i klejenia długich tekstów (String Builder).
        StringBuilder svg = new StringBuilder();
        // Wypisujemy główny blok znaczników pliku .svg, opisującego np rozmiar przestrzeni (200 na 200).
        svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"200\" height=\"200\" viewBox=\"0 0 200 200\">\n");
        // Rysujemy obręcz wokół mechanizmu (circle) – to nasz cyferblat (tarcza bez ramion) z Kroku 7.
        svg.append("<circle cx=\"100\" cy=\"100\" r=\"90\" fill=\"none\" stroke=\"black\" stroke-width=\"2\"/>\n");
        
        // Dodajemy w to miejsce wektorowe opisy kształtów od samych wskazówek...
        for (ClockHand hand : hands) {
            svg.append(hand.toSvg()).append("\n");
        }
        
        // Na sam koniec zamykamy bezpiecznie klamrę blokującą plik wektorowy SVG.
        svg.append("</svg>");
        
        // Szykujemy strumień wejścia/wyjścia (I/O). Narzędzie PrintWriter to najprostszy sposób wrzucenia bloku tekstu i zrobienia zapisu jako plik.
        try (PrintWriter out = new PrintWriter(new File(path))) {
            // Sklej cały StringBuilder z góry używając do normalnego stringa i wklej na dysk.
            out.println(svg.toString());
        } catch (FileNotFoundException e) {
            // Jeśli wystąpi usterka ścieżki Windows, zaalarmuj terminal na dole okna IDE.
            e.printStackTrace();
        }
    }
}