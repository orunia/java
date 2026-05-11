import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Udostępniony w zdaniu parser wzbogacony o nową treść.
public class MapParser {
    static public final class Svg {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JsonProperty("rect")
        private List<Map<String, String>> rects;
        @JacksonXmlElementWrapper(useWrapping = false)
        @JsonProperty("polygon")
        private List<Map<String, String>> polygons;
        @JacksonXmlElementWrapper(useWrapping = false)
        @JsonProperty("text")
        private List<Map<String, String>> texts;
        @JacksonXmlElementWrapper(useWrapping = false)
        @JsonProperty("circle")
        private List<Map<String, String>> circles;
    }

    private record Label(Point point, String text) {}
    private List<Label> labels = new ArrayList<>();
    
    // Krok 13. Zgodnie z instrukcją, stwarzamy nowe listy trzymające obiekty wysp i wiosek.
    private List<Land> lands = new ArrayList<>();
    private List<City> cities = new ArrayList<>();

    // Krok 13. Zlecony publiczny getter oddający zebrane lądy.
    public List<Land> getLands() {
        return lands;
    }

    private void parseText(Map<String, String> params) {
        addLabel(params.get(""), new Point (Double.parseDouble(params.get("x")), Double.parseDouble(params.get("y"))));
    }

    private void addLabel(String text, Point bottomLeft) {
        labels.add(new Label(bottomLeft, text));
    }

    // Nowa metoda pomocnicza. Tłumaczy string na obiekty Lądu.
    private void parsePolygon(Map<String, String> params) {
        // Pobiera zawartość punktów, czyści boki (trim), tnie podwójną spacją na kawałki parami X,Y.
        String[] pairs = params.get("points").trim().split("\\s+");
        // Roboczy worek na budowę.
        List<Point> pts = new ArrayList<>();
        // Dla każdej tekstowej pary...
        for (String pair : pairs) {
            // Przetnij je po przecinku oddzielającym osie...
            String[] coords = pair.split(",");
            // ...i wypuść ze stoczni nowy Punkt konwertując napis na realny typ zmiennoprzecinkowy.
            pts.add(new Point(Double.parseDouble(coords[0]), Double.parseDouble(coords[1])));
        }
        // Mając zbudowane fundamenty ulokuj powstały obiekt wyspy na mapie.
        lands.add(new Land(pts));
    }

    // Nowa metoda pomocnicza. Tłumaczy kształt miasta na logikę.
    private void parseRect(Map<String, String> params) {
        // Skoro lewy-górny narożnik (wg poleceń) leży w parametrach...
        double x = Double.parseDouble(params.get("x"));
        double y = Double.parseDouble(params.get("y"));
        // Rozmiar mury bierzemy jako szerokość z pliku svg.
        double width = Double.parseDouble(params.get("width"));
        
        // Zgodnie z ostrzeżeniem w Krok 13, odliczamy matematyczny wektor samego serca miasta pośrodku.
        Point center = new Point(x + width / 2, y + width / 2);
        
        // Wypluwamy nowe bezimienne (null) na ten moment miasto do puli.
        cities.add(new City(center, null, width));
    }

    // Krok 15 (W szkielecie nazywany "14"). Operacja nadawania etykiet po odległości.
    void matchLabelsToTowns() {
        // Bierzemy na tapet każde z uformowanych właśnie miast z listy.
        for (City city : cities) {
            Label closest = null;
            // Rekord, bardzo duża początkowa liczba by pętla szybko znalazła cokolwiek mniejszego.
            double minD = Double.MAX_VALUE;
            
            // Przeszukujemy każdy dostarczony w pliku podpis wektorowy.
            for (Label label : labels) {
                // Skoro liczymy z centrum, znowu wykorzystujemy trójkąt pitagorejski by zyskać odległość.
                double d = Math.hypot(city.center.x - label.point().x, city.center.y - label.point().y);
                
                // Jeśli jest to rekord z najbliżej przyklejonym tekstem do tego punktu w pętli...
                if (d < minD) {
                    // Ocal ten wynik rzutując wartość do nowej minimalnej do znalezienia w kolejnych.
                    minD = d;
                    // Ocal z metadanych tego zwycięskiego rekordu jego obiekt napisu by użyć po wyjściu z pętli.
                    closest = label;
                }
            }
            // Zabezpieczenie. Jeśli nie oddano pustego worka...
            if (closest != null) {
                // Wykorzystujemy skonstruowany Mutator by nanieść nową farbę na szyld w bezimiennej bramie.
                city.setName(closest.text());
            }
        }
    }

    // Krok 14 (W szkielecie to wpis TODO Krok 15). Umieszczanie ułożonych już elementów na kontynentach.
    void addCitiesToLands() {
        // Pętla przelatująca całą globalną populacje Lądów na nowej liście.
        for (Land land : lands) {
            // Przepychająca przez pętlę listę miast.
            for (City city : cities) {
                // Ląd zapytany, czy odnajduje środek we własnych brzegach?
                if (land.inside(city.center)) {
                    // Zaprasza go więc na swoje włości, korzystając z autorskiej bezpiecznej furtki AddCity odpalającej wyliczenia portu.
                    land.addCity(city);
                }
            }
        }
    }

    // Główny węzeł odpalający wszystkie zaimplementowane śrubki maszynowe od podstaw.
    void parse(String path) {
        XmlMapper xmlMapper = new XmlMapper();
        File file = new File(path);
        try {
            Svg svg = xmlMapper.readValue(file, Svg.class);
            for(var item : svg.texts)
                parseText(item);

            // Krok 13 ciąg dalszy. Zlecenie napełnienia logiką miast - zielone wielokąty i czerwone kwadraty jeśli nie oddało nula.
            if (svg.polygons != null) {
                for(var item : svg.polygons) parsePolygon(item);
            }
            if (svg.rects != null) {
                for(var item : svg.rects) parseRect(item);
            }
            
            // Nakazanie przypięcia etykiet z napisami.
            matchLabelsToTowns();
            // Nakazanie kategoryzacji podziału na stany i kontynenty.
            addCitiesToLands();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}