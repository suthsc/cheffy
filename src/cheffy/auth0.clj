(ns cheffy.auth0
  (:require [clj-http.client :as http]
            [muuntaja.core :as m]))

(defn get-test-token
  []
  (->> {:content-type :json
        :cookie-policy :standard
        :body         (m/encode "application/json"
                                {:client_id  "ClhvE2HzUjCbuXJkxmYnwxMwuRKLwSS9"
                                 :audience   "https://dev-gnzt0rrur86ghxqh.us.auth0.com/api/v2/"
                                 :grant_type "password"
                                 :username   "testing@cheffy.app"
                                 :password   "U37QsoE145"
                                 :scope      "openid profile email"})}
       (http/post "https://dev-gnzt0rrur86ghxqh.us.auth0.com/oauth/token")
       (m/decode-response-body)
       :access_token))

(comment
  (get-test-token))
