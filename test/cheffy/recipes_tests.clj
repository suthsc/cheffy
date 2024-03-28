(ns cheffy.recipes-tests
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [cheffy.server :refer :all]
            [cheffy.test-system :as ts]))

(use-fixtures :once ts/token-fixture)

(def recipe-id (atom nil))
(def step-id (atom nil))

(def ingredient-id (atom nil))

(def recipe
  {:img       "https://images.pexels.com/photos/263168/pexels-photo-263168.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
   :prep-time 30
   :name      "My Test Recipe"})

(def update-recipe
  (assoc recipe :public true))

(def step
  {:sort 1
   :description "My First Steps"})

(def update-step
  (assoc step :description "Updated First Step"))

(def ingredient
  {:sort 1
   :name "Elbow noodles"
   :amount 2
   :measure "cups"})

(def update-ingredient
  (assoc ingredient :name "Romain Lettuce"))

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

  (testing "Create step"
    (testing "with auth"
      (let [{:keys [status body]} (ts/test-endpoint :post (str "/v1/recipes/" @recipe-id "/steps")
                                                    {:auth true :body step})]
        (reset! step-id (:step-id body))
        (is (= status 201))))

    (testing "without auth"
      (let [{:keys [status body]} (ts/test-endpoint :post (str "/v1/recipes/" @recipe-id "/steps")
                                                    {:body step})]
        (is (= status 401))
        (is (= body {:data    (str "recipe-id " @recipe-id)
                     :message "You need to be the recipe owner"
                     :type    "authorization-required"})))))

  (testing "Update step"
    (testing "with auth"
      (let [{:keys [status]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id "/steps/" @step-id)
                                               {:auth true :body update-step})]
        (is (= status 204))))
    (testing "without auth"
      (let [{:keys [body status]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id "/steps/" @step-id)
                                                    {:body update-step})]
        (is (= status 401))
        (is (= body {:data    (str "recipe-id " @recipe-id)
                     :message "You need to be the recipe owner"
                     :type    "authorization-required"})))))

  (testing "Ingredients"
    (testing "Create"
      (let [{:keys [status body]} (ts/test-endpoint :post (str "/v1/recipes/" @recipe-id "/ingredient")
                                                    {:auth true :body ingredient})]
        (reset! ingredient-id (:ingredient-id body))
        (is (= status 201))))

    (testing "Update"
      (let [{:keys [status]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id "/ingredient/" @ingredient-id)
                                               {:auth true :body update-ingredient})]
        (is (= status 204))))

    (testing "Delete"
      (let [{:keys [status]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id "/ingredient/" @ingredient-id)
                                               {:auth true})]
        (is (= status 204)))))

  (testing "Delete step"
    (let [{:keys [status]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id "/steps/" @step-id)
                                             {:auth true})]
      (is (= status 204))))

  (testing "Update recipe"
    (let [{:keys [status]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id) {:auth true :body update-recipe})]
      (is (= status 204))))

  (testing "Favorite recipe"
    (let [{:keys [status]} (ts/test-endpoint :post (str "/v1/recipes/" @recipe-id "/favorite") {:auth true})]
      (is (= status 204))))

  (testing "Unfavorite recipe"
    (let [{:keys [status]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id "/favorite") {:auth true})]
      (is (= status 204))))

  (testing "Delete recipe"
    (let [{:keys [status body]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id) {:auth true})]
      (is (= status 204)))))

(comment

  (reset! recipe-id "1234-recipe")
  (ts/test-endpoint :post "/v1/recipes" {:auth true :body recipe})
  (ts/test-endpoint :put "/v1/recipes/ec180cf5-a5a1-4ead-89ba-32e681c0d782" {:auth true
                                                                             :body update-recipe})
  (ts/test-endpoint :delete "/v1/recipes/f30cd1e7-29ef-4489-aac1-2a563f2fa707" {:auth true})

  (ts/test-endpoint :post "/v1/recipes/ec180cf5-a5a1-4ead-89ba-32e681c0d782/favorite" {:auth true})
  (ts/test-endpoint :delete "/v1/recipes/ec180cf5-a5a1-4ead-89ba-32e681c0d782/favorite" {:auth true})

  (ts/test-endpoint :post "/v1/recipes/21e59ccc-b208-4419-a7a7-90a2e8decbc5/steps" {:auth true
                                                                                    :body step})

  ())
