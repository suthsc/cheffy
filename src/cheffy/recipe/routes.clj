(ns cheffy.recipe.routes
  (:require [cheffy.recipe.handlers :as recipe]
            [cheffy.responses :as responses]
            [cheffy.middleware :as mw]))

(defn routes
  [env]
  (let [db (:jdbc-url env)]
    ["/recipes"
     {:swagger    {:tags ["recipes"]}
      :middleware [[mw/wrap-auth0]]}
     ["" {:get  {:handler   (recipe/list-all-recipes db)
                 :responses {200 {:body responses/recipes}}
                 :summary   "List of recipes"}
          :post {:handler    (recipe/create-recipe! db)
                 :parameters {:body {:name      string?
                                     :prep-time number?
                                     :img       string?}}
                 :responses  {201 {:body {:recipe-id string?}}}
                 :summary    "Create recipe"}}]
     ["/:recipe-id"
      {:parameters {:path {:recipe-id string?}}}
      [""
       {:get    {:handler    (recipe/retrieve-recipe db)
                 :responses  {200 {:body responses/recipe}}
                 :summary    "Retrieve recipe"}
        :put    {:handler    (recipe/update-recipe! db)
                 :middleware [[mw/wrap-recipe-owner db]]
                 :parameters {:body {:name string? :prep-time int? :public boolean? :img string?}}
                 :responses  {204 {:body nil?}}
                 :summary    "Update Recipe"}
        :delete {:handler    (recipe/delete-recipe! db)
                 :middleware [[mw/wrap-recipe-owner db]]
                 :responses  {204 {:body nil?}}
                 :summary    "Delete recipe"}}]
      ["/favorite"
       {:post   {:handler    (recipe/favorite-recipe! db)
                 :responses  {204 {:body nil?}}
                 :summary    "Favorite Recipe"}
        :delete {:handler    (recipe/unfavorite-recipe! db)
                 :responses  {204 {:body nil?}}
                 :summary    "Unfavorite recipe"}}]
      ["/steps"
       [""
        {:post {:handler    (recipe/create-step! db)
                :middleware [[mw/wrap-recipe-owner db]]
                :parameters {:body {:sort        number?
                                    :description string?}}
                :summary    "Create step"}}]
       ["/:step-id"
         {:put    {:handler    (recipe/update-step! db)
                   :middleware [[mw/wrap-recipe-owner db]]
                   :parameters {:path {:step-id string?}
                                :body {:sort number? :description string?}}
                   :summary "Update step"}
          :delete {:handler (recipe/delete-step! db)
                   :middleware [[mw/wrap-recipe-owner db]]
                   :parameters {:path {:step-id string?}}
                   :summary "Delete step"}}]]]]))
