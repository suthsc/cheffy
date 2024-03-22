(ns cheffy.recipe.handlers
  (:require [cheffy.recipe.db :as recipe-db]
            [cheffy.responses :as responses]
            [ring.util.response :as rr])
  (:import (java.util UUID)))

(defn list-all-recipes
  [db]
  (fn [request]
    (let [uid (-> request :claims :sub)
          recipes (recipe-db/find-all-recipes db uid)]
      (rr/response recipes))))

(defn retrieve-recipe
  [db]
  (fn [request]
    (let [recipe-id (-> request :parameters :path :recipe-id)
          recipe (recipe-db/find-recipe-by-id db recipe-id)]
      (if recipe
        (rr/response recipe)
        (rr/not-found {:type    "recipe-not-found"
                       :message "Recipe not found"
                       :data    (str "recipe-id" recipe-id)})))))

(defn create-recipe! [db]
  (fn [request]
    (let [recipe-id (str (UUID/randomUUID))
          uid (-> request :claims :sub)
          recipe (-> request :parameters :body)]
      (recipe-db/insert-recipe! db (assoc recipe :recipe-id recipe-id :uid uid))
      (rr/created (str responses/base-url "/recipes/" recipe-id) {:recipe-id recipe-id}))))

(defn update-recipe!
  [db]
  (fn [request]
    (let [recipe-id (-> request :parameters :path :recipe-id)
          uid (-> request :claims :sub)
          recipe (-> request :parameters :body)
          update-successful? (recipe-db/update-recipe! db (assoc recipe :recipe-id recipe-id))]
      (if update-successful?
        (rr/status 204)
        (rr/not-found {:type    "recipe-not-found"
                       :message "Recipe not found"
                       :data    (str "recipe-id" recipe-id)})))))

(defn delete-recipe!
  [db]
  (fn [request]
    (let [recipe-id (-> request :parameters :path :recipe-id)
          uid (-> request :claims :sub)
          deleted? (recipe-db/delete-recipe! db {:recipe-id recipe-id})]
      (if deleted?
        (rr/status 204)
        (rr/not-found {:type    "recipe-not-found"
                       :message "Recipe not found"
                       :data    (str "recipe-id" recipe-id)}))))
  )

(defn favorite-recipe!
  [db]
  (fn [request]
    (let [uid (-> request :claims :sub)
          recipe-id (-> request :parameters :path :recipe-id)
          favorited? (recipe-db/favorite-recipe! db {:uid uid :recipe-id recipe-id})]
      (if favorited?
        (rr/status 204)
        (rr/not-found {:type    "recipe-not-found"
                       :message "Recipe not found"
                       :data    (str "recipe-id " recipe-id)})))))

(defn unfavorite-recipe!
  [db]
  (fn [request]
    (let [uid (-> request :claims :sub)
          recipe-id (-> request :parameters :path :recipe-id)
          deleted? (recipe-db/unfavorite-recipe! db {:uid uid :recipe-id recipe-id})]
      (if deleted?
        (rr/status 204)
        (rr/not-found {:type    "recipe-not-found"
                       :message "Recipe not found"
                       :data    (str "recipe-id " recipe-id)})))))

(defn create-step!
  [db]
  (fn [request]
    (let [recipe-id (-> request :parameters :path :recipe-id)
          step-id (str (UUID/randomUUID))
          step (-> request :parameters :body)]
      (recipe-db/insert-step! db (assoc step :recipe-id recipe-id :step-id step-id))
      (rr/created (str responses/base-url "/recipies/" recipe-id "/steps") {:step-id step-id}))))

(defn update-step!
  [db]
  (fn [request]
    (let [{:keys [recipe-id step-id]} (-> request :parameters :path)
          step (-> request :parameters :body)
          update-successful? (recipe-db/update-step! db (assoc step :recipe-id recipe-id :step-id step-id))]
      (if update-successful?
        (rr/status 204)
        (rr/not-found {:type    "step-not-found"
                       :message "Step not found"
                       :data    (format "recipe-id: %s, step-id: %s" recipe-id step-id)})))))

(defn delete-step!
  [db]
  (fn [request]
    (let [{:keys [recipe-id step-id]} (-> request :parameters :path)
          delete-successful? (recipe-db/delete-step! db {:recipe-id recipe-id :step-id step-id})]
      (if delete-successful?
        (rr/status 204)
        (rr/not-found {:type    "step-not-found"
                       :message "Step not found"
                       :data    (format "recipe-id %s, step-id: %s" recipe-id step-id)})))))
