            1. If _x_ and _y_ are both *undefined*, return *+0*<sub>๐ฝ</sub>.
            1. If _x_ is *undefined*, return *1*<sub>๐ฝ</sub>.
            1. If _y_ is *undefined*, return *-1*<sub>๐ฝ</sub>.
            1. If _comparefn_ is not *undefined*, then
              1. Let _v_ be ? ToNumber(? Call(_comparefn_, *undefined*, ยซ _x_, _y_ ยป)).
              1. If _v_ is *NaN*, return *+0*<sub>๐ฝ</sub>.
              1. Return _v_.
            1. [id="step-sortcompare-tostring-x"] Let _xString_ be ? ToString(_x_).
            1. [id="step-sortcompare-tostring-y"] Let _yString_ be ? ToString(_y_).
            1. Let _xSmaller_ be the result of performing Abstract Relational Comparison _xString_ < _yString_.
            1. If _xSmaller_ is *true*, return *-1*<sub>๐ฝ</sub>.
            1. Let _ySmaller_ be the result of performing Abstract Relational Comparison _yString_ < _xString_.
            1. If _ySmaller_ is *true*, return *1*<sub>๐ฝ</sub>.
            1. Return *+0*<sub>๐ฝ</sub>.