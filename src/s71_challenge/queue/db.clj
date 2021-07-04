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
   (peek-messages db nil 1))
  ([db message-type]
   (peek-messages db message-type 1))
  ([db message-type limit]
   (with-open [conn (jdbc/get-connection db)]
     (let [all-types "SELECT *
                    FROM queue
                    WHERE hidden IS false"
           msg-type (str " AND message_type = '" message-type "'")
           lmit (str " LIMIT " (if (= 0 limit)
                                 1
                                 (or limit 1)))]
       (if (nil? message-type)
         (jdbc/execute! conn [(str all-types lmit)]
                        {:builder-fn rs/as-unqualified-kebab-maps})
         (jdbc/execute! conn [(str all-types msg-type " ORDER BY id ASC" lmit)]
                        {:builder-fn rs/as-unqualified-kebab-maps}))))))

(comment
  (let [db (-> integrant.repl.state/system :db/mysql)]
    (peek-messages db))

  (let [db (-> integrant.repl.state/system :db/mysql)]
    (peek-messages db "ab" 0))


  ;;
  )
