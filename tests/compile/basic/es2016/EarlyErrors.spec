* It is a Syntax Error if IsValidSimpleAssignmentTarget of |LeftHandSideExpression| is *false*.
* It is a Syntax Error if the |LeftHandSideExpression| is CoverParenthesizedExpressionAndArrowParameterList : `(` Expression `)` and |Expression| derives a production that would produce a Syntax Error according to these rules if that production is substituted for |LeftHandSideExpression|. This rule is recursively applied.