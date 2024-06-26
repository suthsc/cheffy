(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.core :as ig]
            [integrant.repl.state :as state]
            [cheffy.server]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(ig-repl/set-prep!
  (fn [] (-> "resources/config.edn"
             slurp
             ig/read-string)))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(def app (-> state/system :cheffy/app))
(def db (-> state/system :db/postgres))

(comment

  (sql/delete! db :recipe {:recipe-id "a3dde84c-4a33-45aa-b0f3-4bf9ac997680"})

  (sql/find-by-keys db :step {:recipe-id "a3dde84c-4a33-45aa-b0f3-4bf9ac997680"}
                    {:order-by [:sort]})
  (def recipes (sql/find-by-keys db :recipe {:public true}))
  (def recipe (first recipes))
  (select-keys recipe [:recipe/recipe-id])


  ;; base recipe route
  (-> (app {:request-method :get
            :uri "/v1/recipes/1234-recipe"})
      :body
      (slurp))

  (-> (app {:request-method :post
            :uri "/v1/recipes"
            :body-params {:name "my recipe"
                          :prep-time 49
                          :img "image-url"}})
      :body
      (slurp))

  (go)
  (halt)
  (reset))
