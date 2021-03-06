          1. Return a new Matcher with parameters (_x_, _c_) that captures nothing and performs the following steps when called:
            1. Assert: _x_ is a State.
            1. Assert: _c_ is a Continuation.
            1. Let _e_ be _x_'s _endIndex_.
            1. Let _a_ be ! IsWordChar(_e_ - 1).
            1. Let _b_ be ! IsWordChar(_e_).
            1. If _a_ is *true* and _b_ is *false*, or if _a_ is *false* and _b_ is *true*, return _c_(_x_).
            1. Return ~failure~.