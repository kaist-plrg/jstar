        1. Let _scope_ be the running execution context's LexicalEnvironment.
        1. Let _funcEnv_ be NewDeclarativeEnvironment(_scope_).
        1. Let _name_ be StringValue of |BindingIdentifier|.
        1. Perform _funcEnv_.CreateImmutableBinding(_name_, *false*).
        1. Let _sourceText_ be the source text matched by |GeneratorExpression|.
        1. Let _closure_ be OrdinaryFunctionCreate(%GeneratorFunction.prototype%, _sourceText_, |FormalParameters|, |GeneratorBody|, ~non-lexical-this~, _funcEnv_).
        1. Perform SetFunctionName(_closure_, _name_).
        1. Let _prototype_ be ! OrdinaryObjectCreate(%GeneratorFunction.prototype.prototype%).
        1. Perform DefinePropertyOrThrow(_closure_, *"prototype"*, PropertyDescriptor { [[Value]]: _prototype_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *false* }).
        1. Perform _funcEnv_.InitializeBinding(_name_, _closure_).
        1. Return _closure_.