package co.crystaldev.client.util.javax;

import java.io.Serializable;

public class Point2i extends Tuple2i implements Serializable {
    static final long serialVersionUID = 9208072376494084954L;

    public Point2i(int x, int y) {
        super(x, y);
    }

    public Point2i(int[] t) {
        super(t);
    }

    public Point2i(Tuple2i t1) {
        super(t1);
    }

    public Point2i() {
    }
}

