          1. Return a new Matcher with parameters (_x_, _c_) that captures nothing and performs the following steps when called:
            1. Assert: _x_ is a State.
            1. Assert: _c_ is a Continuation.
            1. Let _e_ be _x_'s _endIndex_.
            1. If _e_ is equal to _InputLength_, or if _Multiline_ is *true* and the character _Input_[_e_] is one of |LineTerminator|, then
              1. Call _c_(_x_) and return its result.
            1. Return ~failure~.