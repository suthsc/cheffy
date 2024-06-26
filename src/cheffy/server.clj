(ns cheffy.server
  (:require [cheffy.router :as router]
            [environ.core :refer [env]]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [ring.adapter.jetty :as jetty]))

(defn app
  [env]
  (router/routes env))

(defmethod ig/prep-key :server/jetty
  [_ config]
  (println (str "\n Jetty prep'ed"))
  (merge config {:port (Integer/parseInt (env :port))}))

(defmethod ig/prep-key :db/postgres
  [_ config]
  (println (str "\n Postgres jdbc url configured"))
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
  [_ {:keys [jdbc-url]}]
  (println "\nConfigured db")
  (jdbc/with-options jdbc-url jdbc/snake-kebab-opts))

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
