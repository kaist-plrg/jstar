            1. If _exponent_ is *NaN*, return *NaN*.
            1. If _exponent_ is *+0*<sub>𝔽</sub> or _exponent_ is *-0*<sub>𝔽</sub>, return *1*<sub>𝔽</sub>.
            1. If _base_ is *NaN*, return *NaN*.
            1. If _base_ is *+∞*<sub>𝔽</sub>, then
              1. If _exponent_ > *+0*<sub>𝔽</sub>, return *+∞*<sub>𝔽</sub>. Otherwise, return *+0*<sub>𝔽</sub>.
            1. If _base_ is *-∞*<sub>𝔽</sub>, then
              1. If _exponent_ > *+0*<sub>𝔽</sub>, then
                1. If _exponent_ is an odd integral Number, return *-∞*<sub>𝔽</sub>. Otherwise, return *+∞*<sub>𝔽</sub>.
              1. Else,
                1. If _exponent_ is an odd integral Number, return *-0*<sub>𝔽</sub>. Otherwise, return *+0*<sub>𝔽</sub>.
            1. If _base_ is *+0*<sub>𝔽</sub>, then
              1. If _exponent_ > *+0*<sub>𝔽</sub>, return *+0*<sub>𝔽</sub>. Otherwise, return *+∞*<sub>𝔽</sub>.
            1. If _base_ is *-0*<sub>𝔽</sub>, then
              1. If _exponent_ > *+0*<sub>𝔽</sub>, then
                1. If _exponent_ is an odd integral Number, return *-0*<sub>𝔽</sub>. Otherwise, return *+0*<sub>𝔽</sub>.
              1. Else,
                1. If _exponent_ is an odd integral Number, return *-∞*<sub>𝔽</sub>. Otherwise, return *+∞*<sub>𝔽</sub>.
            1. Assert: _base_ is finite and is neither *+0*<sub>𝔽</sub> nor *-0*<sub>𝔽</sub>.
            1. If _exponent_ is *+∞*<sub>𝔽</sub>, then
              1. If abs(ℝ(_base_)) > 1, return *+∞*<sub>𝔽</sub>.
              1. If abs(ℝ(_base_)) is 1, return *NaN*.
              1. If abs(ℝ(_base_)) < 1, return *+0*<sub>𝔽</sub>.
            1. If _exponent_ is *-∞*<sub>𝔽</sub>, then
              1. If abs(ℝ(_base_)) > 1, return *+0*<sub>𝔽</sub>.
              1. If abs(ℝ(_base_)) is 1, return *NaN*.
              1. If abs(ℝ(_base_)) < 1, return *+∞*<sub>𝔽</sub>.
            1. Assert: _exponent_ is finite and is neither *+0*<sub>𝔽</sub> nor *-0*<sub>𝔽</sub>.
            1. If _base_ < *+0*<sub>𝔽</sub> and _exponent_ is not an integral Number, return *NaN*.
            1. Return an implementation-approximated value representing the result of raising ℝ(_base_) to the ℝ(_exponent_) power.