(ns s71-challenge.queue.db
  (:require [next.jdbc.sql :as sql]
[next.jdbc.result-set :as rs]
            [next.jdbc :as jdbc]))

(defn push-messages
  [db messages]
  (let [cols [:message :message-type]]
    (sql/insert-multi! db :queue cols (mapv (apply juxt cols) messages))))

(defn peek-messages
  "Returns one or more messages, not hidden, from the queue.
  May be filtered by message-type or limit, default to 1."
  ([db]
   (peek-messages db "%" 1))

  ([db message-type]
   (peek-messages db message-type 1))

  ([db message-type limit]
   (with-open [conn (jdbc/get-connection db)]
     (let [select (str "SELECT *
                    FROM queue
                    WHERE hidden IS false
                    AND message_type LIKE '" message-type "'")
           lmit (str " LIMIT " (if (= 0 limit)
                                 1
                                 (or limit 1)))]
       (jdbc/execute! conn [(str select " ORDER BY id ASC" lmit)]
                      {:builder-fn rs/as-unqualified-kebab-maps})))))

(comment
  (let [db (-> integrant.repl.state/system :db/mysql)]
    (peek-messages db))

  (let [db (-> integrant.repl.state/system :db/mysql)]
    (peek-messages db "ABC" 2))


  ;;
  )
