            1. Assert: _constructor_ is a constructor function.
            1. Assert: _resultCapability_ is a PromiseCapability Record.
            1. Let _values_ be a new empty List.
            1. Let _remainingElementsCount_ be a new Record { [[Value]]: 1 }.
            1. Let _index_ be 0.
            1. Repeat,
              1. Let _next_ be IteratorStep(_iteratorRecord_.[[Iterator]]).
              1. If _next_ is an abrupt completion, set _iteratorRecord_.[[Done]] to *true*.
              1. ReturnIfAbrupt(_next_).
              1. If _next_ is *false*, then
                1. Set _iteratorRecord_.[[Done]] to *true*.
                1. Set _remainingElementsCount_.[[Value]] to _remainingElementsCount_.[[Value]] - 1.
                1. If _remainingElementsCount_.[[Value]] is 0, then
                  1. Let _valuesArray_ be CreateArrayFromList(_values_).
                  1. Perform ? Call(_resultCapability_.[[Resolve]], *undefined*, « _valuesArray_ »).
                1. Return _resultCapability_.[[Promise]].
              1. Let _nextValue_ be IteratorValue(_next_).
              1. If _nextValue_ is an abrupt completion, set _iteratorRecord_.[[Done]] to *true*.
              1. ReturnIfAbrupt(_nextValue_).
              1. Append *undefined* to _values_.
              1. Let _nextPromise_ be ? Invoke(_constructor_, `"resolve"`, « _nextValue_ »).
              1. Let _resolveElement_ be a new built-in function object as defined in <emu-xref href="#sec-promise.all-resolve-element-functions" title></emu-xref>.
              1. Set _resolveElement_.[[AlreadyCalled]] to a new Record { [[Value]]: *false* }.
              1. Set _resolveElement_.[[Index]] to _index_.
              1. Set _resolveElement_.[[Values]] to _values_.
              1. Set _resolveElement_.[[Capability]] to _resultCapability_.
              1. Set _resolveElement_.[[RemainingElements]] to _remainingElementsCount_.
              1. Set _remainingElementsCount_.[[Value]] to _remainingElementsCount_.[[Value]] + 1.
              1. Perform ? Invoke(_nextPromise_, `"then"`, « _resolveElement_, _resultCapability_.[[Reject]] »).
              1. Set _index_ to _index_ + 1.