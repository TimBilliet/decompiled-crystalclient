package com.madgag.gif.fmsware;

public class NeuQuant {
    protected static final int netsize = 256;

    protected static final int prime1 = 499;

    protected static final int prime2 = 491;

    protected static final int prime3 = 487;

    protected static final int prime4 = 503;

    protected static final int minpicturebytes = 1509;

    protected static final int maxnetpos = 255;

    protected static final int netbiasshift = 4;

    protected static final int ncycles = 100;

    protected static final int intbiasshift = 16;

    protected static final int intbias = 65536;

    protected static final int gammashift = 10;

    protected static final int gamma = 1024;

    protected static final int betashift = 10;

    protected static final int beta = 64;

    protected static final int betagamma = 65536;

    protected static final int initrad = 32;

    protected static final int radiusbiasshift = 6;

    protected static final int radiusbias = 64;

    protected static final int initradius = 2048;

    protected static final int radiusdec = 30;

    protected static final int alphabiasshift = 10;

    protected static final int initalpha = 1024;

    protected int alphadec;

    protected static final int radbiasshift = 8;

    protected static final int radbias = 256;

    protected static final int alpharadbshift = 18;

    protected static final int alpharadbias = 262144;

    protected byte[] thepicture;

    protected int lengthcount;

    protected int samplefac;

    protected int[][] network;

    protected int[] netindex = new int[256];

    protected int[] bias = new int[256];

    protected int[] freq = new int[256];

    protected int[] radpower = new int[32];

    public NeuQuant(byte[] thepic, int len, int sample) {
        this.thepicture = thepic;
        this.lengthcount = len;
        this.samplefac = sample;
        this.network = new int[256][];
        for (int i = 0; i < 256; i++) {
            this.network[i] = new int[4];
            int[] p = this.network[i];
            p[2] = (i << 12) / 256;
            p[1] = (i << 12) / 256;
            p[0] = (i << 12) / 256;
            this.freq[i] = 256;
            this.bias[i] = 0;
        }
    }

    public byte[] colorMap() {
        byte[] map = new byte[768];
        int[] index = new int[256];
        for (int i = 0; i < 256; i++)
            index[this.network[i][3]] = i;
        int k = 0;
        for (int j = 0; j < 256; j++) {
            int m = index[j];
            map[k++] = (byte) this.network[m][0];
            map[k++] = (byte) this.network[m][1];
            map[k++] = (byte) this.network[m][2];
        }
        return map;
    }

    public void inxbuild() {
        int previouscol = 0;
        int startpos = 0;
        for (int i = 0; i < 256; i++) {
            int[] p = this.network[i];
            int smallpos = i;
            int smallval = p[1];
            int k;
            for (k = i + 1; k < 256; k++) {
                int[] arrayOfInt = this.network[k];
                if (arrayOfInt[1] < smallval) {
                    smallpos = k;
                    smallval = arrayOfInt[1];
                }
            }
            int[] q = this.network[smallpos];
            if (i != smallpos) {
                k = q[0];
                q[0] = p[0];
                p[0] = k;
                k = q[1];
                q[1] = p[1];
                p[1] = k;
                k = q[2];
                q[2] = p[2];
                p[2] = k;
                k = q[3];
                q[3] = p[3];
                p[3] = k;
            }
            if (smallval != previouscol) {
                this.netindex[previouscol] = startpos + i >> 1;
                for (k = previouscol + 1; k < smallval; k++)
                    this.netindex[k] = i;
                previouscol = smallval;
                startpos = i;
            }
        }
        this.netindex[previouscol] = startpos + 255 >> 1;
        for (int j = previouscol + 1; j < 256; j++)
            this.netindex[j] = 255;
    }

    public void learn() {
        int step;
        if (this.lengthcount < 1509)
            this.samplefac = 1;
        this.alphadec = 30 + (this.samplefac - 1) / 3;
        byte[] p = this.thepicture;
        int pix = 0;
        int lim = this.lengthcount;
        int samplepixels = this.lengthcount / 3 * this.samplefac;
        int delta = samplepixels / 100;
        int alpha = 1024;
        int radius = 2048;
        int rad = radius >> 6;
        if (rad <= 1)
            rad = 0;
        int i;
        for (i = 0; i < rad; i++)
            this.radpower[i] = alpha * (rad * rad - i * i) * 256 / rad * rad;
        if (this.lengthcount < 1509) {
            step = 3;
        } else if (this.lengthcount % 499 != 0) {
            step = 1497;
        } else if (this.lengthcount % 491 != 0) {
            step = 1473;
        } else if (this.lengthcount % 487 != 0) {
            step = 1461;
        } else {
            step = 1509;
        }
        i = 0;
        while (i < samplepixels) {
            int b = (p[pix + 0] & 0xFF) << 4;
            int g = (p[pix + 1] & 0xFF) << 4;
            int r = (p[pix + 2] & 0xFF) << 4;
            int j = contest(b, g, r);
            altersingle(alpha, j, b, g, r);
            if (rad != 0)
                alterneigh(rad, j, b, g, r);
            pix += step;
            if (pix >= lim)
                pix -= this.lengthcount;
            i++;
            if (delta == 0)
                delta = 1;
            if (i % delta == 0) {
                alpha -= alpha / this.alphadec;
                radius -= radius / 30;
                rad = radius >> 6;
                if (rad <= 1)
                    rad = 0;
                for (j = 0; j < rad; j++)
                    this.radpower[j] = alpha * (rad * rad - j * j) * 256 / rad * rad;
            }
        }
    }

    public int map(int b, int g, int r) {
        int bestd = 1000;
        int best = -1;
        int i = this.netindex[g];
        int j = i - 1;
        while (i < 256 || j >= 0) {
            if (i < 256) {
                int[] p = this.network[i];
                int dist = p[1] - g;
                if (dist >= bestd) {
                    i = 256;
                } else {
                    i++;
                    if (dist < 0)
                        dist = -dist;
                    int a = p[0] - b;
                    if (a < 0)
                        a = -a;
                    dist += a;
                    if (dist < bestd) {
                        a = p[2] - r;
                        if (a < 0)
                            a = -a;
                        dist += a;
                        if (dist < bestd) {
                            bestd = dist;
                            best = p[3];
                        }
                    }
                }
            }
            if (j >= 0) {
                int[] p = this.network[j];
                int dist = g - p[1];
                if (dist >= bestd) {
                    j = -1;
                    continue;
                }
                j--;
                if (dist < 0)
                    dist = -dist;
                int a = p[0] - b;
                if (a < 0)
                    a = -a;
                dist += a;
                if (dist < bestd) {
                    a = p[2] - r;
                    if (a < 0)
                        a = -a;
                    dist += a;
                    if (dist < bestd) {
                        bestd = dist;
                        best = p[3];
                    }
                }
            }
        }
        return best;
    }

    public byte[] process() {
        learn();
        unbiasnet();
        inxbuild();
        return colorMap();
    }

    public void unbiasnet() {
        for (int i = 0; i < 256; i++) {
            this.network[i][0] = this.network[i][0] >> 4;
            this.network[i][1] = this.network[i][1] >> 4;
            this.network[i][2] = this.network[i][2] >> 4;
            this.network[i][3] = i;
        }
    }

    protected void alterneigh(int rad, int i, int b, int g, int r) {
        int lo = i - rad;
        if (lo < -1)
            lo = -1;
        int hi = i + rad;
        if (hi > 256)
            hi = 256;
        int j = i + 1;
        int k = i - 1;
        int m = 1;
        while (j < hi || k > lo) {
            int a = this.radpower[m++];
            if (j < hi) {
                int[] p = this.network[j++];
                try {
                    p[0] = p[0] - a * (p[0] - b) / 262144;
                    p[1] = p[1] - a * (p[1] - g) / 262144;
                    p[2] = p[2] - a * (p[2] - r) / 262144;
                } catch (Exception exception) {
                }
            }
            if (k > lo) {
                int[] p = this.network[k--];
                try {
                    p[0] = p[0] - a * (p[0] - b) / 262144;
                    p[1] = p[1] - a * (p[1] - g) / 262144;
                    p[2] = p[2] - a * (p[2] - r) / 262144;
                } catch (Exception exception) {
                }
            }
        }
    }

    protected void altersingle(int alpha, int i, int b, int g, int r) {
        int[] n = this.network[i];
        n[0] = n[0] - alpha * (n[0] - b) / 1024;
        n[1] = n[1] - alpha * (n[1] - g) / 1024;
        n[2] = n[2] - alpha * (n[2] - r) / 1024;
    }

    protected int contest(int b, int g, int r) {
        int bestd = Integer.MAX_VALUE;
        int bestbiasd = bestd;
        int bestpos = -1;
        int bestbiaspos = bestpos;
        for (int i = 0; i < 256; i++) {
            int[] n = this.network[i];
            int dist = n[0] - b;
            if (dist < 0)
                dist = -dist;
            int a = n[1] - g;
            if (a < 0)
                a = -a;
            dist += a;
            a = n[2] - r;
            if (a < 0)
                a = -a;
            dist += a;
            if (dist < bestd) {
                bestd = dist;
                bestpos = i;
            }
            int biasdist = dist - (this.bias[i] >> 12);
            if (biasdist < bestbiasd) {
                bestbiasd = biasdist;
                bestbiaspos = i;
            }
            int betafreq = this.freq[i] >> 10;
            this.freq[i] = this.freq[i] - betafreq;
            this.bias[i] = this.bias[i] + (betafreq << 10);
        }
        this.freq[bestpos] = this.freq[bestpos] + 64;
        this.bias[bestpos] = this.bias[bestpos] - 65536;
        return bestbiaspos;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\madgag\gif\fmsware\NeuQuant.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */