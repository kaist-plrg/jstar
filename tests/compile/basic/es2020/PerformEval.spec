          1. Assert: If _direct_ is *false*, then _strictCaller_ is also *false*.
          1. If Type(_x_) is not String, return _x_.
          1. Let _evalRealm_ be the current Realm Record.
          1. Perform ? HostEnsureCanCompileStrings(_callerRealm_, _evalRealm_).
          1. Let _thisEnvRec_ be ! GetThisEnvironment().
          1. If _thisEnvRec_ is a function Environment Record, then
            1. Let _F_ be _thisEnvRec_.[[FunctionObject]].
            1. Let _inFunction_ be *true*.
            1. Let _inMethod_ be _thisEnvRec_.HasSuperBinding().
            1. If _F_.[[ConstructorKind]] is ~derived~, let _inDerivedConstructor_ be *true*; otherwise, let _inDerivedConstructor_ be *false*.
          1. Else,
            1. Let _inFunction_ be *false*.
            1. Let _inMethod_ be *false*.
            1. Let _inDerivedConstructor_ be *false*.
          1. Perform the following substeps in an implementation-dependent order, possibly interleaving parsing and error detection:
            1. Let _script_ be the ECMAScript code that is the result of parsing ! UTF16DecodeString(_x_), for the goal symbol |Script|. If the parse fails, throw a *SyntaxError* exception. If any early errors are detected, throw a *SyntaxError* exception (but see also clause <emu-xref href="#sec-error-handling-and-language-extensions"></emu-xref>).
            1. If _script_ Contains |ScriptBody| is *false*, return *undefined*.
            1. Let _body_ be the |ScriptBody| of _script_.
            1. If _inFunction_ is *false*, and _body_ Contains |NewTarget|, throw a *SyntaxError* exception.
            1. If _inMethod_ is *false*, and _body_ Contains |SuperProperty|, throw a *SyntaxError* exception.
            1. If _inDerivedConstructor_ is *false*, and _body_ Contains |SuperCall|, throw a *SyntaxError* exception.
          1. If _strictCaller_ is *true*, let _strictEval_ be *true*.
          1. Else, let _strictEval_ be IsStrict of _script_.
          1. Let _runningContext_ be the running execution context.
          1. NOTE: If _direct_ is *true*, _runningContext_ will be the execution context that performed the direct eval. If _direct_ is *false*, _runningContext_ will be the execution context for the invocation of the `eval` function.
          1. If _direct_ is *true*, then
            1. Let _lexEnv_ be NewDeclarativeEnvironment(_runningContext_'s LexicalEnvironment).
            1. Let _varEnv_ be _runningContext_'s VariableEnvironment.
          1. Else,
            1. Let _lexEnv_ be NewDeclarativeEnvironment(_evalRealm_.[[GlobalEnv]]).
            1. Let _varEnv_ be _evalRealm_.[[GlobalEnv]].
          1. If _strictEval_ is *true*, set _varEnv_ to _lexEnv_.
          1. If _runningContext_ is not already suspended, suspend _runningContext_.
          1. Let _evalContext_ be a new ECMAScript code execution context.
          1. Set _evalContext_'s Function to *null*.
          1. Set _evalContext_'s Realm to _evalRealm_.
          1. Set _evalContext_'s ScriptOrModule to _runningContext_'s ScriptOrModule.
          1. Set _evalContext_'s VariableEnvironment to _varEnv_.
          1. Set _evalContext_'s LexicalEnvironment to _lexEnv_.
          1. Push _evalContext_ onto the execution context stack; _evalContext_ is now the running execution context.
          1. Let _result_ be EvalDeclarationInstantiation(_body_, _varEnv_, _lexEnv_, _strictEval_).
          1. If _result_.[[Type]] is ~normal~, then
            1. Set _result_ to the result of evaluating _body_.
          1. If _result_.[[Type]] is ~normal~ and _result_.[[Value]] is ~empty~, then
            1. Set _result_ to NormalCompletion(*undefined*).
          1. Suspend _evalContext_ and remove it from the execution context stack.
          1. Resume the context that is now on the top of the execution context stack as the running execution context.
          1. Return Completion(_result_).