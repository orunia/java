Jasne, wyciągam resztę "mięsa" z Twoich zadań. Są to specyficzne algorytmy, operacje na węzłach (listy własnej roboty), matematyka pod geometrię i komunikacja z systemem (pliki i komendy).

Wklejaj i dostosowuj na kolokwium.

---

## 1. Algorytmy Geometryczne (z projektu SVG)

Często na kolokwiach pojawia się matematyka na obiektach. Tutaj masz gotowce dla punktów, odcinków i wielokątów.

### Długość odcinka (Pitagoras) i tworzenie odcinka prostopadłego

Zadanie ze środkiem odcinka i obrotem o 90 stopni.

```java
public class Segment {
    private Vec2 p1, p2;

    // Obliczanie odległości między dwoma punktami
    public double length() {
        // Math.hypot(dx, dy) to odpowiednik Math.sqrt(dx*dx + dy*dy)
        return Math.hypot(p2.x() - p1.x(), p2.y() - p1.y());
    }

    // Zwraca odcinek prostopadły, przecinający się w środku, o tej samej długości
    public Segment perpendicular() {
        // 1. Znajdź środek obecnego odcinka
        double cx = (p1.x() + p2.x()) / 2.0;
        double cy = (p1.y() + p2.y()) / 2.0;

        // 2. Różnice (wektor odcinka)
        double dx = p2.x() - p1.x();
        double dy = p2.y() - p1.y();

        // 3. Obrót wektora o 90 stopni (zamiana X i Y plus zmiana znaku)
        // Nowe punkty oddalone o połowę wektora obróconego od środka
        Vec2 newP1 = new Vec2(cx - dy/2.0, cy + dx/2.0);
        Vec2 newP2 = new Vec2(cx + dy/2.0, cy - dx/2.0);

        return new Segment(newP1, newP2);
    }
}

```

### Wyliczanie BoundingBox (Skrajne punkty - obwiednia)

Klasyczny algorytm szukania wartości min i max w tablicy jednocześnie. Przydaje się też do tworzenia klas `Record`.

```java
// Zwykły rekord (odpowiednik klasy z polami finalnymi i getterami)
public record BoundingBox(double x, double y, double width, double height) {}

public BoundingBox boundingBox() {
    // Ustawiamy wartości początkowe na pierwszy punkt tablicy
    double minX = points[0].x(), minY = points[0].y();
    double maxX = points[0].x(), maxY = points[0].y();

    // Szukamy najmniejszych i największych wartości X i Y
    for (Vec2 p : points) {
        if (p.x() < minX) minX = p.x();
        if (p.x() > maxX) maxX = p.x();
        if (p.y() < minY) minY = p.y();
        if (p.y() > maxY) maxY = p.y();
    }
    
    // Zwracamy pozycję (lewy górny róg) oraz rozmiar (max - min)
    return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
}

```

---

## 2. Operacje na Tablicach (Bez Kolekcji)

### Cykliczne nadpisywanie tablicy

Zadanie: "Jeżeli miejsce się wyczerpie, powinna zacząć nadpisywać wielokąty od początku". Częsty trick z użyciem operatora reszty z dzielenia (`%`).

```java
public class SvgScene {
    private Polygon[] polygons = new Polygon[3];
    private int currentIndex = 0;

    public void addPolygon(Polygon poly) {
        polygons[currentIndex] = poly;
        // Operator modulo '%' sprawia, że po indeksie 2, wzór (2+1)%3 zwraca 0.
        // Index kręci się: 0, 1, 2, 0, 1, 2...
        currentIndex = (currentIndex + 1) % polygons.length; 
    }
}

```

### Szukanie najdłuższego elementu w zwykłej tablicy

```java
public static Segment getLongest(Segment[] segments) {
    if (segments == null || segments.length == 0) return null;
    
    Segment longest = segments[0];
    for (int i = 1; i < segments.length; i++) {
        if (segments[i].length() > longest.length()) {
            longest = segments[i];
        }
    }
    return longest;
}

```

---

## 3. Implementacja własnej Listy (Struktury Danych)

Jeśli na kolokwium wpadnie zadanie z własną strukturą powiązaną węzłami (Wskaźniki/Node), to tu masz bazę pod jednokierunkową listę wiązaną, którą wklejasz w `CustomList`.

```java
public class CustomList<T> {
    
    // Wewnętrzna klasa Node
    private class Node {
        T data;
        Node next;
        Node(T data) { this.data = data; this.next = null; }
    }

    private Node head = null;
    private Node tail = null;

    // Dodawanie na początek O(1)
    public void addFirst(T value) {
        Node newNode = new Node(value);
        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head = newNode;
        }
    }

    // Dodawanie na koniec O(1) dzięki zmiennej tail
    public void addLast(T value) {
        Node newNode = new Node(value);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
    }

    // Zwracanie i usuwanie z początku O(1)
    public T removeFirst() {
        if (head == null) return null;
        T value = head.data;
        head = head.next;
        if (head == null) tail = null; // Jeśli lista stała się pusta
        return value;
    }
}

```

---

## 4. Komunikacja ze światem: Zapis plików i Procesy systemowe

Zadania z tworzeniem pliku `.svg` albo odpalaniem środowiska PlantUML prosto z Javy.

### Zapis do pliku tekstowego / SVG

Najprostsza, jednolinijkowa metoda z pakietu `java.nio.file`.

```java
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public void save(String filePath, String content) {
    try {
        // Tworzy plik lub go nadpisuje, wrzuca cały String i zamyka strumień
        Files.writeString(Paths.get(filePath), content);
    } catch (IOException e) {
        e.printStackTrace(); // Na kolokwium to wystarczy jako obsługa błędu
    }
}

```

### Uruchamianie zewnętrznych programów / komend (ProcessBuilder)

Zadanie z aplikacją PlantUML. `ProcessBuilder` pozwala odpalić np. plik `.jar` tak, jakbyś robił to w terminalu.

```java
import java.io.IOException;

public class PlantUMLRunner {
    private static String jarPath = "plantuml.jar"; // Domyslna sciezka

    public static void setJarPath(String path) {
        jarPath = path;
    }

    public static void generateDiagram(String dataPath, String outputDir, String fileName) {
        try {
            // Składamy komendę do terminala: java -jar plantuml.jar -o katalog_wyjscia nazwa_pliku
            ProcessBuilder builder = new ProcessBuilder(
                "java", "-jar", jarPath, "-o", outputDir, fileName
            );
            
            // start() uruchamia program w tle
            Process process = builder.start();
            process.waitFor(); // Czeka aż wygenerowanie obrazka się skończy
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

```



