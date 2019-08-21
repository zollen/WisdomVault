package machinelearning;

public class Forward {

    /**
     * This function computes the likelihood of the observation sequence obs, given the HMM
     *
     * @param obs        - given observation list, for which the likelihood has to be calculated
     * @param states     - the various states the HMM goes through
     * @param start_p    - the start probability of each state (also denoted using the pi sign on paper)
     * @param trans_p    - the transition probability for each state, i.e. probability of transitioning from prev state to current state or one state to another
     * @param emission_p - the emission probability, i.e. the probability associated with each observation
     * @return prob - likelihood probability of the input observation sequence
     */
    public static double compute(int obs[], int states[], double start_p[], double trans_p[][], double emission_p[][]) {
        double fwd[][] = new double[obs.length][states.length];

        System.out.println("\nPrinting the observation list:-");
        for(int i=0;i<obs.length;i++)
            System.out.print(obs[i]+" ");
        System.out.println();

        //initializing the forward matrix
        for (int state : states) {
            fwd[0][state] = start_p[state] * emission_p[state][obs[0] - 1];
        }

        for (int i = 1; i < obs.length; i++) {
            for (int state1 : states) {
                fwd[i][state1] = 0;
                for (int state2 : states) {
                    fwd[i][state1] += fwd[i - 1][state2] * trans_p[state2][state1]; //forward algo adds up everything, in contrast with the viterbi algo, which takes the max
                }
                fwd[i][state1] *= emission_p[state1][obs[i] - 1];
            }
        }

        //Uncomment below code part to see the status of the fwd matrix
        /*
        for(int i=0;i<obs.length;i++){
            for(int j=0;j<states.length;j++)
                System.out.print(fwd[i][j]+" ");
            System.out.println();
        }
        */

        //calculating the final likelihood probability
        double prob = 0;
        for (int state : states) {
            prob += fwd[obs.length - 1][state];
        }
        return prob;
    }

    public static void main(String[] args) {
        int obs[] = {3, 3, 1, 1, 2, 2, 3, 1, 3};
        int states[] = {0, 1, 2, 3}; //0 - start; 1 - hot; 2 - cold; 3 - end;
        double start_p[] = {1, 1, 1, 1};
        double trans_p[][] = {
                {0, 0.8, 0.2, 0},
                {0, 0.6, 0.3, 0.1},
                {0, 0.4, 0.5, 0.1},
                {0, 0, 0, 0}
        };
        double emit_p[][] = {
                {0, 0, 0},
                {0.2, 0.4, 0.4},
                {0.5, 0.4, 0.1},
                {0, 0, 0}
        };

        double prob_result = compute(obs, states, start_p, trans_p, emit_p);
        System.out.println("Resultant path of first observation: " + prob_result);

        int obs2[] = {3, 3, 1, 1, 2, 3, 3, 1, 2};
        prob_result = compute(obs2, states, start_p, trans_p, emit_p);
        System.out.println("Resultant path of second observation: " + prob_result);
    }
}