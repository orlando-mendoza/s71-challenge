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
               :responses {200 {:body [{:generated-key number?
                                        :success boolean?}]}}
               :summary "Pushes the given messages to a queue"}}]
      ["/peek"
       {:post {:handler (handler/peek-messages db)
               :parameters {:query {(ds/opt :message-type) string?
                                    (ds/opt :limit) int?}}
               :responses {200 {:body [{:id number?
                                        :message string?
                                        :message-type string?
                                        :hidden boolean?
                                        :created-at inst?}]}}
               :summary "Returns one or more messages from the queue."}}]
      ["/pop"
       {:post {:handler (handler/pop-messages db)
               :parameters {:body {:ttl number?}
                            :query {(ds/opt :message-type) string?
                                    (ds/opt :limit) int?}}
               :summary "Returns one or more messages from the queue. Messages are hidden during ttl secs."}}]
      ["/confirm"
       {:post {:handler (handler/confirm-messages db)
               :parameters {:body [{:id int?}]}
               :responses {204 {:body nil?}}
               :summary "Deletes the given messages from a queue by message id"}}]]]))




(comment
  (s71-challenge.test-system/test-endpoint :post "/v1/push"
                                           {:body [{:message      "message 1121"
                                                    :message-type "ABC"}
                                                   {:message      "message 1621"
                                                    :message-type "ABC"}]})

  (s71-challenge.test-system/test-endpoint :post "/v1/peek"
                                           {:body {:message-type "AB"
                                                   :limit        11}})

  (s71-challenge.test-system/test-endpoint :post  "/v1/peek?message-type=AB&limit=2")

  (s71-challenge.test-system/test-endpoint :post  "/v1/peek")

  (s71-challenge.test-system/test-endpoint :post  "/v1/pop?limit=5"
                                           {:body {:ttl 10}})

  (s71-challenge.test-system/test-endpoint :post  "/v1/confirm"
                                           {:body [{:id 10}
                                                   {:id 31}
                                                   {:id 32}]})
  ;;
  )
