package box.shoe.gameutils.ai;

public class Clause
{
    public final Premise[] PREMISES;
    public final Predicate[] PREDICATES;

    /* pack */ Clause(Premise[] premises, Predicate[] predicates)
    {
        PREMISES = premises;
        PREDICATES = predicates;
    }
}
