import java.time.LocalTime;
import java.util.Locale;

// Krok 10.
public class HourHand extends ClockHand {

    @Override
    public void setTime(LocalTime time) {
        // Pełne koło (360) / 12h = 30 stopni przeskoku co jedną godzinę.
        // "time.getHour() % 12" gwarantuje, że godzina np. 15 wyliczy się jako 3 na cyferblacie.
        // Do tego płynnie dokładamy ułamek tego co wybił minutnik i same sekundy (dla precyzji do 1 sekundy, wedle poleceń).
        angle = ((time.getHour() % 12) + time.getMinute() / 60.0 + time.getSecond() / 3600.0) * 30;
    }

    @Override
    public String toSvg() {
        // Wskazówka od godziny: najkrótsza (y2=50), gruba jak parówka (width=4).
        return String.format(Locale.US, 
            "<line x1=\"100\" y1=\"100\" x2=\"100\" y2=\"50\" stroke=\"black\" stroke-width=\"4\" transform=\"rotate(%f 100 100)\" />", 
            angle);
    }
}