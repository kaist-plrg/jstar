          1. Let _keyResult_ be ? ForIn/OfHeadEvaluation(« », |AssignmentExpression|, ~iterate~).
          1. Return ? ForIn/OfBodyEvaluation(|ForBinding|, |Statement|, _keyResult_, ~iterate~, ~varBinding~, _labelSet_).