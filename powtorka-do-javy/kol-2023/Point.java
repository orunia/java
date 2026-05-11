// Krok 1. Definiujemy publiczną klasę Point. Będzie reprezentować współrzędne X i Y.
public class Point {
    // Definiujemy publiczne (widoczne wszędzie) i ostateczne (final - nie można ich zmienić po ustawieniu) pole dla osi X.
    public final double x;
    // Definiujemy analogiczne publiczne i ostateczne pole dla osi Y. Liczby są zmiennoprzecinkowe (double).
    public final double y;

    // Publiczny konstruktor - specjalna metoda wywoływana podczas tworzenia nowego punktu.
    // Przyjmuje dwa argumenty z zewnątrz (x oraz y).
    public Point(double x, double y) {
        // Przypisujemy argument x do pola klasy (słówko 'this' oznacza pole w tym obiekcie, a samo 'x' to argument).
        this.x = x;
        // Przypisujemy argument y do pola klasy y.
        this.y = y;
    }
}