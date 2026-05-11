import java.time.LocalTime;
// Klasa do wymuszania amerykańskiego (kropkowego) dzielenia ułamków.
import java.util.Locale;

// Krok 9. Klasa dziedzicząca po ramie zrobionej krok wyżej.
public class SecondHand extends ClockHand {

    // Nadpisana implementacja wyliczania położenia.
    @Override
    public void setTime(LocalTime time) {
        // Dyskretnie, czyli skokowo: Pytamy LocalTime o to, jaka jest sekunda. 
        // Zegar kołowy ma 360 stopni. Skoro minut ma 60 sekund to 360/60 = 6 stopni na jedno cyknięcie.
        // Mnożymy to. Przykład: 15 sekunda * 6 stopni = 90 stopni (prosto w bok).
        angle = time.getSecond() * 6;
    }

    // Producent graficznego wycinka (kodu) dla tej wskazówki.
    @Override
    public String toSvg() {
        // Sekundnik: długi (y2="20" – zaledwie 20 pikseli od sufitu obrazka), cienki (width="1") i czerwony (red).
        // Transform rotate() nakazuje technologii SVG obrócić element wokół punktu centralnego (100,100).
        // String.format z Locale.US zapobiega pojawianiu się polskiego przecinka (90,5) dla ułamków - daje kropkę (90.5), by SVG się nie "popsuło".
        return String.format(Locale.US, 
            "<line x1=\"100\" y1=\"100\" x2=\"100\" y2=\"20\" stroke=\"red\" stroke-width=\"1\" transform=\"rotate(%f 100 100)\" />", 
            angle);
    }
}