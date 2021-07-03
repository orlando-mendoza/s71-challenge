(ns s71-challenge.queue.db
  (:require [next.jdbc.sql :as sql]
            ))

(defn push-messages
  [db messages]
  (let [cols [:message :message-type]]
    (sql/insert-multi! db :queue cols (mapv (apply juxt cols) messages))))


(comment

  (def messages
    [{:message "message1"
      :message-type "A"}
     {:message "message2"
      :message-type "B"}])

  (let [db (-> integrant.repl.state/system :db/mysql)]
    (push-messages db messages))
  ;;
  )
