### Rational

The RDD API is type-safe.
Transformations return well-typed RDDs and are defined using regular Scala functions.

RDD downsides:
- No planning and query optimization for ex. using reduceByKey before filter or doing all filters on single sides of joins
- The execution engine can’t introspect Scala functions. For ex if only one col is used can do projection pushdown to pq/jdbc
- The API mixes lazy transformations that return RDD instances like flatMap and filter with strict actions like top that trigger the RDD execution and bring the data to memory.
- Functions can capture values from the outer scope (closure), eventually failing at runtime if a value is not serializable. 

Dataframe addressed some of the optimization limitations of RDDs by making transformations  less opaque to the execution engine … allowing projection push down. Also, the user’s intent is more evident since the transformation uses first-class operations like group by and the count aggregation.

Dataframe downside:
- based on untyped string values to represent columns and expressions. Fails at runtime on action evaluation.

Dataset aims to make the API more type safe. Dataframe is just an untyped Dataset. 

Dataset downsides:
- The API switches between Dataset and Dataframe depending on how the operation is done. The untyped operations make the code prone to runtime type errors.
- Some of the transformations (flatMap, filter, map) still use opaque Scala functions, so they don’t enable more advanced optimizations of projection pushdown, as the argument is whole case class.

 An ideal solution should give the execution engine enough information about the transformations but at the same time preserve type safety.

Quoted API advantages over Dataset:
- The computation is defined using regular code, leveraging Scala’s type system and syntax.
- Given that the computation becomes a SQL string, Spark is able to introspect all transformations and can apply more optimizations.
- The execution engine can reorder operations if necessary to avoid inefficiencies introduced by the user.
- All transformations are lazy, so the user doesn’t need to worry whether a method is lazy or not.
- Any captured value needs to be lifted into the quotation using the lift method, or else the compilation fails. (Quill also ensures that only supported values can be lifted, so there’s no risk of runtime exceptions.)
- Monadic api (RDD flatmap is actually an applicative).


### Running
`sbt run`


### References

[Everything old is new again: Quoted Domain Specific Languages](http://homepages.inf.ed.ac.uk/wadler/papers/qdsl/qdsl.pdf)

[Kleisli, a Functional Query System](https://www.comp.nus.edu.sg/~wongls/psZ/wls-jfp98-3.ps)

https://medium.com/@fwbrasil/quill-spark-a-type-safe-scala-api-for-spark-sql-2672e8582b0d

https://typelevel.org/frameless/TypedDatasetVsSparkDataset.html

