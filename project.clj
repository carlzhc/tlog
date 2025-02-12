(defproject com.github.carlzhc/tlog "0.1.0"
  :description "A tiny log for Clojure"
  :url "https://github.com:carlzhc/tlog"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clojure.java-time "1.4.2"]
                 [clj-stacktrace "0.2.5"]]

  :source-paths ["src/clojure"]
  :resource-paths ["res/clojure"]
  :test-paths ["test/clojure"])
