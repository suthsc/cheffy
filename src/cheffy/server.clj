(ns cheffy.server
  (:require [integrant.core :as ig]
            [reitit.ring :as ring]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [cheffy.router :as router]))

(defn app
  [env]
  (router/routes env)
  )

(defmethod ig/prep-key :server/jetty
  [_ config]
  (merge config {:port (Integer/parseInt (env :port))}))

(defmethod ig/prep-key :db/postgres
  [_ config]
  (merge config {:jdbc-url (env :jdbc-database-url)}))

(defmethod ig/init-key :server/jetty
  [_ {:keys [handler port]}]
  (println (str "\nServer running on port " port))
  (jetty/run-jetty handler {:port port :join? false}))

(defmethod ig/init-key :cheffy/app
  [_ config]
  (println "\nStarted app")
  (app config))

(defmethod ig/init-key :db/postgres
  [_ config]
  (println "\nConfigured db")
  (:jdbc-url config))

(defmethod ig/halt-key! :server/jetty
  [_ jetty]
  (.stop jetty))

(defn -main
  [config-file]
  (let [config (-> config-file slurp ig/read-string)]
    (-> config ig/prep ig/init)))

(comment
  (start)
  (app {:request-method :get
        :uri "/"})
  (-main))
