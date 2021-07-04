(ns s71-challenge.test-system
  (:require [integrant.repl.state :as state]
            [muuntaja.core :as m]
            [ring.mock.request :as mock]))


(defn test-endpoint
  ([method uri]
   (test-endpoint method uri nil))
  ([method uri opts]
   (let [app (-> state/system :s71-challenge/app)
         request (app (-> (mock/request method uri)
                          (cond-> (:body opts) (mock/json-body (:body opts)))))]
     (update request :body (fn [data] (m/decode "application/json" data))))))
