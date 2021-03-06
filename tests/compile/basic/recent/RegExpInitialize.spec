            1. If _pattern_ is *undefined*, let _P_ be the empty String.
            1. Else, let _P_ be ? ToString(_pattern_).
            1. If _flags_ is *undefined*, let _F_ be the empty String.
            1. Else, let _F_ be ? ToString(_flags_).
            1. If _F_ contains any code unit other than *"g"*, *"i"*, *"m"*, *"s"*, *"u"*, or *"y"* or if it contains the same code unit more than once, throw a *SyntaxError* exception.
            1. If _F_ contains *"u"*, let _u_ be *true*; else let _u_ be *false*.
            1. If _u_ is *true*, then
              1. Let _patternText_ be ! StringToCodePoints(_P_).
              1. Let _patternCharacters_ be a List whose elements are the code points of _patternText_.
            1. Else,
              1. Let _patternText_ be the result of interpreting each of _P_'s 16-bit elements as a Unicode BMP code point. UTF-16 decoding is not applied to the elements.
              1. Let _patternCharacters_ be a List whose elements are the code unit elements of _P_.
            1. Let _parseResult_ be ParsePattern(_patternText_, _u_).
            1. If _parseResult_ is a non-empty List of *SyntaxError* objects, throw a *SyntaxError* exception.
            1. Assert: _parseResult_ is a Parse Node for |Pattern|.
            1. Set _obj_.[[OriginalSource]] to _P_.
            1. Set _obj_.[[OriginalFlags]] to _F_.
            1. Set _obj_.[[RegExpMatcher]] to the Abstract Closure that evaluates _parseResult_ by applying the semantics provided in <emu-xref href="#sec-pattern-semantics"></emu-xref> using _patternCharacters_ as the pattern's List of |SourceCharacter| values and _F_ as the flag parameters.
            1. Perform ? Set(_obj_, *"lastIndex"*, *+0*<sub>𝔽</sub>, *true*).
            1. Return _obj_.