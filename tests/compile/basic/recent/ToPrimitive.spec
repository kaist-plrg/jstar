        1. Assert: _input_ is an ECMAScript language value.
        1. If Type(_input_) is Object, then
          1. Let _exoticToPrim_ be ? GetMethod(_input_, @@toPrimitive).
          1. If _exoticToPrim_ is not *undefined*, then
            1. If _preferredType_ is not present, let _hint_ be *"default"*.
            1. Else if _preferredType_ is ~string~, let _hint_ be *"string"*.
            1. Else,
              1. Assert: _preferredType_ is ~number~.
              1. Let _hint_ be *"number"*.
            1. Let _result_ be ? Call(_exoticToPrim_, _input_, « _hint_ »).
            1. If Type(_result_) is not Object, return _result_.
            1. Throw a *TypeError* exception.
          1. If _preferredType_ is not present, let _preferredType_ be ~number~.
          1. Return ? OrdinaryToPrimitive(_input_, _preferredType_).
        1. Return _input_.