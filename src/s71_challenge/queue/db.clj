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

(defn hide-message
  [db {:keys [id]} hide?]
  (sql/update! db :queue {:hidden hide?} {:id id}))

(defn pop-message
  ([db]
   (pop-message db "%" 1))

  ([db message-type]
   (pop-message db message-type 1))

  ([db message-type limit]
   (let [messages (peek-messages db message-type limit)]
     ;; other worker could pop the same messages while they're not updated
     ;; consider using transaction with SELECT... FOR UPDATE
     (pmap #(hide-message db % true) messages)
     messages)))

(defn return-messages-to-front
  [db messages]
  (pmap #(hide-message db % false) messages))

(defn delete-message
  [db {:keys [id]}]
  (sql/delete! db :queue {:id id}))

(defn confirm-messages
  [db messages]
  (pmap #(delete-message db %) messages))

(defn queue-length
  [db message-type with-hidden?]
  (let [hidden (if with-hidden? "%" false)]
    (with-open [conn (jdbc/get-connection db)]
      (jdbc/execute! conn [(str "SELECT count(*)
                          FROM queue
                          WHERE message_type LIKE '" message-type
                                "' AND hidden LIKE " hidden)]))))



(comment
  (let [db (-> integrant.repl.state/system :db/mysql)]
    (peek-messages db))

  (let [db (-> integrant.repl.state/system :db/mysql)]
    (peek-messages db "AB" 3))

  (let [db (-> integrant.repl.state/system :db/mysql)]
    (pop-message db))

  (let [db (-> integrant.repl.state/system :db/mysql)]
    (return-messages-to-front db [{:id 32}]))

  (let [db (-> integrant.repl.state/system :db/mysql)]
    (queue-length db "AB" true))



  ;;
  )
