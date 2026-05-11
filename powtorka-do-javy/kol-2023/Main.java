// Krok 16. Ostatni akord, odpalamy program z gotowym silnikiem mapowania XML.
public class Main {
    // Statyczna brama startowa programu dla środowisk.
    public static void main(String[] args) {
        // Skonstruowanie fizycznego bytu narzędzia obróbki kodu XML (MapParsera) w pamięci operacyjnej.
        MapParser parser = new MapParser();
        // Wysłanie mu ścieżki na tacy z nakazem konwersji ze stringa.
        parser.parse("map.svg");
        
        // Pętla wyłuskująca listę gotowych kontynentów prosto z serca zbudowanego już pasera.
        for (Land land : parser.getLands()) {
            // Bezpośredni nakaz wystrzelenia na monitor formatowania string z nadpisanej opcji land.toString().
            System.out.println(land.toString());
        }
    }
}