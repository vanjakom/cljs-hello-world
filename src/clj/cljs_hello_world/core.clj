(ns cljs-hello-world.core
  (:require
   [clj-common.localfs :as fs]
   [clj-common.jvm :as jvm]
   [clj-common.io :as io]
   [clj-common.http-server :as server]
   [clj-common.jvm :as jvm]
   [clj-common.path :as path]
   cljs.build.api
   compojure.core))

(def handler
  (compojure.core/routes
   (compojure.core/GET
    "/loader"
    _
    (fn [request]
      (println "request loader")
      (try
        {
         :status 200
         :headers {
                  "ContentType" "application/javascript"}
         :body (cljs.build.api/build
                (path/path->string
                 (path/child
                  (jvm/jvm-path)
                  "src"
                  "cljs"
                  "myns.cljs"))
                ;; two approaches
                ;; with :optimizations :advanced ony single file is generated
                ;; but it takes a lot of time
                ;; with :optimizations :none and :main set to ns suitable file
                ;; will be generated, without :main different file is generated
                ;; output of build is result of fn call ( string ) and files
                ;; in out/ directory
                {
                 :optimizations :none
                 :main "myns"})}
        (catch Exception e
          (.printStackTrace e)
          {
           :status 500}))))
   (compojure.core/GET
    "/out/*"
    _
    (fn [request]
      (println "request " (:uri request))
      (try
        {
         :status 200
         :headers {
                  "ContentType" "application/javascript"}
         :body (with-open [is (fs/input-stream
                               (apply
                                path/child
                                (jvm/jvm-path)
                                (path/string->path (:uri request))))]
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

(-main)
