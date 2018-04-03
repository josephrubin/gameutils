package box.shoe.gameutils.ai;

/* pack */ class Clause
{
    public final Premise[] PREMISES;
    public final Predicate[] PREDICATES;

    public Clause(Premise[] premises, Predicate[] predicates)
    {
        PREMISES = premises;
        PREDICATES = predicates;
    }
}
