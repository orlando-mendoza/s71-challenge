(ns s71-challenge.queue-tests
  (:require [clojure.test :refer [is deftest testing]]
            [s71-challenge.test-system :as ts]))

(def ids (atom []))

(defn get-ids
  [data]
   (->> data
          (map :generated-key)
          (map #(merge {:id %}))
          (into [])))

(deftest fifo-queue-tests
  (testing "Push messages"
    (let [{:keys [status body]} (ts/test-endpoint :post "/v1/push"
                                                  {:body [{:message (str "message " (rand-int 9999))
                                                           :message-type "TEST"}
                                                          {:message (str "message " (rand-int 9999))
                                                           :message-type "TEST"}]})]
      (reset! ids (get-ids body))
      (is (= 200 status))
      (is (vector? body))))

  (testing "Peek messages"
    (let [{:keys [status body]} (ts/test-endpoint :get "/v1/peek")]
      (is (= 200 status))
      (is (vector? body))
      (is (= (count body) 1))))

  (testing "Peek messages with message-type and limit"
    (let [{:keys [status body]} (ts/test-endpoint :get "/v1/peek?message-type=TEST&limit=2")]
      (is (= 200 status))
      (is (vector? body))
      (is (= (count body) 2))
      (is (= "TEST" (-> body first :message-type)))))

  (testing "Pop messages"
    (let [{:keys [status body]} (ts/test-endpoint :post "/v1/pop"
                                                  {:body {:ttl 1}})]
      (is (= 200 status))
      (is (vector? body))
      (is (= (count body) 1))))

  (testing "Pop messages with message-type and limit."
    (let [{:keys [status body]} (ts/test-endpoint :post "/v1/pop?message-type=TEST&limit=2"
                                                  {:body {:ttl 1}})]
      (is (= 200 status))
      (is (vector? body))
      (is (= (count body) 2))
      (is (= "TEST" (-> body first :message-type)))))

  (testing "Queue Length"
    (let [{:keys [status body]} (ts/test-endpoint :get "/v1/queue-length")]
      (is (= 200 status))
      (is (map? body))))

  (testing "Queue Length with message-type and hidden messages."
    (let [{:keys [status body]} (ts/test-endpoint :get "/v1/queue-length?message-type=TEST&with-hidden?=true")]
      (is (= 200 status))
      (is (map? body))))

  (testing "Confirm messages"
    (let [{:keys [status body]} (ts/test-endpoint :post "/v1/confirm"
                                                  {:body @ids})]
      (is (= 204 status))
      (is (nil? body)))))
