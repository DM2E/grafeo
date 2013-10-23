Grafeo
======

Grafeo is an easy to use RDF framework.

Create a grafeo:
```java
Grafeo g1 = new GrafeoImpl();
Grafeo g2 = new GrafeoImpl("http://foo.bar/x.rdf"); // load from URI, guess format
Grafeo g3 = new GrafeoImpl("<http://foo/res1> <http://foo/prop1> <http://foo/res2>", true); // load from String, guess format
```

Add statements:
```java
g.addTriple("http://foo/res1", "rdf:type", "http://foo/res2"); // can use URI or qname, common prefixes pre-defined
g.setNamespace("foo", "http://foo/");
g.addTriple("foo:res1", "rdf:type", "foo:res2"); // same thing
```

Serialize it:
```java
System.out.println(g.getNTriples());
System.out.println(g.getTurtle());
System.out.println(g.getTerseTurtle()); // Turtle sans the @prefix, not valid but easier to read
```

Publish it:
```java
g.putToEndpoint("http://endpoint", "htttp://name-of-the-graph-to-put-to"); // this empties the graph first
g.postToEndpoint("http://endpoint", "htttp://name-of-the-graph-to-put-to"); // this adds the statements to the graph
```

