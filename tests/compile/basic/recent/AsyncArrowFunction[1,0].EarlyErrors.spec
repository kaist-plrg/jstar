        <li>It is a Syntax Error if |CoverCallExpressionAndAsyncArrowHead| Contains |YieldExpression| is *true*.</li>
        <li>It is a Syntax Error if |CoverCallExpressionAndAsyncArrowHead| Contains |AwaitExpression| is *true*.</li>
        <li>It is a Syntax Error if |CoverCallExpressionAndAsyncArrowHead| is not covering an |AsyncArrowHead|.</li>
        <li>It is a Syntax Error if any element of the BoundNames of |CoverCallExpressionAndAsyncArrowHead| also occurs in the LexicallyDeclaredNames of |AsyncConciseBody|.</li>
        <li>It is a Syntax Error if AsyncConciseBodyContainsUseStrict of |AsyncConciseBody| is *true* and IsSimpleParameterList of |CoverCallExpressionAndAsyncArrowHead| is *false*.</li>
        <li>All Early Error rules for |AsyncArrowHead| and its derived productions apply to CoveredAsyncArrowHead of |CoverCallExpressionAndAsyncArrowHead|.</li>