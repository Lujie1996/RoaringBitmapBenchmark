package org.example;

import org.roaringbitmap.longlong.Roaring64NavigableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Benchmark {

    private static final long BITMAP_SIZE = 100_000;

    private static final long NUM_OF_OPERATIONS = 5_000;

    private static final long UPPER_BOUND = Long.MAX_VALUE;

    Random random = new Random();

    List<Long> allLongs = new ArrayList<>();

    public void start() {
        System.out.println("Bitmap size: " + BITMAP_SIZE);
        System.out.println("#operations: " + NUM_OF_OPERATIONS);

        startBenchmark(false, true);
        startBenchmark(false, false);
    }


    private void loadBitmap(Roaring64NavigableMap bitmap) {
        allLongs.clear();

        long randomLong;
        for (int i = 0; i < BITMAP_SIZE; i++) {
            randomLong = ThreadLocalRandom.current().nextLong(UPPER_BOUND);
            bitmap.add(randomLong);
            allLongs.add(randomLong);
        }
    }

    private void startBenchmark(boolean signedLongs, boolean cacheCardinalities) {
        System.out.println("-----------------------------------");
        System.out.println("signedLongs: " + signedLongs + ", cacheCardinalities: " + cacheCardinalities);

        Roaring64NavigableMap bitmap = new Roaring64NavigableMap(signedLongs, cacheCardinalities);
        loadBitmap(bitmap);

        long startTime, endTime;

        startTime = System.currentTimeMillis();
        contains(bitmap);
        endTime = System.currentTimeMillis();
        System.out.println("contains - time(ms): " + (endTime-startTime));

        startTime = System.currentTimeMillis();
        rank(bitmap);
        endTime = System.currentTimeMillis();
        System.out.println("rank - time(ms): " + (endTime-startTime));

        startTime = System.currentTimeMillis();
        select(bitmap);
        endTime = System.currentTimeMillis();
        System.out.println("select - time(ms): " + (endTime-startTime));
    }

    private boolean contains(Roaring64NavigableMap bitmap) {
        boolean res = true;
        for (int i = 0; i < NUM_OF_OPERATIONS / 2; i++) {
            long item = allLongs.get(random.nextInt(allLongs.size()));
            res &= bitmap.contains(item);
            res &= bitmap.contains(ThreadLocalRandom.current().nextLong(UPPER_BOUND));
        }
        return res;
    }

    private long rank(Roaring64NavigableMap bitmap) {
        long res = 0;
        for (int i = 0; i < NUM_OF_OPERATIONS; i++) {
            long item = allLongs.get(random.nextInt(allLongs.size()));
            res += bitmap.rankLong(item);
        }
        return res;
    }

    private long select(Roaring64NavigableMap bitmap) {
        long res = 0;
        for (int i = 0; i < NUM_OF_OPERATIONS; i++) {
            long index = random.nextInt(allLongs.size());
            res += bitmap.select(index);
        }
        return res;
    }

}
