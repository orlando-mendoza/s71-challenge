(ns s71-challenge.queue.handler
  (:require [s71-challenge.queue.db :as db]
            [ring.util.response :as rr]))

; The challenge objective is to create a FiFo multi-queue with messages stored in a MySQL database,
; with an implementation that is independent of test data or use case.
;
; In addition your application should...
; 1. Fill in the logic for all of the stubbed functions modifying the namespace as needed to develop the queue application.
; 2. Submit a SQL file that includes the table structure and any other data needed for your application.
; 3. Add additional functionality as needed so that the namspace can be run from lein.
; 4. While not required, any notes on the development or optimizations you built in to your application would be a plus.
;
; If you need access to a MySQL database we can provide credentials upon request.
;
; Your completed files can be submitted as a zip file, GitHub repo, or GitHub gist.

(defn add-bool
  "Adds a boolean key for each truty genereted-key"
  [data]
  (if (pos? (:generated-key data))
    (merge data {:success true})
    (merge data {:success false})))

(defn push-messages
  "Pushes the given messages to the queue.
   Returns a list of booleans indicating whether or not each message
   was successfully added to the queue."
  [db]
  (fn [request]
    (let [messages (-> request :parameters :body)
          added (db/push-messages db messages)
          result (into [] (map add-bool added))]
      (rr/response result))))
;; TODO: Try catch errors on db call

(defn peek-messages
  "Returns one or more messages from the queue.
   Messages are still visible after this call is made.
   Optional keyword args:
     message-type - filters for messages of the given type
     limit - returns the given number of messages (default: 1)"
  [db]
  (fn [request]
    (let [{:keys [message-type limit]
           :or {message-type "%" limit 1}} (-> request :parameters :query)
          result (db/peek-messages db message-type limit)]
      (rr/response result))))

(defn pop-messages
  "Returns one or more messages from the queue.
   Messages are hidden for the duration (in sec) specified by the
   required ttl arg, after which they return to the front of the queue.
   Optional keyword args:
     message-type - filters for messages of the given type
     limit - returns the given number of messages (default: 1)"
  [db]
  (fn [request]
    (let [ttl (-> request :parameters :body :ttl)
          {:keys [message-type limit]
           :or {message-type "%" limit 1}} (-> request :parameters :query)
          messages (db/pop-message db message-type limit)]

      ;; waits for the duration ttl and then unhides the messages
      ;; if the worker confirmed the message during Thread/sleep ttl time
      ;; unhide will have no effect for that message, which will be deleted by then
      (future
        (Thread/sleep (* ttl 1000))
        (db/return-messages-to-front db messages))
      (rr/response messages))))

(defn confirm-messages
  "Deletes the given messages from the queue.
   This function should be called to confirm the successful handling
   of messages returned by the pop function."
  [db]
  (fn [request]
    (let [messages (-> request :parameters :body)]
      (db/confirm-messages db messages)
      (rr/status 204))))

(defn queue-length
  "Returns a count of the number of messages on the queue.
   Optional keyword args:
     message-type - filters for message of the given type
     with-hidden? - if truthy, includes messages that have been
                    popped but not confirmed"
  [db]
  (fn [request]
    (let [{:keys [message-type with-hidden?]
           :or {message-type "%" with-hidden? false}} (-> request :parameters :query)
          queue-count (db/queue-length db message-type with-hidden?)]
      (rr/response {:queue-length queue-count}))))
