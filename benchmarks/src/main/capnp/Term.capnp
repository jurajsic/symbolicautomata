@0xd4c5f198b0c1c530;

using Java = import "/java.capnp";
$Java.package("org.automata.capnp.Afa.Model");
$Java.outerClassname("Term");

struct QTerm11 {
  union {
    state @0 :UInt32;
    and @1 :List(UInt32);
    or @2 :List(UInt32);
    litTrue @3 :Void;
  }
}

struct PredicateQTerm11P(P) {
  union {
    litTrue @0 :Void;
    predicate @1 :P;
    state @2 :UInt32;
    or @3 :List(UInt32);
    and @4 :List(UInt32);
  }
}

struct PredicateQTerm111 {
  union {
    litTrue @0 :Void;
    predicate @1 :UInt32;
    state @2 :UInt32;
    or @3 :List(UInt32);
    and @4 :List(UInt32);
  }
}

struct BoolTerm1P(P) {
  union {
    predicate @0 :P;
    and @1 :List(UInt32);
    or @2 :List(UInt32);
    not @3 :UInt32;
    litTrue @4 :Void;
    litFalse @5 :Void;
  }
}

struct BoolTerm11 {
  union {
    predicate @0 :UInt32;
    and @1 :List(UInt32);
    or @2 :List(UInt32);
    not @3 :UInt32;
    litTrue @4 :Void;
    litFalse @5 :Void;
  }
}
