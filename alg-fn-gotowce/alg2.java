Racja, sama teoria na kolokwium z programowania to za mało xD.

Oto konkretne implementacje w kodzie (tzw. "gotowce") na podstawie Twoich zadań, które pokrywają najważniejsze mechanizmy. To Twoja ściągawka z kodu.

## 1. Zaawansowana obsługa Mapy i metody wariadyczne (Varargs)

Zadanie z klasą `Family`, gdzie używamy `Map<String, List<Person>>` i dodajemy wiele osób naraz. Metoda `computeIfAbsent` to absolutny klasyk na kolokwiach, żeby uniknąć sprawdzania `if (map.containsKey(...))`.

```java
public void add(Person... people) {
    for (Person person : people) {
        // computeIfAbsent tworzy nową pustą listę, jeśli klucza nie ma, a potem dodaje do niej osobę
        this.members.computeIfAbsent(person.getFullName(), k -> new ArrayList<>()).add(person);
    }
}

```

## 2. Interfejs Comparable (Naturalne sortowanie)

Zadanie z klasą `Person`, aby można było używać `Collections.sort()` bez pisania dodatkowych komparatorów.

```java
public class Person implements Comparable<Person> {
    private LocalDate birthDate;
    
    // ... reszta klasy ...

    @Override
    public int compareTo(Person other) {
        // Sortowanie od najstarszego: wcześniejsza data urodzenia = mniejszy obiekt
        return this.birthDate.compareTo(other.birthDate);
    }
}

```

## 3. Przetwarzanie Strumieniowe (Streams API)

Zadania z filtrowaniem i sortowaniem list. Strumienie to podstawa na zaliczeniach z nowszej Javy.

**Filtrowanie po napisie (Substring):**

```java
public static List<Person> filterByName(List<Person> people, String substring) {
    return people.stream()
            .filter(p -> p.getFullName().contains(substring))
            .collect(Collectors.toList());
}

```

**Zwracanie posortowanej listy zmarłych osób (długość życia malejąco):**

```java
public static List<Person> getDeceasedSortedByLifespan(List<Person> people) {
    return people.stream()
            // Filtrujemy tylko tych, którzy mają zdefiniowaną datę śmierci (nie żyją)
            .filter(p -> p.getDeathDate() != null) 
            // Sortujemy używając własnego komparatora (odwrotność normalnego sortowania)
            .sorted((p1, p2) -> Long.compare(p2.getLifespan(), p1.getLifespan()))
            .collect(Collectors.toList());
}

```

## 4. Wzorzec Dekorator

Podstawa do modyfikowania zachowania bez dziedziczenia z projektu `svg`.

```java
public class ShapeDecorator implements Shape {
    protected Shape decoratedShape; // Chronione pole dla klas dziedziczących

    public ShapeDecorator(Shape decoratedShape) {
        this.decoratedShape = decoratedShape;
    }

    @Override
    public String toSvg(String attributes) {
        // Przekazanie wywołania do obudowanego obiektu
        return decoratedShape.toSvg(attributes); 
    }
}

// Przykład użycia w konkretnym dekoratorze:
public class SolidFillShapeDecorator extends ShapeDecorator {
    private String color;
    // ...
    @Override
    public String toSvg(String attributes) {
        String filledAttributes = String.format("fill=\"%s\" %s ", color, attributes);
        return super.toSvg(filledAttributes); // Odwołanie do bazowego dekoratora
    }
}

```

## 5. Wyjątki i Metoda Wytwórcza

Rzucanie własnego wyjątku wewnątrz statycznej metody tworzącej obiekt (`fromCsvLine`).

```java
// Własny wyjątek dziedziczący po RuntimeException (nie wymaga throws w nagłówku)
public class NegativeLifespanException extends RuntimeException {
    public NegativeLifespanException(String message) {
        super(message);
    }
}

// Metoda wytwórcza w klasie Person
public static Person fromCsvLine(String line) {
    String[] parts = line.split(",");
    LocalDate birth = LocalDate.parse(parts[1]);
    LocalDate death = parts[2].isEmpty() ? null : LocalDate.parse(parts[2]);

    if (death != null && death.isBefore(birth)) {
        throw new NegativeLifespanException("Data śmierci jest wcześniejsza niż data urodzin dla: " + parts[0]);
    }
    
    return new Person(parts[0], birth, death);
}

```

## 6. Typy Generyczne (Szablony) i Refleksja

Zadanie z `CustomList` - metoda, która filtruje listę na podstawie tego, czy obiekty dziedziczą po danej klasie.

```java
public static <T> List<T> filterByClass(List<T> list, Class<?> targetClass) {
    return list.stream()
            // isAssignableFrom sprawdza, czy targetClass jest nadklasą (lub tą samą klasą) co klasa obiektu
            .filter(element -> targetClass.isAssignableFrom(element.getClass()))
            .collect(Collectors.toList());
}
```</String,>

```