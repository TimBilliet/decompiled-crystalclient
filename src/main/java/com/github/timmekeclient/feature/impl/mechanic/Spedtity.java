package com.github.timmekeclient.feature.impl.mechanic;

class Spedtity {
    double x;

    double y;

    double z;

    int type;

    public Spedtity(double x, double y, double z, int type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass())
            return false;
        Spedtity other = (Spedtity)obj;
        if (this.type != other.type)
            return false;
        if (this.type == 50) {
            if (Math.abs(this.y - other.y) > 0.10000002384185791D)
                return false;
            return (this.x == other.x && this.z == other.z);
        }
        return (this.x == other.x && this.y == other.y && this.z == other.z);
    }
}