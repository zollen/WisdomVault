package genetic;


import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;


public class JeneticsDemo1 {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Factory<Genotype<BitGene>> gtf =
	            Genotype.of(BitChromosome.of(10, 0.5));
	 
	        // 3.) Create the execution environment.
	        Engine<BitGene, Integer> engine = Engine
	            .builder(JeneticsDemo1::eval, gtf)
	            .build();
	 
	        // 4.) Start the execution (evolution) and
	        //     collect the result.
	        Genotype<BitGene> result = engine.stream()
	            .limit(100)
	            .collect(EvolutionResult.toBestGenotype());
	 
	        System.out.println("Hello World:\n" + result);
	}
	
	private static int eval(Genotype<BitGene> gt) {
        return gt.getChromosome()
            .as(BitChromosome.class)
            .bitCount();
    }
	
}
