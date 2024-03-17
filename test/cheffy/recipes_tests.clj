(ns cheffy.recipes-tests
  (:require [clojure.test :refer [deftest testing is]]
            [cheffy.server :refer :all]
            [cheffy.test-system :as ts]))

(def recipe-id (atom nil))

(def recipe
  {:img "https://images.pexels.com/photos/263168/pexels-photo-263168.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
   :prep-time 30
   :name "My Test Recipe"})

(def update-recipe
  (assoc recipe :public true))

(deftest a-test
  (testing "List recipes"
    (testing "with auth -- public and drafts"
      (let [{:keys [status body]} (ts/test-endpoint :get "/v1/recipes" {:auth true})]
        (is (= 200 status))
        (is (vector? (:public body)))
        (is (vector? (:drafts body)))))

    (testing "without auth -- public"
      (let [{:keys [status body]} (ts/test-endpoint :get "/v1/recipes" {:auth false})]
        (is (= 200 status))
        (is (vector? (:public body)))
        (is (nil? (:drafts body)))))))

(deftest recipe-tests
  (testing "Create recipe"
    (let [{:keys [status body]} (ts/test-endpoint :post "/v1/recipes" {:auth true :body recipe})]
      (reset! recipe-id (:recipe-id body))
      (is (= status 201))))

  (testing "Update recipe"
    (let [{:keys [status body]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id) {:auth true :body update-recipe})]
      (is (= status 204))))

  (testing "Delete recipe"
    (let [{:keys [status body]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id) {:auth true})]
      (is (= status 204)))))

(comment

  (reset! recipe-id "1234-recipe")
  (ts/test-endpoint :post "/v1/recipes" {:auth true :body recipe})
  (ts/test-endpoint :put "/v1/recipes/f30cd1e7-29ef-4489-aac1-2a563f2fa707" {:auth true
                                                                             :body update-recipe})
  (ts/test-endpoint :delete "/v1/recipes/f30cd1e7-29ef-4489-aac1-2a563f2fa707" {:auth true})

  ())
