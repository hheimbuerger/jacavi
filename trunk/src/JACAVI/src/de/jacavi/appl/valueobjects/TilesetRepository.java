package de.jacavi.appl.valueobjects;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Point;



public class TilesetRepository {

    public enum TileSet {
        analogue, digital;
    }

    private Map<TileSet, Map<String, Tile>> tiles = new HashMap<TileSet, Map<String, Tile>>();

    public TilesetRepository() {
        // HACK: just for debugging purposes, should actually be read from configuration file
        Map<String, Tile> digitalTiles = new HashMap<String, Tile>();
        digitalTiles.put("finishingStraight", new Tile("finishing_straight.png", new Point(-53, +6),
                new Point(+53, +6), new Angle(0)));
        digitalTiles.put("straight", new Tile("straight.png", new Point(-26, 0), new Point(+26, 0), new Angle(0)));
        digitalTiles.put("turn90deg", new Tile("turn_90deg.png", new Point(-12, 0), new Point(0, -12), new Angle(-90)));
        // digitalTiles.put("finishingStraight", new Tile("finishing_straight.png", new Point(0, 24), new Point(106,
        // 24),
        // new Angle(0)));
        // digitalTiles.put("straight", new Tile("straight.png", new Point(0, 12), new Point(53, 12), new Angle(0)));
        // digitalTiles.put("turn90deg", new Tile("turn_90deg.png", new Point(25, 12), new Point(25, 12), new
        // Angle(-90)));
        tiles.put(TileSet.digital, digitalTiles);
    }

    public Tile getTile(TileSet tileSet, String id) {
        return tiles.get(tileSet).get(id);
    }

}
