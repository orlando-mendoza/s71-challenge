(ns s71-challenge.queue.routes
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
               :responses {200 {:body [{:success boolean?
                                        :generated-key int?}]}}
               :summary "Pushes the given messages to a queue"}}]]]))

(comment
  (s71-challenge.test-system/test-endpoint :post "/v1/push"
                                           {:body [{:message "message3"
                                                    :message-type "AB"}
                                                   {:message "message5"
                                                    :message-type "1"}]})
  ;;
  )
