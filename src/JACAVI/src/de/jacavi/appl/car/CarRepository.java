package de.jacavi.appl.car;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.jacavi.appl.track.Tileset;
import de.jacavi.appl.track.TilesetRepository;
import de.jacavi.appl.track.TilesetRepository.TilesetRepositoryInitializationFailedException;



public class CarRepository {
    @SuppressWarnings("serial")
    public static class CarRepositoryInitializationFailedException extends Exception {
        public CarRepositoryInitializationFailedException(Exception e) {
            super(e);
        }
    }

    private final List<Car> cars = new ArrayList<Car>();

    private final TilesetRepository tilesetRepository;

    private final File bitmapsPath;

    public CarRepository(String configurationFile, String bitmapsPath, TilesetRepository tilesetRepository)
            throws CarRepositoryInitializationFailedException {
        this.tilesetRepository = tilesetRepository;

        try {
            // make sure the base path for the bitmaps exists
            this.bitmapsPath = new File(bitmapsPath);
            if(!this.bitmapsPath.exists())
                throw new TilesetRepositoryInitializationFailedException(
                        "The specified path for the car bitmaps does not exist: " + this.bitmapsPath.getCanonicalPath());

            // parse the XML file
            Document document = parseConfigurationFile(configurationFile);

            // iterate over all tilesets
            NodeList carList = document.getElementsByTagName("car");
            for(int i = 0; i < carList.getLength(); i++)
                importCar((Element) carList.item(i));
        } catch(Exception e) {
            throw new CarRepositoryInitializationFailedException(e);
        }
    }

    private Document parseConfigurationFile(String filename) throws ParserConfigurationException, SAXException,
            IOException {
        InputStream is = new FileInputStream(filename);
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder domBuilder = null;
        domBuilder = domFactory.newDocumentBuilder();
        return domBuilder.parse(is);
    }

    private void importCar(Element parentItem) throws IOException {
        File bitmap = null;
        List<Tileset> tilesets = null;
        double acceleration = 0;
        double mass = 0;
        double topSpeed = 0;
        double inertia = 0;
        String carName = parentItem.getAttribute("id");

        NodeList carNodes = parentItem.getChildNodes();
        for(int i = 0; i < carNodes.getLength(); i++) {
            if(carNodes.item(i) instanceof Element) {
                Element carProperty = (Element) carNodes.item(i);
                if(carProperty.getNodeName().equals("bitmap"))
                    bitmap = new File(bitmapsPath, carProperty.getTextContent());
                else if(carProperty.getNodeName().equals("tilesets"))
                    tilesets = importAssociatedTilesets(carProperty);
                else if(carProperty.getNodeName().equals("acceleration"))
                    acceleration = Double.valueOf(carProperty.getTextContent());
                else if(carProperty.getNodeName().equals("mass"))
                    mass = Double.valueOf(carProperty.getTextContent());
                else if(carProperty.getNodeName().equals("top-speed"))
                    topSpeed = Double.valueOf(carProperty.getTextContent());
                else if(carProperty.getNodeName().equals("inertia"))
                    inertia = Double.valueOf(carProperty.getTextContent());
            }
        }

        cars.add(new Car(carName, bitmap, tilesets, acceleration, mass, topSpeed, inertia));
    }

    private List<Tileset> importAssociatedTilesets(Element carProperty) {
        List<Tileset> tilesets = new ArrayList<Tileset>();

        NodeList tilesetNodes = carProperty.getElementsByTagName("tileset");
        for(int i = 0; i < tilesetNodes.getLength(); i++) {
            Element tileset = (Element) tilesetNodes.item(i);
            String tilesetID = tileset.getAttribute("id");
            tilesets.add(tilesetRepository.getTileset(tilesetID));
        }

        return tilesets;
    }

    public List<Car> getCars() {
        return cars;
    }

    public List<Car> getCarsByTileset(Tileset tileset) {
        List<Car> result = new ArrayList<Car>();
        for(Car car: cars) {
            if(car.getTilesets().contains(tileset))
                result.add(car);
        }
        return result;
    }

    /**
     * Returns the car by given id/name witch is specified as car attribute id in xml
     * <p>
     * 
     * @param id
     *            The id of the car
     *            <p>
     * @return The car if found else null
     */
    public Car getCarByID(String id) {
        Car retVal = null;
        for(Car car: cars) {
            if(car.getName().equals(id))
                retVal = car;
        }
        return retVal;
    }

}
