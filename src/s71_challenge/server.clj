(ns s71-challenge.server
  (:require
    [ring.adapter.jetty :as jetty]
    [integrant.core :as ig]
    [environ.core :refer [env]]
    [s71-challenge.router :as router]
    [next.jdbc :as jdbc])
  (:gen-class))

(defn app
 [env]
 (router/routes env))

(defmethod ig/prep-key :server/jetty
  [_ config]
  (merge config {:port (Integer/parseInt (env :port))}))

(defmethod ig/prep-key :db/mysql
  [_ config]
  (merge config {:jdbc-url (env :jdbc-database-url)}))

(defmethod ig/init-key :server/jetty
  [_ {:keys [handler port]}]
  (println (str "\n Server running on port " port))
  (jetty/run-jetty handler {:port port :join? false}))

(defmethod ig/init-key :s71-challenge/app
  [_ config]
  (println "\n Started app")
  (app config))

(defmethod ig/init-key :db/mysql
  [_ {:keys [jdbc-url]}]
  (println "\n Configured db")
  (jdbc/with-options jdbc-url jdbc/snake-kebab-opts))

(defmethod ig/halt-key! :server/jetty
  [_ jetty]
  (.stop jetty))

(defn -main
  [config-file]
  (let [config (-> config-file slurp ig/read-string)]
    (-> config ig/prep ig/init)))
