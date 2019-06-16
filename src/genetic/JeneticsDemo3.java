package genetic;


import java.util.Arrays;

import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.ext.SingleNodeCrossover;
import io.jenetics.ext.util.Tree;
import io.jenetics.prog.ProgramChromosome;
import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.EphemeralConst;
import io.jenetics.prog.op.MathOp;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Var;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

/**
 *  https://github.com/jenetics/jenetics/blob/master/jenetics.prog/README.adoc#examples
 *  
 */
public class JeneticsDemo3 {

	public static final ISeq<Op<Double>> OPERATIONS = ISeq.of(
			MathOp.ADD, 
			MathOp.SUB, 
			MathOp.MUL
	);

	public static final ISeq<Op<Double>> TERMINALS = ISeq.of(
			Var.of("x", 0),
			EphemeralConst.of(() -> (double) RandomRegistry.getRandom().nextInt(10))
	);
	
	public static final Codec<ProgramGene<Double>, ProgramGene<Double>> CODEC =
		    Codec.of(
		        Genotype.of(ProgramChromosome.of(
		            // Program tree depth.
		            5,
		            // Chromosome validator.
		            ch -> ch.getRoot().size() <= 50,
		            OPERATIONS,
		            TERMINALS
		        )),
		        Genotype::getGene
		    );
	
	public static final double [][] SAMPLES = {
			{ -1.0, func(-1.0) },	
			{ -0.9, func(-0.9) },
			{ -0.8, func(-0.8) },
			{ -0.7, func(-0.7) },
			{ -0.6, func(-0.6) },
			{ -0.5, func(-0.5) },
			{ -0.4, func(-0.4) },
			{ -0.3, func(-0.3) },
			{ -0.2, func(-0.2) },
			{ -0.1, func(-0.1) },
			{  0.0, func( 0.0) },
			{  0.1, func( 0.1) },
			{  0.2, func( 0.2) },
			{  0.3, func( 0.3) },
			{  0.4, func( 0.4) },
			{  0.5, func( 0.5) },
			{  0.6, func( 0.6) },
			{  0.7, func( 0.7) },
			{  0.8, func( 0.8) },
			{  0.9, func( 0.9) },
			{  1.0, func( 1.0) }		
	};

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final Engine<ProgramGene<Double>, Double> engine = Engine
		        .builder(JeneticsDemo3::error, CODEC)
		        .minimizing()
		        .alterers(
		            new SingleNodeCrossover<>(),
		            new Mutator<>())
		        .build();

		    final ProgramGene<Double> program = engine.stream()
		        .limit(500)
		        .collect(EvolutionResult.toBestGenotype())
		        .getGene();

		    System.out.println(Tree.toString(program));
	}
	
	public static double error(final ProgramGene<Double> program) {
	    return Arrays.stream(SAMPLES).mapToDouble(sample -> {
	        final double x = sample[0];
	        final double y = sample[1];
	        final double result = program.eval(x);
	        return Math.abs(y - result) + program.size() * 0.0001;
	    })
	    .sum();
	}
	
	public static double func(double x) {
		return 4 * Math.pow(x, 3) - 3 * Math.pow(x, 2) + x;
	}

}
