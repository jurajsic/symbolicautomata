package transducers.sst;

import java.util.ArrayList;
import java.util.List;

import theory.BooleanAlgebraSubst;

public class FunctionalVariableUpdate<P, F, S> extends VariableUpdate<P, F, S> {

	public ArrayList<List<Token<P, F, S>>> variableUpdate;

	public FunctionalVariableUpdate() {
		super();
		this.variableUpdate = new ArrayList<List<Token<P, F, S>>>();
	}

	public FunctionalVariableUpdate(
			ArrayList<List<Token<P, F, S>>> variableUpdate) {
		super();
		this.variableUpdate = variableUpdate;
	}

	public FunctionalVariableUpdate(List<Token<P, F, S>> singleUpdate) {
		super();
		this.variableUpdate = new ArrayList<List<Token<P, F, S>>>();
		this.variableUpdate.add(singleUpdate);
	}

	/**
	 * applies the current update to the current variable configuration
	 * 
	 * @param assignment
	 * @param ba
	 * @return
	 */
	public VariableAssignment<S> applyTo(VariableAssignment<S> assignment,
			S input, BooleanAlgebraSubst<P, F, S> ba) {

		int numVars = assignment.numVars();
		ArrayList<List<S>> variableValues = new ArrayList<List<S>>(numVars);
		for (int variable = 0; variable < numVars; variable++) {
			List<S> value = new ArrayList<S>();

			for (Token<P, F, S> token : variableUpdate.get(variable)) {
				List<S> tokenApp = token.applyTo(assignment, input, ba);
				value.addAll(tokenApp);
			}

			variableValues.add(value);
		}

		return new VariableAssignment<S>(variableValues);
	}

	public FunctionalVariableUpdate<P, F, S> renameVars(
			Integer varRename) {
		if(varRename==0)
			return this;
		
		ArrayList<List<Token<P, F, S>>> newVariableUpdate = new ArrayList<List<Token<P, F, S>>>();
		for (List<Token<P, F, S>> singleVarUp : variableUpdate) {
			newVariableUpdate.add(renameTokens(varRename, singleVarUp));
		}

		return new FunctionalVariableUpdate<P, F, S>(newVariableUpdate);
	}

	public FunctionalVariableUpdate<P, F, S> liftToNVars(int n) {
		ArrayList<List<Token<P, F, S>>> newVariableUpdate = new ArrayList<List<Token<P, F, S>>>(
				variableUpdate);
		for (int i = variableUpdate.size(); i < n; i++) {
			newVariableUpdate.add(new ArrayList<Token<P, F, S>>());
		}

		return new FunctionalVariableUpdate<P, F, S>(newVariableUpdate);
	}

	private List<Token<P, F, S>> renameTokens(
			Integer varRename,
			List<Token<P, F, S>> singleVarUp) {
		List<Token<P, F, S>> renamed = new ArrayList<Token<P, F, S>>();
		for (Token<P, F, S> t : singleVarUp)
			renamed.add(t.rename(varRename));

		return renamed;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (List<Token<P, F, S>> ass : variableUpdate) {
			for (Token<P, F, S> tok : ass) {
				sb.append(tok.toString());
			}
			sb.append(';');
		}
		return sb.toString();
	}

	// STATIC METHODS
	/**
	 * Combines two updates into a single one by renaming variables accordingly
	 * using the disjoint rename functions
	 */
	public static <P1, F1, S1> FunctionalVariableUpdate<P1, F1, S1> combineUpdates(
			FunctionalVariableUpdate<P1, F1, S1> update1,
			FunctionalVariableUpdate<P1, F1, S1> update2) {

		ArrayList<List<Token<P1, F1, S1>>> combinedVariableUpdate = new ArrayList<List<Token<P1, F1, S1>>>();
		FunctionalVariableUpdate<P1, F1, S1> ren2 = (FunctionalVariableUpdate<P1, F1, S1>) update2
				.renameVars(update1.variableUpdate.size());
		combinedVariableUpdate.addAll(update1.variableUpdate);
		combinedVariableUpdate.addAll(ren2.variableUpdate);
		return new FunctionalVariableUpdate<P1, F1, S1>(combinedVariableUpdate);

	}

	/**
	 * Combines two updates into a single one by renaming variables accordingly
	 * using the disjoint rename functions
	 */
	public static <P1, F1, S1> FunctionalVariableUpdate<P1, F1, S1> addUpdate(
			Integer varRename,
			FunctionalVariableUpdate<P1, F1, S1> update1,
			FunctionalVariableUpdate<P1, F1, S1> update2) {

		ArrayList<List<Token<P1, F1, S1>>> combinedVariableUpdate = new ArrayList<List<Token<P1, F1, S1>>>();
		FunctionalVariableUpdate<P1, F1, S1> ren2 = (FunctionalVariableUpdate<P1, F1, S1>) update2
				.renameVars(varRename);
		combinedVariableUpdate.addAll(update1.variableUpdate);
		combinedVariableUpdate.addAll(ren2.variableUpdate);
		return new FunctionalVariableUpdate<P1, F1, S1>(combinedVariableUpdate);

	}
}
