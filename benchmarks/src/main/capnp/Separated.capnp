@0x9478efb7d68e5dd0;

using Java = import "/java.capnp";
$Java.package("org.automata.capnp.Afa.Model");
$Java.outerClassname("Separated");

using Term = import "Term.capnp";

struct BoolAfa {
  aterms @0 :List(Term.BoolTerm11);
  qterms @1 :List(Term.QTerm11);
  states @2 :List(List(Conjunct11));
  varCount @3 :UInt32;
}

struct BoolAfa2 {
  aterms @0 :List(Term.BoolTerm11);
  qterms @1 :List(Term.QTerm11);
  states @2 :List(List(SimpleConjunct11));
  varCount @3 :UInt32;
  initialFormula @4 :UInt32;
  finalStates @5 :List(UInt32);
}

struct TwoBoolAfas {
  aterms @0 :List(Term.BoolTerm11);
  qterms @1 :List(Term.QTerm11);
  states @2 :List(List(SimpleConjunct11));
  varCount @3 :UInt32;
  initialFormula1 @4 :UInt32;
  finalStates1 @5 :List(UInt32);
  initialFormula2 @6 :UInt32;
  finalStates2 @7 :List(UInt32);
}

struct SimpleConjunct11 {
  aterm @0 :UInt32;
  qterm @1 :UInt32;
}

struct Conjunct11 {
  aterm @0 :Maybe1;
  qterm @1 :Maybe1;
}

struct Maybe1 {
  union {
    nothing @0 :Void;
    just @1 :UInt32;
  }
}

struct Range16Nfa {
  states @0 :List(List(ConjunctR16Q));
  initial @1 :UInt32;
  finals @2 :List(UInt32);
}

struct ConjunctR16Q {
  ranges @0 :List(Range16);
  state @1 :UInt32;
}

struct Range16 {
  begin @0 :UInt16;
  end @1 :UInt16;
}
