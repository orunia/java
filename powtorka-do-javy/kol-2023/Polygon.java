// Importujemy Listę, aby móc przechowywać grupę punktów.
import java.util.List;

// Krok 2. Definiujemy klasę Polygon (wielokąt).
public class Polygon {
    // Tworzymy prywatną (dostępną tylko w tej klasie) listę przechowującą punkty (wierzchołki wielokąta).
    // Zgodnie z Krokiem 6. nie zmieniamy tego na protected, zostaje private!
    private List<Point> points;

    // Krok 2. Konstruktor ustawiający listę punktów przy tworzeniu wielokąta.
    public Polygon(List<Point> points) {
        // Zapisujemy przekazaną listę punktów w prywatnym polu klasy.
        this.points = points;
    }

    // Krok 3. Publiczna metoda zwracająca wartość logiczną (boolean - prawda/fałsz).
    // Służy do sprawdzenia, czy podany 'point' jest wewnątrz tego wielokąta.
    public boolean inside(Point point) {
        // Tworzymy zmienną (licznik) zgodnie z pseudokodem z polecenia. Zaczynamy od zera.
        int counter = 0;
        
        // Pętla przechodząca przez wszystkie punkty wielokąta. 'i' to indeks aktualnego punktu.
        for (int i = 0; i < points.size(); i++) {
            // Pobieramy pierwszy punkt krawędzi (pa) z listy na podstawie indeksu 'i'.
            Point pa = points.get(i);
            // Pobieramy drugi punkt krawędzi (pb). Dodajemy 1 do indeksu.
            // Znak modulo (%) sprawia, że dla ostatniego punktu 'i+1' przeskoczy z powrotem na indeks 0 (zamykając obwód).
            Point pb = points.get((i + 1) % points.size());

            // Jeżeli Y pierwszego punktu jest większe niż drugiego (sprawdzamy kierunek rysowania linii)...
            if (pa.y > pb.y) {
                // ...zamieniamy pa z pb miejscami. Najpierw zapisujemy pa do schowka (temp).
                Point temp = pa;
                // Wrzucamy pb w miejsce pa.
                pa = pb;
                // Wyciągamy stare pa ze schowka i wrzucamy jako pb.
                pb = temp;
            }

            // Jeżeli sprawdzany punkt na osi Y mieści się dokładnie pomiędzy Y punktu pa i pb.
            if (pa.y < point.y && point.y < pb.y) {
                // Obliczamy 'd' z pseudokodu, czyli różnicę położeń X krawędzi.
                double d = pb.x - pa.x;
                // Zmienna 'x', w której zapiszemy ostateczny punkt przecięcia promienia z krawędzią.
                double x;
                
                // Jeżeli d wynosi 0 (czyli krawędź jest idealnie pionowa).
                if (d == 0) {
                    // x zrównuje się z pozycją krawędzi.
                    x = pa.x;
                // W przeciwnym razie (krawędź pochylona):
                } else {
                    // Wyliczamy współczynnik 'a' prostej (różnica Y dzielona przez różnicę X).
                    double a = (pb.y - pa.y) / d;
                    // Wyliczamy współczynnik przesunięcia 'b' prostej.
                    double b = pa.y - a * pa.x;
                    // Wyliczamy punkt 'x' uderzenia promienia na podstawie 'y' naszego punktu.
                    x = (point.y - b) / a;
                }
                
                // Jeżeli wyliczony 'x' przecięcia leży po lewej stronie od 'x' naszego punktu...
                if (x < point.x) {
                    // Zwiększamy licznik (promień przebił ścianę).
                    counter++;
                }
            }
        }
        // Na sam koniec zwracamy prawdę (true), jeżeli licznik jest nieparzysty, w przeciwnym razie fałsz (false).
        // (counter % 2 != 0) dzieli przez 2 i sprawdza, czy zostaje reszta - to definicja nieparzystości.
        return counter % 2 != 0;
    }
}