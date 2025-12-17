# Example of how to use test factories to increase coverage

Compile project with mvn clean install

Focus on the Opportunity class. The other class are simple constructors

Lets start by generating tests OOTB by running the command `dcover create com.example.FactoryExample.Opportunity`

Note in the creation summary the following outputs:

No tests created for:                               12 methods

INFO 8 R013: No inputs found that don't throw a trivial exception

INFO 4 R081: Exception in arrange section

Uncomment the `createOpportunity` method in OpportunityTestUtil within the src/test directory
Re-run `dcover create com.example.FactoryExample.Opportunity`

Note how new tests have been unlocked, coverage has increased and now the DiffblueTests use the factory methods.