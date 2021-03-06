            1. If _n_ is *NaN* or _d_ is *NaN*, return *NaN*.
            1. If _n_ is *+∞*<sub>𝔽</sub> or _n_ is *-∞*<sub>𝔽</sub>, return *NaN*.
            1. If _d_ is *+∞*<sub>𝔽</sub> or _d_ is *-∞*<sub>𝔽</sub>, return _n_.
            1. If _d_ is *+0*<sub>𝔽</sub> or _d_ is *-0*<sub>𝔽</sub>, return *NaN*.
            1. If _n_ is *+0*<sub>𝔽</sub> or _n_ is *-0*<sub>𝔽</sub>, return _n_.
            1. Assert: _n_ and _d_ are finite and non-zero.
            1. Let _r_ be ℝ(_n_) - (ℝ(_d_) × _q_) where _q_ is an integer that is negative if and only if _n_ and _d_ have opposite sign, and whose magnitude is as large as possible without exceeding the magnitude of ℝ(_n_) / ℝ(_d_).
            1. Return 𝔽(_r_).