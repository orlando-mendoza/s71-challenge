(ns s71-challenge.queue.routes
  (:require [s71-challenge.queue.handler :as handler]
            [spec-tools.data-spec :as ds]))

(defn routes
  [env]
  (let [db (:jdbc-url env)]
    [""
     {:swagger {:tags ["Fifo multi-queue"]}}
     [["/push"
       {:post {:handler (handler/push-messages db)
               :parameters {:body [{:message string?
                                    :message-type string?}]}
               ;:responses {200 {:body [{
               ;                         :generated-key int?
               ;                         :success boolean?
               ;                         }]}}
               :summary "Pushes the given messages to a queue"}}]
      ["/peek"
       {:post {:handler (handler/peek-messages db)
               :parameters {:body {:message-type string?
                                   :limit int?}}
               ;:responses {200 {:body [{:id int?
               ;                         :message string?
               ;                         :message-type string?
               ;                         :hidden boolean?
               ;                         :created-at inst?}]}}
               :summary "Returns one or more messages from the queue."}}]]]))




(comment
  (s71-challenge.test-system/test-endpoint :post "/v1/push"
                                           {:body [{:message      "message 11"
                                                    :message-type "AB"}
                                                   {:message      "message 16"
                                                    :message-type "B"}]})

  (s71-challenge.test-system/test-endpoint :post "/v1/peek"
                                           {:body {:message-type "AB"
                                                   :limit        5}})
  ;;
  )
