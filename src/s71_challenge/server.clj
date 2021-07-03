(ns s71-challenge.server
  (:require
    [ring.adapter.jetty :as jetty]
    [integrant.core :as ig]
    [environ.core :refer [env]]
    [reitit.ring :as ring]))

;(defn app
;  [env]
;  (router/routes env))

(defn app
  [env]
  (ring/ring-handler
    (ring/router
      [["/"
        {:get {:handler (fn [req] {:status 200
                                   :body   "S71 Challenge"})}}]])))

(defmethod ig/prep-key :server/jetty
  [_ config]
  (merge config {:port (Integer/parseInt (env :port))}))

(defmethod ig/prep-key :db/mysql
  [_ config]
  (merge config {:jdbc-url (env :jdbc-database)}))

(defmethod ig/init-key :server/jetty
  [_ {:keys [handler port]}]
  (println (str "\n Server running on port " port))
  (jetty/run-jetty handler {:port port :join? false}))

(defmethod ig/init-key :s71-challenge/app
  [_ config]
  (println "\n Started app")
  (app config))

(defmethod ig/init-key :db/mysql
  [_ config]
  (println "\n Configured db")
  (:jdbc-url config))

(defmethod ig/halt-key! :server/jetty
  [_ jetty]
  (.stop jetty))

(defn -main
  [config-file]
  (let [config (-> config-file slurp ig/read-string)]
    (-> config ig/prep ig/init)))
