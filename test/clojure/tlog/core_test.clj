(ns tlog.core-test
  (:require [clojure.test :refer [deftest is]]
            [clojure.string :as str]
            [tlog.core :as tl])
  (:import [java.io ByteArrayOutputStream PrintStream]))

(def dest (ByteArrayOutputStream. 256))
(alter-var-root (var tl/*destination*)
                #(identity %2)
                (PrintStream. dest))

(deftest core-test
  (let [msg "This is a warning message"]
    (is (str/index-of
         (do (tl/warn msg)
             (str dest))
         msg)))

  (let [msg "(+ 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8... => 135"]
    (is (str/index-of
         (do (tl/spy (+ 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9))
             (str dest))
         msg))))
