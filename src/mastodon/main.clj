(ns mastodon.main
  (:gen-class)
  (:require [cheshire.core         :refer [parse-string]]
            [mastodon.config       :refer [config]]
            [mastodon.util         :as util]
            [mastodon.persistance  :as persist]
            [mastodon.validation   :as validation]
            [org.httpkit.client    :as http]
            [mastodon.server       :as server]
            [clojure.tools.logging :as log]))

(defn pmap-partitions
  "Realize func with pmap over a collection of collections." 
  [infunc collection]
  (doseq [i collection]
    (doall (pmap infunc i))))

(defn data-ingest
  [tileid args]
  (let [data_url       (util/inventory-url-format (:ard_host config) tileid (:from_date config) (:to_date config))
        ard_response   (http/get data_url {:timeout (:inventory_timeout config)})
        response_map   (-> (:body @ard_response) (parse-string true))
        missing_vector (:missing response_map)
        ard_partition  (partition (:partition_level config) (:partition_level config) "" missing_vector)
        ingest_map     #(persist/ingest % (:chipmunk_host config))
        autoingest     (first args)]

    (if (:error @ard_response)
      (do (log/errorf "Error response from ARD_HOST: %s" (:error @ard_response))
          (System/exit 1))
      (do (log/infof "Tile Status report for: %s \nTo be ingested: %s \nAlready ingested: %s\n" 
               tileid (count missing_vector) (:ingested response_map))))

    (if (= autoingest "-y")
      (do (pmap-partitions ingest_map ard_partition)
          (log/infof "Ingest Complete"))
      (do (println "Ingest? (y/n)")
          (if (= (read-line) "y")
            (do (pmap-partitions ingest_map ard_partition)
                (println "Ingest Complete"))
            (do (println "Exiting!")))))))

(defn -main 
  ([]
   (try
     (when (not (validation/validate-server config))
       (log/errorf "validation failed, exiting")
       (System/exit 1))
     (server/run-server config)
     (catch Exception ex
       (log/errorf "error starting Mastodon server. exception: %s" (util/exception-cause-trace ex "mastodon"))
       (System/exit 1))))
  ([tileid & args]
   (try
     (when (not (validation/validate-cli tileid config))
       (log/errorf "validation failed, exiting")
       (System/exit 1))
     (data-ingest tileid args)
     (System/exit 0)
     (catch Exception ex
       (log/errorf "Error determining tile ingest status. exception: %s" (util/exception-cause-trace ex "mastodon"))
       (System/exit 1)))))


