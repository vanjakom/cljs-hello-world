(ns myns)

(println "loading myns")

(defn create-value [value]
  (let [element (. js/document createElement "div")]
    (set! (. element -innerHTML) value)
    element))

(.
 (. js/document -body)
 appendChild
 (create-value "HelloWorld from ClojureScript"))

(defn ^:export transfer []
  (let [src (. js/document getElementById "src")
        dst (. js/document getElementById "dst")]
    (println "transfer fn executing")
    (set! (. dst -innerHTML) (. src -value))))
