(ns core)

(println "HelloWorld from ClojureScript")

(defn ^:export transfer []
  (let [src (. js/document getElementById "src")
        dst (. js/document getElementById "dst")]
    (println "transfer fn")
    (set! (. dst -innerHTML) (. src -value))))
