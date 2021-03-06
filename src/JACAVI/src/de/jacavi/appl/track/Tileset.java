package de.jacavi.appl.track;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import de.jacavi.appl.track.TilesetRepository.TilesetRepositoryInitializationFailedException;



public class Tileset {
    private final String id;

    private final SortedMap<String, Tile> tiles;

    private String initialTileID = null;

    private boolean hasMultipleInitials = false;

    private final int laneCount;

    public Tileset(String id, int laneCount) {
        this.id = id;
        this.laneCount = laneCount;
        this.tiles = new TreeMap<String, Tile>();
    }

    public String getId() {
        return id;
    }

    public SortedMap<String, Tile> getTiles() {
        return tiles;
    }

    public void validate() throws TilesetRepositoryInitializationFailedException {
        // make sure exactly one of the tiles is marked as the initial tile
        if(hasMultipleInitials)
            throw new TilesetRepositoryInitializationFailedException("The tileset " + id
                    + " contains at least two different tiles marked as initial.");
        else if(initialTileID == null)
            throw new TilesetRepositoryInitializationFailedException("The tileset " + id
                    + " is missing a tile marked as initial");

        // make sure there's at least one starting point defined in the track
        if(getInitialTile().getStartingPoints().length == 0)
            throw new TilesetRepositoryInitializationFailedException("The tileset " + id
                    + " is lacking starting points.");
    }

    public void add(String tileID, Tile tile) throws TilesetRepositoryInitializationFailedException {
        // add it to the set
        tiles.put(tileID, tile);

        // make sure there is only one initial tile in the set
        if(tile.isInitial() && initialTileID != null)
            hasMultipleInitials = true;
        else if(tile.isInitial())
            initialTileID = tileID;

        // make sure all tiles have the same number of lanes
        if(laneCount != tile.getLaneCount())
            throw new TilesetRepositoryInitializationFailedException("The tile " + tileID + " of the tileset " + id
                    + " has an invalid number of lanes. Expected: <" + laneCount + ">. Got: <" + tile.getLaneCount()
                    + ">.");
    }

    public Tile getTile(String tileID) {
        return tiles.get(tileID);
    }

    public Tile getInitialTile() {
        return tiles.get(initialTileID);
    }

    /**
     * Returns a list of all available tiles of a specific tileset.
     */
    public Map<String, Tile> getUsableTiles() {
        SortedMap<String, Tile> result = new TreeMap<String, Tile>(tiles);

        // remove the initial tile
        for(String tileID: result.keySet())
            if(result.get(tileID).isInitial()) {
                result.remove(tileID);
                break;
            }

        return result;
    }

    public int getLaneCount() {
        return laneCount;
    }
}
