(ns cljs-hello-world.core
  (:require
   [clj-common.localfs :as fs]
   [clj-common.jvm :as jvm]
   [clj-common.io :as io]
   [clj-common.http-server :as server]
   compojure.core))

(def handler
  (compojure.core/routes
   (compojure.core/GET
    "/core.js"
    _
    (fn [request]
      (println "request core.js")
      (try
        {
         :status 200
         :headers {
                  "ContentType" "application/javascript"}
         :body (with-open [is (fs/input-stream ["tmp" "out" "main.js"])]
                (io/bytes->input-stream
                 (io/input-stream->byte-array is)))}
        (catch Exception e
          (.printStackTrace e)
          {
           :status 500}))))
   (compojure.core/GET
    "/"
    _
    (fn [request]
      (println "request index.html")
      (try
        {
         :status 200
         :headers {
                  "ContentType" "text/html"}
         :body (with-open [is (jvm/resource-as-stream ["index.html"])]
                (io/bytes->input-stream
                 (io/input-stream->byte-array is)))}
        (catch Exception e
          (.printStackTrace e)
          {
           :status 500}))))))

(defn -main [& args]
  (println "starting server at 8080")
  (server/create-server 8080 handler)
  (println "server-started"))

;; build clojurescript part
;; https://purelyfunctional.tv/mini-guide/building-clojurescript-process/
(require 'cljs.build.api)

(cljs.build.api/build "/Users/vanja/projects/cljs-hello-world/src/cljs/core.cljs"
  {:output-to "/tmp/out/main.js"
   :optimizations :advanced})

;; faster but I don't know how to link generated dependencies
;; some files are generated in ./out/
#_(cljs.build.api/build "/Users/vanja/projects/cljs-hello-world/src/cljs/core.cljs"
  {:output-to "/tmp/out-min/main.js"
   :optimizations :none})

(-main)

