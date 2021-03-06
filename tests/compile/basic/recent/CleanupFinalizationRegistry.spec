      1. Assert: _finalizationRegistry_ has [[Cells]] and [[CleanupCallback]] internal slots.
      1. Let _callback_ be _finalizationRegistry_.[[CleanupCallback]].
      1. While _finalizationRegistry_.[[Cells]] contains a Record _cell_ such that _cell_.[[WeakRefTarget]] is ~empty~, an implementation may perform the following steps:
        1. Choose any such _cell_.
        1. Remove _cell_ from _finalizationRegistry_.[[Cells]].
        1. Perform ? Call(_callback_, *undefined*, « _cell_.[[HeldValue]] »).
      1. Return NormalCompletion(*undefined*).