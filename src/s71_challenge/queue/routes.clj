(ns s71-challenge.routes
  (:require [s71-challenge.queue.handler :as handler]))

(defn routes
  [env]
  (let [db (:jdbc-url env)]
    [""
     {:swagger {:tags ["Fifo multi-queue"]}}
     [["/push"
       {:post {:handler (handler/push-messages db)
               :parameters {:body [{:message string?
                                    :message-type string?}]}
               :summary "Pushes the given messages to a queue"}}]]]))
