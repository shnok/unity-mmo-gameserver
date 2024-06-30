package com.shnok.javaserver.security;

import java.security.SecureRandom;
import java.util.Random;

public final class Rnd {
    private static final long ADDEND = 11L;
    private static final long MASK = 281474976710655L;
    private static final long MULTIPLIER = 25214903917L;
    private static final RandomContainer rnd;
    protected static volatile long SEED_UNIQUIFIER;

    public Rnd() {
    }

    public static Random directRandom() {
        return rnd.directRandom();
    }

    public static double get() {
        return rnd.nextDouble();
    }

    public static int get(int n) {
        return rnd.get(n);
    }

    public static int get(int min, int max) {
        return rnd.get(min, max);
    }

    public static long get(long min, long max) {
        return rnd.get(min, max);
    }

    public static RandomContainer newInstance(RandomType type) {
        switch (type.ordinal()) {
            case 0:
                return new RandomContainer(new SecureRandom());
            case 1:
                return new RandomContainer(new Random());
            case 2:
                return new RandomContainer(new ThreadLocalRandom());
            case 3:
                return new RandomContainer(new NonAtomicRandom());
            default:
                throw new IllegalArgumentException();
        }
    }

    public static boolean nextBoolean() {
        return rnd.nextBoolean();
    }

    public static void nextBytes(byte[] array) {
        rnd.nextBytes(array);
    }

    public static double nextDouble() {
        return rnd.nextDouble();
    }

    public static float nextFloat() {
        return rnd.nextFloat();
    }

    public static double nextGaussian() {
        return rnd.nextGaussian();
    }

    public static int nextInt() {
        return rnd.nextInt();
    }

    public static int nextInt(int n) {
        return get(n);
    }

    public static long nextLong() {
        return rnd.nextLong();
    }

    public static <T> T randomElement(T[] array) {
        return array[get(array.length)];
    }

    static {
        rnd = newInstance(RandomType.UNSECURE_THREAD_LOCAL);
        SEED_UNIQUIFIER = 8682522807148012L;
    }

    protected static final class RandomContainer {
        private final Random _random;

        protected RandomContainer(Random random) {
            this._random = random;
        }

        public Random directRandom() {
            return this._random;
        }

        public double get() {
            return this._random.nextDouble();
        }

        public int get(int n) {
            return (int) (this._random.nextDouble() * (double) n);
        }

        public int get(int min, int max) {
            return min + (int) (this._random.nextDouble() * (double) (max - min + 1));
        }

        public long get(long min, long max) {
            return min + (long) (this._random.nextDouble() * (double) (max - min + 1L));
        }

        public boolean nextBoolean() {
            return this._random.nextBoolean();
        }

        public void nextBytes(byte[] array) {
            this._random.nextBytes(array);
        }

        public double nextDouble() {
            return this._random.nextDouble();
        }

        public float nextFloat() {
            return this._random.nextFloat();
        }

        public double nextGaussian() {
            return this._random.nextGaussian();
        }

        public int nextInt() {
            return this._random.nextInt();
        }

        public long nextLong() {
            return this._random.nextLong();
        }
    }

    public static enum RandomType {
        SECURE,
        UNSECURE_ATOMIC,
        UNSECURE_THREAD_LOCAL,
        UNSECURE_VOLATILE;

        private RandomType() {
        }
    }

    public static final class NonAtomicRandom extends Random {
        private static final long serialVersionUID = 1L;
        private volatile long _seed;

        public NonAtomicRandom() {
            this(++SEED_UNIQUIFIER + System.nanoTime());
        }

        public NonAtomicRandom(long seed) {
            this.setSeed(seed);
        }

        public int next(int bits) {
            return (int) ((this._seed = this._seed * 25214903917L + 11L & 281474976710655L) >>> 48 - bits);
        }

        public void setSeed(long seed) {
            this._seed = (seed ^ 25214903917L) & 281474976710655L;
        }
    }

    public static final class ThreadLocalRandom extends Random {
        private static final long serialVersionUID = 1L;
        private final ThreadLocal<Seed> _seedLocal;

        public ThreadLocalRandom() {
            this._seedLocal = new ThreadLocal<Seed>() {
                public final Seed initialValue() {
                    return new Seed(++SEED_UNIQUIFIER + System.nanoTime());
                }
            };
        }

        public ThreadLocalRandom(final long seed) {
            this._seedLocal = new ThreadLocal<Seed>() {
                public final Seed initialValue() {
                    return new Seed(seed);
                }
            };
        }

        public int next(int bits) {
            return ((Seed) this._seedLocal.get()).next(bits);
        }

        public void setSeed(long seed) {
            if (this._seedLocal != null) {
                ((Seed) this._seedLocal.get()).setSeed(seed);
            }

        }

        private static final class Seed {
            long _seed;

            Seed(long seed) {
                this.setSeed(seed);
            }

            int next(int bits) {
                return (int) ((this._seed = this._seed * 25214903917L + 11L & 281474976710655L) >>> 48 - bits);
            }

            void setSeed(long seed) {
                this._seed = (seed ^ 25214903917L) & 281474976710655L;
            }
        }
    }
}