package edu.coursera.concurrent;

import java.util.ArrayList;
import edu.rice.pcdp.Actor;
import edu.rice.pcdp.PCDP;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     *
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
        final SieveActorActor sieveActor = new SieveActorActor(2);
        PCDP.finish(() -> {
            for (int i = 3; i<=limit; i++) {
                sieveActor.send(i);
            }
        });
        int numPrimes = 0;
        SieveActorActor mutableSieveActor = sieveActor;
        while (mutableSieveActor != null) {
            numPrimes += 1;
            mutableSieveActor = mutableSieveActor.next;
        }
        return numPrimes;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        private int sievePrime;
        private SieveActorActor next = null;
        private boolean nextPrimeFound = false;

        private SieveActorActor(int prime) {
            sievePrime = prime;
        }

        /**
         * Process a single message sent to this actor.
         *
         * TODO complete this method.
         *
         * @param msg Received message
         */
        @Override
        public void process(final Object msg) {
            int candidate = (int)msg;
            if (candidate%sievePrime!=0) {
                if (nextPrimeFound) {
                    next.send(candidate);
                }
                else {
                    next = new SieveActorActor(candidate);
                    nextPrimeFound = true;
                }
            }
        }
    }
}
