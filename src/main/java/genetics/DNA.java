package genetics;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class DNA {
    /**
     * Class to model DNA strands, mutation and crossover
     */
    private Random random = new Random();
    public byte[] data;

    public DNA(boolean empty, int size) {
        data = new byte[size];

        if (!empty) {
            try {
                loadFromFile();
            } catch (IOException e) {
                for (int i = 0; i < data.length; i++) {
                    data[i] = (byte) Math.floor(Math.random() * 256d);
                }
            }
        } else {
            for (int i = 0; i < data.length; i++) {
                data[i] = 0;
            }
        }
    }

    /**
     * Crossover function which combines this DNA with another DNA object.
     * Process is done byte-wise and a gaussian noise is added to each byte-value
     * Bits flip according to mutation probability
     */
    public DNA crossoverNoise(DNA other, double mutationprob) {  //byte-wise, noise applied to each value
        DNA newdna = new DNA(true, data.length);
        int numswaps = data.length / 10;
        int[] swaps = new int[numswaps + 1];
        for (int i = 0; i < swaps.length - 1; i++) {
            swaps[i] = (int) Math.floor(Math.random() * data.length);
        }
        swaps[numswaps] = data.length;  //save last
        Arrays.sort(swaps);
        int swapidx = 0;
        boolean that = true;
        for (int i = 0; i < data.length; i++) {
            if (i >= swaps[swapidx]) {
                swapidx++;
                that = !that;
            }
            byte d = 0;
            if (that) {
                d = this.data[i];
            } else {
                d = other.data[i];
            }
            d += (byte) (random.nextGaussian() * mutationprob * 256);
            newdna.data[i] = d;
        }
        return newdna;
    }

    /**
     * Gaussian mutation function
     */
    public void mutateNoise(double prob, double mag) {
        for (int i = 0; i < data.length; i++) {
            if (Math.random() < prob) data[i] += (byte) (random.nextGaussian() * mag * 256);
        }
    }

    /**
     * Crossover function which combines this DNA with another DNA object.
     * Process is done bit-wise
     * Bits flip according to mutation probability
     */
    public DNA crossover(DNA other, double mutationprob) {
        DNA newdna = new DNA(true, data.length);
        int numswaps = data.length / 8;
        int[] swaps = new int[numswaps + 1];
        for (int i = 0; i < swaps.length - 1; i++) {
            swaps[i] = (int) Math.floor(Math.random() * 8 * data.length);
        }
        return getDna(other, mutationprob, newdna, numswaps, swaps);
    }

    /**
     * Crossover function which combines this DNA with another DNA object.
     * Process is only done byte-wise, so less noise is added
     * Bits flip according to mutation probability
     */
    public DNA crossoverBytewise(DNA other, double mutationProb) {
        DNA newDNA = new DNA(true, data.length);
        int numSwaps = data.length / 8;
        int[] swaps = new int[numSwaps + 1];
        for (int i = 0; i < swaps.length - 1; i++) {
            swaps[i] = 8 * (int) Math.floor(Math.random() * data.length);
        }
        return getDna(other, mutationProb, newDNA, numSwaps, swaps);
    }

    private DNA getDna(DNA other, double mutationProb, DNA newDNA, int numSwaps, int[] swaps) {
        swaps[numSwaps] = 8 * data.length;  //save last
        Arrays.sort(swaps);
        int swapidx = 0;
        boolean that = true;
        for (int i = 0; i < 8 * data.length; i++) {
            if (i >= swaps[swapidx]) {
                swapidx++;
                that = !that;
            }
            int bit;
            if (that) {
                bit = ((this.data[i / 8] >> (i % 8)) & 1);
            } else {
                bit = ((other.data[i / 8] >> (i % 8)) & 1);
            }
            if (Math.random() < mutationProb) bit = 1 - bit;
            newDNA.data[i / 8] |= (bit << (i % 8));
        }
        return newDNA;
    }

    public void saveToFile(int bestscore) throws IOException {
        FileOutputStream fos = new FileOutputStream(String.valueOf(bestscore) + ".out");
        fos.write(data, 0, data.length);
        fos.flush();
        fos.close();
    }

    private void loadFromFile() throws IOException {
        File inputFile = new File("best.out");
        FileInputStream fis = new FileInputStream(inputFile);
        data = new byte[(int) inputFile.length()];
        fis.read(data, 0, data.length);
        fis.close();
    }
}
