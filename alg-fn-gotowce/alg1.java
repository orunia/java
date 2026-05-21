Dobra, rozumiem sytuację. Zero lania wody, same konkrety do skopiowania na kolokwium. Masz tu kompletny, posortowany tematycznie zbiór gotowców ze wszystkich Twoich zadań.

Kopiuj, wklejaj, modyfikuj pod konkretne polecenie.

---

## 1. Wzorce Projektowe (Dekorator i Budowniczy)

To jest schemat, który wklejasz, gdy w zadaniu każą "dodawać nowe cechy do obiektu bez modyfikacji jego klasy" (Dekorator) lub "zbudować złożony obiekt krok po kroku" (Budowniczy).

### Wzorzec Dekorator (Decorator)

```java
// 1. BAZOWY INTERFEJS (w zadaniach to było Shape)
public interface Component {
    String generate(String attributes);
}

// 2. BAZOWY DEKORATOR (wklejasz go zawsze, przyjmuje interfejs i go implementuje)
public abstract class BaseDecorator implements Component {
    protected Component decoratedComponent;

    public BaseDecorator(Component decoratedComponent) {
        this.decoratedComponent = decoratedComponent;
    }

    @Override
    public String generate(String attributes) {
        return decoratedComponent.generate(attributes); // Przekazuje wywołanie dalej
    }
}

// 3. KONKRETNY DEKORATOR (np. dodający kolor wypełnienia lub obrys)
public class ColorDecorator extends BaseDecorator {
    private String color;

    public ColorDecorator(Component decoratedComponent, String color) {
        super(decoratedComponent);
        this.color = color;
    }

    @Override
    public String generate(String attributes) {
        // Dodaje swoją cechę i wywołuje metodę z BaseDecorator
        String newAttributes = String.format("color=\"%s\" %s", color, attributes);
        return super.generate(newAttributes);
    }
}

```

### Wzorzec Budowniczy (Builder) z płynnym interfejsem

Używasz tego, gdy w poleceniu masz metody, które dodają parametry i można je łączyć w łańcuch (`.translate().rotate().build()`).

```java
public class TransformationDecorator extends BaseDecorator {
    private String transformChain;

    private TransformationDecorator(Component component, String transformChain) {
        super(component);
        this.transformChain = transformChain;
    }

    // Wewnętrzna, statyczna klasa Builder
    public static class Builder {
        private StringBuilder builderTransform = new StringBuilder();

        // Metody budownicze ZAWSZE zwracają 'this'
        public Builder translate(double dx, double dy) {
            builderTransform.append(String.format(Locale.US, "translate(%f, %f) ", dx, dy));
            return this;
        }

        public Builder rotate(float angle) {
            builderTransform.append(String.format(Locale.US, "rotate(%f) ", angle));
            return this;
        }

        // Metoda kończąca - zwraca gotowy obiekt opakowany w dekorator
        public TransformationDecorator build(Component component) {
            return new TransformationDecorator(component, builderTransform.toString());
        }
    }
}

```

---

## 2. Kolekcje, Mapy i Varargs (Zarządzanie grupą obiektów)

Gotowce do zadań typu "Napisz klasę Family/Grupa", która zarządza elementami, dopuszcza duplikaty, albo przyjmuje nieskończoną liczbę argumentów.

### Zaawansowana Mapa (Grupowanie kluczy) i Varargs

```java
import java.util.*;

public class GroupManager {
    // Mapa: Klucz to String (np. Imię i Nazwisko), Wartość to LISTA obiektów (obsługa duplikatów)
    private Map<String, List<Person>> members = new HashMap<>();

    // VARARGS: Przyjmuje od 0 do nieskończoności obiektów Person (Person... people)
    public void add(Person... people) {
        for (Person p : people) {
            // MAGICZNA FUNKCJA NA KOLOKWIUM: 
            // computeIfAbsent sprawdza czy klucz istnieje. Jak nie -> tworzy pustą listę. Na koniec dodaje element.
            members.computeIfAbsent(p.getFullName(), k -> new ArrayList<>()).add(p);
        }
    }

    // Zwracanie posortowanej tablicy (lub listy) na podstawie klucza
    public Person[] get(String key) {
        List<Person> found = members.get(key);
        if (found == null) return new Person[0]; // Zabezpieczenie przed NullPointerException

        List<Person> copy = new ArrayList<>(found); // Kopia, żeby nie zepsuć oryginału
        Collections.sort(copy); // Wymaga, by Person implementowało Comparable (patrz niżej)
        return copy.toArray(new Person[0]); // Konwersja Listy na Tablicę
    }
}

```

### Interfejs Comparable (Naturalne sortowanie)

Do wklejenia w klasie (np. `Person`), żeby można było używać `Collections.sort(lista)` albo `Collections.max(zbior)`.

```java
// Klasa MUSI deklarować implements Comparable<NazwaKlasy>
public class Person implements Comparable<Person> {
    private LocalDate birthDate;

    // Nadpisujemy compareTo
    @Override
    public int compareTo(Person other) {
        // Zwraca < 0 jeśli this jest "mniejszy" (wcześniejszy)
        // Zwraca 0 jeśli równe
        // Zwraca > 0 jeśli "większy"
        return this.birthDate.compareTo(other.birthDate);
    }
}

```

---

## 3. Przetwarzanie Strumieniowe (Streams API)

To musisz umieć na blachę, jeśli w zadaniu każą filtrować, sortować lub grupować "listę wejściową" na "listę wyjściową".

```java
import java.util.*;
import java.util.stream.*;

public class StreamUtils {

    // 1. FILTROWANIE PO NAPISIE (np. zawiera substring)
    public static List<Person> filterByName(List<Person> list, String substring) {
        return list.stream()
                .filter(p -> p.getFullName().contains(substring))
                .collect(Collectors.toList());
    }

    // 2. SORTOWANIE (korzystając z gettera, np. po roku)
    public static List<Person> sortByYear(List<Person> list) {
        return list.stream()
                // Comparator.comparing wyciąga klucz do sortowania
                .sorted(Comparator.comparing(p -> p.getBirthDate().getYear()))
                .collect(Collectors.toList());
    }

    // 3. FILTROWANIE + ZŁOŻONE SORTOWANIE MALEJĄCO
    // Zwraca zmarłych posortowanych malejąco według długości życia
    public static List<Person> getDeceasedSortedByLifespan(List<Person> list) {
        return list.stream()
                .filter(p -> p.getDeathDate() != null) // Zostaw tylko zmarłych
                // Zwykłe sortowanie rosnące robi Comparator.comparing. 
                // Do malejącego dodajemy .reversed() lub piszemy (p1, p2) -> Long.compare(w2, w1)
                .sorted((p1, p2) -> Long.compare(p2.getLifespan(), p1.getLifespan())) 
                .collect(Collectors.toList());
    }

    // 4. ZNALEZIENIE JEDNEGO EKSTREMUM (np. najstarsza ŻYJĄCA osoba)
    public static Person getOldestLiving(List<Person> list) {
        return list.stream()
                .filter(p -> p.getDeathDate() == null) // żyjący
                // min() szuka wg komparatora. Ponieważ starszy ma "mniejszą" datę urodzenia, używamy daty.
                .min(Comparator.comparing(Person::getBirthDate))
                .orElse(null); // Zwróć null, jeśli lista jest pusta
    }
}

```

---

## 4. Parsowanie Plików, Wyjątki i Metody Wytwórcze

Ten schemat wklejasz, gdy każą "wczytać z CSV" i rzucić wyjątek, "gdy coś się nie zgadza".

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// Własny wyjątek (RuntimeException nie wymaga pisania "throws" wszędzie w kodzie)
public class NegativeLifespanException extends RuntimeException {
    public NegativeLifespanException(String message) {
        super(message);
    }
}

public class CsvParser {
    
    // Metoda wytwórcza - przetwarza jedną linijkę
    public static Person fromCsvLine(String line) {
        // line.split limit -1 zapobiega ucinaniu pustych elementów na końcu linii
        String[] columns = line.split(",", -1); 
        
        String name = columns[0];
        LocalDate birth = LocalDate.parse(columns[1]);
        // Jeśli pole jest puste -> null, jak nie -> parsujemy
        LocalDate death = columns[2].isEmpty() ? null : LocalDate.parse(columns[2]);

        // Walidacja i Rzucanie własnego wyjątku
        if (death != null && death.isBefore(birth)) {
            throw new NegativeLifespanException("Błąd: śmierć przed urodzeniem dla " + name);
        }

        return new Person(name, birth, death);
    }

    // Metoda wczytująca cały plik za pomocą strumieni
    public static List<Person> fromCsv(String filePath) {
        try {
            return Files.lines(Paths.get(filePath)) // Czyta linie z pliku jako Stream<String>
                    .map(CsvParser::fromCsvLine)    // Każdą linię przepuszcza przez metodę wytwórczą
                    .collect(Collectors.toList());  // Zbiera do listy
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}

```

---

## 5. Generyki (Typy Szablonowe) i Refleksja (`CustomList`)

Gotowce do zadan z `<T>`, sprawdzaniem klas (`Class<?>`) i własnymi listami iterowalnymi.

### Filtrowanie obiektów po typie (Refleksja)

```java
// Metoda statyczna szablonowa <T>
public static <T> List<T> filterByClass(List<T> list, Class<?> targetClass) {
    return list.stream()
            // isAssignableFrom sprawdza, czy 'targetClass' jest NADKLASĄ dla 'element.getClass()'
            // (czyli czy element dziedziczy po targetClass)
            .filter(element -> targetClass.isAssignableFrom(element.getClass()))
            .collect(Collectors.toList());
}

```

### Konfiguracja własnej kolekcji żeby wspierała pętle `for-each` i strumienie

```java
import java.util.AbstractList;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

// Dziedziczy po AbstractList żeby mieć połowę metod za darmo
public class CustomList<T> extends AbstractList<T> {
    // Zależnie od zadania tu są Node'y

    @Override
    public int size() {
        return 0; // Tu dajesz zmienną z rozmiarem
    }

    @Override
    public T get(int index) {
        return null; // Tu szukanie węzła po indeksie
    }

    // Implementacja własnego Iteratora
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            // private Node current = head;
            
            @Override
            public boolean hasNext() {
                return false; // return current != null;
            }

            @Override
            public T next() {
                // T val = current.value; current = current.next; return val;
                return null;
            }
        };
    }

    // Dodanie metody .stream() do własnej kolekcji
    public Stream<T> stream() {
        // Tworzy strumień na podstawie iteratora
        return StreamSupport.stream(this.spliterator(), false);
    }
}

```

---

## 6. Przekazywanie funkcji (Interfejsy `Function` i `Predicate`)

Używasz tego, gdy w zadaniu każą zmodyfikować funkcję tak, by "wywołała funkcję przekazaną w parametrze".

```java
import java.util.function.Function;
import java.util.function.Predicate;

public class FunctionProcessor {

    // Argumenty to interfejsy funkcyjne wbudowane w Javę.
    // Function<Wejscie, Wyjscie> - służy do przetwarzania
    // Predicate<Wejscie> - służy do sprawdzania warunku (zwraca boolean)
    public static void processLines(List<Person> people, Function<String, String> postProcess, Predicate<Person> condition) {
        for (Person p : people) {
            // Predicate używamy wywołując .test()
            if (condition.test(p)) {
                String originalText = p.toPlantUML();
                // Function używamy wywołując .apply()
                String modifiedText = postProcess.apply(originalText);
                System.out.println(modifiedText);
            }
        }
    }
}

// ---- JAK TEGO UŻYWAĆ W MAIN() ----
// processLines(
//     osoby, 
//     napis -> napis.replace("color", "yellow"), // Wyrażenie lambda dla Function (postProcess)
//     osoba -> osoba.getBirthDate().getYear() > 2000 // Wyrażenie lambda dla Predicate (condition)
// );

```

### Predykat "na przedział" jako metoda i Komparator "rozmiarów"

Zadanie 5 i 6 z Laboratorium:

```java
// Tworzenie Predykatu dla przedziału otwartego
public static <T extends Comparable<T>> Predicate<T> inOpenInterval(T min, T max) {
    // Zwraca true jeśli val jest większe od min (>0) I mniejsze od max (<0)
    return val -> val.compareTo(min) > 0 && val.compareTo(max) < 0;
}

// Liczenie elementów listy spełniających Predykat
public static <T> long countMatching(List<T> list, Predicate<T> condition) {
    return list.stream().filter(condition).count();
}

// Komparator porównujący sumę elementów w dwóch kolekcjach liczb
public static Comparator<Collection<? extends Number>> sumComparator() {
    return (col1, col2) -> {
        double sum1 = col1.stream().mapToDouble(Number::doubleValue).sum();
        double sum2 = col2.stream().mapToDouble(Number::doubleValue).sum();
        return Double.compare(sum1, sum2);
    };
}
```</T>

```