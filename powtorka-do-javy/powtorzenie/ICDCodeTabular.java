// Tworzymy publiczny interfejs (interface) o nazwie z zadania. To tylko zarys zachowania.
public interface ICDCodeTabular {
    
    // Deklarujemy, że każda klasa implementująca (korzystająca z) ten interfejs 
    // MUSI posiadać publiczną metodę 'getDescription', która przyjmuje tekst (String) 'code' 
    // i zwraca tekst (String) będący opisem, lub rzuca wyjątek IndexOutOfBoundsException (co też tu zapowiadamy przez "throws").
    public String getDescription(String code) throws IndexOutOfBoundsException;
}