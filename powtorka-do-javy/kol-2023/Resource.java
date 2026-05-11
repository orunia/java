// Krok 10. Publiczna klasa dla wszystkich surowców.
public class Resource {
    // Tworzymy tzw. enumerator (zestaw ściśle zdefiniowanych, zamrożonych typów), z opcjami Węgiel, Drewno, Ryby.
    public enum Type { Coal, Wood, Fish }

    // Dwie publiczne ostateczne wartości charakteryzujące złoże: punkt istnienia...
    public final Point point;
    // ...i jego typ geologiczny wyciągnięty z enuma.
    public final Type type;

    // Konstruktor do napełniania powyższych pół podczas fizycznego powstawania obiektu.
    public Resource(Point point, Type type) {
        this.point = point;
        this.type = type;
    }
}