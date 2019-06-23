package map;

import functions.TestFunction;
import functions.TestFunctionAckley;
import functions.TestFunctionHimmelblau;
import functions.TestFuntion2;
import sim.field.grid.ObjectGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//import java.awt;


public class MapCreator {

    /**
     * @param imgName  name of the image to load
     * @param cellSize ratio of aspect
     * @return Bag which contains (0) an ObjectGrid2D grid, (1) the function to use,
     * and (2) the Int2D representing the stating point
     */
    public static Bag createMap(String imgName, int cellSize) {
        BufferedImage img = loadImage(imgName);
        MapElements[][] imgMap = parseImageToMap(img);
        return buildMap(imgMap, cellSize);
    }

    private static BufferedImage loadImage(String imgName) {
        try {
            return ImageIO.read(Objects.requireNonNull(MapCreator.class.getClassLoader().getResourceAsStream(imgName)));
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Image " + imgName + " not found");
        }
    }

    private static MapElements[][] parseImageToMap(BufferedImage image) {
        int error = 2;
        MapElements[][] result = new MapElements[image.getWidth()][image.getHeight()];
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                result[x][y] = MapElements.BLACK; // if it doesn't match, it will be treated as obstacle
//                final int clr = image.getRGB(x, y);
                Color color = new Color(image.getRGB(x, y));

                if (color.equals(Color.BLACK))
                    result[x][y] = MapElements.BLACK;
                if (color.equals(Color.WHITE))
                    result[x][y] = MapElements.WHITE;
                if (color.equals(Color.RED)) {
                    result[x][y] = MapElements.START;
                    error--;
                }
                if (color.equals(Color.GREEN)) {
                    result[x][y] = MapElements.GOAL;
                    error--;
                }

            }
        }
        if (error > 0) System.err.println("THE MAP HAS NOT GOAL/START MARKS");
        return result;
    }

    private static Bag buildMap(MapElements[][] imgMap, int cellSize) {
        ObjectGrid2D grid = new ObjectGrid2D(imgMap.length / cellSize, imgMap[0].length / cellSize);
        List<TestFunction> functions = new ArrayList<>();
        Int2D start = null;
        int xCount = 0, yCount = 0;
        int x = 0, y = 0;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < imgMap.length; i++) {
            for (int j = 0; j < imgMap[0].length; j++) {

                // if it is not empty, save the data and finish the iteration
                if (imgMap[i][j].equals(MapElements.GOAL)) {
                    functions.add(new TestFuntion2(x, y));
                    if (grid.get(x, y) == null) grid.set(x, y, MapElements.WHITE); // only if not assigned yet
                } else if (imgMap[i][j].equals(MapElements.START)) {
                    start = new Int2D(x, y);
                    if (grid.get(x, y) == null) grid.set(x, y, MapElements.WHITE); // only if not assigned yet
                } else if (imgMap[i][j].equals(MapElements.BLACK)) {
                    grid.set(x, y, MapElements.BLACK); // if any black, always the cell is black
                } else {
                    if (grid.get(x, y) == null) grid.set(x, y, MapElements.WHITE);
                }

                yCount++;
                if (yCount >= cellSize) {
                    y++;
                    yCount = 0;
                }
                if (y >= grid.getHeight()) break;
            }
            y = 0;
            xCount++;
            if (xCount >= cellSize) {
                x++;
                xCount = 0;
            }
            if (x >= grid.getWidth()) break;

        }

        functions.add(new TestFunctionAckley());
        functions.add(new TestFunctionHimmelblau());

        Bag res = new Bag();
        res.add(grid);
        res.add(functions);
        res.add(start);
        return res;
    }


}
