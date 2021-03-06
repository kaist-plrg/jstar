          1. Let _state_ be ? GeneratorValidate(_generator_, _generatorBrand_).
          1. If _state_ is ~completed~, return CreateIterResultObject(*undefined*, *true*).
          1. Assert: _state_ is either ~suspendedStart~ or ~suspendedYield~.
          1. Let _genContext_ be _generator_.[[GeneratorContext]].
          1. Let _methodContext_ be the running execution context.
          1. Suspend _methodContext_.
          1. Set _generator_.[[GeneratorState]] to ~executing~.
          1. Push _genContext_ onto the execution context stack; _genContext_ is now the running execution context.
          1. Resume the suspended evaluation of _genContext_ using NormalCompletion(_value_) as the result of the operation that suspended it. Let _result_ be the value returned by the resumed computation.
          1. Assert: When we return here, _genContext_ has already been removed from the execution context stack and _methodContext_ is the currently running execution context.
          1. Return Completion(_result_).