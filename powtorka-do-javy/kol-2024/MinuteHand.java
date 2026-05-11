import java.time.LocalTime;
import java.util.Locale;

// Krok 10.
public class MinuteHand extends ClockHand {

    @Override
    public void setTime(LocalTime time) {
        // Tutaj czas idzie "ruchem ciągłym" jak to napisano w poleceniu. 
        // Minutnik kręci się 6 stopni na minutę.
        // Ale dodajemy ułamek pochodzący z sekund! Dzięki "time.getSecond() / 60.0", w 30 sekundzie minutnik będzie 
        // nie sztywno na kresce, tylko minimalnie (0.5 krotności swojego stopnia) wychylony między minutami.
        angle = (time.getMinute() + time.getSecond() / 60.0) * 6;
    }

    @Override
    public String toSvg() {
        // Minutnik jest dłuższy od godziny (sięga y2=30), w miarę chudy (width=2) i np. szary lub czarny.
        return String.format(Locale.US, 
            "<line x1=\"100\" y1=\"100\" x2=\"100\" y2=\"30\" stroke=\"black\" stroke-width=\"2\" transform=\"rotate(%f 100 100)\" />", 
            angle);
    }
}