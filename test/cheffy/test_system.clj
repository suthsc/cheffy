(ns cheffy.test-system
  (:require [clojure.test :refer :all]
            [integrant.repl.state :as state]
            [muuntaja.core :as m]
            [ring.mock.request :as mock]
            [cheffy.auth0 :as auth0]))

(def token (atom nil))

(defn token-fixture
  [f]
  (reset! token (auth0/get-test-token))
  (f)
  (reset! token nil))

(defn test-endpoint
  ([method uri]
   (test-endpoint method uri nil))
  ([method uri opts]
   (let [app (-> state/system :cheffy/app)
         request (app (-> (mock/request method uri)
                          (cond->
                            (:auth opts) (mock/header :authorization (str "Bearer " (or @token (auth0/get-test-token))))
                            (:body opts) (mock/json-body (:body opts)))))]
     (update request :body (partial m/decode "application/json")))))

(comment
  (let [request (test-endpoint :get "/v1/recipes")
        decoded-request (m/decode-response-body request)]
    (assoc request :body decoded-request))

  (test-endpoint :post "/v1/recipes" {:img "string"
                                      :name "my name"
                                      :prep-time 30})
  )
