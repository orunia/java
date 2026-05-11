// Wymagany element biblioteki czasu.
import java.time.LocalTime;

// Krok 8. Bazowa, abstrakcyjna klasa dla każdego patyka (wskazówki) kręcącego się na osi zegara.
public abstract class ClockHand {
    // Chroniona wartość rotacji wskazówki trzymana we wnętrzu w stopniach geometrycznych (np. 90 stopni to godzina 3:00).
    protected double angle;

    // Abstrakcyjna (czyli musi być zaprogramowana u dzieci) funkcja ustawiająca kat dla podanej godziny.
    public abstract void setTime(LocalTime time);
    
    // Abstrakcyjna funkcja plująca kodem do rysowania w formacie SVG.
    public abstract String toSvg();
}