package box.shoe.gameutils.ai;

import java.util.Deque;

/* pack */ interface Result
{
    void resolve(Deque<Behavior> stack);

    /* pack */ final class PushResult implements Result
    {
        private final Outcome OUTCOME;

        public PushResult(Outcome outcome)
        {
            this.OUTCOME = outcome;
        }

        @Override
        public void resolve(Deque<Behavior> stack)
        {
            stack.push(OUTCOME.BEHAVIOR);
        }
    }

    /* pack */ final class SwapResult implements Result
    {
        private final Outcome OUTCOME;

        public SwapResult(Outcome outcome)
        {
            this.OUTCOME = outcome;
        }

        @Override
        public void resolve(Deque<Behavior> stack)
        {
            stack.pop();
            stack.push(OUTCOME.BEHAVIOR);
        }
    }

    /* pack */ final class PopResult implements Result
    {
        @Override
        public void resolve(Deque<Behavior> stack)
        {
            stack.pop();
        }
    }
}
