package robots;

import sim.util.Double2D;
import sim.util.Int2D;

class Double2DGrid {
    private Double2D[][] matrix;

    Double2DGrid(int w, int h) {
        this.matrix = new Double2D[w][h];
    }


    int getWidth() {
        return matrix.length;
    }

    void setObjectLocation(Double2D vector, Int2D p) {
        matrix[p.x][p.y] = vector;
    }

    int getHeight() {
        return matrix[0].length;
    }

    public Double2D getObjectAtLocation(Int2D p) {
        return matrix[p.x][p.y];
    }
}
