          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _s_ be ? ToString(_O_).
          1. Let _closure_ be a new Abstract Closure with no parameters that captures _s_ and performs the following steps when called:
            1. Let _position_ be 0.
            1. Let _len_ be the length of _s_.
            1. Repeat, while _position_ < _len_,
              1. Let _cp_ be ! CodePointAt(_s_, _position_).
              1. Let _nextIndex_ be _position_ + _cp_.[[CodeUnitCount]].
              1. Let _resultString_ be the substring of _s_ from _position_ to _nextIndex_.
              1. Set _position_ to _nextIndex_.
              1. Perform ? Yield(_resultString_).
            1. Return *undefined*.
          1. Return ! CreateIteratorFromClosure(_closure_, *"%StringIteratorPrototype%"*, %StringIteratorPrototype%).