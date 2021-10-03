package utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;
import java.util.Scanner;

import org.sat4j.specs.TimeoutException;

import RegexParser.RegexNode;
import RegexParser.RegexParserProvider;
import automata.sfa.SFA;
import automata.sfa.SFAMove;
import automata.sfa.SFAInputMove;
import regexconverter.RegexConverter;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;

import org.capnproto.PrimitiveList;
import org.capnproto.StructList;
import org.capnproto.ListList;

import org.automata.safa.capnp.Afa.Model.Separated.Range16Nfa;
import org.automata.safa.capnp.Afa.Model.Separated.ConjunctR16Q;
import org.automata.safa.capnp.Afa.Model.Separated.Range16;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class RegexToBoolAfa {
	public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int i = 0;
        while (input.hasNext()) {
            try {
                System.out.println("PARSING " + i);
                RegexNode regex = RegexParserProvider.parse(new String[]{input.nextLine()}).get(0);
                UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();
                // System.out.println("PARSING2");
                SFA<CharPred, Character> nfa = RegexConverter.toSFA(regex, solver);
                // System.out.println("PARSING3");

                // System.out.println("STATES " + nfa.getStates());
                // System.out.println("INIT " + nfa.getInitialState());
                // System.out.println("FINAL " + nfa.getFinalStates());
                // System.out.println("TRANS " + nfa.getTransitions());

                nfa = nfa.removeEpsilonMoves(solver);
                // System.out.println("PARSED");

                // System.out.println("STATES " + nfa.getStates());
                // System.out.println("INIT " + nfa.getInitialState());
                // System.out.println("FINAL " + nfa.getFinalStates());
                // System.out.println("TRANS " + nfa.getTransitions());

                // System.out.println("BUILDING");
                org.capnproto.MessageBuilder message = new org.capnproto.MessageBuilder();
                Range16Nfa.Builder outnfa = message.initRoot(Range16Nfa.factory);

                outnfa.setInitial(nfa.getInitialState());

                int fcount = 0;
                for (@SuppressWarnings("unused") int fq: nfa.getFinalStates()) fcount++;
                PrimitiveList.Int.Builder finals = outnfa.initFinals(fcount);
                int f = 0;
                for (int fq: nfa.getFinalStates()) { finals.set(f, fq); f++; }

                ListList.Builder<StructList.Builder<ConjunctR16Q.Builder>> states = outnfa.initStates(nfa.stateCount());

                int q = 0;
                for (int state: nfa.getStates()) {
                    assert state == q;
                    int tcount = 0;
                    for (@SuppressWarnings("unused") SFAMove<CharPred, Character> trans: nfa.getTransitionsFrom(q)) tcount++;
                    StructList.Builder<ConjunctR16Q.Builder> transitions = states.init(q, tcount);

                    int t = 0;
                    for (SFAMove<CharPred, Character> trans0: nfa.getTransitionsFrom(q)) {
                        SFAInputMove<CharPred, Character> trans = (SFAInputMove<CharPred, Character>)trans0;
                        ImmutableList<ImmutablePair<Character, Character>> ranges = trans.guard.intervals;

                        ConjunctR16Q.Builder conjunct = transitions.get(t);
                        conjunct.setState(trans.to);
                        StructList.Builder<Range16.Builder> outRanges = conjunct.initRanges(ranges.size());

                        int g = 0;
                        for (ImmutablePair<Character, Character> range: ranges) {
                            Range16.Builder outRange = outRanges.get(g);
                            outRange.setBegin((short)(int)range.left);
                            outRange.setEnd((short)(int)range.right);
                            g++;
                        }
                        t++;
                    }
                    q++;
                }

                // System.out.println("BUILT");

                File file0 = new File("regexNfas/" + i);
                file0.createNewFile(); // if file already exists will do nothing 
                FileOutputStream outfile = new FileOutputStream(file0, false);
                WritableByteChannel outchan = outfile.getChannel();
                try {
                    // System.out.println("WRITING");
                    org.capnproto.Serialize.write(outchan, message);
                    // System.out.println("WRITTEN");
                } finally {
                    try {
                        outchan.close();
                    } finally {
                        outfile.close();
                    }
                }
            } catch (java.io.IOException e) {
                System.out.println(i);
                e.printStackTrace();
            } catch (TimeoutException e) {
                System.out.println(i);
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.out.println(i + " NULL");
            } catch (UnsupportedOperationException e) {
                System.out.println(i + " unsupported");
            }
            i++;
        }
        input.close();
    }
}
