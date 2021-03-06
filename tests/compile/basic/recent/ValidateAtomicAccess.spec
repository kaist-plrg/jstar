          1. Assert: _typedArray_ is an Object that has a [[ViewedArrayBuffer]] internal slot.
          1. Let _length_ be _typedArray_.[[ArrayLength]].
          1. Let _accessIndex_ be ? ToIndex(_requestIndex_).
          1. Assert: _accessIndex_ ≥ 0.
          1. If _accessIndex_ ≥ _length_, throw a *RangeError* exception.
          1. Let _arrayTypeName_ be _typedArray_.[[TypedArrayName]].
          1. Let _elementSize_ be the Element Size value specified in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _arrayTypeName_.
          1. Let _offset_ be _typedArray_.[[ByteOffset]].
          1. Return (_accessIndex_ × _elementSize_) + _offset_.