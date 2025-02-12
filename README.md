# tlog
A tiny log library for Clojure

## usage
In your project.clj file, add:
[![Clojars Project](https://img.shields.io/clojars/v/com.github.carlzhc/tlog.svg)](https://clojars.org/com.github.carlzhc/tlog)

In your clojure files, require it:
```(require '[tlog.core :as log])```

## api functions
- log
- logf
- trace
- debug
- info
- warn
- error
- fatal
- tracef
- debugf
- infof
- warnf
- errorf
- fatalf
- set-limit!

## example
```
(log/set-limit! :debug) ; any keyword from [:trace :debug :info :warn :error :fatal]
(log/debug "hello world")
(log/debugf "hello %s" "world")
```

