          1. Assert: _kind_ is ~key+value~ or ~value~.
          1. Perform ? RequireInternalSlot(_set_, [[SetData]]).
          1. Let _closure_ be a new Abstract Closure with no parameters that captures _set_ and _kind_ and performs the following steps when called:
            1. Let _index_ be 0.
            1. Let _entries_ be the List that is _set_.[[SetData]].
            1. Let _numEntries_ be the number of elements of _entries_.
            1. Repeat, while _index_ < _numEntries_,
              1. Let _e_ be _entries_[_index_].
              1. Set _index_ to _index_ + 1.
              1. If _e_ is not ~empty~, then
                1. If _kind_ is ~key+value~, then
                  1. Perform ? Yield(! CreateArrayFromList(« _e_, _e_ »)).
                1. Else,
                  1. Assert: _kind_ is ~value~.
                  1. Perform ? Yield(_e_).
                1. NOTE: the number of elements in _entries_ may have changed while execution of this abstract operation was paused by Yield.
                1. Set _numEntries_ to the number of elements of _entries_.
            1. Return *undefined*.
          1. Return ! CreateIteratorFromClosure(_closure_, *"%SetIteratorPrototype%"*, %SetIteratorPrototype%).