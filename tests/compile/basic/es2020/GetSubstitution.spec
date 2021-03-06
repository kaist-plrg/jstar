            1. Assert: Type(_matched_) is String.
            1. Let _matchLength_ be the number of code units in _matched_.
            1. Assert: Type(_str_) is String.
            1. Let _stringLength_ be the number of code units in _str_.
            1. Assert: ! IsNonNegativeInteger(_position_) is *true*.
            1. Assert: _position_ ≤ _stringLength_.
            1. Assert: _captures_ is a possibly empty List of Strings.
            1. Assert: Type(_replacement_) is String.
            1. Let _tailPos_ be _position_ + _matchLength_.
            1. Let _m_ be the number of elements in _captures_.
            1. Let _result_ be the String value derived from _replacement_ by copying code unit elements from _replacement_ to _result_ while performing replacements as specified in <emu-xref href="#table-45"></emu-xref>. These `$` replacements are done left-to-right, and, once such a replacement is performed, the new replacement text is not subject to further replacements.
            1. Return _result_.