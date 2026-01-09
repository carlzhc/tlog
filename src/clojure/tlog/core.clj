(ns tlog.core
  (:require [clojure.string :as s]
            [clj-stacktrace.repl :as stacktrace]
            [java-time.api :as jt])
  (:import [java.io ByteArrayOutputStream PrintWriter]))

(set! *warn-on-reflection* true)

(def ^:private levels (zipmap [:trace :debug :info :notice :warn :error :fatal] (range)))
(def limit (atom (:trace levels)))
(def time-fmt (atom "yyyy/MM/dd HH:mm:ss.SSS"))
(def truncate-at (atom nil))
(def truncate-suffix (atom nil))
(def destination (atom System/out))


(defn set-limit! [level]
  (when-not (levels level) (throw (IllegalArgumentException. (str "unknown log level:" level))))
  (reset! limit (levels level)))

(defn make-log-message ^bytes [file line level format-args & args]
  (let [buffer (ByteArrayOutputStream. 256)
        out (PrintWriter. buffer)
        e (when (instance? Throwable (first args)) (first args))
        args (if e (rest args) args)]
    (doto out
      (.print (name level))
      (.print \space)
      (.print (jt/format @time-fmt (jt/local-date-time)))
      (.print " [")
      (.print file)
      (.print \:)
      (.print line)
      (.print "]: "))
    (.println out (if format-args
                    (apply format args)
                    (s/join \space args)))
    (when e
      (stacktrace/pst-on out false e))
    (.flush out)
    (.toByteArray buffer)))


(defmacro -log [level file line format-args & args]
  `(when (>= ~(levels level) (deref limit))
     (.write (deref destination) (make-log-message ~file ~line ~level ~format-args ~@args))
     (.flush (deref destination))))

(defmacro log [level & args] `(-log ~level ~*file* ~(:line (meta &form)) false ~@args))
(defmacro logf [level & args] `(-log ~level ~*file* ~(:line (meta &form)) true ~@args))

(defmacro trace  [& args] `(-log :trace  ~*file* ~(:line (meta &form)) false ~@args))
(defmacro debug  [& args] `(-log :debug  ~*file* ~(:line (meta &form)) false ~@args))
(defmacro info   [& args] `(-log :info   ~*file* ~(:line (meta &form)) false ~@args))
(defmacro notice [& args] `(-log :notice ~*file* ~(:line (meta &form)) false ~@args))
(defmacro warn   [& args] `(-log :warn   ~*file* ~(:line (meta &form)) false ~@args))
(defmacro error  [& args] `(-log :error  ~*file* ~(:line (meta &form)) false ~@args))
(defmacro fatal  [& args] `(-log :fatal  ~*file* ~(:line (meta &form)) false ~@args))

(defmacro tracef  [& args] `(-log :trace  ~*file* ~(:line (meta &form)) true ~@args))
(defmacro debugf  [& args] `(-log :debug  ~*file* ~(:line (meta &form)) true ~@args))
(defmacro infof   [& args] `(-log :info   ~*file* ~(:line (meta &form)) true ~@args))
(defmacro noticef [& args] `(-log :notice ~*file* ~(:line (meta &form)) true ~@args))
(defmacro warnf   [& args] `(-log :warn   ~*file* ~(:line (meta &form)) true ~@args))
(defmacro errorf  [& args] `(-log :error  ~*file* ~(:line (meta &form)) true ~@args))
(defmacro fatalf  [& args] `(-log :fatal  ~*file* ~(:line (meta &form)) true ~@args))

;; Truncate a string to `max-len` characters, optionally adding a suffix like "..."
(defn- truncate
  ([s]
   (truncate s @truncate-at @truncate-suffix))
  ([s max-len]
   (truncate s max-len @truncate-suffix))

  ([s max-len suffix]
   (cond
     (nil? s) nil
     (not (string? s)) (throw (IllegalArgumentException.
                               "Input must be a string"))
     (<= (count s) max-len) s
     :else (str (subs s 0 max-len) suffix))))

(defmacro spy [arg]
  "Eval the arg, log the output, then return the result."
  `(let [val# ~arg]
     (-log :debug ~*file* ~(:line (meta &form)) false
           (str (truncate (str (quote arg)) (or @trancate-at 40) (or @truncate-suffix "...")) " => " val#))
     val#))
