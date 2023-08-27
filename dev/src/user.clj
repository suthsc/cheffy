(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.core :as ig]
            [integrant.repl.state :as state]
            [cheffy.server]))

(ig-repl/set-prep!
  (fn [] (-> "resources/config.edn" slurp ig/read-string)))

(def go ig-repl/go)
(def halt ig-repl/go)
(def reset ig-repl/go)
(def reset-all ig-repl/go)

(defn app [] (-> state/system :cheffy/app))
(defn db [] (-> state/system :db/postgres))

(comment
  (go)
  (halt)
  (reset))
