package com.github.timmekeclient.feature.impl.mechanic;

class Duplicates {
    int type;

    int ticksLived;

    double x;

    double y;

    double z;

    public Duplicates(int type, int ticksLived, double x, double y, double z) {
        this.type = type;
        this.ticksLived = ticksLived;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass())
            return false;
        Duplicates other = (Duplicates)obj;
        if (this.type == 50) {
            if (Math.abs(this.y - other.y) > 0.10000002384185791D)
                return false;
            return (this.ticksLived == other.ticksLived && this.x == other.x && this.z == other.z);
        }
        if (this.type != 70 && this.ticksLived != other.ticksLived)
            return false;
        return (this.type == other.type && this.x == other.x && this.y == other.y && this.z == other.z);
    }
}

