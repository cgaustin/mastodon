(ns mastodon.clj.server
   (:require [clojure.string           :as string]
             [compojure.core           :as compojure]
             [compojure.route          :as route]
             [environ.core             :as environ]
             [mastodon.cljc.ard        :as ard]
             [mastodon.cljc.util       :as util]
             [mastodon.clj.file        :as file]
             [mastodon.clj.persistance :as persist]
             [ring.middleware.json     :as ring-json]))

(def iwds-host (:iwds-host environ/env))
(def ard-host  (:ard-host  environ/env))
(def ard-path  (:ard-path  environ/env))

(defn bulk-ingest
  "Generate ingest requests for list of posted ARD."
  [{:keys [:body] :as req}]
  (let [tifs    (string/split (:urls body) #",")
        results (doall (pmap #(persist/ingest % iwds-host) tifs))]
    {:status 200 :body results}))

(defn ard-status
  "Determine the ARD ingest status for the given tile id."
  [tileid]
  (let [hvmap    (util/hv-map tileid)
        filepath (str ard-path (:h hvmap) "/" (:v hvmap) "/*")
        ardtifs  (-> filepath (file/get-filenames "tar")
                              (#(map ard/ard-manifest %))
                              (flatten))
        ard_res  (doall (pmap #(persist/status-check % iwds-host (str ard-host "/ard")) ardtifs))
        missing  (-> ard_res (#(filter (fn [i] (= (vals i) '("[]"))) %)) 
                             (#(apply merge-with concat %)) 
                             (keys))]
      {:status 200 :body {:ingested (- (count ard_res) (count missing)) :missing missing}}))

(defn get-base [request]
  "Hello Mastodon."
  {:status 200 :body ["Would you like some ARD with that?"]})

;; ## Routes
(compojure/defroutes routes
  (compojure/context "/" request
    (route/resources "/")
    (compojure/GET   "/" [] (get-base request))
    (compojure/GET   "/inventory/:tileid{[0-9]{6}}" [tileid] (ard-status tileid))
    (compojure/POST  "/bulk-ingest" [] (bulk-ingest request))))

(def app (-> routes
             (ring-json/wrap-json-body {:keywords? true})
             (ring-json/wrap-json-response)))