package test.SST;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import theory.CharFunc;
import theory.CharPred;
import theory.CharSolver;
import transducers.sst.CharFunction;
import transducers.sst.ConstantToken;
import transducers.sst.FunctionalVariableUpdate;
import transducers.sst.SST;
import transducers.sst.SSTEpsilon;
import transducers.sst.SSTInputMove;
import transducers.sst.SSTMove;
import transducers.sst.SimpleVariableUpdate;
import transducers.sst.StringVariable;
import transducers.sst.Token;
import automata.AutomataException;

public class SSTUnitTest {

	@Test
	public void testMkSST() {

		try {
			CharSolver ba = new CharSolver();

			SST<CharPred, CharFunc, Character> sstA = getSSTa(ba);

			assertTrue(sstA.stateCount() == 2);
			assertTrue(sstA.transitionCount() == 3);

			assertTrue(sstA.stateCount() == 2);
			assertTrue(sstA.transitionCount() == 3);

		} catch (AutomataException e) {
			System.out.print(e);
		}

	}

	@Test
	public void testAccept() {

		try {
			CharSolver ba = new CharSolver();

			SST<CharPred, CharFunc, Character> sstA = getSSTaNoEps(ba);

			List<Character> goodInput = lOfS("a2c");
			List<Character> badInput = lOfS("#2c");

			assertTrue(sstA.accepts(goodInput, ba));
			assertTrue(!sstA.accepts(badInput, ba));

		} catch (AutomataException e) {
			System.out.print(e);
		}

	}

	@Test
	public void testOutput() {

		try {
			CharSolver ba = new CharSolver();

			SST<CharPred, CharFunc, Character> sstA = getSSTaNoEps(ba);

			List<Character> input1 = lOfS("a2c");
			List<Character> input2 = lOfS("acc");

			List<Character> output1 = sstA.outputOn(input1, ba);
			List<Character> output2 = sstA.outputOn(input2, ba);

			assertTrue(ba.stringOfList(output1).equals("ac"));
			assertTrue(ba.stringOfList(output2).equals("acc"));

		} catch (AutomataException e) {
			System.out.print(e);
		}

	}
	
	@Test
	public void testEpsilonRemoval() {

		try {
			CharSolver ba = new CharSolver();

			SST<CharPred, CharFunc, Character> sstA = getSSTa(ba);
			
			SST<CharPred, CharFunc, Character> sstAnoEps= sstA.removeEpsilonMoves(ba);

			List<Character> input1 = lOfS("a2c");
			List<Character> input2 = lOfS("acc");

			List<Character> output1 = sstAnoEps.outputOn(input1, ba);
			List<Character> output2 = sstAnoEps.outputOn(input2, ba);

			assertTrue(ba.stringOfList(output1).equals("ac"));
			assertTrue(ba.stringOfList(output2).equals("acc"));

		} catch (AutomataException e) {
			System.out.print(e);
		}

	}

	@Test
	public void testCombine() {

		try {
			CharSolver ba = new CharSolver();

			SST<CharPred, CharFunc, Character> sst1 = getSSTc(ba);
			SST<CharPred, CharFunc, Character> sst2 = getSSTd(ba);
			SST<CharPred, CharFunc, Character> combined = sst1.combineWith(
					sst2, ba);

			List<Character> input1 = lOfS("a2c");
			List<Character> input2 = lOfS("a22");
			List<Character> input3 = lOfS("#");

			assertTrue(combined.accepts(input1, ba));
			assertTrue(!combined.accepts(input2, ba));
			assertTrue(!combined.accepts(input3, ba));

			List<Character> output1 = combined.outputOn(input1, ba);
			assertTrue(ba.stringOfList(output1).equals("acac"));

		} catch (AutomataException e) {
			System.out.print(e);
		}

	}

	private List<Character> lOfS(String s) {
		List<Character> l = new ArrayList<Character>();
		char[] ca = s.toCharArray();
		for (int i = 0; i < s.length(); i++)
			l.add(ca[i]);
		return l;
	}

	// SST with one epsilon transition and two states, deletes all numbers and
	// keeps all letters
	private SST<CharPred, CharFunc, Character> getSSTa(CharSolver ba)
			throws AutomataException {

		CharPred alpha = new CharPred('a', 'z');
		CharPred num = new CharPred('1', '9');
		String[] variables = { "x" };
		StringVariable<CharPred, CharFunc, Character> xv = new StringVariable<>(
				"x");

		// Updates
		// x:= x
		LinkedList<ConstantToken<CharPred, CharFunc, Character>> justX = new LinkedList<>();
		justX.add(xv);

		LinkedList<Token<CharPred, CharFunc, Character>> justX2 = new LinkedList<>();
		justX2.add(xv);

		// x:= xa
		LinkedList<Token<CharPred, CharFunc, Character>> xa = new LinkedList<>();
		xa.add(xv);
		xa.add(new CharFunction<CharPred, CharFunc, Character>(CharFunc.ID()));

		// Create corresponding matrix
		SimpleVariableUpdate<CharPred, CharFunc, Character> justXva = new SimpleVariableUpdate<>(
				justX);

		FunctionalVariableUpdate<CharPred, CharFunc, Character> justXva2 = new FunctionalVariableUpdate<>(
				justX2);
		FunctionalVariableUpdate<CharPred, CharFunc, Character> xava = new FunctionalVariableUpdate<>(
				xa);

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTEpsilon<CharPred, CharFunc, Character>(0, 1,
				justXva));

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				alpha, xava));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				num, justXva2));

		// Output function just outputs x
		Map<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(0, justXva);

		return SST.MkSST(transitionsA, 0, variables, outputFunction, ba);
	}

	// SST with one epsilon transition and two states, deletes all numbers and
	// keeps all letters
	private SST<CharPred, CharFunc, Character> getSSTaNoEps(CharSolver ba)
			throws AutomataException {

		CharPred alpha = new CharPred('a', 'z');
		CharPred num = new CharPred('1', '9');
		String[] variables = { "x" };
		StringVariable<CharPred, CharFunc, Character> xv = new StringVariable<>(
				"x");

		// Updates
		// x:= x
		LinkedList<ConstantToken<CharPred, CharFunc, Character>> justX = new LinkedList<>();
		justX.add(xv);

		LinkedList<Token<CharPred, CharFunc, Character>> justX2 = new LinkedList<>();
		justX2.add(xv);

		// x:= xa
		LinkedList<Token<CharPred, CharFunc, Character>> xa = new LinkedList<>();
		xa.add(xv);
		xa.add(new CharFunction<CharPred, CharFunc, Character>(CharFunc.ID()));

		// Create corresponding matrix
		SimpleVariableUpdate<CharPred, CharFunc, Character> justXva = new SimpleVariableUpdate<>(
				justX);

		FunctionalVariableUpdate<CharPred, CharFunc, Character> justXva2 = new FunctionalVariableUpdate<>(
				justX2);
		FunctionalVariableUpdate<CharPred, CharFunc, Character> xava = new FunctionalVariableUpdate<>(
				xa);

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				alpha, xava));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				num, justXva2));

		// Output function just outputs x
		Map<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(0, justXva);

		return SST.MkSST(transitionsA, 0, variables, outputFunction, ba);
	}

	// SST with two states, deletes all numbers and
	// keeps all letters. Only defined if string ends with letter
	private SST<CharPred, CharFunc, Character> getSSTc(CharSolver ba)
			throws AutomataException {

		CharPred alpha = new CharPred('a', 'z');
		CharPred num = new CharPred('1', '9');
		String[] variables = { "x" };
		StringVariable<CharPred, CharFunc, Character> xv = new StringVariable<>(
				"x");

		// Updates
		// x:= x
		LinkedList<ConstantToken<CharPred, CharFunc, Character>> justX = new LinkedList<>();
		justX.add(xv);

		LinkedList<Token<CharPred, CharFunc, Character>> justX2 = new LinkedList<>();
		justX2.add(xv);

		// x:= xa
		LinkedList<Token<CharPred, CharFunc, Character>> xa = new LinkedList<>();
		xa.add(xv);
		xa.add(new CharFunction<CharPred, CharFunc, Character>(CharFunc.ID()));

		// Create corresponding matrix
		SimpleVariableUpdate<CharPred, CharFunc, Character> justXva = new SimpleVariableUpdate<>(
				justX);

		FunctionalVariableUpdate<CharPred, CharFunc, Character> justXva2 = new FunctionalVariableUpdate<>(
				justX2);
		FunctionalVariableUpdate<CharPred, CharFunc, Character> xava = new FunctionalVariableUpdate<>(
				xa);

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				alpha, xava));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 1,
				alpha, xava));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				num, justXva2));

		// Output function just outputs x
		Map<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(1, justXva);

		return SST.MkSST(transitionsA, 0, variables, outputFunction, ba);
	}

	// SST with two states, deletes all numbers and
	// keeps all letters. Always defined
	private SST<CharPred, CharFunc, Character> getSSTd(CharSolver ba)
			throws AutomataException {

		CharPred alpha = new CharPred('a', 'z');
		CharPred num = new CharPred('1', '9');
		String[] variables = { "x" };
		StringVariable<CharPred, CharFunc, Character> xv = new StringVariable<>(
				"x");

		// Updates
		// x:= x
		LinkedList<ConstantToken<CharPred, CharFunc, Character>> justX = new LinkedList<>();
		justX.add(xv);

		LinkedList<Token<CharPred, CharFunc, Character>> justX2 = new LinkedList<>();
		justX2.add(xv);

		// x:= xa
		LinkedList<Token<CharPred, CharFunc, Character>> xa = new LinkedList<>();
		xa.add(xv);
		xa.add(new CharFunction<CharPred, CharFunc, Character>(CharFunc.ID()));

		// Create corresponding matrix
		SimpleVariableUpdate<CharPred, CharFunc, Character> justXva = new SimpleVariableUpdate<>(
				justX);

		FunctionalVariableUpdate<CharPred, CharFunc, Character> justXva2 = new FunctionalVariableUpdate<>(
				justX2);
		FunctionalVariableUpdate<CharPred, CharFunc, Character> xava = new FunctionalVariableUpdate<>(
				xa);

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				alpha, xava));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 1,
				alpha, xava));
		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				num, justXva2));

		// Output function just outputs x
		Map<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, SimpleVariableUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(0, justXva);

		return SST.MkSST(transitionsA, 0, variables, outputFunction, ba);
	}

}