          1. Assert: _generator_ is an AsyncGenerator instance.
          1. Assert: _generator_.[[AsyncGeneratorState]] is *undefined*.
          1. Let _genContext_ be the running execution context.
          1. Set the Generator component of _genContext_ to _generator_.
          1. Set the code evaluation state of _genContext_ such that when evaluation is resumed for that execution context the following steps will be performed:
            1. Let _result_ be the result of evaluating _generatorBody_.
            1. Assert: If we return here, the async generator either threw an exception or performed either an implicit or explicit return.
            1. Remove _genContext_ from the execution context stack and restore the execution context that is at the top of the execution context stack as the running execution context.
            1. Set _generator_.[[AsyncGeneratorState]] to `"completed"`.
            1. If _result_ is a normal completion, let _resultValue_ be *undefined*.
            1. Else,
              1. Let _resultValue_ be _result_.[[Value]].
              1. If _result_.[[Type]] is not ~return~, then
                1. Return ! AsyncGeneratorReject(_generator_, _resultValue_).
            1. Return ! AsyncGeneratorResolve(_generator_, _resultValue_, *true*).
          1. Set _generator_.[[AsyncGeneratorContext]] to _genContext_.
          1. Set _generator_.[[AsyncGeneratorState]] to `"suspendedStart"`.
          1. Set _generator_.[[AsyncGeneratorQueue]] to a new empty List.
          1. Return *undefined*.