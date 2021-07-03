(ns user
  (:require
    [integrant.repl :as ig-repl]
    [integrant.core :as ig]
    [integrant.repl.state :as state]
    [s71-challenge.server]))

(ig-repl/set-prep!
  (fn []
    (-> "dev/resources/config.edn" slurp ig/read-string)))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(def app (-> state/system :s71-challenge/app))
(def db (-> state/system :db/mysql))

(comment
  (go)
  (halt)
  (reset)
  (set! *print-namespace-maps* false)

  )
